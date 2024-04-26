package dev.piotrp.melodyshare.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ajalt.timberkt.d
import com.github.ajalt.timberkt.i
import com.google.android.material.snackbar.Snackbar
import dev.piotrp.melodyshare.MyApp
import dev.piotrp.melodyshare.MyApp.Companion.isStringOnlyAlphaNumSpace
import dev.piotrp.melodyshare.R
import dev.piotrp.melodyshare.adapters.MelodyNoteAdapter
import dev.piotrp.melodyshare.adapters.MelodyNoteListener
import dev.piotrp.melodyshare.databinding.ActivityMelodyChangeBinding
import dev.piotrp.melodyshare.models.MelodyModel
import dev.piotrp.melodyshare.models.MelodyNote
import java.util.UUID

class MelodyChangeActivity : AppCompatActivity(), MelodyNoteListener {
    private lateinit var binding: ActivityMelodyChangeBinding
    private var melody = MelodyModel()
    private lateinit var app: MyApp

    // This is a test comment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMelodyChangeBinding.inflate(layoutInflater)
        setSupportActionBar(binding.topAppBar)
        binding.topAppBar.title = getString(R.string.button_message)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager

        app = application as MyApp
        i { "MainActivity started." }

        if (intent.hasExtra("melody_edit")) {
            @Suppress("DEPRECATION")
            melody = intent.extras?.getParcelable("melody_edit")!!
            binding.titleTextField.editText?.setText(melody.title)
            binding.descriptionTextField.editText?.setText(melody.description)

            binding.button.text = getString(R.string.save_melody)
        }

        binding.recyclerView.adapter = MelodyNoteAdapter(melody.notes, this)

        binding.titleTextField.editText?.doAfterTextChanged {
            val titleValid = isStringOnlyAlphaNumSpace(it.toString().trim())
            if (titleValid) {
                binding.titleTextField.error = null
                binding.titleTextField.isErrorEnabled = false
            } else {
                binding.titleTextField.isErrorEnabled = true
                binding.titleTextField.error = getString(R.string.alpha_num_space_error)
            }
        }

