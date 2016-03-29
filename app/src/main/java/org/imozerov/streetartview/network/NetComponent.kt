package org.imozerov.streetartview.network

import dagger.Component
import org.imozerov.streetartview.AppModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, NetModule::class))
interface NetComponent {
    fun inject(fetchService: FetchService)
}
