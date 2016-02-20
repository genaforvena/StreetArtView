package org.imozerov.streetartview.ui.add

import android.app.Activity

import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.bumptech.glide.Glide
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_add_art_object.*
import org.imozerov.streetartview.R
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.storage.DataSource
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by sergei on 18.02.16.
 */
class AddArtObjectActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    val TAG = "AddArtObjectActivity"

    val RESULT_LOAD_IMAGE = 1

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

        add_art_object_gallery_button.setOnClickListener(
                { openGalleryToSetImageForArtObjectWithId () }
        )

        add_art_object_save_button.setOnClickListener(
                {
                    if (lastLocation != null) {
                        dataSource.addArtObject(
                                add_art_object_name.text.toString(),
                                add_art_object_author.text.toString(),
                                selectedImageUrl,
                                lastLocation
                        )
                    }
                    finish()
                }
        )

        googleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener({})
                .addApi(LocationServices.API)
                .build();

    }

    override fun onStart() {
        googleApiClient?.connect();
        compositeSubscription = CompositeSubscription()
        super.onStart()
    }

    public override fun onStop() {
        googleApiClient?.disconnect();
        compositeSubscription!!.unsubscribe()
        super.onStop()
    }

    fun openGalleryToSetImageForArtObjectWithId() {
        val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, RESULT_LOAD_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            selectedImageUrl = data.data
            Glide.with(this).load(selectedImageUrl).into(add_art_object_gallery_button)
        }

    }

    override fun onConnected(p0: Bundle?) {
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
    }

    override fun onConnectionSuspended(p0: Int) {
        throw UnsupportedOperationException()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        throw UnsupportedOperationException()
    }
}