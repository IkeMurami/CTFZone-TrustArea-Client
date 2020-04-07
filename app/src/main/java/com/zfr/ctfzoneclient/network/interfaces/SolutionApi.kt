package com.zfr.ctfzoneclient.network.interfaces

import com.zfr.ctfzoneclient.network.data.Response
import com.zfr.ctfzoneclient.network.data.ResponseData
import com.zfr.ctfzoneclient.network.data.SolutionNetworkEntity
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.io.StringReader


interface SolutionApi {

    @POST("solutions")
    fun solve(@Header("Authorization") sessionToken: String, @Body solution: SolutionNetworkEntity): Call<Response<ResponseData>>

}