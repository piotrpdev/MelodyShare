package dev.piotrp.melodyshare.models

import timber.log.Timber.i

class MelodyMemStore : MelodyStore {
    private val melodies = ArrayList<MelodyModel>()

    override fun findAll(): List<MelodyModel> {
        return melodies.toMutableList()
    }

    override fun create(melody: MelodyModel) {
        melodies.add(melody)
        logAll()
    }

    override fun update(melody: MelodyModel) {
        val foundMelody: MelodyModel? = melodies.find { p -> p.id == melody.id }
        if (foundMelody != null) {
            foundMelody.title = melody.title
            foundMelody.description = melody.description
            foundMelody.notes = melody.notes
            logAll()
        }
    }

    override fun remove(melody: MelodyModel) {
        melodies.removeIf { p -> p.id == melody.id }
        logAll()
    }

    private fun logAll() {
        melodies.forEach { i("$it") }
    }
}
