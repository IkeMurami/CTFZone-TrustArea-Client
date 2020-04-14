package com.zfr.ctfzoneclient.network.interfaces

import com.zfr.ctfzoneclient.network.data.Response
import com.zfr.ctfzoneclient.network.data.ResponseData
import com.zfr.ctfzoneclient.network.data.TaskNetworkEntity
import com.zfr.ctfzoneclient.network.data.TaskNetworkEntityResponse
import retrofit2.Call
import retrofit2.http.*

interface TaskApi {

    @POST("tasks")
    fun create(@Header("Authorization") sessionToken: String, @Body task:TaskNetworkEntity): Call<Response<TaskNetworkEntity>>

    @PUT("tasks/{task_id}")
    fun update(@Header("Authorization") sessionToken: String, @Path("task_id") task_id: Int, @Body task: TaskNetworkEntity): Call<Response<TaskNetworkEntity>>

    @GET("tasks")
    fun tasks(@Header("Authorization") sessionToken: String, @Query("username") username: String?): Call<Response<List<TaskNetworkEntityResponse>>>

    @GET("tasks/{task_id}")
    fun task(@Header("Authorization") sessionToken: String, @Path("task_id") task_id: Int): Call<Response<TaskNetworkEntityResponse>>

}