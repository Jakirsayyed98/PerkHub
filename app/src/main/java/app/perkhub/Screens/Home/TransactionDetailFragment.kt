package app.perkhub.Screens.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import app.perkhub.R
import app.perkhub.Screens.Home.Model.TransactionModel.Data
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.DATA
import app.perkhub.Utils.onlyDatewithMonth
import app.perkhub.Utils.setSuffixAmount
import app.perkhub.databinding.FragmentTransactionDetailBinding
import com.bumptech.glide.Glide
import com.google.gson.Gson


class TransactionDetailFragment : BaseFragment<FragmentTransactionDetailBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTransactionDetailBinding.inflate(layoutInflater,container,false)
        _binding!!.toolbar.backbtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher?.onBackPressed()
        }

        _binding!!.toolbar.name.text = "Transaction Detail"

        val data =Gson().fromJson<Data>( requireActivity().intent?.getStringExtra(DATA),Data::class.java)
        setData(data)
        return _binding?.root
    }


    private fun setData(data: Data?) {
        _binding!!.storename.text = data!!.miniApp[0].name
        Glide.with(requireContext()).load(data.miniApp[0].logo).into(_binding!!.logo)
        _binding!!.transactiondate.text = onlyDatewithMonth(data.created_at)
        _binding!!.transactionAmt.text = setSuffixAmount(data.sale_amount)
        _binding!!.commision.text = setSuffixAmount(data.user_commission)
        _binding!!.transactionstatus.text = if (data.status=="0") "Your Transaction is Tracked Successfully" else if(data.status=="1") "Your Transaction is Verified" else "Your Transaction is Rejected"
        _binding!!.status.text = if (data.status=="0") "Transaction in Pending" else if(data.status=="1") "Transaction is Verified" else "Transaction is Rejected"
        _binding!!.transactionstatus.setTextColor( if (data.status=="0") ContextCompat.getColor(requireContext(),R.color.orange) else if(data.status=="1")  ContextCompat.getColor(requireContext(),R.color.green) else  ContextCompat.getColor(requireContext(),R.color.red))
        _binding!!.icon.setColorFilter(if (data.status=="0") ContextCompat.getColor(requireContext(),R.color.orange) else if(data.status=="1")  ContextCompat.getColor(requireContext(),R.color.green) else  ContextCompat.getColor(requireContext(),R.color.red))

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            TransactionDetailFragment().apply {
            }
    }
}