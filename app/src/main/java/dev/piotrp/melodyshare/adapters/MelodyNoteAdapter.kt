package dev.piotrp.melodyshare.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.piotrp.melodyshare.databinding.CardMelodyNoteBinding
import dev.piotrp.melodyshare.models.MelodyModel
import dev.piotrp.melodyshare.models.MelodyNote

class MelodyNoteAdapter(private var melodyNotes: ArrayList<MelodyNote>) : RecyclerView.Adapter<MelodyNoteAdapter.MainHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardMelodyNoteBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val melody = melodyNotes[holder.adapterPosition]
        holder.bind(melody)
    }

    override fun getItemCount(): Int = melodyNotes.size

    class MainHolder(private val binding : CardMelodyNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(melody: MelodyNote) {
            binding.melodyNoteTitle.text = melody.pitch.toString()
        }
    }
}