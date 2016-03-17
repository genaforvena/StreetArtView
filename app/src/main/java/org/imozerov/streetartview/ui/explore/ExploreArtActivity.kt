package org.imozerov.streetartview.ui.explore

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView
import kotlinx.android.synthetic.main.activity_explore_art.*
import org.imozerov.streetartview.R
import org.imozerov.streetartview.storage.IDataSource
import org.imozerov.streetartview.ui.detail.DetailArtObjectActivity
import org.imozerov.streetartview.ui.detail.interfaces.ArtObjectDetailOpener
import org.imozerov.streetartview.ui.explore.interfaces.Filterable
import org.imozerov.streetartview.ui.explore.list.ArtListFragment
import org.imozerov.streetartview.ui.explore.map.ArtMapFragment
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription
import java.util.*
import javax.inject.Inject

class ExploreArtActivity : AppCompatActivity(), ArtObjectDetailOpener {

    val TAG = "ExploreArtActivity"

    val compositeSubscription = CompositeSubscription()

    @Inject
    lateinit var dataSource: IDataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate()")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore_art)

        val adapter = Adapter(supportFragmentManager)
        adapter.addFragment(ArtMapFragment.newInstance(), getString(R.string.map_fragment_pager_label))
        adapter.addFragment(ArtListFragment.newInstance(), getString(R.string.list_fragment_pager_label))
        viewpager.adapter = adapter
        viewpager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageScrollStateChanged(p0: Int) {
                // We're clearing every subscription but
                // we need to clear only search subscription.
                // This solution is not error-prone
                compositeSubscription.clear()
                createSearchSubscription()
                getMapFragmentIfCurrentOrNull()?.hideArtObjectDigest()
            }
        })
        tabs.setupWithViewPager(viewpager)

        explore_floating_action_button.setOnClickListener { openSearchView() }
        search_view.findViewById(R.id.search_close_btn).setOnClickListener { closeSearchView() }
    }

    override fun onStart() {
        super.onStart()
        createSearchSubscription()
    }

    override fun onStop() {
        super.onStop()
        compositeSubscription.clear()
    }

    override fun onBackPressed() {
        if (getMapFragmentIfCurrentOrNull()?.onBackPressed()!!) {
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

    private fun createSearchSubscription() {
        val searchSubscription = RxSearchView
                .queryTextChanges(search_view)
                .doOnNext { Log.v(TAG, "Filtering $it") }
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val fragment = supportFragmentManager
                            .findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewpager.currentItem)
                    (fragment as? Filterable)?.applyFilter(it.toString())
                }
        compositeSubscription.add(searchSubscription)
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
            return mFragmentTitles[position]
        }
    }
}

fun SearchView.animateToGone() {
    animate().translationY(height.toFloat())
            .alpha(0.0f)
            .setDuration(300)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    visibility = View.GONE
                    setQuery("", true)
                }
            });
}

fun SearchView.animateToVisible() {
    animate().translationY(0f)
            .alpha(1f)
            .setDuration(300)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    visibility = View.VISIBLE
                    isIconified = false
                    requestFocus()
                }
            });
}
