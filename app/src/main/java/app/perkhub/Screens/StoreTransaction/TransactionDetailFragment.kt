package app.perkhub.Screens.StoreTransaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
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

        _binding!!.storename.text = data!!.miniapp_name
        Glide.with(requireContext()).load(data!!.icon).into(_binding!!.logo)

        _binding!!.transactiondate.text = onlyDatewithMonth(data!!.created_at)
        _binding!!.transactionId.text ="Transaction Id:- #TX"+ data!!.transaction_id
        _binding!!.transactionAmt.text = setSuffixAmount(data!!.sale_amount)
        _binding!!.commision.text = setSuffixAmount(data!!.user_commission)
        _binding!!.transactionstatus.text = if (data!!.status=="pending") "Pending" else if(data!!.status=="verified") "Verified" else "Rejected"
//        _binding!!.transactionstatus.text = if (data!!.status=="pending") "Your Transaction is Tracked Successfully" else if(data!!.status=="verified") "Your Transaction is Verified" else "Your Transaction is Rejected"
        _binding!!.status.text = if (data!!.status=="pending") "Transaction in Pending" else if(data!!.status=="verified") "Transaction is Verified" else "Transaction is Rejected"
        _binding!!.transactionstatus.setTextColor( if (data!!.status=="pending") ContextCompat.getColor(requireContext(),R.color.orange) else if(data!!.status=="verified")  ContextCompat.getColor(requireContext(),R.color.green) else  ContextCompat.getColor(requireContext(),R.color.red))
        _binding!!.icon.setColorFilter(if (data!!.status=="pending") ContextCompat.getColor(requireContext(),R.color.orange) else if(data!!.status=="verified")  ContextCompat.getColor(requireContext(),R.color.green) else  ContextCompat.getColor(requireContext(),R.color.red))
        _binding!!.statusView.setCardBackgroundColor(if (data!!.status=="pending") ContextCompat.getColor(requireContext(),R.color.status_note_stroke) else if(data!!.status=="verified")  ContextCompat.getColor(requireContext(),R.color.status_verified_bg) else  ContextCompat.getColor(requireContext(),R.color.red_100))

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            TransactionDetailFragment().apply {
            }
    }
}