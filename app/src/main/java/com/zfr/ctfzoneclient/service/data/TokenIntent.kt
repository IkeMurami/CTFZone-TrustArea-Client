package com.zfr.ctfzoneclient.service.data

import android.content.Intent
import com.zfr.ctfzoneclient.PACKAGE_ID
import com.zfr.ctfzoneclient.network.data.TokenNetworkEntity
import com.zfr.ctfzoneclient.network.data.TokenType

private const val EXTRA_TOKEN = "$PACKAGE_ID.extra.TOKEN"
private const val EXTRA_TOKEN_TYPE = "$PACKAGE_ID.extra.TOKEN_TYPE"


fun Intent.asTokenNetworkEntity(): TokenNetworkEntity {
    return TokenNetworkEntity(
        tokenType = TokenType.valueOf(this.getStringExtra(EXTRA_TOKEN_TYPE)!!),
        token = this.getStringExtra(EXTRA_TOKEN)!!
    )
}


fun TokenNetworkEntity.asIntent(intent: Intent): Intent {
    intent.putExtra(EXTRA_TOKEN, this.token)
    intent.putExtra(EXTRA_TOKEN_TYPE, this.tokenType)

    return intent
}