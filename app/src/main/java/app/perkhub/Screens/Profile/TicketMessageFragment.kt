package app.perkhub.Screens.Profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import app.perkhub.Screens.Profile.adapter.TicketsMessageAdapter
import app.perkhub.Screens.Profile.model.*
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.DATA
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.api.ApiListener
import app.perkhub.databinding.FragmentTicketMessageBinding
import com.google.gson.Gson
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
        callTicketMessageLayout(ticketId)

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

    /** Fetch messages from API */
    private fun callTicketMessageLayout(ticketId: String) {
        viewModel.getAllTicketByMessage(ticketId, requireContext(), object : ApiListener<TicketMessageRes> {
            override fun onSuccess(t: TicketMessageRes?, mess: String) {
                t?.let {
                    if (it.status == 200) {
                        requireActivity().runOnUiThread {
                            messageList.clear()
                            messageList.addAll(it.data.ticket_messages)
                            mAdapter.addAllItem(messageList)
                            scrollToBottom()
                        }
                    }
                }
            }
        })
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
                callTicketMessageLayout(ticketId)
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
