package dev.piotrp.melodyshare.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ajalt.timberkt.d
import com.github.ajalt.timberkt.e
import com.github.ajalt.timberkt.i
import dev.piotrp.melodyshare.MyApp
import dev.piotrp.melodyshare.R
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
    private lateinit var filteredMelodies: List<MelodyModel>

    private var _binding: FragmentFeedBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        app = activity?.applicationContext as MyApp
        filteredMelodies = app.melodies.findAll()

        val layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = MelodyAdapter(filteredMelodies, this)

        binding.removeMelody.setOnClickListener { onRemoveMelodyClicked(it) }
        binding.addMelody.setOnClickListener { onAddMelodyClicked(it) }
        binding.searchTextInput.editText?.doAfterTextChanged { onSearchTextInputChanged(it) }

        val risingMelody = filteredMelodies.find { it.title == "Rising Melody" }

        if (risingMelody != null) {
            val midiFile = File(requireActivity().filesDir.absolutePath + "${risingMelody.id}.mid")

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

    @SuppressLint("NotifyDataSetChanged")
    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                filteredMelodies = app.melodies.findAll()
                // TODO: Replace with something smarter
                binding.recyclerView.adapter = MelodyAdapter(filteredMelodies, this)

                val searchText = binding.searchTextInput.editText?.text
                if (searchText.toString() !== "null") {
                    onSearchTextInputChanged(searchText)
                }
            }
        }

    private fun onSearchTextInputChanged(editable: Editable?) {
        val parsedTitle = editable.toString().trim()

        // Accept empty string to show all melodies
        val titleValid = parsedTitle != "null" && (MyApp.alphaNumSpaceRegex.matchEntire(parsedTitle) != null || parsedTitle.isEmpty())

        if (titleValid) {
            binding.searchTextInput.error = null
            binding.searchTextInput.isErrorEnabled = false

            // TODO: Fetching from firestore/IO on every search character
            // change is extremely inefficient, find a better solution
            filteredMelodies = app.melodies.findAll().filter { it.title.lowercase().contains(parsedTitle.lowercase()) }

            d { "Search result: [${filteredMelodies.joinToString { it.title }}]" }

            binding.recyclerView.adapter = MelodyAdapter(filteredMelodies, this)
        } else {
            binding.searchTextInput.isErrorEnabled = true
            binding.searchTextInput.error = getString(R.string.alpha_num_space_error)
        }
    }

    override fun onMelodyClick(melody: MelodyModel) {
        val launcherIntent = Intent(requireActivity(), MelodyChangeActivity::class.java)
        launcherIntent.putExtra("melody_edit", melody)
        getResult.launch(launcherIntent)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onAddMelodyClicked(view: View) {
        val launcherIntent = Intent(requireActivity(), MelodyChangeActivity::class.java)
        getResult.launch(launcherIntent)
    }

    @SuppressLint("NotifyDataSetChanged")
    @Suppress("UNUSED_PARAMETER")
    private fun onRemoveMelodyClicked(view: View) {
        // TODO: Should remove based on ID or something predictable
        // Use filtered melodies since we want to delete the last
        // melody currently on screen
        val lastMelody = filteredMelodies.lastOrNull()

        if (lastMelody == null) {
            i { "No melodies to remove" }
            return
        }

        i { "Removing melody (ID: ${lastMelody.id}, Title: ${lastMelody.title})" }

        app.melodies.remove(lastMelody)
        filteredMelodies = app.melodies.findAll()
        // TODO: Replace with something smarter
        binding.recyclerView.adapter = MelodyAdapter(filteredMelodies, this)

        val searchText = binding.searchTextInput.editText?.text
        if (searchText.toString() !== "null") {
            onSearchTextInputChanged(searchText)
        }
    }
}
