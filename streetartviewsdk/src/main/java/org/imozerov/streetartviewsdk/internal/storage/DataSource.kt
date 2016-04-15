package org.imozerov.streetartview.storage

import android.util.Log
import io.realm.Realm
import io.realm.RealmObject
import org.imozerov.streetartview.ui.model.ArtObject
import org.imozerov.streetartviewsdk.internal.network.model.Artwork
import org.imozerov.streetartviewsdk.internal.storage.model.RealmArtObject
import org.imozerov.streetartviewsdk.internal.storage.model.copyDataFromJson
import rx.Observable
import java.util.*

/**
 * Created by imozerov on 05.02.16.
 */
class DataSource() : IDataSource {
    val TAG = "DataSource"
    // This realm instance should be used only to read data
    val readOnlyRealm = Realm.getDefaultInstance()

    override fun insert(artworks: List<Artwork>) {
        executeAsyncRealmOperation {
            Log.d(TAG, "inserting $artworks")
            val realmObjects = artworks.map {
                val realmArtObject = RealmArtObject()
                realmArtObject.copyDataFromJson(it)
                return@map realmArtObject
            }
            val favouriteIds = it.where(RealmArtObject::class.java)
                    .equalTo("isFavourite", true).findAll().map { it.id }
            realmObjects.filter { favouriteIds.contains(it.id) }.forEach { it.setIsFavourite(true) }
            it.batchInsertOrUpdate(realmObjects)
        }
    }

    override fun listArtObjects(): Observable<List<ArtObject>> {
        return readOnlyRealm
                .where(RealmArtObject::class.java)
                // TODO We're selecting only alive objects for now!
                .equalTo("status", 0)
                .findAll()
                .asObservable()
                .cache()
                // Do not try to use flatMap().toList() here
                // as onComplete() will never be called so
                // No call to toList() will be performed.
                .map {
                    val listOfArtObjects = ArrayList<ArtObject>(it.size)
                    it.filter { it.picsUrls.isNotEmpty() }
                            .forEach { listOfArtObjects.add(ArtObject(it)) }
                    listOfArtObjects
                }
    }

    override fun listFavourites(): Observable<List<ArtObject>> {
        return readOnlyRealm
                .where(RealmArtObject::class.java)
                .equalTo("isFavourite", true)
                // TODO We're selecting only alive objects for now!
                .equalTo("status", 0)
                .findAll()
                .asObservable()
                .cache()
                // Do not try to use flatMap().toList() here
                // as onComplete() will never be called so
                // No call to toList() will be performed.
                .map {
                    val listOfArtObjects = ArrayList<ArtObject>(it.size)
                    it.filter { it.picsUrls.isNotEmpty() }
                            .forEach { listOfArtObjects.add(ArtObject(it)) }
                    listOfArtObjects
                }
    }

    override fun getArtObject(id: String): ArtObject {
        return ArtObject(readOnlyRealm
                .where(RealmArtObject::class.java)
                .equalTo("id", id)
                .findFirst())
    }

    override fun changeFavouriteStatus(artObjectId: String): Boolean {
        with (readOnlyRealm) {
            val artObjectInRealm = where(RealmArtObject::class.java)
                    .equalTo("id", artObjectId)
                    .findFirst()

            beginTransaction()
            val newStatus = !artObjectInRealm.isFavourite
            artObjectInRealm.setIsFavourite(newStatus)
            copyToRealmOrUpdate(artObjectInRealm)
            commitTransaction()

            return newStatus
        }
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