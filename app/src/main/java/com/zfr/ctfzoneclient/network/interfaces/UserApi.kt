package com.zfr.ctfzoneclient.network.interfaces


import com.zfr.ctfzoneclient.network.data.UserNetworkEntity
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header


interface UserApi {

    @GET("users/login")
    fun register(@Header("Authorization") credentials:String): Call<UserNetworkEntity> // If user not exist - create

    @GET("users")
    fun users(@Header("Authorization") credentials: String): Call<List<UserNetworkEntity>>

}

