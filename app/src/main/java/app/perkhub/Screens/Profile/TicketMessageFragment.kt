package app.perkhub.Screens.Profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import app.perkhub.Screens.Home.Model.Notification.NotificationListRes
import app.perkhub.Screens.Profile.adapter.TicketsMessageAdapter
import app.perkhub.Screens.Profile.model.*
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.DATA
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.Utils.toast
import app.perkhub.api.ApiListener
import app.perkhub.databinding.FragmentTicketMessageBinding
import app.perkhub.room.NotificationRepository
import app.perkhub.room.Tickets.TicketMessageRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class TicketMessageFragment : BaseFragment<FragmentTicketMessageBinding>() {

    private var ticketId = ""
    private var ticketStatus = ""
    private lateinit var mAdapter: TicketsMessageAdapter<TicketMessage>
    private val messageList = mutableListOf<TicketMessage>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTicketMessageBinding.inflate(inflater, container, false)

        val jsonData = requireActivity().intent.getStringExtra(DATA)
        val data = Gson().fromJson(jsonData, Data::class.java)
        ticketId = data.id
        ticketStatus = data.status.lowercase(Locale.ROOT)

        setupToolbar(data)
        setupRecycler()
        setupSendButtonVisibility()
        setupSendButton()
        observeTicketsMessage(ticketId)

        return binding.root
    }

    private fun setupToolbar(data: Data) {
        binding.toolbar.name.text = "Ticket Messages"
        binding.toolbar.backbtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.ticketSubject.text = data.subject
        binding.ticketStatus.text = "Status: ${data.status.replaceFirstChar { it.uppercase() }}"
    }

    private fun setupRecycler() {
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.stackFromEnd = true // ✅ always start from bottom
        mAdapter = TicketsMessageAdapter(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {}
        })
        binding.recyclerMessages.apply {
            this.layoutManager = layoutManager
            adapter = mAdapter

            // ✅ scroll to bottom automatically when layout changes (like WhatsApp)
            addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
                if (bottom < oldBottom) {
                    scrollToBottom()
                }
            }
        }
    }

    /** Show/hide input box based on ticket status */
    private fun setupSendButtonVisibility() {
        binding.messageInputLayout.visibility =
            if (ticketStatus == "closed") View.GONE else View.VISIBLE
    }

    /** Setup Send Button click logic */
    private fun setupSendButton() {
        binding.btnSend.setOnClickListener {
            val messageText = binding.inputMessage.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessageInstantly(messageText)
                sendMessageToAdmin(ticketId, messageText)
                binding.inputMessage.text?.clear()
            }
        }
    }

    private fun fetchTicketMessageList(forceRefresh: Boolean = false) {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getAllTicketByMessage(
                ticketId,
                requireContext(),
                forceRefresh,
                object : ApiListener<TicketMessageRes> {
                    override fun onSuccess(t: TicketMessageRes?, mess: String) {
                        t?.data?.let {
                            lifecycleScope.launch(Dispatchers.Main) {
                                messageList.clear()
                                messageList.addAll(it.ticket_messages)
                                mAdapter.addAllItem(messageList)
                                scrollToBottom()
                            }
                        }
                    }

                    override fun onError(mess: String) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            requireContext().toast("Failed to fetch ticketMessage: $mess")
                        }
                    }
                })
        }
    }

    private fun observeTicketsMessage(ticketId: String) {
        val repository = TicketMessageRepository(requireContext())

        // CRITICAL FIX: Use viewLifecycleOwner instead of lifecycleScope
        // This prevents memory leaks when Fragment is destroyed
        viewLifecycleOwner.lifecycleScope.launch {
            repository.getMessages(ticketId).collect { notifications ->
                // This will be called automatically when:
                // 1. Fragment opens (shows cached data)
                // 2. Server data is fetched (shows fresh data)
                // 3. Push notification arrives (shows new notification)
                setData(notifications)
            }
        }

        // Trigger refresh from server in background
        fetchTicketMessageList(forceRefresh = true)
    }

    private fun setData(data: List<TicketMessage>) {
        messageList.clear()
        messageList.addAll(data)
        mAdapter.addAllItem(messageList)
        scrollToBottom()
    }


    /** Instantly show user message in UI */
    private fun sendMessageInstantly(messageText: String) {
        val tempMessage = TicketMessage(
            id = UUID.randomUUID().toString(),
            ticket_id = ticketId,
            author_type = "user",
            body = messageText,
            created_at = Date().toString()
        )
        messageList.add(tempMessage)
        mAdapter.addAllItem(messageList)
        scrollToBottom()
    }

    /** Send message to backend */
    private fun sendMessageToAdmin(ticketId: String, ticketMessage: String) {
        val requestData = RequestTicketMessage(ticketId, ticketMessage)
        viewModel.sendTicketMessage(requestData, requireContext(), object : ApiListener<TicketMessageResponse> {
            override fun onSuccess(t: TicketMessageResponse?, mess: String) {
//                callTicketMessageLayout(ticketId)
                messageList.clear()
                messageList.add(TicketMessage("",ticketId,"user",ticketMessage,""))
                mAdapter.addAllItem(messageList)
            }
        })
    }

    private fun scrollToBottom() {
        if (messageList.isNotEmpty()) {
            binding.recyclerMessages.post {
                binding.recyclerMessages.scrollToPosition(messageList.size - 1)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = TicketMessageFragment()
    }
}
