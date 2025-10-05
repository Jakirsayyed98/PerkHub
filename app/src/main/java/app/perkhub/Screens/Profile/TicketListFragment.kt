package app.perkhub.Screens.Profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import app.perkhub.ContainerActivity
import app.perkhub.R
import app.perkhub.Screens.Home.Model.Notification.NotificationListRes
import app.perkhub.Screens.Profile.adapter.TicketsAdapter
import app.perkhub.Screens.Profile.model.Data
import app.perkhub.Screens.Profile.model.TicketsRes
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.Utils.toast
import app.perkhub.api.ApiListener
import app.perkhub.databinding.FragmentTicketListBinding
import app.perkhub.room.NotificationRepository
import app.perkhub.room.Tickets.TicketsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TicketListFragment : BaseFragment<FragmentTicketListBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTicketListBinding.inflate(layoutInflater,container,false)
        applySafeAreaInsets()
        _binding!!.toolbar.name.text = "Customer Support"
        _binding!!.toolbar.backbtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        observeNotifications()

        _binding!!.newTicket.setOnClickListener {
            ContainerActivity.OpenContainer(requireContext(),"NewTicketFragment",null)
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

    private fun fetchTickets(forceRefresh: Boolean = false) {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getAllTickets(
                requireContext(),
                forceRefresh,
                object : ApiListener<TicketsRes> {
                    override fun onSuccess(t: TicketsRes?, mess: String) {
                        t?.data?.let {
                            lifecycleScope.launch(Dispatchers.Main) {
                                // Don't call setData here if using Flow
                                // The Flow will handle it automatically
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

    private fun observeNotifications() {
        val repository = TicketsRepository(requireContext())

        // CRITICAL FIX: Use viewLifecycleOwner instead of lifecycleScope
        // This prevents memory leaks when Fragment is destroyed
        viewLifecycleOwner.lifecycleScope.launch {
            repository.getTickets().collect { tickets ->
                // This will be called automatically when:
                // 1. Fragment opens (shows cached data)
                // 2. Server data is fetched (shows fresh data)
                // 3. Push notification arrives (shows new notification)
                setTicketsData(tickets)
            }
        }

        // Trigger refresh from server in background
        fetchTickets(forceRefresh = true)
    }

    private fun setTicketsData(tickets: List<Data>){
        val mAdapter = TicketsAdapter<Data>(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {

            }
        })

        _binding!!.tickets.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            adapter = mAdapter
        }

        mAdapter.addAllItem(tickets)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            TicketListFragment().apply {

            }
    }
}