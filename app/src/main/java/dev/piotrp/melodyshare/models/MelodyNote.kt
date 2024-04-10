package dev.piotrp.melodyshare.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Represents a single note in a melody, holds some MIDI properties.
 *
 * @property id A unique ID for the note.
 * @property pitch The MIDI pitch value of the note, determines its frequency. 21 is the lowest note that will
 * play, 60 is middle C (C4), 88 is the highest piano key.
 * @property velocity The MIDI velocity of the note, determines its volume. 0 is silent, 127 is
 * as loud as possible.
 * @property tick The start time of the note in MIDI ticks, determines when the note is played. See
 * ["Converting MIDI ticks to actual playback seconds"](https://stackoverflow.com/a/2038364/19020549).
 * @property duration The duration of the note in MIDI ticks, determines how long the note plays.
 *
 * @see [MelodyModel]
 */
@Serializable
@Parcelize
data class MelodyNote(
    var id: String = UUID.randomUUID().toString(),
    var pitch: Int = 0,
    var velocity: Int = 0,
    var tick: Long = 0L,
    var duration: Long = 0L,
) : Parcelable
