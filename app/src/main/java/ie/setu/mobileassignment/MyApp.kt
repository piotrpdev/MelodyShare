package ie.setu.mobileassignment

import android.app.Application
import com.github.ajalt.timberkt.Timber

class MyApp  : Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}