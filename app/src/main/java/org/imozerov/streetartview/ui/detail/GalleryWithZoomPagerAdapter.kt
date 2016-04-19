package org.imozerov.streetartview.ui.detail

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.ImageView
import android.widget.LinearLayout
import org.imozerov.streetartview.ui.extensions.loadImage
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.matchParent
import uk.co.senab.photoview.PhotoView
import uk.co.senab.photoview.PhotoViewAttacher

/**
 * Created by imozerov on 11.03.16.
 */
class GalleryWithZoomPagerAdapter(val context: Context, val images: List<String>) : PagerAdapter() {
    override fun getCount() = images.size

    override fun isViewFromObject(view: View, anyObject: Any) = view === anyObject as LinearLayout

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = container.linearLayout {
            val imageView = photoView {
                scaleType = ImageView.ScaleType.FIT_CENTER
                lparams {
                    width = matchParent
                    height = matchParent
                }
            }
            PhotoViewAttacher(imageView)
            imageView.loadImage(images[position])
        }
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, anyObject: Any) {
        container.removeView(anyObject as LinearLayout)
    }
}

inline fun ViewManager.photoView(init: PhotoView.() -> Unit) = ankoView({ PhotoView(it) }, init)