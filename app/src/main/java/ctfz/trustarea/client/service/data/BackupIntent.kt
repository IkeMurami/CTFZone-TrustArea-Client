package ctfz.trustarea.client.service.data

import android.content.Intent


private const val EXTRA_DATA = "DATA"
private const val EXTRA_STATUS = "STATUS"


fun backupIntent(status: Boolean, data: ByteArray?): Intent {
    return Intent().apply {
        putExtra(EXTRA_STATUS, status)
        putExtra(EXTRA_DATA, data)
    }
}