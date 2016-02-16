package org.imozerov.streetartview.storage.model

import io.realm.RealmList
import org.imozerov.streetartview.network.model.Artwork

/**
 * Created by imozerov on 16.02.16.
 */
fun RealmArtObject.copyDataFromJson(artwork: Artwork) {
    this.id = artwork.id
    this.description = artwork.description
    this.name = artwork.name
    this.thumbPicUrl = artwork.photos[0].thmb

    val artistFromJson = artwork.artists[0]
    val realmAuthor = RealmAuthor()
    // TODO art object may have several authors! Migrate model!
    realmAuthor.id = artistFromJson.id
    realmAuthor.description = ""
    realmAuthor.name = artistFromJson.name
    // Artist may have photo!
//    realmAuthor.photo
    this.author = realmAuthor

    this.picsUrls = RealmList()
    for (photo in artwork.photos) {
        val url = RealmString()
        url.value = photo.big
        this.picsUrls.add(url)
    }

    this.lat = artwork.location.lat
    this.lng = artwork.location.lng
}