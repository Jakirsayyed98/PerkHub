package app.perkhub.Screens.Home.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.perkhub.R
import app.perkhub.Screens.Home.Model.Notification.Data
import app.perkhub.Utils.BaseRecyclerView
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.Utils.onlyDatewithMonth
import app.perkhub.databinding.NotificationLayoutBinding
import com.bumptech.glide.Glide

class NotificationAdapter<S>(val clickListener: RecyclerClickListener): BaseRecyclerView<S,NotificationAdapter<S>.Holder>() {

    inner class Holder(val binding:NotificationLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun setData(s: S) {
            if (s is Data) {
                binding.title.text = s.title
                binding.message.text = s.message
                binding.date.text = onlyDatewithMonth(s.created_at)

                // Load image or fallback to app icon
                val imageUrl = s.image
                if (imageUrl.isNullOrEmpty()) {
                    // Set default app icon
                    Glide.with(itemView.context)
                        .load(R.drawable.perkhub_icon) // replace with your default app icon
                        .circleCrop()
                        .into(binding.icon)
                } else {
                    Glide.with(itemView.context)
                        .load(imageUrl)
                        .circleCrop()
                        .into(binding.icon)
                }

                binding.root.setOnClickListener {
                    clickListener.RecyclerClickListener(0, "web", s.click_action)
                }
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(NotificationLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        return holder.setData(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }


}