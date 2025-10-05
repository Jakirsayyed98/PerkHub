package app.perkhub.Screens.Profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import app.perkhub.R
import app.perkhub.Utils.BaseFragment
import app.perkhub.databinding.FragmentNewTicketBinding


class NewTicketFragment : BaseFragment<FragmentNewTicketBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewTicketBinding.inflate(layoutInflater,container,false)
        applySafeAreaInsets()
        _binding!!.toolbar.name.text = "Create New Ticket"

        val items = arrayOf("Cashback Issues", "Withdrawal Issues", "Account Problems", "Games", "Store Issues","Technical Support","Other")

        val adapter = ArrayAdapter(
            requireContext(), // or requireContext() if inside a Fragment
            android.R.layout.simple_spinner_item,
            items
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        _binding!!.category.adapter =adapter

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
            NewTicketFragment().apply {

            }
    }
}