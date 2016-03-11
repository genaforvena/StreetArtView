package org.imozerov.streetartview.location

import android.content.Context
import android.location.Criteria
import android.location.LocationManager
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * Created by imozerov on 11.03.16.
 */
val DEFAULT_USER_LOCATION = LatLng(56.3298063, 44.0095144)

val TAG = "LocationHelper"

fun getCurrentLocation(context: Context): LatLng {
    val criteria = Criteria()
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val provider = locationManager.getBestProvider(criteria, false);
    val location = locationManager.getLastKnownLocation(provider);
    var userLocation: LatLng
    if (location != null) {
        Log.e(TAG, "Current location is null!")
        userLocation = LatLng(location.latitude, location.longitude);
    } else {
        userLocation = DEFAULT_USER_LOCATION
    }
    return userLocation
}

fun GoogleMap.addUserLocationMarker(userLocation: LatLng) {
    if (userLocation != DEFAULT_USER_LOCATION) {
        addMarker(MarkerOptions().position(userLocation))
    }
}