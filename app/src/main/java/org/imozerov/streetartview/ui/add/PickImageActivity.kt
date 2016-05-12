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
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_add_art_object.*
import org.imozerov.streetartview.R
import org.imozerov.streetartview.StreetArtViewApp
import java.util.*
import javax.inject.Inject

class PickImageActivity : AppCompatActivity(), OnImagePickListener {
    private val adapter by lazy { SdCardImagesAdapter(this) }

    @Inject
    lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as StreetArtViewApp).appComponent!!.inject(this)
        setContentView(R.layout.activity_add_art_object)

        setSupportActionBar(pick_image_toolbar);

        val layoutManager = GridLayoutManager(this, 3)
        adapter.setHasStableIds(true)
        sd_card_images.layoutManager = layoutManager
        sd_card_images.adapter = adapter

        displayImages("Camera")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.pick_image_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_proceed) {
            startActivity(Intent(this, AddInfoActivity::class.java))
        } else if (item?.itemId == R.id.action_camera) {
            displayImages("Camera")
        } else if (item?.itemId == R.id.action_pictures) {
            displayImages("Pictures")
        }
        return super.onOptionsItemSelected(item)
    }

    val IMAGES_ID = MediaStore.Images.Media._ID
    val DISPLAY_NAME = MediaStore.Images.Media.DISPLAY_NAME
    val BUCKET_ID = MediaStore.Images.Media.BUCKET_ID
    val BUCKET_DISPLAY_NAME = MediaStore.Images.Media.BUCKET_DISPLAY_NAME
    val DATE_TAKEN = MediaStore.Images.Media.DATE_TAKEN

    private fun displayImages(bucket: String?) {
        val projection = arrayOf<String>(IMAGES_ID,
                DISPLAY_NAME,
                BUCKET_ID,
                BUCKET_DISPLAY_NAME,
                DATE_TAKEN)

        val cursor = contentResolver.query(
                IMAGES_PATH,
                projection,
                null,
                null,
                DATE_TAKEN + " DESC")

        adapter.updateDataset(cursorToList(cursor, bucket))
    }

    private fun cursorToList(cursor: Cursor, bucket: String?): List<PictureItem> {
        val items = ArrayList<PictureItem>()

        if (cursor.moveToFirst()) {
            do {
                val bucketName = cursor.getString(cursor.getColumnIndex(BUCKET_DISPLAY_NAME))
                val id = cursor.getInt(cursor.getColumnIndex(IMAGES_ID))
                if (bucket.equals(bucketName)) {
                    items.add(PictureItem(id))
                }
            } while (cursor.moveToNext())
        }

        cursor.close()
        return items
    }

    override fun onImagePicked(imageId: Int) {
        val uri = Uri.withAppendedPath(IMAGES_PATH, imageId.toString())
        Glide.with(this).load(uri).into(image_picked)
        prefs.storeLastImageUri(uri)
    }

    companion object {
        val IMAGES_PATH = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

}