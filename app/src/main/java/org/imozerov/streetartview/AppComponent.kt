package org.imozerov.streetartview

import dagger.Component
import org.imozerov.streetartview.location.LocationService
import org.imozerov.streetartview.network.FetchService
import org.imozerov.streetartview.network.NetModule
import org.imozerov.streetartview.ui.detail.DetailArtObjectActivity
import org.imozerov.streetartview.ui.detail.ImageViewActivity
import org.imozerov.streetartview.ui.explore.ExploreArtActivity
import org.imozerov.streetartview.ui.explore.base.ArtListPresenter
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, NetModule::class))
interface AppComponent {
    fun inject(locationService: LocationService)
    fun inject(fetchService: FetchService)
    fun inject(artListPresenter: ArtListPresenter)
    fun inject(exploreArtActivity: ExploreArtActivity)
    fun inject(detailArtObjectActivity: DetailArtObjectActivity)
    fun inject(imageViewActivity: ImageViewActivity)
}
