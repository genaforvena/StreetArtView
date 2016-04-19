package org.imozerov.streetartview.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable
import com.google.android.gms.analytics.Tracker
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.jakewharton.rxbinding.view.RxView
import kotlinx.android.synthetic.main.activity_detail.*
import org.imozerov.streetartview.R
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.storage.IDataSource
import org.imozerov.streetartview.ui.extensions.*
import org.imozerov.streetartview.ui.model.ArtObjectUi
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

class DetailArtObjectActivity : AppCompatActivity() {

    val PICK_IMAGE_REQUEST = 1

    @Inject
    lateinit var dataSource: IDataSource

    @Inject
    lateinit var tracker: Tracker

    private var artObjectId: String? = null
    private var artObjectUi: ArtObjectUi? = null

    val compositeSubscription: CompositeSubscription = CompositeSubscription()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        (application as StreetArtViewApp).appComponent.inject(this)

        artObjectId = intent.getStringExtra(EXTRA_KEY_ART_OBJECT_DETAIL_ID)
        artObjectUi = dataSource.getArtObject(artObjectId!!)

        tracker.sendScreen("DetailArtObjectActivity: ${artObjectUi!!.name}")

        art_object_detail_name.text = "\"${artObjectUi!!.name}\""
        art_object_detail_author.text = artObjectUi!!.authorsNames()
        if (artObjectUi!!.description.isBlank()) {
            art_object_detail_description.visibility = View.GONE
        } else {
            art_object_detail_description.text = artObjectUi!!.description
        }
        art_object_detail_address.text = artObjectUi!!.address
        setFavouriteIcon(artObjectUi!!.isFavourite)
        detail_share_button.setImageDrawable(getDrawableSafely(R.drawable.ic_share_black_24dp))

        val picsNumber = artObjectUi!!.picsUrls.size
        art_object_images_number.text = resources.getQuantityString(R.plurals.photos, picsNumber, picsNumber)

        art_object_detail_image.adapter = GalleryPagerAdapter(this, artObjectUi!!.picsUrls, { position ->
            val intent = Intent(this@DetailArtObjectActivity, ImageViewActivity::class.java)
            intent.putExtra(EXTRA_KEY_ART_OBJECT_DETAIL_ID, artObjectId)
            intent.putExtra(EXTRA_IMAGE_CHOSEN_IN_DETAILS, position)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        })

        val mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(art_object_detail_map.id, mapFragment).commit()
        mapFragment.getMapAsync {
            val artObjectLocation = LatLng(artObjectUi!!.lat, artObjectUi!!.lng)

            val userLocation = getCurrentLocation(this)
            if (userLocation != DEFAULT_USER_LOCATION) {
                art_object_detail_distance.text = "${userLocation.printableDistanceTo(artObjectLocation)} km"
                art_object_detail_distance.visibility = View.VISIBLE
            }

            with (it) {
                addUserLocationMarker(userLocation)
                addArtObjectSimpleMarker(artObjectUi!!)
                moveCamera(CameraUpdateFactory.newLatLngZoom(artObjectLocation, 14f))
            }
        }
    }

    override fun onStart() {
        super.onStart()

        compositeSubscription.add(
                RxView.clicks(detail_share_button).subscribe {
                    shareArtObjectInfo()
                }
        )
        compositeSubscription.add(
                RxView.clicks(detail_set_favourite_button).subscribe {
                    artObjectUi!!.isFavourite = !artObjectUi!!.isFavourite
                    dataSource.setFavourite(artObjectId!!, artObjectUi!!.isFavourite)
                    setFavouriteIcon(artObjectUi!!.isFavourite)
                }
        )
        compositeSubscription.add(
                RxView.clicks(art_object_detail_address).subscribe {
                    val latitude = artObjectUi!!.lat
                    val longitude = artObjectUi!!.lng
                    val label = artObjectUi!!.address
                    val uriBegin = "geo: $latitude, $longitude"
                    val query = "$latitude, $longitude ($label)"
                    val encodedQuery = Uri.encode(query)
                    val uriString = "$uriBegin?q=$encodedQuery&z=16"
                    val uri = Uri.parse(uriString)
                    val intent = Intent(android.content.Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                }
        )
    }

    override fun onStop() {
        super.onStop()
        compositeSubscription.clear()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                val position = data?.getIntExtra(ImageViewActivity.EXTRA_IMAGE_CHOSEN_IN_VIEWPAGER, 0)
                art_object_detail_image.currentItem = position!!
            }
        }
    }

    private fun shareArtObjectInfo() {

        val curImage: ImageView = art_object_detail_image
                .findViewWithTag(GalleryPagerAdapter.TAG_CURRENT_DETAIL_IMAGE) as ImageView
        val drawable = curImage.drawable
        val bitmap = (drawable as GlideBitmapDrawable).bitmap

        val path = MediaStore.Images.Media.insertImage(contentResolver,
                bitmap, "Street Art object temporary file", null)

        val uri = Uri.parse(path)

        val shareImageIntent = Intent()
        shareImageIntent.action = Intent.ACTION_SEND
        shareImageIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareImageIntent.putExtra(Intent.EXTRA_TEXT,
                "${resources.getString(R.string.street_art_object)}: ${artObjectUi!!.name} - ${artObjectUi!!.address}")
        shareImageIntent.type = "image/*"
        startActivity(Intent.createChooser(shareImageIntent, resources.getText(R.string.share)))

    }

    private fun setFavouriteIcon(isFavourite: Boolean) {
        if (isFavourite) {
            detail_set_favourite_button.setImageDrawable(getDrawableSafely(R.drawable.ic_favorite_black_24dp))
        } else {
            detail_set_favourite_button.setImageDrawable(getDrawableSafely(R.drawable.ic_favorite_border_black_24dp))
        }
    }

    companion object {
        val EXTRA_KEY_ART_OBJECT_DETAIL_ID = "EXTRA_KEY_ART_OBJECT_DETAIL_ID"
        val EXTRA_IMAGE_CHOSEN_IN_DETAILS = "EXTRA_IMAGE_CHOSEN_IN_DETAILS"
    }
}
