package app.perkhub

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import app.perkhub.Utils.BaseActivity
import app.perkhub.Utils.LOGIN
import app.perkhub.Utils.toast
import app.perkhub.databinding.ActivitySplashBinding


class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
//        statusBarColor(R.color.main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        this.toast(getSharedpref().getBoolen(LOGIN).toString())

        val timer = object: CountDownTimer(2000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {

                if (getSharedpref().getBoolen(LOGIN) == true){
                    ContainerActivity.OpenContainer(this@SplashActivity,"HomeScreenFragment",null)
                    finish()
                }else{
                    ContainerActivity.OpenContainer(this@SplashActivity,"EnterMobileNumber",null)
                    finish()
                }
            }
        }
        timer.start()

    }
}