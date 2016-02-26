package org.imozerov.streetartview.ui.detail

import android.content.Context
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.art_object_gallery_item.view.*
import org.imozerov.streetartview.R
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.storage.DataSource
import org.imozerov.streetartview.ui.extensions.addArtObject
import javax.inject.Inject

/**
 * Created by sergei on 08.02.16.
 */
class DetailArtObjectActivity : AppCompatActivity() {
    companion object {
        val EXTRA_KEY_ART_OBJECT_DETAIL_ID = "EXTRA_KEY_ART_OBJECT_DETAIL_ID"
    }

    val TAG = "DetailArtObjectActivity"

    @Inject
    lateinit var dataSource: DataSource

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        (application as StreetArtViewApp).appComponent.inject(this)

        val artObjectId = intent.getStringExtra(EXTRA_KEY_ART_OBJECT_DETAIL_ID)

        val artObjectUi = dataSource.getArtObject(artObjectId)
        art_object_detail_name.text = artObjectUi.name
        art_object_detail_author.text = artObjectUi.authorsNames()
        art_object_detail_description.text = artObjectUi.description

        art_object_detail_image_pager.adapter = GalleryPagerAdapter(this, artObjectUi.picsUrls)

        val mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(art_object_detail_map.id, mapFragment).commit()
        mapFragment.getMapAsync {
            it.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(artObjectUi.lat, artObjectUi.lng), 14f))
            it.addArtObject(artObjectUi)
        }
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
            container.addView(itemView)

            val thumbnailSize = (art_object_detail_image_pager.layoutParams as FrameLayout.LayoutParams).bottomMargin

            val params = LinearLayout.LayoutParams(thumbnailSize, thumbnailSize)
            params.setMargins(0, 0, 0, 0);

            val thumbView = ImageView(context)
            thumbView.scaleType = ImageView.ScaleType.CENTER_CROP
            thumbView.layoutParams = params
            thumbView.setOnClickListener {
                art_object_detail_image_pager.currentItem = position
            }
            art_object_detail_image_picker.addView(thumbView)

            Picasso.with(context).load(images[position]).into(itemView.image_preview)
            Picasso.with(context).load(images[position]).into(thumbView)

            return itemView
        }

        override fun destroyItem(container: ViewGroup, position: Int, anyObject: Any) {
            container.removeView(anyObject as LinearLayout)
        }
    }

}
