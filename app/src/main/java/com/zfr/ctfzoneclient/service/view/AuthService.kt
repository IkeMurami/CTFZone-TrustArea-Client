package com.zfr.ctfzoneclient.service.view

import android.app.IntentService
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zfr.ctfzoneclient.network.ControllerApi
import com.zfr.ctfzoneclient.network.data.UserNetworkEntity

import com.zfr.ctfzoneclient.PACKAGE_ID
import com.zfr.ctfzoneclient.core.ResponseErrorException
import com.zfr.ctfzoneclient.network.data.ErrorNetworkEntity
import com.zfr.ctfzoneclient.network.data.TokenNetworkEntity
import com.zfr.ctfzoneclient.network.data.asErrorNetworkEntity
import com.zfr.ctfzoneclient.repository.*
import com.zfr.ctfzoneclient.service.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception


// IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
private const val ACTION_AUTH_REGISTRATION = "${PACKAGE_ID}.action.AUTH_REGISTRATION"  // request refresh token
private const val ACTION_AUTH_SESSION = "${PACKAGE_ID}.action.AUTH_SESSION"  // refresh token -> session



/**
 * An AuthService subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * helper methods.
 */
class AuthService : IntentService("AuthService") {

    private val TAG = "AuthService"

    private lateinit var logger: LogRepository
    private lateinit var userRepository: UsersRepository
    private lateinit var sessionRepository: SessionRepository

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
        sessionRepository = getSessionRepo(applicationContext)

        logger.info(TAG, "Started")

        return Service.START_REDELIVER_INTENT
    }

    override fun onHandleIntent(intent: Intent?) {
        logger = getLogger(applicationContext)
        logger.info(TAG, intent?.action!!)

        when (intent?.action) {
            ACTION_AUTH_REGISTRATION -> {
                val user = intent.asUserNetworkEntity()
                val pendingIntent = intent.getPendingIntent()

                logger.info(TAG, "Registration ${user}")
                handleActionAuthRegistration(user, pendingIntent)
            }
            ACTION_AUTH_SESSION -> {
                val token = intent.asTokenNetworkEntity()
                val pendingIntent = intent.getPendingIntent()

                logger.info(TAG, "Authorization ${token}")
                handleActionGetSession(token, pendingIntent)
            }
        }
    }

    /**
     * Handle action Registration in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionAuthRegistration(user: UserNetworkEntity, pendingIntent: PendingIntent?) {

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val refreshToken = sessionRepository.regUser(user)
                val sessionToken = sessionRepository.getSession(refreshToken!!)

                logger.info(TAG, "Cache reg data")

                userRepository.updateProfile(sessionToken!!)  // user and session token already cached
                sessionRepository.cacheToken(refreshToken, user)
                // sessionRepository.cacheToken(sessionToken, user)

                sendSuccess(pendingIntent, refreshToken?.asIntent(Intent()))
            }
            catch (e: ResponseErrorException) {
                sendError(pendingIntent, e.error)
            }
            catch (e: Exception) {
                sendException(pendingIntent, e.localizedMessage!!)
            }

        }

    }

    /**
     * Handle action Session in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionGetSession(token: TokenNetworkEntity, pendingIntent: PendingIntent?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sessionToken = sessionRepository.getSession(token)

                // for update cache info
                val user = userRepository.userInfo(sessionToken!!)

                sendSuccess(pendingIntent, sessionToken?.asIntent(Intent()))
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

    companion object {


        @JvmStatic
        fun startActionAuthRegistration(context: Context, user: UserNetworkEntity) {
            val intent = Intent(context, AuthService::class.java).apply {
                action = ACTION_AUTH_REGISTRATION
            }.let {
                    intent -> user.asIntent(intent)
            }

            context.startService(intent)
        }

        @JvmStatic
        fun startActionGetSession(context: Context, refresh_token: TokenNetworkEntity) {
            val intent = Intent(context, AuthService::class.java).apply {
                action = ACTION_AUTH_SESSION
            }.let {
                intent -> refresh_token.asIntent(intent)
            }

            context.startService(intent)
        }
    }
}


