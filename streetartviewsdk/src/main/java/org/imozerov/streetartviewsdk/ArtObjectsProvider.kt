package org.imozerov.streetartviewsdk

import android.app.Application
import org.imozerov.streetartview.storage.IDataSource
import org.imozerov.streetartview.ui.model.ArtObject
import org.imozerov.streetartviewsdk.internal.network.FetchService
import rx.Observable
import javax.inject.Inject

/**
 * Created by imozerov on 15.04.16.
 */
class ArtObjectsProvider : IArtObjectsProvider {
    @Inject
    lateinit var dataSource: IDataSource

    @Inject
    lateinit var application: Application

    constructor(artObjectProviderCreator: ArtObjectProviderCreator) {
        artObjectProviderCreator.dataSourceComponent.inject(this)
    }

    override fun syncWithBackend() {
        FetchService.startFetch(application)
    }

    override fun listArtObjects(): Observable<List<ArtObject>> {
        return dataSource.listArtObjects()
    }

    override fun getArtObject(id: String): ArtObject {
        return dataSource.getArtObject(id)
    }

    override fun listFavourites(): Observable<List<ArtObject>> {
        return dataSource.listFavourites()
    }

    override fun changeFavouriteStatus(artObjectId: String): Boolean {
        return dataSource.changeFavouriteStatus(artObjectId)
    }
}