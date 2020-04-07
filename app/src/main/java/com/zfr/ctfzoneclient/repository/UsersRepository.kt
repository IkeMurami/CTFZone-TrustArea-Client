package com.zfr.ctfzoneclient.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.zfr.ctfzoneclient.database.CTFZoneDatabase
import com.zfr.ctfzoneclient.database.data.asDomainModel
import com.zfr.ctfzoneclient.database.getDatabase
import com.zfr.ctfzoneclient.network.ControllerApi
import com.zfr.ctfzoneclient.network.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.await


private const val TAG = "UsersRepository"

/*
* Repository for fetching users from the network and storing them on disk
*/
class UsersRepository(private val database: CTFZoneDatabase, private val sessionRepository: SessionRepository, private val logger: LogRepository) {

    /*
     * [Username] -> [User] + update
     */
    suspend fun updateUserInfo(username: String): Response<ResponseData>? {
        logger.info(TAG, "Update info for user ${username}")

        val user = ControllerApi().getUserApi().user(username).await()
        when(user.data) {
            is UserNetworkEntity -> {
                database.userDao.insertUser(user.data?.asDatabaseEntity()!!)
                logger.info(TAG, "Updated info: ${user.data}")
            }
            is OtherNetworkEntity -> {
                logger.info(TAG, "Request failure. message: ${user.message}")
            }
        }

        return user
    }

    /*
     * [Username] -> [User]
     */
    suspend fun userInfo(username: String): Response<ResponseData>? {
        // get local
        logger.info(TAG, "Get user info by username ${username}")

        val user = database.userDao.getUser(username)

        if (user == null) {
            logger.info(TAG, "User not found in local database")
            return updateUserInfo(username)
        }
        else {
            logger.info(TAG, "User found: ${user}")
            return Response(data = user.asDomainModel())
        }
    }

    /*
     * [Token] -> [User]
     */
    suspend fun userInfo(token: TokenNetworkEntity): Response<ResponseData>? {
        logger.info(TAG, "Get user info by token: ${token}")
        val username = sessionRepository.userByToken(token)

        if (username.isNullOrBlank()) {
            logger.info(TAG, "Token invalid: ${token}")
            return Response(message = "Token invalid")
        }
        else {
            return userInfo(username)
        }

    }
}


private lateinit var INSTANCE: UsersRepository


fun getUserRepository(context: Context): UsersRepository {

    synchronized(UsersRepository::class.java) {

        if (!::INSTANCE.isInitialized) {
            val logger = getLogger(context)
            logger.info(TAG, "Initialized")

            INSTANCE = UsersRepository(getDatabase(context), getSessionRepo(context), getLogger(context))
        }

    }

    return INSTANCE
}