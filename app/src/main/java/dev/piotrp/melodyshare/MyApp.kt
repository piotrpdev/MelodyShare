package dev.piotrp.melodyshare

import android.app.Application
import com.github.ajalt.timberkt.Timber
import dev.piotrp.melodyshare.models.PlacemarkMemStore
import dev.piotrp.melodyshare.models.PlacemarkModel
import timber.log.Timber.i

class MyApp  : Application() {
//    val placemarks = ArrayList<PlacemarkModel>()
    val placemarks = PlacemarkMemStore()
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
        i("MelodyShare started")

//        placemarks.add(PlacemarkModel("One", "About one..."))
//        placemarks.add(PlacemarkModel("Two", "About two..."))
//        placemarks.add(PlacemarkModel("Three", "About three..."))
    }
}