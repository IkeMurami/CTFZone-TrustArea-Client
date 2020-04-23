package ctfz.trustarea.client.network.data

import ctfz.trustarea.client.database.data.TokenDBEntity

enum class TokenType(token_type: String) {
    bearer("bearer"),
    refresh("refresh"),
    access("access"),
    session("session")
}


fun TokenNetworkEntity.asDatabaseEntity(user: UserNetworkEntity): TokenDBEntity {

    return TokenDBEntity(
        username = user.username!!,
        token = this.token ?: "empty",
        token_type = this.token_type.name,
        expired = this.expired
    )

}