package app.perkhub.Screens.StoreTransaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import app.perkhub.ContainerActivity
import app.perkhub.Screens.Home.Adapter.TransactionLayoutAdapter
import app.perkhub.Screens.Home.Model.TransactionModel.Data
import app.perkhub.Screens.Home.Model.TransactionModel.TransactionDataRes
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.Utils.Snackbar
import app.perkhub.api.ApiListener
import app.perkhub.databinding.FragmentTransactionHistoryBinding
import app.perkhub.room.StoreTransaction.StoresTransactionsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TransactionHistoryFragment : BaseFragment<FragmentTransactionHistoryBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTransactionHistoryBinding.inflate(layoutInflater,container,false)
        _binding!!.toolbar.backbtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        _binding!!.toolbar.name.text = "Store Orders"

        getTransactionData()

        return _binding?.root
    }

//    private fun getTransactionData() {
//        viewModel.getAllTransaction(requireContext(),object : ApiListener<TransactionDataRes>{
//            override fun onSuccess(t: TransactionDataRes?, mess: String) {
//                if (t!!.status==200){
//                    requireActivity().runOnUiThread {
//                        setData(t.data.data)
//                    }
//                }
//            }
//        })
//    }


    private fun fetchStoreTransactionsList(forceRefresh: Boolean = false) {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getAllTransaction(
                requireContext(),
                forceRefresh,
                object : ApiListener<TransactionDataRes> {
                    override fun onSuccess(t: TransactionDataRes?, mess: String) {
                        t?.data?.let {
                            lifecycleScope.launch(Dispatchers.Main) {
                                // Don't call setData here if using Flow
                                // The Flow will handle it automatically
                            }
                        }
                    }

                    override fun onError(mess: String) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            requireContext().Snackbar("Failed to fetch Transactions",_binding!!.mainlayout)
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
                // This will be called automatically when:
                // 1. Fragment opens (shows cached data)
                // 2. Server data is fetched (shows fresh data)
                // 3. Push notification arrives (shows new notification)
                setData(t)
            }
        }

        // Trigger refresh from server in background
        fetchStoreTransactionsList(forceRefresh = true)
    }


    private fun setData(data: List<Data>) {

        val mAdapter = TransactionLayoutAdapter<Data>(object :RecyclerClickListener{
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                if (data is Data){
                    ContainerActivity.OpenContainer(requireContext(),"TransactionDetailFragment",data)
                }
            }
        })

        _binding!!.transactions.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            adapter = mAdapter
        }
        mAdapter.addAllItem(data)
        if(data.isEmpty()){
            binding.noDataFound.visibility = View.VISIBLE
            binding.transactions.visibility = View.GONE
        }else{
            binding.transactions.visibility = View.VISIBLE
            binding.noDataFound.visibility = View.GONE
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            TransactionHistoryFragment().apply {

            }
    }
}