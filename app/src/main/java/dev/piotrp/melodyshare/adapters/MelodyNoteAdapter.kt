package dev.piotrp.melodyshare.adapters

import android.text.Editable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.github.ajalt.timberkt.d
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

class MelodyNoteAdapter(
    private var melodyNotes: ArrayList<MelodyNote>,
    private val listener: MelodyNoteListener,
) : RecyclerView.Adapter<MelodyNoteAdapter.MainHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MainHolder {
        val binding =
            CardMelodyNoteBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)

        d { "Creating viewholder" }
        return MainHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MainHolder,
        position: Int,
    ) {
        // FIXME: Listeners fire with correct ID but wrong Editable values
        //  https://stackoverflow.com/a/47787151/19020549
        //  https://stackoverflow.com/a/64396998/19020549
//        holder.setIsRecyclable(false)
        val melodyNote = melodyNotes[holder.adapterPosition]
//        d { "Binding idx: ${holder.adapterPosition} melody: $melodyNote" }
        holder.bind(melodyNote, listener)
    }

    override fun getItemCount(): Int = melodyNotes.size

    class MainHolder(private val binding: CardMelodyNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var oldMelodyNote: MelodyNote? = null
        private var bindedMelodyNote: MelodyNote? = null
        private var isPitchBindChange = false
        private var isPitchTextFieldListenerSet = false
        private var isVelocityBindChange = false
        private var isVelocityTextFieldListenerSet = false
        private var isTickBindChange = false
        private var isTickTextFieldListenerSet = false
        private var isDurationBindChange = false
        private var isDurationTextFieldListenerSet = false

        /**
         *  This function will be called whenever a [MainHolder] instance goes
         *  into view, so a lot of code is needed to avoid duplication.
         *
         *  This code uses [isPitchTextFieldListenerSet] to set a
         *  single `doAfterTextChanged()` listener for `pitchTextField`
         *  in each `MainHolder` (there will probably be around 8 instances).
         *
         *  `doAfterTextChanged()` will also be fired when this code changes
         *  the `pitchTextField.text` (using `editText?.setText()`) for an
         *  existing `MainHolder` instance (e.g. listener already created,
         *  and now the first `MainHolder` is being reused/recycled since
         *  you scrolled down enough.
         *
         * @see: <a href="https://medium.com/qasir/recyclerviews-event-listener-could-be-improved-b95220788610">This Medium article</a>
         * @see: <a href="https://stackoverflow.com/a/64396998/19020549">This StackOverflow answer</a>
         *
         * @param melodyNote
         * @param listener
         */
        fun bind(
            melodyNote: MelodyNote,
            listener: MelodyNoteListener,
        ) {
            // `doAfterTextChanged` may use melodyNote from when it was created instead of current
            bindedMelodyNote = melodyNote

            if (oldMelodyNote == null) {
                oldMelodyNote = melodyNote
            }

            if (bindedMelodyNote!!.id != oldMelodyNote!!.id) {
//                d { "New: ${bindedMelodyNote!!.id.toString().slice(0..6)}, old: ${oldMelodyNote!!.id.toString().slice(0..6)}" }
                oldMelodyNote = bindedMelodyNote
            }

            if (!isPitchTextFieldListenerSet) {
                isPitchTextFieldListenerSet = true
                binding.pitchTextField.editText?.doAfterTextChanged {
//                    d { "Changing: { pos: $adapterPosition, newId?: ${newMelodyNote?.id ?: "none"}" }
                    if (isPitchBindChange) {
//                        d { "pitch bind change" }
                        isPitchBindChange = false
                    } else {
                        d { "not a pitch bind change" }
                        val valid = listener.onMelodyNotePitchTextChanged(bindedMelodyNote!!, it)
                        // TODO: Better error message
                        binding.pitchTextField.error = if (valid) null else "0-9"
                        binding.pitchTextField.isErrorEnabled = !valid
                    }
                }
            }
            if (!isVelocityTextFieldListenerSet) {
                isVelocityTextFieldListenerSet = true
                binding.velocityTextField.editText?.doAfterTextChanged {
//                    d { "Changing: { pos: $adapterPosition, newId?: ${newMelodyNote?.id ?: "none"}" }
                    if (isVelocityBindChange) {
//                        d { "velocity bind change" }
                        isVelocityBindChange = false
                    } else {
                        d { "not a velocity bind change" }
                        val valid = listener.onMelodyNoteVelocityTextChanged(bindedMelodyNote!!, it)
                        // TODO: Better error message
                        binding.velocityTextField.error = if (valid) null else "0-9"
                        binding.velocityTextField.isErrorEnabled = !valid
                    }
                }
            }

            if (!isTickTextFieldListenerSet) {
                isTickTextFieldListenerSet = true
                binding.tickTextField.editText?.doAfterTextChanged {
//                    d { "Changing: { pos: $adapterPosition, newId?: ${newMelodyNote?.id ?: "none"}" }
                    if (isTickBindChange) {
//                        d { "tick bind change" }
                        isTickBindChange = false
                    } else {
                        d { "not a tick bind change" }
                        val valid = listener.onMelodyNoteTickTextChanged(bindedMelodyNote!!, it)
                        // TODO: Better error message
                        binding.tickTextField.error = if (valid) null else "0-9"
                        binding.tickTextField.isErrorEnabled = !valid
                    }
                }
            }

            if (!isDurationTextFieldListenerSet) {
                isDurationTextFieldListenerSet = true
                binding.durationTextField.editText?.doAfterTextChanged {
//                    d { "Changing: { pos: $adapterPosition, newId?: ${newMelodyNote?.id ?: "none"}" }
                    if (isDurationBindChange) {
//                        d { "duration bind change" }
                        isDurationBindChange = false
                    } else {
                        d { "not a duration bind change" }
                        val valid = listener.onMelodyNoteDurationTextChanged(bindedMelodyNote!!, it)
                        // TODO: Better error message
                        binding.durationTextField.error = if (valid) null else "0-9"
                        binding.durationTextField.isErrorEnabled = !valid
                    }
                }
            }

            isPitchBindChange = true
            binding.pitchTextField.editText?.setText(melodyNote.pitch.toString())
            isVelocityBindChange = true
            binding.velocityTextField.editText?.setText(melodyNote.velocity.toString())
            isTickBindChange = true
            binding.tickTextField.editText?.setText(melodyNote.tick.toString())
            isDurationBindChange = true
            binding.durationTextField.editText?.setText(melodyNote.duration.toString())
        }
    }
}
