package org.imozerov.streetartview.ui.model

import org.imozerov.streetartview.storage.model.RealmArtObject
import org.imozerov.streetartview.storage.model.RealmAuthor
import org.imozerov.streetartview.storage.model.RealmString

import java.util.ArrayList

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

    constructor(aId: String,
                aName: String,
                aAuthor: AuthorUi,
                aDescription: String,
                aThumbPicUrl: String,
                aPicsUrls: List<String>) {
        id = aId
        name = aName
        author = aAuthor
        description = aDescription
        thumbPicUrl = aThumbPicUrl
        picsUrls = aPicsUrls
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
