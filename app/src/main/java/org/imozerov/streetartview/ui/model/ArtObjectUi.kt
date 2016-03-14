package org.imozerov.streetartview.ui.model

import android.support.annotation.VisibleForTesting
import org.imozerov.streetartview.storage.model.RealmArtObject
import java.util.*

/**
 * Created by imozerov on 05.02.16.
 */
class ArtObjectUi {
    val id: String
    val name: String
    val authors: List<AuthorUi>
    val description: String
    val thumbPicUrl: String
    val picsUrls: List<String>
    val lat: Double
    val lng: Double
    val address: String

    constructor(realmArtObject: RealmArtObject) {
        val picUrls = ArrayList<String>()
        for (realmString in realmArtObject.picsUrls) {
            picUrls.add(realmString.value)
        }

        id = realmArtObject.id
        name = realmArtObject.name
        authors = realmArtObject.authors.map {
            return@map AuthorUi(
                    it.id,
                    it.name,
                    it.photo
            )
        }
        description = realmArtObject.description
        thumbPicUrl = realmArtObject.thumbPicUrl ?: ""
        picsUrls = picUrls

        lat = realmArtObject.location.lat
        lng = realmArtObject.location.lng
        address = realmArtObject.location.address
    }

    @VisibleForTesting
    constructor(id: String, name: String, authors: List<AuthorUi>, description: String, thumbPicUrl: String, picsUrls: List<String>, lat: Double, lng: Double, address: String) {
        this.id = id
        this.name = name
        this.authors = authors
        this.description = description
        this.thumbPicUrl = thumbPicUrl
        this.picsUrls = picsUrls
        this.lat = lat
        this.lng = lng
        this.address = address
    }

    fun authorsNames() : String {
        return authors.map{ return@map it.name }.reduce { s1, s2 -> s1 + ", " + s2 }
    }

    fun matches(query: String) : Boolean {
        if (query.isEmpty()) {
            return true
        }

        if (name.toLowerCase().contains(query.toLowerCase())) {
            return true
        }

        if (authorsNames().toLowerCase().contains(query.toLowerCase())) {
            return true
        }

        return false
    }
}
