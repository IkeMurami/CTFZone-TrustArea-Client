package com.zfr.ctfzoneclient.service.data

import android.app.PendingIntent
import android.content.Intent
import com.zfr.ctfzoneclient.core.Responder

private const val EXTRA_PI = "PENDING_INTENT"


fun Intent.getPendingIntent(): PendingIntent? {
    return this.getParcelableExtra(EXTRA_PI)
}


fun Intent.putPendingIntent(pendingIntent: PendingIntent) {
    this.apply {
        putExtra(EXTRA_PI, pendingIntent)
    }
}


fun Intent.callback(): String? {
    return this.getStringExtra(Responder.EXTRA_CALLBACK)
}