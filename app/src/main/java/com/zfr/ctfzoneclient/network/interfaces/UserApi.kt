package com.zfr.ctfzoneclient.network.interfaces


import com.zfr.ctfzoneclient.network.data.User
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST


interface UserApi {

    @GET("users/login")
    fun register(@Header("Authorization") credentials:String): Call<User> // If user not exist - create

    @GET("users/list")
    fun users(@Header("Authorization") credentials: String): Call<List<User>>

}
