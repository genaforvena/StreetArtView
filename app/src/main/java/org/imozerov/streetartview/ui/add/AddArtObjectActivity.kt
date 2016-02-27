package org.imozerov.streetartview.ui.add

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.bumptech.glide.Glide
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.realm.RealmList
import kotlinx.android.synthetic.main.activity_add_art_object.*
import org.imozerov.streetartview.R
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.storage.DataSource
import org.imozerov.streetartview.storage.model.RealmArtObject
import org.imozerov.streetartview.storage.model.RealmAuthor
import org.imozerov.streetartview.storage.model.RealmLocation
import org.imozerov.streetartview.storage.model.RealmString
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject


/**
 * Created by sergei on 18.02.16.
 */
class AddArtObjectActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    val TAG = "AddArtObjectActivity"

    val FRAGMENT_TAG = "MapFragmentInAdd"

    val RESULT_LOAD_IMAGE_FROM_GALLERY = 1
    val RESULT_CAPTURE_IMAGE_WITH_CAMERA = 2

    @Inject
    lateinit var dataSource: DataSource

    private var compositeSubscription: CompositeSubscription? = null

    private var selectedImageUrl: Uri? = null

    private var googleApiClient: GoogleApiClient? = null
    private var lastLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate()")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_art_object)

        (application as StreetArtViewApp).appComponent.inject(this)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.add_art_object_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        add_art_object_from_gallery_button.setOnClickListener(
                { openGalleryToSetImageForArtObjectWithId () }
        )

        add_art_object_camera_button.setOnClickListener(
                { startCameraToSetImageForArtObjectWithId () }
        )

        add_art_object_save_button.setOnClickListener(
                {
                    val realmAuthor = RealmAuthor()
                    with (realmAuthor) {
                        id = SystemClock.currentThreadTimeMillis().toString()
                        name = add_art_object_author.text.toString()
                        photo = "http://photos.state.gov/libraries/media/788/images/500x500-sample.jpg"
                    }

                    val realmAuthors = RealmList<RealmAuthor>()
                    realmAuthors.add(realmAuthor)

                    val realmLocation = RealmLocation()
                    with(realmLocation) {
                        address = "Some address, 34"
                        lat = lastLocation!!.latitude
                        lng = lastLocation!!.longitude
                    }

                    val realmArtObject = RealmArtObject()
                    with (realmArtObject) {
                        authors = realmAuthors
                        description = add_art_object_description.text.toString()
                        name = add_art_object_name.text.toString()
                        id = SystemClock.currentThreadTimeMillis().toString()
                        thumbPicUrl = selectedImageUrl.toString()
                        picsUrls = RealmList<RealmString>()
                        location = realmLocation
                        println("Novikov lastLocation:" + lastLocation.toString())
                        //                        lat = 56.307872
                        //                        lng = 44.076207
                    }
                    dataSource.addArtObject(realmArtObject)
                    finish()
                }
        )

        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener({})
                    .addApi(LocationServices.API)
                    .build()
        }

    }

    override fun onStart() {
        googleApiClient?.connect()
        compositeSubscription = CompositeSubscription()
        super.onStart()
    }

    public override fun onStop() {
        googleApiClient?.disconnect()
        compositeSubscription!!.unsubscribe()
        super.onStop()
    }

    fun openGalleryToSetImageForArtObjectWithId() {
        val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, RESULT_LOAD_IMAGE_FROM_GALLERY)
    }

    fun startCameraToSetImageForArtObjectWithId() {
        val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, RESULT_CAPTURE_IMAGE_WITH_CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESULT_LOAD_IMAGE_FROM_GALLERY && resultCode == Activity.RESULT_OK && null != data) {
            selectedImageUrl = data.data
            Glide.with(this).load(selectedImageUrl).into(add_art_object_image)
        } else if (requestCode == RESULT_CAPTURE_IMAGE_WITH_CAMERA && resultCode == Activity.RESULT_OK && null != data) {
            selectedImageUrl = data.data
            Glide.with(this).load(selectedImageUrl).into(add_art_object_image)
        }

    }

    override fun onConnected(connectionHint: Bundle?) {
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
    }

    override fun onConnectionSuspended(p0: Int) {
        throw UnsupportedOperationException()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        throw UnsupportedOperationException()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        val lastLatLng = LatLng(lastLocation!!.latitude, lastLocation!!.longitude)
        googleMap?.addMarker(MarkerOptions().position(lastLatLng).title("Here you are"));
        googleMap?.moveCamera(CameraUpdateFactory.newLatLng(lastLatLng));
    }
}