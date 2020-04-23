package ctfz.trustarea.client.repository

import android.content.Context
import ctfz.trustarea.client.core.NetworkException
import ctfz.trustarea.client.core.ResponseErrorException
import ctfz.trustarea.client.database.CTFZoneDatabase
import ctfz.trustarea.client.database.data.asDomainModel
import ctfz.trustarea.client.database.getDatabase
import ctfz.trustarea.client.network.ControllerApi
import ctfz.trustarea.client.network.data.*


private const val TAG = "TaskRepository"

class TaskRepository(private val database: CTFZoneDatabase, private val usersRepository: UsersRepository, private val logger: LogRepository) {

    suspend fun createTask(token: TokenNetworkEntity, task: TaskNetworkEntity?): TaskNetworkEntity? {
        token.token ?: throw Exception("Token is null")
        task ?: throw Exception("Task is null")
        // post data
        val api = ControllerApi().getTaskApi()
        api.create(token.token, task).execute().let {
            if (it.isSuccessful) {
                var createdTask = it.body()?.data
                createdTask = TaskNetworkEntity(
                    task_id = createdTask?.task_id,
                    reward = task.reward,
                    challenge = task.challenge,
                    description = task.description
                )
                logger.info(TAG, "Create new task ${createdTask} for user with token ${token}")

                cacheTask(createdTask, token)
                return createdTask
            }
            else {
                if (it.code() >= 500)
                    throw NetworkException("Network Exception", it.errorBody()!!)
                throw ResponseErrorException("Something wrong", it.errorBody()!!)
            }
        }
    }

    suspend fun updateTask(token: TokenNetworkEntity, task: TaskNetworkEntity?): TaskNetworkEntity? {
        token.token ?: throw Exception("Token is null")
        task ?: throw Exception("Task is null")

        val api = ControllerApi().getTaskApi()

        api.update(token.token, task.task_id!!, task).execute().let {

            if (it.isSuccessful) {
                logger.info(TAG, "Task updated ${task}")

                cacheTask(task, token)

                return task
            }
            else {
                if (it.code() >= 500)
                    throw NetworkException("Network Exception", it.errorBody()!!)
                throw ResponseErrorException("Something wrong", it.errorBody()!!)
            }
        }
    }

    suspend fun getTask(token: TokenNetworkEntity?, task: TaskNetworkEntity?): TaskNetworkEntity? {
        token ?: throw Exception("Token is null")
        task ?: throw Exception("Task is null")
        token.token ?: throw Exception("Token is null")
        task.task_id ?: throw Exception("Task_ID is null")

        val user = usersRepository.userInfo(token)
        val fetchTask = database.taskDao.task(user?.username ?: throw Exception("User not found by token ${token.token}"), task.task_id)

        if (fetchTask == null) {
            val api = ControllerApi().getTaskApi()
            api.task(token.token, task.task_id).execute().let {
                if (it.isSuccessful) {
                    val taskFromBackend = it.body()?.data?.asTaskNetworkEntity()

                    logger.info(TAG, "Find task ${taskFromBackend} for user with token ${token}")

                    cacheTask(taskFromBackend, token)
                    return taskFromBackend
                }
                else {

                    if (it.code() >= 500)
                        throw NetworkException("Network Exception", it.errorBody()!!)
                    throw ResponseErrorException("Something wrong", it.errorBody()!!)
                }
            }
        }
        else {
            logger.info(TAG, "Find local task ${fetchTask}")
            return fetchTask.asDomainModel()
        }
    }

    suspend fun getAllTasks(token: TokenNetworkEntity?, user: UserNetworkEntity? = null): List<TaskNetworkEntity> {
        token ?: throw Exception("Token is null")
        token.token ?: throw Exception("Token is null")

        ControllerApi().getTaskApi().tasks(token.token, user?.username).execute().let{
            if (it.isSuccessful) {
                val tasks = it.body()?.data?.asTaskNetworkEntity()
                logger.info(TAG, "Fetch ${tasks?.size} tasks ${user?.username} ${token.token}")

                return tasks!!
            }
            else {

                if (it.code() >= 500)
                    throw NetworkException("Network Exception", it.errorBody()!!)
                throw  ResponseErrorException("Something wrong", it.errorBody()!!)
            }
        }
    }

    suspend fun cacheTask(task: TaskNetworkEntity?, token: TokenNetworkEntity?) {
        token ?: throw Exception("Token is null")
        task ?: throw Exception("Task is null")
        token.token ?: throw Exception("Token is null")
        task.task_id ?: throw Exception("Task_ID is null")

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