package com.zfr.ctfzoneclient.network.interfaces

import com.zfr.ctfzoneclient.network.data.LogNetworkEntity
import retrofit2.http.Body
import retrofit2.http.POST


interface LogApi {

    @POST("logging")
    fun report(@Body message:LogNetworkEntity)

}