package com.example.gpstest

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.gpstest.gps.GpsProvider
import com.example.gpstest.gps.listener.GpsTaskListener
import java.lang.Exception

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private lateinit var gpsProvider:GpsProvider
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gpsProvider = GpsProvider(this)

        gpsProvider.getLastLocationFromGooglePlayService(object : GpsTaskListener<Location>{
            override fun onSuccess(data: Location) {
                Log.d("MainActivity","location latitude = ${data.latitude}, longitude = ${data.longitude}")
            }

            override fun onFailure(exception: Exception?) {
                Log.d("MainActivity","location exception msg = ${exception?.message}")
            }
        })
    }
}