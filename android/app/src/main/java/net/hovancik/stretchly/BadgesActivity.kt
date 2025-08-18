package net.hovancik.stretchly

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class BadgesActivity : AppCompatActivity() {

    private lateinit var streakTextView: TextView
    private lateinit var badgesRecyclerView: RecyclerView
    private lateinit var resetButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_badges)

        initializeViews()
        loadBadgesAndStreaks()
    }

    private fun initializeViews() {
        streakTextView = findViewById(R.id.streakTextView)
        badgesRecyclerView = findViewById(R.id.badgesRecyclerView)
        resetButton = findViewById(R.id.resetButton)

        badgesRecyclerView.layoutManager = GridLayoutManager(this, 2)
        
        resetButton.setOnClickListener {
            resetAllData()
        }
    }

    private fun loadBadgesAndStreaks() {
        // Load streaks
        val microbreakStreak = StreakManager.getStreak("microbreak")
        val breakStreak = StreakManager.getStreak("break")
        
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val streakText = """
            Current Streaks
            
            Microbreaks: ${microbreakStreak.currentStreak} days (Longest: ${microbreakStreak.longestStreak})
            Long Breaks: ${breakStreak.currentStreak} days (Longest: ${breakStreak.longestStreak})
            
            Last Microbreak: ${if (microbreakStreak.lastBreakDate.time > 0) dateFormat.format(microbreakStreak.lastBreakDate) else "Never"}
            Last Long Break: ${if (breakStreak.lastBreakDate.time > 0) dateFormat.format(breakStreak.lastBreakDate) else "Never"}
        """.trimIndent()
        
        streakTextView.text = streakText

        // Load badges
        val badges = StreakManager.getBadges()
        val adapter = BadgeAdapter(badges)
        badgesRecyclerView.adapter = adapter
    }

    private fun resetAllData() {
        StreakManager.resetAllData()
        loadBadgesAndStreaks()
        android.widget.Toast.makeText(this, "All data reset", android.widget.Toast.LENGTH_SHORT).show()
    }
}

class BadgeAdapter(private val badges: List<Badge>) : 
    RecyclerView.Adapter<BadgeAdapter.BadgeViewHolder>() {

    class BadgeViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
        val iconTextView: TextView = view.findViewById(R.id.badgeIconTextView)
        val nameTextView: TextView = view.findViewById(R.id.badgeNameTextView)
        val descriptionTextView: TextView = view.findViewById(R.id.badgeDescriptionTextView)
        val unlockedDateTextView: TextView = view.findViewById(R.id.badgeUnlockedDateTextView)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): BadgeViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_badge, parent, false)
        return BadgeViewHolder(view)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        val badge = badges[position]
        
        holder.iconTextView.text = badge.icon
        holder.nameTextView.text = badge.name
        holder.descriptionTextView.text = badge.description
        
        if (badge.isUnlocked) {
            holder.unlockedDateTextView.text = "Unlocked!"
            holder.unlockedDateTextView.setTextColor(android.graphics.Color.GREEN)
            holder.itemView.alpha = 1.0f
        } else {
            holder.unlockedDateTextView.text = "Locked"
            holder.unlockedDateTextView.setTextColor(android.graphics.Color.GRAY)
            holder.itemView.alpha = 0.5f
        }
    }

    override fun getItemCount() = badges.size
}
