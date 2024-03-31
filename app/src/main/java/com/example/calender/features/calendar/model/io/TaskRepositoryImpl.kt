package com.example.calender.features.calendar.model.io

import android.content.Context
import com.example.calender.features.calendar.common.AppDatabase
import com.example.calender.features.calendar.common.IOState
import com.example.calender.features.calendar.common.MyApplication
import com.example.calender.features.calendar.model.Task
import com.example.calender.features.calendar.model.asValidated
import com.example.calender.features.calendar.model.toAddTaskPayload
import com.example.calender.features.calendar.model.toUITask
import com.example.calender.features.calendar.common.handleApi


class TaskRepositoryImpl(context: Context, private val apiService: TaskApiService) :
    TaskRepository {

    val database = AppDatabase.getDatabase(context)
    val taskDao = database.taskDao()

    override suspend fun addTask(task: Task): IOState<String> {
        val result = taskDao.addTask(task)
        return if(result > 0){
            when(val res = handleApi { apiService.addTask(task.toAddTaskPayload(MyApplication.userId)) }){
                is IOState.Success -> {
                    IOState.Success(res.data.asValidated().status)
                }
                is IOState.Failure -> {
                    taskDao.deleteTask(task)
                    IOState.Failure(res.reason)
                }
            }

        }else {
            IOState.Failure("Could not insert")
        }
    }

    override suspend fun getTasks(): IOState<List<Task>> {
        return try {
            val tasks = taskDao.getTasks()
            if (tasks.isNotEmpty()) {
                IOState.Success(tasks)
            } else {
                when (val response = handleApi { apiService.getTask(hashMapOf("user_id" to MyApplication.userId)) }) {
                    is IOState.Success -> {
                        val apiTasks = response.data.tasks
                        apiTasks.forEach { taskDao.addTask(it.toUITask()) }
                        IOState.Success(apiTasks.map { it.toUITask() })
                    }
                    is IOState.Failure -> IOState.Failure(response.reason)
                }
            }
        } catch (e: Exception) {
            IOState.Failure("Could not get tasks")
        }
    }


    override suspend fun deleteTask(task: Task): IOState<String> {
        return try {
            taskDao.deleteTask(task)
            when(val res = handleApi { apiService.deleteTask(hashMapOf("user_id" to MyApplication.userId,"task_id" to task.id))}){
                is IOState.Success -> IOState.Success(res.data.status ?: "Task Deleted")
                is IOState.Failure -> {
                    taskDao.addTask(task)
                    IOState.Failure(res.reason)
                }
            }

        }catch (e:Exception){
            IOState.Failure("Could not delete")
        }
    }
}