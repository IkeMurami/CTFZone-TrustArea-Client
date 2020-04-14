package ctfz.trustarea.client.network.interfaces

import ctfz.trustarea.client.network.data.Response
import ctfz.trustarea.client.network.data.SolutionNetworkEntity
import ctfz.trustarea.client.network.data.TaskNetworkEntity
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


interface SolutionApi {

    @POST("solutions")
    fun solve(@Header("Authorization") sessionToken: String, @Body solution: SolutionNetworkEntity): Call<Response<TaskNetworkEntity>>

}