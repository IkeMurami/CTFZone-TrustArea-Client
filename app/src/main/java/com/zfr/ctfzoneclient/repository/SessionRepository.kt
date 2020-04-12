package com.zfr.ctfzoneclient.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.zfr.ctfzoneclient.core.ResponseErrorException
import com.zfr.ctfzoneclient.database.CTFZoneDatabase
import com.zfr.ctfzoneclient.database.data.asDomainModel
import com.zfr.ctfzoneclient.database.getDatabase
import com.zfr.ctfzoneclient.network.ControllerApi
import com.zfr.ctfzoneclient.network.data.RefreshTokenNetworkEntity
import com.zfr.ctfzoneclient.network.data.TokenNetworkEntity
import com.zfr.ctfzoneclient.network.data.UserNetworkEntity
import com.zfr.ctfzoneclient.network.data.asDatabaseEntity
import retrofit2.await

private const val TAG = "SessionRepository"


/**
 * Repository for saving sessions users
 */
class SessionRepository(private val database: CTFZoneDatabase, private val logger: LogRepository) {

    /*
     * [User Info] -> [Refresh Token]
     */
    suspend fun regUser(user: UserNetworkEntity): TokenNetworkEntity? {
        val authApi = ControllerApi().getAuthApi()

        authApi.register(user).execute().let {
            if(it.isSuccessful) {
                val refreshToken = it.body()?.data

                return refreshToken
            }
            else {
                throw ResponseErrorException("User already exist", it.errorBody()!!)
            }
        }
    }

    /*
     * [Refresh Token] -> [Session Token]
     */
    suspend fun getSession(token: TokenNetworkEntity): TokenNetworkEntity? {
        val authApi = ControllerApi().getAuthApi()
        authApi.session(RefreshTokenNetworkEntity(refresh_token = token.token)).execute().let {
            if (it.isSuccessful) {
                val sessionToken = it.body()?.data

                return sessionToken
            }
            else {
                throw ResponseErrorException("Invalid token", it.errorBody()!!)
            }
        }
    }

    /*
     * [Token, User] -> Database
     */
    suspend fun cacheToken(token: TokenNetworkEntity?, user: UserNetworkEntity?) {
        if (token != null && user != null) {
            logger.info(TAG, "Cache token ${token} for user ${user}")
            database.tokenDao.insertSession(token.asDatabaseEntity(user))
        } else {
            logger.info(TAG, "Token (${token}) or user (${user}) is null")
        }
    }

    /*
     * [Token] -> [Username]
     */
    suspend fun userByToken(token: TokenNetworkEntity): String? {
        logger.info(TAG, "Get user by token: ${token}")
        val tokenRec = database.tokenDao.userByToken(token.token)

        logger.info(TAG, "Found session ${token} for user ${tokenRec?.username}")


        return tokenRec?.username
    }

}


private lateinit var INSTANCE: SessionRepository


fun getSessionRepo(context: Context): SessionRepository {
    synchronized(SessionRepository::class.java) {

        if (!::INSTANCE.isInitialized) {
            INSTANCE = SessionRepository(getDatabase(context), getLogger(context))
        }
    }

    return INSTANCE
}