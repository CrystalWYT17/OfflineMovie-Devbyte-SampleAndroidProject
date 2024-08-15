package com.example.offlinemovie_devbyte

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import timber.log.Timber

class MainActivity : Application() {
    override fun onCreate() {
        super.onCreate()
//        setContentView(R.layout.)
        Timber.plant(Timber.DebugTree())
    }
}
