package com.zfr.ctfzoneclient.network.data

import com.google.gson.annotations.SerializedName

data class ClientNetworkEntity (

    @SerializedName("android_id")
    val android_id: String,

    @SerializedName("client_id")
    val client_id: String

)