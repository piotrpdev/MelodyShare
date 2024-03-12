package dev.piotrp.melodyshare.fragments

import android.app.Activity
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ajalt.timberkt.e
import com.github.ajalt.timberkt.i
import dev.piotrp.melodyshare.MyApp
import dev.piotrp.melodyshare.activities.MelodyChangeActivity
import dev.piotrp.melodyshare.adapters.MelodyAdapter
import dev.piotrp.melodyshare.adapters.MelodyListener
import dev.piotrp.melodyshare.databinding.FragmentFeedBinding
import dev.piotrp.melodyshare.models.MelodyModel
import dev.piotrp.melodyshare.models.writeMidiToFile
import java.io.File
import java.io.FileInputStream


class FeedFragment : Fragment(), MelodyListener {
    private lateinit var app: MyApp
    private val mediaPlayer = MediaPlayer()

    private var _binding: FragmentFeedBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        app = activity?.applicationContext as MyApp

        val layoutManager = LinearLayoutManager(app)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = MelodyAdapter(app.melodies.findAll(), this)

        binding.removeMelody.setOnClickListener { onRemoveMelodyClicked(it) }
        binding.addMelody.setOnClickListener { onAddMelodyClicked(it) }

        val risingMelody = app.melodies.findAll().find { it.title == "Rising Melody" }

        if (risingMelody != null) {
            val midiFile = File(app.filesDir.absolutePath + "${risingMelody.id}.mid")

            // TODO: Move to separate thread or coroutine since IO is slow
            // and we don't want to block UI thread
            risingMelody.writeMidiToFile(midiFile)
            playMidi(midiFile)
        } else {
            e { "'Rising Melody' not found" }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun playMidi(file: File) =
        run {
            FileInputStream(file).use {
                mediaPlayer.apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build(),
                    )
                    setDataSource(it.fd)
                    prepareAsync()
                    setOnPreparedListener { start() }
                }
            }
        }

    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                (binding.recyclerView.adapter)
                    ?.notifyItemRangeChanged(0, app.melodies.findAll().size)
            }
        }

    override fun onMelodyClick(melody: MelodyModel) {
        val launcherIntent = Intent(app, MelodyChangeActivity::class.java)
        launcherIntent.putExtra("melody_edit", melody)
        getResult.launch(launcherIntent)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onAddMelodyClicked(view: View) {
        val launcherIntent = Intent(app, MelodyChangeActivity::class.java)
        getResult.launch(launcherIntent)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onRemoveMelodyClicked(view: View) {
        // TODO: Should remove based on ID or something predictable
        val lastMelody = app.melodies.findAll().lastOrNull()

        if (lastMelody == null) {
            i { "No melodies to remove" }
            return
        }

        i { "Removing melody (ID: ${lastMelody.id}, Title: ${lastMelody.title})" }

        app.melodies.remove(lastMelody)
        (binding.recyclerView.adapter)
            ?.notifyItemRemoved(app.melodies.findAll().size + 1)
    }
}