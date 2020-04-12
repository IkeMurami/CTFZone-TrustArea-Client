package com.zfr.ctfzoneclient.service.view

import android.app.IntentService
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.Context
import com.zfr.ctfzoneclient.PACKAGE_ID
import com.zfr.ctfzoneclient.core.ResponseErrorException
import com.zfr.ctfzoneclient.network.data.TaskNetworkEntity
import com.zfr.ctfzoneclient.network.data.TokenNetworkEntity
import com.zfr.ctfzoneclient.network.data.UserNetworkEntity
import com.zfr.ctfzoneclient.network.data.asErrorNetworkEntity
import com.zfr.ctfzoneclient.repository.LogRepository
import com.zfr.ctfzoneclient.repository.TaskRepository
import com.zfr.ctfzoneclient.repository.getLogger
import com.zfr.ctfzoneclient.repository.getTaskRepository
import com.zfr.ctfzoneclient.service.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody


private const val ACTION_CREATE = "${PACKAGE_ID}.action.TASK_CREATE"
private const val ACTION_GET    = "${PACKAGE_ID}.action.TASK_GET"
private const val ACTION_ALL    = "${PACKAGE_ID}.action.TASK_ALL"
private const val ACTION_UPDATE = "${PACKAGE_ID}.action.TASK_UPDATE"



class TaskService : IntentService("TaskService") {

    private val TAG = "TaskService"

    private lateinit var logger: LogRepository
    private lateinit var taskRepository: TaskRepository

    fun sendSuccess(pendingIntent: PendingIntent?, intent: Intent?) {
        logger.info(TAG, "Send response to ${pendingIntent?.creatorPackage}")
        pendingIntent?.send(applicationContext, 0, intent)
    }

    fun sendError(pendingIntent: PendingIntent?, errorBody: ResponseBody?) {
        val error = errorBody?.asErrorNetworkEntity()
        logger.info(TAG, "Return message: ${error?.message}; errors: ${error?.errors}")
        logger.info(TAG, "Send response to ${pendingIntent?.creatorPackage}")

        pendingIntent?.send(applicationContext, 0, errorIntent(error?.message!!, error.errors))
    }

    fun sendException(pendingIntent: PendingIntent?, message: String) {
        logger.info(TAG, "Request failure: ${message}")

        pendingIntent?.send(applicationContext, 0, errorIntent("Request failure", listOf(message)))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        logger = getLogger(applicationContext)
        taskRepository = getTaskRepository(applicationContext)

        logger.info(TAG, "Started")

        return Service.START_REDELIVER_INTENT
    }

    override fun onHandleIntent(intent: Intent?) {
        logger = getLogger(applicationContext)
        logger.info(TAG, intent?.action!!)

        when (intent?.action) {
            ACTION_CREATE -> {
                val token = intent.asTokenNetworkEntity()
                val task = intent.asTaskNetworkEntity()
                val pendingIntent = intent.getPendingIntent()

                logger.info(TAG, "Create new task ${task} by token ${token}")
                handleActionCreateTask(token, task, pendingIntent)
            }
            ACTION_GET -> {
                val token = intent.asTokenNetworkEntity()
                val task = intent.asTaskNetworkEntity()
                val pendingIntent = intent.getPendingIntent()

                logger.info(TAG, "Get task ${task} by token ${token}")
                handleActionGetTask(task, token, pendingIntent)
            }
            ACTION_ALL -> {
                // handleActionFoo(param1, param2)
                val token = intent.asTokenNetworkEntity()
                val user = intent.asUserNetworkEntity()
                val pendingIntent = intent.getPendingIntent()

                logger.info(TAG, "Get all tasks by token ${token} ${user}")
                handleActionGetAllTask(token, user, pendingIntent)

            }
            ACTION_UPDATE -> {
                val token = intent.asTokenNetworkEntity()
                val task = intent.asTaskNetworkEntity()
                val pendingIntent = intent.getPendingIntent()

                logger.info(TAG, "Update task ${task} by token ${token}")
                handleActionUpdateTask(token, task, pendingIntent)
            }
        }
    }

    private fun handleActionCreateTask(sessionToken: TokenNetworkEntity, task: TaskNetworkEntity, pendingIntent: PendingIntent?) {

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val createdTask = taskRepository.createTask(sessionToken, task)

                sendSuccess(pendingIntent, createdTask?.asIntent(Intent()))
            }
            catch (e: ResponseErrorException) {
                sendError(pendingIntent, e.error)
            }
            catch (e: Exception) {
                sendException(pendingIntent, e.localizedMessage!!)
            }

        }

    }

    private fun handleActionUpdateTask(sessionToken: TokenNetworkEntity, task: TaskNetworkEntity, pendingIntent: PendingIntent?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val updatedTask = taskRepository.updateTask(sessionToken, task)

                sendSuccess(pendingIntent, updatedTask?.asIntent(Intent()))
            }
            catch (e: ResponseErrorException) {
                sendError(pendingIntent, e.error)
            }
            catch (e: Exception) {
                sendException(pendingIntent, e.localizedMessage!!)
            }
        }
    }

    private fun handleActionGetAllTask(sessionToken: TokenNetworkEntity, user: UserNetworkEntity, pendingIntent: PendingIntent?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val allTasks = taskRepository.getAllTasks(sessionToken, user)

                sendSuccess(pendingIntent, allTasks.asIntent(Intent()))
            }
            catch (e: ResponseErrorException) {
                sendError(pendingIntent, e.error)
            }
            catch (e: Exception) {
                sendException(pendingIntent, e.localizedMessage!!)
            }
        }
    }

    private fun handleActionGetTask(task: TaskNetworkEntity, token: TokenNetworkEntity, pendingIntent: PendingIntent?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val fetchedTask = taskRepository.getTask(token, task)

                sendSuccess(pendingIntent, fetchedTask?.asIntent(Intent()))
            }
            catch (e: ResponseErrorException) {
                sendError(pendingIntent, e.error)
            }
            catch (e: Exception) {
                sendException(pendingIntent, e.localizedMessage!!)
            }
        }
    }

    override fun onDestroy() {
        logger.info(TAG, "Destroy")
        super.onDestroy()
    }
}
