package dev.piotrp.melodyshare.activities

import android.os.Bundle
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
import dev.piotrp.melodyshare.adapters.MelodyAdapter
import dev.piotrp.melodyshare.adapters.MelodyNoteAdapter
import dev.piotrp.melodyshare.databinding.ActivityMainBinding
import dev.piotrp.melodyshare.models.MelodyModel

// TODO: Change to better name
class MainActivity : AppCompatActivity() {
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
        binding.recyclerView.adapter = MelodyNoteAdapter(ArrayList())

        app = application as MyApp
        i { "MainActivity started." }

        if (intent.hasExtra("melody_edit")) {
            @Suppress("DEPRECATION")
            melody = intent.extras?.getParcelable("melody_edit")!!
            binding.titleTextField.editText?.setText(melody.title)
            binding.descriptionTextField.editText?.setText(melody.description)
            binding.recyclerView.adapter = MelodyNoteAdapter(melody.notes)

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
}