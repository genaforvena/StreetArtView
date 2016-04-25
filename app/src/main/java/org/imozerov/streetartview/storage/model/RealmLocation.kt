package org.imozerov.streetartview.storage.model

import io.realm.RealmObject

open class RealmLocation : RealmObject() {
    open var address: String? = null
    open var lat: Double = 0.toDouble()
    open var lng: Double = 0.toDouble()
}
