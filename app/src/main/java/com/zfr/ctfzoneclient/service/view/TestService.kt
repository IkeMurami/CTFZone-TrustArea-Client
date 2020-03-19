package com.zfr.ctfzoneclient.service.view

import android.app.IntentService
import android.app.PendingIntent
import android.content.Intent
import android.content.Context
import com.zfr.ctfzoneclient.network.data.UserNetworkEntity
import com.zfr.ctfzoneclient.service.data.asUserNetworkEntity
import com.zfr.ctfzoneclient.PACKAGE_ID
import com.zfr.ctfzoneclient.network.data.TokenNetworkEntity
import com.zfr.ctfzoneclient.service.data.asIntent
import com.zfr.ctfzoneclient.service.data.asTokenNetworkEntity

// IntentService can perform, e.g. ACTION_TEST
private const val ACTION_TEST_REGISTER = "${PACKAGE_ID}.action.TEST_REGISTER"
private const val ACTION_TEST_SESSION = "${PACKAGE_ID}.action.TEST_SESSION"

private const val ACTION_PENDING_INTENT_RETURN_VALUE = "${PACKAGE_ID}.action.RETURN"

// Extra params
private const val EXTRA_PENDING_INTENT = "PENDING_INTENT"

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
class TestService : IntentService("TestService") {

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_TEST_REGISTER -> {
                val user = intent.asUserNetworkEntity()
                handleActionTestRegister(user)
            }
            ACTION_TEST_SESSION -> {
                val session = intent.asTokenNetworkEntity()

                handleActionTestSession(session)
            }
            ACTION_PENDING_INTENT_RETURN_VALUE -> {

            }
        }
    }


    private fun handleActionTestRegister(user: UserNetworkEntity) {
        val intent_local = Intent(applicationContext, TestService::class.java)
        intent_local.action = ACTION_PENDING_INTENT_RETURN_VALUE
        val pendingIntent = PendingIntent.getService(applicationContext, 0, intent_local, 0)
        var intent_remote = Intent("${PACKAGE_ID}.action.AUTH_REGISTRATION")

        intent_remote = user.asIntent(intent_remote)
        intent_remote.putExtra(EXTRA_PENDING_INTENT, pendingIntent)

        startService(intent_remote)
    }

    private fun handleActionTestSession(session: TokenNetworkEntity) {
        val intent_local = Intent(applicationContext, TestService::class.java)
        intent_local.action = ACTION_PENDING_INTENT_RETURN_VALUE
        val pendingIntent = PendingIntent.getService(applicationContext, 0, intent_local, 0)

        var intent_remote = Intent("${PACKAGE_ID}.action.AUTH_SESSION")

        intent_remote = session.asIntent(intent_remote)
        intent_remote.putExtra(EXTRA_PENDING_INTENT, pendingIntent)

        startService(intent_remote)
    }



}
