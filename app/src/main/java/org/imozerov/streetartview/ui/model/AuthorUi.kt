package org.imozerov.streetartview.ui.model

import org.imozerov.streetartview.storage.model.RealmAuthor

/**
 * Created by imozerov on 05.02.16.
 */
class AuthorUi {
    val id: String
    val name: String
    val description: String

    constructor(aId: String, aName: String, aDescription: String) {
        id = aId
        name = aName
        description = aDescription
    }

    constructor(realmAuthor: RealmAuthor) {
        id = realmAuthor.id
        name = realmAuthor.name
        description = realmAuthor.description
    }
}
