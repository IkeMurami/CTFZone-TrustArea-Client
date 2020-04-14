package com.zfr.ctfzoneclient.service.view

import android.app.IntentService
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.Context
import com.zfr.ctfzoneclient.PACKAGE_ID
import com.zfr.ctfzoneclient.core.Responder
import com.zfr.ctfzoneclient.core.Responder.Companion.sendError
import com.zfr.ctfzoneclient.core.Responder.Companion.sendException
import com.zfr.ctfzoneclient.core.Responder.Companion.sendSuccess
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
                val actionCallback = intent.callback()

                handleActionSolutionSend(token, solution, actionCallback!!)
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionSolutionSend(token: TokenNetworkEntity, solution: SolutionNetworkEntity, actionCallback: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ControllerApi().getSolutionApi().solve(token.token, solution).execute().let {
                    if (it.isSuccessful) {
                        val task = it.body()?.data
                        logger.info(TAG, "Challenge ${task?.challenge} solve correct by user with token ${token} - ${solution}")

                        sendSuccess(logger, TAG, applicationContext, task?.asIntent(Intent()), actionCallback)
                    }
                    else {
                        throw ResponseErrorException("Wrong solution", it.errorBody()!!)
                    }
                }
            }
            catch (e: ResponseErrorException) {
                sendError(logger, TAG, applicationContext, e.error, actionCallback)
            }
            catch (e: Exception) {
                sendException(logger, TAG, applicationContext, e.localizedMessage!!, actionCallback)
            }
        }
    }
}
