package com.zfr.ctfzoneclient.repository

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.zfr.ctfzoneclient.database.CTFZoneDatabase
import com.zfr.ctfzoneclient.database.data.LogDBEntity
import com.zfr.ctfzoneclient.database.data.asDomainModel
import com.zfr.ctfzoneclient.database.getDatabase
import com.zfr.ctfzoneclient.network.ControllerApi
import com.zfr.ctfzoneclient.network.data.LogNetworkEntity
import com.zfr.ctfzoneclient.network.data.LogType
import com.zfr.ctfzoneclient.network.data.ResponseData
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await


private const val TAG = "LogRepository"
/**
 * Repository for storing logs on disk and send them on backend
 */
class LogRepository(private val database: CTFZoneDatabase) {

    /*
     * Send logs to backend
     */
    fun syncRecords() {

        Log.d(TAG, "SyncRecords")
        val api = ControllerApi().getLoggingApi()
        val records: List<LogNetworkEntity> = database.logDao.getLogQueries().asDomainModel()

        //records.value?.forEach {
        CoroutineScope(Dispatchers.IO).launch {
            records.forEach {

                api.report(it).await()
            }

            clearLogCache()
        }

    }

    /*
     * clear log cache
     */
    fun clearLogCache() {
        Log.d(TAG, "Clear log cache")
        database.logDao.deleteLogQueries()
    }

    /*
     * insert log record
     */
    private fun insert(tag: String, message: String) {
        val record = LogDBEntity(log_type = LogType.debug.name, message = "${tag} - ${message}")
        database.logDao.insert(record)
    }

    fun verbose(tag: String, message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            insert(tag, message)
        }
    }

    fun info(tag: String, message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            insert(tag, message)
        }
    }

    fun debug(tag: String, message: String) {

        CoroutineScope(Dispatchers.IO).launch {
            insert(tag, message)
        }
    }

    fun warning(tag: String, message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            insert(tag, message)
        }
    }

    fun error(tag: String, message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            insert(tag, message)
        }
    }

    fun assert(tag: String, message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            insert(tag, message)
        }
    }
}


private lateinit var INSTANCE: LogRepository


fun getLogger(context: Context): LogRepository {
    synchronized(LogRepository::class.java) {

        if (!::INSTANCE.isInitialized) {
            INSTANCE = LogRepository(getDatabase(context))
        }

    }

    return INSTANCE
}