package app.perkhub.Screens.Home

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import app.perkhub.R
import app.perkhub.Screens.Home.Model.Withdrawal.withdrawalRes
import app.perkhub.Screens.login.models.LoginRes
import app.perkhub.StatusDialogFragment
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.FAILED
import app.perkhub.Utils.SUCCESS
import app.perkhub.Utils.isValidUpiId
import app.perkhub.api.ApiListener
import app.perkhub.databinding.FragmentWidthdrawFlowBinding


class WidthdrawFlowFragment : BaseFragment<FragmentWidthdrawFlowBinding>() {

    var userdata : LoginRes?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWidthdrawFlowBinding.inflate(layoutInflater,container,false)
        _binding!!.toolbar.name.text = "Withdraw Amount"

        getUserDetails()

        _binding!!.toolbar.backbtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher?.onBackPressed()
        }

        return _binding?.root
    }

    private fun getUserDetails() {
        viewModel.getUserDetail(requireContext(),object : ApiListener<LoginRes>{
            override fun onSuccess(t: LoginRes?, mess: String) {
                t!!.let {
                    if (it.errorCode.equals("0")){
                        userdata = it
                        callMainFunctions()
                    }
                }
            }
        })
    }

    private fun callMainFunctions() {

        _binding!!.vpa.addTextChangedListener {
            if (isValidUpiId(_binding!!.vpa.text.toString())){
                if (_binding!!.amount.text.toString().toInt()>=100){
                    _binding!!.sendOTP.isSelected = true
                }else{
                    _binding!!.sendOTP.isSelected = false
                }
            }else{
                _binding!!.vpa.setError("Please Enter valid UPI id")
                _binding!!.sendOTP.isSelected = false
            }
        }
        _binding!!.amount.addTextChangedListener {
            if (_binding!!.amount.text.toString().toInt()>=100){
                if (isValidUpiId(_binding!!.vpa.text.toString())){
                    _binding!!.sendOTP.isSelected = true
                }else{
                    _binding!!.sendOTP.isSelected = false
                }
            }else{
                _binding!!.sendOTP.isSelected = false
            }
        }

        _binding!!.sendOTP.setOnClickListener {
            if (_binding!!.sendOTP.isSelected){
                if (isValidUpiId(_binding!!.vpa.text.toString())){
                    if (_binding!!.amount.text.toString().toDouble()<=userdata!!.data.verified.toDouble()){
                        SendForWithdrawal()
                    }
                }
            }
        }

    }

    private fun SendForWithdrawal() {
        viewModel.ReqForWithdrawal(getUserID()!!,_binding!!.vpa.text.toString(),_binding!!.amount.text.toString(),requireContext(),object : ApiListener<withdrawalRes>{
            override fun onSuccess(t: withdrawalRes?, mess: String) {
                t!!.let {
                    if (it.errorCode.equals("0") && it.data.size>=0){
                        ShowSuccessDialog(SUCCESS)
                    }else{
                        ShowSuccessDialog(FAILED)
                    }
                }
            }
        })
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
              }
          }, 4000) // 2000 milliseconds = 2 seconds
      }catch (e :Exception){
            e.printStackTrace()
      }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            WidthdrawFlowFragment().apply {

            }
    }
}