package app.perkhub.Screens.Home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import app.perkhub.ContainerActivity
import app.perkhub.Screens.Home.Adapter.CategoryAdapter
import app.perkhub.Screens.Home.Adapter.MiniAppCategoryAdapter
import app.perkhub.Screens.Home.Model.Home.Category
import app.perkhub.Screens.Home.Model.Home.MiniApp
import app.perkhub.Screens.Home.Model.MiniAppByCategory.MiniAppByCategoryReq
import app.perkhub.Screens.Home.Model.MiniAppByCategory.MiniAppSearchReq
import app.perkhub.Screens.Home.Model.MiniAppByCategory.MiniAppbyCategory
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.CONTAINER_TYPE_KEY1
import app.perkhub.Utils.DATA
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.Utils.openCustomTab
import app.perkhub.api.ApiListener
import app.perkhub.databinding.FragmentSelectedCategoryBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class SelectedCategoryFragment : BaseFragment<FragmentSelectedCategoryBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSelectedCategoryBinding.inflate(layoutInflater, container, false)
        _binding!!.toolbar.backbtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val type1 = requireActivity().intent.getStringExtra(CONTAINER_TYPE_KEY1)
        when(type1){
            "CategoryMiniApp"->{
                val value = requireActivity().intent.getStringExtra(DATA)
                val data = Gson().fromJson<Category>(value, Category::class.java)
                _binding!!.toolbar.name.text = data.name
                _binding!!.searchLayout.visibility = View.GONE
                getListOfMiniApps(data.id.toString())
            }
            "Categorires"->{
                _binding!!.toolbar.name.text = "All Categories"
                _binding!!.searchLayout.visibility = View.GONE
                val value = requireActivity().intent.getStringExtra(DATA)
                val gson = Gson()
                val categoryListType = object : TypeToken<ArrayList<Category?>?>() {}.type
                val categories: ArrayList<Category> = gson.fromJson(value, categoryListType)
                setMainCategoryLayout(categories)

            }
            "Popular"->{
                _binding!!.toolbar.name.text = "Popular Stores"
                _binding!!.searchLayout.visibility = View.GONE
                val value = requireActivity().intent.getStringExtra(DATA)
                val gson = Gson()
                val popularType = object : TypeToken<ArrayList<MiniApp?>?>() {}.type
                val Popular: ArrayList<MiniApp> = gson.fromJson(value, popularType)
                setLayout(Popular)
            }
            "Trending"->{
                _binding!!.toolbar.name.text = "Trending Stores"
                _binding!!.searchLayout.visibility = View.GONE
                val value = requireActivity().intent.getStringExtra(DATA)
                val gson = Gson()
                val TrendingType = object : TypeToken<ArrayList<MiniApp?>?>() {}.type
                val Trending: ArrayList<MiniApp> = gson.fromJson(value, TrendingType)
                setLayout(Trending)
            }

            "Search"->{
                _binding!!.searchLayout.visibility = View.VISIBLE
                _binding!!.toolbar.name.text = "Search"
            }

        }

        _binding!!.search.addTextChangedListener {
            SearchMiniApps(it.toString())
        }
        return _binding?.root
    }

    private fun setMainCategoryLayout(categories: ArrayList<Category>) {
        val mAdapter = CategoryAdapter<Category>(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                if (data is Category){
                    ContainerActivity.OpenContainerType2(requireContext(),"SelectedCategoryFragment","CategoryMiniApp",data)
                }
            }

        })
        _binding!!.listOfStores.apply {
            layoutManager = GridLayoutManager(requireContext(), 4)
            adapter = mAdapter
        }
        mAdapter.addAllItem(categories)

    }


    private fun SearchMiniApps(name: String) {
        val req = MiniAppSearchReq(name)
        viewModel.searchminiApps(
            req,
            requireContext(),
            object : ApiListener<MiniAppbyCategory> {
                override fun onSuccess(t: MiniAppbyCategory?, mess: String) {
                    t!!.let {
                        setLayout(it.data)
                    }
                }
            })

    }

    private fun getListOfMiniApps(id: String) {
        val req = MiniAppByCategoryReq(id)
        System.out.println(req)
        viewModel.getMiniAppByCategory(
            req,
            requireContext(),
            object : ApiListener<MiniAppbyCategory> {
                override fun onSuccess(t: MiniAppbyCategory?, mess: String) {
                    t!!.let {
                        setLayout(it.data)
                    }
                }
            })

    }

    private fun setLayout(data: List<MiniApp>) {
        var mAdapter = MiniAppCategoryAdapter<MiniApp>(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                if (data is MiniApp){
                    ContainerActivity.OpenContainerType2(requireContext(),"StoreDetailFragment",data.name,data)
                }

            }
        })
        _binding!!.listOfStores.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = mAdapter
        }
        mAdapter.clear()
        mAdapter.notifyDataSetChanged()
        mAdapter.addAllItem(data)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            SelectedCategoryFragment().apply {

            }
    }
}