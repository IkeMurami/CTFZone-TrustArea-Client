package ctfz.trustarea.client.repository

import android.content.Context
import android.util.Log
import ctfz.trustarea.client.database.CTFZoneDatabase
import ctfz.trustarea.client.database.data.LogDBEntity
import ctfz.trustarea.client.database.data.asDomainModel
import ctfz.trustarea.client.database.getDatabase
import ctfz.trustarea.client.network.ControllerApi
import ctfz.trustarea.client.network.data.LogNetworkEntity
import ctfz.trustarea.client.network.data.LogType
import kotlinx.coroutines.*
import okhttp3.ResponseBody
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

    fun sendRecord(record: LogDBEntity) {
        val api = ControllerApi().getLoggingApi()
        api.report(record.asDomainModel()).enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}
        })
    }

    /*
     * insert log record
     */
    private fun insert(tag: String, message: String) {
        val record = LogDBEntity(log_type = LogType.debug.name, message = "${tag} - ${message}")
        database.logDao.insert(record)
        sendRecord(record)
    }

    fun verbose(tag: String, message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            insert(tag, message)
        }
    }

    fun info(tag: String, message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            // insert(tag, message)
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