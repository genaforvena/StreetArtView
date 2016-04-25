package org.imozerov.streetartview.storage.model

import io.realm.RealmList
import org.imozerov.streetartview.network.model.Artwork

/**
 * Created by imozerov on 16.02.16.
 */
fun RealmArtObject.copyDataFromJson(artwork: Artwork) {
    id = artwork.id
    description = artwork.description
    name = artwork.name
    createdAt = artwork.createdAt ?: 0
    updatedAt = artwork.updatedAt ?: 0

    authors = RealmList()
    if (artwork.artists != null) {
        artwork.artists.map {
            val realmAuthor = RealmAuthor()
            realmAuthor.id = it.id
            realmAuthor.name = it.name
            authors!!.add(realmAuthor)
        }
    }

    picsUrls = RealmList()
    if (artwork.photos != null && artwork.photos.size > 0 && artwork.photos[0].big != null) {
        thumbPicUrl = artwork.photos[0].big
        artwork.photos.forEach {
            val url = RealmString()
            url.value = it.big
            picsUrls!!.add(url)
        }
    }

    location = RealmLocation()
    with(location!!) {
        address = artwork.location.address
        lat = artwork.location.lat
        lng = artwork.location.lng
    }

    status = artwork.status.code
}