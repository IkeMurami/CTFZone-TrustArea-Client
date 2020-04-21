package ctfz.trustarea.client.database.tables

import androidx.room.Dao
import androidx.room.*
import ctfz.trustarea.client.database.data.TaskDBEntity


@Dao
interface TaskDao {

    @Query("SELECT * FROM TaskDBEntity WHERE username LIKE :username AND task_id LIKE :task_id")
    fun task(username: String, task_id: Int): TaskDBEntity?

    @Query("SELECT * FROM TaskDBEntity WHERE username LIKE :username")
    fun tasks(username: String): List<TaskDBEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTask(task: TaskDBEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTasks(tasks: List<TaskDBEntity>)

    @Update
    fun update(task: TaskDBEntity)

    @Delete
    fun deleteTask(task: TaskDBEntity)

}