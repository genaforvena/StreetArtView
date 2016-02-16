package org.imozerov.streetartview.storage

import android.os.Handler
import android.os.SystemClock
import android.util.Log
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import org.imozerov.streetartview.network.FetchService
import org.imozerov.streetartview.network.model.Artwork
import org.imozerov.streetartview.storage.model.RealmArtObject
import org.imozerov.streetartview.storage.model.RealmAuthor
import org.imozerov.streetartview.storage.model.RealmString
import org.imozerov.streetartview.storage.model.copyDataFromJson
import org.imozerov.streetartview.ui.model.ArtObjectUi
import rx.Observable
import java.util.*

/**
 * Created by imozerov on 05.02.16.
 */
class DataSource(private val realm: Realm, private val handler: Handler) {
    val TAG = "DataSource"

    fun insert(artworks: MutableList<Artwork>) {
        Log.d(TAG, "inserting $artworks")
        val realmObjects = artworks.map {
            val realmArtObject = RealmArtObject()
            realmArtObject.copyDataFromJson(it)
            return@map realmArtObject
        }

        handler.post { realm.batchInsertOrUpdate(realmObjects) }
    }

    fun listArtObjects(): Observable<List<ArtObjectUi>> {
        return realm
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

    fun getArtObject(id: String): Observable<ArtObjectUi> {
        return realm
                .where(RealmArtObject::class.java)
                .equalTo("id", id)
                .findFirst()
                .asObservable<RealmObject>()
                .map { ArtObjectUi(it as RealmArtObject) }
    }

    fun addArtObjectStub() {
        val realmAuthor = RealmAuthor()
        with (realmAuthor) {
            id = "1"
            name = "Vasya"
            description = "Description"
        }

        val realmArtObject = RealmArtObject()
        with (realmArtObject) {
            author = realmAuthor
            description = "Description should be a bit bigger than just a one word. That's why I'm writing this!"
            name = "Name"
            id = SystemClock.currentThreadTimeMillis().toString()
            thumbPicUrl = "Pic"
            picsUrls = RealmList<RealmString>()
        }

        handler.post { realm.insertOrUpdate(realmArtObject) }
    }
}

fun Realm.insertOrUpdate(realmObject: RealmObject) {
    beginTransaction()
    copyToRealmOrUpdate(realmObject)
    commitTransaction()
}

fun Realm.batchInsertOrUpdate(realmObjects: List<RealmObject>) {
    beginTransaction()
    copyToRealmOrUpdate(realmObjects)
    commitTransaction()
}
