package com.example.mtgportal

import android.app.Application
import com.example.mtgportal.api.ApiService
import timber.log.Timber

import timber.log.Timber.DebugTree




class App : Application() {

    //region declaration
    val apiService by lazy { ApiService.create() }
    //endregion

    //region init
    override fun onCreate() {
        super.onCreate()
        instance = this
        setTimber()
    }

    private fun setTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }
    //endregion

    companion object{
        lateinit var instance: App
        private set
    }
}