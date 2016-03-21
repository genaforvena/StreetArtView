package org.imozerov.streetartview.ui.explore.favourites

import org.imozerov.streetartview.ui.explore.base.AbstractListFragment
import org.imozerov.streetartview.ui.explore.base.ArtListPresenter
import org.imozerov.streetartview.ui.model.ArtObjectUi
import rx.Observable

/**
 * Created by imozerov on 21.03.16.
 */
class FavouritesListFragment : AbstractListFragment() {
    override fun createPresenter(): ArtListPresenter {
        return object : ArtListPresenter(this) {
            override fun fetchFromDataSource(): Observable<List<ArtObjectUi>> {
                return dataSource.listFavourites()
            }
        }
    }

    companion object {
        fun newInstance(): FavouritesListFragment {
            val fragment = FavouritesListFragment()
            return fragment
        }
    }
}