package app.perkhub

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import app.perkhub.Utils.BaseActivity
import app.perkhub.Utils.LOGIN
import app.perkhub.databinding.ActivitySplashBinding

class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        statusBarColor(R.color.main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        val timer = object: CountDownTimer(2000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {

//                if (getSharedpref().getBoolen(LOGIN)!!){
//                    ContainerActivity.OpenContainer(this@SplashActivity,"EnterDetailFragment",null)
//                    finish()
//                }else{
                    ContainerActivity.OpenContainer(this@SplashActivity,"EnterMobileNumber",null)
                    finish()
//                }
            }
        }
        timer.start()

    }
}