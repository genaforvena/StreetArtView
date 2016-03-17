package org.imozerov.streetartview.ui.explore.map

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.bottom_details.*
import kotlinx.android.synthetic.main.bottom_details.view.*
import kotlinx.android.synthetic.main.fragment_art_map.*
import org.imozerov.streetartview.R
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.ui.detail.interfaces.ArtObjectDetailOpener
import org.imozerov.streetartview.ui.explore.ArtListPresenter
import org.imozerov.streetartview.ui.explore.interfaces.ArtView
import org.imozerov.streetartview.ui.explore.interfaces.Filterable
import org.imozerov.streetartview.ui.extensions.addArtObject
import org.imozerov.streetartview.ui.extensions.addUserLocationMarker
import org.imozerov.streetartview.ui.extensions.getCurrentLocation
import org.imozerov.streetartview.ui.extensions.loadImage
import org.imozerov.streetartview.ui.model.ArtObjectUi

class ArtMapFragment : Fragment(), Filterable, ArtView {
    val TAG = "ArtMapFragment"
    val FRAGMENT_TAG = "MapFragment"

    private var presenter: ArtListPresenter? = null
    private var artObjectDetailOpener: ArtObjectDetailOpener? = null
    private var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null
    private val markersMap: MutableMap<Marker, String> = mutableMapOf()

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            artObjectDetailOpener = context as ArtObjectDetailOpener
        } catch (e: ClassCastException) {
            throw RuntimeException("$context must implement ArtObjectDetailOpener interface")
        }
    }

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
            it.setOnMarkerClickListener { marker ->
                if (marker in markersMap) {
                    showArtObjectDigest(markersMap[marker]!!)
                }
                true
            }
            it.setOnMapClickListener {
                hideArtObjectDigest()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        presenter = ArtListPresenter(this)
        val layout = inflater!!.inflate(R.layout.fragment_art_map, container, false)
        bottomSheetBehavior = BottomSheetBehavior.from(layout.bottom_sheet)
        return layout
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

    override fun onDetach() {
        super.onDetach()
        artObjectDetailOpener = null
        markersMap.clear()
    }

    fun onBackPressed() : Boolean {
        if (bottomSheetBehavior!!.state != BottomSheetBehavior.STATE_HIDDEN) {
            hideArtObjectDigest()
            return true
        }
        return false
    }

    private fun showArtObjectDigest(id: String) {
        Log.v(TAG, "showArtObjectDigest($id)")
        var artObject: ArtObjectUi = presenter!!.getArtObject(id)
        bottom_detail_author.text = artObject.authorsNames()
        bottom_detail_name.text = artObject.name
        bottom_detail_image.loadImage(artObject.picsUrls[0])

        bottom_sheet.visibility = View.VISIBLE
        bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
        bottom_info_linear.setOnClickListener { artObjectDetailOpener!!.openArtObjectDetails(artObject.id) }
    }

    fun hideArtObjectDigest() {
        bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun showArtObjects(artObjectUis: List<ArtObjectUi>) {
        (childFragmentManager.findFragmentByTag(FRAGMENT_TAG) as SupportMapFragment).getMapAsync { googleMap ->
            markersMap.clear()
            googleMap.clear()
            googleMap.addUserLocationMarker(getCurrentLocation(context))
            artObjectUis.forEach {
                val marker = googleMap.addArtObject(it)
                if (marker != null) {
                    markersMap[marker] = it.id
                }
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
