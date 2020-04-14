package ctfz.trustarea.client

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import ctfz.trustarea.client.service.view.ACTION_SYNC_LOG
import ctfz.trustarea.client.service.view.LogService


const val PACKAGE_ID = BuildConfig.APPLICATION_ID

class MainActivity : AppCompatActivity() {

    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //startLogManager()
        //stopLogManager()
        this.finish()
    }

    private fun startLogManager() {
        Log.d("MainActivity", "Init Logger")
        // val logger = getLogger(applicationContext)  // init log repository
        // logger.records.observe(this, Observer { data -> Log.d("MainActivity", "records changed") })
        /**
         * init and run alarm manager for sync log repository
         */
        alarmMgr = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmIntent = Intent(applicationContext, LogService::class.java).apply {
            action = ACTION_SYNC_LOG
        }.let { intent ->
            PendingIntent.getService(applicationContext, 0, intent, 0)
        }

        alarmMgr?.setRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + 10 * 1000,  // sync each 60 second
            60 * 1000,
            alarmIntent)
    }

    private fun stopLogManager() {
        alarmIntent = Intent(applicationContext, LogService::class.java).apply {
            action = ACTION_SYNC_LOG
        }.let { intent ->
            PendingIntent.getService(applicationContext, 0, intent, 0)
        }
        alarmMgr?.cancel(alarmIntent)
    }
}
