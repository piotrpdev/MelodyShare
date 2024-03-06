package dev.piotrp.melodyshare.models

import timber.log.Timber.i

var lastId = 0L
internal fun getId() = lastId++

class MelodyMemStore : MelodyStore  {
    private val melodies = ArrayList<MelodyModel>()

    override fun findAll(): List<MelodyModel> {
        return melodies
    }

    override fun create(melody: MelodyModel) {
        melody.id = getId()
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

    private fun logAll() {
        melodies.forEach{ i("$it") }
    }
}