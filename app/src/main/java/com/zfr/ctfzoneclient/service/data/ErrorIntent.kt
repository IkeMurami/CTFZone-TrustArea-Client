package com.zfr.ctfzoneclient.service.data

import android.content.Intent

private const val EXTRA_MESSAGE = "Message"
private const val EXTRA_ERRORS = "Errors"

fun errorIntent(message: String, errors: List<String>): Intent {
    val intent = Intent().apply {
        putExtra(EXTRA_MESSAGE, message)
        putStringArrayListExtra(EXTRA_ERRORS, ArrayList(errors))
    }

    return intent
}