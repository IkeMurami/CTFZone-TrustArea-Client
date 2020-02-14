package com.zfr.ctfzoneclient.network.data

import com.google.gson.annotations.SerializedName

data class User (
    @SerializedName("login")
    val login: String,
    @SerializedName("user_id")
    val user_id: String
)
