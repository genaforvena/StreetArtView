package org.imozerov.streetartview.ui.detail

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.art_object_gallery_item.view.*
import org.imozerov.streetartview.R
import org.imozerov.streetartview.ui.extensions.loadImage

class GalleryPagerAdapter(val context: Context, val images: List<String>, listenerAction: ((position: Int) -> (Unit))) : PagerAdapter() {

    val inflater: LayoutInflater
    val listenerAction: ((Int) -> (Unit))

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this.listenerAction = listenerAction
    }

    override fun getCount() = images.size

    override fun isViewFromObject(view: View, anyObject: Any) = view === anyObject as LinearLayout

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = inflater.inflate(R.layout.art_object_gallery_item, container, false)
        itemView.image_preview.loadImage(images[position])
        itemView.image_preview.setOnClickListener {
            listenerAction(position)
        }
        itemView.image_preview.tag = TAG_CURRENT_DETAIL_IMAGE
        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, anyObject: Any) {
        container.removeView(anyObject as LinearLayout)
    }

    companion object {
        val TAG_CURRENT_DETAIL_IMAGE = "TAG_CURRENT_DETAIL_IMAGE"
    }

}