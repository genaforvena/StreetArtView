package org.imozerov.streetartview.storage

import android.util.Log
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import org.imozerov.streetartview.network.model.Artwork
import org.imozerov.streetartview.storage.model.*
import org.imozerov.streetartview.ui.model.ArtObjectUi
import rx.Observable
import java.util.*

/**
 * Created by imozerov on 05.02.16.
 */
class DataSource() : IDataSource {
    val TAG = "DataSource"
    // This realm instance should be used only to read data
    val readOnlyRealm = Realm.getDefaultInstance()

    override fun insert(artworks: MutableList<Artwork>) {
        executeAsyncRealmOperation {
            Log.d(TAG, "inserting $artworks")
            val realmObjects = artworks.map {
                val realmArtObject = RealmArtObject()
                realmArtObject.copyDataFromJson(it)
                return@map realmArtObject
            }
            it.batchInsertOrUpdate(realmObjects)
        }
    }

    override fun listArtObjects(): Observable<List<ArtObjectUi>> {
        return readOnlyRealm
                .allObjects(RealmArtObject::class.java)
                .asObservable()
                .cache()
                // Do not try to use flatMap().toList() here
                // as onComplete() will never be called so
                // No call to toList() will be performed.
                .map {
                    val listOfArtObjects = ArrayList<ArtObjectUi>(it.size)
                    for (model in it) {
                        listOfArtObjects.add(ArtObjectUi(model))
                    }
                    listOfArtObjects
                }
    }

    override fun getArtObject(id: String): ArtObjectUi {
        return ArtObjectUi(readOnlyRealm
                .where(RealmArtObject::class.java)
                .equalTo("id", id)
                .findFirst())
    }
}

private fun executeAsyncRealmOperation(operation: ((realm: Realm) -> (Unit))) {
    Thread() {
        val realm = Realm.getDefaultInstance();
        try {
            operation(realm)
        } finally {
            realm.close()
        }
    }.start()
}

private fun Realm.batchInsertOrUpdate(realmObjects: List<RealmObject>) {
    beginTransaction()
    copyToRealmOrUpdate(realmObjects)
    commitTransaction()
}