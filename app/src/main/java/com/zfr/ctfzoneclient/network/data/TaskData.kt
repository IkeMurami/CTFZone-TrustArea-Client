package com.zfr.ctfzoneclient.network.data

import com.google.gson.annotations.SerializedName
import com.zfr.ctfzoneclient.database.data.TaskDBEntity




fun TaskNetworkEntity.asDatabaseEntity(user: UserNetworkEntity): TaskDBEntity {
    return TaskDBEntity(
        owner_id = user.user_id!!,
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

