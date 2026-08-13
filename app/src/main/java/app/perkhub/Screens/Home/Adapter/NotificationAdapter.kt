package app.perkhub.Screens.Home.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.perkhub.R
import app.perkhub.Screens.Home.Model.Notification.Data
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.databinding.NotificationHeaderLayoutBinding
import app.perkhub.databinding.NotificationLayoutBinding
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class NotificationAdapter(
    private val clickListener: RecyclerClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {

        private const val TYPE_HEADER = 0
        private const val TYPE_NOTIFICATION = 1
    }

    private sealed class NotificationRow {

        data class Header(
            val title: String
        ) : NotificationRow()

        data class NotificationItem(
            val data: Data
        ) : NotificationRow()
    }

    private val rows = mutableListOf<NotificationRow>()

    fun setNotifications(notifications: List<Data>) {

        rows.clear()

        val sortedNotifications = notifications.sortedByDescending {
            parseDate(it.created_at)?.time ?: 0L
        }

        var todayAdded = false
        var earlierAdded = false

        sortedNotifications.forEach { notification ->

            if (isToday(notification.created_at)) {

                if (!todayAdded) {
                    rows.add(
                        NotificationRow.Header("Today")
                    )
                    todayAdded = true
                }

                rows.add(
                    NotificationRow.NotificationItem(notification)
                )

            } else {

                if (!earlierAdded) {
                    rows.add(
                        NotificationRow.Header("Earlier")
                    )
                    earlierAdded = true
                }

                rows.add(
                    NotificationRow.NotificationItem(notification)
                )
            }
        }

        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {

        return when (rows[position]) {

            is NotificationRow.Header ->
                TYPE_HEADER

            is NotificationRow.NotificationItem ->
                TYPE_NOTIFICATION
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        return when (viewType) {

            TYPE_HEADER -> {

                val binding =
                    NotificationHeaderLayoutBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )

                HeaderViewHolder(binding)
            }

            else -> {

                val binding =
                    NotificationLayoutBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )

                NotificationViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {

        when (val item = rows[position]) {

            is NotificationRow.Header -> {

                (holder as HeaderViewHolder)
                    .setData(item.title)
            }

            is NotificationRow.NotificationItem -> {

                (holder as NotificationViewHolder)
                    .setData(item.data)
            }
        }
    }

    override fun getItemCount(): Int {
        return rows.size
    }

    // ----------------------------------------------------------------
    // HEADER
    // ----------------------------------------------------------------

    inner class HeaderViewHolder(
        private val binding: NotificationHeaderLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun setData(title: String) {

            binding.headerTitle.text = title
        }
    }

    // ----------------------------------------------------------------
    // NOTIFICATION
    // ----------------------------------------------------------------

    inner class NotificationViewHolder(
        private val binding: NotificationLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun setData(data: Data) {

            binding.title.text = data.title ?: ""
            binding.message.text = data.message ?: ""

            // --------------------------------------------------------
            // DATE + TIME
            // --------------------------------------------------------

            binding.date.text = formatDate(data.created_at)
            binding.time.text = formatTime(data.created_at)

            // --------------------------------------------------------
            // IMAGE
            // --------------------------------------------------------

            val imageUrl = data.image

            if (imageUrl.isNullOrBlank()) {

                Glide.with(itemView.context)
                    .load(R.drawable.perkhub_icon)
                    .circleCrop()
                    .into(binding.icon)

            } else {

                Glide.with(itemView.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.perkhub_icon)
                    .error(R.drawable.perkhub_icon)
                    .circleCrop()
                    .into(binding.icon)
            }

            // --------------------------------------------------------
            // READ / UNREAD DOT
            // --------------------------------------------------------
            //
            // If your Data model has a read/read_status/is_read field,
            // use it here.
            //
            // Currently green is used for all notifications to match
            // the screenshot's unread state.
            //

            binding.unreadDot.visibility = View.GONE

            // --------------------------------------------------------
            // CLICK
            // --------------------------------------------------------

            binding.root.setOnClickListener {

                val action = data.click_action

                if (!action.isNullOrBlank()) {

                    clickListener.RecyclerClickListener(
                        0,
                        "web",
                        action
                    )
                }
            }
        }
    }

    // ----------------------------------------------------------------
    // DATE HELPERS
    // ----------------------------------------------------------------

    private fun parseDate(value: String?): Date? {

        if (value.isNullOrBlank()) {
            return null
        }

        val formats = listOf(
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "dd-MM-yyyy HH:mm:ss",
            "dd/MM/yyyy HH:mm:ss"
        )

        for (format in formats) {

            try {

                val formatter = SimpleDateFormat(
                    format,
                    Locale.getDefault()
                )

                formatter.timeZone =
                    TimeZone.getDefault()

                val date = formatter.parse(value)

                if (date != null) {
                    return date
                }

            } catch (_: Exception) {
            }
        }

        return null
    }

    private fun isToday(value: String?): Boolean {

        val date = parseDate(value) ?: return false

        val today = Calendar.getInstance()

        val notificationDate = Calendar.getInstance()
        notificationDate.time = date

        return today.get(Calendar.YEAR) ==
                notificationDate.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) ==
                notificationDate.get(Calendar.DAY_OF_YEAR)
    }

    private fun formatDate(value: String?): String {

        val date = parseDate(value) ?: return ""

        return SimpleDateFormat(
            "dd MMM, yyyy",
            Locale.getDefault()
        ).format(date)
    }

    private fun formatTime(value: String?): String {

        val date = parseDate(value) ?: return ""

        return SimpleDateFormat(
            "hh:mm a",
            Locale.getDefault()
        ).format(date)
    }
}