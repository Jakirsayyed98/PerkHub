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
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

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

            // -----------------------------
            // Date Handling
            // -----------------------------
            val daysLeft = getDaysLeft(item.end_date)

            binding.validTill.text =
                "Valid till ${formatDate(item.end_date)}"

            when {

                daysLeft < 0 -> {
                    binding.urgencyText.text = "Expired"
                    binding.urgencyText.setTextColor(
                        itemView.context.getColor(R.color.error)
                    )
                }

                daysLeft == 0L -> {
                    binding.urgencyText.text = "Ends Today"
                }

                else -> {
                    binding.urgencyText.text =
                        "⏰ $daysLeft days left"
                }
            }

            // -----------------------------
            // Fake Social Proof
            // -----------------------------
            val usedCount = generateUsageCount(item.id)

            binding.usageText.text =
                "$usedCount people used this today"


            binding.shop.setOnClickListener {
                clickList.RecyclerClickListener(adapterPosition, item.store_id.toString(), item)
            }
        }
        private fun getDaysLeft(endDate: String): Long {

            return try {

                val sdf = SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss'Z'",
                    Locale.getDefault()
                )

                sdf.timeZone = TimeZone.getTimeZone("UTC")

                val end = sdf.parse(endDate)
                val current = System.currentTimeMillis()

                val diff = end!!.time - current

                TimeUnit.MILLISECONDS.toDays(diff)

            } catch (e: Exception) {
                0
            }
        }

        // ---------------------------------
        // Format Date
        // Example:
        // 30 Jun 2026
        // ---------------------------------
        private fun formatDate(date: String): String {

            return try {

                val input = SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss'Z'",
                    Locale.getDefault()
                )

                input.timeZone = TimeZone.getTimeZone("UTC")

                val output = SimpleDateFormat(
                    "dd MMM yyyy",
                    Locale.getDefault()
                )

                output.format(input.parse(date)!!)

            } catch (e: Exception) {
                "-"
            }
        }

        // ---------------------------------
        // Fake Usage Count
        // ---------------------------------
        private fun generateUsageCount(id: String): String {

            return try {

                val hash = kotlin.math.abs(id.hashCode())

                when {

                    hash % 10 <= 3 -> {
                        "${500 + (hash % 500)}+"
                    }

                    hash % 10 <= 7 -> {
                        "${1000 + (hash % 3000)}+"
                    }

                    else -> {
                        "${5000 + (hash % 5000)}+"
                    }
                }

            } catch (e: Exception) {
                "1K+"
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