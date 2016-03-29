package org.imozerov.streetartview.network

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.squareup.okhttp.Cache
import com.squareup.okhttp.OkHttpClient

import org.imozerov.streetartview.network.internal.RestClient

import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import retrofit.GsonConverterFactory
import retrofit.Retrofit

@Module
class NetModule(var mBaseUrl: String) {

    @Provides
    @Singleton
    internal fun providesSharedPreferences(application: Application): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(application)
    }

    @Provides
    @Singleton
    internal fun provideOkHttpCache(application: Application): Cache {
        val cacheSize = 10 * 1024 * 1024 // 10 MiB
        val cache = Cache(application.cacheDir, cacheSize.toLong())
        return cache
    }

    @Provides
    @Singleton
    internal fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        return gsonBuilder.create()
    }

    @Provides
    @Singleton
    internal fun provideOkHttpClient(cache: Cache): OkHttpClient {
        val client = OkHttpClient()
        client.cache = cache
        return client
    }

    @Provides
    @Singleton
    internal fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(gson)).baseUrl(mBaseUrl).client(okHttpClient).build()
        return retrofit
    }

    @Provides
    @Singleton
    internal fun provideRestClient(retrofit: Retrofit): RestClient {
        return RestClient(retrofit)
    }
}
