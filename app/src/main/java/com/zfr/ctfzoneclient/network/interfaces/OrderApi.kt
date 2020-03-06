package com.zfr.ctfzoneclient.network.interfaces

import com.zfr.ctfzoneclient.network.data.OrderNetworkEntity
import retrofit2.Call
import retrofit2.http.*


interface OrderApi {

    @POST("orders/create")
    fun create(@Body order:OrderNetworkEntity): Call<OrderNetworkEntity>

    @GET("orders/list")
    fun orders(): Call<List<OrderNetworkEntity>>

    @GET("orders/{order_id}/get")
    fun order(@Path("order_id") order_id: String): Call<OrderNetworkEntity>

    @POST("orders/{order_id}/update")
    fun update(@Path("order_id") order_id: String, @Body order: OrderNetworkEntity): Call<OrderNetworkEntity>

    @GET("orders/{order_id}/delete")
    fun delete(@Path("order_id") order_id: String)

}