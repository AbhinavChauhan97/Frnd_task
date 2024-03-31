package com.example.calender.features.calendar.model

class AddResponse(
    val status:String? = null
)

class ValidatedAddResponse(
    val status:String
)


fun AddResponse.asValidated() = ValidatedAddResponse(status = status ?: "Task Added Successfully")

