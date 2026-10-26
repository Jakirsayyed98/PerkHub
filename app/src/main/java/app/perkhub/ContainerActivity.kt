package app.perkhub

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import app.perkhub.Screens.Games.GamesFragment
import app.perkhub.Screens.Games.SearchGameFragment
import app.perkhub.Screens.Home.HomeScreenFragment
import app.perkhub.Screens.Notifications.NotificationListFragment
import app.perkhub.Screens.Offers.OffersFragment
import app.perkhub.Screens.Profile.NewTicketFragment
import app.perkhub.Screens.Profile.ProfileFragment
import app.perkhub.Screens.Profile.TicketListFragment
import app.perkhub.Screens.Profile.TicketMessageFragment
import app.perkhub.Screens.Stores.SelectedCategoryFragment
import app.perkhub.Screens.Stores.StoreDetailFragment
import app.perkhub.Screens.StoreTransaction.TransactionDetailFragment
import app.perkhub.Screens.StoreTransaction.TransactionHistoryFragment
import app.perkhub.Screens.withdrawal.WalletFragment
import app.perkhub.Screens.withdrawal.WithdrawalFlowFragment
import app.perkhub.Utils.BaseActivity
import app.perkhub.Utils.CONTAINER_TYPE_KEY
import app.perkhub.Utils.DATA
import app.perkhub.databinding.ActivityContainerBinding
import app.perkhub.Screens.login.EnterDetailFragment
import app.perkhub.Screens.login.EnterMobileNFragment
import app.perkhub.Screens.withdrawal.AddPaymentMethodFragment
import app.perkhub.Screens.withdrawal.WithdrawalConfirmationFragment
import app.perkhub.Utils.CONTAINER_TYPE_KEY1
import com.google.gson.Gson
import android.Manifest
import app.perkhub.Screens.Profile.EditPersonalInfoFragment
import app.perkhub.Screens.login.OTP_verification_Fragment
import app.perkhub.Utils.EDIT_PERSONAL_INFO_FRAGMENT
import app.perkhub.Utils.ENTER_DETAIL_FRAGMENT
import app.perkhub.Utils.ENTER_MOBILE_NUMBER
import app.perkhub.Utils.HOME_SCREEN_FRAGMENT
import app.perkhub.Utils.PROFILE_SCREEN_FRAGMENT
import app.perkhub.Utils.SELECTED_CATEGORY_FRAGMENT
import app.perkhub.Utils.TRANSACTION_HISTORY_FRAGMENT
import app.perkhub.Utils.VERIFY_OTP_FRAGMENT
import app.perkhub.Utils.WITHDRAW_FLOW_FRAGMENT

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
    private val notificationPermissionRequestCode = 1001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContainerBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.WHITE
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
        }

        setContentView(binding.root)
        requestNotificationPermissionIfNeeded()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                left = systemBars.left,
                top = systemBars.top,
                right = systemBars.right,
                bottom = systemBars.bottom
            )
            insets
        }
        val type = intent.getStringExtra(CONTAINER_TYPE_KEY)
        PassScreen(type)
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    notificationPermissionRequestCode
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == notificationPermissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied — you can show a message if needed
            }
        }


    }


    private fun PassScreen(type: String?) {
        when(type){
            ENTER_MOBILE_NUMBER ->{
                addFragment(EnterMobileNFragment.newInstance())
            }
            VERIFY_OTP_FRAGMENT ->{
                addFragment(OTP_verification_Fragment.newInstance())
            }
            ENTER_DETAIL_FRAGMENT ->{
                addFragment(EnterDetailFragment.newInstance())
            }

            HOME_SCREEN_FRAGMENT ->{
                addFragment(HomeScreenFragment.newInstance())
            }

            SELECTED_CATEGORY_FRAGMENT ->{
                addFragment(SelectedCategoryFragment.newInstance())
            }

            "StoreDetailFragment"->{
                addFragment(StoreDetailFragment.newInstance())
            }
            TRANSACTION_HISTORY_FRAGMENT ->{
                addFragment(TransactionHistoryFragment.newInstance())
            }
            "TransactionDetailFragment"->{
                addFragment(TransactionDetailFragment.newInstance())
            }

            "WalletFragment"->{
                addFragment(WalletFragment.newInstance())
            }
            WITHDRAW_FLOW_FRAGMENT ->{
                addFragment(WithdrawalFlowFragment.newInstance())
            }
            "NotificationListFragment"->{
                addFragment(NotificationListFragment.newInstance())
            }
            "AddWithdrawalPaymentMethod"->{
                addFragment(AddPaymentMethodFragment.newInstance())
            }

            "WithdrawalConfirmationFragment"->{
                addFragment(WithdrawalConfirmationFragment.newInstance())
            }

            PROFILE_SCREEN_FRAGMENT->{
                addFragment(ProfileFragment.newInstance())
            }
            EDIT_PERSONAL_INFO_FRAGMENT->{
                addFragment(EditPersonalInfoFragment.newInstance())
            }

            "TicketListFragment"->{
                addFragment(TicketListFragment.newInstance())
            }

            "NewTicketFragment"->{
                addFragment(NewTicketFragment.newInstance())
            }

            "TicketMessageFragment"->{
                addFragment(TicketMessageFragment.newInstance())
            }

            "OffersFragment"->{
                addFragment(OffersFragment.newInstance())
            }

             "GamesFragment"->{
                addFragment(GamesFragment.newInstance())
             }

            "SearchGameFragment"->{
                addFragment(SearchGameFragment.newInstance())
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