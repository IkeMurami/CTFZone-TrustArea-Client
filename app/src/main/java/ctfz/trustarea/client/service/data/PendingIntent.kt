package ctfz.trustarea.client.service.data

import android.app.PendingIntent
import android.content.Intent
import ctfz.trustarea.client.core.Responder

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