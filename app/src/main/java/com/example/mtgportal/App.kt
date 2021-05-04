package com.example.mtgportal

import android.app.Application
import androidx.room.Room
import com.example.mtgportal.api.ApiService
import com.example.mtgportal.database.AppDatabase
import com.example.mtgportal.injection.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

import timber.log.Timber.DebugTree


class App : Application() {

    //region init
    override fun onCreate() {
        super.onCreate()
        instance = this
        initTimber()
        initKoin()
    }

    private fun initKoin() {
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(
                apiModule,
                viewModelModule,
                repositoryModule,
                networkModule,
                databaseModule
            )
        }
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }
    //endregion

    companion object {
        lateinit var instance: App
            private set
    }
}