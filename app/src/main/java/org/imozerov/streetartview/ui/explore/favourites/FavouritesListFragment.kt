package org.imozerov.streetartview.ui.explore.favourites

import org.imozerov.streetartview.ui.explore.base.AbstractListFragment

/**
 * Created by imozerov on 21.03.16.
 */
class FavouritesListFragment : AbstractListFragment() {
    override val presenter = FavouritesPresenter()

    companion object {
        fun newInstance(): FavouritesListFragment {
            val fragment = FavouritesListFragment()
            return fragment
        }
    }
}