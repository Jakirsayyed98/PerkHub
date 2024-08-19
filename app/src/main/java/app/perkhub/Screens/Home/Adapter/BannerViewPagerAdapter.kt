package app.perkhub.Screens.Home.Adapter

import app.perkhub.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import app.perkhub.Screens.Home.Model.Home.Banner1
import app.perkhub.Utils.RecyclerClickListener
import com.bumptech.glide.Glide


class BannerViewPagerAdapter(sliderItem: MutableList<SliderModelMain>, viewPager: ViewPager2, var clickListener: RecyclerClickListener):
    RecyclerView.Adapter<BannerViewPagerAdapter.SliderViewHolder>(){
    lateinit var context: Context
    val SliderItemlist:List<SliderModelMain>
    val ViewPager2: ViewPager2


    init {
        this.SliderItemlist=sliderItem
        this.ViewPager2=viewPager
    }

    class SliderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val sliderItem: ImageView =itemView.findViewById(R.id.bannerItem)

        fun SliderData(SliderImageItem: SliderModelMain){
            kotlin.runCatching {
                Glide.with(itemView.context).load(SliderImageItem.image).centerCrop().into(sliderItem)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        return SliderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.banners_layout,parent,false))
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.SliderData(SliderItemlist[position])
        holder.sliderItem.setOnClickListener {
            clickListener.RecyclerClickListener(0, SliderItemlist[position].url.toString(), "AppCategory")
        }
    }
    override fun getItemCount(): Int {
        return SliderItemlist.size
    }
}