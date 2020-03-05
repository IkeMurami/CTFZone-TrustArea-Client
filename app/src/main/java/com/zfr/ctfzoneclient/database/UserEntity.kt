package com.zfr.ctfzoneclient.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zfr.ctfzoneclient.network.data.UserNetworkEntity

/**
 * UserDBEntity represents a user entity in the database
 */
@Entity
data class UserDBEntity constructor(
    @PrimaryKey
    val user_id: String,
    val user_name: String,
    val profile: String,
    val photo_url: String
)


/**
 * Extension: Map List<UserDBEntity> to domain entities (to Network Entity)
 */
fun List<UserDBEntity>.asDomainModel(): List<UserNetworkEntity> {
    return map {
        UserNetworkEntity(
            login = it.user_name,
            user_id = it.user_id
        )
    }
}

