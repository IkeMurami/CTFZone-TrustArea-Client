package ctfz.trustarea.client.network.data

import android.content.Intent
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import java.io.Serializable


enum class RespondStatus(status: String) {
    success("success"),
    failure("failure")
}

sealed class ResponseData : Serializable

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


data class RefreshTokenNetworkEntity (@SerializedName("refresh_token") val refresh_token: String) : ResponseData()


data class UserNetworkEntity (

    @SerializedName("username") val username: String? = null,
    @SerializedName("first_name") val first_name: String? = null,
    @SerializedName("last_name") val last_name: String? = null

) : ResponseData()


data class UserNetworkEntityResponse (

    @SerializedName("Username") val Username: String? = null,
    @SerializedName("FirstName") val FirstName: String? = null,
    @SerializedName("LastName") val LastName: String? = null,
    @SerializedName("Date") val Date: String? = null

) : ResponseData()


data class TaskNetworkEntity (

    @SerializedName("task_id") val task_id: Int?,
    @SerializedName("description") val description: String,
    @SerializedName("challenge") val challenge: String,
    @SerializedName("reward") val reward: String

) : ResponseData()


data class TaskNetworkEntityResponse (

    @SerializedName("ID") val ID: Int?,
    @SerializedName("Description") val Description: String,
    @SerializedName("Challenge") val Challenge: String,
    @SerializedName("Reward") val Reward: String,
    @SerializedName("Owner") val Owner: String,
    @SerializedName("Solved") val Solved: Boolean,
    @SerializedName("Date") val Date: String

) : ResponseData()


data class SolutionNetworkEntity(

    @SerializedName("task_id") val task_id: Int,
    @SerializedName("solution") val solution: String

) : ResponseData()


object ErrorNetworkEntity: ResponseData()

fun ResponseBody.asErrorNetworkEntity(): Response<ErrorNetworkEntity>? {
    val gson = Gson()
    val type = object : TypeToken<Response<ErrorNetworkEntity>>(){}.type
    val errorBody: Response<ErrorNetworkEntity>? = gson.fromJson(this.charStream(), type)

    return errorBody
}



private const val EXTRA_MESSAGE = "Message"
private const val EXTRA_ERRORS = "Errors"

fun Response<Any>.asIntent(intent: Intent): Intent {
    intent.apply {
        putExtra(EXTRA_MESSAGE, message)
        putStringArrayListExtra(EXTRA_ERRORS, ArrayList(errors))
    }

    return intent
}