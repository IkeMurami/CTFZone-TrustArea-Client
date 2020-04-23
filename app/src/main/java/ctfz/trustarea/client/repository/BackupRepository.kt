package ctfz.trustarea.client.repository

import android.content.Context
import androidx.core.content.FileProvider
import ctfz.trustarea.client.core.IMS
import ctfz.trustarea.client.network.data.TaskNetworkEntity
import ctfz.trustarea.client.network.data.TokenNetworkEntity
import ctfz.trustarea.client.network.data.UserNetworkEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.Exception
import java.util.logging.Logger


class BackupRepository(private val context: Context, private val logger: LogRepository) {

    private val TAG = "BackupRepository"
    private val backupPath: File = context.filesDir

    suspend fun save(token: TokenNetworkEntity?): Boolean {
        token ?: throw Exception("Token is null")
        token.token ?: throw Exception("Token is null")

        val users = getUserRepository(context)
        val user = users.userInfo(token) ?: throw Exception("User with token ${token.token} not found")

        logger.info(TAG, "Save backup for user with token ${token.token}")

        val backup = File(backupPath, user.username ?: throw Exception("Username is null"))

        return if (backup.exists()) { true } else {
            val tasks = getTaskRepository(context).getAllTasks(token, user)
            val sessions = getSessionRepo(context).sessions(user)

            writer(backup, user, sessions, tasks)
        }

    }

    suspend fun small_save(token: TokenNetworkEntity?): Boolean {
        token ?: throw Exception("Token is null")
        token.token ?: throw Exception("Token is null")

        val users = getUserRepository(context)
        val user = users.userInfo(token) ?: throw Exception("User with token ${token.token} not found")

        logger.info(TAG, "Save backup for user with token ${token.token}")

        val backup = File(backupPath, user.username ?: throw Exception("Username is null"))

        return if (backup.exists()) { true } else {
            val tasks = getTaskRepository(context).getAllTasks(token, user)

            small_writer(backup, tasks)
        }

    }

    suspend fun send(token: TokenNetworkEntity?): ByteArray? {
        token ?: throw Exception("Token is null")
        token.token ?: throw Exception("Token is null")

        val users = getUserRepository(context)
        val user = users.userInfo(token)?: throw Exception("User with token ${token.token} not found")

        val backup = File(backupPath, user.username ?: throw Exception("User with token ${token.token} not found (username is null)"))
        val data = backup.readBytes()

        return data
    }



    suspend fun small_writer(file: File, tasks: List<TaskNetworkEntity>): Boolean {

        return withContext(Dispatchers.IO) {
            file.bufferedWriter().apply {

                write("Tasks"); newLine()
                tasks.forEach {
                    write(it.toString()); newLine()
                }
                close()
            }

            return@withContext true
        }

    }

    suspend fun writer(file: File, user: UserNetworkEntity, sessions: List<TokenNetworkEntity>, tasks: List<TaskNetworkEntity>): Boolean {

        return withContext(Dispatchers.IO) {
            file.bufferedWriter().apply {
                write("User"); newLine()
                write(user.toString()); newLine()

                write("Sessions"); newLine()
                sessions.forEach {
                    write(it.toString()); newLine()
                }

                write("Tasks"); newLine()
                tasks.forEach {
                    write(it.toString()); newLine()
                }
                close()
            }

            return@withContext true
        }

    }
}