package ctfz.trustarea.client.database.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import ctfz.trustarea.client.network.data.TaskNetworkEntity


@Entity
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