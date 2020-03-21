package com.zfr.ctfzoneclient.network.data

import com.google.gson.annotations.SerializedName

enum class TokenType(token_type: String) {
    bearer("bearer"),
    refresh("refresh"),
    access("access"),
    session("session")
}

enum class Scope {
    ORDERS,
    USERS
}

data class TokenNetworkEntity (

    @SerializedName("token_type") val token_type: TokenType = TokenType.access,
    @SerializedName("token") val token: String,
    @SerializedName("expired") val expired: Long = 0

)

