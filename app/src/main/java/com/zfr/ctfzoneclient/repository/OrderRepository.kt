package com.zfr.ctfzoneclient.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.zfr.ctfzoneclient.database.CTFZoneDatabase
import com.zfr.ctfzoneclient.database.data.asDomainModel
import com.zfr.ctfzoneclient.network.data.OrderNetworkEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/**
 * Repository for fetching orders from the network and storing them on disk
 */
class OrderRepository(private val database: CTFZoneDatabase) {

    val orders: LiveData<List<OrderNetworkEntity>> = Transformations.map(database.orderDao.getOrders()) {
        it.asDomainModel()
    }

    suspend fun refreshOrders() {
        withContext(Dispatchers.IO) {
            /* ??? */
        }
    }

}