package ctfz.trustarea.client.core

import okhttp3.ResponseBody
import java.lang.Exception

class InvalidTokenException(message: String) : Exception(message)
class ExpiredTokenException(message: String) : Exception(message)
class NetworkException(message: String) : Exception(message)
class ResponseErrorException(message: String, val error: ResponseBody) : Exception(message)
