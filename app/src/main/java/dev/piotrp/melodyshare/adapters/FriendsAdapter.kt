package dev.piotrp.melodyshare.adapters

import android.view.LayoutInflater
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.ajalt.timberkt.d
import dev.piotrp.melodyshare.R
import dev.piotrp.melodyshare.databinding.CardFriendBinding
import dev.piotrp.melodyshare.models.FirestoreUser

interface FriendsListener {
    fun onActionButtonClick(
        user: FirestoreUser,
        currentFirestoreUser: FirestoreUser,
    )
    fun onShareButtonClick(
        user: FirestoreUser,
        currentFirestoreUser: FirestoreUser,
        melodyShared: String
    )
}

class FriendsAdapter(
    private var friends: List<FirestoreUser>,
    private val currentFirestoreUser: FirestoreUser,
    private val sharedMelodyContainer: MutableMap<String, String>,
    private val listener: FriendsListener,
) : RecyclerView.Adapter<FriendsAdapter.MainHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MainHolder {
        val binding =
            CardFriendBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MainHolder,
        position: Int,
    ) {
        val friend = friends[holder.adapterPosition]
        holder.bind(friend, currentFirestoreUser, sharedMelodyContainer, listener)
    }

    override fun getItemCount(): Int = friends.size

    class MainHolder(private val binding: CardFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            user: FirestoreUser,
            currentFirestoreUser: FirestoreUser,
            sharedMelodyContainer: MutableMap<String, String>,
            listener: FriendsListener,
        ) {
            binding.friendName.text = user.displayName

            d { "Fetching friend image for '${user.displayName}'" }

            // TODO: Handle offline caching/usage
            // TODO: Prefetch these? How efficient is this?
            Glide.with(binding.root.context)
                .load(user.photoURL)
                .into(binding.avatarImageView)

            binding.actionButton.apply {
//                d { "${currentFirestoreUser.uid} => ${currentFirestoreUser.friends}" }
//                d { "${user.uid} => ${user.friends}" }

                if (currentFirestoreUser.friends.contains(user.uid)) {
                    // You have added user
                    if (user.friends.contains(currentFirestoreUser.uid)) {
                        // and user has added you
                        icon = AppCompatResources.getDrawable(binding.root.context, R.drawable.person_remove)
                        isEnabled = true
                    } else {
                        icon = AppCompatResources.getDrawable(binding.root.context, R.drawable.pending)
                        isEnabled = false
                    }
                } else {
                    icon = AppCompatResources.getDrawable(binding.root.context, R.drawable.person_add)
                    isEnabled = true
                }

                setOnClickListener { listener.onActionButtonClick(user, currentFirestoreUser) }
            }

            binding.shareButton.apply {
                val melodyShared = sharedMelodyContainer["melody_shared"]
                d { "melodyContainer: $melodyShared" }

                // TODO: Handle offline
                if (
                    currentFirestoreUser.friends.contains(user.uid) &&
                    user.friends.contains(currentFirestoreUser.uid) &&
                    !melodyShared.isNullOrBlank()
                    )
                {
                    visibility = VISIBLE
                    setOnClickListener { listener.onShareButtonClick(user, currentFirestoreUser, melodyShared) }
                }
            }
        }
    }
}
