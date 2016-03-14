package org.imozerov.streetartview.ui.explore.map

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.fragment_art_map.*
import org.imozerov.streetartview.R
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.ui.explore.ArtListPresenter
import org.imozerov.streetartview.ui.explore.interfaces.ArtView
import org.imozerov.streetartview.ui.explore.interfaces.Filterable
import org.imozerov.streetartview.ui.extensions.addArtObject
import org.imozerov.streetartview.ui.extensions.addUserLocationMarker
import org.imozerov.streetartview.ui.extensions.getCurrentLocation
import org.imozerov.streetartview.ui.model.ArtObjectUi

class ArtMapFragment : Fragment(), Filterable, ArtView {
    val FRAGMENT_TAG = "MapFragment"

    private var presenter: ArtListPresenter? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val mapFragment: SupportMapFragment = SupportMapFragment.newInstance()
        childFragmentManager
                .beginTransaction()
                .replace(map.id, mapFragment, FRAGMENT_TAG)
                .commit()

        mapFragment.getMapAsync {
            it.uiSettings.isMapToolbarEnabled = false
            val userLocation = getCurrentLocation(context)
            it.addUserLocationMarker(userLocation)
            it.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 11f))
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        presenter = ArtListPresenter(this)
        return inflater!!.inflate(R.layout.fragment_art_map, container, false)
    }

    override fun onStart() {
        super.onStart()
        presenter!!.onStart(activity.application as StreetArtViewApp)
    }

    override fun onStop() {
        super.onStop()
        presenter!!.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter = null
    }

    override fun onDestroy() {
        super.onDestroy()
        val refWatcher = StreetArtViewApp.getRefWatcher(activity);
        refWatcher.watch(this);
    }

    override fun showArtObjects(artObjectUis: List<ArtObjectUi>) {
        (childFragmentManager.findFragmentByTag(FRAGMENT_TAG) as SupportMapFragment).getMapAsync { googleMap ->
            googleMap.clear()
            googleMap.addUserLocationMarker(getCurrentLocation(context))
            artObjectUis.forEach {
                googleMap.addArtObject(it)
            }
        }
    }

    override fun applyFilter(queryToApply: String) {
        presenter!!.applyFilter(queryToApply)
    }

    companion object {
        fun newInstance(): ArtMapFragment {
            val fragment = ArtMapFragment()
            return fragment
        }
    }

}
