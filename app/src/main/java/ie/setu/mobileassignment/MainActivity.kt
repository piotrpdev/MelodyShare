package ie.setu.mobileassignment

import android.annotation.SuppressLint
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

    @SuppressLint("SetTextI18n")
    fun clickMe(view: View) {
        buttonPressedCount++

        Toast.makeText(this,
            R.string.message,
            Toast.LENGTH_LONG).show()

        findViewById<TextView>(R.id.btnTextView).text = "Button pressed $buttonPressedCount time(s)"
        log.info{"Button pressed $buttonPressedCount time(s)"}
    }
}