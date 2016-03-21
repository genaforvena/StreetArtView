package org.imozerov.streetartview.ui.explore.base

import android.util.Log
import org.imozerov.streetartview.StreetArtViewApp
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
open class ArtListPresenter(private val view: ArtView) {
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

    fun getArtObject(id: String): ArtObjectUi {
        return dataSource.getArtObject(id)
    }

    private fun startFetchingArtObjectsFromDataSource(): Subscription {
        return fetchFromDataSource()
                .debounce(200, TimeUnit.MILLISECONDS)
                .map { it.filter { it.matches(filterQuery) } }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view.showArtObjects(it) }
    }

    open fun fetchFromDataSource() : Observable<List<ArtObjectUi>> {
        return dataSource.listArtObjects()
    }
}