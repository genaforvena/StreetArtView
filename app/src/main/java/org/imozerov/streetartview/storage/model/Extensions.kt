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

    authors = RealmList()
    if (artwork.artists != null) {
        artwork.artists.map {
            val realmAuthor = RealmAuthor()
            realmAuthor.id = it.id
            realmAuthor.name = it.name
            realmAuthor.photo = it.photo
            authors.add(realmAuthor)
        }
    }

    picsUrls = RealmList()
    if (artwork.photos != null && artwork.photos.size > 0) {
        thumbPicUrl = artwork.photos[0].big
        artwork.photos.map {
            val url = RealmString()
            url.value = it.big
            this.picsUrls.add(url)
        }
    }

    location = RealmLocation()
    with(location) {
        address = artwork.location.address
        lat = artwork.location.lat
        lng = artwork.location.lng
    }
}