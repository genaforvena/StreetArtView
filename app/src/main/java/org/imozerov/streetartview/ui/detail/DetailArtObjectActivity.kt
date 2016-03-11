package org.imozerov.streetartview.ui.detail

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_detail.*
import org.imozerov.streetartview.R
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.location.addUserLocationMarker
import org.imozerov.streetartview.location.getCurrentLocation
import org.imozerov.streetartview.storage.IDataSource
import org.imozerov.streetartview.ui.extensions.addArtObject
import javax.inject.Inject

/**
 * Created by sergei on 08.02.16.
 */
class DetailArtObjectActivity : AppCompatActivity() {
    val PICK_IMAGE_REQUEST = 1

    @Inject
    lateinit var dataSource: IDataSource

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        (application as StreetArtViewApp).appComponent.inject(this)

        val artObjectId = intent.getStringExtra(EXTRA_KEY_ART_OBJECT_DETAIL_ID)

        val artObjectUi = dataSource.getArtObject(artObjectId)
        art_object_detail_name.text = artObjectUi.name
        art_object_detail_author.text = artObjectUi.authorsNames()
        art_object_detail_description.text = artObjectUi.description

        val picsNumber = artObjectUi.picsUrls.size

        if (picsNumber > 0) {
            art_object_images_number.text = resources
                    .getQuantityString(R.plurals.photos, picsNumber, picsNumber)
            art_object_detail_pager.adapter = GalleryPagerAdapter(this, artObjectUi.picsUrls, { position ->
                val intent = Intent(this@DetailArtObjectActivity, ImageViewActivity::class.java)
                intent.putExtra(EXTRA_KEY_ART_OBJECT_DETAIL_ID, artObjectId)
                intent.putExtra(EXTRA_IMAGE_CHOSEN_IN_DETAILS, position)
                startActivityForResult(intent, PICK_IMAGE_REQUEST)
            })
        }

        val mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(art_object_detail_map.id, mapFragment).commit()
        mapFragment.getMapAsync {
            it.addUserLocationMarker(getCurrentLocation(this))
            it.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(artObjectUi.lat, artObjectUi.lng), 14f))
            it.addArtObject(artObjectUi)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                val position = data?.getIntExtra(ImageViewActivity.EXTRA_IMAGE_CHOSEN_IN_VIEWPAGER, 0)
                art_object_detail_pager.currentItem = position!!
            }
        }
    }

    companion object {
        val EXTRA_KEY_ART_OBJECT_DETAIL_ID = "EXTRA_KEY_ART_OBJECT_DETAIL_ID"
        val EXTRA_IMAGE_CHOSEN_IN_DETAILS = "EXTRA_IMAGE_CHOSEN_IN_DETAILS"
    }
}
