package org.imozerov.streetartview.ui

import org.imozerov.streetartview.storage.DataSource
import org.imozerov.streetartview.ui.interfaces.ArtView
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers

/**
 * Created by imozerov on 11.02.16.
 */
class ArtListPresenter(private val dataSource: DataSource, private val view: ArtView) {
    private var fetchSubscription: Subscription? = null
    private var filterQuery: String = ""

    fun onStart() {
        fetchSubscription = startFetchingArtObjectsFromDataSource()
    }

    fun onStop() {
        fetchSubscription!!.unsubscribe()
    }

    fun applyFilter(query: String) {
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