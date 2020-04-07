package com.zfr.ctfzoneclient.network.data

import com.google.gson.annotations.SerializedName
import com.zfr.ctfzoneclient.database.data.UserDBEntity



fun List<UserNetworkEntity>.asDatabaseEntity(): List<UserDBEntity> {
    return map {
        UserDBEntity(
            user_id = it.user_id!!,
            user_name = it.username!!,
            first_name = it.first_name!!,
            last_name = it.last_name!!
        )
    }
}



fun UserNetworkEntity.asDatabaseEntity(): UserDBEntity {
    return UserDBEntity(
        user_id = this.user_id!!,
        user_name = this.username!!,
        first_name = this.first_name!!,
        last_name = this.last_name!!
    )
}

