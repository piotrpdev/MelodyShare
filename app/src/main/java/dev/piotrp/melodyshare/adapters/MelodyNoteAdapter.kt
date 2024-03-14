package dev.piotrp.melodyshare.adapters

import android.text.Editable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import dev.piotrp.melodyshare.databinding.CardMelodyNoteBinding
import dev.piotrp.melodyshare.models.MelodyNote

interface MelodyNoteListener {
    fun onMelodyNotePitchTextChanged(
        melodyNote: MelodyNote,
        editable: Editable?,
    ): Boolean

    fun onMelodyNoteVelocityTextChanged(
        melodyNote: MelodyNote,
        editable: Editable?,
    ): Boolean

    fun onMelodyNoteTickTextChanged(
        melodyNote: MelodyNote,
        editable: Editable?,
    ): Boolean

    fun onMelodyNoteDurationTextChanged(
        melodyNote: MelodyNote,
        editable: Editable?,
    ): Boolean
}

class MelodyNoteAdapter(private var melodyNotes: ArrayList<MelodyNote>, private val listener: MelodyNoteListener) : RecyclerView.Adapter<MelodyNoteAdapter.MainHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MainHolder {
        val binding =
            CardMelodyNoteBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MainHolder,
        position: Int,
    ) {
        // FIXME: Listeners fire with correct ID but wrong Editable values
        // https://stackoverflow.com/a/47787151/19020549
        holder.setIsRecyclable(false)
        val melody = melodyNotes[holder.adapterPosition]
        holder.bind(melody, listener)
    }

    override fun getItemCount(): Int = melodyNotes.size

    class MainHolder(private val binding: CardMelodyNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            melody: MelodyNote,
            listener: MelodyNoteListener,
        ) {
            binding.pitchTextField.editText?.setText(melody.pitch.toString())
            binding.velocityTextField.editText?.setText(melody.velocity.toString())
            binding.tickTextField.editText?.setText(melody.tick.toString())
            binding.durationTextField.editText?.setText(melody.duration.toString())

            binding.pitchTextField.editText?.doAfterTextChanged {
                val valid = listener.onMelodyNotePitchTextChanged(melody, it)
                binding.pitchTextField.error = if (valid) null else "Numbers only"
            }
            binding.velocityTextField.editText?.doAfterTextChanged {
                val valid = listener.onMelodyNoteVelocityTextChanged(melody, it)
                binding.velocityTextField.error = if (valid) null else "Numbers only"
            }
            binding.tickTextField.editText?.doAfterTextChanged {
                val valid = listener.onMelodyNoteTickTextChanged(melody, it)
                binding.tickTextField.error = if (valid) null else "Numbers only"
            }
            binding.durationTextField.editText?.doAfterTextChanged {
                val valid = listener.onMelodyNoteDurationTextChanged(melody, it)
                binding.durationTextField.error = if (valid) null else "Numbers only"
            }
        }
    }
}
