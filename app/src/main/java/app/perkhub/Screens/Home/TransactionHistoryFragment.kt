package app.perkhub.Screens.Home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import app.perkhub.ContainerActivity
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

        _binding!!.toolbar.name.text = "Store Orders"

        getTransactionData()
        applySafeAreaInsets()
        return _binding?.root
    }

    private fun getTransactionData() {
        viewModel.getAllTransaction(requireContext(),object : ApiListener<TransactionDataRes>{
            override fun onSuccess(t: TransactionDataRes?, mess: String) {
                if (t!!.status==200){
                    requireActivity().runOnUiThread {
                        setData(t.data.data)
                    }
                }
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
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            TransactionHistoryFragment().apply {

            }
    }
}