package org.imozerov.streetartview.ui.model

import org.imozerov.streetartview.storage.model.RealmAuthor

/**
 * Created by imozerov on 05.02.16.
 */
class AuthorUi {
    val id: String
    val name: String
    val photoUrl: String

    constructor(aId: String, aName: String, aPhoto: String) {
        id = aId
        name = aName
        photoUrl = aPhoto
    }

    constructor(realmAuthor: RealmAuthor) {
        id = realmAuthor.id
        name = realmAuthor.name
        photoUrl = realmAuthor.photo
    }
}
