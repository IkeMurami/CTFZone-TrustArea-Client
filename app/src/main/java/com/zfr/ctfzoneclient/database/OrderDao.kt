package com.zfr.ctfzoneclient.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.*

@Dao
interface OrderDao {

    @Query("SELECT * FROM OrderDBEntity WHERE order_id LIKE :order_id")
    fun getOrder(order_id: String): LiveData<OrderDBEntity>

    @Query("SELECT * FROM OrderDBEntity")
    fun getOrders(): LiveData<List<OrderDBEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrder(order: OrderDBEntity): LiveData<OrderDBEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrders(orders: List<OrderDBEntity>): LiveData<List<OrderDBEntity>>

    @Update
    fun updateOrder(order: OrderDBEntity): LiveData<OrderDBEntity>

    @Delete
    fun deleteOrder(order: OrderDBEntity)

}

