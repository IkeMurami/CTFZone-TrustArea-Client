package com.zfr.ctfzoneclient.repository

import android.content.Context
import android.media.session.MediaSession
import com.zfr.ctfzoneclient.core.ResponseErrorException
import com.zfr.ctfzoneclient.database.CTFZoneDatabase
import com.zfr.ctfzoneclient.database.data.asDomainModel
import com.zfr.ctfzoneclient.database.getDatabase
import com.zfr.ctfzoneclient.network.ControllerApi
import com.zfr.ctfzoneclient.network.data.*
import retrofit2.await


private const val TAG = "TaskRepository"

class TaskRepository(private val database: CTFZoneDatabase, private val usersRepository: UsersRepository, private val logger: LogRepository) {

    suspend fun createTask(token: TokenNetworkEntity, task: TaskNetworkEntity): TaskNetworkEntity? {
        // post data
        val api = ControllerApi().getTaskApi()
        api.create(token.token, task).execute().let {
            if (it.isSuccessful) {
                val createdTask = it.body()?.data
                logger.info(TAG, "Create new task ${createdTask} for user with token ${token}")
                cacheTask(createdTask!!, token)
                return createdTask
            }
            else {
                throw ResponseErrorException("Something wrong", it.errorBody()!!)
            }
        }
    }

    suspend fun updateTask(token: TokenNetworkEntity, task: TaskNetworkEntity): TaskNetworkEntity? {
        val api = ControllerApi().getTaskApi()
        api.update(token.token, task.task_id!!, task).execute().let {
            if (it.isSuccessful) {
                val updatedTask = it.body()?.data
                logger.info(TAG, "Task updated ${updatedTask}")

                cacheTask(updatedTask!!, token)
                return updatedTask
            }
            else {
                throw ResponseErrorException("Something wrong", it.errorBody()!!)
            }
        }
    }

    suspend fun getTask(token: TokenNetworkEntity, task: TaskNetworkEntity): TaskNetworkEntity? {
        val user = usersRepository.userInfo(token)
        val fetchTask = database.taskDao.task(user?.user_id!!, task.task_id!!)

        if (fetchTask == null) {
            val api = ControllerApi().getTaskApi()
            api.task(token.token, task.task_id).execute().let {
                if (it.isSuccessful) {
                    val taskFromBackend = it.body()?.data
                    logger.info(TAG, "Find task ${taskFromBackend} for user with token ${token}")

                    cacheTask(taskFromBackend!!, token)
                    return taskFromBackend
                }
                else {
                    throw ResponseErrorException("Something wrong", it.errorBody()!!)
                }
            }
        }
        else {
            logger.info(TAG, "Find local task ${fetchTask}")
            return fetchTask.asDomainModel()
        }
    }

    suspend fun getAllTasks(token: TokenNetworkEntity, user: UserNetworkEntity? = null): List<TaskNetworkEntity> {

        ControllerApi().getTaskApi().tasks(token.token, user?.username).execute().let{
            if (it.isSuccessful) {
                val tasks = it.body()?.data
                logger.info(TAG, "Fetch ${tasks?.size} tasks ${user?.username} ${token.token}")

                return tasks!!
            }
            else {
                throw  ResponseErrorException("Something wrong", it.errorBody()!!)
            }
        }
    }

    suspend fun cacheTask(task: TaskNetworkEntity, token: TokenNetworkEntity) {
        val user = usersRepository.userInfo(token)

        if (user != null) {
            logger.info(TAG, "Cache task ${task}")
            database.taskDao.insertTask(task.asDatabaseEntity(user))
        }
        else {
            throw Exception("Token invalid")
        }
    }

}


private lateinit var INSTANCE: TaskRepository


fun getTaskRepository(context: Context): TaskRepository {

    synchronized(TaskRepository::class.java) {

        if(!::INSTANCE.isInitialized) {
            INSTANCE = TaskRepository(getDatabase(context), getUserRepository(context), getLogger(context))
        }

    }

    return INSTANCE
}