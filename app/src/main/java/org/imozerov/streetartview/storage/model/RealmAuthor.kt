package org.imozerov.streetartview.storage.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmAuthor : RealmObject() {
    @PrimaryKey
    open var id: String? = null
    open var name: String? = null
    open var photo: String? = null
}
