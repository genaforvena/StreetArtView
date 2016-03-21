package org.imozerov.streetartview.ui.explore

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView
import com.jakewharton.rxbinding.view.RxView
import kotlinx.android.synthetic.main.activity_explore_art.*
import org.imozerov.streetartview.R
import org.imozerov.streetartview.ui.detail.DetailArtObjectActivity
import org.imozerov.streetartview.ui.detail.interfaces.ArtObjectDetailOpener
import org.imozerov.streetartview.ui.explore.favourites.FavouritesListFragment
import org.imozerov.streetartview.ui.explore.interfaces.Filterable
import org.imozerov.streetartview.ui.explore.list.ArtListFragment
import org.imozerov.streetartview.ui.explore.map.ArtMapFragment
import org.imozerov.streetartview.ui.extensions.addAll
import org.imozerov.streetartview.ui.extensions.animateToGone
import org.imozerov.streetartview.ui.extensions.animateToVisible
import rx.subscriptions.CompositeSubscription
import java.util.*

class ExploreArtActivity : AppCompatActivity(), ArtObjectDetailOpener {

    val TAG = "ExploreArtActivity"

    val compositeSubscription = CompositeSubscription()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate()")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore_art)

        val adapter = Adapter(supportFragmentManager)
        adapter.addFragment(ArtMapFragment.newInstance(), getString(R.string.map_fragment_pager_label))
        adapter.addFragment(ArtListFragment.newInstance(), getString(R.string.list_fragment_pager_label))
        adapter.addFragment(FavouritesListFragment.newInstance(), getString(R.string.favourites_fragment_pager_label))
        viewpager.adapter = adapter
        viewpager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageScrollStateChanged(p0: Int) {
                compositeSubscription.clear()
                rxInit()
            }
        })
        tabs.setupWithViewPager(viewpager)
        tabs.getTabAt(0)?.icon = getDrawable(R.drawable.ic_explore_black_36dp)
        tabs.getTabAt(1)?.icon = getDrawable(R.drawable.ic_visibility_black_36dp)
        tabs.getTabAt(2)?.icon = getDrawable(R.drawable.ic_favorite_black_36dp)
    }

    override fun onStart() {
        super.onStart()
        rxInit()
    }

    private fun rxInit() {
        compositeSubscription.addAll(
                RxView.clicks(explore_floating_action_button)
                        .subscribe { openSearchView() },

                RxView.clicks(search_view.findViewById(R.id.search_close_btn))
                        .subscribe { closeSearchView() },

                RxSearchView.queryTextChanges(search_view)
                        .subscribe { applyFilter(it) }
        )
    }

    override fun onStop() {
        super.onStop()
        compositeSubscription.clear()
    }

    override fun onBackPressed() {
        if (getMapFragmentIfCurrentOrNull()?.onBackPressed() == true) {
            return
        }

        if (search_view.visibility == View.VISIBLE) {
            closeSearchView()
            return
        }
        super.onBackPressed()
    }

    override fun openArtObjectDetails(id: String?) {
        Log.d(TAG, "openArtObjectDetails($id)")
        val intent = Intent(this, DetailArtObjectActivity::class.java)
        intent.putExtra(DetailArtObjectActivity.EXTRA_KEY_ART_OBJECT_DETAIL_ID, id)
        startActivity(intent)
    }

    private fun applyFilter(query: CharSequence) {
        val fragment = supportFragmentManager
                .findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewpager.currentItem)
        (fragment as? Filterable)?.applyFilter(query.toString())
    }

    private fun openSearchView() {
        getMapFragmentIfCurrentOrNull()?.hideArtObjectDigest()
        explore_floating_action_button.hide()
        search_view.animateToVisible()
    }

    private fun closeSearchView() {
        hideKeyboard()
        explore_floating_action_button.show()
        search_view.animateToGone()
    }

    private fun hideKeyboard() {
        if (currentFocus != null) {
            val inputMgr = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMgr.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }

    private fun getMapFragmentIfCurrentOrNull(): ArtMapFragment? {
        val currentFragment = (viewpager.adapter as FragmentPagerAdapter).getItem(viewpager.currentItem)
        if (currentFragment is ArtMapFragment) {
            return currentFragment
        }

        return null
    }
}

private class Adapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
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
        return ""
    }
}
