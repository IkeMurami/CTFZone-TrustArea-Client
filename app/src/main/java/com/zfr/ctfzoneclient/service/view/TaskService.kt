package com.zfr.ctfzoneclient.service.view

import android.app.IntentService
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.Context
import com.zfr.ctfzoneclient.PACKAGE_ID
import com.zfr.ctfzoneclient.core.Responder.Companion.sendError
import com.zfr.ctfzoneclient.core.Responder.Companion.sendException
import com.zfr.ctfzoneclient.core.Responder.Companion.sendSuccess
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


        val actionCallback = intent.callback()

        when (intent?.action) {
            ACTION_CREATE -> {
                val token = intent.asTokenNetworkEntity()
                val task = intent.asTaskNetworkEntity()

                logger.info(TAG, "Create new task ${task} by token ${token}")
                handleActionCreateTask(token, task, actionCallback!!)
            }
            ACTION_GET -> {
                val token = intent.asTokenNetworkEntity()
                val task = intent.asTaskNetworkEntity()

                logger.info(TAG, "Get task ${task} by token ${token}")
                handleActionGetTask(task, token, actionCallback!!)
            }
            ACTION_ALL -> {
                // handleActionFoo(param1, param2)
                val token = intent.asTokenNetworkEntity()
                val user = intent.asUserNetworkEntity()

                logger.info(TAG, "Get all tasks by token ${token} ${user}")
                handleActionGetAllTask(token, user, actionCallback!!)

            }
            ACTION_UPDATE -> {
                val token = intent.asTokenNetworkEntity()
                val task = intent.asTaskNetworkEntity()

                logger.info(TAG, "Update task ${task} by token ${token}")
                handleActionUpdateTask(token, task, actionCallback!!)
            }
        }
    }

    private fun handleActionCreateTask(sessionToken: TokenNetworkEntity, task: TaskNetworkEntity, actionCallback: String) {

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val createdTask = taskRepository.createTask(sessionToken, task)

                sendSuccess(logger, TAG, applicationContext, createdTask?.asIntent(Intent()), actionCallback)
            }
            catch (e: ResponseErrorException) {
                sendError(logger, TAG, applicationContext, e.error, actionCallback)
            }
            catch (e: Exception) {
                sendException(logger, TAG, applicationContext, e.localizedMessage!!, actionCallback)
            }

        }

    }

    private fun handleActionUpdateTask(sessionToken: TokenNetworkEntity, task: TaskNetworkEntity, actionCallback: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val updatedTask = taskRepository.updateTask(sessionToken, task)

                sendSuccess(logger, TAG, applicationContext, updatedTask?.asIntent(Intent()), actionCallback)
            }
            catch (e: ResponseErrorException) {
                sendError(logger, TAG, applicationContext, e.error, actionCallback)
            }
            catch (e: Exception) {
                sendException(logger, TAG, applicationContext, e.localizedMessage!!, actionCallback)
            }
        }
    }

    private fun handleActionGetAllTask(sessionToken: TokenNetworkEntity, user: UserNetworkEntity, actionCallback: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val allTasks = taskRepository.getAllTasks(sessionToken, user)

                sendSuccess(logger, TAG, applicationContext, allTasks.asIntent(Intent()), actionCallback)
            }
            catch (e: ResponseErrorException) {
                sendError(logger, TAG, applicationContext, e.error, actionCallback)
            }
            catch (e: Exception) {
                sendException(logger, TAG, applicationContext, e.localizedMessage!!, actionCallback)
            }
        }
    }

    private fun handleActionGetTask(task: TaskNetworkEntity, token: TokenNetworkEntity, actionCallback: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val fetchedTask = taskRepository.getTask(token, task)

                sendSuccess(logger, TAG, applicationContext, fetchedTask?.asIntent(Intent()), actionCallback)
            }
            catch (e: ResponseErrorException) {
                sendError(logger, TAG, applicationContext, e.error, actionCallback)
            }
            catch (e: Exception) {
                sendException(logger, TAG, applicationContext, e.localizedMessage!!, actionCallback)
            }
        }
    }

    override fun onDestroy() {
        logger.info(TAG, "Destroy")
        super.onDestroy()
    }
}
