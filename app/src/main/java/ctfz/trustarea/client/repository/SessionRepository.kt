package ctfz.trustarea.client.repository

import android.content.Context
import ctfz.trustarea.client.core.NetworkException
import ctfz.trustarea.client.core.ResponseErrorException
import ctfz.trustarea.client.database.CTFZoneDatabase
import ctfz.trustarea.client.database.data.asDomainModel
import ctfz.trustarea.client.database.getDatabase
import ctfz.trustarea.client.network.ControllerApi
import ctfz.trustarea.client.network.data.RefreshTokenNetworkEntity
import ctfz.trustarea.client.network.data.TokenNetworkEntity
import ctfz.trustarea.client.network.data.UserNetworkEntity
import ctfz.trustarea.client.network.data.asDatabaseEntity
import kotlin.Exception

private const val TAG = "SessionRepository"


/**
 * Repository for saving sessions users
 */
class SessionRepository(private val database: CTFZoneDatabase, private val logger: LogRepository) {

    /*
     * [User Info] -> [Refresh Token]
     */
    suspend fun regUser(user: UserNetworkEntity): TokenNetworkEntity? {
        user.username ?: throw Exception("Username is null")
        val authApi = ControllerApi().getAuthApi()

        authApi.register(user).execute().let {
            if(it.isSuccessful) {
                val refreshToken = it.body()?.data

                return refreshToken
            }
            else {
                if (it.code() >= 500)
                    throw NetworkException("Network Exception", it.errorBody()!!)
                throw ResponseErrorException("User already exist", it.errorBody()!!)
            }
        }
    }

    /*
     * [Refresh Token] -> [Session Token]
     */
    suspend fun getSession(token: TokenNetworkEntity?): TokenNetworkEntity? {
        token ?: throw Exception("Token is null")
        token.token ?: throw Exception("Token is null")

        val authApi = ControllerApi().getAuthApi()
        authApi.session(RefreshTokenNetworkEntity(refresh_token = token.token)).execute().let {
            if (it.isSuccessful) {
                val sessionToken = it.body()?.data

                return sessionToken
            }
            else {
                if (it.code() >= 500)
                    throw NetworkException("Network Exception", it.errorBody()!!)
                throw ResponseErrorException("Invalid token", it.errorBody()!!)
            }
        }
    }

    /*
     * [Token, User] -> Database
     */
    suspend fun cacheToken(token: TokenNetworkEntity?, user: UserNetworkEntity?) {
        token ?: throw Exception("Token is null")
        token.token ?: throw Exception("Token is null")
        user ?: throw Exception("User is null")
        user.username ?: throw Exception("Username is null")

        logger.info(TAG, "Cache token ${token} for user ${user}")
        database.tokenDao.insertSession(token.asDatabaseEntity(user))
    }

    /*
     * [Token] -> [Username]
     */
    suspend fun userByToken(token: TokenNetworkEntity): String? {
        token.token ?: throw Exception("Token is null")

        logger.info(TAG, "Get user by token: ${token}")
        val tokenRec = database.tokenDao.userByToken(token.token)

        logger.info(TAG, "Found session ${token} for user ${tokenRec?.username}")


        return tokenRec?.username
    }

    suspend fun sessions(user: UserNetworkEntity): List<TokenNetworkEntity> {
        logger.info(TAG, "Backup sessions for user ${user}")
        val tokens = database.tokenDao.userSessions(user.username?:return emptyList())
        return tokens.asDomainModel()
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