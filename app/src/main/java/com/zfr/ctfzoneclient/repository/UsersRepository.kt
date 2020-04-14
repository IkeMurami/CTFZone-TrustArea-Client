package com.zfr.ctfzoneclient.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.zfr.ctfzoneclient.core.InvalidTokenException
import com.zfr.ctfzoneclient.core.ResponseErrorException
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
    suspend fun updateUserInfo(username: String): UserNetworkEntity? {

        logger.info(TAG, "Update info for user ${username}")

        val userResp = ControllerApi().getUserApi().user(username).execute()
        if (userResp.isSuccessful) {
            val user = userResp.body()?.data?.asUserNetworkEntity()
            database.userDao.insertUser(user?.asDatabaseEntity()!!)
            logger.info(TAG, "Updated info: ${user}")

            return user
        }
        else {
            throw ResponseErrorException("User already exist", userResp.errorBody()!!)
        }
    }

    /*
     * [Session Token] -> [User<Me>]
     */
    suspend fun updateProfile(token: TokenNetworkEntity): UserNetworkEntity? {
        logger.info(TAG, "Update info for user with token ${token.token}")

        val meResp = ControllerApi().getUserApi().profile(token.token).execute()

        if (meResp.isSuccessful) {
            val test = meResp.body()?.data
            val user = meResp.body()?.data?.asUserNetworkEntity()

            logger.info(TAG, "Updated info: ${user}")

            database.userDao.insertUser(user?.asDatabaseEntity()!!)
            database.tokenDao.insertSession(token.asDatabaseEntity(user))

            return user
        }
        else {
            throw ResponseErrorException("Invalid token", meResp.errorBody()!!)
        }
    }

    /*
     * [Username] -> [User]
     */
    suspend fun userInfo(username: String): UserNetworkEntity? {
        // get local
        logger.info(TAG, "Get user info by username ${username}")

        val user = database.userDao.getUser(username)

        if (user == null) {
            logger.info(TAG, "User not found in local database")
            return updateUserInfo(username)
        }
        else {
            logger.info(TAG, "User found: ${user}")
            return user.asDomainModel()
        }
    }

    /*
     * [Token] -> [User]
     */
    suspend fun userInfo(token: TokenNetworkEntity): UserNetworkEntity? {
        logger.info(TAG, "Get user info by token: ${token}")
        val user = updateProfile(token)

        if (user == null) {
            logger.info(TAG, "User not found by token ${token}")
            throw InvalidTokenException("Invalid token")
        }
        else {
            return user
        }
    }

    suspend fun usersList(): List<UserNetworkEntity> {
        logger.info(TAG, "Get list users")
        val usersResp = ControllerApi().getUserApi().users().execute()
        if (usersResp.isSuccessful) {
            val users = usersResp.body()?.data?.asUserNetworkEntity()

            logger.info(TAG, "Fetch ${users?.size} users")

            return users ?: return emptyList()
        }
        else {
            throw ResponseErrorException("Invalid token", usersResp.errorBody()!!)
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