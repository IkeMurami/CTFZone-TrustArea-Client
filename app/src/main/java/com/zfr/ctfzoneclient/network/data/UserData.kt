package com.zfr.ctfzoneclient.network.data

import com.google.gson.annotations.SerializedName

data class UserNetworkEntity (
    @SerializedName("login")
    val login: String,
    @SerializedName("user_id")
    val user_id: String
)


