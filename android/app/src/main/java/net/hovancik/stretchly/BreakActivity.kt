package net.hovancik.stretchly

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class BreakActivity : AppCompatActivity() {

    private lateinit var countDownTimer: CountDownTimer
    private lateinit var timerTextView: TextView
    private lateinit var titleTextView: TextView
    private lateinit var messageTextView: TextView
    private lateinit var ideasLoader: IdeasLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_break)

        timerTextView = findViewById(R.id.timerTextView)
        titleTextView = findViewById(R.id.titleTextView)
        messageTextView = findViewById(R.id.messageTextView)
        ideasLoader = IdeasLoader(this)

        val idea = ideasLoader.getRandomBreakIdea()
        titleTextView.text = idea.first
        messageTextView.text = idea.second

        val duration = intent.getLongExtra("duration", DefaultSettings.BREAK_DURATION.toLong())

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
