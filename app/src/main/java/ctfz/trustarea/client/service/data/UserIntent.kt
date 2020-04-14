package ctfz.trustarea.client.service.data

import android.content.Intent
import ctfz.trustarea.client.network.data.UserNetworkEntity


private const val EXTRA_USER_NAME = "USER_NAME"
private const val EXTRA_FIRST_NAME = "FIRST_NAME"
private const val EXTRA_LAST_NAME = "LAST_NAME"
private const val EXTRA_USERS = "USERS"
private const val EXTRA_USER_ID = "USER_ID"

fun Intent.asUserNetworkEntity(): UserNetworkEntity {
    return UserNetworkEntity(
        username = this.getStringExtra(EXTRA_USER_NAME),
        first_name = this.getStringExtra(EXTRA_FIRST_NAME),
        last_name = this.getStringExtra(EXTRA_LAST_NAME),
        user_id = this.getStringExtra(EXTRA_USER_ID)
    )
}


fun UserNetworkEntity.asIntent(intent: Intent): Intent {
    intent.putExtra(EXTRA_USER_NAME, this.username)
    intent.putExtra(EXTRA_FIRST_NAME, this.first_name)
    intent.putExtra(EXTRA_LAST_NAME, this.last_name)
    intent.putExtra(EXTRA_USER_ID, this.user_id)

    return intent
}

fun List<UserNetworkEntity>.asIntent(intent: Intent): Intent {
    intent.putExtra(EXTRA_USERS, ArrayList(this))

    return intent
}
