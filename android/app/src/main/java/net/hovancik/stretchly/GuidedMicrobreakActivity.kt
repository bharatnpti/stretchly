package net.hovancik.stretchly

import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.view.WindowManager

class GuidedMicrobreakActivity : AppCompatActivity() {

    private lateinit var timerTextView: TextView
    private lateinit var titleTextView: TextView
    private lateinit var instructionTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var breathingCircle: ImageView
    private lateinit var skipButton: Button
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var breathingAnimator: ValueAnimator
    private lateinit var vibrator: Vibrator
    
    private var currentRoutine: GuidedRoutine? = null
    private var currentStepIndex = 0
    private var isBreathingRoutine = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guided_microbreak)
        
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or 
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or 
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        initializeViews()
        setupVibrator()
        
        val routineType = intent.getStringExtra("routine_type") ?: "breathing"
        currentRoutine = GuidedRoutineFactory.getRoutine(routineType)
        isBreathingRoutine = routineType == "breathing"
        
        startRoutine()
    }

    private fun initializeViews() {
        timerTextView = findViewById(R.id.timerTextView)
        titleTextView = findViewById(R.id.titleTextView)
        instructionTextView = findViewById(R.id.instructionTextView)
        progressBar = findViewById(R.id.progressBar)
        breathingCircle = findViewById(R.id.breathingCircle)
        skipButton = findViewById(R.id.skipButton)
        
        skipButton.setOnClickListener {
            finish()
        }
    }

    private fun setupVibrator() {
        vibrator = ContextCompat.getSystemService(this, Vibrator::class.java)!!
    }

    private fun startRoutine() {
        currentRoutine?.let { routine ->
            titleTextView.text = routine.title
            progressBar.max = routine.steps.size
            progressBar.progress = 0
            
            if (isBreathingRoutine) {
                breathingCircle.visibility = View.VISIBLE
                startBreathingAnimation()
            } else {
                breathingCircle.visibility = View.GONE
            }
            
            startStep(0)
        }
    }

    private fun startStep(stepIndex: Int) {
        currentStepIndex = stepIndex
        currentRoutine?.let { routine ->
            if (stepIndex >= routine.steps.size) {
                finish()
                return
            }
            
            val step = routine.steps[stepIndex]
            instructionTextView.text = step.instruction
            progressBar.progress = stepIndex + 1
            
            // Haptic feedback for step change
            if (settingsManager.getBoolean("hapticEnabled", true)) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            }
            
            countDownTimer = object : CountDownTimer(step.duration * 1000L, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timerTextView.text = (millisUntilFinished / 1000).toString()
                }
                
                override fun onFinish() {
                    // Fade transition effect
                    instructionTextView.animate()
                        .alpha(0f)
                        .setDuration(300)
                        .withEndAction {
                            startStep(stepIndex + 1)
                            instructionTextView.animate()
                                .alpha(1f)
                                .setDuration(300)
                                .start()
                        }
                        .start()
                }
            }.start()
        }
    }

    private fun startBreathingAnimation() {
        breathingAnimator = ValueAnimator.ofFloat(0.5f, 1.5f).apply {
            duration = 4000 // 4 seconds for inhale
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            
            addUpdateListener { animator ->
                val scale = animator.animatedValue as Float
                breathingCircle.scaleX = scale
                breathingCircle.scaleY = scale
            }
        }
        breathingAnimator.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
        breathingAnimator.cancel()
    }

    companion object {
        private lateinit var settingsManager: SettingsManager
        
        init {
            settingsManager = SettingsManager(StretchlyApplication.instance)
        }
    }
}
