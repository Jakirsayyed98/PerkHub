package app.perkhub.Utils

import android.app.Dialog
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import app.perkhub.R
import app.perkhub.api.RequestViewModel

abstract class BaseFragment<T:ViewBinding> : Fragment() {

    lateinit var viewModel: RequestViewModel
    var _binding : T? = null
    val binding get() = _binding!!

    lateinit var progress :Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =ViewModelProvider(this).get(RequestViewModel::class.java)
        progress = Dialog(requireContext())
        progress.setContentView(R.layout.progress_dialog)
    }

    fun statusBarColor(color : Int){
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(),color)
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
}