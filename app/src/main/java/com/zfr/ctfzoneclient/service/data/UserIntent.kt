package com.zfr.ctfzoneclient.service.data

import android.content.Intent
import com.zfr.ctfzoneclient.PACKAGE_ID
import com.zfr.ctfzoneclient.network.data.UserNetworkEntity


private const val EXTRA_USER_NAME = "$PACKAGE_ID.extra.USER_NAME"
private const val EXTRA_FIRST_NAME = "$PACKAGE_ID.extra.FIRST_NAME"
private const val EXTRA_LAST_NAME = "$PACKAGE_ID.extra.LAST_NAME"

fun Intent.asUserNetworkEntity(): UserNetworkEntity {
    return UserNetworkEntity(
        username = this.getStringExtra(EXTRA_USER_NAME),
        firstName = this.getStringExtra(EXTRA_FIRST_NAME),
        lastName = this.getStringExtra(EXTRA_LAST_NAME)
    )
}


fun UserNetworkEntity.asIntent(intent: Intent): Intent {
    intent.putExtra(EXTRA_USER_NAME, this.username)
    intent.putExtra(EXTRA_FIRST_NAME, this.firstName)
    intent.putExtra(EXTRA_LAST_NAME, this.lastName)

    return intent
}
