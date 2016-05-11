package org.imozerov.streetartview.ui.add

import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_add_art_object.*
import org.imozerov.streetartview.R
import org.imozerov.streetartview.StreetArtViewApp
import javax.inject.Inject

class PickImageActivity : AppCompatActivity(), OnImagePickListener {
    private val adapter by lazy { SdCardImagesAdapter(this, cursor!!) }

    private var cursor: Cursor? = null

    @Inject
    lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as StreetArtViewApp).appComponent!!.inject(this)
        setContentView(R.layout.activity_add_art_object)

        setSupportActionBar(pick_image_toolbar);

        val list = arrayOf(MediaStore.Images.Media._ID)

        cursor = contentResolver.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, list, null, null, MediaStore.Images.Thumbnails._ID);

        val layoutManager = GridLayoutManager(this, 3)
        adapter.setHasStableIds(true)
        sd_card_images.layoutManager = layoutManager
        sd_card_images.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.pick_image_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_proceed) {
            startActivity(Intent(this, AddInfoActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onImagePicked(imageId: Int?) {
        // TODO this needs to be full image but not just thumbnail
        val uri = Uri.withAppendedPath(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, imageId.toString())
        image_picked.setImageURI(uri)
        prefs.storeLastImageUri(uri)
    }
}

interface OnImagePickListener {
    fun onImagePicked(imageId: Int?)
}
