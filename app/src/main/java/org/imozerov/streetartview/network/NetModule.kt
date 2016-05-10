package org.imozerov.streetartview.network

import android.app.Application
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.squareup.okhttp.Cache
import com.squareup.okhttp.OkHttpClient
import dagger.Module
import dagger.Provides
import org.imozerov.streetartview.network.internal.RestClient
import retrofit.GsonConverterFactory
import retrofit.Retrofit
import retrofit.RxJavaCallAdapterFactory
import javax.inject.Singleton

@Module
class NetModule(private val mBaseUrl: String) {
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
        val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(gson)).addCallAdapterFactory(RxJavaCallAdapterFactory.create()).baseUrl(mBaseUrl).client(okHttpClient).build()
        return retrofit
    }

    @Provides
    @Singleton
    internal fun provideRestClient(retrofit: Retrofit): RestClient {
        return RestClient(retrofit)
    }
}
