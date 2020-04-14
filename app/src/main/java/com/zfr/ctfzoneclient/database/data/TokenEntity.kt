package com.zfr.ctfzoneclient.database.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.zfr.ctfzoneclient.network.data.TokenNetworkEntity
import com.zfr.ctfzoneclient.network.data.TokenType

/**
 * TokenDBEntity represents a session entity in the database
 * only for local user logic !!!
 */
@Entity
data class TokenDBEntity constructor(
    val token_type: String,
    @PrimaryKey
    val token: String,
    val expired: Long?,
    val username: String
)


/**
 * Extension: Map List<TokenDBEntity> to domain entities (to Network Entity)
 */
fun List<TokenDBEntity>.asDomainModel(): List<TokenNetworkEntity> {
    return map {
        TokenNetworkEntity(
            token_type = TokenType.valueOf(it.token_type),
            token = it.token,
            expired = it.expired
        )
    }
}

fun TokenDBEntity.asDomainModel(): TokenNetworkEntity {
    return TokenNetworkEntity(
        token_type = TokenType.valueOf(this.token_type),
        token = this.token,
        expired = this.expired
    )
}