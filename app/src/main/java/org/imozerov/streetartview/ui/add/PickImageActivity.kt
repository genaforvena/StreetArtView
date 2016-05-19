package org.imozerov.streetartview.ui.add

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_add_art_object.*
import org.imozerov.streetartview.R
import org.imozerov.streetartview.StreetArtViewApp
import java.util.*

class PickImageActivity : AppCompatActivity(), OnImagePickListener {
    private val adapter by lazy { SdCardImagesAdapter(this) }

    private var pickedImageUri: Uri? = null

    val CAMERA_FOLDER_NAME = "Camera"
    val PICTURES_FOLDER_NAME = "Pictures"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as StreetArtViewApp).appComponent!!.inject(this)
        setContentView(R.layout.activity_add_art_object)

        setSupportActionBar(pick_image_toolbar)

        val layoutManager = GridLayoutManager(this, 3)
        adapter.setHasStableIds(true)
        sd_card_images.layoutManager = layoutManager
        sd_card_images.adapter = adapter

        displayImages(CAMERA_FOLDER_NAME)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.pick_image_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_proceed) {
            if (pickedImageUri == null) {
                Toast.makeText(this, getString(R.string.non_empty_photo), Toast.LENGTH_LONG).show()
            } else {
                val intent = Intent(this, FillActivity::class.java)
                intent.putExtra(FillActivity.EXTRA_PICKED_IMAGE_URI, pickedImageUri)
                startActivity(intent)
            }
        } else if (item?.itemId == R.id.action_camera) {
            displayImages(CAMERA_FOLDER_NAME)
        } else if (item?.itemId == R.id.action_pictures) {
            displayImages(PICTURES_FOLDER_NAME)
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
        pickedImageUri = Uri.withAppendedPath(IMAGES_PATH, imageId.toString())
        Glide.with(this).load(pickedImageUri).into(image_picked)
    }

    companion object {
        val IMAGES_PATH = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

}