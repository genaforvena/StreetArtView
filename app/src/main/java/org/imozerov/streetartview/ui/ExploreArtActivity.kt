package org.imozerov.streetartview.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_explore_art.*

import org.imozerov.streetartview.R
import org.imozerov.streetartview.ui.catalog.ArtListFragment
import org.imozerov.streetartview.ui.detail.ArtObjectDetailOpener
import org.imozerov.streetartview.ui.detail.DetailArtObjectActivity
import org.imozerov.streetartview.ui.helper.replaceFragment
import org.imozerov.streetartview.ui.map.ArtMapFragment

class ExploreArtActivity : AppCompatActivity(), ArtObjectDetailOpener {
    val MAIN_CONTENT_TAG = "MainContent"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore_art)

        if (savedInstanceState == null) {
            replaceMainContentWith(ArtListFragment.newInstance())
        }

        floating_button.setOnClickListener { v -> swapMainContent() }
    }

    private fun swapMainContent() {
        val currentFragment = supportFragmentManager.findFragmentByTag(MAIN_CONTENT_TAG)
        when (currentFragment) {
            is ArtListFragment -> replaceMainContentWith(ArtMapFragment.newInstance())
            is ArtMapFragment -> replaceMainContentWith(ArtListFragment.newInstance())
            else -> throw RuntimeException("Unknown fragment in main content! " + currentFragment);
        }
    }

    private fun replaceMainContentWith(fragment: Fragment) {
        replaceFragment(R.id.main_content, fragment, MAIN_CONTENT_TAG)
    }

    override fun openArtObjectDetails(id: String?) {
        val intent = Intent(this, DetailArtObjectActivity::class.java)
        intent.putExtra(DetailArtObjectActivity.EXTRA_KEY_ART_OBJECT_DETAIL_ID, id)
        startActivity(intent)
    }

}
