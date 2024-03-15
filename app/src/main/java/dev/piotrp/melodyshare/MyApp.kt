package dev.piotrp.melodyshare

import android.app.Application
import com.github.ajalt.timberkt.Timber
import com.github.ajalt.timberkt.i
import dev.piotrp.melodyshare.models.MelodyMemStore
import dev.piotrp.melodyshare.models.MelodyModel
import dev.piotrp.melodyshare.models.MelodyNote
import timber.log.Timber.i

class MyApp : Application() {
    val melodies = MelodyMemStore()

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
        i("MelodyShare started")

        i { "Adding example melody to store" }

        // TODO: load example MIDI from asset?
        melodies.create(generateRisingMelody())
        melodies.create(generateLoweringMelody())
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
            risingNotes.add(MelodyNote(i, pitch, velocity, tick, duration))
        }

        return MelodyModel(0, "Rising Melody", "Rising Melody", 228f, risingNotes)
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
            loweringNotes.add(MelodyNote(i, pitch, velocity, tick, duration))
        }

        return MelodyModel(0, "Lowering Melody", "Lowering Melody", 228f, loweringNotes)
    }

    companion object {
        val alphaNumSpaceRegex = Regex("^[a-zA-Z\\d\\s]*\$")

        fun isStringOnlyAlphaNumSpace(string: String): Boolean =
            string.isNotBlank() && string != "null" && alphaNumSpaceRegex.matchEntire(string) != null
    }
}
