package app.perkhub.Screens.Games.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import app.perkhub.Screens.Games.model.Data
import app.perkhub.Utils.BaseRecyclerView
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.databinding.GameSearchLayoutBinding
import com.bumptech.glide.Glide
import java.util.Locale

// 1. Implement Filterable
class SearchGameAdapter<S>(val clickListener: RecyclerClickListener):
    BaseRecyclerView<S,SearchGameAdapter<S>.Holder>(), Filterable {

    // 2. Add a publicly accessible list to store all games (unfiltered)
    // The Fragment will set this list manually.
    var originalList: List<S> = emptyList()

    // New method for the Fragment to check the current filtered size
    fun getFilteredListSize(): Int {
        return list.size
    }

    inner class Holder(val binding: GameSearchLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun setData(s:S){
            if (s is Data){
                binding.tvGameName.text = s.name
                binding.tvGameDis.text = s.description
                binding.tvPlays.text = "${formatPlays(s.gamePlays)} plays"
                Glide.with(itemView.context).load(s.assets.cover).into(binding.imgCover)
                binding.playbtn.setOnClickListener {
                    clickListener.RecyclerClickListener(0,"",s)
                }
            }
        }
    }

    fun formatPlays(count: String): String {
        val n = count.toLongOrNull() ?: 0
        return when {
            n >= 1_000_000 -> "${n / 1_000_000}M"
            n >= 1_000 -> "${n / 1_000}K"
            else -> "$n"
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(GameSearchLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        return holder.setData(list[position])
    }

    // 3. Implement getFilter() for searching
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString().toLowerCase(Locale.ROOT)
                val filteredList: List<S> = if (charSearch.isEmpty()) {
                    originalList // If search is empty, show the full list
                } else {
                    originalList.filter { item ->
                        // Cast to Data to access properties for filtering
                        if (item is Data) {
                            val query = charSearch
                            val name = item.name.toLowerCase(Locale.ROOT)
                           // val description = item.description?.toLowerCase(Locale.ROOT) ?: ""

                            // Search logic: check name, category, or description
                            name.contains(query) //|| description.contains(query)
                        } else {
                            false // Can't filter if not Data
                        }
                    }
                }

                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                // Clear the base class's list (which holds the currently displayed items)
                list.clear()
                // Add the filtered results to the base class's list
                list.addAll(results?.values as? List<S> ?: emptyList())
                notifyDataSetChanged()
            }
        }
    }

}