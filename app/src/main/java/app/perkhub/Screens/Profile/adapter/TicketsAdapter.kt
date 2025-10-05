package app.perkhub.Screens.Profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import app.perkhub.R
import app.perkhub.Screens.Profile.model.Data
import app.perkhub.Utils.BaseRecyclerView
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.databinding.MenuListItemBinding
import app.perkhub.databinding.TicketsLayoutBinding
import com.bumptech.glide.Glide

class TicketsAdapter<S>(val clickList: RecyclerClickListener):BaseRecyclerView<S,TicketsAdapter<S>.Holder>() {

    inner class Holder(val binding: TicketsLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun setdata(s: S) {
            if (s is Data){

                binding.subject.text =itemView.context.getString(R.string.subject_test_subject,s.subject)
                binding.ticketId.text =itemView.context.getString(R.string.ticket_id_1234,s.id)
                binding.category.text =itemView.context.getString(R.string.category_shopping,s.category)
                binding.lastMessage.text =itemView.context.getString(R.string.last_message_shopping,s.last_message)
                binding.priority.text =itemView.context.getString(R.string.priority_medium,s.priority)
                binding.status.text =s.status

                if (s.status=="open"){
                    binding.status.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.status_pending_bg))
                    binding.card.setCardBackgroundColor(ContextCompat.getColor(itemView.context,R.color.status_pending_bg))
                }else if (s.status =="ongoing"){
                    binding.status.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.status_verified_bg))
                    binding.card.setCardBackgroundColor(ContextCompat.getColor(itemView.context,R.color.status_verified_bg))
                }else{
                    binding.status.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.rejected_icon_bg))
                    binding.card.setCardBackgroundColor(ContextCompat.getColor(itemView.context,R.color.rejected_icon_bg))
                }

                binding.root.setOnClickListener {
                    clickList.RecyclerClickListener(0,"",s)
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(TicketsLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        return holder.setdata(list[position])
    }


}