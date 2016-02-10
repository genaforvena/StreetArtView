package org.imozerov.streetartview.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

import org.imozerov.streetartview.R
import org.imozerov.streetartview.ui.catalog.ArtListFragment
import org.imozerov.streetartview.ui.helper.replaceFragment
import org.imozerov.streetartview.ui.map.MapFragment

class MainActivity : AppCompatActivity() {
    val MAIN_CONTENT_TAG = "MainContent"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        replaceMainContentWith(ArtListFragment.newInstance())

        floating_button.setOnClickListener { v -> swapFragments() }
    }

    private fun swapFragments() {
        val currentFragment = supportFragmentManager.findFragmentByTag(MAIN_CONTENT_TAG)
        if (currentFragment is ArtListFragment) {
            replaceMainContentWith(MapFragment.newInstance())
        } else if (currentFragment is MapFragment) {
            replaceMainContentWith(ArtListFragment.newInstance())
        } else {
            throw RuntimeException("Unknown fragment in main content! " + currentFragment);
        }
    }

    private fun replaceMainContentWith(fragment: Fragment) {
        replaceFragment(R.id.main_content, fragment, MAIN_CONTENT_TAG)
    }
}
