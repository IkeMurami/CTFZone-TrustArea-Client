package com.zfr.ctfzoneclient.network.data

import com.google.gson.annotations.SerializedName


data class UserNetworkEntity (

    @SerializedName("username") val username: String?,
    @SerializedName("first_name") val first_name: String? = null,
    @SerializedName("last_name") val last_name: String? = null,
    @SerializedName("user_id") val user_id: String? = null

)
