package org.imozerov.streetartview.ui.extensions

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.imozerov.streetartview.ui.model.ArtObjectUi

/**
 * Created by imozerov on 19.02.16.
 */
fun GoogleMap.addArtObject(artObject: ArtObjectUi) {
    addMarker(MarkerOptions().position(LatLng(artObject.lat, artObject.lng))
            .title(artObject.authorsNames())
            .snippet(artObject.name)
            .icon(BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_AZURE)))
}