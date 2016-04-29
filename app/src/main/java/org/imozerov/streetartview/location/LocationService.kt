package org.imozerov.streetartview.location

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.bus.RxBus
import org.imozerov.streetartview.bus.events.LocationChangedEvent
import org.imozerov.streetartview.ui.extensions.cacheLocation
import javax.inject.Inject

class LocationService : Service(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private val TAG = "LocationService"

    enum class Mode {
        CHECK, TRACKING
    }

    companion object {
        private val ACTION_CHECK_LOCATION = "check_location"
        private val ACTION_START_TRACK_LOCATION = "track_location"
        private val ACTION_STOP_TRACK_LOCATION = "track_location"

        fun checkLocation(context: Context) {
            val intent = Intent(context, LocationService::class.java)
            intent.action = ACTION_CHECK_LOCATION
            context.startService(intent)
        }

        fun startTrackingLocation(context: Context) {
            val intent = Intent(context, LocationService::class.java)
            intent.action = ACTION_START_TRACK_LOCATION
            context.startService(intent)
        }

        fun stopTrackingLocation(context: Context) {
            val intent = Intent(context, LocationService::class.java)
            intent.action = ACTION_STOP_TRACK_LOCATION
            context.startService(intent)
        }
    }

    @Inject
    lateinit var bus: RxBus
    @Inject
    lateinit var prefs: SharedPreferences

    private var mode = Mode.CHECK

    private val googleApiClient by lazy {
        GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        (application as StreetArtViewApp).appComponent!!.inject(this)
        googleApiClient.connect()
    }

    override fun onDestroy() {
        googleApiClient.disconnect()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            if (intent.action == ACTION_CHECK_LOCATION) {
                if (mode != Mode.TRACKING) {
                    mode = Mode.CHECK
                }
            } else if (intent.action == ACTION_START_TRACK_LOCATION) {
                mode = Mode.TRACKING
            } else if (intent.action == ACTION_STOP_TRACK_LOCATION) {
                mode = Mode.CHECK
            }
        }
        return START_STICKY
    }

    override fun onLocationChanged(lastKnownLocation: Location?) {
        if (lastKnownLocation != null) {
            val newLocation = LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)
            prefs.cacheLocation(newLocation)
            bus.post(LocationChangedEvent(newLocation))

            if (mode == Mode.CHECK) {
                stopSelf()
            }
        }
    }

    override fun onConnected(p0: Bundle?) {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, LocationRequest.create().setInterval(10000), this)
        Log.v(TAG, "onConnected($p0)")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.e(TAG, "onConnectionFailed($p0)")
        stopSelf()
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.e(TAG, "onConnectionSuspended($p0)")
        stopSelf()
    }
}
