package ie.setu.mobileassignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import ie.setu.mobileassignment.utils.log

class MainActivity : AppCompatActivity() {
    var buttonPressedCount: Int = 0

    // This is a test comment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
}