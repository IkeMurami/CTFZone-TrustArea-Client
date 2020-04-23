package ctfz.trustarea.client.service.data

import android.content.Intent
import ctfz.trustarea.client.network.data.TokenNetworkEntity
import ctfz.trustarea.client.network.data.TokenType

private const val EXTRA_TOKEN = "TOKEN"
private const val EXTRA_TOKEN_TYPE = "TOKEN_TYPE"


fun Intent.asTokenNetworkEntity(): TokenNetworkEntity {
    return TokenNetworkEntity(
        token_type = TokenType.valueOf(this.getStringExtra(EXTRA_TOKEN_TYPE) ?: "session"),
        token = this.getStringExtra(EXTRA_TOKEN) ?: "null"
    )
}


fun TokenNetworkEntity.asIntent(intent: Intent): Intent {
    intent.putExtra(EXTRA_TOKEN, this.token)
    intent.putExtra(EXTRA_TOKEN_TYPE, this.token_type.toString())

    return intent
}