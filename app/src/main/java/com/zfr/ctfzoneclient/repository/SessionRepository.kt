package com.zfr.ctfzoneclient.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.zfr.ctfzoneclient.database.CTFZoneDatabase
import com.zfr.ctfzoneclient.network.data.TokenNetworkEntity

/**
 * Repository for saving sessions users
 */
class SessionRepository(private val database: CTFZoneDatabase) {
    /*val sessions: LiveData<List<TokenNetworkEntity>> = Transformations.map(database.sessionDao.getSessions()) {
        it.asDomainModel()
    }*/
}