package org.imozerov.streetartview.ui.detail

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_image_view.*
import kotlinx.android.synthetic.main.art_object_gallery_item.view.*
import org.imozerov.streetartview.R
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.storage.DataSource
import org.imozerov.streetartview.ui.extensions.loadImage
import javax.inject.Inject

class ImageViewActivity : AppCompatActivity() {

    companion object {
        val EXTRA_CHOSEN_IMAGE = "EXTRA_CHOSEN_IMAGE"
    }

    @Inject
    lateinit var dataSource: DataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)

        (application as StreetArtViewApp).appComponent.inject(this)

        val artObjectId = intent.getStringExtra(DetailArtObjectActivity.EXTRA_KEY_ART_OBJECT_DETAIL_ID)

        val artObjectUi = dataSource.getArtObject(artObjectId)

        image_view_full_screen.adapter = GalleryPagerAdapter(this, artObjectUi.picsUrls)
    }

    internal inner class GalleryPagerAdapter(val context: Context, val images: List<String>) : PagerAdapter() {
        var inflater: LayoutInflater

        init {
            inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }

        override fun getCount(): Int {
            return images.size
        }

        override fun isViewFromObject(view: View, anyObject: Any): Boolean {
            return view === anyObject as LinearLayout
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val itemView = inflater.inflate(R.layout.art_object_gallery_item, container, false)
            itemView.image_preview.loadImage(images[position])
            itemView.image_preview.setOnClickListener {
                val returnIntent = Intent()
                returnIntent.putExtra(EXTRA_CHOSEN_IMAGE, position)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
            container.addView(itemView)
            return itemView
        }

        override fun destroyItem(container: ViewGroup, position: Int, anyObject: Any) {
            container.removeView(anyObject as LinearLayout)
        }
    }

}
