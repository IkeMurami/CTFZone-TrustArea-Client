package com.zfr.ctfzoneclient.network.data

import com.google.gson.annotations.SerializedName

data class ClientNetworkEntity (

    @SerializedName("android_id")
    val deviceId: String,

    @SerializedName("client_id")
    val clientId: String

)