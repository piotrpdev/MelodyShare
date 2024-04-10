package dev.piotrp.melodyshare

import android.app.Application
import com.github.ajalt.timberkt.Timber
import com.github.ajalt.timberkt.i
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.piotrp.melodyshare.models.MelodyJsonStore
import dev.piotrp.melodyshare.models.MelodyModel
import dev.piotrp.melodyshare.models.MelodyNote
import dev.piotrp.melodyshare.models.MelodyStore
import timber.log.Timber.i
import java.io.File
import java.util.UUID

class MyApp : Application() {
    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    lateinit var melodies: MelodyStore

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
        i("MelodyShare started")

        i { "Setting db variable in app" }
        db = Firebase.firestore

        i { "Setting auth variable in app" }
        auth = Firebase.auth

        i { "Initializing MelodyStore" }
        // TODO: Check if this File could somehow get gc'd and cause a NullPointerException
        val jsonFile = File(filesDir.absolutePath + "/" + "melodies.json")
        melodies = MelodyJsonStore(jsonFile)

        // TODO: Make this behaviour opt-in with a setting?
        val melodyTitles = arrayOf("Rising Melody", "Lowering Melody")
        if (melodies.findAll().none { melodyTitles.contains(it.title) }) {
            i { "Adding example melodies to store" }

            // TODO: load example MIDI from asset?
            // TODO: don't create these if using melodies from Firestore
            melodies.create(generateRisingMelody())
            melodies.create(generateLoweringMelody())
        }
    }

    private fun generateRisingMelody(): MelodyModel {
        val risingNotes: ArrayList<MelodyNote> = ArrayList()

        for (i in 0..20) {
            // 21 is the lowest note that will play
            // 60 is middle C (C4)
            // 88 is the highest piano key
            val pitch = 60 + i
            val velocity = 100
            // 480 = quarter note
            val tick = (i * 480).toLong()
            val duration = 120.toLong()
            risingNotes.add(MelodyNote(UUID.randomUUID(), pitch, velocity, tick, duration))
        }

        return MelodyModel(UUID.randomUUID(), "Rising Melody", "The notes get higher", 228f, risingNotes)
    }

    private fun generateLoweringMelody(): MelodyModel {
        val loweringNotes: ArrayList<MelodyNote> = ArrayList()

        for (i in 0..20) {
            // 21 is the lowest note that will play
            // 60 is middle C (C4)
            // 88 is the highest piano key
            val pitch = 60 - i
            val velocity = 100
            // 480 = quarter note
            val tick = (i * 480).toLong()
            val duration = 120.toLong()
            loweringNotes.add(MelodyNote(UUID.randomUUID(), pitch, velocity, tick, duration))
        }

        return MelodyModel(UUID.randomUUID(), "Lowering Melody", "The notes get lower", 228f, loweringNotes)
    }

    companion object {
        val alphaNumSpaceRegex = Regex("^[a-zA-Z\\d\\s]*\$")

        fun isStringOnlyAlphaNumSpace(string: String): Boolean =
            string.isNotBlank() && string != "null" && alphaNumSpaceRegex.matchEntire(string) != null
    }
}
