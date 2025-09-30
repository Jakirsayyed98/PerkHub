package app.perkhub.Screens.withdrawal

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import app.perkhub.ContainerActivity
import app.perkhub.StatusDialogFragment
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.toast
import app.perkhub.databinding.FragmentWithdrawalFlowBinding


class WithdrawalFlowFragment : BaseFragment<FragmentWithdrawalFlowBinding>() {

//    var userdata : LoginRes?=null
    var VerifiedAmount = "0"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWithdrawalFlowBinding.inflate(layoutInflater,container,false)
        _binding!!.toolbar.name.text = "Withdraw Funds"
        applySafeAreaInsets()

        _binding!!.toolbar.backbtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher?.onBackPressed()
        }

        _binding!!.amount.addTextChangedListener {
            if (it.toString()!=""){
                if (it.toString().toInt()>=100){
                    _binding!!.continuebtn.isSelected = true
                }else{
                    _binding!!.continuebtn.isSelected = false
                }
            }

        }

        _binding!!.continuebtn.setOnClickListener {
            if(_binding!!.continuebtn.isSelected){
                ContainerActivity.OpenContainer(requireContext(),"AddWithdrawalPaymentMethod",null)
            }
        }

        return _binding?.root
    }

    private fun applySafeAreaInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.toolbar2.updatePadding(top = systemBars.top)
            binding.mainlayout.updatePadding(bottom = systemBars.bottom)
            insets
        }
    }

    private fun ShowSuccessDialog(status: String) {
        val dialogFragment = StatusDialogFragment.newInstance(status)
        val fragmentManager: FragmentManager = childFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        dialogFragment.show(fragmentTransaction, "StatusDialogFragment")

        // Dismiss the dialog after 2 seconds
      try {
          Handler().postDelayed({
              if (dialogFragment.isAdded) {
                  dialogFragment.dismiss()
                  requireActivity().onBackPressedDispatcher?.onBackPressed()
              }
          }, 4000) // 2000 milliseconds = 2 seconds
      }catch (e :Exception){
            e.printStackTrace()
      }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            WithdrawalFlowFragment().apply {

            }
    }
}