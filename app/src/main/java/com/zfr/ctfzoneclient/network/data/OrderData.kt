package com.zfr.ctfzoneclient.network.data

data class OrderNetworkEntity (
    val owner_id: String,
    val order_id: String,
    val order_name: String,
    val description: String,
    val thumb_url: String
)