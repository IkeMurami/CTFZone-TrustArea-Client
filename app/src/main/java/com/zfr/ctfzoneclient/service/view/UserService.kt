package com.zfr.ctfzoneclient.service.view

import android.app.IntentService
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.zfr.ctfzoneclient.network.ControllerApi
import com.zfr.ctfzoneclient.network.data.UserNetworkEntity
import retrofit2.Callback
import okhttp3.Credentials
import retrofit2.Call
import retrofit2.Response
import retrofit2.await
import com.zfr.ctfzoneclient.PACKAGE_ID
import com.zfr.ctfzoneclient.core.ResponseErrorException
import com.zfr.ctfzoneclient.network.data.TokenNetworkEntity
import com.zfr.ctfzoneclient.network.data.asErrorNetworkEntity
import com.zfr.ctfzoneclient.repository.LogRepository
import com.zfr.ctfzoneclient.repository.UsersRepository
import com.zfr.ctfzoneclient.repository.getLogger
import com.zfr.ctfzoneclient.repository.getUserRepository
import com.zfr.ctfzoneclient.service.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.lang.Exception

private const val ACTION_GET_USER = "${PACKAGE_ID}.action.GET_USER"
private const val ACTION_GET_USERS = "${PACKAGE_ID}.action.GET_USERS"
private const val ACTION_GET_PROFILE = "${PACKAGE_ID}.action.GET_PROFILE"

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * helper methods.
 */
class UserService : IntentService("UserService") {

    private val TAG = "UserService"

    private lateinit var logger: LogRepository
    private lateinit var userRepository: UsersRepository

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
        userRepository = getUserRepository(applicationContext)

        logger.info(TAG, "Started")

        return Service.START_REDELIVER_INTENT
    }

    override fun onHandleIntent(intent: Intent?) {
        logger = getLogger(applicationContext)
        logger.info(TAG, intent?.action!!)

        when (intent?.action) {
            ACTION_GET_USER -> {
                val user = intent.asUserNetworkEntity()
                val pendingIntent = intent.getPendingIntent()

                logger.info(TAG, "Get user info by username ${user.username}")

                handleActionUser(user.username, pendingIntent)
            }
            ACTION_GET_USERS -> {
                val pendingIntent = intent.getPendingIntent()
                logger.info(TAG, "Get all users info")

                handleActionListUser(pendingIntent)
            }
            ACTION_GET_PROFILE -> {
                val token = intent.asTokenNetworkEntity()
                val pendingIntent = intent.getPendingIntent()

                logger.info(TAG, "Get profile by token ${token.token}")
                handleActionProfile(token, pendingIntent)
            }
        }
    }

    private fun handleActionUser(username: String?, pendingIntent: PendingIntent?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val user = userRepository.userInfo(username!!)

                sendSuccess(pendingIntent, user?.asIntent(Intent()))
            }
            catch (e: ResponseErrorException) {
                sendError(pendingIntent, e.error)
            }
            catch (e: Exception) {
                sendException(pendingIntent, e.localizedMessage!!)
            }
        }
    }

    private fun handleActionListUser(pendingIntent: PendingIntent?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val users = userRepository.usersList()

                sendSuccess(pendingIntent, users?.asIntent(Intent()))
            }
            catch (e: ResponseErrorException) {
                sendError(pendingIntent, e.error)
            }
            catch (e: Exception) {
                sendException(pendingIntent, e.localizedMessage!!)
            }
        }
    }

    private fun handleActionProfile(token: TokenNetworkEntity, pendingIntent: PendingIntent?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val user = userRepository.updateProfile(token)

                sendSuccess(pendingIntent, user?.asIntent(Intent()))
            }
            catch (e: ResponseErrorException) {
                sendError(pendingIntent, e.error)
            }
            catch (e: Exception) {
                sendException(pendingIntent, e.localizedMessage!!)
            }
        }
    }

    override fun onDestroy() {
        logger.info(TAG, "Destroy")
        super.onDestroy()
    }
}
