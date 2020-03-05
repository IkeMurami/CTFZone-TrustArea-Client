package com.zfr.ctfzoneclient.service.view

import android.app.IntentService
import android.content.Intent
import android.content.Context


private const val PACKAGE_ID = "com.zfr.ctfzoneclient"

// TODO: Rename actions, choose action names that describe tasks that this
// IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
private const val ACTION_AUTH_REGISTRATION = "${PACKAGE_ID}.action.AUTH_REGISTRATION"  // creds -> refresh token
private const val ACTION_AUTH_TOKEN = "${PACKAGE_ID}.action.AUTH_TOKEN"  // refresh token -> session

// TODO: Rename parameters
private const val EXTRA_REG_LOGIN = "${PACKAGE_ID}.extra.LOGIN"
private const val EXTRA_REG_PASSWORD = "${PACKAGE_ID}.extra.PASSWORD"

private const val EXTRA_PARAM1 = "${PACKAGE_ID}.extra.PARAM1"
private const val EXTRA_PARAM2 = "${PACKAGE_ID}.extra.PARAM2"

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
class AuthService : IntentService("AuthService") {

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_AUTH_REGISTRATION -> {
                val param1 = intent.getStringExtra(EXTRA_PARAM1)
                val param2 = intent.getStringExtra(EXTRA_PARAM2)
                handleActionAuthRegistration(param1, param2)
            }
            ACTION_AUTH_TOKEN -> {
                val param1 = intent.getStringExtra(EXTRA_PARAM1)
                val param2 = intent.getStringExtra(EXTRA_PARAM2)
                handleActionAuthToken(param1, param2)
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionAuthRegistration(param1: String, param2: String) {
        TODO("Handle action Auth Registration")
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionAuthToken(param1: String, param2: String) {
        TODO("Handle action Auth Token")
    }

    companion object {
        /**
         * Starts this service to perform action Foo with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        // TODO: Customize helper method
        @JvmStatic
        fun startActionAuthRegistration(context: Context, param1: String, param2: String) {
            val intent = Intent(context, AuthService::class.java).apply {
                action = ACTION_AUTH_REGISTRATION
                putExtra(EXTRA_PARAM1, param1)
                putExtra(EXTRA_PARAM2, param2)
            }
            context.startService(intent)
        }

        /**
         * Starts this service to perform action Baz with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        // TODO: Customize helper method
        @JvmStatic
        fun startActionAuthToken(context: Context, param1: String, param2: String) {
            val intent = Intent(context, AuthService::class.java).apply {
                action = ACTION_AUTH_TOKEN
                putExtra(EXTRA_PARAM1, param1)
                putExtra(EXTRA_PARAM2, param2)
            }
            context.startService(intent)
        }
    }
}
