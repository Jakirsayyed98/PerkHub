package app.perkhub.Screens.withdrawal

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
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
import app.perkhub.Utils.RecyclerClickListener
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
        _binding!!.toolbar.name.text = "Withdraw Amount"
        _binding!!.toolbar.backbtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher?.onBackPressed()
        }

        _binding!!.amount.addTextChangedListener {
            if (it.toString()!=""){
                if (it.toString().toInt()>=100){
                    _binding!!.continueBtn.isSelected = true
                }else{
                    _binding!!.continueBtn.isSelected = false
                }
            }

        }

        _binding!!.continueBtn.setOnClickListener {
            if(_binding!!.continueBtn.isSelected){
                getPaymentMethod()

            }
        }

        return _binding?.root
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