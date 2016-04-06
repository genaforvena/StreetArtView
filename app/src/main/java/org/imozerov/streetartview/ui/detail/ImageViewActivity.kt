package org.imozerov.streetartview.ui.detail

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_image_view.*
import org.imozerov.streetartview.R
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.storage.IDataSource
import javax.inject.Inject

class ImageViewActivity : AppCompatActivity() {

    @Inject
    lateinit var dataSource: IDataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)

        (application as StreetArtViewApp).appComponent.inject(this)

        val artObjectId = intent.getStringExtra(DetailArtObjectActivity.EXTRA_KEY_ART_OBJECT_DETAIL_ID)
        val artObjectUi = dataSource.getArtObject(artObjectId)

        image_viewpager.adapter = GalleryWithZoomPagerAdapter(this, artObjectUi.picsUrls)
        val imageChosen = intent.getIntExtra(DetailArtObjectActivity.EXTRA_IMAGE_CHOSEN_IN_DETAILS, 0)
        image_viewpager.currentItem = imageChosen
    }

    companion object {
        val EXTRA_IMAGE_CHOSEN_IN_VIEWPAGER = "EXTRA_IMAGE_CHOSEN_IN_VIEWPAGER"
    }
}