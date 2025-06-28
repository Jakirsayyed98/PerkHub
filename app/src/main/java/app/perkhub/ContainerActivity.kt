package app.perkhub

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import app.perkhub.Screens.Home.HomeScreenFragment
import app.perkhub.Screens.Home.NotificationListFragment
import app.perkhub.Screens.Home.SelectedCategoryFragment
import app.perkhub.Screens.Home.StoreDetailFragment
import app.perkhub.Screens.Home.TransactionDetailFragment
import app.perkhub.Screens.Home.TransactionHistoryFragment
import app.perkhub.Screens.Home.WalletFragment
import app.perkhub.Screens.Home.WithdrawalFlowFragment
import app.perkhub.Utils.BaseActivity
import app.perkhub.Utils.CONTAINER_TYPE_KEY
import app.perkhub.Utils.DATA
import app.perkhub.databinding.ActivityContainerBinding
import app.perkhub.Screens.login.EnterDetailFragment
import app.perkhub.Screens.login.EnterMobileNFragment
import app.perkhub.Utils.CONTAINER_TYPE_KEY1
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
        fun OpenContainerType2(
            context: Context?,
            type : String?,
            type1 : String?,
            data:Any?
        ){
            context?.startActivity(Intent(context,ContainerActivity::class.java).apply{
                putExtra(CONTAINER_TYPE_KEY,type)
                putExtra(CONTAINER_TYPE_KEY1,type1)
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
//        enableEdgeToEdge()
        setContentView(binding.root)
//        statusBarColor(R.color.white)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        window.statusBarColor = ContextCompat.getColor(this, android.R.color.white)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }


        val type = intent.getStringExtra(CONTAINER_TYPE_KEY)
        PassScreen(type)
    }

    private fun PassScreen(type: String?) {
        when(type){
            "EnterMobileNumber" ->{
                addFragment(EnterMobileNFragment.newInstance())
            }
            "EnterDetailFragment" ->{
                addFragment(EnterDetailFragment.newInstance())
            }

            "HomeScreenFragment" ->{
                addFragment(HomeScreenFragment.newInstance())
            }

            "SelectedCategoryFragment" ->{
                addFragment(SelectedCategoryFragment.newInstance())
            }

            "StoreDetailFragment"->{
                addFragment(StoreDetailFragment.newInstance())
            }
            "TransactionHistoryFragment"->{
                addFragment(TransactionHistoryFragment.newInstance())
            }
            "TransactionDetailFragment"->{
                addFragment(TransactionDetailFragment.newInstance())
            }

            "WalletFragment"->{
                addFragment(WalletFragment.newInstance())
            }
            "WidthdrawFlowFragment"->{
                addFragment(WithdrawalFlowFragment.newInstance())
            }
            "NotificationListFragment"->{
                addFragment(NotificationListFragment.newInstance())
            }
        }
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.container,fragment).commit()
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.container)
        if (fragment is HomeScreenFragment) {
            fragment.onBackPressed()  // Notify the fragment of the back press
        } else {
            super.onBackPressed()  // Otherwise, use the default behavior
        }
    }

}