package app.perkhub.Screens.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import app.perkhub.ContainerActivity
import app.perkhub.R
import app.perkhub.Screens.Home.Adapter.WalletWithdrawalAdapter
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

        _binding!!.withdraw.setOnClickListener {
            ContainerActivity.OpenContainer(requireContext(),"WidthdrawFlowFragment","")
        }

        getUserDetail()
        getWithdrawalTransaction()
        return _binding?.root
    }

    private fun getWithdrawalTransaction() {
        viewModel.getWithdrawalTxn(getUserID()!!,requireContext(),object : ApiListener<withdrawalRes>{
            override fun onSuccess(t: withdrawalRes?, mess: String) {
                t!!.let {
                    if (it.errorCode.equals("0")){
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
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            adapter = mAdapter
        }

        mAdapter.addAllItem(data)
    }

    private fun getUserDetail() {
            viewModel.getUserDetail(requireContext(),object : ApiListener<LoginRes>{
                override fun onSuccess(t: LoginRes?, mess: String) {
                    t.let {
                        if (t!!.errorCode.equals("0")){
                            setUserData(t.data)
                        }
                    }
                }
            })
    }

    private fun setUserData(data: Data) {
        _binding!!.available.text = setSuffixAmount(data.verified)
//        _binding!!.rejected.text = setSuffixAmount(data.rejected)
//        _binding!!.pending.text = setSuffixAmount(data.pending)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            WalletFragment().apply {

            }
    }
}