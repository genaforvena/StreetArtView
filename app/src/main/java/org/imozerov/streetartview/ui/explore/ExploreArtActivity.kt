package org.imozerov.streetartview.ui.explore

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView
import kotlinx.android.synthetic.main.activity_explore_art.*
import kotlinx.android.synthetic.main.bottom_details.*
import org.imozerov.streetartview.R
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.storage.IDataSource
import org.imozerov.streetartview.ui.detail.DetailArtObjectActivity
import org.imozerov.streetartview.ui.detail.interfaces.ArtObjectDetailOpener
import org.imozerov.streetartview.ui.explore.interfaces.ArtObjectDigestOpener
import org.imozerov.streetartview.ui.explore.interfaces.Filterable
import org.imozerov.streetartview.ui.explore.list.ArtListFragment
import org.imozerov.streetartview.ui.explore.map.ArtMapFragment
import org.imozerov.streetartview.ui.extensions.loadImage
import org.imozerov.streetartview.ui.model.ArtObjectUi
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription
import java.util.*
import javax.inject.Inject

class ExploreArtActivity : AppCompatActivity(), ArtObjectDetailOpener, ArtObjectDigestOpener {

    val TAG = "ExploreArtActivity"

    val compositeSubscription = CompositeSubscription()
    var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null

    @Inject
    lateinit var dataSource: IDataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate()")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore_art)
        (application as StreetArtViewApp).appComponent.inject(this)

        val adapter = Adapter(supportFragmentManager)
        adapter.addFragment(ArtMapFragment.newInstance(), getString(R.string.map_fragment_pager_label))
        adapter.addFragment(ArtListFragment.newInstance(), getString(R.string.list_fragment_pager_label))
        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet)
        viewpager.adapter = adapter
        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {
                // We're clearing every subscription but
                // we need to clear only search subscription.
                // This solution is not error-prone
                compositeSubscription.clear()
                createSearchSubscription()
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(p0: Int) {
            }
        })
        tabs.setupWithViewPager(viewpager)

        explore_floating_action_button.setOnClickListener { openSearchView() }
        search_view.findViewById(R.id.search_close_btn).setOnClickListener { closeSearchView() }
        viewpager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                hideArtObjectDigest()
            }
        })
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
        hideArtObjectDigest()
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

    override fun showArtObjectDigest(id: String) {
        Log.v(TAG, "showArtObjectDigest($id)")
        var artObject: ArtObjectUi = dataSource.getArtObject(id)
        bottom_detail_author.text = artObject.authorsNames()
        bottom_detail_name.text = artObject.name
        if (artObject.picsUrls.isEmpty()) {
            bottom_detail_image.setImageDrawable(null)
        } else {
            bottom_detail_image.loadImage(artObject.picsUrls[0])
        }

        bottom_sheet.visibility = View.VISIBLE
        bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
        bottom_info_linear.setOnClickListener { openArtObjectDetails(artObject.id) }
    }

    override fun hideArtObjectDigest() {
        bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
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
