package ctfz.trustarea.client.network.interfaces

import ctfz.trustarea.client.network.data.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST




interface AuthApi {

    @POST("auth/register")
    fun register(@Body user:UserNetworkEntity): Call<Response<TokenNetworkEntity>>  // refresh token

    @POST("auth/session")
    fun session(@Body refresh_token:RefreshTokenNetworkEntity): Call<Response<TokenNetworkEntity>>  // access_token

}
