package com.example.calender.features.calendar.common

import android.app.Application


class MyApplication : Application() {

    companion object {
        val userId = 555
    }
    override fun onCreate() {
        super.onCreate()
    }
}