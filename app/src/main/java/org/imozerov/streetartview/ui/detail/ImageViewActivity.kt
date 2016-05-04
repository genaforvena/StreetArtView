package org.imozerov.streetartview.ui.detail

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.ViewManager
import com.liuguangqiang.swipeback.SwipeBackLayout
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.storage.IDataSource
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.viewPager
import javax.inject.Inject

class ImageViewActivity : AppCompatActivity() {

    @Inject
    lateinit var dataSource: IDataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as StreetArtViewApp).appComponent!!.inject(this)

        val artObjectId = intent.getStringExtra(DetailArtObjectActivity.EXTRA_KEY_ART_OBJECT_DETAIL_ID)
        val artObjectUi = dataSource.getArtObject(artObjectId)
        val imageChosen = intent.getIntExtra(DetailArtObjectActivity.EXTRA_IMAGE_CHOSEN_IN_DETAILS, 0)

        linearLayout {
            swipeBackLayout {
                viewPager {
                    backgroundResource = android.R.color.black
                    lparams(width = matchParent, height = matchParent) {
                        gravity = Gravity.CENTER_VERTICAL
                        adapter = GalleryWithZoomPagerAdapter(this@ImageViewActivity, artObjectUi.picsUrls)
                        currentItem = imageChosen
                    }
                }
            }.setDragEdge(SwipeBackLayout.DragEdge.LEFT);
        }
    }

    companion object {
        val EXTRA_IMAGE_CHOSEN_IN_VIEWPAGER = "EXTRA_IMAGE_CHOSEN_IN_VIEWPAGER"
    }
}

inline fun ViewManager.swipeBackLayout(init: SwipeBackLayout.() -> Unit) = ankoView({ SwipeBackLayout(it) }, init)


