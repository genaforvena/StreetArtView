package org.imozerov.streetartview.storage

import android.util.Log
import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.Sort
import org.imozerov.streetartview.network.model.Artwork
import org.imozerov.streetartview.storage.model.RealmArtObject
import org.imozerov.streetartview.storage.model.copyDataFromJson
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
            realmObjects.filter { favouriteIds.contains(it.id) }.forEach { it.isFavourite = true }
            it.batchInsertOrUpdate(realmObjects)
        }
    }

    override fun listArtObjects(): Observable<List<ArtObjectUi>> {
        return readOnlyRealm
                .where(RealmArtObject::class.java)
                // TODO We're selecting only alive objects for now!
                .equalTo("status", 0)
                .findAllSortedAsync("updatedAt", Sort.DESCENDING)
                .asObservable()
                .cache()
                // Do not try to use flatMap().toList() here
                // as onComplete() will never be called so
                // No call to toList() will be performed.
                .map { realmToUi(it) }
    }

    override fun listFavourites(): Observable<List<ArtObjectUi>> {
        return readOnlyRealm
                .where(RealmArtObject::class.java)
                .equalTo("isFavourite", true)
                // TODO We're selecting only alive objects for now!
                .equalTo("status", 0)
                .findAllSortedAsync("updatedAt", Sort.DESCENDING)
                .asObservable()
                .cache()
                // Do not try to use flatMap().toList() here
                // as onComplete() will never be called so
                // No call to toList() will be performed.
                .map { realmToUi(it) }
    }

    override fun getArtObject(id: String): ArtObjectUi {
        return ArtObjectUi(readOnlyRealm
                .where(RealmArtObject::class.java)
                .equalTo("id", id)
                .findFirst())
    }

    override fun setFavourite(artObjectId: String, isFavourite: Boolean) {
        executeAsyncRealmOperation {
            with (it) {
                val artObjectInRealm = where(RealmArtObject::class.java)
                        .equalTo("id", artObjectId)
                        .findFirst()

                beginTransaction()
                artObjectInRealm.isFavourite = isFavourite
                commitTransaction()
            }
        }
    }

    private fun realmToUi(realmObjects: RealmResults<RealmArtObject>): List<ArtObjectUi> {
        val listOfArtObjects = ArrayList<ArtObjectUi>(realmObjects.size)
        realmObjects.filter { it.picsUrls.isNotEmpty() }
                .forEach { listOfArtObjects.add(ArtObjectUi(it)) }
        return listOfArtObjects
    }
}

private inline fun executeAsyncRealmOperation(crossinline operation: ((realm: Realm) -> (Unit))) {
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