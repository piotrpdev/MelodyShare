package dev.piotrp.melodyshare.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dev.piotrp.melodyshare.MyApp
import dev.piotrp.melodyshare.R

class PlacemarkListActivity : AppCompatActivity() {
    private lateinit var app: MyApp
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_placemark_list)
        app = application as MyApp
    }
}