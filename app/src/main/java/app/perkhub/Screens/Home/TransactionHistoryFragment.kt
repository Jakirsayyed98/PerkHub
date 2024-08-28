package app.perkhub.Screens.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.perkhub.R
import app.perkhub.Utils.BaseFragment
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

        return _binding?.root
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            TransactionHistoryFragment().apply {

            }
    }
}