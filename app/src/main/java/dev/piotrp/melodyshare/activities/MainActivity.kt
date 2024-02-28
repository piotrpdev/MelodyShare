package dev.piotrp.melodyshare.activities

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.github.ajalt.timberkt.d
import com.github.ajalt.timberkt.i
import com.google.android.material.snackbar.Snackbar
import dev.piotrp.melodyshare.R
import dev.piotrp.melodyshare.databinding.ActivityMainBinding
import dev.piotrp.melodyshare.models.PlacemarkModel

// TODO: Change to better name
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var buttonPressedCount: Int = 0
    // TODO: Maybe move to Application for persistence
    private val placemarks = ArrayList<PlacemarkModel>()

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
        setContentView(binding.root)

        i { "MainActivity started." }

        updateSwitchBasedOnTheme()
    }

    fun onAddPlacemarkClicked(view: View) {
        buttonPressedCount++

        val title = binding.titleTextField.editText?.text.toString()
        val messageId = if (title.isBlank() || title == "null") R.string.button_clicked_message_titleless else R.string.button_clicked_message
        val message = getString(messageId, binding.titleTextField.editText?.text.toString())

        val description = binding.descriptionTextField.editText?.text.toString()

        placemarks.add(PlacemarkModel(title, description))

        Snackbar
            .make(binding.root, message, Snackbar.LENGTH_LONG)
            .show()

        i { "Placemark added with title \"$title\" and description \"$description\" " }
        d { "Full placemark ArrayList: $placemarks" }
    }

    fun onThemeSwitchToggle(view: View) {
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