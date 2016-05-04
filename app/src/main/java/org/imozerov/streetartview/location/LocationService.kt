package org.imozerov.streetartview.location

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.bus.RxBus
import org.imozerov.streetartview.bus.events.LocationChangedEvent
import org.imozerov.streetartview.location.cacheLocation
import rx.Observable
import rx.schedulers.Schedulers
import javax.inject.Inject

class LocationService : Service(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    @Inject
    lateinit var bus: RxBus
    @Inject
    lateinit var prefs: SharedPreferences

    private val googleApiClient by lazy {
        GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build()
    }

    override fun onBind(intent: Intent): IBinder? {
        throw RuntimeException("$this could not be bound!")
    }

    override fun onCreate() {
        (application as StreetArtViewApp).appComponent!!.inject(this)
    }

    override fun onDestroy() {
        googleApiClient.disconnect()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            if (intent.action == ACTION_CHECK_LOCATION) {
                googleApiClient.connect()
            } else {
                throw RuntimeException("Unknown intent received: $intent")
            }
        }
        return START_STICKY
    }

    override fun onConnected(bundle: Bundle?) {
        Log.v(TAG, "onConnected($bundle)")
        Observable.just(LocationServices.FusedLocationApi.getLastLocation(googleApiClient))
                .filter { it != null }
                .map { LatLng(it!!.latitude, it.longitude) }
                .doOnNext { prefs.cacheLocation(it) }
                .doOnNext { bus.post(LocationChangedEvent(it)) }
                .observeOn(Schedulers.newThread())
                .subscribe {
                    stopSelf()
                }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.e(TAG, "onConnectionFailed($connectionResult)")
        stopSelf()
    }

    override fun onConnectionSuspended(errorCode: Int) {
        Log.e(TAG, "onConnectionSuspended($errorCode)")
        stopSelf()
    }

    companion object {
        private val TAG = "LocationService"

        private val ACTION_CHECK_LOCATION = "check_location"

        fun getLocationOnce(context: Context) {
            val intent = Intent(context, LocationService::class.java)
            intent.action = ACTION_CHECK_LOCATION
            context.startService(intent)
        }
    }
}
