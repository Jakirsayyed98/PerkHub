package app.perkhub.Screens.withdrawal

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import app.perkhub.R
import app.perkhub.StatusDialogFragment
import app.perkhub.Utils.BaseFragment
import app.perkhub.databinding.FragmentWithdrawalConfirmationBinding

class WithdrawalConfirmationFragment : BaseFragment<FragmentWithdrawalConfirmationBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWithdrawalConfirmationBinding.inflate(layoutInflater,container,false)
        _binding!!.toolbar.name.text = "Withdraw Funds"
        applySafeAreaInsets()

        _binding!!.toolbar.backbtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher?.onBackPressed()
        }

        return _binding?.root
    }

    private fun applySafeAreaInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.toolbar2.updatePadding(top = systemBars.top)
            binding.mainlayout.updatePadding(bottom = systemBars.bottom)
            insets
        }
    }

    private fun ShowSuccessDialog(status: String) {
        val dialogFragment = StatusDialogFragment.newInstance(status)
        val fragmentManager: FragmentManager = childFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        dialogFragment.show(fragmentTransaction, "StatusDialogFragment")

        // Dismiss the dialog after 2 seconds
        try {
            Handler().postDelayed({
                if (dialogFragment.isAdded) {
                    dialogFragment.dismiss()
                    requireActivity().onBackPressedDispatcher?.onBackPressed()
                }
            }, 4000) // 2000 milliseconds = 2 seconds
        }catch (e :Exception){
            e.printStackTrace()
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            WithdrawalConfirmationFragment().apply {

            }
    }
}