package com.example.calender.features.calendar.common

import android.widget.Toast
import androidx.fragment.app.Fragment
import retrofit2.Response

fun Fragment.showToast(message:String){
    Toast.makeText(requireContext(),message,Toast.LENGTH_LONG).show()
}

suspend fun <T> handleApi(
    block: suspend () -> Response<T>
) : IOState<T> {
    return try {
        val response = block()
        val body:T?
        if(response.isSuccessful){
            body = response.body()
            if(body != null){
                IOState.Success(body)
            }else{
                IOState.Failure(response.message())
            }
        }else{
            IOState.Failure(response.message())
        }
    }catch (e : Exception){
        IOState.Failure(e.message ?: "Something went wrong")
    }
}