package com.zfr.ctfzoneclient.core

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.zfr.ctfzoneclient.network.data.asErrorNetworkEntity
import com.zfr.ctfzoneclient.repository.LogRepository
import com.zfr.ctfzoneclient.service.data.errorIntent
import okhttp3.ResponseBody




class Responder {

    companion object {

        val EXTRA_CALLBACK = "RECEIVER_CALLBACK"

        fun sendSuccess(logger: LogRepository, TAG: String, context: Context, intent: Intent?, callback: String) {

            logger.info(TAG, "Success: Send response to ${callback}")
            context.sendBroadcast(intent?.apply { action = callback })

        }

        fun sendError(logger: LogRepository, TAG: String, context: Context, errorBody: ResponseBody?, callback: String) {
            val error = errorBody?.asErrorNetworkEntity()
            logger.info(TAG, "Error: Return message: ${error?.message}; errors: ${error?.errors}")
            logger.info(TAG, "Error: Send response to ${callback}")

            context.sendBroadcast(errorIntent(error?.message!!, error.errors).apply { action = callback })

        }

        fun sendException(logger: LogRepository, TAG: String, context: Context, message: String, callback: String) {
            logger.info(TAG, "Exception: Send response to ${callback} with message ${message}")

            context.sendBroadcast(errorIntent("Request failure", listOf(message)).apply { action = callback })
        }
    }
}
