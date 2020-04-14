package com.zfr.ctfzoneclient.network.interfaces


import com.zfr.ctfzoneclient.network.data.Response
import com.zfr.ctfzoneclient.network.data.ResponseData
import com.zfr.ctfzoneclient.network.data.UserNetworkEntity
import com.zfr.ctfzoneclient.network.data.UserNetworkEntityResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path


interface UserApi {

    @GET("users/{username}")
    fun user(@Path("username") username: String): Call<Response<UserNetworkEntityResponse>> // If user not exist - create

    @GET("users")
    fun users(): Call<Response<List<UserNetworkEntityResponse>>>

    @GET("users/me")
    fun profile(@Header("Authorization") session_token: String): Call<Response<UserNetworkEntityResponse>>  // Get user by token

}

