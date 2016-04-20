package org.imozerov.streetartview.ui.explore.base

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.google.android.gms.analytics.Tracker
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.network.FetchService
import org.imozerov.streetartview.storage.IDataSource
import org.imozerov.streetartview.ui.explore.interfaces.ArtView
import org.imozerov.streetartview.ui.extensions.sendScreen
import org.imozerov.streetartview.ui.model.ArtObjectUi
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by imozerov on 11.02.16.
 */
abstract class ArtListPresenter {
    val TAG = "ArtListPresenter"

    private var view: ArtView? = null
    private var fetchSubscription: Subscription? = null
    private var filterQuery: String = ""

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

    fun bindView(artView: ArtView, context: Context) {
        view = artView
        (context.applicationContext as StreetArtViewApp).appComponent.inject(this)
        tracker.sendScreen(artView.javaClass.simpleName)

        fetchSubscription = createDataFetchSubscription()

        val intentFilter = IntentFilter()
        intentFilter.addAction(FetchService.EVENT_FETCH_FINISHED)
        LocalBroadcastManager.getInstance(application).registerReceiver(fetchFinishedBroadcastReceiver, intentFilter)
    }

    fun unbindView() {
        fetchSubscription!!.unsubscribe()
        LocalBroadcastManager.getInstance(application).unregisterReceiver(fetchFinishedBroadcastReceiver)
        view = null
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
        if (filterQuery.isBlank()) {
            return fetchFromDataSource()
                    .debounce(200, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { view?.showArtObjects(it) }
        } else {
            return fetchFromDataSource()
                    .debounce(200, TimeUnit.MILLISECONDS)
                    .map { it.filter { it.matches(filterQuery) } }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { view?.showArtObjects(it) }
        }
    }

    abstract fun fetchFromDataSource(): Observable<List<ArtObjectUi>>
}