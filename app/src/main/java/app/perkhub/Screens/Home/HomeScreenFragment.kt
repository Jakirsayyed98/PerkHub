package app.perkhub.Screens.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.perkhub.R
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.USERDATA
import app.perkhub.databinding.FragmentHomeScreenBinding


class HomeScreenFragment : BaseFragment<FragmentHomeScreenBinding>() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =FragmentHomeScreenBinding.inflate(layoutInflater,container,false)
        _binding!!.title.text=getSharedpref().getUser(USERDATA).data[0].name
        return _binding?.root
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            HomeScreenFragment().apply {

            }
    }
}