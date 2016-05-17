package org.imozerov.streetartview.ui.explore

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView
import com.jakewharton.rxbinding.view.RxView
import com.roughike.bottombar.BottomBar
import com.roughike.bottombar.BottomBarTab
import com.roughike.bottombar.OnTabClickListener
import kotlinx.android.synthetic.main.activity_explore_art.*
import org.imozerov.streetartview.BuildConfig
import org.imozerov.streetartview.R
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.location.LocationService
import org.imozerov.streetartview.ui.detail.DetailArtObjectActivity
import org.imozerov.streetartview.ui.detail.DetailArtObjectFragment
import org.imozerov.streetartview.ui.detail.interfaces.ArtObjectDetailOpener
import org.imozerov.streetartview.ui.explore.all.ArtListFragment
import org.imozerov.streetartview.ui.explore.favourites.FavouritesListFragment
import org.imozerov.streetartview.ui.explore.interfaces.Filterable
import org.imozerov.streetartview.ui.explore.map.ArtMapFragment
import org.imozerov.streetartview.ui.explore.sort.SortOrder
import org.imozerov.streetartview.ui.explore.sort.getSortOrder
import org.imozerov.streetartview.ui.explore.sort.swapSortOrder
import org.imozerov.streetartview.ui.extensions.addAll
import org.imozerov.streetartview.ui.extensions.animateToGone
import org.imozerov.streetartview.ui.extensions.animateToVisible
import org.imozerov.streetartview.ui.extensions.getDrawableSafely
import org.jetbrains.anko.toast
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

class ExploreArtActivity : AppCompatActivity(), ArtObjectDetailOpener {

    val TAG = "ExploreArtActivity"
    val CURRENT_FRAGMENT_TAG = "Current fragment"

    val compositeSubscription = CompositeSubscription()

    var bottomBar: BottomBar? = null

    @Inject
    lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate()")
        super.onCreate(savedInstanceState)
        (application as StreetArtViewApp).appComponent!!.inject(this)
        setContentView(R.layout.activity_explore_art)

        bottomBar = BottomBar.attach(this, savedInstanceState)
        initTabs()
        initSortOrderIcon(prefs.getSortOrder())
    }

    override fun onStart() {
        super.onStart()
        initRxSubscriptions()
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

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        bottomBar!!.onSaveInstanceState(outState)
    }

    override fun openArtObjectDetails(id: String?) {
        Log.d(TAG, "openArtObjectDetails($id)")
        val intent = Intent(this, DetailArtObjectActivity::class.java)
        intent.putExtra(DetailArtObjectFragment.EXTRA_KEY_ART_OBJECT_DETAIL_ID, id)
        startActivity(intent)
    }

    private fun initTabs() {
        bottomBar!!.setItems(
                BottomBarTab(getDrawableSafely(R.drawable.ic_compass), getString(R.string.map_fragment_pager_label)),
                BottomBarTab(getDrawableSafely(R.drawable.ic_eye), getString(R.string.list_fragment_pager_label)),
                BottomBarTab(getDrawableSafely(R.drawable.ic_star_white_24dp), getString(R.string.favourites_fragment_pager_label))
        )

        bottomBar!!.setOnTabClickListener(object : OnTabClickListener {
            override fun onTabReSelected(position: Int) {

            }

            override fun onTabSelected(position: Int) {
                val fragmentToShow: Fragment = if (position == 0) {
                    ArtMapFragment.newInstance()
                } else if (position == 1) {
                    ArtListFragment.newInstance()
                } else {
                    FavouritesListFragment.newInstance()
                }

                supportFragmentManager.beginTransaction().replace(R.id.main_content, fragmentToShow, CURRENT_FRAGMENT_TAG).commit()
                closeSearchView()

                if (explore_floating_action_button_sort_by.isShown) {
                    explore_floating_action_button_search.hide()
                    explore_floating_action_button_sort_by.hide()
                }
            }
        })
    }

    private fun initSortOrderIcon(sortOrder: Int) {
        if (sortOrder == SortOrder.byDate) {
            explore_floating_action_button_sort_by.setImageDrawable(getDrawableSafely(R.drawable.ic_schedule_black_24dp))
        } else if (sortOrder == SortOrder.byDistance) {
            explore_floating_action_button_sort_by.setImageDrawable(getDrawableSafely(R.drawable.ic_location_on_black_24dp))
        } else {
            val errorMsg = "Unknown sort order: $sortOrder"
            if (BuildConfig.DEBUG) {
                throw RuntimeException(errorMsg)
            }
            Log.e(TAG, errorMsg)
        }
    }

    private fun initRxSubscriptions() {
        compositeSubscription.addAll(
                RxView.clicks(explore_floating_action_button_expand).subscribe { swapFloatingActionButtonsVisibility() },
                RxView.clicks(explore_floating_action_button_search).subscribe { openSearchView() },
                RxView.clicks(explore_floating_action_button_sort_by).subscribe { changeSortOrder() },
                RxView.clicks(search_view.findViewById(R.id.search_close_btn)).subscribe { closeSearchView() },
                RxSearchView.queryTextChanges(search_view).subscribe { applyFilter(it) }
        )
    }

    private fun applyFilter(query: CharSequence) {
        val fragment = supportFragmentManager.findFragmentByTag(CURRENT_FRAGMENT_TAG)
        (fragment as? Filterable)?.applyFilter(query.toString())
    }

    private fun changeSortOrder() {
        val newSortOrder = prefs.swapSortOrder()
        initSortOrderIcon(newSortOrder)
        toast(SortOrder.getString(newSortOrder))
    }

    private fun swapFloatingActionButtonsVisibility() {
        if (!explore_floating_action_button_sort_by.isShown) {
            if (search_view.visibility != View.VISIBLE) {
                explore_floating_action_button_search.show()
            }
            explore_floating_action_button_sort_by.show()
        } else {
            explore_floating_action_button_search.hide()
            explore_floating_action_button_sort_by.hide()
        }
    }

    private fun openSearchView() {
        explore_floating_action_button_search.hide()
        search_view.animateToVisible()
    }

    private fun closeSearchView() {
        search_view.setQuery("", true)
        hideKeyboard()
        if (explore_floating_action_button_sort_by.isShown) {
            explore_floating_action_button_search.show()
        }
        search_view.animateToGone()
    }

    private fun hideKeyboard() {
        if (currentFocus != null) {
            val inputMgr = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMgr.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }
}
