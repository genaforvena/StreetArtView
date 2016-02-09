package org.imozerov.streetartview.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import org.imozerov.streetartview.R
import org.imozerov.streetartview.ui.catalog.ArtListFragment
import org.imozerov.streetartview.ui.helper.replaceFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        replaceFragment(R.id.main_content, ArtListFragment.newInstance())
    }
}
