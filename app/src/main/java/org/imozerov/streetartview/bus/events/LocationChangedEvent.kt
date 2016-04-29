package org.imozerov.streetartview.bus.events

import com.google.android.gms.maps.model.LatLng

/**
 * Created by imozerov on 29.04.16.
 */
data class LocationChangedEvent(val newLocation: LatLng)