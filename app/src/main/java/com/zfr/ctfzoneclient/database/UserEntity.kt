package com.zfr.ctfzoneclient.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zfr.ctfzoneclient.network.data.User

/**
 * DatabaseUser represents a user entity in the database
 */
@Entity
data class DatabaseUser constructor(
    @PrimaryKey
    val user_id: String,
    val user_name: String,
    val proofile: String,
    val photo_url: String
)


/**
 * Extension: Map List<DatabaseUser> to domain entities (to Network Entity)
 */
fun List<DatabaseUser>.asDomainModel(): List<User> {
    return map {
        User(
            login = it.user_name,
            user_id = it.user_id
        )
    }
}

