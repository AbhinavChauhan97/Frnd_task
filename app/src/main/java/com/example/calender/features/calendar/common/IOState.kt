package com.example.calender.features.calendar.common

sealed class IOState<T> {
    data class Success<T>(val data:T): IOState<T>()
    data class Failure<T>(val reason:String): IOState<T>()
}