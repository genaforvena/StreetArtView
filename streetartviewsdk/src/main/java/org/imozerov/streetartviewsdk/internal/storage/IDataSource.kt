package org.imozerov.streetartview.storage

import org.imozerov.streetartview.ui.model.ArtObject
import org.imozerov.streetartviewsdk.internal.network.model.Artwork
import rx.Observable

/**
 * Created by imozerov on 04.03.16.
 */
interface IDataSource {
    fun insert(artworks: List<Artwork>)
    fun listArtObjects(): Observable<List<ArtObject>>
    fun getArtObject(id: String): ArtObject
    fun listFavourites(): Observable<List<ArtObject>>
    fun changeFavouriteStatus(artObjectId: String) : Boolean
}