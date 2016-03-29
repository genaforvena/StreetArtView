package org.imozerov.streetartview

import dagger.Component

import org.imozerov.streetartview.ui.detail.DetailArtObjectActivity
import org.imozerov.streetartview.ui.detail.ImageViewActivity
import org.imozerov.streetartview.ui.explore.base.ArtListPresenter

import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {
    fun inject(artListPresenter: ArtListPresenter)
    fun inject(detailArtObjectActivity: DetailArtObjectActivity)
    fun inject(imageViewActivity: ImageViewActivity)
}
