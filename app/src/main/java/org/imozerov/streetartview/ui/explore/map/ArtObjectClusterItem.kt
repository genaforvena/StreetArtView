package org.imozerov.streetartview.ui.explore.map

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import org.imozerov.streetartview.ui.model.ArtObjectUi

/**
 * Created by imozerov on 18.03.16.
 */
class ArtObjectClusterItem(val artObjectUi: ArtObjectUi) : ClusterItem {
    override fun getPosition(): LatLng? = LatLng(artObjectUi.lat, artObjectUi.lng)
}
