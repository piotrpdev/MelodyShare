package dev.piotrp.melodyshare.activities

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
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
import dev.piotrp.melodyshare.R
import dev.piotrp.melodyshare.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = title

        setSupportActionBar(binding.toolbar)

        auth = Firebase.auth

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_actual_main) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.feed -> {
                    // Respond to navigation item 1 click
                    i { "Feed clicked" }
                    navController.navigate(R.id.FeedFragment)
                    true
                }
                R.id.likes -> {
                    // Respond to navigation item 2 click
                    // TODO: Implement
                    i { "Likes clicked" }
                    navController.navigate(R.id.SecondFragment)
                    true
                }
                R.id.friends -> {
                    // Respond to navigation item 3 click
                    i { "Friends clicked" }
                    // TODO: Implement
                    true
                }
                R.id.settings -> {
                    // Respond to navigation item 4 click
                    i { "Settings clicked" }
                    // TODO: Implement
                    true
                }
                else -> false
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
            // TODO: Handle offline caching/usage
            Glide.with(this)
                .asDrawable()
                .circleCrop()
                .load(currentUser.photoUrl)
                .into(
                    object : CustomTarget<Drawable?>() {
                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable?>?,
                        ) {
                            i { "Setting account icon to user's photo" }
                            menuItem.setIcon(resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    },
                )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

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

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_actual_main)
        return navController.navigateUp(appBarConfiguration) ||
            super.onSupportNavigateUp()
    }
}
