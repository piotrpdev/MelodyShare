package dev.piotrp.melodyshare.models

import android.os.Parcelable
import com.leff.midi.MidiFile
import com.leff.midi.MidiTrack
import com.leff.midi.event.meta.Tempo
import com.leff.midi.event.meta.TimeSignature
import kotlinx.parcelize.Parcelize
import java.io.File

/**
 * A class that holds all of the data needed to create a MIDI file along with some metadata.
 *
 * @property id A unique ID for the melody.
 * @property title The title of the melody.
 * @property description The description of the melody.
 * @property bpm The beats per minute of the melody, determines how fast it plays.
 * @property notes A list of [MelodyNote] objects that represent the individual notes of the melody.
 */
@Parcelize
data class MelodyModel(
    var id: Long = 0,
    var title: String = "",
    var description: String = "",
    var bpm: Float = 120f,
    var notes: ArrayList<MelodyNote> = ArrayList(),
) : Parcelable

/**
 * Writes the [MelodyModel] as a MIDI file to the specified location.
 *
 * This function creates a MIDI file with two tracks: one for the tempo and time signature,
 * and another for the melody notes. Each note from the [MelodyModel.notes] list is inserted into the
 * note track. The tempo is derived from the [MelodyModel.bpm] property.
 *
 * Slightly reworked version of an example from the `android-midi-lib` README.
 * MIT License, Copyright (c) 2017 Alex Leffelman
 * [(GitHub Source)](https://github.com/LeffelMania/android-midi-lib/blob/7cdd855c2b70d2074a53732e8a3979fe8e65e12a/README.md?plain=1#L67-L115)
 *
 * @param file The [File] object which the MIDI file will be written to.
 */
fun MelodyModel.writeMidiToFile(file: File) {
    val tracks: MutableList<MidiTrack> = ArrayList()

    val tempoTrack = MidiTrack()
    val ts = TimeSignature(0, 0, 4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION)
    tempoTrack.insertEvent(ts)

    // Tempo is 228
    val tempo = Tempo(0, 0, (60000000 / bpm).toInt())
    tempoTrack.insertEvent(tempo)

    tracks.add(tempoTrack)

    val noteTrack = MidiTrack()

    notes.forEach {
        noteTrack.insertNote(0, it.pitch, it.velocity, it.tick, it.duration)
    }

    tracks.add(noteTrack)

    val midi = MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks)

    midi.writeToFile(file)
}
