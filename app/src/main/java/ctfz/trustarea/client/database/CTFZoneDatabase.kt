package ctfz.trustarea.client.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ctfz.trustarea.client.database.data.*
import ctfz.trustarea.client.database.tables.*

@Database(
    entities = [
        UserDBEntity::class,
        LogDBEntity::class,
        TaskDBEntity::class,
        TokenDBEntity::class
    ],
    version = 1
)
abstract class CTFZoneDatabase: RoomDatabase() {
    abstract val userDao: UserDao
    abstract val logDao: LogDao
    abstract val taskDao: TaskDao
    abstract val tokenDao: SessionDao
}

private val DATABASE_NAME = "ctfzonedb"
private lateinit var INSTANCE: CTFZoneDatabase


fun getDatabase(context: Context): CTFZoneDatabase {

    synchronized(CTFZoneDatabase::class.java) {

        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                CTFZoneDatabase::class.java,
                DATABASE_NAME).build()
        }

    }

    return INSTANCE

}