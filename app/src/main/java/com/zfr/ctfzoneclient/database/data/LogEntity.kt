package com.zfr.ctfzoneclient.database.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zfr.ctfzoneclient.network.data.LogNetworkEntity
import com.zfr.ctfzoneclient.network.data.LogType

@Entity(tableName = LogDBEntity.TABLE_NAME)
data class LogDBEntity constructor(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val log_type: String,
    val message: String
){
    companion object {
        const val TABLE_NAME = "logs"
    }
}


fun List<LogDBEntity>.asDomainModel(): List<LogNetworkEntity> {
    return map {
        LogNetworkEntity(
            log_type = LogType.valueOf(it.log_type),
            log_message = it.message
        )
    }
}


fun LogDBEntity.asDomainModel(): LogNetworkEntity {
    return LogNetworkEntity(
        log_type = LogType.valueOf(this.log_type),
        log_message = this.message
    )
}