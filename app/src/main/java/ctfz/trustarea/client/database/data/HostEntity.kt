package ctfz.trustarea.client.database.data

import androidx.room.Entity

/**
 * TokenDBEntity represents a session entity in the database
 * only for local user logic !!!
 */
@Entity
data class HostDBEntity constructor(
    val host: String,
    val port: Int
)