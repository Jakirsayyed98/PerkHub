// 10. CORRECTED Fragment Implementation
package app.perkhub.Screens.Notifications

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
import app.perkhub.Utils.Snackbar
import app.perkhub.Utils.openCustomTab
import app.perkhub.api.ApiListener
import app.perkhub.databinding.FragmentNotificationListBinding
import app.perkhub.room.NotificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationListFragment : BaseFragment<FragmentNotificationListBinding>() {

    private val notificationAdapter by lazy {
        NotificationAdapter<Data>(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                if (data.toString().startsWith("http")) {
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
        binding.toolbar.name.text = "Notification List"
        binding.toolbar.backbtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        setupRecyclerView()

        // Start observing notifications
        observeNotifications()

        return binding.root
    }


    private fun setupRecyclerView() {
        binding.notificationlist.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = notificationAdapter
        }
    }

    private fun fetchNotificationList(forceRefresh: Boolean = false) {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getNotificationList(
                requireContext(),
                forceRefresh,
                object : ApiListener<NotificationListRes> {
                    override fun onSuccess(t: NotificationListRes?, mess: String) {
                        t?.data?.let {
                            lifecycleScope.launch(Dispatchers.Main) {
                                // Don't call setData here if using Flow
                                // The Flow will handle it automatically
                            }
                        }
                    }

                    override fun onError(mess: String) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            requireContext().Snackbar("Failed to fetch notifications: $mess",_binding!!.mainlayout)
                        }
                    }
                })
        }
    }

    private fun observeNotifications() {
        val repository = NotificationRepository(requireContext())

        // CRITICAL FIX: Use viewLifecycleOwner instead of lifecycleScope
        // This prevents memory leaks when Fragment is destroyed
        viewLifecycleOwner.lifecycleScope.launch {
            repository.getNotificationsFlow().collect { notifications ->
                // This will be called automatically when:
                // 1. Fragment opens (shows cached data)
                // 2. Server data is fetched (shows fresh data)
                // 3. Push notification arrives (shows new notification)
                setData(notifications)
            }
        }

        // Trigger refresh from server in background
        fetchNotificationList(forceRefresh = true)
    }

    private fun setData(data: List<Data>) {
        // Show/hide empty state if needed
        if (data.isEmpty()) {
            binding.notificationlist.visibility = View.GONE
             binding.noDataFound.visibility = View.VISIBLE
        } else {
            binding.notificationlist.visibility = View.VISIBLE
             binding.noDataFound.visibility = View.GONE
            notificationAdapter.clear()
            notificationAdapter.addAllItem(data)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = NotificationListFragment()
    }
}