        binding.descriptionTextField.editText?.doAfterTextChanged {
            val descriptionValid = isStringOnlyAlphaNumSpace(it.toString().trim())
            if (descriptionValid) {
                binding.descriptionTextField.error = null
                binding.descriptionTextField.isErrorEnabled = false
            } else {
                binding.descriptionTextField.isErrorEnabled = true
                binding.descriptionTextField.error = getString(R.string.alpha_num_space_error)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_melody_change, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> {
                setResult(RESULT_CANCELED)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onAddMelodyClicked(view: View) {
        val titleText = binding.titleTextField.editText?.text.toString().trim()
        val descriptionText = binding.descriptionTextField.editText?.text.toString().trim()

        val titleValid = isStringOnlyAlphaNumSpace(titleText)
        val descriptionValid = isStringOnlyAlphaNumSpace(descriptionText)

        if (!titleValid || !descriptionValid) {
            i { "Not saving melody because title or description isn't valid" }
            Snackbar
                .make(binding.root, R.string.title_description_error, Snackbar.LENGTH_LONG)
                .show()

            return
        }

        melody.title = titleText
        melody.description = descriptionText

        d { "Returning melody from onAddMelodyClicked(): $melody" }

        val replyIntent = Intent()

        if (intent.hasExtra("melody_edit")) replyIntent.putExtra("melody_edit", true)
        replyIntent.putExtra("melody", melody.copy())

        setResult(RESULT_OK, replyIntent)
        finish()
    }

    @SuppressLint("NotifyDataSetChanged")
    @Suppress("UNUSED_PARAMETER")
    fun onAddNoteClicked(view: View) {
        i { "Adding MelodyNote to Melody" }
        val id = UUID.randomUUID()
        val tick = melody.notes.maxOfOrNull { it.tick }?.plus(480) ?: 0

        melody.notes.add(MelodyNote(id.toString(), 60, 100, tick, 120))

        // TODO: Might need to notify, but not doing it works fine for some reason

        binding.recyclerView.scrollToPosition(melody.notes.size - 1)
    }

    @SuppressLint("NotifyDataSetChanged")
    @Suppress("UNUSED_PARAMETER")
    fun onRemoveNoteClicked(view: View) {
        i { "Removing MelodyNote from Melody" }
        val lastNote = melody.notes.lastOrNull()

        if (lastNote == null) {
            i { "No MelodyNote to remove" }
            return
        }

        melody.notes.remove(lastNote)

        // TODO: Might need to notify, but not doing it works fine for some reason

        // TODO: Maybe use itemCount from adapter here instead
        val notesSize = melody.notes.size

        if (notesSize > 0) {
            binding.recyclerView.scrollToPosition(melody.notes.size - 1)
        }
    }

    override fun onMelodyNotePitchTextChanged(
        melodyNote: MelodyNote,
        editable: Editable?,
    ): Boolean {
        i { "Pitch text changed for MelodyNote (ID: ${melodyNote.id}). Old: ${melodyNote.pitch}, New: $editable" }
        val parsedValue = editable.toString()
        val unformattedPitch = formattedMelodyPitchToInt(parsedValue) ?: return false

        melodyNote.pitch = unformattedPitch

        return true
    }

    override fun onMelodyNoteTickTextChanged(
        melodyNote: MelodyNote,
        editable: Editable?,
    ): Boolean {
        i { "Tick text changed for MelodyNote (ID: ${melodyNote.id}). Old: ${melodyNote.tick}, New: $editable" }
        val parsedValue = editable.toString()
        val unformattedTick = formattedMelodyTickToLong(parsedValue) ?: return false

        melodyNote.tick = unformattedTick

        return true
    }

    override fun onMelodyNoteDurationTextChanged(
        melodyNote: MelodyNote,
        editable: Editable?,
    ): Boolean {
        i { "Duration text changed for MelodyNote (ID: ${melodyNote.id}). Old: ${melodyNote.duration}, New: $editable" }
        val parsedValue = editable.toString()
        val unformattedDuration = formattedMelodyDurationToLong(parsedValue) ?: return false

        melodyNote.duration = unformattedDuration

        return true
    }

    companion object {
        // TODO: Implement sharps and flats: ^[A-G][#|b]?[3-5]$
        private val melodyPitchRegex = Regex("^[A-G][3-5]\$")
        private val melodyTickRegex = Regex("^[1-8].[1-3]\$")

        fun formattedMelodyPitchToInt(formattedPitch: String): Int? {
            if (melodyPitchRegex.matchEntire(formattedPitch) == null) return null

            // C1 = 24
            // C2 = 36
            val octave = formattedPitch[1].digitToIntOrNull()?.times(12)?.plus(12)
            // ? Avoid sharps and flats
            val note = "C,D,EF,G,A,B".indexOf(formattedPitch[0])
            if (octave == null || note == -1) return null

            val pitch = octave + note

            // Restrict to two octaves (C3 - C5)
            // see MelodyView for more info
            if (pitch !in 48..72) return null

            return pitch
        }

        fun intToFormattedMelodyPitch(number: Int): String {
            // C1 = 24
            // C2 = 36
            val octave = 1 + ((number - 24) / 12)
            val note = (number - 24) % 12

            // ? Avoid sharps and flats
            val formattedNote = "C.D.EF.G.A.B"[note]

            return formattedNote + octave.toString()
        }

        fun formattedMelodyTickToLong(formattedTick: String): Long? {
            if (melodyTickRegex.matchEntire(formattedTick) == null) return null

            val parsedSplitValue = formattedTick.split(".")
            if (parsedSplitValue.size != 2) return null

            val num = parsedSplitValue[0].toIntOrNull()?.minus(1)?.times(480)
            val remainder = parsedSplitValue[1].toIntOrNull()?.minus(1)?.times((480 / 4))
            if (num == null || remainder == null) return null

            return (num + remainder).toLong()
        }

        fun longToFormattedMelodyTick(number: Long): String {
            val num = 1 + number / 480
            val remainder = 1 + ((number % 480) / (480 / 4))

            return "$num.$remainder"
        }

        fun formattedMelodyDurationToLong(formattedDuration: String): Long? {
            val parsedValue = formattedDuration.toIntOrNull() ?: return null

            if (parsedValue !in 1..4) return null

            return (parsedValue * (480 / 4)).toLong()
        }

        fun longToFormattedMelodyDuration(number: Long): String {
            return (number / (480 / 4)).toString()
        }
    }
}
