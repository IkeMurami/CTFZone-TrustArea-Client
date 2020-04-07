package com.zfr.ctfzoneclient.database.tables

import androidx.lifecycle.LiveData
import androidx.room.*
import com.zfr.ctfzoneclient.database.data.UserDBEntity


@Dao
interface UserDao  {

    @Query("SELECT * FROM ${UserDBEntity.TABLE_NAME} WHERE user_id LIKE :user_id")
    fun getUser(user_id: String): UserDBEntity?

    @Query("SELECT * FROM ${UserDBEntity.TABLE_NAME}")
    fun getUsers(): List<UserDBEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsers(users: List<UserDBEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: UserDBEntity)

    @Delete
    fun deleteUser(user: UserDBEntity)

}