package org.imozerov.streetartview.ui.explore.map

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.flipboard.bottomsheet.BottomSheetLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.maps.android.clustering.ClusterManager
import kotlinx.android.synthetic.main.fragment_art_map.*
import kotlinx.android.synthetic.main.fragment_art_map.view.*
import org.imozerov.streetartview.R
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.location.NIZHNY_NOVGOROD_LOCATION
import org.imozerov.streetartview.location.zoomTo
import org.imozerov.streetartview.ui.detail.DetailArtObjectFragment
import org.imozerov.streetartview.ui.detail.interfaces.ArtObjectDetailOpener
import org.imozerov.streetartview.ui.explore.all.AllPresenter
import org.imozerov.streetartview.ui.explore.interfaces.ArtView
import org.imozerov.streetartview.ui.explore.interfaces.Filterable
import org.imozerov.streetartview.ui.model.ArtObjectUi

class ArtMapFragment : Fragment(), Filterable, ArtView {
    val TAG = "ArtMapFragment"
    val FRAGMENT_TAG = "MapFragment"

    private val presenter = AllPresenter()
    private var clickedClusterItem: ArtObjectClusterItem? = null

    private var artObjectDetailOpener: ArtObjectDetailOpener? = null
    private var clusterManager: ClusterManager<ArtObjectClusterItem>? = null

    var isLocationTracking = false
        private set

    var layout: View? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            artObjectDetailOpener = context as ArtObjectDetailOpener
        } catch (e: ClassCastException) {
            throw RuntimeException("$context must implement ArtObjectDetailOpener interface")
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        layout = inflater!!.inflate(R.layout.fragment_art_map, container, false)

        layout!!.bottom_sheet.setShouldDimContentView(false)
        layout!!.bottom_sheet.peekOnDismiss = false
        layout!!.bottom_sheet.peekSheetTranslation = resources.getDimensionPixelSize(R.dimen.detail_image_min_height).toFloat()
        layout!!.bottom_sheet.interceptContentTouch = false

        val mapFragment: SupportMapFragment = SupportMapFragment.newInstance()
        childFragmentManager.beginTransaction().replace(layout!!.map.id, mapFragment, FRAGMENT_TAG).commit()

        mapFragment.getMapAsync { gMap ->
            clusterManager = ClusterManager(context, gMap)
            val renderer = ArtObjectRenderer(context, gMap, clusterManager!!)
            with (clusterManager!!) {
                setRenderer(renderer)

                setOnClusterClickListener {
                    if (clickedClusterItem != null) {
                        renderer.deselectItem(clickedClusterItem)
                    }
                    clickedClusterItem = null
                    gMap.zoomTo(it.position)
                    hideArtObjectDigest()
                    true
                }

                setOnClusterItemClickListener {
                    if (clickedClusterItem != null) {
                        renderer.deselectItem(clickedClusterItem)
                    }
                    clickedClusterItem = it
                    renderer.selectItem(clickedClusterItem)
                    showArtObjectDigest(it.artObjectUi.id)
                    true
                }
            }
            with (gMap) {
                setOnCameraChangeListener(clusterManager)
                setOnMarkerClickListener(clusterManager)
                setOnInfoWindowClickListener(clusterManager)

                uiSettings.isMapToolbarEnabled = false
                uiSettings.isZoomControlsEnabled = true
                moveCamera(CameraUpdateFactory.newLatLngZoom(NIZHNY_NOVGOROD_LOCATION, 11f))
                setOnMapClickListener {
                    if (clickedClusterItem != null) {
                        renderer.deselectItem(clickedClusterItem)
                    }
                    clickedClusterItem = null
                    hideArtObjectDigest()
                }
            }
        }
        return layout
    }

    fun openBottomSheet(id: String?) {
        if (bottom_sheet.isSheetShowing) {
            bottom_sheet.dismissSheet()
            bottom_sheet.addOnSheetDismissedListener {
                DetailArtObjectFragment.newInstance(id!!).show(fragmentManager, R.id.bottom_sheet)
            }
        } else {
            DetailArtObjectFragment.newInstance(id!!).show(fragmentManager, R.id.bottom_sheet)
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.bindView(this, context)
    }

    override fun onStop() {
        super.onStop()
        presenter.unbindView()
    }

    override fun onDestroy() {
        super.onDestroy()
        val refWatcher = StreetArtViewApp.getRefWatcher(activity);
        refWatcher.watch(this);
    }

    override fun onDetach() {
        super.onDetach()
        artObjectDetailOpener = null
    }

    fun startLocationTracking() {
        (childFragmentManager.findFragmentByTag(FRAGMENT_TAG) as? SupportMapFragment)?.getMapAsync {
            it.isMyLocationEnabled = true
            isLocationTracking = true
        }
    }

    fun stopLocationTracking() {
        (childFragmentManager.findFragmentByTag(FRAGMENT_TAG) as? SupportMapFragment)?.getMapAsync {
            it.isMyLocationEnabled = false
            isLocationTracking = false
        }
    }

    fun showArtObjectDigest(id: String) {
        Log.v(TAG, "showArtObjectDigest($id)")
        openBottomSheet(id)
    }

    fun hideArtObjectDigest() {
        if ((layout?.findViewById(R.id.bottom_sheet) as? BottomSheetLayout)?.isSheetShowing == true) {
            bottom_sheet.dismissSheet()
        }
    }

    override fun showArtObjects(artObjectUis: List<ArtObjectUi>) {
        (childFragmentManager.findFragmentByTag(FRAGMENT_TAG) as SupportMapFragment).getMapAsync { googleMap ->
            with (clusterManager!!) {
                clearItems()
                addItems(artObjectUis.map { ArtObjectClusterItem(it) })
                cluster()
            }
        }
    }

    override fun stopRefresh() {
        // TODO refactor this code as it smells. We have an interface method that we do not implement.
    }

    override fun applyFilter(queryToApply: String) {
        presenter.applyFilter(queryToApply)
    }

    companion object {
        fun newInstance(): ArtMapFragment {
            val fragment = ArtMapFragment()
            return fragment
        }
    }
}



