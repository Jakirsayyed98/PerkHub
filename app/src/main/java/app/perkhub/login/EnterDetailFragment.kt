package app.perkhub.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.perkhub.R
import app.perkhub.Utils.BaseFragment
import app.perkhub.databinding.FragmentEnterDetailBinding


class EnterDetailFragment : BaseFragment<FragmentEnterDetailBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEnterDetailBinding.inflate(layoutInflater,container,false)

        return _binding?.root
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            EnterDetailFragment().apply {

            }
    }
}