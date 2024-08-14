package app.perkhub.Screens.Home

import android.R
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import app.perkhub.ContainerActivity
import app.perkhub.Screens.Home.Adapter.BannerViewPagerAdapter
import app.perkhub.Screens.Home.Adapter.CategoryAdapter
import app.perkhub.Screens.Home.Adapter.LootProductAdapter
import app.perkhub.Screens.Home.Adapter.PopularStoresAdapter
import app.perkhub.Screens.Home.Adapter.TrendingStoresAdapter
import app.perkhub.Screens.Home.Model.Home.Banner1
import app.perkhub.Screens.Home.Model.Home.Category
import app.perkhub.Screens.Home.Model.Home.HomeRes
import app.perkhub.Screens.Home.Model.Home.MiniApp
import app.perkhub.Screens.Home.Model.LootProduct.Data
import app.perkhub.Screens.Home.Model.LootProduct.LootProductRes
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.Utils.openCustomTab
import app.perkhub.Utils.toast
import app.perkhub.api.ApiListener
import app.perkhub.databinding.FragmentHomeScreenBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async


class HomeScreenFragment : BaseFragment<FragmentHomeScreenBinding>() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeScreenBinding.inflate(layoutInflater, container, false)
        _binding!!.progressbar.visibility = View.VISIBLE
        _binding!!.mainlayout.visibility = View.GONE
        clickEvenets()

        GlobalScope.async {
            getHomeData()
        }
        GlobalScope.async {
            getLootProduct()
        }
        return _binding?.root
    }

    private fun clickEvenets() {
        with(_binding!!){
            search.setOnClickListener {
                ContainerActivity.OpenContainer(requireContext(),"SelectedCategoryFragment",null)
            }
        }
    }

    private fun getHomeData() {
        viewModel.getHomeData(getUserID()!!, object : ApiListener<HomeRes> {
            override fun onSuccess(t: HomeRes?, mess: String) {
                if (t!!.errorCode.equals("0")) {
                    t.data.get(0).let {
                        _binding!!.progressbar.visibility = View.GONE
                        _binding!!.mainlayout.visibility = View.VISIBLE
                        setMainCategoryLayout(it.categories)

                        if (it.popular.size < 1) {
                            _binding!!.popularlayout.visibility = View.GONE
                        } else {
                            _binding!!.popularlayout.visibility = View.VISIBLE
                            setPopularStore(it.popular)
                        }

                        if (it.trending.size < 1) {
                            _binding!!.trendingStorelayout.visibility = View.GONE
                        } else {
                            _binding!!.trendingStorelayout.visibility = View.VISIBLE
                            setTrendingStores(it.trending)
                        }

                        if (it.banner1.size < 1) {
                            _binding!!.banner1layout.visibility = View.GONE
                        } else {
                            _binding!!.banner1layout.visibility = View.VISIBLE
                            Banner1Func(it.banner1)
                        }

                    }
                }
            }

            override fun onError(mess: String) {
                super.onError(mess)

            }
        })
    }

    private fun setMainCategoryLayout(categories: ArrayList<Category>) {
        val mAdapter = CategoryAdapter<Category>(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                if (data is Category){
                    ContainerActivity.OpenContainer(requireContext(),"SelectedCategoryFragment",data)
                }
            }

        })
        _binding!!.categories.apply {
            layoutManager = GridLayoutManager(requireContext(), 4)
            adapter = mAdapter
        }
        mAdapter.addAllItem(if (categories.size <= 8) categories else categories.subList(0, 8))
    }

    private fun setPopularStore(popular: java.util.ArrayList<MiniApp>) {
        val mAdapter = PopularStoresAdapter<MiniApp>(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {

               requireContext().openCustomTab(type)
            }
        })
        _binding!!.popularstore.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = mAdapter
        }
        popular.shuffled()
        mAdapter.addAllItem(if (popular.size <= 12) popular else popular.subList(0, 12))
    }

    private fun setTrendingStores(trending: java.util.ArrayList<MiniApp>) {
        trending.shuffled()
        var mAdapter = TrendingStoresAdapter<MiniApp>(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                requireContext().openCustomTab(type)
            }
        })

        _binding!!.trendingstore.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, LinearLayoutManager.HORIZONTAL, false)
            adapter = mAdapter
        }
        mAdapter.addAllItem(if (trending.size > 20) trending.subList(0, 20) else trending)
    }

    private fun Banner1Func(banner: ArrayList<Banner1>) {

        val adapter = BannerViewPagerAdapter(requireContext(), banner)

        _binding!!.bannerKocMarket.adapter = adapter
        _binding!!.tabDots.setupWithViewPager(_binding!!.bannerKocMarket, true)

        _binding!!.bannerKocMarket.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                requireContext().toast(_binding!!.bannerKocMarket.currentItem.toString())
            }
        })


    }

    fun getLootProduct() {
        viewModel.getLootProduct(getUserID()!!, "1", object : ApiListener<LootProductRes> {
            override fun onSuccess(t: LootProductRes?, mess: String) {
                if (t!!.data.isNotEmpty()) {
                    setLootProductLayout(t.data)
                }
            }

            override fun onError(mess: String) {
                super.onError(mess)

            }
        })
    }

    private fun setLootProductLayout(data: List<Data>) {
        val mAdapter = LootProductAdapter<Data>(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                requireContext().openCustomTab(type)
            }
        })
        _binding!!.lootProduct.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = mAdapter
        }

        mAdapter.addAllItem(data)
    }


    companion object {

        @JvmStatic
        fun newInstance() =
            HomeScreenFragment().apply {

            }
    }
}