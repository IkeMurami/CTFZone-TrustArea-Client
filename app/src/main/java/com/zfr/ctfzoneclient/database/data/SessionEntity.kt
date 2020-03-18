package com.zfr.ctfzoneclient.database.data

import androidx.room.Entity
import com.zfr.ctfzoneclient.network.data.TokenNetworkEntity
import com.zfr.ctfzoneclient.network.data.TokenType

/**
 * SessionDBEntity represents a session entity in the database
 * only for local user logic !!!
 */
@Entity
data class SessionDBEntity constructor(
    val token_type: TokenType,
    val token: String,
    val expired: Long,
    val user: UserDBEntity,
    val target_host: HostDBEntity
)

/**
 * Extension: Map List<SessionDBEntity> to domain entities (to Network Entity)
 */
fun List<SessionDBEntity>.asDomainModel(): List<TokenNetworkEntity> {
    return map {
        TokenNetworkEntity(
            tokenType = it.token_type,
            token = it.token,
            expired = it.expired
        )
    }
}