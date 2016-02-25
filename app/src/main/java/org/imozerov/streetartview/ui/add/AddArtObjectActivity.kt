package org.imozerov.streetartview.ui.add

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import io.realm.RealmList
import kotlinx.android.synthetic.main.activity_add_art_object.*
import kotlinx.android.synthetic.main.fragment_art_map.*
import org.imozerov.streetartview.R
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.storage.DataSource
import org.imozerov.streetartview.storage.model.RealmArtObject
import org.imozerov.streetartview.storage.model.RealmAuthor
import org.imozerov.streetartview.storage.model.RealmString
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by sergei on 18.02.16.
 */
class AddArtObjectActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

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

        val mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager
                .beginTransaction()
                .replace(add_art_object_map.id, mapFragment, FRAGMENT_TAG)
                .commit()

        mapFragment.getMapAsync {
            it.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(56.3298063, 44.0095144), 14f))
        }

        add_art_object_from_gallery_button.setOnClickListener(
                { openGalleryToSetImageForArtObjectWithId () }
        )

        add_art_object_camera_button.setOnClickListener(
                { startCameraToSetImageForArtObjectWithId () }
        )

        add_art_object_save_button.setOnClickListener(
                {
                    println("Novikov setOnClickListener")

                    val realmAuthor = RealmAuthor()
                    with (realmAuthor) {
                        id = SystemClock.currentThreadTimeMillis().toString()
                        name = add_art_object_author.text.toString()
                        description = "Some author"
                    }

                    val realmArtObject = RealmArtObject()
                    with (realmArtObject) {
                        author = realmAuthor
                        description = add_art_object_description.text.toString()
                        name = add_art_object_name.text.toString()
                        id = SystemClock.currentThreadTimeMillis().toString()
                        thumbPicUrl = selectedImageUrl.toString()
                        picsUrls = RealmList<RealmString>()
                        println("Novikov lastLocation:" + lastLocation.toString())
                        lat = lastLocation!!.latitude
                        lng = lastLocation!!.longitude
                        //                        lat = 56.307872
                        //                        lng = 44.076207
                    }
                    dataSource.addArtObject(realmArtObject)
                    finish()
                }
        )

        googleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener({})
                .addApi(LocationServices.API)
                .build()

        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, object : LocationListener {
            override fun onLocationChanged(location: Location) {
                println("Novikov onLocationChanged")
                lastLocation = location

                val sequence = "lat: " + location.latitude + "; lng: " + location.longitude
                Toast.makeText(applicationContext, sequence, Toast.LENGTH_LONG)
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
                println("Novikov onStatusChanged")
            }

            override fun onProviderEnabled(provider: String) {
                println("Novikov onProviderEnabled")
            }

            override fun onProviderDisabled(provider: String) {
                println("Novikov onProviderDisabled")
            }
        })

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

    override fun onConnected(p0: Bundle?) {
    }

    override fun onConnectionSuspended(p0: Int) {
        throw UnsupportedOperationException()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        throw UnsupportedOperationException()
    }
}