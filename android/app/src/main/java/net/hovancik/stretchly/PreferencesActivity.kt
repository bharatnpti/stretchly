package net.hovancik.stretchly

import android.os.Bundle
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial

class PreferencesActivity : AppCompatActivity() {

    private lateinit var settingsManager: SettingsManager
    private lateinit var microbreakSwitch: SwitchMaterial
    private lateinit var breakSwitch: SwitchMaterial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)

        settingsManager = SettingsManager(this)

        microbreakSwitch = findViewById(R.id.microbreakSwitch)
        breakSwitch = findViewById(R.id.breakSwitch)

        microbreakSwitch.isChecked = settingsManager.getBoolean("microbreakEnabled", DefaultSettings.MICROBREAK_ENABLED)
        breakSwitch.isChecked = settingsManager.getBoolean("breakEnabled", DefaultSettings.BREAK_ENABLED)

        microbreakSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.putBoolean("microbreakEnabled", isChecked)
        }

        breakSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.putBoolean("breakEnabled", isChecked)
        }
    }
}
