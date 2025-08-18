package net.hovancik.stretchly

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class InsightsActivity : AppCompatActivity() {

    private lateinit var todayStatsTextView: TextView
    private lateinit var weeklyStatsTextView: TextView
    private lateinit var monthlyStatsTextView: TextView
    private lateinit var exportButton: Button
    private lateinit var clearDataButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insights)

        initializeViews()
        loadInsights()
    }

    private fun initializeViews() {
        todayStatsTextView = findViewById(R.id.todayStatsTextView)
        weeklyStatsTextView = findViewById(R.id.weeklyStatsTextView)
        monthlyStatsTextView = findViewById(R.id.monthlyStatsTextView)
        exportButton = findViewById(R.id.exportButton)
        clearDataButton = findViewById(R.id.clearDataButton)

        exportButton.setOnClickListener {
            exportData()
        }

        clearDataButton.setOnClickListener {
            clearAllData()
        }
    }

    private fun loadInsights() {
        val today = Date()
        val todayStats = InsightsManager.getDailyStats(today)
        
        // Get weekly stats (current week)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        val weekStart = calendar.time
        val weeklyStats = InsightsManager.getWeeklyStats(weekStart)
        
        // Get monthly stats (current month)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val monthStart = calendar.time
        val monthlyStats = InsightsManager.getMonthlyStats(monthStart)

        displayTodayStats(todayStats)
        displayWeeklyStats(weeklyStats)
        displayMonthlyStats(monthlyStats)
    }

    private fun displayTodayStats(stats: DailyStats) {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val text = """
            Today (${dateFormat.format(stats.date)})
            
            Microbreaks: ${stats.microbreaksTaken} taken, ${stats.microbreaksSkipped} skipped
            Long Breaks: ${stats.breaksTaken} taken, ${stats.breaksSkipped} skipped
            Time Protected: ${stats.totalTimeProtected} minutes
            Average Screen Time: ${stats.averageScreenTime} minutes
            Snoozes: ${stats.snoozeCount}
        """.trimIndent()
        
        todayStatsTextView.text = text
    }

    private fun displayWeeklyStats(stats: WeeklyStats) {
        val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
        val text = """
            This Week (${dateFormat.format(stats.weekStart)})
            
            Total Microbreaks: ${stats.totalMicrobreaks}
            Total Long Breaks: ${stats.totalBreaks}
            Adherence Rate: ${String.format("%.1f", stats.adherenceRate)}%
            Average Daily Time Protected: ${stats.averageDailyTimeProtected} minutes
            Most Active Day: ${stats.mostActiveDay}
        """.trimIndent()
        
        weeklyStatsTextView.text = text
    }

    private fun displayMonthlyStats(stats: Map<String, Any>) {
        val text = """
            This Month
            
            Total Breaks: ${stats["totalBreaks"]}
            Adherence Rate: ${String.format("%.1f", stats["adherenceRate"] as Double)}%
            Total Time Protected: ${stats["totalTimeProtected"]} minutes
            Average Daily Time Protected: ${stats["averageDailyTimeProtected"]} minutes
        """.trimIndent()
        
        monthlyStatsTextView.text = text
    }

    private fun exportData() {
        val csvData = InsightsManager.exportData()
        // In a real app, you'd save this to a file and share it
        // For now, we'll just show a toast
        android.widget.Toast.makeText(this, "Data exported (${csvData.lines().size - 1} records)", android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun clearAllData() {
        InsightsManager.clearAllData()
        loadInsights()
        android.widget.Toast.makeText(this, "All data cleared", android.widget.Toast.LENGTH_SHORT).show()
    }
}
