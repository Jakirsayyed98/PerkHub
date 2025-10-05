package app.perkhub.Screens.withdrawal

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import app.perkhub.Screens.withdrawal.model.RequestWithdrawalReq
import app.perkhub.Screens.withdrawal.model.RequestWithdrawalRes
import app.perkhub.StatusDialogFragment
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.DATA
import app.perkhub.Utils.FAILED
import app.perkhub.Utils.SUCCESS
import app.perkhub.Utils.setSuffixAmount
import app.perkhub.api.ApiListener
import app.perkhub.databinding.FragmentWithdrawalConfirmationBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class WithdrawalConfirmationFragment : BaseFragment<FragmentWithdrawalConfirmationBinding>() {
    var amount:String="0"
    var paymentMethodId:String="0"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWithdrawalConfirmationBinding.inflate(layoutInflater,container,false)
        _binding!!.toolbar.name.text = "Withdraw Funds"
        applySafeAreaInsets()

        val jsonString = activity?.intent?.getStringExtra(DATA)
        val mapData: HashMap<String, String>? = jsonString?.let {
            val type = object : TypeToken<HashMap<String, String>>() {}.type
            Gson().fromJson<HashMap<String, String>>(it, type)
        }

        mapData?.let {
            _binding!!.requestedAmount.text = setSuffixAmount(it["amount"].toString())
            _binding!!.totalAmount.text = setSuffixAmount(it["amount"].toString())
            _binding!!.bankName.text = it["bankName"].toString()
            _binding!!.accountNumber.text = it["accountNumber"].toString()
            amount=it["amount"].toString()
            paymentMethodId=it["paymentMethodId"].toString()
        }

        _binding!!.toolbar.backbtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher?.onBackPressed()
        }
        _binding!!.cnfWithdraw.setOnClickListener {
            WithdrawalRequest(amount,paymentMethodId)
        }
        return _binding?.root
    }

    private fun WithdrawalRequest(amount:String, paymentMethodId:String) {
        val requestBody = RequestWithdrawalReq(
            request_amt = amount,
            payment_method = paymentMethodId
        )
        viewModel.RequestWithdrawalPayment(requestBody,requireContext(),object :ApiListener<RequestWithdrawalRes>{
            override fun onSuccess(t: RequestWithdrawalRes?, mess: String) {
                t!!.let {
                    if (it.status==200){
                        showSuccessDialog(SUCCESS)
                    }else{
                        showSuccessDialog(FAILED)
                    }
                }
            }

            override fun onError(mess: String) {
                super.onError(mess)
                showSuccessDialog(FAILED)
            }

        })
    }

    private fun applySafeAreaInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.toolbar2.updatePadding(top = systemBars.top)
            binding.mainlayout.updatePadding(bottom = systemBars.bottom)
            insets
        }
    }

    private fun showSuccessDialog(status: String) {
        val dialogFragment = StatusDialogFragment.newInstance(status)
        dialogFragment.show(childFragmentManager, "StatusDialogFragment")

        // Dismiss the dialog after 4 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            if (dialogFragment.isAdded) {
                dialogFragment.dismiss()
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }, 4000)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            WithdrawalConfirmationFragment().apply {

            }
    }
}