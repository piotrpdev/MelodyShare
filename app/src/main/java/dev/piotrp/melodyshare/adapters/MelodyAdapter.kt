package dev.piotrp.melodyshare.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.piotrp.melodyshare.databinding.CardMelodyBinding
import dev.piotrp.melodyshare.models.MelodyModel

interface MelodyListener {
    fun onMelodyClick(melody: MelodyModel)
}

class MelodyAdapter(private var melodies: List<MelodyModel>, private val listener: MelodyListener) :
    RecyclerView.Adapter<MelodyAdapter.MainHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MainHolder {
        val binding =
            CardMelodyBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MainHolder,
        position: Int,
    ) {
        val melody = melodies[holder.adapterPosition]
        holder.bind(melody, listener)
    }

    override fun getItemCount(): Int = melodies.size

    class MainHolder(private val binding: CardMelodyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            melody: MelodyModel,
            listener: MelodyListener,
        ) {
            binding.melodyTitle.text = melody.title
            binding.melodyDescription.text = melody.description
            binding.root.setOnClickListener { listener.onMelodyClick(melody) }
        }
    }
}
