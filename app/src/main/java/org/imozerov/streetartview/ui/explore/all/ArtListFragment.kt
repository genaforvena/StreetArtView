package org.imozerov.streetartview.ui.explore.all

import org.imozerov.streetartview.ui.explore.base.AbstractListFragment
import org.imozerov.streetartview.ui.explore.base.ArtListPresenter

class ArtListFragment : AbstractListFragment() {
    override val presenter: ArtListPresenter = AllPresenter()

    companion object {
        fun newInstance(): ArtListFragment {
            val fragment = ArtListFragment()
            return fragment
        }
    }
}