package com.nametag.githubtestapplication.di

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.nametag.githubtestapplication.data.network.ApiService
import com.nametag.githubtestapplication.data.network.IRemoteRepo
import com.nametag.githubtestapplication.data.network.RemoteRepoImpl
import com.nametag.githubtestapplication.data.preferences.IPrefDataSource
import com.nametag.githubtestapplication.data.preferences.PrefDataSourceImpl
import com.nametag.githubtestapplication.data.providers.GithubProviderImpl
import com.nametag.githubtestapplication.data.providers.IGithubProvider
import com.nametag.githubtestapplication.flow.MainVM
import com.nametag.githubtestapplication.utils.CollectionDeserializer
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.BuildConfig
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


val repositoriesModule = module {
    single { RemoteRepoImpl(get()) } bind IRemoteRepo::class
    single { PrefDataSourceImpl(get()) } bind IPrefDataSource::class
}

val viewModelsModule = module {
    viewModel { MainVM(get()) }
}

val networkModule = module {
    factory { provideLoggingInterceptor() }
    factory { provideOkHttpClient(get()) }
    single { provideRetrofit(get()) }
    single { provideAPI(get()) }
}

val providers  = module {
    single { GithubProviderImpl(get(), get()) } bind IGithubProvider::class
}
fun provideLoggingInterceptor(): HttpLoggingInterceptor {

    val interceptor = HttpLoggingInterceptor(PrettyLoggerRemote())
    interceptor.level =
        if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    return interceptor
}

fun provideOkHttpClient(
    interceptor: HttpLoggingInterceptor
): OkHttpClient {
    val okHttpClient = OkHttpClient().newBuilder().apply {
        addInterceptor(interceptor)
        connectTimeout(30, TimeUnit.SECONDS)
        readTimeout(30, TimeUnit.SECONDS)
        writeTimeout(30, TimeUnit.SECONDS)
    }
    return okHttpClient.build()
}

fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    val retrofit = Retrofit.Builder().apply {
        baseUrl(API_URL_PROD)
        client(okHttpClient)
        addConverterFactory(provideGson())
        addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    }
    return retrofit.build()
}

fun provideAPI(retrofit: Retrofit): ApiService {
    return retrofit.create(ApiService::class.java)
}

fun provideGson(): GsonConverterFactory {

    val gson = GsonBuilder()
        .serializeNulls()
        .setPrettyPrinting()
        .registerTypeAdapter(Collection::class.java, CollectionDeserializer())
        .create()

    return GsonConverterFactory.create(gson)
}