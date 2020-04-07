package com.zfr.ctfzoneclient.service.view

import android.app.IntentService
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.Context
import android.util.Log
import com.zfr.ctfzoneclient.network.ControllerApi
import com.zfr.ctfzoneclient.network.data.UserNetworkEntity

import com.zfr.ctfzoneclient.PACKAGE_ID
import com.zfr.ctfzoneclient.database.getDatabase
import com.zfr.ctfzoneclient.network.data.OtherNetworkEntity
import com.zfr.ctfzoneclient.network.data.ResponseData
import com.zfr.ctfzoneclient.network.data.TokenNetworkEntity
import com.zfr.ctfzoneclient.repository.*
import com.zfr.ctfzoneclient.service.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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
        logger.debug(TAG, intent?.action!!)

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
        val authApi = ControllerApi().getAuthApi()
        val refreshTokenReq = authApi.register(user)

        refreshTokenReq.enqueue(object : Callback<com.zfr.ctfzoneclient.network.data.Response<ResponseData>> {
            override fun onResponse(
                call: Call<com.zfr.ctfzoneclient.network.data.Response<ResponseData>>,
                response: Response<com.zfr.ctfzoneclient.network.data.Response<ResponseData>>
            ) {
                if (response.isSuccessful) {
                    val refreshToken = response.body()!!.data as TokenNetworkEntity
                    logger.info(TAG, "Cache reg data")

                    CoroutineScope(Dispatchers.IO).launch {
                        userRepository.updateUserInfo(user.username!!)
                        sessionRepository.cacheToken(refreshToken, user)
                    }

                    // send response to other component
                    logger.info(TAG, "Send response to ${pendingIntent?.creatorPackage}")
                    pendingIntent?.send(applicationContext, 0, refreshToken.asIntent(Intent()))
                }
                else {
                    val resp = response.body()!!
                    logger.info(TAG, "Return message: ${resp.message}; errors: ${resp.errors}")
                    logger.info(TAG, "Send response to ${pendingIntent?.creatorPackage}")

                    pendingIntent?.send(applicationContext, 0, errorIntent(resp.message, resp.errors))
                }
            }

            override fun onFailure(
                call: Call<com.zfr.ctfzoneclient.network.data.Response<ResponseData>>,
                t: Throwable
            ) {
                logger.info(TAG, "Request failure: ${t.localizedMessage}")
                pendingIntent?.send(applicationContext, 0, errorIntent("Request failure", listOf(t.localizedMessage)))
            }
        })

    }

    /**
     * Handle action Session in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionGetSession(token: TokenNetworkEntity, pendingIntent: PendingIntent?) {
        val authApi = ControllerApi().getAuthApi()
        val accessTokenReq = authApi.session(token)

        accessTokenReq.enqueue(object : Callback<com.zfr.ctfzoneclient.network.data.Response<ResponseData>>{
            override fun onResponse(
                call: Call<com.zfr.ctfzoneclient.network.data.Response<ResponseData>>,
                response: Response<com.zfr.ctfzoneclient.network.data.Response<ResponseData>>
            ) {
                if (response.isSuccessful) {
                    val accessToken = response.body()!!.data as TokenNetworkEntity
                    logger.info(TAG, "Cache auth data")

                    CoroutineScope(Dispatchers.IO).launch {
                        val user = userRepository.userInfo(accessToken)

                        sessionRepository.cacheToken(accessToken, (user as com.zfr.ctfzoneclient.network.data.Response<UserNetworkEntity>).data)
                    }
                    pendingIntent?.send(applicationContext, 0, accessToken.asIntent(Intent()))
                }
                else {
                    val resp = response.body()!!
                    logger.info(TAG, "Return message: ${resp.message}; errors: ${resp.errors}")
                    logger.info(TAG, "Send response to ${pendingIntent?.creatorPackage}")

                    pendingIntent?.send(applicationContext, 0, errorIntent(resp.message, resp.errors))
                }
            }

            override fun onFailure(
                call: Call<com.zfr.ctfzoneclient.network.data.Response<ResponseData>>,
                t: Throwable
            ) {
                logger.info(TAG, "Request failure: ${t.localizedMessage}")
                pendingIntent?.send(applicationContext, 0, errorIntent("Request failure", listOf(t.localizedMessage)))
            }
        })

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


