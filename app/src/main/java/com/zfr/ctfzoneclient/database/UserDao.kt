package com.zfr.ctfzoneclient.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDao  {
    @Query("select * from databaseuser")
    fun getUsers(): LiveData<List<DatabaseUser>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(users: List<DatabaseUser>)

}

@Database(entities = [DatabaseUser::class], version = 1)
abstract class UsersDatabase: RoomDatabase() {
    abstract val userDao: UserDao
}

private val DATABASE_NAME = "users"
private lateinit var INSTANCE: UsersDatabase

fun getDatabase(context: Context): UsersDatabase {

    synchronized(UsersDatabase::class.java) {

        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                UsersDatabase::class.java,
                DATABASE_NAME).build()
        }

    }

    return INSTANCE
}
