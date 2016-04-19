package org.imozerov.streetartview.ui.detail

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import org.imozerov.streetartview.ui.extensions.loadImage
import org.jetbrains.anko.imageView
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.onClick

/**
 * Created by imozerov on 11.03.16.
 */
class GalleryPagerAdapter(val context: Context, val images: List<String>, private val listenerAction: ((position: Int) -> (Unit))) : PagerAdapter() {

    override fun getCount() = images.size

    override fun isViewFromObject(view: View, anyObject: Any) = view === anyObject as LinearLayout

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        return container.linearLayout {
            imageView {
                onClick {
                    listenerAction(position)
                }
                lparams {
                    width = matchParent
                    height = matchParent
                }
                scaleType = ImageView.ScaleType.CENTER_CROP
            }.loadImage(images[position])
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, anyObject: Any) {
        container.removeView(anyObject as LinearLayout)
    }
}