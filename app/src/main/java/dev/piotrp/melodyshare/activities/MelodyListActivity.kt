package dev.piotrp.melodyshare.activities

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.github.ajalt.timberkt.e
import com.github.ajalt.timberkt.i
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.leff.midi.MidiFile
import com.leff.midi.MidiTrack
import com.leff.midi.event.meta.Tempo
import com.leff.midi.event.meta.TimeSignature
import dev.piotrp.melodyshare.MyApp
import dev.piotrp.melodyshare.R
import dev.piotrp.melodyshare.adapters.MelodyAdapter
import dev.piotrp.melodyshare.adapters.MelodyListener
import dev.piotrp.melodyshare.databinding.ActivityMelodyListBinding
import dev.piotrp.melodyshare.models.MelodyModel
import java.io.File
import java.io.FileInputStream
import java.io.IOException


class MelodyListActivity : AppCompatActivity(), MelodyListener {
    private lateinit var app: MyApp
    private lateinit var binding: ActivityMelodyListBinding
    private lateinit var auth: FirebaseAuth
    private val mediaPlayer = MediaPlayer()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMelodyListBinding.inflate(layoutInflater)
        binding.topAppBar.title = title
        setSupportActionBar(binding.topAppBar)
        setContentView(binding.root)
        app = application as MyApp

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = MelodyAdapter(app.melodies.findAll(), this)

        auth = Firebase.auth

        val midiFile = File(this.filesDir.absolutePath + "examplemidi.mid")

        createMidiFile(midiFile)
        playMidi(midiFile)
    }

    // MIT License, Copyright (c) 2017 Alex Leffelman
    // https://github.com/LeffelMania/android-midi-lib/blob/7cdd855c2b70d2074a53732e8a3979fe8e65e12a/README.md?plain=1#L67-L115
    private fun createMidiFile(file: File) {
        i { "Creating new MIDI tracks" }
        val tracks: MutableList<MidiTrack> = ArrayList()

        val tempoTrack = MidiTrack()
        val ts = TimeSignature(0, 0, 4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION)
        tempoTrack.insertEvent(ts)

        // Tempo is 228
        val tempo = Tempo(0, 0, (60000000 / 228f).toInt())
        tempoTrack.insertEvent(tempo)

        tracks.add(tempoTrack)

        val noteCount = 30
        val noteTrack = MidiTrack()

        for (i in 0 until noteCount) {
            // Usually each instrument gets own channel
            val channel = 0
            // 21 is the lowest note that will play
            // 60 is middle C (C4)
            // 88 is the highest piano key
            val pitch = 60 + i
            val velocity = 100
            // 480 = quarter note
            val tick = (i * 480).toLong()
            val duration = 120.toLong()
            noteTrack.insertNote(channel, pitch, velocity, tick, duration)
        }

        tracks.add(noteTrack)

        val midi = MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks)

        try {
            i { "Attempting to write MIDI to file at ${file.absolutePath}" }
            midi.writeToFile(file)
        } catch (e: IOException) {
            System.err.println(e)
        }
    }

    private fun playMidi(file: File) = run {
        FileInputStream(file).use {
            mediaPlayer.apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(it.fd)
                prepareAsync()
                setOnPreparedListener { start() }
            }
        }
    }

    private fun setUserIconToAvatar(menuItem: MenuItem) {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // https://stackoverflow.com/a/33042690/19020549
            // https://stackoverflow.com/a/39249024/19020549
            // https://stackoverflow.com/a/57347465/19020549
            // Is it possible to use bindings here?
            Glide.with(this)
                .asDrawable()
                .circleCrop()
                .load(currentUser.photoUrl)
                .into(object : CustomTarget<Drawable?>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable?>?
                    ) {
                        i { "Setting account icon to user's photo" }
                        menuItem.setIcon(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_list, menu)

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            i { "User already signed in: $currentUser" }

            setUserIconToAvatar(menu.findItem(R.id.sign_in))
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sign_in -> {
                val currentUser = auth.currentUser
                if (currentUser == null) {
                    val provider = OAuthProvider.newBuilder("github.com")

                    val pendingResultTask = auth.pendingAuthResult
                    if (pendingResultTask != null) {
                        // There's something already here! Finish the sign-in for your user.
                        pendingResultTask
                            .addOnSuccessListener {
                                i { "User sign-in success: ${it.user}" }
                                setUserIconToAvatar(item)
                            }
                            .addOnFailureListener {
                                i { "User sign-in failure" }
                                e { it.toString() }
                                Snackbar
                                    .make(binding.root, R.string.sign_in_fail, Snackbar.LENGTH_LONG)
                                    .show()
                            }
                    } else {
                        auth
                            .startActivityForSignInWithProvider(this, provider.build())
                            .addOnSuccessListener {
                                i { "User sign-in success: ${it.user}" }
                                setUserIconToAvatar(item)
                            }
                            .addOnFailureListener {
                                i { "User sign-in failure" }
                                e { it.toString() }
                                Snackbar
                                    .make(binding.root, R.string.sign_in_fail, Snackbar.LENGTH_LONG)
                                    .show()
                            }
                    }
                } else {
                    i { "Signing out user" }
                    auth.signOut()
                    item.setIcon(R.drawable.account_circle)
                }
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
                notifyItemRangeChanged(0, app.melodies.findAll().size)
            }
        }

    override fun onMelodyClick(melody: MelodyModel) {
        val launcherIntent = Intent(this, MainActivity::class.java)
        launcherIntent.putExtra("melody_edit", melody)
        getResult.launch(launcherIntent)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onAddMelodyClicked(view: View) {
        val launcherIntent = Intent(this, MainActivity::class.java)
        getResult.launch(launcherIntent)
    }
}