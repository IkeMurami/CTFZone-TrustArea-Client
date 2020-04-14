package ctfz.trustarea.client.service.view

import android.app.IntentService
import android.content.Intent
import ctfz.trustarea.client.database.getDatabase
import ctfz.trustarea.client.repository.LogRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async


// IntentService can perform, e.g. ACTION_SYNC_LOG
const val ACTION_SYNC_LOG = "SYNC_LOG"


class LogService : IntentService("LogService") {


    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_SYNC_LOG -> {
                handleActionSyncLog()
            }
        }
    }

    /**
     * Handle action Sync log in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionSyncLog() {
        CoroutineScope(Dispatchers.IO).async {
            LogRepository(getDatabase(applicationContext)).syncRecords()
        }
    }
}
