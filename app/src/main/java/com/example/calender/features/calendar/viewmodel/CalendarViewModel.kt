package com.example.calender.features.calendar.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.calender.features.calendar.common.IOState
import com.example.calender.features.calendar.model.Task
import com.example.calender.features.calendar.model.io.TaskApiService
import com.example.calender.features.calendar.model.io.TaskRepositoryImpl
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CalendarViewModel(application: Application) : AndroidViewModel(application) {

    sealed class TaskStatusEvent{
        data class TaskAdded(val message:String) : TaskStatusEvent()
        data class FailedToAddTask(val reason:String) : TaskStatusEvent()
    }

    sealed class TasksScreenEvents{
        data class OnTasksLoaded(val tasks:List<Task>) : TasksScreenEvents()
        data class OnTaskLoadFailed(val reason:String) : TasksScreenEvents()
        data class OnTaskDeleted(val message: String, val remainingTasks: List<Task>) : TasksScreenEvents()
        data class OnTaskDeletionFailed(val reason:String) : TasksScreenEvents()
    }

    private val taskApiService = Retrofit
        .Builder()
        .baseUrl("http://dev.frndapp.in:8085/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }).build())
        .build()
        .create(TaskApiService::class.java)

    val taskRepository = TaskRepositoryImpl(application,taskApiService)

    private val _taskStatusEventsFlow = MutableSharedFlow<TaskStatusEvent>()
    val taskStatusEventSharedFlow:SharedFlow<TaskStatusEvent> = _taskStatusEventsFlow

    private val _tasksScreenEventsFlow = MutableSharedFlow<TasksScreenEvents>()
    val taskScreenEventSharedFlow:SharedFlow<TasksScreenEvents> = _tasksScreenEventsFlow

    private val _tasks = mutableListOf<Task>()
    val tasks:List<Task> = _tasks


    fun addTask(task: Task) {
        viewModelScope.launch {
            when(val result = taskRepository.addTask(task)){
                is IOState.Success -> {
                     _tasks.add(task)
                    _taskStatusEventsFlow.emit(TaskStatusEvent.TaskAdded(result.data))
                }
                is IOState.Failure -> {
                    _taskStatusEventsFlow.emit(TaskStatusEvent.FailedToAddTask(result.reason))
                }
            }

        }
    }

    fun loadTasks() {
        viewModelScope.launch {
            when(val result = taskRepository.getTasks()){
                is IOState.Success -> {
                     _tasks.clear()
                     _tasks.addAll(result.data)
                     _tasksScreenEventsFlow.emit(TasksScreenEvents.OnTasksLoaded(result.data))
                }
                is IOState.Failure -> {
                     _tasksScreenEventsFlow.emit(TasksScreenEvents.OnTaskLoadFailed(result.reason))
                }
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            when (val result = taskRepository.deleteTask(task)) {
                is IOState.Success -> {
                     val remainingTasks = _tasks.filter { it.id != task.id }
                     _tasks.clear()
                     _tasks.addAll(remainingTasks)
                    _tasksScreenEventsFlow.emit(
                        TasksScreenEvents.OnTaskDeleted(
                            result.data,
                            remainingTasks
                        )
                    )
                }
                is IOState.Failure -> {
                    _tasksScreenEventsFlow.emit(TasksScreenEvents.OnTaskDeletionFailed(result.reason))
                }
            }
        }
    }

}
