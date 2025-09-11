package app.perkhub.Screens.Home.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import app.perkhub.R
import app.perkhub.Utils.RecyclerClickListener
import com.bumptech.glide.Glide

class BannerViewPagerAdapter(
    private val sliderItems: List<SliderModelMain>,
    private val viewPager: ViewPager2,
    private val clickListener: RecyclerClickListener
) : RecyclerView.Adapter<BannerViewPagerAdapter.SliderViewHolder>() {

    class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sliderImage: ImageView = itemView.findViewById(R.id.bannerItem)

        fun bind(item: SliderModelMain, clickListener: RecyclerClickListener, position: Int) {
            Glide.with(itemView.context)
                .load(item.image)
                .centerCrop() // ensures image fills properly
//                .placeholder(R.drawable.placeholder_banner) // add fallback
//                .error(R.drawable.placeholder_banner)
                .into(sliderImage)

            sliderImage.setOnClickListener {
                clickListener.RecyclerClickListener(position, item.url ?: "", item.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.banners_layout, parent, false)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.bind(sliderItems[position], clickListener, position)
    }

    override fun getItemCount(): Int = sliderItems.size
}
