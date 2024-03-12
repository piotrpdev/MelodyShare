package dev.piotrp.melodyshare.adapters

import android.text.Editable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.github.ajalt.timberkt.i
import dev.piotrp.melodyshare.databinding.CardMelodyNoteBinding
import dev.piotrp.melodyshare.models.MelodyNote
import timber.log.Timber.i

interface MelodyNoteListener {
    fun onMelodyNotePitchTextChanged(melodyNote: MelodyNote, editable: Editable?)
    fun onMelodyNoteVelocityTextChanged(melodyNote: MelodyNote, editable: Editable?)
    fun onMelodyNoteTickTextChanged(melodyNote: MelodyNote, editable: Editable?)
    fun onMelodyNoteDurationTextChanged(melodyNote: MelodyNote, editable: Editable?)

}

class MelodyNoteAdapter(private var melodyNotes: ArrayList<MelodyNote>, private val listener: MelodyNoteListener) : RecyclerView.Adapter<MelodyNoteAdapter.MainHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardMelodyNoteBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        // https://stackoverflow.com/a/47787151/19020549
        holder.setIsRecyclable(false)
        val melody = melodyNotes[holder.adapterPosition]
        holder.bind(melody, listener)
    }

    override fun getItemCount(): Int = melodyNotes.size

    class MainHolder(private val binding : CardMelodyNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(melody: MelodyNote, listener: MelodyNoteListener) {
            binding.pitchTextField.editText?.setText(melody.pitch.toString())
            binding.velocityTextField.editText?.setText(melody.velocity.toString())
            binding.tickTextField.editText?.setText(melody.tick.toString())
            binding.durationTextField.editText?.setText(melody.duration.toString())

            binding.pitchTextField.editText?.doAfterTextChanged { listener.onMelodyNotePitchTextChanged(melody, it) }
            binding.velocityTextField.editText?.doAfterTextChanged { listener.onMelodyNoteVelocityTextChanged(melody, it) }
            binding.tickTextField.editText?.doAfterTextChanged { listener.onMelodyNoteTickTextChanged(melody, it) }
            binding.durationTextField.editText?.doAfterTextChanged { listener.onMelodyNoteDurationTextChanged(melody, it) }
        }
    }
}