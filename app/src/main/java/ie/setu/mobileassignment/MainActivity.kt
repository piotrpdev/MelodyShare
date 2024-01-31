package ie.setu.mobileassignment

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.github.ajalt.timberkt.i
import com.google.android.material.materialswitch.MaterialSwitch

class MainActivity : AppCompatActivity() {
    private var buttonPressedCount: Int = 0

    private fun updateSwitchBasedOnTheme() {
        val themeSwitch = findViewById<MaterialSwitch>(R.id.themeSwitch)
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
        setContentView(R.layout.activity_main)

        i { "MainActivity started." }

        updateSwitchBasedOnTheme()
    }

    fun clickMe(view: View) {
        buttonPressedCount++

        Toast.makeText(this,
            R.string.message,
            Toast.LENGTH_LONG).show()

        val message = getString(R.string.button_pressed_time_s, buttonPressedCount)

        findViewById<TextView>(R.id.btnTextView).text = message
        i { message }
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