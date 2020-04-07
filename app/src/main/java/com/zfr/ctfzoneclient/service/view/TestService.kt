package com.zfr.ctfzoneclient.service.view

import android.app.IntentService
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.zfr.ctfzoneclient.network.data.UserNetworkEntity
import com.zfr.ctfzoneclient.service.data.asUserNetworkEntity
import com.zfr.ctfzoneclient.PACKAGE_ID
import com.zfr.ctfzoneclient.database.getDatabase
import com.zfr.ctfzoneclient.network.data.TokenNetworkEntity
import com.zfr.ctfzoneclient.network.data.asDatabaseEntity
import com.zfr.ctfzoneclient.service.data.asIntent
import com.zfr.ctfzoneclient.service.data.asTokenNetworkEntity

// IntentService can perform, e.g. ACTION_TEST
private const val ACTION_TEST_REGISTER = "${PACKAGE_ID}.action.TEST_REGISTER"
private const val ACTION_TEST_SESSION = "${PACKAGE_ID}.action.TEST_SESSION"

// ACTION POC
private const val ACTION_TEST_POC_ROOM_SQLI = "${PACKAGE_ID}.action.TEST_ROOM_SQLI"

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

    private val TAG = "TestService"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        return Service.START_REDELIVER_INTENT
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.d(TAG, intent?.action)

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

                handleActionResult(intent)
            }

            // PoC
            ACTION_TEST_POC_ROOM_SQLI -> {
                handleActionPoC_ROOM_SQLi()
            }
        }
    }

    private fun handleActionPoC_ROOM_SQLi() {
        Log.d(TAG, "PoC Room SQLi Started")
        val db = getDatabase(applicationContext).userDao
        val user = UserNetworkEntity(
            username = "Testuser",
            first_name = "FN Testuser",
            last_name = "LN Testuser",
            user_id = "ID Testuser"
        ).asDatabaseEntity()

        db.insertUser(user)

        Log.d(TAG, "PoC Room SQLi Insert user to table")
        var user1 = db.getUser("ID Testuser")
        Log.d(TAG, user1.toString())

        user1 = db.getUser("ID %")
        Log.d(TAG, user1.toString())

        user1 = db.getUser("ID blabla OR 1=1 ")
        Log.d(TAG, user1.toString())


        Log.d(TAG, "PoC Room SQLi End")
    }

    private fun handleActionResult(intent: Intent) {
        Log.d(TAG, intent.asTokenNetworkEntity().toString())
    }

    private fun handleActionTestRegister(user: UserNetworkEntity) {
        val intent_local = Intent(applicationContext, TestService::class.java)
        intent_local.action = ACTION_PENDING_INTENT_RETURN_VALUE
        val pendingIntent = PendingIntent.getService(applicationContext, 0, intent_local, 0)

        // Android not allow send implicit intent to myself
        // var intent_remote = Intent("${PACKAGE_ID}.action.AUTH_REGISTRATION")
        var intent_remote = Intent(applicationContext, AuthService::class.java).apply {
            action = "${PACKAGE_ID}.action.AUTH_REGISTRATION"
        }

        intent_remote = user.asIntent(intent_remote)
        intent_remote.putExtra(EXTRA_PENDING_INTENT, pendingIntent)

        startService(intent_remote)
    }

    private fun handleActionTestSession(session: TokenNetworkEntity) {
        val intent_local = Intent(applicationContext, TestService::class.java)
        intent_local.action = ACTION_PENDING_INTENT_RETURN_VALUE
        val pendingIntent = PendingIntent.getService(applicationContext, 0, intent_local, 0)

        // Android not allow send implicit intent to myself
        // var intent_remote = Intent("${PACKAGE_ID}.action.AUTH_SESSION")
        var intent_remote = Intent(applicationContext, AuthService::class.java).apply {
            action = "${PACKAGE_ID}.action.AUTH_SESSION"
        }

        intent_remote = session.asIntent(intent_remote)
        intent_remote.putExtra(EXTRA_PENDING_INTENT, pendingIntent)

        startService(intent_remote)
    }

    override fun onDestroy() {
        Log.d(TAG, "Destroy")
        super.onDestroy()
    }
}
