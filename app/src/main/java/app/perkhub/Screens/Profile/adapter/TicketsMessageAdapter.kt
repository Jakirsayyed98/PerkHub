package app.perkhub.Screens.Profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.perkhub.Screens.Profile.model.TicketMessage
import app.perkhub.Utils.BaseRecyclerView
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.databinding.ItemMessageUserBinding
import app.perkhub.databinding.ItemMessageAdminBinding

class TicketsMessageAdapter<S>(val clickList: RecyclerClickListener) :
    BaseRecyclerView<S, TicketsMessageAdapter<S>.Holder>() {

    // Base holder for type compatibility
    open inner class Holder(bindingRoot: androidx.viewbinding.ViewBinding) :
        RecyclerView.ViewHolder(bindingRoot.root) {
        open fun setdata(s: S) {}
    }

    // Holder for user messages
    inner class HolderUser(private val binding: ItemMessageUserBinding) : Holder(binding) {
        override fun setdata(s: S) {
            if (s is TicketMessage) {
                binding.tvMessageBody.text = s.body
                binding.tvMessageTime.text = s.getFormattedTime()
                binding.root.setOnClickListener {
                    clickList.RecyclerClickListener(0, "", s)
                }
            }
        }
    }

    // Holder for admin messages
    inner class HolderAdmin(private val binding: ItemMessageAdminBinding) : Holder(binding) {
        override fun setdata(s: S) {
            if (s is TicketMessage) {
                binding.tvMessageBody.text = s.body
                binding.tvMessageTime.text = s.getFormattedTime()
                binding.root.setOnClickListener {
                    clickList.RecyclerClickListener(0, "", s)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = list[position]
        return if (item is TicketMessage && item.author_type == "user") 1 else 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            1 -> HolderUser(ItemMessageUserBinding.inflate(inflater, parent, false))
            else -> HolderAdmin(ItemMessageAdminBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.setdata(list[position])
    }

    override fun getItemCount(): Int = list.size
}
