package org.imozerov.streetartview.ui.explore.list

import org.imozerov.streetartview.ui.explore.base.ArtListPresenter
import org.imozerov.streetartview.ui.explore.base.AbstractListFragment

class ArtListFragment : AbstractListFragment() {
    override fun createPresenter(): ArtListPresenter {
        return ArtListPresenter(this)
    }

    companion object {
        fun newInstance(): ArtListFragment {
            val fragment = ArtListFragment()
            return fragment
        }
    }
}