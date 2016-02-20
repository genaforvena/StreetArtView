package org.imozerov.streetartview.storage

import android.location.Location
import android.net.Uri
import android.os.Handler
import android.os.SystemClock
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

    fun getArtObject(id: String): ArtObjectUi {
        return ArtObjectUi(realm
                .where(RealmArtObject::class.java)
                .equalTo("id", id)
                .findFirst())
    }

    fun addArtObject(newName: String, newAuthor: String, newImageUrl: Uri?, location: Location?) {
        val realmAuthor = RealmAuthor()
        with (realmAuthor) {
            id = SystemClock.currentThreadTimeMillis().toString()
            name = newAuthor
            photo = "http://photos.state.gov/libraries/media/788/images/500x500-sample.jpg"
        }

        val realmAuthors = RealmList<RealmAuthor>()
        realmAuthors.add(realmAuthor)

        val realmLocation = RealmLocation()
        with(realmLocation) {
            address = "Some address, 34"
            lat = location!!.latitude
            lng = location.longitude
        }

        val realmArtObject = RealmArtObject()
        with (realmArtObject) {
            authors = realmAuthors
            description = "The Moderniest Art Work Ever"
            name = newName
            id = SystemClock.currentThreadTimeMillis().toString()
            thumbPicUrl = newImageUrl.toString()
            picsUrls = RealmList<RealmString>()
            setLocation(realmLocation)
        }

        handler.post { realm.insertOrUpdate(realmArtObject) }
    }

    fun addArtObjectStub() {
        val names = arrayOf("Vasya", "Nikita", "Dima", "Alexander", "Sergey",
                "Vlad", "Andrey", "Artem", "Ivan", "Anton", "Maxim")
        val lastNames = arrayOf("Smirnov", "Ivanov", "Kuznetsov", "Popov", "Sokolov",
                "Lebedev", "Kozlov", "Novikov", "Morozov", "Petrov")

        val firstArtPart = arrayOf("The Last", "The Starry", "The Persistence of",
                "American", "The Creation of", "The Art of", "The School of", "Portrait of",
                "Massacre of", "The Treachery")
        val lastArtPart = arrayOf("Supper", "Night", "Memory", "Gothic", "Adam",
                "Painting", "Athens", "a Man", "the Innocents", "Images")

        val realmAuthor = RealmAuthor()
        with (realmAuthor) {
            id = SystemClock.currentThreadTimeMillis().toString()
            name = "${randomFrom(names)} ${randomFrom(lastNames)}"
            photo = "http://photos.state.gov/libraries/media/788/images/500x500-sample.jpg"
        }

        val realmAuthors = RealmList<RealmAuthor>()
        realmAuthors.add(realmAuthor)

        val realmLocation = RealmLocation()
        with(realmLocation) {
            address = "Some address, 34"
            lat = getRandomBetween(56.26, 56.33)
            lng = getRandomBetween(43.86, 44.05)
        }

        val realmArtObject = RealmArtObject()
        with (realmArtObject) {
            authors = realmAuthors
            description = "The Moderniest Art Work Ever"
            name = "${randomFrom(firstArtPart)} ${randomFrom(lastArtPart)}"
            id = SystemClock.currentThreadTimeMillis().toString()
            thumbPicUrl = "Pic"
            picsUrls = RealmList<RealmString>()
            location = realmLocation
        }

        handler.post { realm.insertOrUpdate(realmArtObject) }
    }
}

private fun getRandomBetween(from: Double, to: Double): Double {
    return from + (Math.random() * (to - from))
}

private fun randomFrom(list: Array<String>): String {
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
