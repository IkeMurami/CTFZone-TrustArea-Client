package com.zfr.ctfzoneclient.network.data

import com.google.gson.annotations.SerializedName


data class UserNetworkEntity (

    @SerializedName("username") val username: String?,
    @SerializedName("first_name") val firstName: String? = null,
    @SerializedName("last_name") val lastName: String? = null,
    @SerializedName("user_id") val user_id: String? = null

)
