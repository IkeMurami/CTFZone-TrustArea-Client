package com.zfr.ctfzoneclient.network.data

import com.google.gson.annotations.SerializedName


data class ResponseData<Data> (

    @SerializedName("message") val message: String,
    @SerializedName("data") val data: Data? = null

)