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
        val names: Array<String> = arrayOf("Vasya", "Nikita", "Dima", "Alexander", "Sergey",
                "Vlad", "Andrey", "Artem", "Ivan", "Anton", "Maxim")
        val lastNames: Array<String> = arrayOf("Smirnov", "Ivanov", "Kuznetsov","Popov", "Sokolov",
                "Lebedev", "Kozlov", "Novikov", "Morozov", "Petrov")

        val firstArtPart: Array<String> = arrayOf("The Last", "The Starry", "The Persistence of",
                "American", "The Creation of", "The Art of", "The School of", "Portrait of",
                "Massacre of", "The Treachery")
        val lastArtPart: Array<String> = arrayOf("Supper", "Night", "Memory", "Gothic", "Adam",
                "Painting", "Athens", "a Man", "the Innocents", "Images")

        val realmAuthor = RealmAuthor()
        realmAuthor.id = SystemClock.currentThreadTimeMillis().toString()
        realmAuthor.name = randomFrom(names) + " " + randomFrom(lastNames)
        realmAuthor.description = "The best artist in the world"

        val realmArtObject = RealmArtObject()
        realmArtObject.author = realmAuthor
        realmArtObject.description = "The Moderniest Art Work Ever"
        realmArtObject.name = randomFrom(firstArtPart) + " " + randomFrom(lastArtPart)
        realmArtObject.id = SystemClock.currentThreadTimeMillis().toString()
        realmArtObject.thumbPicUrl = "Pic"
        realmArtObject.picsUrls = RealmList<RealmString>()
        realmArtObject.lat = getRandomBetween(56.26, 56.33)
        realmArtObject.lng = getRandomBetween(43.86, 44.05)

        handler.post { realm.insertOrUpdate(realmArtObject) }
    }
}

private fun getRandomBetween(from: Double, to: Double): Double{
    return from + (Math.random() * (to - from))
}

private fun randomFrom(list: Array<String>): String{
    val position: Int = (Math.random() * list.size).toInt()
    return list.elementAt(position)
}

private fun Realm.insertOrUpdate(realmObject: RealmObject) {
    beginTransaction()
    copyToRealmOrUpdate(realmObject)
    commitTransaction()
}

private fun Realm.batchInsertOrUpdate(realmObjects: List<RealmObject>) {
    beginTransaction()
    copyToRealmOrUpdate(realmObjects)
    commitTransaction()
}
