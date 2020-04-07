package com.zfr.ctfzoneclient.database.tables

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zfr.ctfzoneclient.database.data.LogDBEntity

@Dao
interface LogDao {

    @Query("SELECT * FROM ${LogDBEntity.TABLE_NAME}")
    fun getLogQueries(): List<LogDBEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(message: LogDBEntity)

    @Query("DELETE FROM ${LogDBEntity.TABLE_NAME}")
    fun deleteLogQueries()

}