package app.perkhub.Utils

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import app.perkhub.R
import app.perkhub.api.MyApi
import app.perkhub.api.RequestViewModel
import app.perkhub.room.HomeDatabase
import app.perkhub.room.HomeRepository
import app.perkhub.room.RequestViewModelFactory

abstract class BaseFragment<T:ViewBinding> : Fragment() {

    lateinit var viewModel: RequestViewModel
    var _binding : T? = null
    val binding get() = _binding!!

    lateinit var progress :Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val api = MyApi(requireContext())
        val db = HomeDatabase.getInstance(requireContext())
        val repository = HomeRepository(api, db.homeCacheDao())

        val factory = RequestViewModelFactory(repository) { ctx -> MyApi(ctx) }
        viewModel = ViewModelProvider(requireActivity(), factory)[RequestViewModel::class.java]

//        viewModel =ViewModelProvider(this).get(RequestViewModel::class.java)
        progress = Dialog(requireContext())
        progress.setContentView(R.layout.progress_dialog)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val decor = requireActivity().window.decorView
//            decor.systemUiVisibility = decor.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//        }
    }


    fun statusBarColor(color: Int) {
        activity?.window?.statusBarColor =
            ContextCompat.getColor(requireContext(), color)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun statusBarTextblack() {
//        theme?.applyStyle(R.style.TextViewBold, true)
        val controller =  activity?.window?.insetsController
        controller?.setSystemBarsAppearance(
            android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
            android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )
    }


    /**
     * true  -> dark icons (light background)
     * false -> light icons (dark background)
     */
    fun setStatusBarIconsDark(isDark: Boolean) {
        activity?.window?.let { window ->
            WindowInsetsControllerCompat(
                window,
                window.decorView
            ).isAppearanceLightStatusBars = isDark
        }
    }

    fun showProgress (){
        progress.show()
    }
    fun dissmissProgress (){
        progress.dismiss()
    }

    fun getSharedpref():AppSharedPefrence{
        return AppSharedPefrence.getInstance(requireContext())
    }

    fun getUserID():String?{
        return ""//getSharedpref().getUser(USERDATA).data.user_id.toString()
    }


    fun getToken(): String {
        return "Bearer " + getSharedpref().getString(TOKEN)
    }

}