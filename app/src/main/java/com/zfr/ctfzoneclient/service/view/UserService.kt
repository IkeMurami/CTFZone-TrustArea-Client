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
import com.zfr.ctfzoneclient.core.Responder.Companion.sendError
import com.zfr.ctfzoneclient.core.Responder.Companion.sendException
import com.zfr.ctfzoneclient.core.Responder.Companion.sendSuccess
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

        val actionCallback = intent.callback()

        when (intent?.action) {
            ACTION_GET_USER -> {
                val user = intent.asUserNetworkEntity()

                logger.info(TAG, "Get user info by username ${user.username}")

                handleActionUser(user.username, actionCallback!!)
            }
            ACTION_GET_USERS -> {
                logger.info(TAG, "Get all users info")

                handleActionListUser(actionCallback!!)
            }
            ACTION_GET_PROFILE -> {
                val token = intent.asTokenNetworkEntity()

                logger.info(TAG, "Get profile by token ${token.token}")
                handleActionProfile(token, actionCallback!!)
            }
        }
    }

    private fun handleActionUser(username: String?, actionCallback: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val user = userRepository.userInfo(username!!)

                sendSuccess(logger, TAG, applicationContext, user?.asIntent(Intent()), actionCallback)
            }
            catch (e: ResponseErrorException) {
                sendError(logger, TAG, applicationContext, e.error, actionCallback)
            }
            catch (e: Exception) {
                sendException(logger, TAG, applicationContext, e.localizedMessage!!, actionCallback)
            }
        }
    }

    private fun handleActionListUser(actionCallback: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val users = userRepository.usersList()

                sendSuccess(logger, TAG, applicationContext, users?.asIntent(Intent()), actionCallback)
            }
            catch (e: ResponseErrorException) {
                sendError(logger, TAG, applicationContext, e.error, actionCallback)
            }
            catch (e: Exception) {
                sendException(logger, TAG, applicationContext, e.localizedMessage!!, actionCallback)
            }
        }
    }

    private fun handleActionProfile(token: TokenNetworkEntity, actionCallback: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val user = userRepository.updateProfile(token)

                sendSuccess(logger, TAG, applicationContext, user?.asIntent(Intent()), actionCallback)
            }
            catch (e: ResponseErrorException) {
                sendError(logger, TAG, applicationContext, e.error, actionCallback)
            }
            catch (e: Exception) {
                sendException(logger, TAG, applicationContext, e.localizedMessage!!, actionCallback)
            }
        }
    }

    override fun onDestroy() {
        logger.info(TAG, "Destroy")
        super.onDestroy()
    }
}
