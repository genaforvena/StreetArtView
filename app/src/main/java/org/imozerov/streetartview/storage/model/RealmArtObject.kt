package org.imozerov.streetartview.storage.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmArtObject : RealmObject() {
    @PrimaryKey
    open var id: String? = null
    open var name: String? = null
    open var authors: RealmList<RealmAuthor>? = null
    open var description: String? = null
    open var thumbPicUrl: String? = null
    open var location: RealmLocation? = null
    open var picsUrls: RealmList<RealmString>? = null
    open var isFavourite: Boolean = false
    open var status: Int = 0
    open var createdAt: Long = 0
    open var updatedAt: Long = 0
}
