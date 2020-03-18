package com.zfr.ctfzoneclient.database.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zfr.ctfzoneclient.network.data.OrderNetworkEntity

/**
 * OrderDBEntity represents a order entity in the database
 */
@Entity
data class OrderDBEntity constructor(
    val owner_id: String,
    @PrimaryKey
    val order_id: String,
    val order_name: String,
    val description: String,
    val thumb_url: String
)

/**
 * Extension: Map List<OrderDBEntity> to domain entities (to Network Entity)
 */
fun List<OrderDBEntity>.asDomainModel(): List<OrderNetworkEntity> {
    return map {
        OrderNetworkEntity(
            owner_id = it.owner_id,
            order_id = it.order_id,
            order_name = it.order_name,
            description = it.description,
            thumb_url = it.thumb_url
        )
    }
}