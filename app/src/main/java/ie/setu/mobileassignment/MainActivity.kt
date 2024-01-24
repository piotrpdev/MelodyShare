package ie.setu.mobileassignment

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import ie.setu.mobileassignment.utils.log

class MainActivity : AppCompatActivity() {
    var buttonPressedCount: Int = 0

    // This is a test comment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        when ((resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                findViewById<TextView>(R.id.themeBtn).text = getString(R.string.lightModeBtnText)
                log.info { "Dark mode detected, changing theme button text" }
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                findViewById<TextView>(R.id.themeBtn).text = getString(R.string.darkModeBtnText)
                log.info { "Light mode detected, changing theme button text" }
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {}
        }
    }

    fun clickMe(view: View) {
        buttonPressedCount++

        Toast.makeText(this,
            R.string.message,
            Toast.LENGTH_LONG).show()

        val message = getString(R.string.button_pressed_time_s, buttonPressedCount)

        findViewById<TextView>(R.id.btnTextView).text = message
        log.info{message}
    }

    fun themeBtnClick(view: View) {
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

        log.info { "UI_MODE_NIGHT is set to $currentlyDark, attempting to invert..." }
        AppCompatDelegate.setDefaultNightMode(if (currentlyDark) AppCompatDelegate.MODE_NIGHT_NO else AppCompatDelegate.MODE_NIGHT_YES)
    }
}