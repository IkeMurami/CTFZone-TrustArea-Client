package ctfz.trustarea.client.service.view

import android.app.IntentService
import android.content.Intent
import android.content.Context
import ctfz.trustarea.client.PACKAGE_ID
import ctfz.trustarea.client.core.IMS


class EchoService : IntentService("EchoService") {

    private val ECHO_ACTION = "${PACKAGE_ID}.action.ECHO"

    private lateinit var ims: IMS

    override fun onCreate() {
        super.onCreate()
        ims = IMS(applicationContext)
    }

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ECHO_ACTION -> {
                val success = ims.replyTo(intent, intent)
            }
        }
    }
}
