package org.imozerov.streetartview.ui.explore.base

import android.app.Application
import android.content.*
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.google.android.gms.analytics.Tracker
import com.google.android.gms.maps.model.LatLng
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.network.FetchService
import org.imozerov.streetartview.storage.IDataSource
import org.imozerov.streetartview.ui.explore.interfaces.ArtView
import org.imozerov.streetartview.ui.explore.sort.SortOrder
import org.imozerov.streetartview.ui.explore.sort.getSortOrder
import org.imozerov.streetartview.ui.extensions.distanceTo
import org.imozerov.streetartview.ui.extensions.sendScreen
import org.imozerov.streetartview.ui.model.ArtObjectUi
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by imozerov on 11.02.16.
 */
abstract class ArtListPresenter : SharedPreferences.OnSharedPreferenceChangeListener {
    val TAG = "ArtListPresenter"

    private var view: ArtView? = null
    private var fetchSubscription: Subscription? = null
    private var filterQuery: String = ""
    private var sortOrder: Int? = SortOrder.byDate

    private val fetchFinishedBroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == FetchService.EVENT_FETCH_FINISHED) {
                    view?.stopRefresh()
                }
            }
        }
    }

    @Inject
    lateinit var tracker: Tracker

    @Inject
    lateinit var dataSource: IDataSource

    @Inject
    lateinit var application: Application

    @Inject
    lateinit var prefs: SharedPreferences

    fun bindView(artView: ArtView, context: Context) {
        view = artView
        (context.applicationContext as StreetArtViewApp).appComponent.inject(this)

        sortOrder = prefs.getSortOrder()

        prefs.registerOnSharedPreferenceChangeListener(this)

        tracker.sendScreen(artView.javaClass.simpleName)

        fetchSubscription = createDataFetchSubscription()

        val intentFilter = IntentFilter()
        intentFilter.addAction(FetchService.EVENT_FETCH_FINISHED)
        LocalBroadcastManager.getInstance(application).registerReceiver(fetchFinishedBroadcastReceiver, intentFilter)
    }

    fun unbindView() {
        fetchSubscription!!.unsubscribe()

        prefs.unregisterOnSharedPreferenceChangeListener(this)
        LocalBroadcastManager.getInstance(application).unregisterReceiver(fetchFinishedBroadcastReceiver)
        view = null
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (SortOrder.KEY.equals(key)) {
            Log.e(TAG, "Applying new sort order")
            fetchSubscription?.unsubscribe()
            fetchSubscription = createDataFetchSubscription()
        }
    }

    fun applyFilter(query: String) {
        Log.v(TAG, "Applying filter $query")
        filterQuery = query
        fetchSubscription?.unsubscribe()
        fetchSubscription = createDataFetchSubscription()
    }

    fun getArtObject(id: String): ArtObjectUi {
        return dataSource.getArtObject(id)
    }

    fun refreshData() {
        FetchService.startFetch(application)
        tracker.sendScreen("Refresh data requested")
    }

    private fun createDataFetchSubscription(): Subscription {
        if (sortOrder == SortOrder.byDate) {
            if (filterQuery.isBlank()) {
                return fetchData()
                        .debounce(200, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { view?.showArtObjects(it) }
            } else {
                return fetchData()
                        .debounce(200, TimeUnit.MILLISECONDS)
                        .map { it.filter { it.matches(filterQuery) } }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { view?.showArtObjects(it) }
            }
        } else {
            if (filterQuery.isBlank()) {
                return fetchData()
                        .debounce(200, TimeUnit.MILLISECONDS)
                        .map { it.sortedWith(Comparator<ArtObjectUi> { lhs, rhs -> LatLng(lhs.lat, lhs.lng).distanceTo(LatLng(rhs.lat, rhs.lng)).toInt() }) }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { view?.showArtObjects(it) }
            } else {
                return fetchData()
                        .debounce(200, TimeUnit.MILLISECONDS)
                        .map { it.filter { it.matches(filterQuery) } }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { view?.showArtObjects(it) }
            }
        }
    }

    abstract fun fetchData(): Observable<List<ArtObjectUi>>
}