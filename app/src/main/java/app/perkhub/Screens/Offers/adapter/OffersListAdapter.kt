package app.perkhub.Screens.Offers.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.perkhub.R
import app.perkhub.Screens.Offers.model.OffersData
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.Utils.copyToClipboard
import app.perkhub.databinding.OfferListLayoutBinding
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import kotlin.math.abs


class OffersListAdapter(
    private val clickList: RecyclerClickListener
) : ListAdapter<OffersData, OffersListAdapter.Holder>(DiffCallback()) {


    // ---------------------------------------------------------
    // DIFF CALLBACK
    // ---------------------------------------------------------

    class DiffCallback : DiffUtil.ItemCallback<OffersData>() {

        override fun areItemsTheSame(
            oldItem: OffersData,
            newItem: OffersData
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: OffersData,
            newItem: OffersData
        ): Boolean {
            return oldItem == newItem
        }
    }


    // ---------------------------------------------------------
    // VIEW HOLDER
    // ---------------------------------------------------------

    inner class Holder(
        private val binding: OfferListLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {


        fun bind(item: OffersData) {

            val context = binding.root.context


            // =====================================================
            // STORE
            // =====================================================

            binding.storename.text = item.store_name

            binding.storeType.text =
                if (item.type.equals("coupon", ignoreCase = true)) {
                    "COUPON"
                } else {
                    "OFFER"
                }


            // =====================================================
            // TITLE
            // =====================================================

            binding.title.text = item.title


            // =====================================================
            // DESCRIPTION
            // =====================================================

            binding.dis.text =
                Html.fromHtml(
                    item.description ?: "",
                    Html.FROM_HTML_MODE_LEGACY
                )

            binding.dis.visibility =
                if (item.description.isNullOrBlank()) {
                    View.GONE
                } else {
                    View.VISIBLE
                }


            // =====================================================
            // STORE IMAGE
            // =====================================================

            Glide.with(context)
                .load(item.image)
                .circleCrop()
                .placeholder(R.color.offers_bg1)
                .error(R.color.offers_bg1)
                .into(binding.icon)


            // =====================================================
            // BADGE
            // =====================================================

            setupBadge(item)


            // =====================================================
            // COUPON
            // =====================================================

            val hasCoupon =
                item.type.equals("coupon", ignoreCase = true) &&
                        !item.coupon_code.isNullOrBlank()


            if (hasCoupon) {

                binding.couponlayout.visibility = View.VISIBLE

                binding.couponCode.text =
                    item.coupon_code.trim()

                binding.copy.visibility = View.VISIBLE

                binding.copyLabel.visibility = View.VISIBLE

                binding.copy.setOnClickListener {

                    copyToClipboard(
                        context,
                        item.coupon_code,
                        binding.couponlayout
                    )
                }

            } else {

                binding.couponlayout.visibility = View.GONE

                binding.copy.visibility = View.GONE

                binding.copyLabel.visibility = View.GONE

                binding.copy.setOnClickListener(null)
            }


            // =====================================================
            // DATE
            // =====================================================

            val daysLeft = getDaysLeft(item.end_date)


            binding.validTill.text =
                if (daysLeft < 0) {
                    "Expired on ${formatDate(item.end_date)}"
                } else {
                    "Valid till ${formatDate(item.end_date)}"
                }


            // =====================================================
            // URGENCY
            // =====================================================

            setupUrgency(daysLeft)


            // =====================================================
            // SHOP
            // =====================================================

            binding.shop.setOnClickListener {

//                val position = bindingAdapterPosition

                if (position != RecyclerView.NO_POSITION) {

                    clickList.RecyclerClickListener(
                        position,
                        item.store_id.toString(),
                        item
                    )
                }
            }
        }


        // ---------------------------------------------------------
        // BADGE
        // ---------------------------------------------------------

        private fun setupBadge(item: OffersData) {

            val hash = abs(item.id.hashCode())

            when {

                item.type.equals("coupon", ignoreCase = true) -> {

                    binding.trendingBadge.text = "COUPON"
                    binding.trendingBadge.visibility = View.VISIBLE
                }

                hash % 4 == 0 -> {

                    binding.trendingBadge.text = "HOT"
                    binding.trendingBadge.visibility = View.VISIBLE
                }

                hash % 4 == 1 -> {

                    binding.trendingBadge.text = "DEAL"
                    binding.trendingBadge.visibility = View.VISIBLE
                }

                hash % 4 == 2 -> {

                    binding.trendingBadge.text = "NEW"
                    binding.trendingBadge.visibility = View.VISIBLE
                }

                else -> {

                    binding.trendingBadge.visibility = View.GONE
                }
            }
        }


        // ---------------------------------------------------------
        // URGENCY
        // ---------------------------------------------------------

        private fun setupUrgency(daysLeft: Long) {

            val context = binding.root.context


            when {

                daysLeft < 0 -> {

                    binding.urgencyText.text = "EXPIRED"

                    binding.urgencyText.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.error
                        )
                    )

                    binding.urgencyText.background =
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.deal_expired_bg
                        )
                }


                daysLeft == 0L -> {

                    binding.urgencyText.text =
                        "ENDS TODAY"

                    binding.urgencyText.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.urgency_red
                        )
                    )

                    binding.urgencyText.background =
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.deal_urgency_bg
                        )
                }


                daysLeft <= 2L -> {

                    binding.urgencyText.text =
                        "ENDS IN ${daysLeft}D"

                    binding.urgencyText.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.urgency_red
                        )
                    )

                    binding.urgencyText.background =
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.deal_urgency_bg
                        )
                }


                daysLeft <= 7L -> {

                    binding.urgencyText.text =
                        "${daysLeft} DAYS LEFT"

                    binding.urgencyText.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.main
                        )
                    )

                    binding.urgencyText.background =
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.deal_soft_urgency_bg
                        )
                }


                else -> {

                    binding.urgencyText.text =
                        "${daysLeft} DAYS LEFT"

                    binding.urgencyText.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.main
                        )
                    )

                    binding.urgencyText.background =
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.deal_soft_urgency_bg
                        )
                }
            }
        }


        // ---------------------------------------------------------
        // GET DAYS LEFT
        // ---------------------------------------------------------

        private fun getDaysLeft(
            endDate: String?
        ): Long {

            if (endDate.isNullOrBlank()) {
                return 0L
            }

            return try {

                val sdf = SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss'Z'",
                    Locale.getDefault()
                )

                sdf.timeZone =
                    TimeZone.getTimeZone("UTC")


                val end = sdf.parse(endDate)

                if (end == null) {
                    0L
                } else {

                    val diff =
                        end.time - System.currentTimeMillis()

                    TimeUnit.MILLISECONDS.toDays(diff)
                }

            } catch (e: Exception) {

                0L
            }
        }


        // ---------------------------------------------------------
        // FORMAT DATE
        // ---------------------------------------------------------

        private fun formatDate(
            date: String?
        ): String {

            if (date.isNullOrBlank()) {
                return "-"
            }

            return try {

                val input =
                    SimpleDateFormat(
                        "yyyy-MM-dd'T'HH:mm:ss'Z'",
                        Locale.getDefault()
                    )

                input.timeZone =
                    TimeZone.getTimeZone("UTC")


                val output =
                    SimpleDateFormat(
                        "dd MMM yyyy",
                        Locale.getDefault()
                    )

                output.format(
                    input.parse(date)!!
                )

            } catch (e: Exception) {

                "-"
            }
        }
    }


    // ---------------------------------------------------------
    // CREATE HOLDER
    // ---------------------------------------------------------

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {

        val binding =
            OfferListLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return Holder(binding)
    }


    // ---------------------------------------------------------
    // BIND HOLDER
    // ---------------------------------------------------------

    override fun onBindViewHolder(
        holder: Holder,
        position: Int
    ) {

        holder.bind(
            getItem(position)
        )
    }
}