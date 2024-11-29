package app.perkhub.Screens.Home

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import app.perkhub.Screens.Home.Model.TransactionModel.TransactionDataRes
import app.perkhub.Screens.Home.Model.Withdrawal.WithdrawalReq
import app.perkhub.Screens.Home.Model.Withdrawal.withdrawalRes
import app.perkhub.StatusDialogFragment
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.FAILED
import app.perkhub.Utils.SUCCESS
import app.perkhub.Utils.isValidUpiId
import app.perkhub.api.ApiListener
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
        _binding!!.toolbar.name.text = "Withdraw Amount"

        getUserDetails()

        _binding!!.toolbar.backbtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher?.onBackPressed()
        }

        return _binding?.root
    }

    private fun getUserDetails() {
        viewModel.getAllTransaction(requireContext(),object : ApiListener<TransactionDataRes>{
            override fun onSuccess(t: TransactionDataRes?, mess: String) {
                t!!.let {
                    if (t!!.status==200){
                        callMainFunctions()
                        VerifiedAmount = it.verified
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
                    if (_binding!!.amount.text.toString().toDouble()<=VerifiedAmount.toDouble()){
                        SendForWithdrawal()
                    }else{
                        Toast.makeText(requireContext(),"Available Amount should not be less then ₹100",
                            Toast.LENGTH_SHORT).show()
                        ShowSuccessDialog(FAILED)
                    }
                }
            }
        }

    }

    private fun SendForWithdrawal() {
        var request = WithdrawalReq(
            request_amt = _binding!!.amount.text.toString(),
            upi_id = _binding!!.vpa.text.toString()
        )

        viewModel.ReqForWithdrawal(request,requireContext(),object : ApiListener<withdrawalRes>{
            override fun onSuccess(t: withdrawalRes?, mess: String) {
                t!!.let {
                    if (it.status==200){
                        ShowSuccessDialog(SUCCESS)
                    }else{
                        Toast.makeText(requireContext(),t.message,Toast.LENGTH_SHORT).show()
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