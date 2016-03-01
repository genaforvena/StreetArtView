package org.imozerov.streetartview.ui.detail

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_image_view.*
import org.imozerov.streetartview.R
import org.imozerov.streetartview.ui.extensions.loadImage


class ImageViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)

        val imageUrl = intent.getStringExtra(INTENT_EXTRA_IMAGE_URL);
        image_view_full_screen.loadImage(imageUrl)
        image_view_full_screen.setOnClickListener{ finish() }
    }

    companion object {
        val INTENT_EXTRA_IMAGE_URL = "image_url"
    }
}
