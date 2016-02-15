package org.imozerov.streetartview.ui.explore.map


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_art_map.*
import org.imozerov.streetartview.R
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.ui.explore.ArtListPresenter
import org.imozerov.streetartview.ui.explore.interfaces.ArtView
import org.imozerov.streetartview.ui.explore.interfaces.Filterable
import org.imozerov.streetartview.ui.model.ArtObjectUi

/**
 * A simple [Fragment] subclass.
 * Use the [ArtMapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ArtMapFragment : Fragment(), Filterable, ArtView {

    private var presenter: ArtListPresenter? = null

    override fun showArtObjects(artObjectUis: List<ArtObjectUi>) {
        (childFragmentManager.findFragmentByTag("MapFragment") as SupportMapFragment)
                .getMapAsync { googleMap ->
                    googleMap.clear()
                    for(uiObject in artObjectUis){
                        googleMap.addMarker(MarkerOptions()
                                .position(LatLng(uiObject.lat,uiObject.lng))
                                .title(uiObject.author.name)
                                .snippet(uiObject.name)
                                .icon(BitmapDescriptorFactory.defaultMarker(
                                        BitmapDescriptorFactory.HUE_AZURE)))
                    }
                }
    }

    override fun applyFilter(queryToApply: String) {
        presenter!!.applyFilter(queryToApply)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        presenter = ArtListPresenter(this)

        return inflater!!.inflate(R.layout.fragment_art_map, container, false)
    }

    override fun onDestroyView() {
        presenter = null
    }

    override fun onDestroy() {
        super.onDestroy()
        val refWatcher = StreetArtViewApp.getRefWatcher(activity);
        refWatcher.watch(this);
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val mapFragment : SupportMapFragment = SupportMapFragment.newInstance()
        childFragmentManager
                .beginTransaction()
                .add(map.id, mapFragment, "MapFragment")
                .commit()

        mapFragment.getMapAsync { googleMap ->
            googleMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(LatLng(56.3298063,44.0095144), 14f))
        }
    }

    override fun onStart() {
        super.onStart()
        presenter!!.onStart(activity.application as StreetArtViewApp)
    }

    override fun onStop() {
        super.onStop()
        presenter!!.onStop()
    }
    companion object {
        fun newInstance(): ArtMapFragment {
            val fragment = ArtMapFragment()
            return fragment
        }
    }

}
