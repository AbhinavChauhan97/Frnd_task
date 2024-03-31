package com.example.calender.features.calendar.model.io

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.calender.features.calendar.model.Task

@Dao
interface TaskDao {

    @Insert
    suspend fun addTask(task: Task):Long

    @Query("SELECT * FROM tasks")
    suspend fun getTasks(): List<Task>

    @Delete
    suspend fun deleteTask(task: Task)
}