package org.imozerov.streetartview.storage

import android.os.SystemClock
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import org.imozerov.streetartview.storage.model.RealmArtObject
import org.imozerov.streetartview.storage.model.RealmAuthor
import org.imozerov.streetartview.storage.model.RealmString
import org.imozerov.streetartview.ui.model.ArtObjectUi
import rx.Observable
import java.util.*

/**
 * Created by imozerov on 05.02.16.
 */
class DataSource(internal var realm: Realm) {

    fun listArtObjects(): Observable<List<ArtObjectUi>> {
        return realm
                .allObjects(RealmArtObject::class.java)
                .asObservable()
                .cache()
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
        realm.beginTransaction()
        val realmAuthor = RealmAuthor()
        realmAuthor.id = "1"
        realmAuthor.name = "Vasya"
        realmAuthor.description = "Description"

        val realmArtObject = RealmArtObject()
        realmArtObject.author = realmAuthor
        realmArtObject.description = "Description should be a bit bigger than just a one word. That's why I'm writing this!"
        realmArtObject.name = "Name"
        realmArtObject.id = SystemClock.currentThreadTimeMillis().toString()
        realmArtObject.thumbPicUrl = "Pic"
        realmArtObject.picsUrls = RealmList<RealmString>()

        realm.copyToRealmOrUpdate(realmArtObject)
        realm.commitTransaction()
    }
}
