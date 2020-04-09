package com.zfr.ctfzoneclient.database.tables

import androidx.room.Dao
import androidx.room.*
import com.zfr.ctfzoneclient.database.data.TaskDBEntity


@Dao
interface TaskDao {

    @Query("SELECT * FROM TaskDBEntity WHERE owner_id LIKE :user_id AND task_id LIKE :task_id")
    fun task(user_id: String, task_id: Int): TaskDBEntity?

    @Query("SELECT * FROM TaskDBEntity WHERE owner_id LIKE :user_id")
    fun tasks(user_id: String): List<TaskDBEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTask(task: TaskDBEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTasks(tasks: List<TaskDBEntity>)

    @Update
    fun update(task: TaskDBEntity)

    @Delete
    fun deleteTask(task: TaskDBEntity)

}