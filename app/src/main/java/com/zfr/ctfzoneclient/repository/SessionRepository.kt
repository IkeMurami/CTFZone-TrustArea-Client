package com.zfr.ctfzoneclient.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.zfr.ctfzoneclient.database.CTFZoneDatabase
import com.zfr.ctfzoneclient.database.data.asDomainModel
import com.zfr.ctfzoneclient.database.getDatabase
import com.zfr.ctfzoneclient.network.data.TokenNetworkEntity
import com.zfr.ctfzoneclient.network.data.UserNetworkEntity
import com.zfr.ctfzoneclient.network.data.asDatabaseEntity

private const val TAG = "SessionRepository"

/**
 * Repository for saving sessions users
 */
class SessionRepository(private val database: CTFZoneDatabase, private val logger: LogRepository) {

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