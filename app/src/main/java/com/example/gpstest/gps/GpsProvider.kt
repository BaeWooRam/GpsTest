package com.example.gpstest.gps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.example.gpstest.gps.exception.GpsLocationSettingRequestException
import com.example.gpstest.gps.exception.GpsNotPermissionException
import com.example.gpstest.gps.listener.GpsTaskListener
import com.google.android.gms.location.*
import java.lang.Exception

/**
 * Gps 관련 Provider
 * 참조 : https://github.com/android/location-samples/tree/432d3b72b8c058f220416958b444274ddd186abd/LocationUpdatesForegroundService
 */
class GpsProvider(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    /**
     * Google Play Service로 마지막 알려진 정보 가져오기
     *
     * 다음과 같은 경우에 null이 리턴될 수 있습니다.
     * 1.디바이스의 Location 사용이 중지되는 경우 캐시 정보가 삭제되기 때문에 null이 리턴될 수 있습니다.
     * 2.위치 정보를 얻은 적이 없을 때 null이 리턴될 수 있습니다.
     * 3.Google Play 서비스가 재실행되었을 때, 저장된 위치 정보가 없기 때문에 null이 리턴될 수 있습니다.
     *
     * 정확성 검증 방법
     * 1. 가져온 위치가 이전에 가져온 위치보다 훨씬 더 최신인지 확인합니다.
     * 2. 위치에서 주장하는 정확성이 이전 예상치의 정확성보다 좋은지 나쁜지 확인합니다.
     * 3. 새 위치와 연결된 제공자를 확인합니다. 이 제공자를 앱의 캐시된 위치에서 사용하는 제공자보다 더 신뢰하는지 판단합니다.
     *
     */
    fun getLastLocationFromGooglePlayService(taskListener: GpsTaskListener<Location>) {
        val check = checkSelfPermission()

        if (!check) {
            taskListener.onFailure(GpsNotPermissionException())
            return
        }

        fusedLocationClient.lastLocation.addOnCompleteListener {
            if (it.isSuccessful) {
                taskListener.onSuccess(it.result)
            } else {
                taskListener.onFailure(it.exception)
            }
        }
    }

    fun getCurrentLocation(
        locationRequest: LocationRequest,
        taskListener: GpsTaskListener<Location>
    ) {
        val check = checkSelfPermission()

        if (!check) {
            taskListener.onFailure(GpsNotPermissionException())
            return
        }

        val locationSettingsRequest =
            LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()

        /**
         * LocationSettingsStates에서 여러 가지 확인 가능
         *
         * 1. fromIntent(Intent intent) : Retrieves the location settings states from the intent extras.
         * 2. isBlePresent() : Whether BLE is present on the device.
         * 3. isBleUsable() : Whether BLE is enabled and is usable by the app.
         * 4. isGpsPresent() : Whether GPS provider is present on the device.
         * 5. isGpsUsable() : Whether GPS provider is enabled and is usable by the app.
         * 6. isLocationPresent() : Whether location is present on the device.
         * 7. isLocationUsable() : Whether location is enabled and is usable by the app.
         * 8. isNetworkLocationPresent() : Whether network location provider is present on the device.
         * 9. isNetworkLocationUsable() : Whether network location provider is enabled and usable by the app.
         */
        val client = LocationServices.getSettingsClient(context)

        client
            .checkLocationSettings(locationSettingsRequest)
            .addOnCompleteListener {
                if (it.isSuccessful)

                else{
                    taskListener.onFailure(GpsLocationSettingRequestException())
                }
            }
    }


    /**
     * 위치 업데이트 시작
     */
    fun startLocationUpdates(locationRequest: LocationRequest){
        val check = checkSelfPermission()

        if (!check) {
            return
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, object :LocationCallback(){
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)
            }

            override fun onLocationAvailability(p0: LocationAvailability?) {
                super.onLocationAvailability(p0)
            }
        }, Looper.getMainLooper())
    }

    /**
     * 위치 업데이트 중지
     */
    fun stopLocationUpdates(locationCallback: LocationCallback){
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun checkSelfPermission(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED)
    }
}