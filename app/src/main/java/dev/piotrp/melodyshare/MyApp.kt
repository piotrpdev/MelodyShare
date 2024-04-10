package dev.piotrp.melodyshare

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import com.github.ajalt.timberkt.Timber
import com.github.ajalt.timberkt.d
import com.github.ajalt.timberkt.i
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.ktx.Firebase
import dev.piotrp.melodyshare.models.MelodyModel
import dev.piotrp.melodyshare.models.MelodyNote
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import timber.log.Timber.i
import java.util.UUID

class MyApp : Application() {
    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    lateinit var fid: String
    lateinit var connectivityManager: ConnectivityManager

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
        i("MelodyShare started")

        runBlocking {
            fid = FirebaseInstallations.getInstance().id.await()
        }

        d { "FID: $fid" }

        connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        i { "Setting db variable in app" }
        db = Firebase.firestore

        i { "Setting auth variable in app" }
        auth = Firebase.auth
    }

    companion object {
        val alphaNumSpaceRegex = Regex("^[a-zA-Z\\d\\s]*\$")

        fun isStringOnlyAlphaNumSpace(string: String): Boolean =
            string.isNotBlank() && string != "null" && alphaNumSpaceRegex.matchEntire(string) != null
    }
}
