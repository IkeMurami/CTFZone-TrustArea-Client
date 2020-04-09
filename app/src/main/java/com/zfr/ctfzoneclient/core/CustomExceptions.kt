package com.zfr.ctfzoneclient.core

import com.zfr.ctfzoneclient.network.data.ErrorNetworkEntity
import com.zfr.ctfzoneclient.network.data.Response
import okhttp3.ResponseBody
import java.lang.Exception

class InvalidTokenException(message: String) : Exception(message)
class ExpiredTokenException(message: String) : Exception(message)
class NetworkException(message: String) : Exception(message)
class ResponseErrorException(message: String, val error: ResponseBody) : Exception(message)
