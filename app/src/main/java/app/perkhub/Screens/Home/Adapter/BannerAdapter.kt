package app.perkhub.Screens.Home.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.perkhub.Screens.Home.Model.Home.Banner
import app.perkhub.databinding.BannersLayoutBinding
import app.perkhub.databinding.ItemBannerBinding
import app.perkhub.databinding.LayoutBannerBinding
import com.bumptech.glide.Glide


class BannerAdapter(
    private val list: MutableList<Banner>,
    private val onClick: (Banner) -> Unit
) : RecyclerView.Adapter<BannerAdapter.ViewHolder>() {

    fun updateData(newList: List<Banner>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemBannerBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = ItemBannerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val banner = list[position]
        Log.e("LogBanner",banner.image.toString())
        Glide.with(holder.itemView)
            .load(banner.image)
            .centerCrop()
            .into(holder.binding.imgBanner)

        holder.binding.root.setOnClickListener {
            onClick(banner)
        }
    }
}