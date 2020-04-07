package com.zfr.ctfzoneclient.network.interfaces


import com.zfr.ctfzoneclient.network.data.Response
import com.zfr.ctfzoneclient.network.data.ResponseData
import com.zfr.ctfzoneclient.network.data.UserNetworkEntity
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path


interface UserApi {

    @GET("users/{username}")
    fun user(@Path("username") username: String): Call<Response<ResponseData>> // If user not exist - create

    @GET("users")
    fun users(): Call<Response<List<ResponseData>>>

    @GET("users/me")
    fun profile(@Header("Authorization") session_token: String): Call<Response<ResponseData>>  // Get user by token

}

