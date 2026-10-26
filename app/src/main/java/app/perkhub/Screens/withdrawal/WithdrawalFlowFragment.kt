package app.perkhub.Screens.withdrawal

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.perkhub.ContainerActivity
import app.perkhub.R
import app.perkhub.Screens.withdrawal.Adapter.PaymentMethodsAdapter
import app.perkhub.Screens.withdrawal.model.Data
import app.perkhub.Screens.withdrawal.model.getPaymentMethodsRes
import app.perkhub.StatusDialogFragment
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.DATA
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.Utils.Snackbar
import app.perkhub.Utils.setSuffixAmount
import app.perkhub.api.ApiListener
import app.perkhub.databinding.FragmentWithdrawalFlowBinding


class WithdrawalFlowFragment : BaseFragment<FragmentWithdrawalFlowBinding>() {

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
        _binding!!.toolbar.backbtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        val data =  requireActivity().intent.getStringExtra(DATA)?.removeSurrounding("\"")
        binding.availableAmount.text = setSuffixAmount(data.toString())
        _binding!!.continueBtn.setOnClickListener {
            val availableAmount = data.toString().toFloatOrNull()
            if (availableAmount == null) {
                requireContext().Snackbar("Unable to fetch available amount", _binding!!.root)
                return@setOnClickListener
            }
            WithdrawFlowContinue(availableAmount)
        }

        return _binding?.root
    }

    fun WithdrawFlowContinue(availableAmount : Float){
        val enteredAmount = _binding!!.amount.text.toString().trim().toIntOrNull()
        when {
            enteredAmount == null -> {
                requireContext().Snackbar("Please enter a valid amount", _binding!!.mainLayout)
            }

            availableAmount < 100 -> {
                requireContext().Snackbar("Available amount must be at least 100", _binding!!.mainLayout)
            }

            enteredAmount <= 0 -> {
                requireContext().Snackbar("Amount must be greater than 0", _binding!!.mainLayout)
            }

            enteredAmount < 100 -> {
                requireContext().Snackbar("Minimum withdrawal amount is 100", _binding!!.mainLayout)
            }

            enteredAmount > availableAmount -> {
                requireContext().Snackbar("Amount should not be greater than available amount", _binding!!.mainLayout)
            }

            else -> {
                getPaymentMethod()
            }
        }
    }
    private fun getPaymentMethod() {

        viewModel.GetPaymentMethod(requireContext(),object:ApiListener<getPaymentMethodsRes>{
            override fun onSuccess(t: getPaymentMethodsRes?, mess: String) {
                t!!.let {
                    if(t.status==200){
                        if (t.data.size>0){
                            requireActivity().runOnUiThread {
                                requireContext().ShowPaymentMethods(it.data)
                            }

                        }else{
                            ContainerActivity.OpenContainer(requireContext(),"AddWithdrawalPaymentMethod",null)
                        }
                    }
                }
            }

        })
    }

    private fun Context.ShowPaymentMethods(data: ArrayList<Data>) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.payment_method_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val AddMore = dialog.findViewById<AppCompatButton>(R.id.AddMore)
        val paymentMethods = dialog.findViewById<RecyclerView>(R.id.paymentMethods)
        dialog.setCancelable(true)
        setPaymethodList(data,paymentMethods,dialog)
        AddMore.setOnClickListener {
            ContainerActivity.OpenContainer(requireContext(),"AddWithdrawalPaymentMethod",null)
        }
        dialog.show()
    }


    private fun setPaymethodList(data: ArrayList<Data>,paymentMethods: RecyclerView,dialog: Dialog) {
        val withdrawal =HashMap<String,String>()
        withdrawal["amount"]=_binding!!.amount.text.toString()

        val adapter = PaymentMethodsAdapter<Data>(object :RecyclerClickListener{
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                if(data is Data){
                    withdrawal["bankName"]= data.bank_name ?: "UPI"
                    withdrawal["accountNumber"]=data.identifier!!
                    withdrawal["paymentMethodId"]=data.id
                    dialog.dismiss()
                    ContainerActivity.OpenContainer(requireContext(),"WithdrawalConfirmationFragment",withdrawal)
                    requireActivity().onBackPressedDispatcher?.onBackPressed()
                }

            }
        })

        paymentMethods.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            this.adapter = adapter
        }
        adapter.addAllItem(data)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            WithdrawalFlowFragment().apply {

            }
    }
}