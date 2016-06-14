package org.imozerov.streetartview.ui.explore

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView
import com.jakewharton.rxbinding.view.RxView
import com.roughike.bottombar.BottomBar
import com.roughike.bottombar.BottomBarTab
import com.roughike.bottombar.OnTabClickListener
import kotlinx.android.synthetic.main.activity_explore_art.*
import org.imozerov.streetartview.R
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.ui.detail.DetailArtObjectActivity
import org.imozerov.streetartview.ui.detail.DetailArtObjectFragment
import org.imozerov.streetartview.ui.detail.interfaces.ArtObjectDetailOpener
import org.imozerov.streetartview.ui.explore.all.ArtListFragment
import org.imozerov.streetartview.ui.explore.favourites.FavouritesListFragment
import org.imozerov.streetartview.ui.explore.map.ArtMapFragment
import org.imozerov.streetartview.ui.explore.sort.SortOrder
import org.imozerov.streetartview.ui.explore.sort.getSortOrder
import org.imozerov.streetartview.ui.explore.sort.swapSortOrder
import org.imozerov.streetartview.ui.extensions.addAll
import org.imozerov.streetartview.ui.extensions.animateToGone
import org.imozerov.streetartview.ui.extensions.animateToVisible
import org.imozerov.streetartview.ui.extensions.getDrawableSafely
import org.imozerov.streetartview.ui.model.ArtObjectUi
import org.jetbrains.anko.toast
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

class ExploreArtActivity : AppCompatActivity(), ArtObjectDetailOpener {
    val TAG = "ExploreArtActivity"

    @Inject
    lateinit var prefs: SharedPreferences

    val compositeSubscription = CompositeSubscription()

    var mainMenu: Menu? = null
    var bottomBar: BottomBar? = null

    var artMapFragment: ArtMapFragment? = null
    var artListFragment: ArtListFragment? = null
    var favouritesListFragment: FavouritesListFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate()")
        super.onCreate(savedInstanceState)
        (application as StreetArtViewApp).appComponent!!.inject(this)
        setContentView(R.layout.activity_explore_art)

