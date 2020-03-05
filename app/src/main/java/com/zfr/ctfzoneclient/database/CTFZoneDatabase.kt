package com.zfr.ctfzoneclient.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        OrderDBEntity::class,
        UserDBEntity::class
    ],
    version = 1
)
abstract class CTFZoneDatabase: RoomDatabase() {
    abstract val orderDao: OrderDao
    abstract val userDao: UserDao
}

private val DATABASE_NAME = "ctfzonedb"
private lateinit var INSTANCE: CTFZoneDatabase


fun getDatabase(context: Context): CTFZoneDatabase {

    synchronized(CTFZoneDatabase::class.java) {

        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                CTFZoneDatabase::class.java,
                DATABASE_NAME).build()
        }

    }

    return INSTANCE

}