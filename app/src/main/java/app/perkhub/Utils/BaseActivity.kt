package app.perkhub.Utils

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<T : ViewBinding> : AppCompatActivity() {
    lateinit var binding: ViewBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

//    fun statusBarColor(color : Int){
//        window.statusBarColor = ContextCompat.getColor(this,color)
//    }

    fun getSharedpref():AppSharedPefrence{
        return AppSharedPefrence.getInstance(this)
    }

}