package app.perkhub.Screens.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import app.perkhub.R
import app.perkhub.Screens.Home.Adapter.NotificationAdapter
import app.perkhub.Screens.Home.Model.Notification.Data
import app.perkhub.Screens.Home.Model.Notification.NotificationListRes
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.api.ApiListener
import app.perkhub.databinding.FragmentNotificationListBinding

class NotificationListFragment : BaseFragment<FragmentNotificationListBinding>() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNotificationListBinding.inflate(layoutInflater,container,false)

        _binding!!.toolbar.name.text = "Notification List"
        _binding!!.toolbar.backbtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        getNotificationList()
        return _binding?.root
    }

    private fun getNotificationList() {
        viewModel.getNotificationList(requireContext(),object : ApiListener<NotificationListRes>{
            override fun onSuccess(t: NotificationListRes?, mess: String) {
                t!!.let {
                    setData(it)
                }
            }
        })
    }

    private fun setData(it: NotificationListRes) {
        val mAdapter = NotificationAdapter<Data>(object : RecyclerClickListener{
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {

            }
        })

        _binding!!.notificationlist.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            adapter = mAdapter
        }
        mAdapter.addAllItem(it.data)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            NotificationListFragment().apply {

            }
    }
}