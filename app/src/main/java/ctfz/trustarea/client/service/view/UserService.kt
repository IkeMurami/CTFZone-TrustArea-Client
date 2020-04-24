package ctfz.trustarea.client.service.view

import android.app.IntentService
import android.app.Service
import android.content.Intent
import ctfz.trustarea.client.PACKAGE_ID
import ctfz.trustarea.client.core.Responder.Companion.sendError
import ctfz.trustarea.client.core.Responder.Companion.sendException
import ctfz.trustarea.client.core.Responder.Companion.sendSuccess
import ctfz.trustarea.client.core.ResponseErrorException
import ctfz.trustarea.client.network.data.TokenNetworkEntity
import ctfz.trustarea.client.repository.LogRepository
import ctfz.trustarea.client.repository.UsersRepository
import ctfz.trustarea.client.repository.getLogger
import ctfz.trustarea.client.repository.getUserRepository
import ctfz.trustarea.client.service.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.Exception

private const val ACTION_GET_USER = "${PACKAGE_ID}.action.GET_USER"
private const val ACTION_GET_USERS = "${PACKAGE_ID}.action.GET_USERS"
private const val ACTION_GET_PROFILE = "${PACKAGE_ID}.action.GET_PROFILE"


class UserService : IntentService("UserService") {

    private val TAG = "UserService"

    private lateinit var logger: LogRepository
    private lateinit var userRepository: UsersRepository


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        logger = getLogger(applicationContext)
        userRepository = getUserRepository(applicationContext)

        logger.info(TAG, "Started")

        return Service.START_REDELIVER_INTENT
    }

    override fun onHandleIntent(intent: Intent?) {
        logger = getLogger(applicationContext)
        logger.info(TAG, intent?.action!!)

        when (intent?.action) {
            ACTION_GET_USER -> {
                val token = intent.asTokenNetworkEntity()
                val user = intent.asUserNetworkEntity()

                logger.info(TAG, "Get user info by username ${user.username}")

                handleActionUser(token, user.username, intent)
            }
            ACTION_GET_USERS -> {
                val token = intent.asTokenNetworkEntity()
                logger.info(TAG, "Get all users info")

                handleActionListUser(token, intent)
            }
            ACTION_GET_PROFILE -> {
                val token = intent.asTokenNetworkEntity()

                logger.info(TAG, "Get profile by token ${token.token}")
                handleActionProfile(token, intent)
            }
        }
    }

    private fun handleActionUser(token: TokenNetworkEntity, username: String?, request: Intent) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val user = userRepository.userInfo(token, username)

                sendSuccess(logger, TAG, applicationContext, user?.asIntent(Intent()), request)
            }
            catch (e: ResponseErrorException) {
                sendError(logger, TAG, applicationContext, e.error, request)
            }
            catch (e: Exception) {
                sendException(logger, TAG, applicationContext, e.localizedMessage!!, request)
            }
        }
    }

    private fun handleActionListUser(token: TokenNetworkEntity, request: Intent) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val users = userRepository.usersList(token)

                sendSuccess(logger, TAG, applicationContext, users?.asIntent(Intent()), request)
            }
            catch (e: ResponseErrorException) {
                sendError(logger, TAG, applicationContext, e.error, request)
            }
            catch (e: Exception) {
                sendException(logger, TAG, applicationContext, e.localizedMessage!!, request)
            }
        }
    }

    private fun handleActionProfile(token: TokenNetworkEntity, request: Intent) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val user = userRepository.updateProfile(token)

                sendSuccess(logger, TAG, applicationContext, user?.asIntent(Intent()), request)
            }
            catch (e: ResponseErrorException) {
                sendError(logger, TAG, applicationContext, e.error, request)
            }
            catch (e: Exception) {
                sendException(logger, TAG, applicationContext, e.localizedMessage!!, request)
            }
        }
    }

    override fun onDestroy() {
        logger.info(TAG, "Destroy")
        super.onDestroy()
    }
}
