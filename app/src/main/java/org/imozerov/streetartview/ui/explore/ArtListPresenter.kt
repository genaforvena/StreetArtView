package org.imozerov.streetartview.ui.explore

import android.util.Log
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.storage.IDataSource
import org.imozerov.streetartview.ui.explore.interfaces.ArtView
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import javax.inject.Inject

/**
 * Created by imozerov on 11.02.16.
 */
class ArtListPresenter(private val view: ArtView) {
    val TAG = "ArtListPresenter"

    private var fetchSubscription: Subscription? = null
    private var filterQuery: String = ""

    @Inject
    lateinit var dataSource: IDataSource

    fun onStart(application: StreetArtViewApp) {
        application.appComponent.inject(this)
        fetchSubscription = startFetchingArtObjectsFromDataSource()
    }

    fun onStop() {
        fetchSubscription!!.unsubscribe()
    }

    fun applyFilter(query: String) {
        Log.v(TAG, "Applying filter $query")
        filterQuery = query
        fetchSubscription?.unsubscribe()
        fetchSubscription = startFetchingArtObjectsFromDataSource()
    }

    private fun startFetchingArtObjectsFromDataSource(): Subscription {
        return dataSource
                .listArtObjects()
                .map { it.filter { it.matches(filterQuery) } }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view.showArtObjects(it) }
    }
}