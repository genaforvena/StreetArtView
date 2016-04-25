package org.imozerov.streetartview.network

import org.imozerov.streetartview.AppModule

import javax.inject.Singleton

import dagger.Component

@Singleton
@Component(modules = arrayOf(AppModule::class, NetModule::class))
interface NetComponent {
    fun inject(fetchService: FetchService)
}
