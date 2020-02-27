package com.zfr.ctfzoneclient.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.zfr.ctfzoneclient.database.UsersDatabase
import com.zfr.ctfzoneclient.database.asDomainModel
import com.zfr.ctfzoneclient.network.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
* Repository for fetching users from the network and storing them on disk
*/
class UsersRepository(private val database: UsersDatabase) {
    val users: LiveData<List<User>> = Transformations.map(database.userDao.getUsers()) {
        it.asDomainModel()
    }

    suspend fun refreshUsers() {
        withContext(Dispatchers.IO) {
            /*
            val playlist = DevByteNetwork.devbytes.getPlaylist().await()
            database.userDao.insertAll(playlist.asDatabaseModel())

             */
        }
    }
}