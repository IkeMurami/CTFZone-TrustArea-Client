package com.zfr.ctfzoneclient.network.interfaces

import com.zfr.ctfzoneclient.network.data.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import java.lang.Exception


class UserAlreadyRegistered(message: String) : Exception(message)

interface AuthApi {

    @POST("auth/register")
    fun register(@Body user:UserNetworkEntity): Call<Response<TokenNetworkEntity>>  // refresh token

    @POST("auth/session")
    fun session(@Body refresh_token:TokenNetworkEntity): Call<Response<TokenNetworkEntity>>  // access_token

}