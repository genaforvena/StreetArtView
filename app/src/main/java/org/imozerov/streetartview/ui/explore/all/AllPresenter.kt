package org.imozerov.streetartview.ui.explore.all

import org.imozerov.streetartview.ui.explore.base.ArtListPresenter
import org.imozerov.streetartview.ui.model.ArtObjectUi
import rx.Observable

/**
 * Created by imozerov on 18.04.16.
 */
class AllPresenter : ArtListPresenter() {
    override fun fetchData(): Observable<List<ArtObjectUi>> {
        return dataSource.listArtObjects()
    }
}