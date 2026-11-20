package com.scrollcontrol.app

import android.app.Application
import com.scrollcontrol.app.data.PreferencesRepository

class ScrollControlApp : Application() {

    lateinit var preferencesRepository: PreferencesRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        preferencesRepository = PreferencesRepository(this)
    }

    companion object {
        lateinit var instance: ScrollControlApp
            private set
    }
}
