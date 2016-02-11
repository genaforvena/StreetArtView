package org.imozerov.streetartview.ui.explore.interfaces

import org.imozerov.streetartview.ui.model.ArtObjectUi

/**
 * Created by imozerov on 11.02.16.
 */
interface ArtView {
    fun showArtObjects(artObjectUis: List<ArtObjectUi>)
}