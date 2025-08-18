package net.hovancik.stretchly

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent

class EditGoalModeActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var microbreakIntervalEditText: EditText
    private lateinit var microbreakDurationEditText: EditText
    private lateinit var breakIntervalEditText: EditText
    private lateinit var breakDurationEditText: EditText
    private lateinit var longBreakAfterEditText: EditText
    private lateinit var workStartHourEditText: EditText
    private lateinit var workEndHourEditText: EditText
    private lateinit var weekdaysOnlyCheckBox: CheckBox
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var deleteButton: Button

    private var originalMode: GoalMode? = null
    private var isNewMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_goal_mode)

        initializeViews()
        loadModeData()
        setupListeners()
    }

    private fun initializeViews() {
        nameEditText = findViewById(R.id.nameEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        microbreakIntervalEditText = findViewById(R.id.microbreakIntervalEditText)
        microbreakDurationEditText = findViewById(R.id.microbreakDurationEditText)
        breakIntervalEditText = findViewById(R.id.breakIntervalEditText)
        breakDurationEditText = findViewById(R.id.breakDurationEditText)
        longBreakAfterEditText = findViewById(R.id.longBreakAfterEditText)
        workStartHourEditText = findViewById(R.id.workStartHourEditText)
        workEndHourEditText = findViewById(R.id.workEndHourEditText)
        weekdaysOnlyCheckBox = findViewById(R.id.weekdaysOnlyCheckBox)
        saveButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)
        deleteButton = findViewById(R.id.deleteButton)
    }

    private fun loadModeData() {
        val modeId = intent.getStringExtra("mode_id")
        isNewMode = modeId == null

        if (isNewMode) {
            // Creating a new mode
            title = "Create Custom Mode"
            deleteButton.visibility = android.view.View.GONE
            // Set default values
            microbreakIntervalEditText.setText("10")
            microbreakDurationEditText.setText("20")
            breakIntervalEditText.setText("30")
            breakDurationEditText.setText("5")
            longBreakAfterEditText.setText("0")
            workStartHourEditText.setText("9")
            workEndHourEditText.setText("17")
            weekdaysOnlyCheckBox.isChecked = true
        } else {
            // Editing existing mode
            originalMode = GoalModeManager.getAllModes().find { it.id == modeId }
            originalMode?.let { mode ->
                title = if (mode.isCustom) "Edit Custom Mode" else "Edit Mode"
                
                nameEditText.setText(mode.name)
                descriptionEditText.setText(mode.description)
                microbreakIntervalEditText.setText(mode.microbreakInterval.toString())
                microbreakDurationEditText.setText(mode.microbreakDuration.toString())
                breakIntervalEditText.setText(mode.breakInterval.toString())
                breakDurationEditText.setText(mode.breakDuration.toString())
                longBreakAfterEditText.setText(mode.longBreakAfterMicrobreaks.toString())
                workStartHourEditText.setText(mode.workHours.startHour.toString())
                workEndHourEditText.setText(mode.workHours.endHour.toString())
                weekdaysOnlyCheckBox.isChecked = mode.workHours.weekdaysOnly

                // Show delete button only for custom modes
                deleteButton.visibility = if (mode.isCustom) android.view.View.VISIBLE else android.view.View.GONE
                
                // Disable editing for preset modes (name and description)
                if (!mode.isCustom) {
                    nameEditText.isEnabled = false
                    descriptionEditText.isEnabled = false
                }
            }
        }
    }

    private fun setupListeners() {
        saveButton.setOnClickListener {
            if (validateInputs()) {
                saveMode()
            }
        }

        cancelButton.setOnClickListener {
            finish()
        }

        deleteButton.setOnClickListener {
            showDeleteConfirmation()
        }
    }

    private fun validateInputs(): Boolean {
        val name = nameEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()
        
        if (name.isEmpty()) {
            nameEditText.error = "Name is required"
            return false
        }
        
        if (description.isEmpty()) {
            descriptionEditText.error = "Description is required"
            return false
        }

        // Validate numeric inputs
        val inputs = listOf(
            microbreakIntervalEditText to "Microbreak interval",
            microbreakDurationEditText to "Microbreak duration",
            breakIntervalEditText to "Break interval",
            breakDurationEditText to "Break duration",
            longBreakAfterEditText to "Long break after",
            workStartHourEditText to "Work start hour",
            workEndHourEditText to "Work end hour"
        )

        for ((editText, fieldName) in inputs) {
            val value = editText.text.toString().toIntOrNull()
            if (value == null || value < 0) {
                editText.error = "$fieldName must be a positive number"
                return false
            }
        }

        val startHour = workStartHourEditText.text.toString().toInt()
        val endHour = workEndHourEditText.text.toString().toInt()
        
        if (startHour >= endHour) {
            workEndHourEditText.error = "End hour must be after start hour"
            return false
        }

        if (startHour < 0 || startHour > 23 || endHour < 0 || endHour > 23) {
            workStartHourEditText.error = "Hours must be between 0 and 23"
            return false
        }

        return true
    }

    private fun saveMode() {
        val name = nameEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()
        val microbreakInterval = microbreakIntervalEditText.text.toString().toInt()
        val microbreakDuration = microbreakDurationEditText.text.toString().toInt()
        val breakInterval = breakIntervalEditText.text.toString().toInt()
        val breakDuration = breakDurationEditText.text.toString().toInt()
        val longBreakAfter = longBreakAfterEditText.text.toString().toInt()
        val workStartHour = workStartHourEditText.text.toString().toInt()
        val workEndHour = workEndHourEditText.text.toString().toInt()
        val weekdaysOnly = weekdaysOnlyCheckBox.isChecked

        val workHours = WorkHours(
            startHour = workStartHour,
            endHour = workEndHour,
            weekdaysOnly = weekdaysOnly
        )

        if (isNewMode) {
            // Create new custom mode
            val newMode = GoalModeManager.createCustomMode(
                name = name,
                description = description,
                microbreakInterval = microbreakInterval,
                microbreakDuration = microbreakDuration,
                breakInterval = breakInterval,
                breakDuration = breakDuration,
                longBreakAfterMicrobreaks = longBreakAfter,
                workHours = workHours
            )
            
            // Set as current mode
            GoalModeManager.setCurrentMode(newMode.id)
            
            Toast.makeText(this, "Custom mode created and applied", Toast.LENGTH_SHORT).show()
        } else {
            // Update existing mode
            originalMode?.let { mode ->
                if (mode.isCustom) {
                    // Update custom mode
                    GoalModeManager.updateCustomMode(
                        modeId = mode.id,
                        name = name,
                        description = description,
                        microbreakInterval = microbreakInterval,
                        microbreakDuration = microbreakDuration,
                        breakInterval = breakInterval,
                        breakDuration = breakDuration,
                        longBreakAfterMicrobreaks = longBreakAfter,
                        workHours = workHours
                    )
                    
                    Toast.makeText(this, "Custom mode updated", Toast.LENGTH_SHORT).show()
                } else {
                    // Create a custom copy of preset mode
                    val customMode = GoalModeManager.createCustomMode(
                        name = name,
                        description = description,
                        microbreakInterval = microbreakInterval,
                        microbreakDuration = microbreakDuration,
                        breakInterval = breakInterval,
                        breakDuration = breakDuration,
                        longBreakAfterMicrobreaks = longBreakAfter,
                        workHours = workHours
                    )
                    
                    // Set as current mode
                    GoalModeManager.setCurrentMode(customMode.id)
                    
                    Toast.makeText(this, "Custom mode created from preset", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Return to goal modes activity
        setResult(RESULT_OK)
        finish()
    }

    private fun showDeleteConfirmation() {
        android.app.AlertDialog.Builder(this)
            .setTitle("Delete Mode")
            .setMessage("Are you sure you want to delete this custom mode?")
            .setPositiveButton("Delete") { _, _ ->
                originalMode?.let { mode ->
                    GoalModeManager.deleteCustomMode(mode.id)
                    Toast.makeText(this, "Mode deleted", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
