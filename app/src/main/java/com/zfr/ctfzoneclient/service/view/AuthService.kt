package com.zfr.ctfzoneclient.service.view

import android.app.IntentService
import android.app.PendingIntent
import android.content.Intent
import android.content.Context
import com.zfr.ctfzoneclient.network.ControllerApi
import com.zfr.ctfzoneclient.network.data.UserNetworkEntity
import com.zfr.ctfzoneclient.service.data.asIntent
import com.zfr.ctfzoneclient.service.data.asUserNetworkEntity

import com.zfr.ctfzoneclient.PACKAGE_ID
import com.zfr.ctfzoneclient.network.data.TokenNetworkEntity
import com.zfr.ctfzoneclient.service.data.asTokenNetworkEntity


// IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
private const val ACTION_AUTH_REGISTRATION = "${PACKAGE_ID}.action.AUTH_REGISTRATION"  // request refresh token
private const val ACTION_AUTH_SESSION = "${PACKAGE_ID}.action.AUTH_SESSION"  // refresh token -> session

private const val EXTRA_PENDING_INTENT = "${PACKAGE_ID}.extra.PENDING_INTENT"  // for return result
/**
 * An AuthService subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * helper methods.
 */
class AuthService : IntentService("AuthService") {

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_AUTH_REGISTRATION -> {
                val pendingIntent = intent.getParcelableExtra<PendingIntent>(EXTRA_PENDING_INTENT)

                handleActionAuthRegistration(intent.asUserNetworkEntity(), pendingIntent)
            }
            ACTION_AUTH_SESSION -> {

                handleActionGetSession(intent.asTokenNetworkEntity())
            }
        }
    }

    /**
     * Handle action Registration in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionAuthRegistration(user: UserNetworkEntity, pendingIntent: PendingIntent?) {
        val authApi = ControllerApi().getAuthApi()
        val refresh_token = authApi.register(user)

        // ...
    }

    /**
     * Handle action Session in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionGetSession(token: TokenNetworkEntity) {
        val authApi = ControllerApi().getAuthApi()
        val access_token = authApi.session(token)

        // ...
    }

    companion object {
        @JvmStatic
        fun startActionAuthRegistration(context: Context, user: UserNetworkEntity) {
            var intent = Intent(context, AuthService::class.java).apply {
                action = ACTION_AUTH_REGISTRATION
            }
            intent = user.asIntent(intent)

            context.startService(intent)
        }

        @JvmStatic
        fun startActionGetSession(context: Context, refresh_token: TokenNetworkEntity) {
            var intent = Intent(context, AuthService::class.java).apply {
                action = ACTION_AUTH_SESSION
            }

            intent = refresh_token.asIntent(intent)
            context.startService(intent)
        }
    }
}


