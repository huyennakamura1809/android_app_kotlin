package net.braniumacademy.musicapplication.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import net.braniumacademy.musicapplication.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}