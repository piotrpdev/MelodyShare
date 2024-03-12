package dev.piotrp.melodyshare.activities

import android.os.Bundle
import android.text.Editable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ajalt.timberkt.d
import com.github.ajalt.timberkt.i
import com.google.android.material.snackbar.Snackbar
import dev.piotrp.melodyshare.MyApp
import dev.piotrp.melodyshare.R
import dev.piotrp.melodyshare.adapters.MelodyNoteAdapter
import dev.piotrp.melodyshare.adapters.MelodyNoteListener
import dev.piotrp.melodyshare.databinding.ActivityMainBinding
import dev.piotrp.melodyshare.models.MelodyModel
import dev.piotrp.melodyshare.models.MelodyNote

// TODO: Change to better name
class MainActivity : AppCompatActivity(), MelodyNoteListener {
    private lateinit var binding: ActivityMainBinding
    private var buttonPressedCount: Int = 0
    private var melody = MelodyModel()
    private lateinit var app: MyApp

    // This is a test comment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
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
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_real_main, menu)
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

        melody.title = binding.titleTextField.editText?.text.toString()
        melody.description = binding.descriptionTextField.editText?.text.toString()

        val messageId: Int

        if (intent.hasExtra("melody_edit")) {
            messageId = if (melody.title.isBlank() || melody.title == "null") R.string.button_clicked_message_saved_titleless else R.string.button_clicked_message_saved

            app.melodies.update(melody.copy())
        } else {
            messageId = if (melody.title.isBlank() || melody.title == "null") R.string.button_clicked_message_titleless else R.string.button_clicked_message

            app.melodies.create(melody.copy())
        }

        val message = getString(messageId, melody.title)

        Snackbar
            .make(binding.root, message, Snackbar.LENGTH_LONG)
            .show()

        i { "Melody added/saved with title \"${melody.title}\" and description \"${melody.description}\" " }
        d { "Full melody ArrayList: ${app.melodies}" }

        setResult(RESULT_OK)
        finish()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onAddNoteClicked(view: View) {
        i { "Adding MelodyNote to Melody" }
        val id = melody.notes.maxOfOrNull { it.id }?.plus(1) ?: 0
        val tick = melody.notes.maxOfOrNull { it.tick }?.plus(480) ?: 0

        melody.notes.add(MelodyNote(id, 60, 100, tick, 120))

        // FIXME
        // notifyItemRangeChanged causes graphical issues, probably cause
        // recycling is disabled.
//        (binding.recyclerView.adapter)?.
//        notifyItemRangeChanged(0, melody.notes.size)
        binding.recyclerView.adapter = MelodyNoteAdapter(melody.notes, this)

        // Setting a new adapter sends us back to the top, this is nice
        // to have regardless though
        binding.recyclerView.scrollToPosition(melody.notes.size - 1);
    }

    override fun onMelodyNotePitchTextChanged(melodyNote: MelodyNote, editable: Editable?) {
        i { "Pitch text changed for MelodyNote (ID: ${melodyNote.id}). Old: ${melodyNote.pitch}, New: ${editable.toString()}" }
        melodyNote.pitch = editable.toString().toIntOrNull() ?: melodyNote.pitch
    }

    override fun onMelodyNoteVelocityTextChanged(melodyNote: MelodyNote, editable: Editable?) {
        i { "Velocity text changed for MelodyNote (ID: ${melodyNote.id}). Old: ${melodyNote.velocity}, New: ${editable.toString()}" }
        melodyNote.velocity = editable.toString().toIntOrNull() ?: melodyNote.velocity
    }

    override fun onMelodyNoteTickTextChanged(melodyNote: MelodyNote, editable: Editable?) {
        i { "Tick text changed for MelodyNote (ID: ${melodyNote.id}). Old: ${melodyNote.tick}, New: ${editable.toString()}" }
        melodyNote.tick = editable.toString().toLongOrNull() ?: melodyNote.tick
    }

    override fun onMelodyNoteDurationTextChanged(melodyNote: MelodyNote, editable: Editable?) {
        i { "Duration text changed for MelodyNote (ID: ${melodyNote.id}). Old: ${melodyNote.duration}, New: ${editable.toString()}" }
        melodyNote.duration = editable.toString().toLongOrNull() ?: melodyNote.duration
    }
}