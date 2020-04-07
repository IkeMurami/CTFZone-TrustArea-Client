package com.zfr.ctfzoneclient.network.data

import android.content.Intent
import com.google.gson.annotations.SerializedName
import java.io.StringReader



enum class RespondStatus(status: String) {
    success("success"),
    failure("failure")
}

sealed class ResponseData

data class Response<Data> (

    @SerializedName("message") val message: String = "",
    @SerializedName("errors") val errors: List<String> = emptyList(),
    @SerializedName("data") val data: Data? = null

)

/*
 * Response Data
 */
data class TokenNetworkEntity (

    @SerializedName("token_type") val token_type: TokenType = TokenType.access,
    @SerializedName("token") val token: String,
    @SerializedName("expired") val expired: Long? = 0

) : ResponseData()


data class UserNetworkEntity (

    @SerializedName("username") val username: String?,
    @SerializedName("first_name") val first_name: String? = null,
    @SerializedName("last_name") val last_name: String? = null,
    @SerializedName("user_id") val user_id: String? = null

) : ResponseData()


data class TaskNetworkEntity (

    @SerializedName("task_id") val task_id: Int?,
    @SerializedName("description") val description: String,
    @SerializedName("challenge") val challenge: String,
    @SerializedName("reward") val reward: String

) : ResponseData()


data class SolutionNetworkEntity(

    @SerializedName("task_id") val taskId: Int,
    @SerializedName("solution") val solution: String

) : ResponseData()


object OtherNetworkEntity: ResponseData()



private const val EXTRA_MESSAGE = "Message"
private const val EXTRA_ERRORS = "Errors"

fun Response<Any>.asIntent(intent: Intent): Intent {
    intent.apply {
        putExtra(EXTRA_MESSAGE, message)
        putStringArrayListExtra(EXTRA_ERRORS, ArrayList(errors))
    }

    return intent
}