package dev.piotrp.melodyshare.models

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import timber.log.Timber.i
import java.io.File

class MelodyJsonStore(private val jsonFile: File) : MelodyStore {
    private var melodies = ArrayList<MelodyModel>()

    init {
        if (jsonFile.exists()) {
            readFromJsonFile()
        } else {
            writeToJsonFile()
        }
    }

    override fun findAll(): List<MelodyModel> {
        readFromJsonFile()
        return melodies.toMutableList()
    }

    override fun create(melody: MelodyModel) {
        melodies.add(melody)
        writeToJsonFile()
        logAll()
    }

    override fun update(melody: MelodyModel) {
        val foundMelody: MelodyModel? = melodies.find { p -> p.id == melody.id }
        if (foundMelody != null) {
            foundMelody.title = melody.title
            foundMelody.description = melody.description
            foundMelody.notes = melody.notes
            writeToJsonFile()
            logAll()
        }
    }

    override fun remove(melody: MelodyModel) {
        melodies.removeIf { p -> p.id == melody.id }
        writeToJsonFile()
        logAll()
    }

    private fun logAll() {
        melodies.forEach { i("$it") }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun writeToJsonFile() {
        jsonFile.outputStream().use {
            Json.encodeToStream(melodies, it)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun readFromJsonFile() {
        jsonFile.inputStream().use {
            melodies = Json.decodeFromStream(it)
        }
    }
}
