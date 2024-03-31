package com.example.calender.features.calendar.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String
)

data class AddTaskPayload(
    @SerializedName("user_id")
    val userId:Int,
    val task: Task

)
fun Task.toAddTaskPayload(userId: Int) = AddTaskPayload(
    userId = userId,
    task = this
)