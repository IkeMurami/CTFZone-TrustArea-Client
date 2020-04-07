package com.zfr.ctfzoneclient.service.view

import android.app.IntentService
import android.app.PendingIntent
import android.content.Intent
import android.content.Context
import com.zfr.ctfzoneclient.PACKAGE_ID
import com.zfr.ctfzoneclient.network.data.TaskNetworkEntity
import com.zfr.ctfzoneclient.network.data.TokenNetworkEntity
import com.zfr.ctfzoneclient.service.data.asTaskNetworkEntity
import com.zfr.ctfzoneclient.service.data.asTokenNetworkEntity
import com.zfr.ctfzoneclient.service.data.getPendingIntent

// TODO: Rename actions, choose action names that describe tasks that this
// IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
private const val ACTION_CREATE = "${PACKAGE_ID}.action.TASK_CREATE"
private const val ACTION_GET    = "${PACKAGE_ID}.action.TASK_GET"
private const val ACTION_ALL    = "${PACKAGE_ID}.action.TASK_ALL"
private const val ACTION_UPDATE = "${PACKAGE_ID}.action.TASK_UPDATE"


/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
class TaskService : IntentService("TaskService") {

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_CREATE -> {
                val token = intent.asTokenNetworkEntity()
                val task = intent.asTaskNetworkEntity()
                val pendingIntent = intent.getPendingIntent()

                handleActionCreateTask(token, task, pendingIntent)
            }
            ACTION_GET -> {
                // handleActionBaz(param1, param2)
            }
            ACTION_ALL -> {
                // handleActionFoo(param1, param2)
            }
            ACTION_UPDATE -> {
                // handleActionFoo(param1, param2)
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionCreateTask(token: TokenNetworkEntity, task: TaskNetworkEntity, pendingIntent: PendingIntent?) {



    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionBaz(param1: String, param2: String) {
        TODO("Handle action Baz")
    }

    companion object {
        /**
         * Starts this service to perform action Foo with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        // TODO: Customize helper method
        @JvmStatic
        fun startActionFoo(context: Context, param1: String, param2: String) {
            val intent = Intent(context, TaskService::class.java).apply {
                action = ACTION_CREATE
                // putExtra(EXTRA_PARAM1, param1)
                // putExtra(EXTRA_PARAM2, param2)
            }
            context.startService(intent)
        }

        /**
         * Starts this service to perform action Baz with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        // TODO: Customize helper method
        @JvmStatic
        fun startActionBaz(context: Context, param1: String, param2: String) {
            val intent = Intent(context, TaskService::class.java).apply {
                action = ACTION_CREATE
                // putExtra(EXTRA_PARAM1, param1)
                // putExtra(EXTRA_PARAM2, param2)
            }
            context.startService(intent)
        }
    }
}
