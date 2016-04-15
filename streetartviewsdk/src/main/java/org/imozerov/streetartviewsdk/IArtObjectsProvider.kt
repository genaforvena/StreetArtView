package org.imozerov.streetartviewsdk

import org.imozerov.streetartview.ui.model.ArtObject
import rx.Observable

/**
 * Created by imozerov on 15.04.16.
 */
interface IArtObjectsProvider {
    fun syncWithBackend()
    fun listArtObjects(): Observable<List<ArtObject>>
    fun getArtObject(id: String): ArtObject
    fun listFavourites(): Observable<List<ArtObject>>
    fun changeFavouriteStatus(artObjectId: String) : Boolean
}