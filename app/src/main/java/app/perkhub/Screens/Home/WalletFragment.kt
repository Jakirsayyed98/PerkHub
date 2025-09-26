package app.perkhub.Screens.Home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import app.perkhub.ContainerActivity
import app.perkhub.R
import app.perkhub.Screens.Home.Adapter.WalletWithdrawalAdapter
import app.perkhub.Screens.Home.Model.TransactionModel.TransactionDataRes
import app.perkhub.Screens.Home.Model.Withdrawal.withdrawalRes
import app.perkhub.Screens.login.models.Data
import app.perkhub.Screens.login.models.LoginRes
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.Utils.setSuffixAmount
import app.perkhub.api.ApiListener
import app.perkhub.databinding.FragmentWalletBinding


class WalletFragment : BaseFragment<FragmentWalletBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWalletBinding.inflate(layoutInflater,container,false)
        _binding!!.toolbar.backbtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher?.onBackPressed()
        }

        _binding!!.toolbar.name.text = "Wallet"

        applySafeAreaInsets()
        getTransactionData()
//        getUserDetail()
        getWithdrawalTransaction()
        return _binding?.root
    }

    private fun getTransactionData() {
        viewModel.getAllTransaction(requireContext(),object : ApiListener<TransactionDataRes>{
            override fun onSuccess(t: TransactionDataRes?, mess: String) {
                if (t!!.status==200){
                    t.data.let {data->
                        val avail = data.verified.toDouble()// - data.withdrawal.toDouble()
                        _binding!!.available.text = setSuffixAmount(avail.toString())
                        _binding!!.rejected.text = setSuffixAmount(data.rejected)
                        _binding!!.pending.text = setSuffixAmount(data.pending)
                        _binding!!.withdraw.setOnClickListener {
                            Log.d("MyBalance",avail.toString())
                            if (avail>=100){
                                ContainerActivity.OpenContainer(requireContext(),"WidthdrawFlowFragment","")
                            }else{
                                Toast.makeText(requireContext(),"Available Amount should not be less then ₹100",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        })
    }

    private fun getWithdrawalTransaction() {
        viewModel.getWithdrawalTxn(requireContext(),object : ApiListener<withdrawalRes>{
            override fun onSuccess(t: withdrawalRes?, mess: String) {
                t!!.let {
                    if (it.status==200){
                        if (it.data.size>0){
                            setLayout(it.data)
                        }
                    }
                }
            }

        })
    }

    private fun setLayout(data: List<app.perkhub.Screens.Home.Model.Withdrawal.Data>) {
        val mAdapter = WalletWithdrawalAdapter<app.perkhub.Screens.Home.Model.Withdrawal.Data>(object : RecyclerClickListener{
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {

            }
        })

        _binding!!.withdrawalhistory.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,true)
            adapter = mAdapter
        }

        mAdapter.addAllItem(data)
    }

    private fun getUserDetail() {
            viewModel.getAllTransaction(requireContext(),object : ApiListener<TransactionDataRes>{
                override fun onSuccess(t: TransactionDataRes?, mess: String) {
                    t.let {
                        if (t!!.status==200){
                            setUserData(t)
                        }
                    }
                }
            })
    }

    private fun setUserData(data: TransactionDataRes) {
//        val avail = data.verified.toDouble()// - data.withdrawal.toDouble()
//        _binding!!.available.text = setSuffixAmount(avail.toString())
//        _binding!!.rejected.text = setSuffixAmount(data.rejected)
//        _binding!!.pending.text = setSuffixAmount(data.pending)
        _binding!!.withdraw.setOnClickListener {
//            if (avail>=100){
//                ContainerActivity.OpenContainer(requireContext(),"WidthdrawFlowFragment","")
//            }else{
//                Toast.makeText(requireContext(),"Available Amount should not be less then ₹100",Toast.LENGTH_SHORT).show()
//            }
        }
    }

    private fun applySafeAreaInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.toolbar2.updatePadding(top = systemBars.top)
//            binding.navView.updatePadding(top = systemBars.top)
            binding.mainlayout.updatePadding(bottom = systemBars.bottom)
            insets
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            WalletFragment().apply {

            }
    }
}