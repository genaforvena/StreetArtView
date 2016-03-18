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
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator
import kotlinx.android.synthetic.main.art_object_cluster_item.view.*
import kotlinx.android.synthetic.main.bottom_details.*
import kotlinx.android.synthetic.main.bottom_details.view.*
import kotlinx.android.synthetic.main.fragment_art_map.view.*
import org.imozerov.streetartview.R
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.ui.detail.interfaces.ArtObjectDetailOpener
import org.imozerov.streetartview.ui.explore.ArtListPresenter
import org.imozerov.streetartview.ui.explore.interfaces.ArtView
import org.imozerov.streetartview.ui.explore.interfaces.Filterable
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
        presenter = ArtListPresenter(this)
        val layout = inflater!!.inflate(R.layout.fragment_art_map, container, false)
        bottomSheetBehavior = BottomSheetBehavior.from(layout.bottom_sheet)

        val mapFragment: SupportMapFragment = SupportMapFragment.newInstance()
        childFragmentManager.beginTransaction().replace(layout.map.id, mapFragment, FRAGMENT_TAG).commit()

        mapFragment.getMapAsync {
            clusterManager = ClusterManager(context, it)
            clusterManager!!.setOnClusterItemClickListener {
                showArtObjectDigest(it.artObjectUi.id)
                true
            }
            clusterManager!!.setRenderer(ArtObjectRenderer(context, it, clusterManager))
            it.setOnCameraChangeListener(clusterManager);
            it.setOnMarkerClickListener(clusterManager);

            it.uiSettings.isMapToolbarEnabled = false
            val userLocation = getCurrentLocation(context)
            it.addUserLocationMarker(userLocation)
            it.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 11f))
            it.setOnMapClickListener { hideArtObjectDigest() }
        }
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
        bottom_info_go_to_detail_button.setOnClickListener { artObjectDetailOpener!!.openArtObjectDetails(artObject.id) }
    }

    fun hideArtObjectDigest() {
        bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun showArtObjects(artObjectUis: List<ArtObjectUi>) {
        (childFragmentManager.findFragmentByTag(FRAGMENT_TAG) as SupportMapFragment).getMapAsync { googleMap ->
            clusterManager!!.clearItems()
            clusterManager!!.addItems(artObjectUis.map { ArtObjectClusterItem(it) } )
            clusterManager!!.cluster()
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

class ArtObjectRenderer : DefaultClusterRenderer<ArtObjectClusterItem> {
    private val itemView: View
    private val iconGenerator: IconGenerator

    constructor(context: Context?, map: GoogleMap?, clusterManager: ClusterManager<ArtObjectClusterItem>?) : super(context, map, clusterManager) {
        iconGenerator = IconGenerator(context!!.applicationContext);
        itemView = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.art_object_cluster_item, null);

        iconGenerator.setContentView(itemView)
    }

    override fun onBeforeClusterItemRendered(item: ArtObjectClusterItem?, markerOptions: MarkerOptions?) {
        itemView.image.loadImage(item!!.artObjectUi.thumbPicUrl)
        itemView.text.visibility = View.GONE
        val icon = iconGenerator.makeIcon()
        markerOptions!!.icon(BitmapDescriptorFactory.fromBitmap(icon))
    }

    override fun onBeforeClusterRendered(cluster: Cluster<ArtObjectClusterItem>?, markerOptions: MarkerOptions?) {
        itemView.image.loadImage(cluster!!.items.first().artObjectUi.thumbPicUrl)
        itemView.text.visibility = View.VISIBLE
        val icon = iconGenerator.makeIcon(cluster.size.toString())
        markerOptions!!.icon(BitmapDescriptorFactory.fromBitmap(icon))
    }

    override fun shouldRenderAsCluster(cluster: Cluster<ArtObjectClusterItem>?): Boolean = cluster!!.size > 1
}


