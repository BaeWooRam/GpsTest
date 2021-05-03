package com.example.gpstest.gps.listener

import java.lang.Exception

interface GpsTaskListener<T> {
    fun onSuccess(data:T)
    fun onFailure(exception: Exception?)
}