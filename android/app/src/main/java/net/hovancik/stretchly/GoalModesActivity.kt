package net.hovancik.stretchly

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent

class GoalModesActivity : AppCompatActivity() {

    private lateinit var currentModeTextView: TextView
    private lateinit var modesRecyclerView: RecyclerView
    private lateinit var createCustomButton: Button
    
    companion object {
        private const val EDIT_MODE_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal_modes)

        initializeViews()
        loadGoalModes()
    }

    private fun initializeViews() {
        currentModeTextView = findViewById(R.id.currentModeTextView)
        modesRecyclerView = findViewById(R.id.modesRecyclerView)
        createCustomButton = findViewById(R.id.createCustomButton)

        modesRecyclerView.layoutManager = LinearLayoutManager(this)
        
        createCustomButton.setOnClickListener {
            startEditModeActivity(null)
        }
    }

    private fun loadGoalModes() {
        val currentMode = GoalModeManager.getCurrentMode()
        val dailyTargets = GoalModeManager.getDailyTargets()
        
        val currentModeText = """
            Current Mode: ${currentMode.name}
            
            Microbreak: ${currentMode.microbreakInterval} min interval, ${currentMode.microbreakDuration} sec duration
            Long Break: ${currentMode.breakInterval} min interval, ${currentMode.breakDuration} min duration
            
            Daily Targets:
            - Microbreaks: ${dailyTargets["microbreaks"]}
            - Long Breaks: ${dailyTargets["breaks"]}
            
            Work Hours: ${currentMode.workHours.startHour}:00 - ${currentMode.workHours.endHour}:00
            Weekdays Only: ${if (currentMode.workHours.weekdaysOnly) "Yes" else "No"}
        """.trimIndent()
        
        currentModeTextView.text = currentModeText

        // Load all available modes
        val allModes = GoalModeManager.getAllModes()
        val adapter = GoalModeAdapter(allModes, currentMode.id, 
            onModeSelected = { modeId ->
                GoalModeManager.setCurrentMode(modeId)
                loadGoalModes()
            },
            onModeEdit = { modeId ->
                startEditModeActivity(modeId)
            }
        )
        modesRecyclerView.adapter = adapter
    }

    private fun startEditModeActivity(modeId: String?) {
        val intent = Intent(this, EditGoalModeActivity::class.java).apply {
            modeId?.let { putExtra("mode_id", it) }
        }
        startActivityForResult(intent, EDIT_MODE_REQUEST_CODE)
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_MODE_REQUEST_CODE && resultCode == RESULT_OK) {
            loadGoalModes()
        }
    }
}

class GoalModeAdapter(
    private val modes: List<GoalMode>,
    private val currentModeId: String,
    private val onModeSelected: (String) -> Unit,
    private val onModeEdit: (String) -> Unit
) : RecyclerView.Adapter<GoalModeAdapter.GoalModeViewHolder>() {

    class GoalModeViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.modeNameTextView)
        val descriptionTextView: TextView = view.findViewById(R.id.modeDescriptionTextView)
        val selectButton: Button = view.findViewById(R.id.selectModeButton)
        val editButton: Button = view.findViewById(R.id.editModeButton)
        val deleteButton: Button = view.findViewById(R.id.deleteModeButton)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): GoalModeViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_goal_mode, parent, false)
        return GoalModeViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalModeViewHolder, position: Int) {
        val mode = modes[position]
        
        holder.nameTextView.text = mode.name
        holder.descriptionTextView.text = mode.description
        
        if (mode.id == currentModeId) {
            holder.selectButton.text = "Current"
            holder.selectButton.isEnabled = false
        } else {
            holder.selectButton.text = "Select"
            holder.selectButton.isEnabled = true
            holder.selectButton.setOnClickListener {
                onModeSelected(mode.id)
            }
        }
        
        // Show edit button for all modes
        holder.editButton.visibility = android.view.View.VISIBLE
        holder.editButton.setOnClickListener {
            onModeEdit(mode.id)
        }
        
        if (mode.isCustom) {
            holder.deleteButton.visibility = android.view.View.VISIBLE
            holder.deleteButton.setOnClickListener {
                GoalModeManager.deleteCustomMode(mode.id)
                // Refresh the adapter
                notifyDataSetChanged()
            }
        } else {
            holder.deleteButton.visibility = android.view.View.GONE
        }
    }

    override fun getItemCount() = modes.size
}
