package org.imozerov.streetartview.network.internal

import org.imozerov.streetartview.network.endpoints.ArtWorksEndpoint

import retrofit.Retrofit

class RestClient(retrofit: Retrofit) {
    var artWorksEndpoint: ArtWorksEndpoint
        internal set

    init {
        artWorksEndpoint = retrofit.create(ArtWorksEndpoint::class.java)
    }
}
