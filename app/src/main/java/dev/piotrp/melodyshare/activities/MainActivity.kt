package dev.piotrp.melodyshare.activities

import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.github.ajalt.timberkt.d
import com.github.ajalt.timberkt.i
import com.google.android.material.snackbar.Snackbar
import dev.piotrp.melodyshare.MyApp
import dev.piotrp.melodyshare.R
import dev.piotrp.melodyshare.databinding.ActivityMainBinding
import dev.piotrp.melodyshare.models.PlacemarkModel

// TODO: Change to better name
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var buttonPressedCount: Int = 0
    private var placemark = PlacemarkModel()
    private lateinit var app: MyApp

    private fun updateSwitchBasedOnTheme() {
        val themeSwitch = binding.themeSwitch
        themeSwitch.isSaveEnabled = false

        when ((resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                i { "Dark mode detected, changing themeSwitch isChecked to true" }
                themeSwitch.isChecked = true
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                i { "Light mode detected, changing themeSwitch isChecked to false" }
                themeSwitch.isChecked = false
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {}
        }
    }

    // This is a test comment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setSupportActionBar(binding.topAppBar)
        binding.topAppBar.title = getString(R.string.button_message)
        setContentView(binding.root)

        app = application as MyApp
        i { "MainActivity started." }

        if (intent.hasExtra("placemark_edit")) {
            @Suppress("DEPRECATION")
            placemark = intent.extras?.getParcelable("placemark_edit")!!
            binding.titleTextField.editText?.setText(placemark.title)
            binding.descriptionTextField.editText?.setText(placemark.description)
            binding.button.text = getString(R.string.save_placemark)
        }

        updateSwitchBasedOnTheme()
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
    fun onAddPlacemarkClicked(view: View) {
        buttonPressedCount++

        placemark.title = binding.titleTextField.editText?.text.toString()
        placemark.description = binding.descriptionTextField.editText?.text.toString()

        var messageId = -1

        if (intent.hasExtra("placemark_edit")) {
            messageId = if (placemark.title.isBlank() || placemark.title == "null") R.string.button_clicked_message_saved_titleless else R.string.button_clicked_message_saved

            app.placemarks.update(placemark.copy())
        } else {
            messageId = if (placemark.title.isBlank() || placemark.title == "null") R.string.button_clicked_message_titleless else R.string.button_clicked_message

            app.placemarks.create(placemark.copy())
        }

        val message = getString(messageId, placemark.title)

        Snackbar
            .make(binding.root, message, Snackbar.LENGTH_LONG)
            .show()

        i { "Placemark added/saved with title \"${placemark.title}\" and description \"${placemark.description}\" " }
        d { "Full placemark ArrayList: ${app.placemarks}" }

        setResult(RESULT_OK)
        finish()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onThemeSwitchToggle(view: View) {
        // TODO: Move switch to main activity
        var currentlyDark = false

        when ((resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                currentlyDark = true
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                currentlyDark = false
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {}
        }

        i { "UI_MODE_NIGHT is currently set to $currentlyDark, attempting to invert..." }
        AppCompatDelegate.setDefaultNightMode(if (currentlyDark) AppCompatDelegate.MODE_NIGHT_NO else AppCompatDelegate.MODE_NIGHT_YES)
    }
}