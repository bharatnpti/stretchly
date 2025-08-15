package net.hovancik.stretchly

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.view.WindowManager

class MicrobreakActivity : AppCompatActivity() {

    private lateinit var countDownTimer: CountDownTimer
    private lateinit var timerTextView: TextView
    private lateinit var messageTextView: TextView
    private lateinit var ideasLoader: IdeasLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_microbreak)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)

        timerTextView = findViewById(R.id.timerTextView)
        messageTextView = findViewById(R.id.messageTextView)
        ideasLoader = IdeasLoader(this)

        messageTextView.text = ideasLoader.getRandomMicrobreakIdea()

        val duration = intent.getLongExtra("duration", DefaultSettings.MICROBREAK_DURATION.toLong())

        countDownTimer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerTextView.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                finish()
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
    }
}
