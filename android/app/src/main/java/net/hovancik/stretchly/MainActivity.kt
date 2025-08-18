package net.hovancik.stretchly

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val NOTIFICATION_PERMISSION_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_PERMISSION_CODE)
            } else {
                startService(Intent(this, BreakSchedulerService::class.java))
            }
        } else {
            startService(Intent(this, BreakSchedulerService::class.java))
        }

        setupNavigationButtons()
    }

    private fun setupNavigationButtons() {
        findViewById<Button>(R.id.preferencesButton).setOnClickListener {
            startActivity(Intent(this, PreferencesActivity::class.java))
        }
        
        findViewById<Button>(R.id.insightsButton).setOnClickListener {
            startActivity(Intent(this, InsightsActivity::class.java))
        }
        
        findViewById<Button>(R.id.badgesButton).setOnClickListener {
            startActivity(Intent(this, BadgesActivity::class.java))
        }
        
        findViewById<Button>(R.id.goalModesButton).setOnClickListener {
            startActivity(Intent(this, GoalModesActivity::class.java))
        }
        
        findViewById<Button>(R.id.contentPacksButton).setOnClickListener {
            startActivity(Intent(this, ContentPacksActivity::class.java))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startService(Intent(this, BreakSchedulerService::class.java))
            }
        }
    }
}
