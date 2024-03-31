package com.example.calender.features.calendar.model

import com.google.gson.annotations.SerializedName

data class ApiTask(
    @SerializedName("task_id")
    val taskId:Int?,
    @SerializedName("task_detail")
    val taskDetail: TaskDetails
)

data class TaskDetails(
    val title:String?,
    val description:String?
)

fun ApiTask.toUITask() = Task(
    id = taskId ?: 0,
    title = taskDetail.title ?: "Empty Title",
    description = taskDetail.description ?: "Empty Description"
)