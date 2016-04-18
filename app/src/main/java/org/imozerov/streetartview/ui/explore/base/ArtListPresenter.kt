package org.imozerov.streetartview.ui.explore.base

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.network.FetchService
import org.imozerov.streetartview.storage.IDataSource
import org.imozerov.streetartview.ui.explore.interfaces.ArtView
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
    private var fetchFinishedBroadcastReceiver: BroadcastReceiver? = null
    private var fetchSubscription: Subscription? = null
    private var filterQuery: String = ""

    @Inject
    lateinit var tracker: Tracker

    @Inject
    lateinit var dataSource: IDataSource

    @Inject
    lateinit var application: Application

    fun bindView(view: ArtView, context: Context) {
        this.view = view
        (context.applicationContext as StreetArtViewApp).appComponent.inject(this)
        tracker.setScreenName(view.javaClass.simpleName)
        tracker.send(HitBuilders.ScreenViewBuilder().build());

        fetchSubscription = startFetchingArtObjectsFromDataSource()

        fetchFinishedBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                view.stopRefresh()
            }
        }

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
        fetchSubscription = startFetchingArtObjectsFromDataSource()
    }

    fun getArtObject(id: String): ArtObjectUi {
        return dataSource.getArtObject(id)
    }

    fun refreshData() {
        FetchService.startFetch(application)
        tracker.setScreenName("Refresh data requested")
        tracker.send(HitBuilders.ScreenViewBuilder().build());
    }

    private fun startFetchingArtObjectsFromDataSource(): Subscription {
        return fetchFromDataSource()
                .debounce(200, TimeUnit.MILLISECONDS)
                .map { it.filter { it.matches(filterQuery) } }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.showArtObjects(it) }
    }

    abstract fun fetchFromDataSource() : Observable<List<ArtObjectUi>>
}