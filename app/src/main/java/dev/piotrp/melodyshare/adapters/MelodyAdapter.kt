package dev.piotrp.melodyshare.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import dev.piotrp.melodyshare.MyApp
import dev.piotrp.melodyshare.R
import dev.piotrp.melodyshare.databinding.CardMelodyBinding
import dev.piotrp.melodyshare.models.MelodyModel

interface MelodyListener {
    fun onMelodyClick(melody: MelodyModel)
    fun onShareButtonClick(melody: MelodyModel)

    fun onLikeButtonClick(melody: MelodyModel)

    fun onPlayButtonClick(melody: MelodyModel)
}

class MelodyAdapter(private var melodies: List<MelodyModel>, private var app: MyApp, private val listener: MelodyListener) :
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
        holder.bind(melody, app, listener)
    }

    override fun getItemCount(): Int = melodies.size

    class MainHolder(private val binding: CardMelodyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            melody: MelodyModel,
            app: MyApp,
            listener: MelodyListener,
        ) {
            binding.melodyTitle.text = melody.title
            binding.melodyDescription.text = melody.description

            binding.likeButton.icon =
                if (melody.likedBy.contains(app.fid)) {
                    AppCompatResources.getDrawable(binding.root.context, R.drawable.favorite_filled)
                } else {
                    AppCompatResources.getDrawable(binding.root.context, R.drawable.favorite_outlined)
                }

            // TODO: Make this more efficient
            binding.shareButton.isEnabled = app.connectivityManager?.activeNetwork != null && app.auth.currentUser?.uid != null

            // TODO: Can this be done more efficiently?
            binding.melodyView.setMelody(melody)
            binding.root.setOnClickListener { listener.onMelodyClick(melody) }
            binding.shareButton.setOnClickListener { listener.onShareButtonClick(melody) }
            binding.likeButton.setOnClickListener { listener.onLikeButtonClick(melody) }
            binding.playButton.setOnClickListener { listener.onPlayButtonClick(melody) }
        }
    }
}