        initTopBar()
        initContentFragments()
        initNavigationTabs(savedInstanceState)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        mainMenu = menu
        mainMenu!!.findItem(R.id.action_sort)?.isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_search -> {
                openSearchView()
                true
            }
            R.id.action_sort -> {
                changeSortOrder()
                true
            }
            R.id.action_track_location -> {
                changeLocationTracking()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initTopBar() {
        setSupportActionBar(main_toolbar)
        initSortOrderIcon(prefs.getSortOrder())
    }

    private fun initContentFragments() {
        artMapFragment = supportFragmentManager.findFragmentByTag(ART_MAP_FRAGMENT_TAG) as? ArtMapFragment?
        artListFragment = supportFragmentManager.findFragmentByTag(ART_LIST_FRAGMENT_TAG) as? ArtListFragment?
        favouritesListFragment = supportFragmentManager.findFragmentByTag(FAVOURITES_LIST_FRAGMENT_TAG) as? FavouritesListFragment?

        if (artMapFragment == null) {
            artMapFragment = ArtMapFragment.newInstance()
            supportFragmentManager.beginTransaction()
                    .add(R.id.main_content, artMapFragment!!, ART_MAP_FRAGMENT_TAG)
                    .commit()
        }

        if (artListFragment == null) {
            artListFragment = ArtListFragment.newInstance()
            supportFragmentManager.beginTransaction()
                    .add(R.id.main_content, artListFragment!!, ART_LIST_FRAGMENT_TAG)
                    .commit()
        }

        if (favouritesListFragment == null) {
            favouritesListFragment = FavouritesListFragment.newInstance()
            supportFragmentManager.beginTransaction()
                    .add(R.id.main_content, favouritesListFragment!!, FAVOURITES_LIST_FRAGMENT_TAG)
                    .commit()
        }
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

    override fun openArtObjectDetails(artObjectUi: ArtObjectUi) {
        Log.d(TAG, "openArtObjectDetails($artObjectUi)")
        val intent = Intent(this, DetailArtObjectActivity::class.java)
        intent.putExtra(DetailArtObjectFragment.EXTRA_KEY_ART_OBJECT_DETAIL_ID, artObjectUi.id)
        intent.putExtra(DetailArtObjectActivity.EXTRA_KEY_NAME, artObjectUi.name)
        startActivity(intent)
    }

    private fun initNavigationTabs(savedInstanceState: Bundle?) {
        bottomBar = BottomBar.attach(this, savedInstanceState)

        bottomBar!!.setItems(
                BottomBarTab(getDrawableSafely(R.drawable.ic_compass), getString(R.string.map_fragment_pager_label)),
                BottomBarTab(getDrawableSafely(R.drawable.ic_eye), getString(R.string.list_fragment_pager_label)),
                BottomBarTab(getDrawableSafely(R.drawable.ic_star_white_24dp), getString(R.string.favourites_fragment_pager_label))
        )

        bottomBar!!.setOnTabClickListener(object : OnTabClickListener {
            override fun onTabReSelected(position: Int) {
                when (position) {
                    MAP_NAVIGATION_POSITION -> artMapFragment?.hideArtObjectDigest()
                    LIST_NAVIGATION_POSITION -> artListFragment?.scrollToTop()
                    FAVOURITES_NAVIGATION_POSITION -> favouritesListFragment?.scrollToTop()
                }
            }

            override fun onTabSelected(position: Int) {
                val transaction = supportFragmentManager.beginTransaction()
                when (position) {
                    MAP_NAVIGATION_POSITION -> {
                        transaction.show(artMapFragment).hide(artListFragment).hide(favouritesListFragment)
                        mainMenu?.findItem(R.id.action_sort)?.isVisible = false
                        mainMenu?.findItem(R.id.action_track_location)?.isVisible = true
                    }
                    LIST_NAVIGATION_POSITION -> {
                        transaction.hide(artMapFragment).show(artListFragment).hide(favouritesListFragment)
                        mainMenu?.findItem(R.id.action_sort)?.isVisible = true
                        mainMenu?.findItem(R.id.action_track_location)?.isVisible = false
                        mainMenu?.findItem(R.id.action_track_location)?.icon = getDrawableSafely(R.drawable.ic_gps_fixed_black_24dp)
                        artMapFragment?.stopLocationTracking()
                        artMapFragment?.hideArtObjectDigest()
                    }
                    FAVOURITES_NAVIGATION_POSITION -> {
                        transaction.hide(artMapFragment).hide(artListFragment).show(favouritesListFragment)
                        mainMenu?.findItem(R.id.action_sort)?.isVisible = true
                        mainMenu?.findItem(R.id.action_track_location)?.isVisible = false
                        mainMenu?.findItem(R.id.action_track_location)?.icon = getDrawableSafely(R.drawable.ic_gps_fixed_black_24dp)
                        artMapFragment?.stopLocationTracking()
                        artMapFragment?.hideArtObjectDigest()
                    }
                }
                transaction.commit()
            }
        })
    }

    private fun initSortOrderIcon(sortOrder: Int) {
        when (sortOrder) {
            SortOrder.byDate -> mainMenu?.findItem(R.id.action_sort)?.icon = getDrawableSafely(R.drawable.ic_schedule_black_24dp)
            SortOrder.byDistance -> mainMenu?.findItem(R.id.action_sort)?.icon = getDrawableSafely(R.drawable.ic_location_on_black_24dp)
        }
    }

    private fun initRxSubscriptions() {
        compositeSubscription.addAll(
                RxView.clicks(search_view.findViewById(R.id.search_close_btn)).subscribe { closeSearchView() },
                RxSearchView.queryTextChanges(search_view).subscribe { applyFilter(it) }
        )
    }

    private fun applyFilter(query: CharSequence) {
        artMapFragment?.applyFilter(query.toString())
        artListFragment?.applyFilter(query.toString())
        favouritesListFragment?.applyFilter(query.toString())
    }

    private fun changeLocationTracking() {
        if (artMapFragment?.isLocationTracking == true) {
            artMapFragment?.stopLocationTracking()
            mainMenu?.findItem(R.id.action_track_location)?.icon = getDrawableSafely(R.drawable.ic_gps_fixed_black_24dp)
        } else {
            artMapFragment?.startLocationTracking()
            mainMenu?.findItem(R.id.action_track_location)?.icon = getDrawableSafely(R.drawable.ic_location_disabled_black_24dp)
        }
    }

    private fun changeSortOrder() {
        val newSortOrder = prefs.swapSortOrder()
        initSortOrderIcon(newSortOrder)
        toast(SortOrder.getString(newSortOrder))
    }

    private fun openSearchView() {
        artMapFragment?.hideArtObjectDigest()
        mainMenu?.findItem(R.id.action_search)?.isVisible = false
        search_view.animateToVisible()
    }

    private fun closeSearchView() {
        mainMenu?.findItem(R.id.action_search)?.isVisible = true
        search_view.setQuery("", true)
        hideKeyboard()
        search_view.animateToGone()
    }

    private fun hideKeyboard() {
        if (currentFocus != null) {
            val inputMgr = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMgr.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }

    companion object {
        private val ART_MAP_FRAGMENT_TAG = "artMapFragment"
        private val ART_LIST_FRAGMENT_TAG = "artListFragment"
        private val FAVOURITES_LIST_FRAGMENT_TAG = "favouritesListFragment"

        private val MAP_NAVIGATION_POSITION = 0
        private val LIST_NAVIGATION_POSITION = 1
        private val FAVOURITES_NAVIGATION_POSITION = 2
    }
}
