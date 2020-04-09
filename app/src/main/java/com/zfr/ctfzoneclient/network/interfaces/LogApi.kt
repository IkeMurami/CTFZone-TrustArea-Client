package com.zfr.ctfzoneclient.network.interfaces

import com.zfr.ctfzoneclient.network.data.LogNetworkEntity
import com.zfr.ctfzoneclient.network.data.Response
import com.zfr.ctfzoneclient.network.data.ResponseData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface LogApi {

    @POST("logging")
    fun report(@Body message:LogNetworkEntity): Call<ResponseBody>

}