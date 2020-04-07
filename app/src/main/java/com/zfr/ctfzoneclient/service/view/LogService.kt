package com.zfr.ctfzoneclient.service.view

import android.app.AlarmManager
import android.app.IntentService
import android.app.PendingIntent
import android.content.Intent
import android.content.Context
import com.zfr.ctfzoneclient.database.getDatabase
import com.zfr.ctfzoneclient.repository.LogRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
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
