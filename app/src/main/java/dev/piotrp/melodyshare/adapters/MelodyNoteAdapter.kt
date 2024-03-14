package dev.piotrp.melodyshare.adapters

import android.provider.Settings.Global.getString
import android.text.Editable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import dev.piotrp.melodyshare.R
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
                // TODO: Better error message
                binding.pitchTextField.error = if (valid) null else "0-9"
                binding.pitchTextField.isErrorEnabled = !valid
            }
            binding.velocityTextField.editText?.doAfterTextChanged {
                val valid = listener.onMelodyNoteVelocityTextChanged(melody, it)
                // TODO: Better error message
                binding.velocityTextField.error = if (valid) null else "0-9"
                binding.velocityTextField.isErrorEnabled = !valid
            }
            binding.tickTextField.editText?.doAfterTextChanged {
                val valid = listener.onMelodyNoteTickTextChanged(melody, it)
                // TODO: Better error message
                binding.tickTextField.error = if (valid) null else "0-9"
                binding.tickTextField.isErrorEnabled = !valid
            }
            binding.durationTextField.editText?.doAfterTextChanged {
                val valid = listener.onMelodyNoteDurationTextChanged(melody, it)
                // TODO: Better error message
                binding.durationTextField.error = if (valid) null else "0-9"
                binding.durationTextField.isErrorEnabled = !valid
            }
        }
    }
}
