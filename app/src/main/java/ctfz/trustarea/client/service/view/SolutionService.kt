package ctfz.trustarea.client.service.view

import android.app.IntentService
import android.app.Service
import android.content.Intent
import ctfz.trustarea.client.PACKAGE_ID
import ctfz.trustarea.client.core.Responder.Companion.sendError
import ctfz.trustarea.client.core.Responder.Companion.sendException
import ctfz.trustarea.client.core.Responder.Companion.sendSuccess
import ctfz.trustarea.client.core.ResponseErrorException
import ctfz.trustarea.client.network.ControllerApi
import ctfz.trustarea.client.network.data.SolutionNetworkEntity
import ctfz.trustarea.client.network.data.TokenNetworkEntity
import ctfz.trustarea.client.repository.LogRepository
import ctfz.trustarea.client.repository.getLogger
import ctfz.trustarea.client.service.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

                handleActionSolutionSend(token, solution, intent)
                // logger.info(TAG, "Test after party")
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionSolutionSend(token: TokenNetworkEntity, solution: SolutionNetworkEntity, request: Intent) {

        CoroutineScope(Dispatchers.IO).launch {

            try {
                ControllerApi().getSolutionApi().solve(token.token, solution).execute().let {
                    if (it.isSuccessful) {
                        val task = it.body()?.data
                        logger.info(TAG, "Challenge ${task?.challenge} solve correct by user with token ${token} - ${solution}")

                        sendSuccess(logger, TAG, applicationContext, task?.asIntent(Intent())!!, request)
                    }
                    else {
                        throw ResponseErrorException("Wrong solution", it.errorBody()!!)
                    }
                }
            }
            catch (e: ResponseErrorException) {
                sendError(logger, TAG, applicationContext, e.error, request)
            }
            catch (e: Exception) {
                sendException(logger, TAG, applicationContext, e.localizedMessage!!, request)
            }
        }
    }
}
