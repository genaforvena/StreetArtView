package org.imozerov.streetartview.ui.explore.favourites

import org.imozerov.streetartview.ui.explore.base.ArtListPresenter
import org.imozerov.streetartview.ui.model.ArtObjectUi
import rx.Observable

/**
 * Created by imozerov on 18.04.16.
 */
class FavouritesPresenter : ArtListPresenter() {
    override fun fetchFromDataSource(): Observable<List<ArtObjectUi>> {
        return dataSource.listFavourites()
    }
}