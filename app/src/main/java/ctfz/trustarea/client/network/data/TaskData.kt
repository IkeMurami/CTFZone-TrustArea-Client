package ctfz.trustarea.client.network.data

import ctfz.trustarea.client.database.data.TaskDBEntity


fun TaskNetworkEntity.asDatabaseEntity(user: UserNetworkEntity): TaskDBEntity {
    return TaskDBEntity(
        username = user.username!!,
        task_id = this.task_id!!,
        description = this.description,
        challenge = this.challenge,
        reward = this.reward
    )
}


fun TaskNetworkEntityResponse.asTaskNetworkEntity(): TaskNetworkEntity {
    return TaskNetworkEntity(
        task_id = ID,
        reward = Reward,
        challenge = Challenge,
        description = Description
    )
}


fun List<TaskNetworkEntity>.asDatabaseEntity(user: UserNetworkEntity): List<TaskDBEntity> {
    return map {
        it.asDatabaseEntity(user)
    }
}

fun List<TaskNetworkEntityResponse>.asTaskNetworkEntity(): List<TaskNetworkEntity> {
    return map {
        it.asTaskNetworkEntity()
    }
}

