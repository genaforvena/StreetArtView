package org.imozerov.streetartview.ui.explore.interfaces

import org.imozerov.streetartview.ui.model.ArtObject


/**
 * Created by imozerov on 11.02.16.
 */
interface ArtView {
    fun showArtObjects(artObjectUis: List<ArtObject>)
    fun stopRefreshIndication()
}