package ctfz.trustarea.client.service.view

import android.app.IntentService
import android.app.Service
import android.content.Intent
import android.content.Context
import ctfz.trustarea.client.network.data.UserNetworkEntity

import ctfz.trustarea.client.PACKAGE_ID
import ctfz.trustarea.client.core.Responder.Companion.sendError
import ctfz.trustarea.client.core.Responder.Companion.sendException
import ctfz.trustarea.client.core.Responder.Companion.sendSuccess
import ctfz.trustarea.client.core.ResponseErrorException
import ctfz.trustarea.client.network.data.TokenNetworkEntity
import ctfz.trustarea.client.repository.*
import ctfz.trustarea.client.service.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
                val actionCallback = intent.callback()

                logger.info(TAG, "Registration ${user}")
                handleActionAuthRegistration(user, actionCallback!!)
            }
            ACTION_AUTH_SESSION -> {
                val token = intent.asTokenNetworkEntity()
                val actionCallback = intent.callback()

                logger.info(TAG, "Authorization ${token}")
                handleActionGetSession(token, actionCallback!!)
            }
        }
    }

    /**
     * Handle action Registration in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionAuthRegistration(user: UserNetworkEntity, actionCallback: String) {

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val refreshToken = sessionRepository.regUser(user)
                val sessionToken = sessionRepository.getSession(refreshToken!!)

                logger.info(TAG, "Cache reg data")

                userRepository.updateProfile(sessionToken!!)  // user and session token already cached
                sessionRepository.cacheToken(refreshToken, user)
                // sessionRepository.cacheToken(sessionToken, user)

                // Log.d(TAG, "${refreshToken} and ${refToken.getStringExtra("TOKEN")} blyad")
                sendSuccess(logger, TAG, applicationContext, refreshToken?.asIntent(Intent()), actionCallback)
            }
            catch (e: ResponseErrorException) {
                sendError(logger, TAG, applicationContext, e.error, actionCallback)
            }
            catch (e: Exception) {
                sendException(logger, TAG, applicationContext, e.localizedMessage!!, actionCallback)
            }

        }

    }

    /**
     * Handle action Session in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionGetSession(token: TokenNetworkEntity, actionCallback: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sessionToken = sessionRepository.getSession(token)

                // for update cache info
                val user = userRepository.userInfo(sessionToken!!)

                sendSuccess(logger, TAG, applicationContext, sessionToken?.asIntent(Intent()), actionCallback)
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


