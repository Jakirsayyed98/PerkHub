package app.perkhub.Screens.Profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import app.perkhub.Screens.Profile.adapter.TicketsMessageAdapter
import app.perkhub.Screens.Profile.model.*
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.DATA
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.Utils.toast
import app.perkhub.api.ApiListener
import app.perkhub.databinding.FragmentTicketMessageBinding
import app.perkhub.room.Tickets.TicketMessageRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class TicketMessageFragment : BaseFragment<FragmentTicketMessageBinding>() {

    private var ticketId = ""
    private var ticketStatus = ""
    private lateinit var mAdapter: TicketsMessageAdapter<TicketMessage>
    private val messageList = mutableListOf<TicketMessage>()
    private var observerJob: Job? = null
    private val pendingMessageIds = mutableSetOf<String>()

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
        val layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
        }

        mAdapter = TicketsMessageAdapter(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {}
        })

        binding.recyclerMessages.apply {
            this.layoutManager = layoutManager
            adapter = mAdapter

            // Scroll down when keyboard appears
            addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
                if (bottom < oldBottom) scrollToBottom()
            }
        }
    }

    private fun setupSendButtonVisibility() {
        binding.messageInputLayout.visibility =
            if (ticketStatus == "closed") View.GONE else View.VISIBLE
    }

    private fun setupSendButton() {
        binding.btnSend.setOnClickListener {
            val messageText = binding.inputMessage.text.toString().trim()
            if (messageText.isNotEmpty()) {
                val tempId = UUID.randomUUID().toString()
                sendMessageInstantly(messageText, tempId)
                sendMessageToAdmin(ticketId, messageText, tempId)
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
                                setData(it.ticket_messages)
                            }
                        }
                    }

                    override fun onError(mess: String) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            requireContext().toast("Failed to fetch messages: $mess")
                        }
                    }
                })
        }
    }

    private fun observeTicketsMessage(ticketId: String) {
        val repository = TicketMessageRepository(requireContext())

        // Cancel old observer safely
        observerJob?.cancel()

        observerJob = viewLifecycleOwner.lifecycleScope.launch {
            repository.getMessages(ticketId).collect { dbMessages ->
                withContext(Dispatchers.Main) {
                    setData(dbMessages)
                }
            }
        }

        // Refresh from server
        fetchTicketMessageList(forceRefresh = true)
    }

    private fun setData(data: List<TicketMessage>) {
        val previousSize = messageList.size
        messageList.clear()
        messageList.addAll(data)

        mAdapter.addAllItem(messageList)

        Log.d("TicketMessageList", "Loaded ${messageList.size} messages")

        // Scroll only if new messages arrived or first load
        if (messageList.size > previousSize || previousSize == 0) {
            scrollToBottom()
        }
    }

    private fun sendMessageInstantly(messageText: String, tempId: String) {
        val tempMessage = TicketMessage(
            id = tempId,
            ticket_id = ticketId,
            author_type = "user",
            body = messageText,
            created_at = Date().toString()
        )

        pendingMessageIds.add(tempId)
        messageList.add(tempMessage)
        mAdapter.addAllItem(messageList)
        scrollToBottom()
    }

    private fun sendMessageToAdmin(ticketId: String, ticketMessage: String, tempId: String) {
        val requestData = RequestTicketMessage(ticketId, ticketMessage)

        viewModel.sendTicketMessage(
            requestData,
            requireContext(),
            object : ApiListener<TicketMessageResponse> {
                override fun onSuccess(t: TicketMessageResponse?, mess: String) {
                    pendingMessageIds.remove(tempId)
                    messageList.removeAll { it.id == tempId }
                    fetchTicketMessageList(forceRefresh = true)
                }

                override fun onError(mess: String) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        pendingMessageIds.remove(tempId)
                        messageList.removeAll { it.id == tempId }
                        mAdapter.addAllItem(messageList)
                        requireContext().toast("Failed to send message: $mess")
                    }
                }
            })
    }

    /**
     * Safe scroll to bottom — prevents Invalid target position crash
     */
    private fun scrollToBottom() {
        binding.recyclerMessages.post {
            val itemCount = mAdapter.itemCount
            if (itemCount > 0 && binding.recyclerMessages.isAttachedToWindow) {
                try {
                    // Instant scroll (safer and smoother for chat-like UI)
                    binding.recyclerMessages.scrollToPosition(itemCount - 1)
                } catch (e: Exception) {
                    Log.e("TicketMessageFragment", "Safe scroll failed: ${e.message}")
                }
            }
        }
    }

    override fun onDestroyView() {
        observerJob?.cancel()
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance() = TicketMessageFragment()
    }
}
