package ctfz.trustarea.client.service.view

import android.app.IntentService
import android.app.Service
import android.content.Intent
import ctfz.trustarea.client.PACKAGE_ID
import ctfz.trustarea.client.core.Responder.Companion.sendError
import ctfz.trustarea.client.core.Responder.Companion.sendException
import ctfz.trustarea.client.core.Responder.Companion.sendSuccess
import ctfz.trustarea.client.core.ResponseErrorException
import ctfz.trustarea.client.network.data.TaskNetworkEntity
import ctfz.trustarea.client.network.data.TokenNetworkEntity
import ctfz.trustarea.client.network.data.UserNetworkEntity
import ctfz.trustarea.client.repository.LogRepository
import ctfz.trustarea.client.repository.TaskRepository
import ctfz.trustarea.client.repository.getLogger
import ctfz.trustarea.client.repository.getTaskRepository
import ctfz.trustarea.client.service.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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

        when (intent?.action) {
            ACTION_CREATE -> {
                val token = intent.asTokenNetworkEntity()
                val task = intent.asTaskNetworkEntity()

                logger.info(TAG, "Create new task ${task} by token ${token}")
                handleActionCreateTask(token, task, intent)
            }
            ACTION_GET -> {
                val token = intent.asTokenNetworkEntity()
                val task = intent.asTaskNetworkEntity()

                logger.info(TAG, "Get task ${task} by token ${token}")
                handleActionGetTask(task, token, intent)
            }
            ACTION_ALL -> {
                // handleActionFoo(param1, param2)
                val token = intent.asTokenNetworkEntity()
                val user = intent.asUserNetworkEntity()

                logger.info(TAG, "Get all tasks by token ${token} ${user}")
                handleActionGetAllTask(token, user, intent)

            }
            ACTION_UPDATE -> {
                val token = intent.asTokenNetworkEntity()
                val task = intent.asTaskNetworkEntity()

                logger.info(TAG, "Update task ${task} by token ${token}")
                handleActionUpdateTask(token, task, intent)
            }
        }
    }

    private fun handleActionCreateTask(sessionToken: TokenNetworkEntity, task: TaskNetworkEntity, request: Intent) {

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val createdTask = taskRepository.createTask(sessionToken, task)

                sendSuccess(logger, TAG, applicationContext, createdTask?.asIntent(Intent())!!, request)
            }
            catch (e: ResponseErrorException) {
                sendError(logger, TAG, applicationContext, e.error, request)
            }
            catch (e: Exception) {
                sendException(logger, TAG, applicationContext, e.localizedMessage!!, request)
            }

        }

    }

    private fun handleActionUpdateTask(sessionToken: TokenNetworkEntity, task: TaskNetworkEntity, request: Intent) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val updatedTask = taskRepository.updateTask(sessionToken, task)

                sendSuccess(logger, TAG, applicationContext, updatedTask?.asIntent(Intent())!!, request)
            }
            catch (e: ResponseErrorException) {
                sendError(logger, TAG, applicationContext, e.error, request)
            }
            catch (e: Exception) {
                sendException(logger, TAG, applicationContext, e.localizedMessage!!, request)
            }
        }
    }

    private fun handleActionGetAllTask(sessionToken: TokenNetworkEntity, user: UserNetworkEntity, request: Intent) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val allTasks = taskRepository.getAllTasks(sessionToken, user)

                sendSuccess(logger, TAG, applicationContext, allTasks.asIntent(Intent()), request)
            }
            catch (e: ResponseErrorException) {
                sendError(logger, TAG, applicationContext, e.error, request)
            }
            catch (e: Exception) {
                sendException(logger, TAG, applicationContext, e.localizedMessage!!, request)
            }
        }
    }

    private fun handleActionGetTask(task: TaskNetworkEntity, token: TokenNetworkEntity, request: Intent) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val fetchedTask = taskRepository.getTask(token, task)

                sendSuccess(logger, TAG, applicationContext, fetchedTask?.asIntent(Intent())!!, request)
            }
            catch (e: ResponseErrorException) {
                sendError(logger, TAG, applicationContext, e.error, request)
            }
            catch (e: Exception) {
                sendException(logger, TAG, applicationContext, e.localizedMessage!!, request)
            }
        }
    }

    override fun onDestroy() {
        logger.info(TAG, "Destroy")
        super.onDestroy()
    }
}
