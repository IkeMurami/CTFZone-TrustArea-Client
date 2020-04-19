package ctfz.trustarea.client.service.view

import android.app.IntentService
import android.app.Service
import android.content.Intent
import android.content.Context
import android.util.Log
import androidx.core.content.FileProvider
import ctfz.trustarea.client.PACKAGE_ID
import ctfz.trustarea.client.core.IMS
import ctfz.trustarea.client.repository.LogRepository
import ctfz.trustarea.client.repository.getLogger
import java.io.File


class EchoService : IntentService("EchoService") {

    private val TAG = "EchoService"

    private val ECHO_ACTION = "${PACKAGE_ID}.action.ECHO"

    private lateinit var logger: LogRepository
    private lateinit var ims: IMS

    override fun onCreate() {
        logger = getLogger(applicationContext)
        ims = IMS(applicationContext)

        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        logger = getLogger(applicationContext)
        ims = IMS(applicationContext)


        return Service.START_REDELIVER_INTENT
    }

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ECHO_ACTION -> {
                val success = ims.replyTo(intent, intent)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
