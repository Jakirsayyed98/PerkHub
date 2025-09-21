package app.perkhub.Screens.Home.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.perkhub.R
import app.perkhub.Screens.Home.Model.Home.Category
import app.perkhub.Utils.BaseRecyclerView
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.databinding.CategoryLayoutBinding
import app.perkhub.databinding.FragmentHomeScreenBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class CategoryAdapter<S>(val clickList: RecyclerClickListener):BaseRecyclerView<S,CategoryAdapter<S>.Holder>() {

    inner class Holder(val binding: CategoryLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun setdata(s: S) {
            if (s is Category){
                Glide.with(itemView.context).load(s.image) .placeholder(R.drawable.ic_placeholder)
                    .into(binding.image)
                binding.title.text = s.name
                binding.root.setOnClickListener {
                    clickList.RecyclerClickListener(0,"",s)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(CategoryLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        return holder.setdata(list[position])
    }


}