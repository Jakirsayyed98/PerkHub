package app.perkhub.Screens.Home.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.perkhub.ContainerActivity
import app.perkhub.Screens.Home.Model.Home.Category
import app.perkhub.Screens.Home.Model.Home.MiniApp
import app.perkhub.Utils.BaseRecyclerView
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.databinding.ActiveCategoryLayoutBinding

class CategoryActiveAdapter<S>(val clickListener: RecyclerClickListener) : BaseRecyclerView<S,CategoryActiveAdapter<S>.Holder>() {
    var context: Context? = null
    inner class Holder(val binding: ActiveCategoryLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        fun setData(s: S){
            if (s is Category){
                context = itemView.context
                binding.name.text = s.description
                if(s.miniapps.size>1){
                    setMiniApp(binding,s.miniapps)
                }

            }
        }
    }



    private fun setMiniApp(binding: ActiveCategoryLayoutBinding,data: List<MiniApp>) {
        data.shuffled()
        val mAdapter = TrendingStoresAdapter2<MiniApp>(object : RecyclerClickListener{
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {

                if (data is MiniApp){
                    ContainerActivity.OpenContainerType2(context,"StoreDetailFragment",data.name,data)
                }
            }
        })

        if (data.size<=3){
            binding.popularstore.apply {
                layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = mAdapter
            }
        }else{
            binding.popularstore.apply {
                layoutManager =
                    GridLayoutManager(context, 2, LinearLayoutManager.HORIZONTAL, false)
                adapter = mAdapter
            }
        }

//        data.shuffled()
        mAdapter.addAllItem(if (data.size <= 16) data else data.subList(0, 16))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ActiveCategoryLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        return holder.setData(list[position])
    }
}