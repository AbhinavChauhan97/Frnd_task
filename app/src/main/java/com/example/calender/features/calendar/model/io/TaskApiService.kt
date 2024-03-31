package com.example.calender.features.calendar.model.io

import com.example.calender.features.calendar.model.TasksResponse
import com.example.calender.features.calendar.model.AddResponse
import com.example.calender.features.calendar.model.AddTaskPayload
import com.example.calender.features.calendar.model.DeleteResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TaskApiService {

    @POST("api/deleteCalendarTask")
    suspend fun deleteTask(@Body params:HashMap<String,Int>) : Response<DeleteResponse>

    @POST("api/storeCalendarTask")
    suspend fun addTask(@Body task: AddTaskPayload) : Response<AddResponse>

    @POST("api/getCalendarTaskList")
    suspend fun getTask(@Body userId:HashMap<String,Int>) : Response<TasksResponse>

}
