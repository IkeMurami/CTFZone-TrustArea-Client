package com.zfr.ctfzoneclient.database.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.zfr.ctfzoneclient.network.data.TaskNetworkEntity


@Entity(foreignKeys = [
    ForeignKey(
        entity = UserDBEntity::class,
        parentColumns = ["user_id"],
        childColumns = ["owner_id"])
])
data class TaskDBEntity constructor(

    val owner_id: String,
    @PrimaryKey val task_id: Int,
    val description: String,
    val challenge: String,
    val reward: String

)


fun List<TaskDBEntity>.asDomainModel(): List<TaskNetworkEntity> {
    return map {
        TaskNetworkEntity(
            task_id = it.task_id,
            description = it.description,
            challenge = it.challenge,
            reward = it.reward
        )
    }
}


fun TaskDBEntity.asDomainModel(): TaskNetworkEntity {
    return TaskNetworkEntity(
        task_id = this.task_id,
        description = this.description,
        challenge = this.challenge,
        reward = this.reward
    )
}