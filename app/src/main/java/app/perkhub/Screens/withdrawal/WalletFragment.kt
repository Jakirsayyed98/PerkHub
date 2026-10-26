package app.perkhub.Screens.withdrawal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import app.perkhub.ContainerActivity
import app.perkhub.Screens.Home.Adapter.WalletWithdrawalAdapter
import app.perkhub.Screens.Home.Model.TransactionModel.TransactionDataRes
import app.perkhub.Screens.Home.Model.Withdrawal.withdrawalRes
import app.perkhub.Utils.ALL_TRANSACTIONS
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.PENDING_TRANSACTIONS
import app.perkhub.Utils.REJECTED_TRANSACTIONS
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.Utils.Snackbar
import app.perkhub.Utils.TRANSACTION_HISTORY_FRAGMENT
import app.perkhub.Utils.VERIFIED_TRANSACTIONS
import app.perkhub.Utils.WITHDRAW_FLOW_FRAGMENT
import app.perkhub.Utils.setSuffixAmount
import app.perkhub.api.ApiListener
import app.perkhub.databinding.FragmentWalletBinding
import app.perkhub.room.StoreTransaction.StoresTransactionsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WalletFragment : BaseFragment<FragmentWalletBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWalletBinding.inflate(layoutInflater, container, false)

        _binding!!.toolbar.backbtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher?.onBackPressed()
        }

        _binding!!.toolbar.name.text = "PerkHub Wallet"
        getTransactionData()


        _binding!!.pendingLayout.setOnClickListener {
            ContainerActivity.OpenContainer(requireContext(),TRANSACTION_HISTORY_FRAGMENT,PENDING_TRANSACTIONS)
        }

        _binding!!.rejectedLayout.setOnClickListener {
            ContainerActivity.OpenContainer(requireContext(),TRANSACTION_HISTORY_FRAGMENT,REJECTED_TRANSACTIONS)
        }

        _binding!!.verifiedLayout.setOnClickListener {
            ContainerActivity.OpenContainer(requireContext(),TRANSACTION_HISTORY_FRAGMENT, VERIFIED_TRANSACTIONS)
        }

        return _binding?.root
    }

    override fun onResume() {
        super.onResume()
        getWithdrawalTransaction()
    }
    private fun getWithdrawalTransaction() {
        viewModel.getWithdrawalTxn(requireContext(), object : ApiListener<withdrawalRes> {
            override fun onSuccess(t: withdrawalRes?, mess: String) {
                t?.let {
                    if (it.status == 200 && it.data.isNotEmpty()) {
                        // Ensure UI updates happen on the main thread
                        requireActivity().runOnUiThread {
                            setLayout(it.data)
                        }
                    }
                }
            }
        })
    }

    private fun setLayout(data: List<app.perkhub.Screens.Home.Model.Withdrawal.Data>) {
        val mAdapter =
            WalletWithdrawalAdapter<app.perkhub.Screens.Home.Model.Withdrawal.Data>(
                object : RecyclerClickListener {
                    override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                        // handle click here
                    }
                }
            )

        _binding!!.withdrawalhistory.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
            adapter = mAdapter
        }

        mAdapter.addAllItem(data)
        if (data.isEmpty()){
            binding.withdrawalHistoryLayout.visibility = View.GONE
            binding.noDataFound.visibility = View.VISIBLE
            binding.withdrawalhistory.visibility = View.GONE
        }else{
            binding.withdrawalHistoryLayout.visibility = View.VISIBLE
            binding.noDataFound.visibility = View.GONE
            binding.withdrawalhistory.visibility = View.VISIBLE
        }
    }

    private fun fetchStoreTransactionsList(forceRefresh: Boolean = false) {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getAllTransaction(
                requireContext(),
                forceRefresh,
                object : ApiListener<TransactionDataRes> {
                    override fun onSuccess(t: TransactionDataRes?, mess: String) {
                        t?.data?.let {
                            lifecycleScope.launch(Dispatchers.Main) {
                                t.data.let { data ->
                                    val avail = data.verified.toDouble()
                                    _binding!!.available.text = avail.toString()
                                    _binding!!.rejected.text = setSuffixAmount(data.rejected)
                                    _binding!!.pending.text = setSuffixAmount(data.pending)

                                    _binding!!.withdraw.setOnClickListener {
                                        if (avail >= 100) {
                                            ContainerActivity.OpenContainer(requireContext(),WITHDRAW_FLOW_FRAGMENT, avail.toString())
                                        } else {
                                            requireContext().Snackbar("Available Amount should not be less then ₹100",_binding!!.mainlayout)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    override fun onError(mess: String) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            requireContext().Snackbar("Failed to fetch Transactions: $mess",_binding!!.mainlayout)
                        }
                    }
                })
        }
    }


    private fun getTransactionData() {
        val repository = StoresTransactionsRepository(requireContext())

        // CRITICAL FIX: Use viewLifecycleOwner instead of lifecycleScope
        // This prevents memory leaks when Fragment is destroyed
        viewLifecycleOwner.lifecycleScope.launch {
            repository.getStoresTransactionsFlow().collect { t ->

            }
        }

        // Trigger refresh from server in background
        fetchStoreTransactionsList(forceRefresh = true)
    }


    private fun setUserData(data: TransactionDataRes) {
        _binding!!.withdraw.setOnClickListener {
            // Reserved for future implementation
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = WalletFragment().apply { }
    }
}
