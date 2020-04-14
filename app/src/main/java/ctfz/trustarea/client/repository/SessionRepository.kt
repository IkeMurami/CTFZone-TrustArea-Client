package ctfz.trustarea.client.repository

import android.content.Context
import ctfz.trustarea.client.core.ResponseErrorException
import ctfz.trustarea.client.database.CTFZoneDatabase
import ctfz.trustarea.client.database.getDatabase
import ctfz.trustarea.client.network.ControllerApi
import ctfz.trustarea.client.network.data.RefreshTokenNetworkEntity
import ctfz.trustarea.client.network.data.TokenNetworkEntity
import ctfz.trustarea.client.network.data.UserNetworkEntity
import ctfz.trustarea.client.network.data.asDatabaseEntity

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