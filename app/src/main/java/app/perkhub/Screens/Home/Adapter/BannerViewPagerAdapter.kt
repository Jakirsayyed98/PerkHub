package app.perkhub.Screens.Home.Adapter

import app.perkhub.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import app.perkhub.Screens.Home.Model.Home.Banner1
import com.bumptech.glide.Glide


class BannerViewPagerAdapter(private val context: Context,banner : ArrayList<Banner1>) : PagerAdapter() {

    private var inflater: LayoutInflater? = null
    private val images = banner

    override fun isViewFromObject(view: View, `object`: Any):    Boolean {

        return view === `object`
    }

    override fun getCount(): Int {
        return images.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view =inflater!!.inflate(R.layout.banners_layout, null)
        val image = view!!.findViewById<ImageView>(R.id.bannerItem)
        Glide.with(context).load(images[position].image).into(image)

        val vp = container as ViewPager
        vp.addView(view, 0)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val vp = container as ViewPager
        val view = `object` as View
        vp.removeView(view)
    }
}