package com.zfr.ctfzoneclient.service.view

import android.app.IntentService
import android.app.PendingIntent
import android.content.Intent
import android.content.Context

private const val PACKAGE_ID = "com.zfr.ctfzoneclient"

// TODO: Rename actions, choose action names that describe tasks that this
// IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
private const val ACTION_ORDER_GET = "${PACKAGE_ID}.action.ORDER.GET"
private const val ACTION_ORDER_GET_ALL = "${PACKAGE_ID}.action.ORDER.ALL"
private const val ACTION_ORDER_EDIT = "${PACKAGE_ID}.action.ORDER.EDIT"
private const val ACTION_ORDER_CREATE = "${PACKAGE_ID}.action.ORDER.CREATE"
private const val ACTION_ORDER_DELETE = "${PACKAGE_ID}.action.ORDER.DELETE"

// TODO: Rename parameters
private const val EXTRA_PENDING_INTENT = "${PACKAGE_ID}.extra.PENDING_INTENT"
private const val EXTRA_PARAM1 = "${PACKAGE_ID}.extra.PARAM1"
private const val EXTRA_PARAM2 = "${PACKAGE_ID}.extra.PARAM2"

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
class OrderService : IntentService("OrderService") {

    override fun onHandleIntent(intent: Intent?) {

        // if pending intent exist - need return value
        val pending_intent = intent?.getParcelableExtra<PendingIntent>(EXTRA_PENDING_INTENT)

        when (intent?.action) {
            ACTION_ORDER_GET -> {
                val param1 = intent.getStringExtra(EXTRA_PARAM1)
                val param2 = intent.getStringExtra(EXTRA_PARAM2)
                handleActionOrderGet(param1, pending_intent)
            }
            ACTION_ORDER_GET_ALL -> {
                val param1 = intent.getStringExtra(EXTRA_PARAM1)
                val param2 = intent.getStringExtra(EXTRA_PARAM2)
                handleActionBaz(param1, param2)
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionOrderGet(user_id: String, pending_intent: PendingIntent?) {
        TODO("Handle action Foo")
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
            val intent = Intent(context, OrderService::class.java).apply {
                action = ACTION_ORDER_GET
                putExtra(EXTRA_PARAM1, param1)
                putExtra(EXTRA_PARAM2, param2)
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
            val intent = Intent(context, OrderService::class.java).apply {
                action = ACTION_ORDER_GET_ALL
                putExtra(EXTRA_PARAM1, param1)
                putExtra(EXTRA_PARAM2, param2)
            }
            context.startService(intent)
        }
    }
}
