package com.zfr.ctfzoneclient.repository

import android.content.Context
import com.zfr.ctfzoneclient.database.CTFZoneDatabase
import com.zfr.ctfzoneclient.database.getDatabase
import com.zfr.ctfzoneclient.network.ControllerApi
import com.zfr.ctfzoneclient.network.data.RespondStatus
import com.zfr.ctfzoneclient.network.data.TaskNetworkEntity
import com.zfr.ctfzoneclient.network.data.TokenNetworkEntity
import retrofit2.await


class TaskRepository(private val database: CTFZoneDatabase) {

    suspend fun create(token: TokenNetworkEntity, task: TaskNetworkEntity): TaskNetworkEntity {
        // post data
        val api = ControllerApi().getTaskApi()
        val createdTask = api.create(token.token, task).await()

        // save local
        // database.taskDao.
        return createdTask.data!! as TaskNetworkEntity
    }

}


private lateinit var INSTANCE: TaskRepository


fun getTaskRepository(context: Context): TaskRepository {

    synchronized(TaskRepository::class.java) {

        if(!::INSTANCE.isInitialized) {
            INSTANCE = TaskRepository(getDatabase(context))
        }

    }

    return INSTANCE
}