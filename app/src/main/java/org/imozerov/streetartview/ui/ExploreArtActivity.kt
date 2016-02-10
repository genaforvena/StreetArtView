package org.imozerov.streetartview.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_explore_art.*

import org.imozerov.streetartview.R
import org.imozerov.streetartview.ui.catalog.ArtListFragment
import org.imozerov.streetartview.ui.detail.ArtObjectDetailOpener
import org.imozerov.streetartview.ui.detail.DetailArtObjectActivity
import org.imozerov.streetartview.ui.map.ArtMapFragment
import java.util.*

class ExploreArtActivity : AppCompatActivity(), ArtObjectDetailOpener {
    val TAG = "ExploreArtActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate()")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore_art)

        val adapter = Adapter(supportFragmentManager)
        adapter.addFragment(ArtListFragment.newInstance(), "List")
        adapter.addFragment(ArtMapFragment.newInstance(), "Map")
        viewpager.adapter = adapter
        tabs.setupWithViewPager(viewpager)
    }

    override fun openArtObjectDetails(id: String?) {
        Log.d(TAG, "openArtObjectDetails($id)")
        val intent = Intent(this, DetailArtObjectActivity::class.java)
        intent.putExtra(DetailArtObjectActivity.EXTRA_KEY_ART_OBJECT_DETAIL_ID, id)
        startActivity(intent)
    }

    internal class Adapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        private val mFragments = ArrayList<Fragment>()
        private val mFragmentTitles = ArrayList<String>()

        fun addFragment(fragment: Fragment, title: String) {
            mFragments.add(fragment)
            mFragmentTitles.add(title)
        }

        override fun getItem(position: Int): Fragment {
            return mFragments[position]
        }

        override fun getCount(): Int {
            return mFragments.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitles[position]
        }
    }

}
