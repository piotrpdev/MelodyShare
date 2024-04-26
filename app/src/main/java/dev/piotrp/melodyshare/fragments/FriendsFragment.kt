package dev.piotrp.melodyshare.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ajalt.timberkt.i
import com.github.ajalt.timberkt.w
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.toObject
import dev.piotrp.melodyshare.MyApp
import dev.piotrp.melodyshare.adapters.FriendsAdapter
import dev.piotrp.melodyshare.adapters.FriendsListener
import dev.piotrp.melodyshare.databinding.FragmentFriendsBinding
import dev.piotrp.melodyshare.models.FirestoreUser

class FriendsFragment : Fragment(), FriendsListener {
    private lateinit var app: MyApp
    private var _binding: FragmentFriendsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    private var friendsSnapshotListener: ListenerRegistration? = null

    private var pendingChangesCount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerView.layoutManager = layoutManager

        app = activity?.applicationContext as MyApp

        authStateListener =
            FirebaseAuth.AuthStateListener {
                displayUserFriends()
            }

        // authStateListener is apparently called once when added here
        app.auth.addAuthStateListener(authStateListener)
    }

    private fun displayUserFriends() {
        val currentUser = app.auth.currentUser

        if (currentUser == null) {
            w { "currentUser is null, cancelling displayUserFriends()" }
            binding.notSignedInText.visibility = VISIBLE
            binding.recyclerView.adapter = null
            friendsSnapshotListener?.remove()
            return
        }

        binding.notSignedInText.visibility = INVISIBLE

        if (friendsSnapshotListener != null) return

        i { "Fetching friends" }
        friendsSnapshotListener =
            app.db.collection("users")
                .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, e ->
                    if (e != null) {
                        w { "Error getting documents." }
                        return@addSnapshotListener
                    }

                    i { "Success getting snapshot." }

                    if (snapshot != null && snapshot.metadata.hasPendingWrites() && app.connectivityManager?.activeNetwork != null) {
                        // TODO: Maybe handle this for nicer UX
                        // https://firebase.google.com/docs/firestore/query-data/listen#events-local-changes
                        i { "Skipping snapshot because it has pending local changes" }
                        return@addSnapshotListener
                    }

                    // TODO: Make more efficient, currently adapter is replaced twice,
                    // Once on change pending and once on metadata confirm
//                    d { "isFromCache: ${snapshot!!.metadata.isFromCache}, pendingWrites: ${snapshot.metadata.hasPendingWrites()}" }

                    val users: MutableList<FirestoreUser> = arrayListOf()
                    var currentFirestoreUser: FirestoreUser? = null

                    for (document in snapshot!!) {
                        val user = document.toObject<FirestoreUser>()
                        if (user.uid == currentUser.uid) {
                            currentFirestoreUser = user
                            continue
                        }
                        users.add(user)
                    }

                    // TODO: Do this better, add loading spinner, handle offline case
                    binding.recyclerView.adapter = FriendsAdapter(users, currentFirestoreUser!!, this)
                }
    }

    override fun onActionButtonClick(
        user: FirestoreUser,
        currentFirestoreUser: FirestoreUser,
    ) {
        if (currentFirestoreUser.friends.contains(user.uid)) {
            if (user.friends.contains(currentFirestoreUser.uid)) {
                pendingChangesCount++
                app.db.collection("users")
                    .document(currentFirestoreUser.uid)
                    .update("friends", FieldValue.arrayRemove(user.uid))
            }
        } else {
            pendingChangesCount++
            app.db.collection("users")
                .document(currentFirestoreUser.uid)
                .update("friends", FieldValue.arrayUnion(user.uid))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        app.auth.removeAuthStateListener(authStateListener)
        friendsSnapshotListener?.remove()
        _binding = null
    }
}
