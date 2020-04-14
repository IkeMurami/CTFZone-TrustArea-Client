package ctfz.trustarea.client.network.interfaces

import ctfz.trustarea.client.network.data.LogNetworkEntity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface LogApi {

    @POST("logging")
    fun report(@Body message:LogNetworkEntity): Call<ResponseBody>

}