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
import com.github.ajalt.timberkt.i
import com.github.ajalt.timberkt.w
import com.google.android.material.snackbar.Snackbar
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
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var filteredMelodies: MutableList<MelodyModel>

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
        filteredMelodies = app.melodies.findAll().toMutableList()

        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerView.adapter = MelodyAdapter(filteredMelodies, this)

        binding.removeMelody.setOnClickListener { onRemoveMelodyClicked(it) }
        binding.addMelody.setOnClickListener { onAddMelodyClicked(it) }
        binding.searchTextInput.editText?.doAfterTextChanged { onSearchTextInputChanged(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun playMidi(file: File) =
        run {
            FileInputStream(file).use {
                mediaPlayer?.apply {
                    i { "Stopping and releasing mediaPlayer" }
                    stop()
                    release()
                }
                i { "Creating new mediaPlayer instance" }
                mediaPlayer = MediaPlayer()
                mediaPlayer?.apply {
                    d { "Setting mediaPlayer audio attributes" }
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build(),
                    )
                    d { "Setting mediaPlayer data source" }
                    setDataSource(it.fd)
                    d { "Setting setOnPreparedListener" }
                    setOnPreparedListener { start() }
                    d { "Preparing mediaPlayer" }
                    prepareAsync()
                }
            }
        }

    @SuppressLint("NotifyDataSetChanged")
    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                // TODO: Maybe do a get in case another user changed something
//                filteredMelodies.addAll(app.melodies.findAll().toMutableList())
//                binding.recyclerView.adapter!!.notifyDataSetChanged()

                if (activityResult.data == null) {
                    w { "Something went wrong, result data from MelodyChangeActivity is null" }
                    return@registerForActivityResult
                }

                val messageId: Int

                @Suppress("DEPRECATION")
                val melody = activityResult.data!!.extras?.getParcelable<MelodyModel>("melody")

                if (melody == null) {
                    w { "Something went wrong, melody extra from MelodyChangeActivity is null" }
                    return@registerForActivityResult
                }

                val searchText = binding.searchTextInput.editText!!.text
                if (searchText.toString() !== "null") {
                    d { "Trying to add/remove melody with filter. Removing filter..." }
                    binding.searchTextInput.editText!!.text!!.clear()
                }

                if (activityResult.data!!.hasExtra("melody_edit")) {
                    messageId = R.string.button_clicked_message_saved

                    val localMelodyIdx = filteredMelodies.indexOfFirst { it.id == melody.id }

                    if (localMelodyIdx == -1) {
                        w { "Something went wrong, can't find modified melody locally" }
                        return@registerForActivityResult
                    }

                    app.melodies.update(melody)
                    filteredMelodies[localMelodyIdx] = melody
                    binding.recyclerView.adapter!!.notifyItemChanged(localMelodyIdx)
                } else {
                    messageId = R.string.button_clicked_message

                    app.melodies.create(melody)
                    filteredMelodies.add(melody)
                    // TODO: Need to notify item was added here but not in other cases
                    //  for some reason. Should check this doesn't cause errors.
                    binding.recyclerView.adapter!!.notifyItemChanged(binding.recyclerView.adapter!!.itemCount)
                }

                val message = getString(messageId, melody.title)

                i { "Melody added/saved with title \"${melody.title}\" and description \"${melody.description}\" " }
                d { "Full melody ArrayList: ${app.melodies.findAll()}" }

                Snackbar
                    .make(binding.root, message, Snackbar.LENGTH_LONG)
                    .show()
            }
        }

    @SuppressLint("NotifyDataSetChanged")
    private fun onSearchTextInputChanged(editable: Editable?) {
        val parsedTitle = editable.toString().trim()

        // Accept empty string to show all melodies
        val titleValid = parsedTitle != "null" && (MyApp.alphaNumSpaceRegex.matchEntire(parsedTitle) != null || parsedTitle.isEmpty())

        if (titleValid) {
            binding.searchTextInput.error = null
            binding.searchTextInput.isErrorEnabled = false

            // TODO: Fetching from firestore/IO on every search character
            // change is extremely inefficient, find a better solution
            filteredMelodies.clear()
            filteredMelodies.addAll(app.melodies.findAll().filter { it.title.lowercase().contains(parsedTitle.lowercase()) })

            d { "Search result: [${filteredMelodies.joinToString { it.title }}]" }

            binding.recyclerView.adapter!!.notifyDataSetChanged()
        } else {
            binding.searchTextInput.isErrorEnabled = true
            binding.searchTextInput.error = getString(R.string.alpha_num_space_error)
        }
    }

    override fun onMelodyClick(melody: MelodyModel) {
        val launcherIntent = Intent(requireActivity(), MelodyChangeActivity::class.java)
        // TODO: Check if .copy() is needed
        launcherIntent.putExtra("melody_edit", melody.copy())
        getResult.launch(launcherIntent)
    }

    override fun onPlayButtonClick(melody: MelodyModel) {
        // TODO: Handle pausing
        // TODO: Check if it's worth checking if MIDI file already exists and has same contents
        val filePath = requireActivity().filesDir.absolutePath + "/" + "${melody.id}.mid"
        val midiFile = File(filePath)

        // TODO: Move to separate thread or coroutine since IO is slow
        // and we don't want to block UI thread
        // TODO: Maybe write on melody save instead of on every play
        i { "Writing ${melody.title} (ID: ${melody.id}) to MIDI file at '$filePath'" }
        melody.writeMidiToFile(midiFile)
        i { "Playing MIDI file at '$filePath'" }
        playMidi(midiFile)
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
        // TODO: Maybe do a get in case another user changed something
        filteredMelodies.removeIf { it.id == lastMelody.id }
//        binding.recyclerView.adapter!!.notifyDataSetChanged()

        val searchText = binding.searchTextInput.editText?.text
        if (searchText.toString() !== "null") {
            onSearchTextInputChanged(searchText)
        }
    }
}
