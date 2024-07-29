package app.perkhub

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import app.perkhub.Utils.BaseActivity
import app.perkhub.Utils.CONTAINER_TYPE_KEY
import app.perkhub.Utils.DATA
import app.perkhub.databinding.ActivityContainerBinding
import app.perkhub.login.EnterMobileNFragment
import com.google.gson.Gson

class ContainerActivity : BaseActivity<ActivityContainerBinding>() {

    companion object {
        fun OpenContainer(
            context: Context?,
            type : String?,
            data:Any?
        ){
            context?.startActivity(Intent(context,ContainerActivity::class.java).apply{
                putExtra(CONTAINER_TYPE_KEY,type)
                when (data){
                    is Bundle -> putExtras(data)
                    else ->putExtra(DATA,Gson().toJson(data))
                }
            })

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContainerBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        statusBarColor(R.color.main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val type = intent.getStringExtra(CONTAINER_TYPE_KEY)
        print(type)
        println("TYPE Screen "+type)
        PassScreen(type)
    }

    private fun PassScreen(type: String?) {
        when(type){
            "EnterMobileNumber" ->{
                addFragment(EnterMobileNFragment.newInstance())
            }
        }
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.container,fragment).commit()
    }
}