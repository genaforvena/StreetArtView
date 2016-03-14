package org.imozerov.streetartview.ui.extensions

import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.imozerov.streetartview.ui.model.ArtObjectUi

/**
 * Created by imozerov on 19.02.16.
 */
val DEFAULT_USER_LOCATION = LatLng(56.3298063, 44.0095144)

fun LatLng.distanceTo(point: LatLng) : Float {
    val distance = FloatArray(1)
    Location.distanceBetween(point.latitude, point.longitude, this.latitude, this.longitude, distance);
    return (distance[0] / 1000)
}

fun LatLng.printableDistanceTo(point: LatLng) : String {
    val distance = distanceTo(point)
    return String.format("%.1f", distance)
}

fun getCurrentLocation(context: Context): LatLng {
    val criteria = Criteria()
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val provider = locationManager.getBestProvider(criteria, false);
    val location = locationManager.getLastKnownLocation(provider);
    var userLocation: LatLng
    if (location != null) {
        userLocation = LatLng(location.latitude, location.longitude);
    } else {
        Log.e(TAG, "Current location is null!")
        userLocation = DEFAULT_USER_LOCATION
    }
    return userLocation
}

fun GoogleMap.addUserLocationMarker(userLocation: LatLng) {
    if (userLocation != DEFAULT_USER_LOCATION) {
        Log.v(TAG, "Location: Setting user marker in $userLocation")
        val markerOptions = MarkerOptions().position(userLocation)
                .icon(BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_RED))
        addMarker(markerOptions)
    } else {
        Log.e(TAG, "Location of the user is unknown!")
    }
}

fun GoogleMap.addArtObject(artObject: ArtObjectUi) {
    val markerOptions = MarkerOptions().position(LatLng(artObject.lat, artObject.lng))
            .title(artObject.authorsNames())
            .snippet(artObject.name)
            .icon(BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_AZURE))
    addMarker(markerOptions)
}