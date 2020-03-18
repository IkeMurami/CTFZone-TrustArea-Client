package com.zfr.ctfzoneclient.database.tables

import androidx.lifecycle.LiveData
import androidx.room.Dao
import com.zfr.ctfzoneclient.database.data.SessionDBEntity

@Dao
interface SessionDao {

    fun getSession(username: String, host: String): LiveData<SessionDBEntity>
}