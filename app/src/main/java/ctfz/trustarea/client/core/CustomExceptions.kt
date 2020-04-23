package ctfz.trustarea.client.core

import okhttp3.ResponseBody
import kotlin.Exception


class NetworkException(message: String, val error: ResponseBody) : Exception(message)
class ResponseErrorException(message: String, val error: ResponseBody) : Exception(message)
class InvalidTokenException(message: String) : Exception(message)