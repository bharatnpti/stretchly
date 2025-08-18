package net.hovancik.stretchly

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import android.content.Intent

class PreferencesActivity : AppCompatActivity() {

    private lateinit var settingsManager: SettingsManager
    private lateinit var microbreakSwitch: SwitchMaterial
    private lateinit var breakSwitch: SwitchMaterial
    private lateinit var microbreakIntervalInput: TextInputEditText
    private lateinit var microbreakDurationInput: TextInputEditText
    private lateinit var breakIntervalInput: TextInputEditText
    private lateinit var breakDurationInput: TextInputEditText
    private lateinit var hapticSwitch: SwitchMaterial
    private lateinit var audioSwitch: SwitchMaterial
    private lateinit var guidedMicrobreaksSwitch: SwitchMaterial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)

        settingsManager = SettingsManager(this)

        initializeViews()
        loadSettings()
        setupListeners()
    }

    private fun initializeViews() {
        microbreakSwitch = findViewById(R.id.microbreakSwitch)
        breakSwitch = findViewById(R.id.breakSwitch)
        microbreakIntervalInput = findViewById(R.id.microbreakIntervalInput)
        microbreakDurationInput = findViewById(R.id.microbreakDurationInput)
        breakIntervalInput = findViewById(R.id.breakIntervalInput)
        breakDurationInput = findViewById(R.id.breakDurationInput)
        hapticSwitch = findViewById(R.id.hapticSwitch)
        audioSwitch = findViewById(R.id.audioSwitch)
        guidedMicrobreaksSwitch = findViewById(R.id.guidedMicrobreaksSwitch)
    }

    private fun loadSettings() {
        microbreakSwitch.isChecked = settingsManager.getBoolean("microbreakEnabled", DefaultSettings.MICROBREAK_ENABLED)
        breakSwitch.isChecked = settingsManager.getBoolean("breakEnabled", DefaultSettings.BREAK_ENABLED)
        microbreakIntervalInput.setText((settingsManager.getLong("microbreakInterval", DefaultSettings.MICROBREAK_INTERVAL.toLong()) / 60000L).toString())
        microbreakDurationInput.setText((settingsManager.getLong("microbreakDuration", DefaultSettings.MICROBREAK_DURATION.toLong()) / 1000L).toString())
        breakIntervalInput.setText((settingsManager.getLong("breakInterval", DefaultSettings.BREAK_INTERVAL.toLong()) / 60000L).toString())
        breakDurationInput.setText((settingsManager.getLong("breakDuration", DefaultSettings.BREAK_DURATION.toLong()) / 1000L).toString())
        
        hapticSwitch.isChecked = settingsManager.getBoolean("hapticEnabled", true)
        audioSwitch.isChecked = settingsManager.getBoolean("audioEnabled", false)
        guidedMicrobreaksSwitch.isChecked = settingsManager.getBoolean("guidedMicrobreaksEnabled", true)
    }

    private fun setupListeners() {
        microbreakSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.putBoolean("microbreakEnabled", isChecked)
            sendReschedule()
        }

        breakSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.putBoolean("breakEnabled", isChecked)
            sendReschedule()
        }

        hapticSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.putBoolean("hapticEnabled", isChecked)
        }

        audioSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.putBoolean("audioEnabled", isChecked)
        }

        guidedMicrobreaksSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.putBoolean("guidedMicrobreaksEnabled", isChecked)
        }

        microbreakIntervalInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val minutes = s.toString().toLongOrNull()
                if (minutes != null) {
                    settingsManager.putLong("microbreakInterval", minutes * 60000L)
                    sendReschedule()
                }
            }
        })
        microbreakDurationInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val seconds = s.toString().toLongOrNull()
                if (seconds != null) {
                    settingsManager.putLong("microbreakDuration", seconds * 1000L)
                }
            }
        })
        breakIntervalInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val minutes = s.toString().toLongOrNull()
                if (minutes != null) {
                    settingsManager.putLong("breakInterval", minutes * 60000L)
                    sendReschedule()
                }
            }
        })
        breakDurationInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val seconds = s.toString().toLongOrNull()
                if (seconds != null) {
                    settingsManager.putLong("breakDuration", seconds * 1000L)
                }
            }
        })
    }

    private fun sendReschedule() {
        val intent = Intent(this, BreakSchedulerService::class.java).apply {
            action = BreakSchedulerService.ACTION_RESCHEDULE
        }
        startService(intent)
    }
}
