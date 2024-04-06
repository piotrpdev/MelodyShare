package dev.piotrp.melodyshare.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.github.ajalt.timberkt.i
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import dev.piotrp.melodyshare.MyApp
import dev.piotrp.melodyshare.R
import dev.piotrp.melodyshare.databinding.FragmentSettingsBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SettingsFragment : Fragment() {
    private lateinit var app: MyApp
    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var authStateListener: AuthStateListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        app = activity?.applicationContext as MyApp

        binding.themeSwitch.setOnClickListener { onThemeSwitchToggle(it) }

        updateSwitchBasedOnTheme()

        // TODO: Handle offline
        authStateListener =
            AuthStateListener {
                displayUserDetails()
            }

        // authStateListener is apparently called once when added here
        app.auth.addAuthStateListener(authStateListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        app.auth.removeAuthStateListener(authStateListener)
        _binding = null
    }

    private fun displayUserDetails() {
        i { "Displaying user details in settings" }
        val currentUser = app.auth.currentUser

        if (currentUser == null) {
            binding.avatarImageView.setImageResource(R.mipmap.ic_launcher_round)
            binding.userNameText.text = getString(R.string.not_signed_in)
            binding.userEmailText.text = getString(R.string.click_top_right)
            return
        }

        // TODO: Handle offline caching/usage
        Glide.with(this)
            .load(currentUser.photoUrl)
            .into(binding.avatarImageView)

        binding.userNameText.text = currentUser.displayName
        binding.userEmailText.text = currentUser.email
    }

    private fun updateSwitchBasedOnTheme() {
        val themeSwitch = binding.themeSwitch
        themeSwitch.isSaveEnabled = false

        when ((resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                i { "Dark mode detected, changing themeSwitch isChecked to true" }
                themeSwitch.isChecked = true
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                i { "Light mode detected, changing themeSwitch isChecked to false" }
                themeSwitch.isChecked = false
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {}
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onThemeSwitchToggle(view: View) {
        // TODO: Add persistence so once user sets a theme using
        // the switch, it stays that way even upon restart.
        var currentlyDark = false

        when ((resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                currentlyDark = true
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                currentlyDark = false
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {}
        }

        i { "UI_MODE_NIGHT is currently set to $currentlyDark, attempting to invert..." }
        AppCompatDelegate.setDefaultNightMode(if (currentlyDark) AppCompatDelegate.MODE_NIGHT_NO else AppCompatDelegate.MODE_NIGHT_YES)
    }
}
