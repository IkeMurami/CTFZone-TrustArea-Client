package com.zfr.ctfzoneclient.network.interfaces

import com.zfr.ctfzoneclient.network.data.Response
import com.zfr.ctfzoneclient.network.data.ResponseData
import com.zfr.ctfzoneclient.network.data.TaskNetworkEntity
import retrofit2.Call
import retrofit2.http.*

interface TaskApi {

    @POST("tasks")
    fun create(@Header("Authorization") sessionToken: String, @Body task:TaskNetworkEntity): Call<Response<ResponseData>>

    @PUT("tasks/{task_id}")
    fun update(@Header("Authorization") sessionToken: String, @Path("task_id") task_id: Int, @Body task: TaskNetworkEntity): Call<Response<ResponseData>>

    @GET("tasks")
    fun tasks(@Header("Authorization") sessionToken: String, @Query("username") username: String?): Call<Response<List<ResponseData>>>

    @GET("tasks/{task_id}")
    fun task(@Header("Authorization") sessionToken: String, @Path("task_id") task_id: Int): Call<Response<ResponseData>>

}