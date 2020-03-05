package com.zfr.ctfzoneclient.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface UserDao  {

    @Query("SELECT * FROM UserDBEntity WHERE user_id LIKE :user_id")
    fun getUser(user_id: String): LiveData<UserDBEntity>

    @Query("SELECT * FROM UserDBEntity")
    fun getUsers(): LiveData<List<UserDBEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsers(users: List<UserDBEntity>): LiveData<List<UserDBEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: UserDBEntity): LiveData<UserDBEntity>

    @Delete
    fun deleteUser(user: UserDBEntity)

}