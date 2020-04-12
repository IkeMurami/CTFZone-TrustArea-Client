package com.zfr.ctfzoneclient.service.view

import android.app.IntentService
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.Context
import com.zfr.ctfzoneclient.PACKAGE_ID
import com.zfr.ctfzoneclient.core.ResponseErrorException
import com.zfr.ctfzoneclient.network.ControllerApi
import com.zfr.ctfzoneclient.network.data.SolutionNetworkEntity
import com.zfr.ctfzoneclient.network.data.TokenNetworkEntity
import com.zfr.ctfzoneclient.network.data.asErrorNetworkEntity
import com.zfr.ctfzoneclient.repository.LogRepository
import com.zfr.ctfzoneclient.repository.getLogger
import com.zfr.ctfzoneclient.repository.getTaskRepository
import com.zfr.ctfzoneclient.service.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.lang.Exception


private const val ACTION_SOLUTION_SEND = "${PACKAGE_ID}.action.SOLUTION_SEND"




class SolutionService : IntentService("SolutionService") {

    private val TAG = "SolutionService"
    private lateinit var logger: LogRepository

    fun sendSuccess(pendingIntent: PendingIntent?, intent: Intent?) {
        logger.info(TAG, "Send response to ${pendingIntent?.creatorPackage}")
        pendingIntent?.send(applicationContext, 0, intent)
    }

    fun sendError(pendingIntent: PendingIntent?, errorBody: ResponseBody?) {
        val error = errorBody?.asErrorNetworkEntity()
        logger.info(TAG, "Return message: ${error?.message}; errors: ${error?.errors}")
        logger.info(TAG, "Send response to ${pendingIntent?.creatorPackage}")

        pendingIntent?.send(applicationContext, 0, errorIntent(error?.message!!, error.errors))
    }

    fun sendException(pendingIntent: PendingIntent?, message: String) {
        logger.info(TAG, "Request failure: ${message}")

        pendingIntent?.send(applicationContext, 0, errorIntent("Request failure", listOf(message)))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        logger = getLogger(applicationContext)

        logger.info(TAG, "Started")

        return Service.START_REDELIVER_INTENT
    }

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_SOLUTION_SEND -> {
                val token = intent.asTokenNetworkEntity()
                val solution = intent.asSolutionNetworkEntity()
                val pendingIntent = intent.getPendingIntent()

                handleActionSolutionSend(token, solution, pendingIntent)
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionSolutionSend(token: TokenNetworkEntity, solution: SolutionNetworkEntity, pendingIntent: PendingIntent?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ControllerApi().getSolutionApi().solve(token.token, solution).execute().let {
                    if (it.isSuccessful) {
                        val task = it.body()?.data
                        logger.info(TAG, "Challenge ${task?.challenge} solve correct by user with token ${token} - ${solution}")

                        sendSuccess(pendingIntent, task?.asIntent(Intent()))
                    }
                    else {
                        throw ResponseErrorException("Wrong solution", it.errorBody()!!)
                    }
                }
            }
            catch (e: ResponseErrorException) {
                sendError(pendingIntent, e.error)
            }
            catch (e: Exception) {
                sendException(pendingIntent, e.localizedMessage!!)
            }
        }
    }
}
