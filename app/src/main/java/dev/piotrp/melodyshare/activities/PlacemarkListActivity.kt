package dev.piotrp.melodyshare.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import dev.piotrp.melodyshare.MyApp
import dev.piotrp.melodyshare.R
import dev.piotrp.melodyshare.adapters.PlacemarkAdapter
import dev.piotrp.melodyshare.adapters.PlacemarkListener
import dev.piotrp.melodyshare.databinding.ActivityPlacemarkListBinding
import dev.piotrp.melodyshare.models.PlacemarkModel

class PlacemarkListActivity : AppCompatActivity(), PlacemarkListener {
    private lateinit var app: MyApp
    private lateinit var binding: ActivityPlacemarkListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlacemarkListBinding.inflate(layoutInflater)
        binding.topAppBar.title = title
        setSupportActionBar(binding.topAppBar)
        setContentView(binding.root)
        app = application as MyApp

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = PlacemarkAdapter(app.placemarks.findAll(), this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_list, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> {
                val launcherIntent = Intent(this, MainActivity::class.java)
                getResult.launch(launcherIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                (binding.recyclerView.adapter)?.
                notifyItemRangeChanged(0, app.placemarks.findAll().size)
            }
        }

    override fun onPlacemarkClick(placemark: PlacemarkModel) {
        val launcherIntent = Intent(this, MainActivity::class.java)
        launcherIntent.putExtra("placemark_edit", placemark)
        getResult.launch(launcherIntent)
    }
}