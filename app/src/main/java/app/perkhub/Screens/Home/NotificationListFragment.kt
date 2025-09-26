package app.perkhub.Screens.Home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import app.perkhub.Screens.Home.Adapter.NotificationAdapter
import app.perkhub.Screens.Home.Model.Notification.Data
import app.perkhub.Screens.Home.Model.Notification.NotificationListRes
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.Utils.openCustomTab
import app.perkhub.Utils.toast
import app.perkhub.api.ApiListener
import app.perkhub.databinding.FragmentNotificationListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationListFragment : BaseFragment<FragmentNotificationListBinding>() {

    private val notificationAdapter by lazy {
        NotificationAdapter<Data>(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                if (data.toString().startsWith("http")){
                    requireContext().openCustomTab(data.toString())
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationListBinding.inflate(inflater, container, false)
        applySafeAreaInsets()
        binding.toolbar.name.text = "Notification List"
        binding.toolbar.backbtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        setupRecyclerView()
        fetchNotificationList()

        return binding.root
    }

    private fun applySafeAreaInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.toolbar2.updatePadding(top = systemBars.top)
            binding.mainlayout.updatePadding(bottom = systemBars.bottom)
            insets
        }
    }

    private fun setupRecyclerView() {
        binding.notificationlist.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = notificationAdapter
        }
    }

    private fun fetchNotificationList() {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getNotificationList(requireContext(), object : ApiListener<NotificationListRes> {
                override fun onSuccess(t: NotificationListRes?, mess: String) {
                    t?.data?.let { data ->
                        lifecycleScope.launch(Dispatchers.Main) {
                            setData(data)
                        }
                    }
                }

                override fun onError(mess: String) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        requireContext().toast("Failed to fetch notifications: $mess")
                    }
                }
            })
        }
    }

    private fun setData(data: List<Data>) {
        notificationAdapter.clear()
        notificationAdapter.addAllItem(data)
    }

    companion object {
        @JvmStatic
        fun newInstance() = NotificationListFragment()
    }
}
