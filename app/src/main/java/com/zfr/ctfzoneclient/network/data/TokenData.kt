package com.zfr.ctfzoneclient.network.data

import com.google.gson.annotations.SerializedName

enum class TokenType(val tyken_type: String) {
    BEARER("bearer"),
    REFRESH("refresh"),
    ACCESS("access")
}

enum class Scope {
    ORDERS,
    USERS
}

data class TokenNetworkEntity (

    @SerializedName("token_type") val tokenType: TokenType = TokenType.ACCESS,
    @SerializedName("token") val token: String,
    @SerializedName("expired") val expired: Long = 0

)

