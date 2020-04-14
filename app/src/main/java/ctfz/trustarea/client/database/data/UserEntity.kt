package ctfz.trustarea.client.database.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import ctfz.trustarea.client.network.data.UserNetworkEntity

/**
 * UserDBEntity represents a user entity in the database
 */
@Entity(tableName = UserDBEntity.TABLE_NAME)
data class UserDBEntity constructor(
    @PrimaryKey
    val user_id: String,
    val user_name: String,
    val first_name: String,
    val last_name: String
){
    companion object {
        const val TABLE_NAME = "users"
    }
}


/**
 * Extension: Map List<UserDBEntity> to domain entities (to Network Entity)
 */
fun List<UserDBEntity>.asDomainModel(): List<UserNetworkEntity> {
    return map {
        UserNetworkEntity(
            username = it.user_name,
            user_id = it.user_id,
            first_name = it.first_name,
            last_name = it.last_name
        )
    }
}

fun UserDBEntity.asDomainModel(): UserNetworkEntity {
    return UserNetworkEntity(
        username = this.user_name,
        user_id = this.user_id,
        first_name = this.first_name,
        last_name = this.last_name
    )
}

