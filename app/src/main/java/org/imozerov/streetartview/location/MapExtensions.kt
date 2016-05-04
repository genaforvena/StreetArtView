package org.imozerov.streetartview.location

import android.content.SharedPreferences
import android.location.Location
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.imozerov.streetartview.R
import org.imozerov.streetartview.ui.extensions.getDouble
import org.imozerov.streetartview.ui.extensions.putDouble
import org.imozerov.streetartview.ui.model.ArtObjectUi

/**
 * Created by imozerov on 19.02.16.
 */
val NIZHNY_NOVGOROD_LOCATION = LatLng(56.3298063, 44.0095144)

private val LAT_KEY = "lat_lat"
private val LNG_KEY = "lng_lng"

fun LatLng.distanceTo(point: LatLng) : Float {
    val distance = FloatArray(1)
    Location.distanceBetween(point.latitude, point.longitude, this.latitude, this.longitude, distance);
    return (distance[0])
}

fun LatLng.printableDistanceTo(point: LatLng) : String {
    val distance = distanceTo(point) / 1000
    return String.format("%.1f", distance)
}

fun SharedPreferences.getCachedLocation() : LatLng {
    val lat = getDouble(LAT_KEY, NIZHNY_NOVGOROD_LOCATION.latitude)
    val lng = getDouble(LNG_KEY, NIZHNY_NOVGOROD_LOCATION.longitude)
    return LatLng(lat, lng)
}

fun SharedPreferences.cacheLocation(latLng: LatLng) {
    putDouble(LAT_KEY, latLng.latitude)
    putDouble(LNG_KEY, latLng.longitude)
}

fun GoogleMap.zoomTo(latLng: LatLng) {
    animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, Math.floor(cameraPosition.zoom.toDouble() + 1).toFloat()), 300, null)
}

fun GoogleMap.moveTo(latLng: LatLng) {
    animateCamera(CameraUpdateFactory.newLatLng(latLng))
}

fun GoogleMap.addArtObjectSimpleMarker(artObject: ArtObjectUi) : Marker {
    val markerOptions = MarkerOptions().position(LatLng(artObject.lat, artObject.lng))
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place_black_36dp))
    return addMarker(markerOptions)
}