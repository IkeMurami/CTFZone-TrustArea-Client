package ctfz.trustarea.client.service.view

import android.app.IntentService
import android.app.Service
import android.content.Intent
import android.content.Context
import ctfz.trustarea.client.PACKAGE_ID
import ctfz.trustarea.client.core.IMS
import ctfz.trustarea.client.core.Responder.Companion.sendError
import ctfz.trustarea.client.core.Responder.Companion.sendException
import ctfz.trustarea.client.core.Responder.Companion.sendSuccess
import ctfz.trustarea.client.network.data.TokenNetworkEntity
import ctfz.trustarea.client.repository.BackupRepository
import ctfz.trustarea.client.repository.LogRepository
import ctfz.trustarea.client.repository.getLogger
import ctfz.trustarea.client.service.data.asTokenNetworkEntity
import ctfz.trustarea.client.service.data.backupIntent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.Exception

private const val ACTION_BACKUP = "${PACKAGE_ID}.action.CREATE_BACKUP"
private const val ACTION_GET = "${PACKAGE_ID}.action.GET_BACKUP"


class BackupService : IntentService("BackupService") {

    private val TAG = "BackupService"
    private lateinit var logger: LogRepository

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        logger = getLogger(applicationContext)

        logger.info(TAG, "Started")

        return Service.START_REDELIVER_INTENT
    }

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_BACKUP -> {
                val token = intent.asTokenNetworkEntity()

                handleActionCreateBackup(token, intent)
            }
            ACTION_GET -> {
                val token = intent.asTokenNetworkEntity()

                handleActionSendBackup(token, intent)
            }
        }
    }


    private fun handleActionCreateBackup(token: TokenNetworkEntity, request: Intent) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val ok = BackupRepository(applicationContext, getLogger(applicationContext)).save(token)

                sendSuccess(logger, TAG, applicationContext, backupIntent(ok, null), request)
            }
            catch (e: Exception) {
                sendException(logger, TAG, applicationContext, e.localizedMessage ?: "null", request)
            }
        }
    }

    private fun handleActionSendBackup(token: TokenNetworkEntity, request: Intent) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val data = BackupRepository(applicationContext, getLogger(applicationContext)).send(token)

                sendSuccess(logger, TAG, applicationContext, backupIntent(true, data), request)
            }
            catch (e: Exception) {
                sendException(logger, TAG, applicationContext, e.localizedMessage ?: "null", request)
            }
        }
    }
}
