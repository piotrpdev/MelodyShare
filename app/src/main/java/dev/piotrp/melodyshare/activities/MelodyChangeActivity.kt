package dev.piotrp.melodyshare.activities

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

// TODO: Change to better name
class MelodyChangeActivity : AppCompatActivity(), MelodyNoteListener {
    private lateinit var binding: ActivityMelodyChangeBinding
    private var buttonPressedCount: Int = 0
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
        binding.recyclerView.adapter = MelodyNoteAdapter(ArrayList(), this)

        app = application as MyApp
        i { "MainActivity started." }

        if (intent.hasExtra("melody_edit")) {
            @Suppress("DEPRECATION")
            melody = intent.extras?.getParcelable("melody_edit")!!
            binding.titleTextField.editText?.setText(melody.title)
            binding.descriptionTextField.editText?.setText(melody.description)
            binding.recyclerView.adapter = MelodyNoteAdapter(melody.notes, this)

            binding.button.text = getString(R.string.save_melody)
        }

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
        buttonPressedCount++

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

        val messageId: Int

        if (intent.hasExtra("melody_edit")) {
            messageId = R.string.button_clicked_message_saved

            app.melodies.update(melody.copy())
        } else {
            messageId = R.string.button_clicked_message

            app.melodies.create(melody.copy())
        }

        val message = getString(messageId, melody.title)

        Snackbar
            .make(binding.root, message, Snackbar.LENGTH_LONG)
            .show()

        i { "Melody added/saved with title \"${melody.title}\" and description \"${melody.description}\" " }
        d { "Full melody ArrayList: ${app.melodies.findAll()}" }

        setResult(RESULT_OK)
        finish()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onAddNoteClicked(view: View) {
        i { "Adding MelodyNote to Melody" }
        val id = UUID.randomUUID()
        val tick = melody.notes.maxOfOrNull { it.tick }?.plus(480) ?: 0

        melody.notes.add(MelodyNote(id, 60, 100, tick, 120))

        // FIXME
        // notifyItemRangeChanged causes graphical issues, probably cause
        // recycling is disabled.
        // also, replacing the whole adapter uses a lot of resources
        // and is slow
//        (binding.recyclerView.adapter)?.
//        notifyItemRangeChanged(0, melody.notes.size)
        binding.recyclerView.adapter = MelodyNoteAdapter(melody.notes, this)

        // Setting a new adapter sends us back to the top, this is nice
        // to have regardless though
        binding.recyclerView.scrollToPosition(melody.notes.size - 1)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onRemoveNoteClicked(view: View) {
        i { "Removing MelodyNote from Melody" }
        val lastNote = melody.notes.lastOrNull()

        if (lastNote == null) {
            i { "No MelodyNote to remove" }
            return
        }

        melody.notes.remove(lastNote)

        // FIXME
        // notifyItemRangeChanged causes graphical issues, probably cause
        // recycling is disabled.
        // also, replacing the whole adapter uses a lot of resources
        // and is slow
//        (binding.recyclerView.adapter)?.
//        notifyItemRangeChanged(0, melody.notes.size)
        binding.recyclerView.adapter = MelodyNoteAdapter(melody.notes, this)

        val notesSize = melody.notes.size

        if (notesSize > 0) {
            // Setting a new adapter sends us back to the top, this is nice
            // to have regardless though
            binding.recyclerView.scrollToPosition(melody.notes.size - 1)
        }
    }

    override fun onMelodyNotePitchTextChanged(
        melodyNote: MelodyNote,
        editable: Editable?,
    ): Boolean {
        // TODO: Handle logic and conversion
        // TODO: Restricts to 2 octaves?
        i { "Pitch text changed for MelodyNote (ID: ${melodyNote.id}). Old: ${melodyNote.pitch}, New: $editable" }
        val parsedValue = editable.toString().toIntOrNull()
        melodyNote.pitch = parsedValue ?: melodyNote.pitch

        return parsedValue != null
    }

    override fun onMelodyNoteVelocityTextChanged(
        melodyNote: MelodyNote,
        editable: Editable?,
    ): Boolean {
        // TODO: Handle logic and conversion
        // TODO: Remove for simplicity? (e.g. always 100)
        i { "Velocity text changed for MelodyNote (ID: ${melodyNote.id}). Old: ${melodyNote.velocity}, New: $editable" }
        val parsedValue = editable.toString().toIntOrNull()
        melodyNote.velocity = parsedValue ?: melodyNote.velocity

        return parsedValue != null
    }

    override fun onMelodyNoteTickTextChanged(
        melodyNote: MelodyNote,
        editable: Editable?,
    ): Boolean {
        // TODO: Handle logic and conversion
        // TODO: Restrict to max length of eight quarters?
        i { "Tick text changed for MelodyNote (ID: ${melodyNote.id}). Old: ${melodyNote.tick}, New: $editable" }
        val parsedValue = editable.toString().toLongOrNull()
        melodyNote.tick = parsedValue ?: melodyNote.tick

        return parsedValue != null
    }

    override fun onMelodyNoteDurationTextChanged(
        melodyNote: MelodyNote,
        editable: Editable?,
    ): Boolean {
        // TODO: Handle logic and conversion
        // TODO: Restrict to max length of eight quarters?
        i { "Duration text changed for MelodyNote (ID: ${melodyNote.id}). Old: ${melodyNote.duration}, New: $editable" }
        val parsedValue = editable.toString().toLongOrNull()
        melodyNote.duration = parsedValue ?: melodyNote.duration

        return parsedValue != null
    }
}
