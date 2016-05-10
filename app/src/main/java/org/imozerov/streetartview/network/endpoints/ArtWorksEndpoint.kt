package org.imozerov.streetartview.network.endpoints

import org.imozerov.streetartview.network.model.Artwork

import retrofit.Call
import retrofit.http.GET
import rx.Observable

interface ArtWorksEndpoint {
    @GET("artworks/")
    fun list(): Observable<List<Artwork>>
}
