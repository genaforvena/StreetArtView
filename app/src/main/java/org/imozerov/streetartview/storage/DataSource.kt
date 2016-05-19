package org.imozerov.streetartview.storage

import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.Sort
import org.imozerov.streetartview.network.model.Artwork
import org.imozerov.streetartview.storage.model.RealmArtObject
import org.imozerov.streetartview.storage.model.copyDataFromJson
import org.imozerov.streetartview.ui.model.ArtObjectUi
import rx.Observable
import rx.schedulers.Schedulers
import java.util.*

/**
 * Created by imozerov on 05.02.16.
 */
class DataSource() : IDataSource {
    val TAG = "DataSource"
    // This realm instance should be used only to read data
    val readOnlyRealm = Realm.getDefaultInstance()

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

    override fun insert(artworks: List<Artwork>) {
        Observable.from(artworks)
                .map {
                    val realmArtObject = RealmArtObject()
                    realmArtObject.copyDataFromJson(it)
                    return@map realmArtObject
                }
                .toList()
                .subscribeOn(Schedulers.io())
                .subscribe { realmArtWorks ->
                    with (Realm.getDefaultInstance()) {
                        use {
                            val favouriteIds = where(RealmArtObject::class.java)
                                    .equalTo("isFavourite", true).findAll().map { it.id }
                            realmArtWorks.filter { favouriteIds.contains(it.id) }.forEach { it.isFavourite = true }
                            batchInsertOrUpdate(realmArtWorks)
                        }
                    }
                }
    }

    fun insert(artwork: Artwork) {
        val list = ArrayList<Artwork>()
        list.add(artwork)
        insert(list)
    }

    override fun setFavourite(artObjectId: String, isFavourite: Boolean) {
        Observable.create<Unit> {
            with(Realm.getDefaultInstance()) {
                use {
                    val artObjectInRealm = where(RealmArtObject::class.java)
                            .equalTo("id", artObjectId)
                            .findFirst()

                    beginTransaction()
                    artObjectInRealm.isFavourite = isFavourite
                    commitTransaction()
                }
            }
        }.subscribeOn(Schedulers.io())
                .subscribe()
    }

    private fun realmToUi(realmObjects: RealmResults<RealmArtObject>): List<ArtObjectUi> {
        val listOfArtObjects = ArrayList<ArtObjectUi>(realmObjects.size)
        realmObjects.filter { it.picsUrls!!.isNotEmpty() }
                .forEach { listOfArtObjects.add(ArtObjectUi(it)) }
        return listOfArtObjects
    }
}

private fun Realm.batchInsertOrUpdate(realmObjects: List<RealmObject>) {
    beginTransaction()
    copyToRealmOrUpdate(realmObjects)
    commitTransaction()
}