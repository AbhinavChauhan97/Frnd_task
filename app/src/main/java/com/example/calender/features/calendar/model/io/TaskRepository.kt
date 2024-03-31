package com.example.calender.features.calendar.model.io

import com.example.calender.features.calendar.common.IOState
import com.example.calender.features.calendar.model.Task

interface TaskRepository {
    suspend fun addTask(task: Task): IOState<String>
    suspend fun getTasks(): IOState<List<Task>>
    suspend fun deleteTask(task: Task): IOState<String>
}
