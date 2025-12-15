package app.perkhub.Screens.Offers.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.perkhub.R
import app.perkhub.Screens.Offers.model.OffersData
import app.perkhub.Screens.Profile.model.menuList
import app.perkhub.Utils.BaseRecyclerView
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.Utils.copyToClipboard
import app.perkhub.databinding.MenuListItemBinding
import app.perkhub.databinding.OfferListLayoutBinding
import com.bumptech.glide.Glide

class OffersListAdapter(private val clickList: RecyclerClickListener) :
    ListAdapter<OffersData, OffersListAdapter.Holder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<OffersData>() {
        override fun areItemsTheSame(oldItem: OffersData, newItem: OffersData): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: OffersData, newItem: OffersData): Boolean =
            oldItem == newItem
    }

    inner class Holder(val binding: OfferListLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OffersData) {
            binding.storename.text = item.store_name
            binding.title.text = item.title
            binding.dis.text = Html.fromHtml(item.description, Html.FROM_HTML_MODE_LEGACY)

            if (item.type == "coupon") {
                binding.couponlayout.visibility = View.VISIBLE
                binding.couponCode.text = item.coupon_code
            } else {
                binding.couponlayout.visibility = View.GONE
            }

            binding.copy.setOnClickListener {
                copyToClipboard(itemView.context, item.coupon_code, binding.couponlayout)
            }

            Glide.with(itemView.context)
                .load(item.image)
                .circleCrop()
                .placeholder(R.color.offers_bg1) // optional
                .into(binding.icon)

            binding.shop.setOnClickListener {
                clickList.RecyclerClickListener(adapterPosition, item.store_id.toString(), item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = OfferListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position)) // This is allowed inside the adapter
    }
}