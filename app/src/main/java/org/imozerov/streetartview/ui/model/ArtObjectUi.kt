package org.imozerov.streetartview.ui.model

import org.imozerov.streetartview.storage.model.RealmArtObject
import java.util.*

/**
 * Created by imozerov on 05.02.16.
 */
class ArtObjectUi {
    val id: String
    val name: String
    val author: AuthorUi
    val description: String
    val thumbPicUrl: String
    val picsUrls: List<String>
    val lat: Double
    val lng: Double

    constructor(aId: String,
                aName: String,
                aAuthor: AuthorUi,
                aDescription: String,
                aThumbPicUrl: String,
                aPicsUrls: List<String>,
                aLat: Double,
                aLng: Double) {
        id = aId
        name = aName
        author = aAuthor
        description = aDescription
        thumbPicUrl = aThumbPicUrl
        picsUrls = aPicsUrls
        lat = aLat
        lng = aLng
    }

    constructor(realmArtObject: RealmArtObject) {
        val realmAuthor = realmArtObject.author

        val picUrls = ArrayList<String>()
        for (realmString in realmArtObject.picsUrls) {
            picUrls.add(realmString.value)
        }

        id = realmArtObject.id
        name = realmArtObject.name
        author = AuthorUi(realmAuthor)
        description = realmArtObject.description
        thumbPicUrl = realmArtObject.thumbPicUrl
        picsUrls = picUrls
        lat = realmArtObject.lat
        lng = realmArtObject.lng
    }

    fun matches(query: String) : Boolean {
        if (query.isEmpty()) {
            return true
        }

        if (name.toLowerCase().contains(query.toLowerCase())) {
            return true
        }

        if (author.name.toLowerCase().contains(query.toLowerCase())) {
            return true
        }

        return false
    }
}
