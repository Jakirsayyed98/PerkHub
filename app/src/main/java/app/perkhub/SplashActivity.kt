package app.perkhub

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import app.perkhub.Utils.BaseActivity
import app.perkhub.Utils.ENTER_MOBILE_NUMBER
import app.perkhub.Utils.HOME_SCREEN_FRAGMENT
import app.perkhub.Utils.LOGIN
import app.perkhub.Utils.openCustomTab
import app.perkhub.databinding.ActivitySplashBinding
import com.google.firebase.messaging.FirebaseMessaging


class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
//        enableEdgeToEdge()
        setContentView(binding.root)
//        statusBarColor(R.color.main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        window.statusBarColor = ContextCompat.getColor(this, android.R.color.white)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        FirebaseMessaging.getInstance().subscribeToTopic("all_users")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM", "Subscribed to all_users topic")
                }
            }

        val timer = object: CountDownTimer(2000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                handleNavigation()
//                if (getSharedpref().getBoolen(LOGIN) == true){
//                    ContainerActivity.OpenContainer(this@SplashActivity, HOME_SCREEN_FRAGMENT,null)
//                    finish()
//                }else{
//                    ContainerActivity.OpenContainer(this@SplashActivity,ENTER_MOBILE_NUMBER,null)
//                    finish()
//                }
            }
        }
        timer.start()

    }
    private fun handleNavigation() {
        val gameId = intent.getStringExtra("game_id").orEmpty()
        val url = intent.getStringExtra("url").orEmpty()

        Log.d("FCM", "Notification game_id: $gameId")
        Log.d("FCM", "Notification URL: $url")

        if (gameId.isNotBlank() && url.isNotBlank()) {
            if (url.startsWith("https://") || url.startsWith("http://")) {
                this.openCustomTab(url)
                finish()
                return
            }
        }

        // Existing navigation when no valid game notification is received
        if (getSharedpref().getBoolen(LOGIN) == true) {
            ContainerActivity.OpenContainer(
                this@SplashActivity,
                HOME_SCREEN_FRAGMENT,
                null
            )
        } else {
            ContainerActivity.OpenContainer(
                this@SplashActivity,
                ENTER_MOBILE_NUMBER,
                null
            )
        }

        finish()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}