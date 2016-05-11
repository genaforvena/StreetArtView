package org.imozerov.streetartview.ui.add

import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_add_info.*

import org.imozerov.streetartview.R
import org.imozerov.streetartview.StreetArtViewApp
import javax.inject.Inject

class AddInfoActivity : AppCompatActivity() {

    @Inject
    lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as StreetArtViewApp).appComponent!!.inject(this)
        setContentView(R.layout.activity_add_info)
        setSupportActionBar(add_info_toolbar)
        add_info_image_picked.setImageURI(prefs.getLastImageUri())
    }
}
