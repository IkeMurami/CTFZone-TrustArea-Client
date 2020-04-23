package ctfz.trustarea.client.repository

import android.content.Context
import ctfz.trustarea.client.core.InvalidTokenException
import ctfz.trustarea.client.core.NetworkException
import ctfz.trustarea.client.core.ResponseErrorException
import ctfz.trustarea.client.database.CTFZoneDatabase
import ctfz.trustarea.client.database.data.asDomainModel
import ctfz.trustarea.client.database.getDatabase
import ctfz.trustarea.client.network.ControllerApi
import ctfz.trustarea.client.network.data.*


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

            if (userResp.code() >= 500)
                throw NetworkException("Network Exception", userResp.errorBody()!!)
            throw ResponseErrorException("User already exist", userResp.errorBody()!!)
        }
    }

    /*
     * [Session Token] -> [User<Me>]
     */
    suspend fun updateProfile(token: TokenNetworkEntity?): UserNetworkEntity? {
        token ?: throw Exception("Token is null")
        token.token ?: throw Exception("Token is null")

        logger.info(TAG, "Update info for user with token ${token.token}")
        val meResp = ControllerApi().getUserApi().profile(token.token).execute()

        if (meResp.isSuccessful) {
            val user = meResp.body()?.data?.asUserNetworkEntity()

            logger.info(TAG, "Updated info: ${user}")

            database.userDao.insertUser(user?.asDatabaseEntity()!!)
            database.tokenDao.insertSession(token.asDatabaseEntity(user))

            return user
        }
        else {

            if (meResp.code() >= 500)
                throw NetworkException("Network Exception", meResp.errorBody()!!)
            throw ResponseErrorException("Invalid token", meResp.errorBody()!!)
        }

    }
    /*
     * [Username] -> [User]
     */
    suspend fun userInfo(username: String?): UserNetworkEntity? {
        logger.info(TAG, "Get user info by username ${username}")

        username ?: return null

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
    suspend fun userInfo(token: TokenNetworkEntity?): UserNetworkEntity? {
        token ?: throw Exception("Token is null")
        token.token ?: throw Exception("Token is null")

        logger.info(TAG, "Get user info by token: ${token}")
        val username = database.tokenDao.userByToken(token.token)?.username
        val user = userInfo(username) ?: run {
            return updateProfile(token)?: run {
                logger.info(TAG, "User not found by token ${token}")
                throw InvalidTokenException("Invalid token")
            }
        }

        return user
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

            if (usersResp.code() >= 500)
                throw NetworkException("Network Exception", usersResp.errorBody()!!)
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