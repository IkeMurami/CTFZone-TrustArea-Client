package com.zfr.ctfzoneclient.network.data

import com.google.gson.annotations.SerializedName
import com.zfr.ctfzoneclient.database.data.UserDBEntity



fun List<UserNetworkEntity>.asDatabaseEntity(): List<UserDBEntity> {
    return map {
        it.asDatabaseEntity()
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


fun UserNetworkEntityResponse.asUserNetworkEntity(): UserNetworkEntity {
    return UserNetworkEntity(username = Username, user_id = ID, first_name = FirstName, last_name = LastName)
}

fun List<UserNetworkEntityResponse>.asUserNetworkEntity(): List<UserNetworkEntity> {
    return map {
        it.asUserNetworkEntity()
    }
}
