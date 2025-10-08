package app.perkhub.Screens.withdrawal

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.addTextChangedListener
import app.perkhub.R
import app.perkhub.Screens.withdrawal.model.addPaymentMethodReq
import app.perkhub.Screens.withdrawal.model.addPaymentMethodRes
import app.perkhub.Utils.BaseFragment
import app.perkhub.api.ApiListener
import app.perkhub.databinding.FragmentAddPaymentMethodBinding
import app.perkhub.databinding.FragmentWithdrawalFlowBinding

class AddPaymentMethodFragment : BaseFragment<FragmentAddPaymentMethodBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddPaymentMethodBinding.inflate(layoutInflater,container,false)
        _binding!!.toolbar.name.text = "Add Payment Method"
        _binding!!.toolbar.backbtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher?.onBackPressed()
        }

        setUpData()
        return _binding?.root
    }

    private fun setUpData() {

        _binding!!.upi.addTextChangedListener {
            _binding!!.continuebtn.isSelected = it.toString().length >= 10
        }

        _binding!!.apply {
            val updateBankSaveButton = {
                bankdetailSave.isSelected =
                    accountHolderName.text?.isNotEmpty() == true &&
                            accountNumber.text?.isNotEmpty() == true &&
                            bankName.text?.isNotEmpty() == true &&
                            accountIFSC.text?.isNotEmpty() == true
            }

            accountHolderName.addTextChangedListener { updateBankSaveButton() }
            accountNumber.addTextChangedListener { updateBankSaveButton() }
            bankName.addTextChangedListener { updateBankSaveButton() }
            accountIFSC.addTextChangedListener { updateBankSaveButton() }
        }

        val requestBody = addPaymentMethodReq(
            upi_id = "",
            bank_name = "",
            bank_account_number = "",
            ifsc_code = ""
        )

        _binding!!.continuebtn.setOnClickListener {
            if(_binding!!.continuebtn.isSelected){
                requestBody!!.upi_id = _binding!!.upi.text.toString()
                AddBankDetail(requestBody)
            }
        }

        _binding!!.bankdetailSave.setOnClickListener {
            if(_binding!!.bankdetailSave.isSelected){
                requestBody.bank_name = _binding!!.bankName.text.toString()
                requestBody.bank_account_number = _binding!!.accountNumber.text.toString()
                requestBody.ifsc_code = _binding!!.accountNumber.text.toString()
                AddBankDetail(requestBody)
            }
        }
    }

    private fun AddBankDetail(requestBody: addPaymentMethodReq) {
        viewModel.AddPaymentMethod(requestBody,requireContext(),object : ApiListener<addPaymentMethodRes>{
            override fun onSuccess(t: addPaymentMethodRes?, mess: String) {
                requireActivity().runOnUiThread {
                    requireActivity().onBackPressedDispatcher?.onBackPressed()
                }
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AddPaymentMethodFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}