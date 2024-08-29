package app.perkhub.Screens.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import app.perkhub.R
import app.perkhub.Screens.Home.Adapter.TransactionLayoutAdapter
import app.perkhub.Screens.Home.Model.TransactionModel.Data
import app.perkhub.Screens.Home.Model.TransactionModel.TransactionDataRes
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.api.ApiListener
import app.perkhub.databinding.FragmentTransactionHistoryBinding

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

        _binding!!.toolbar.name.text = "Store Transactions"

        getTransactionData()

        return _binding?.root
    }

    private fun getTransactionData() {
        viewModel.getAllTransaction(getUserID()!!,object : ApiListener<TransactionDataRes>{
            override fun onSuccess(t: TransactionDataRes?, mess: String) {
                if (t!!.errorCode.equals("0")){
                    setData(t.data)
                }
            }
        })
    }

    private fun setData(data: List<Data>) {
        val mAdapter = TransactionLayoutAdapter<Data>(object :RecyclerClickListener{
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {

            }
        })

        _binding!!.transactions.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            adapter = mAdapter
        }
        mAdapter.addAllItem(data)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            TransactionHistoryFragment().apply {

            }
    }
}