package org.imozerov.streetartview.ui.detail

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
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
import rx.Observable
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

class DetailArtObjectActivity : AppCompatActivity() {

    val PICK_IMAGE_REQUEST = 1

    @Inject
    lateinit var dataSource: IDataSource
    @Inject
    lateinit var tracker: Tracker
    @Inject
    lateinit var prefs: SharedPreferences

    private val artObjectUi by lazy { dataSource.getArtObject(intent.getStringExtra(EXTRA_KEY_ART_OBJECT_DETAIL_ID)) }

    val compositeSubscription: CompositeSubscription = CompositeSubscription()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        (application as StreetArtViewApp).appComponent!!.inject(this)

        tracker.sendScreen("DetailArtObjectActivity: ${artObjectUi.name}")

        bindViews()
        initImagesGallery()
        setupMapAndDistanceTo()
    }

    override fun onStart() {
        super.onStart()

        compositeSubscription.addAll(
                RxView.clicks(detail_share_button).subscribe { shareArtObjectInfo() },
                RxView.clicks(detail_set_favourite_button).subscribe { changeFavouriteStatus() },
                RxView.clicks(art_object_detail_address).subscribe { showInGoogleMaps() }
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

    private fun bindViews() {
        art_object_detail_name.text = "\"${artObjectUi.name}\""
        art_object_detail_author.text = artObjectUi.authorsNames()
        if (artObjectUi.description.isBlank()) {
            art_object_detail_description.visibility = View.GONE
        } else {
            art_object_detail_description.text = artObjectUi.description
        }

        art_object_detail_address.text = artObjectUi.address
        setFavouriteIcon(artObjectUi.isFavourite)

        val picsNumber = artObjectUi.picsUrls.size
        if (picsNumber > 1) {
            art_object_images_number.text = resources.getQuantityString(R.plurals.photos, picsNumber, picsNumber)
        } else {
            art_object_images_number.visibility = View.GONE
        }
    }

    private fun initImagesGallery() {
        art_object_detail_image.adapter = GalleryPagerAdapter(this, artObjectUi.picsUrls, { position ->
            val intent = Intent(this@DetailArtObjectActivity, ImageViewActivity::class.java)
            intent.putExtra(EXTRA_KEY_ART_OBJECT_DETAIL_ID, artObjectUi.id)
            intent.putExtra(EXTRA_IMAGE_CHOSEN_IN_DETAILS, position)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        })
    }

    private fun setupMapAndDistanceTo() {
        val mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(art_object_detail_map.id, mapFragment).commit()
        mapFragment.getMapAsync {
            val artObjectLocation = LatLng(artObjectUi.lat, artObjectUi.lng)

            val userLocation = prefs.getCachedLocation()
            if (userLocation != NIZHNY_NOVGOROD_LOCATION) {
                art_object_detail_distance.text = "${userLocation.printableDistanceTo(artObjectLocation)} km"
                art_object_detail_distance.visibility = View.VISIBLE
            }

            with (it) {
                addUserLocationMarker(userLocation)
                addArtObjectSimpleMarker(artObjectUi)
                moveCamera(CameraUpdateFactory.newLatLngZoom(artObjectLocation, 14f))
            }
        }
    }

    private fun shareArtObjectInfo() {
        tracker.sendScreen("Sharing ${artObjectUi.name}")
        Observable.create<Uri> {
            val image = Glide.with(applicationContext)
                    .load(artObjectUi.picsUrls[art_object_detail_image.currentItem])
                    .asBitmap()
                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get()
            val path = MediaStore.Images.Media.insertImage(contentResolver, image, "Street Art object temporary file", null)
            it.onNext(Uri.parse(path))
        }
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe {
                    val shareImageIntent = Intent()
                    shareImageIntent.action = Intent.ACTION_SEND
                    shareImageIntent.putExtra(Intent.EXTRA_STREAM, it)
                    shareImageIntent.putExtra(Intent.EXTRA_TEXT, "${artObjectUi.authorsNames()} - ${artObjectUi.name} \n${artObjectUi.address}")
                    shareImageIntent.type = "image/*"
                    startActivity(Intent.createChooser(shareImageIntent, resources.getText(R.string.share)))
                }
    }

    private fun showInGoogleMaps() {
        tracker.sendScreen("Navigation to ${artObjectUi.name}")
        val latitude = artObjectUi.lat
        val longitude = artObjectUi.lng
        val label = artObjectUi.address
        val uriBegin = "geo: $latitude, $longitude"
        val query = "$latitude, $longitude ($label)"
        val encodedQuery = Uri.encode(query)
        val uriString = "$uriBegin?q=$encodedQuery&z=16"
        val uri = Uri.parse(uriString)
        val intent = Intent(android.content.Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    private fun changeFavouriteStatus() {
        artObjectUi.isFavourite = !artObjectUi.isFavourite
        dataSource.setFavourite(artObjectUi.id, artObjectUi.isFavourite)
        setFavouriteIcon(artObjectUi.isFavourite)
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
