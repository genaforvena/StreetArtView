package org.imozerov.streetartview.network.endpoints

import org.imozerov.streetartview.network.model.Artwork

import retrofit.Call
import retrofit.http.GET

interface ArtWorksEndpoint {
    @GET("artworks/")
    fun list(): Call<List<Artwork>>
}
