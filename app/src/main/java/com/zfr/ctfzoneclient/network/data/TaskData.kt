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


fun List<TaskNetworkEntity>.asDatabaseEntity(user: UserNetworkEntity): List<TaskDBEntity> {
    return map {
        it.asDatabaseEntity(user)
    }
}

