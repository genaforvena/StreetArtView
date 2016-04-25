package org.imozerov.streetartview.storage.model

import io.realm.RealmObject

open class RealmString : RealmObject() {
    open var value: String? = null
}
