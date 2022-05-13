package com.nametag.githubtestapplication

import android.app.Application
import com.nametag.githubtestapplication.di.networkModule
import com.nametag.githubtestapplication.di.providers
import com.nametag.githubtestapplication.di.repositoriesModule
import com.nametag.githubtestapplication.di.viewModelsModule
import org.koin.android.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class TestApplication : KotlinApplication() {

    companion object{
        lateinit var instance: TestApplication
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}

open class KotlinApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@KotlinApplication)
            modules(networkModule, viewModelsModule, repositoriesModule, providers)
        }
    }
}