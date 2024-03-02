package dev.piotrp.melodyshare

import android.app.Application
import com.github.ajalt.timberkt.Timber
import dev.piotrp.melodyshare.models.MelodyMemStore
import timber.log.Timber.i

class MyApp  : Application() {
    val melodies = MelodyMemStore()
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
        i("MelodyShare started")
    }
}