package org.imozerov.streetartview.ui.detail

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.art_object_gallery_item.view.*
import org.imozerov.streetartview.R
import org.imozerov.streetartview.ui.extensions.loadImage
import uk.co.senab.photoview.PhotoViewAttacher

/**
 * Created by imozerov on 11.03.16.
 */
class GalleryWithZoomPagerAdapter(val context: Context, val images: List<String>) : PagerAdapter() {
    val inflater: LayoutInflater by lazy {
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount() = images.size

    override fun isViewFromObject(view: View, anyObject: Any) = view === anyObject as LinearLayout

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = inflater.inflate(R.layout.art_object_gallery_item_with_zoom, container, false)
        (itemView.image_preview as ImageView).loadImage(images[position])
        PhotoViewAttacher(itemView.image_preview)
        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, anyObject: Any) {
        container.removeView(anyObject as LinearLayout)
    }
}