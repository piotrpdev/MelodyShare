package dev.piotrp.melodyshare

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dev.piotrp.melodyshare.models.MelodyModel
import dev.piotrp.melodyshare.models.MelodyNote
import dev.piotrp.melodyshare.models.writeMidiToFile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.UUID

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class MelodyModelTest {
    fun generateRisingMelody(): MelodyModel {
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
            risingNotes.add(MelodyNote(UUID.randomUUID().toString(), pitch, velocity, tick, duration))
        }

        return MelodyModel("12345", "Rising Melody", "The notes get higher", 228f, risingNotes)
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("dev.piotrp.melodyshare", appContext.packageName)
    }

    @Test
    fun `MelodyModel is created with correct default parameters`() {
        val melody = MelodyModel()

        assertTrue(melody.id.isNotEmpty())
        assertTrue(melody.title.isEmpty())
        assertTrue(melody.description.isEmpty())
        assertEquals(melody.bpm, 120f)
        assertEquals(melody.notes.size, 0)
        assertEquals(melody.likedBy.size, 0)
    }

    @Test
    fun `MelodyModel matches parameters passed in constructor`() {
        val melody = generateRisingMelody()

        assertEquals(melody.id, "12345")
        assertEquals(melody.title, "Rising Melody")
        assertEquals(melody.description, "The notes get higher")
        assertEquals(melody.bpm, 228f)
        assertEquals(melody.notes.size, 21)
        assertEquals(melody.likedBy.size, 0)
    }

    @Test
    fun `Writing MelodyModel to a MIDI file works`() {
        val melody = generateRisingMelody()

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val filePath = appContext.filesDir.absolutePath + "/" + "${melody.id}.mid"
        val midiFile = File(filePath)

        if (midiFile.exists()) midiFile.delete()

        assertFalse(midiFile.exists())

        melody.writeMidiToFile(midiFile)

        assertTrue(midiFile.exists())
    }
}
