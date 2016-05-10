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
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import kotlinx.android.synthetic.main.bottom_details.*
import kotlinx.android.synthetic.main.bottom_details.view.*
import kotlinx.android.synthetic.main.fragment_art_map.view.*
import org.imozerov.streetartview.R
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.location.NIZHNY_NOVGOROD_LOCATION
import org.imozerov.streetartview.location.moveTo
import org.imozerov.streetartview.location.zoomTo
import org.imozerov.streetartview.ui.detail.interfaces.ArtObjectDetailOpener
import org.imozerov.streetartview.ui.explore.all.AllPresenter
import org.imozerov.streetartview.ui.explore.interfaces.ArtView
import org.imozerov.streetartview.ui.explore.interfaces.Filterable
import org.imozerov.streetartview.ui.extensions.loadImage
import org.imozerov.streetartview.ui.model.ArtObjectUi

class ArtMapFragment : Fragment(), Filterable, ArtView {
    val TAG = "ArtMapFragment"
    val FRAGMENT_TAG = "MapFragment"

    private val presenter = AllPresenter()

    private var artObjectDetailOpener: ArtObjectDetailOpener? = null
    private var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null
    private var clusterManager: ClusterManager<ArtObjectClusterItem>? = null

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
        val layout = inflater!!.inflate(R.layout.fragment_art_map, container, false)
        bottomSheetBehavior = BottomSheetBehavior.from(layout.bottom_sheet)

        val mapFragment: SupportMapFragment = SupportMapFragment.newInstance()
        childFragmentManager.beginTransaction().replace(layout.map.id, mapFragment, FRAGMENT_TAG).commit()

        mapFragment.getMapAsync { gMap ->
            clusterManager = ClusterManager(context, gMap)
            with (clusterManager!!) {
                setOnClusterClickListener {
                    gMap.zoomTo(it.position)
                    hideArtObjectDigest()
                    true
                }
                setOnClusterItemClickListener {
                    gMap.moveTo(LatLng(it.artObjectUi.lat, it.artObjectUi.lng))
                    showArtObjectDigest(it.artObjectUi.id)
                    true
                }
                setRenderer(ArtObjectRenderer(context, gMap, clusterManager))
            }
            with (gMap) {
                setOnCameraChangeListener(clusterManager);
                setOnMarkerClickListener(clusterManager);

                uiSettings.isMapToolbarEnabled = false
                isMyLocationEnabled = true
                moveCamera(CameraUpdateFactory.newLatLngZoom(NIZHNY_NOVGOROD_LOCATION, 11f))
                setOnMapClickListener { hideArtObjectDigest() }
            }
        }
        return layout
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
        }
    }

    fun stopLocationTracking() {
        (childFragmentManager.findFragmentByTag(FRAGMENT_TAG) as? SupportMapFragment)?.getMapAsync {
            it.isMyLocationEnabled = false
        }
    }

    fun onBackPressed(): Boolean {
        if (bottomSheetBehavior!!.state != BottomSheetBehavior.STATE_HIDDEN) {
            hideArtObjectDigest()
            return true
        }
        return false
    }

    private fun showArtObjectDigest(id: String) {
        Log.v(TAG, "showArtObjectDigest($id)")
        var artObject: ArtObjectUi = presenter.getArtObject(id)
        bottom_detail_image.loadImage(artObject.picsUrls[0])
        bottom_detail_address.text = artObject.address

        bottom_sheet.visibility = View.VISIBLE
        bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
        bottom_detail_image.setOnClickListener { artObjectDetailOpener!!.openArtObjectDetails(artObject.id) }
    }

    fun hideArtObjectDigest() {
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
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

class ArtObjectRenderer : DefaultClusterRenderer<ArtObjectClusterItem> {

    // TODO load images into icon
    constructor(context: Context?, map: GoogleMap?, clusterManager: ClusterManager<ArtObjectClusterItem>?) : super(context, map, clusterManager)

    override fun onBeforeClusterItemRendered(item: ArtObjectClusterItem?, markerOptions: MarkerOptions?) {
        val drawable: Int
        if (item!!.artObjectUi.isFavourite) {
            drawable = R.drawable.ic_favorite_black_36dp
        } else {
            drawable = R.drawable.ic_place_black_36dp
        }
        markerOptions!!.icon(BitmapDescriptorFactory.fromResource(drawable))
    }

    override fun shouldRenderAsCluster(cluster: Cluster<ArtObjectClusterItem>?): Boolean = cluster!!.size > 3
}


