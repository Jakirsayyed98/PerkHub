package app.perkhub.Screens.withdrawal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import app.perkhub.R
import app.perkhub.Utils.BaseFragment
import app.perkhub.databinding.FragmentAddPaymentMethodBinding
import app.perkhub.databinding.FragmentWithdrawalFlowBinding

class AddPaymentMethodFragment : BaseFragment<FragmentAddPaymentMethodBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddPaymentMethodBinding.inflate(layoutInflater,container,false)
        applySafeAreaInsets()
        _binding!!.toolbar.name.text = "Add Payment Method"
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

    companion object {
        @JvmStatic
        fun newInstance() =
            AddPaymentMethodFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}