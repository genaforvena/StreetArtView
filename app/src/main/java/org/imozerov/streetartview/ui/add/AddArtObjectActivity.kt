package org.imozerov.streetartview.ui.add

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import org.imozerov.streetartview.R

/**
 * Created by sergei on 18.02.16.
 */
class AddArtObjectActivity : AppCompatActivity() {

    val TAG = "AddArtObjectActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate()")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_art_object)
    }

}