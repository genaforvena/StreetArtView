package org.imozerov.streetartview.ui.explore.base

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.android.gms.analytics.Tracker
import com.google.android.gms.maps.model.LatLng
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.bus.RxBus
import org.imozerov.streetartview.bus.events.FetchFinishedEvent
import org.imozerov.streetartview.bus.events.LocationChangedEvent
import org.imozerov.streetartview.location.LocationService
import org.imozerov.streetartview.location.NIZHNY_NOVGOROD_LOCATION
import org.imozerov.streetartview.location.distanceTo
import org.imozerov.streetartview.location.getCachedLocation
import org.imozerov.streetartview.network.FetchService
import org.imozerov.streetartview.storage.IDataSource
import org.imozerov.streetartview.ui.explore.interfaces.ArtView
import org.imozerov.streetartview.ui.explore.sort.SortOrder
import org.imozerov.streetartview.ui.explore.sort.getSortOrder
import org.imozerov.streetartview.ui.extensions.sendScreen
import org.imozerov.streetartview.ui.model.ArtObjectUi
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by imozerov on 11.02.16.
 */
abstract class ArtListPresenter : SharedPreferences.OnSharedPreferenceChangeListener {
    val TAG = "ArtListPresenter"

    private var view: ArtView? = null
    private var fetchSubscription: Subscription? = null
    private var rxBusSubscription: Subscription? = null
    private var filterQuery: String = ""
    private var sortOrder: Int? = SortOrder.byDate

    private var currentLocation: LatLng? = null

    @Inject
    lateinit var rxBus: RxBus

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
        (context.applicationContext as StreetArtViewApp).appComponent!!.inject(this)
        currentLocation = prefs.getCachedLocation()

        sortOrder = prefs.getSortOrder()

        prefs.registerOnSharedPreferenceChangeListener(this)

        tracker.sendScreen(artView.javaClass.simpleName)

        fetchSubscription = createDataFetchSubscription()

        rxBusSubscription = rxBus.toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it is FetchFinishedEvent) {
                        view?.stopRefresh()
                    }
                    if (it is LocationChangedEvent) {
                        currentLocation = it.newLocation
                        fetchSubscription?.unsubscribe()
                        fetchSubscription = createDataFetchSubscription()
                    }
                }
    }

    fun unbindView() {
        fetchSubscription!!.unsubscribe()
        rxBusSubscription!!.unsubscribe()

        prefs.unregisterOnSharedPreferenceChangeListener(this)
        view = null
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (SortOrder.KEY.equals(key) && sharedPreferences != null) {
            Log.v(TAG, "changing sort order")
            sortOrder = sharedPreferences.getSortOrder()
            if (sortOrder == SortOrder.byDistance) {
                LocationService.getLocationOnce(application)
            }
            fetchSubscription?.unsubscribe()
            fetchSubscription = createDataFetchSubscription()
        }
    }

    fun applyFilter(query: String) {
        if (query == filterQuery) {
            return
        }
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
        LocationService.getLocationOnce(application)
        tracker.sendScreen("Refresh data requested")
    }

    private fun createDataFetchSubscription(): Subscription {
        return fetchData()
                .debounce(200, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.computation())
                .doOnNext {
                    Observable.from(it)
                            .observeOn(Schedulers.computation())
                            .doOnNext { it.distanceTo = LatLng(it.lat, it.lng).distanceTo(currentLocation ?: NIZHNY_NOVGOROD_LOCATION).toInt() }
                            .filter { it.matches(filterQuery) }
                            .toSortedList { artObjectUi, artObjectUi2 ->
                                if (sortOrder == SortOrder.byDistance) {
                                    artObjectUi.distanceTo - artObjectUi2.distanceTo
                                } else {
                                    (artObjectUi2.updatedAt - artObjectUi.updatedAt).toInt()
                                }
                            }
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnNext { view?.showArtObjects(it) }
                            .subscribeOn(Schedulers.computation())
                            .subscribe()
                }
                .subscribe()
    }

    abstract fun fetchData(): Observable<List<ArtObjectUi>>
}