package org.imozerov.streetartview.ui.explore.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.support.v4.content.ContextCompat
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator
import com.google.maps.android.ui.SquareTextView
import org.imozerov.streetartview.R

/**
 * Created by imozerov on 31.05.16.
 */
class ArtObjectRenderer(context: Context, map: GoogleMap, clusterManager: ClusterManager<ArtObjectClusterItem>) : DefaultClusterRenderer<ArtObjectClusterItem>(context, map, clusterManager) {
    private val iconItemBlack: Bitmap
    private val iconItemWhite: Bitmap
    private val iconGenerator: IconGenerator
    private val density: Float

    init {
        density = context.resources.displayMetrics.density

        iconGenerator = CachedIconGenerator(context)
        iconGenerator.setContentView(makeSquareTextView(context, CLUSTER_PADDING))
        iconGenerator.setTextAppearance(com.google.maps.android.R.style.ClusterIcon_TextAppearance)

        val iconItemGenerator = IconGenerator(context)
        iconItemGenerator.setContentView(makeSquareTextView(context, ITEM_PADDING))
        iconItemGenerator.setBackground(makeClusterBackground(ContextCompat.getColor(context, R.color.black)))
        iconItemBlack = iconItemGenerator.makeIcon()

        iconItemGenerator.setBackground(makeClusterBackground(ContextCompat.getColor(context, R.color.white)))
        iconItemWhite = iconItemGenerator.makeIcon()
    }

    override fun onBeforeClusterItemRendered(item: ArtObjectClusterItem?, markerOptions: MarkerOptions?) {
        markerOptions!!.icon(BitmapDescriptorFactory.fromBitmap(iconItemBlack))
    }

    override fun onBeforeClusterRendered(cluster: Cluster<ArtObjectClusterItem>, markerOptions: MarkerOptions) {
        val clusterSize = getBucket(cluster)

        iconGenerator.setBackground(makeClusterBackground(getColor(clusterSize)))
        val descriptor = BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon(getClusterText(clusterSize)))
        markerOptions.icon(descriptor)
    }

    override fun shouldRenderAsCluster(cluster: Cluster<ArtObjectClusterItem>): Boolean {
        // Always render clusters.
        return cluster.size > 4
    }

    override fun getColor(clusterSize: Int): Int {
        val size = Math.min(clusterSize.toFloat(), 300.0f)
        val hue = (300.0f - size) * (300.0f - size) / 90000.0f * 220.0f
        return Color.HSVToColor(floatArrayOf(hue, 1.0f, 0.6f))
    }

    fun selectItem(artObjectClusterItem: ArtObjectClusterItem?) {
        getMarker(artObjectClusterItem)?.setIcon(BitmapDescriptorFactory.fromBitmap(iconItemWhite))
    }

    fun deselectItem(artObjectClusterItem: ArtObjectClusterItem?) {
        getMarker(artObjectClusterItem)?.setIcon(BitmapDescriptorFactory.fromBitmap(iconItemBlack))
    }

    private fun makeClusterBackground(color: Int): LayerDrawable {
        val coloredCircleBackground = ShapeDrawable(OvalShape())
        coloredCircleBackground.paint.color = color
        val outline = ShapeDrawable(OvalShape())
        outline.paint.color = 0x80ffffff.toInt()
        val background = LayerDrawable(arrayOf<Drawable>(outline, coloredCircleBackground))
        val strokeWidth = (density * 3.0f).toInt()
        background.setLayerInset(1, strokeWidth, strokeWidth, strokeWidth, strokeWidth)
        return background
    }

    private fun makeSquareTextView(context: Context, padding: Int): SquareTextView {
        val squareTextView = SquareTextView(context)
        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        squareTextView.layoutParams = layoutParams
        squareTextView.id = R.id.text
        val paddingDpi = (padding * density).toInt()
        squareTextView.setPadding(paddingDpi, paddingDpi, paddingDpi, paddingDpi)
        return squareTextView
    }

    companion object {
        private val CLUSTER_PADDING = 12
        private val ITEM_PADDING = 3
    }
}
