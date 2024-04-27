package dev.piotrp.melodyshare.activities

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.github.ajalt.timberkt.e
import com.github.ajalt.timberkt.i
import com.github.ajalt.timberkt.w
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.OAuthProvider
import dev.piotrp.melodyshare.MyApp
import dev.piotrp.melodyshare.R
import dev.piotrp.melodyshare.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var app: MyApp
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = this.applicationContext as MyApp

        binding.toolbar.title = title

        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_actual_main) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.feed -> {
                    i { "Feed clicked" }
                    navController.navigate(R.id.FeedFragment)
                    true
                }
                R.id.friends -> {
                    i { "Friends clicked" }
                    navController.navigate(R.id.FriendsFragment)
                    true
                }
                R.id.settings -> {
                    i { "Settings clicked" }
                    navController.navigate(R.id.SettingsFragment)
                    true
                }
                else -> false
            }
        }

        askNotificationPermission()
    }

    private val requestNotificationsPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            i { "Notification permissions granted" }
        } else {
            w { "Notification permissions denied" }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationsPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun setUserIconToAvatar(menuItem: MenuItem) {
        val currentUser = app.auth.currentUser

        if (currentUser != null) {
            // https://stackoverflow.com/a/33042690/19020549
            // https://stackoverflow.com/a/39249024/19020549
            // https://stackoverflow.com/a/57347465/19020549
            // Is it possible to use bindings here?
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
        val currentUser = app.auth.currentUser
        if (currentUser != null) {
            i { "User already signed in: $currentUser" }

            setUserIconToAvatar(menu.findItem(R.id.sign_in))
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sign_in -> {
                val currentUser = app.auth.currentUser
                if (currentUser == null) {
                    val provider = OAuthProvider.newBuilder("github.com")

                    val pendingResultTask = app.auth.pendingAuthResult
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
                        app.auth
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
                    app.auth.signOut()
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
