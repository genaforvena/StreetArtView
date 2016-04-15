package org.imozerov.streetartview.ui.explore.base

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.ui.explore.interfaces.ArtView
import org.imozerov.streetartview.ui.model.ArtObject
import org.imozerov.streetartviewsdk.IArtObjectsProvider
import org.imozerov.streetartviewsdk.internal.network.FetchService
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by imozerov on 11.02.16.
 */
open class ArtListPresenter(private val view: ArtView) {
    val TAG = "ArtListPresenter"

    private var fetchSubscription: Subscription? = null
    private var filterQuery: String = ""

    private var fetchFinishedBroadcastReceiver: BroadcastReceiver? = null

    @Inject
    lateinit var dataSource: IArtObjectsProvider

    @Inject
    lateinit var application: Application

    fun onStart(context: Context) {
        StreetArtViewApp.getAppComponent(context).inject(this)
        fetchSubscription = startFetchingArtObjectsFromDataSource()

        fetchFinishedBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                view.stopRefreshIndication()
            }
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(FetchService.EVENT_FETCH_FINISHED)
        application.registerReceiver(fetchFinishedBroadcastReceiver, intentFilter)
    }

    fun onStop() {
        fetchSubscription!!.unsubscribe()
        application.unregisterReceiver(fetchFinishedBroadcastReceiver)
    }

    fun applyFilter(query: String) {
        Log.v(TAG, "Applying filter $query")
        filterQuery = query
        fetchSubscription?.unsubscribe()
        fetchSubscription = startFetchingArtObjectsFromDataSource()
    }

    fun getArtObject(id: String): ArtObject {
        return dataSource.getArtObject(id)
    }

    fun refreshData() {
        dataSource.syncWithBackend()
    }

    private fun startFetchingArtObjectsFromDataSource(): Subscription {
        return fetchFromDataSource()
                .debounce(200, TimeUnit.MILLISECONDS)
                .map { it.filter { it.matches(filterQuery) } }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view.showArtObjects(it) }
    }

    open fun fetchFromDataSource() : Observable<List<ArtObject>> {
        return dataSource.listArtObjects()
    }
}