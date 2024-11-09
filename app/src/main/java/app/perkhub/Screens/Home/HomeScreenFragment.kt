package app.perkhub.Screens.Home

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.AppCompatButton
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import app.perkhub.ContainerActivity
import app.perkhub.R

import app.perkhub.Screens.Home.Adapter.BannerViewPagerAdapter
import app.perkhub.Screens.Home.Adapter.CategoryActiveAdapter
import app.perkhub.Screens.Home.Adapter.CategoryAdapter
import app.perkhub.Screens.Home.Adapter.LootProductAdapter
import app.perkhub.Screens.Home.Adapter.PopularGameAdapter
import app.perkhub.Screens.Home.Adapter.PopularStoresAdapter
import app.perkhub.Screens.Home.Adapter.SliderModelMain
import app.perkhub.Screens.Home.Adapter.TrendingStoresAdapter
import app.perkhub.Screens.Home.Model.Games.GamesRes
import app.perkhub.Screens.Home.Model.Home.Banner1
import app.perkhub.Screens.Home.Model.Home.Category
import app.perkhub.Screens.Home.Model.Home.CategoryX
import app.perkhub.Screens.Home.Model.Home.HomeRes
import app.perkhub.Screens.Home.Model.Home.MiniApp
import app.perkhub.Screens.Home.Model.LootProduct.Data
import app.perkhub.Screens.Home.Model.LootProduct.LootProductRes
import app.perkhub.Screens.Home.Model.MiniAppByCategory.MiniAppbyCategory
import app.perkhub.Utils.ABOUTUS
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.CONTACTUS
import app.perkhub.Utils.PRIVACYPOLICY
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.Utils.USERDATA
import app.perkhub.Utils.openCustomTab
import app.perkhub.Utils.rateUsApp
import app.perkhub.Utils.shareAppLink
import app.perkhub.Utils.toast
import app.perkhub.api.ApiListener
import app.perkhub.databinding.FragmentHomeScreenBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class HomeScreenFragment : BaseFragment<FragmentHomeScreenBinding>() {

    private val handler = Handler(Looper.getMainLooper())
    lateinit var toggle : ActionBarDrawerToggle

    private var backPressedTime: Long = 0
    private val doubleBackPressDuration = 3000L  // 2 seconds



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeScreenBinding.inflate(layoutInflater, container, false)

        menuset()

        _binding!!.progressbar.visibility = View.VISIBLE
        _binding!!.mainlayout.visibility = View.GONE
        clickEvenets()

        GlobalScope.launch {
            getHomeData()
        }
        GlobalScope.launch {
            getLootProduct()
        }

        GlobalScope.launch {
            getGamesData()
        }

        GlobalScope.launch {
            updateFirebaseToken()
        }

        return _binding?.root
    }


    private fun clickEvenets() {
        with(_binding!!){
            search.setOnClickListener {
                ContainerActivity.OpenContainerType2(requireContext(),"SelectedCategoryFragment","Search",null)
            }
            notification.setOnClickListener {
                ContainerActivity.OpenContainer(requireContext(),"NotificationListFragment",null)
            }
        }
    }

    private fun getHomeData() {
        viewModel.getHomeData(requireContext(), object : ApiListener<HomeRes> {
            override fun onSuccess(t: HomeRes?, mess: String) {
                if (t!!.status.equals(200)) {
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

                        if (it.category_list.size <1){
                            _binding!!.Activecategorylayout.visibility = View.GONE
                        }else{
                            _binding!!.Activecategorylayout.visibility = View.VISIBLE
                            setActivatecategoryLayout(it.category_list)
                        }

                    }
                }
            }

            override fun onError(mess: String) {
                super.onError(mess)

            }
        })
    }

    private fun setActivatecategoryLayout(categoryList: java.util.ArrayList<CategoryX>) {

        var mAdapter = CategoryActiveAdapter<CategoryX>(object : RecyclerClickListener{
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {

            }
        })

        _binding!!.ActiveCategoryList.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            adapter = mAdapter
        }

        mAdapter.addAllItem(categoryList)
    }

    private fun setMainCategoryLayout(categories: ArrayList<Category>) {
        val mAdapter = CategoryAdapter<Category>(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                if (data is Category){
                    ContainerActivity.OpenContainerType2(requireContext(),"SelectedCategoryFragment","CategoryMiniApp",data)
                }
            }

        })
        _binding!!.categories.apply {
            layoutManager = GridLayoutManager(requireContext(), 4)
            adapter = mAdapter
        }
        mAdapter.addAllItem(if (categories.size <= 8) categories else categories.subList(0, 8))

        _binding!!.AllCategories.setOnClickListener {
            ContainerActivity.OpenContainerType2(requireContext(),"SelectedCategoryFragment","Categorires",categories)
        }
    }

    private fun setPopularStore(popular: java.util.ArrayList<MiniApp>) {
        popular.shuffled()
        val mAdapter = PopularStoresAdapter<MiniApp>(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {

                if (data is MiniApp){
                    ContainerActivity.OpenContainerType2(requireContext(),"StoreDetailFragment",data.name,data)
                }

            }
        })
        _binding!!.popularstore.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = mAdapter
        }
        popular.shuffled()
        mAdapter.addAllItem(if (popular.size <= 12) popular else popular.subList(0, 12))
        _binding!!.allPopular.setOnClickListener {
            ContainerActivity.OpenContainerType2(requireContext(),"SelectedCategoryFragment","Popular",if (popular.size > 18) popular.subList(0, 18) else popular)
        }
    }

    private fun setTrendingStores(trending: java.util.ArrayList<MiniApp>) {
        trending.shuffled()
        Log.d("PopularCountTre",trending.size.toString())
        var mAdapter = TrendingStoresAdapter<MiniApp>(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                if (data is MiniApp){
                    ContainerActivity.OpenContainerType2(requireContext(),"StoreDetailFragment",data.name,data)
                }

            }
        })

        _binding!!.trendingstore.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, LinearLayoutManager.HORIZONTAL, false)
            adapter = mAdapter
        }
        mAdapter.addAllItem(if (trending.size > 16) trending.subList(0, 16) else trending)
        _binding!!.allTrending.setOnClickListener {
            ContainerActivity.OpenContainerType2(requireContext(),"SelectedCategoryFragment","Trending",if (trending.size > 18) trending.subList(0, 18) else trending)
        }
    }


    private fun Banner1Func(banners: java.util.ArrayList<Banner1>) {

        if (banners.isNullOrEmpty()) {
            _binding!!.banner1layout.visibility = View.GONE

        } else {
            _binding!!.banner1layout.visibility = View.VISIBLE

        }

        val imageList = java.util.ArrayList<SliderModelMain>()
        //   bannerlist4 = bannerList4

        for (x in banners) {
            if (x.image.isNullOrEmpty().not()){
                imageList.add(SliderModelMain(x.image,"",x.url,x.id))
            }
        }


        _binding!!.bannerKocMarket.adapter = BannerViewPagerAdapter(imageList, _binding!!.bannerKocMarket, object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                requireContext().openCustomTab(type)
            }
        })

        _binding!!.bannerKocMarket.clipToPadding = false
        _binding!!.bannerKocMarket.clipChildren = false
        _binding!!.bannerKocMarket.offscreenPageLimit = 3
        _binding!!.bannerKocMarket.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        _binding!!.bannerKocMarket.setPadding(20, 0, 20, 0)

        //Handler Start

        Handler(Looper.getMainLooper()).apply {
            val runnable = object : Runnable {
                override fun run() {
                    kotlin.runCatching {
                        var i = _binding!!.bannerKocMarket.currentItem

                        if (i == banners.size - 1) {
                            i = -1//0
                            _binding!!.bannerKocMarket.currentItem = i
                        }
                        i++
                        _binding!!.bannerKocMarket.setCurrentItem(i, true)
                        postDelayed(this, 3000)
                    }
                }
            }
            postDelayed(runnable, 3000)
        }


        TabLayoutMediator(_binding!!.tabDots, _binding!!.bannerKocMarket,false) { _,_ -> }.attach()
    }



    fun getLootProduct() {
        viewModel.getLootProduct("1",requireContext(), object : ApiListener<LootProductRes> {
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


    private fun getGamesData() {
        viewModel.getAllGames(requireContext(),object : ApiListener<GamesRes>{
            override fun onSuccess(t: GamesRes?, mess: String) {
                if (t!!.errorCode == "0"){
                    setPopularGamesLayout(t!!.data)
                }
            }

            override fun onError(mess: String) {
                super.onError(mess)

            }
        })
    }

    private fun setPopularGamesLayout(data: java.util.ArrayList<app.perkhub.Screens.Home.Model.Games.Data>) {
        val mAdapter = PopularGameAdapter<app.perkhub.Screens.Home.Model.Games.Data>(object : RecyclerClickListener{
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                if (data is app.perkhub.Screens.Home.Model.Games.Data){
                    requireContext().openCustomTab(data.url)
                }
            }
        })
        _binding!!.populargames.apply {
            layoutManager = GridLayoutManager(requireContext(),3,GridLayoutManager.HORIZONTAL,false)
            adapter = mAdapter
        }
        mAdapter.addAllItem(if (data.size>24) data.subList(0,24) else data)
    }


    private fun updateFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            // Token ID :-  d8F1TQ_rQCqJ_kY9ELyd4M:APA91bH_yeiKZdAaVYSjnNhe3tDrzX4nwZal-FSzSD0d4uASMpq191G-m8ObnH9Tj8tNiw4wQ6dEQANIlSkKiffGU9KyOzeWndoGe4MAC_I-QmJsS0aBGsdO5BwqBTd-FU1mgtAljE8k
            // Get new FCM registration token
            val token = task.result
            viewModel.updateNotification(token,requireContext(), object : ApiListener<MiniAppbyCategory> {
                override fun onSuccess(t: MiniAppbyCategory?, mess: String) {

                }
            })
        })
    }


    private fun menuset() {

        toggle = ActionBarDrawerToggle(requireActivity(),_binding!!.drawerlayout, R.string.open,R.string.close)
        _binding!!.drawerlayout.addDrawerListener(toggle)
        toggle.syncState()
        val name = _binding!!.navView.getHeaderView(0).findViewById<TextView>(R.id.Profilename)
        val email = _binding!!.navView.getHeaderView(0).findViewById<TextView>(R.id.email)
//        val email = _binding!!.navView.findViewById<TextView>(R.id.email)
//        name.text = getSharedpref().getUser(USERDATA).data.name.toString()
//        email.text = getSharedpref().getUser(USERDATA).data.email.toString()
        _binding!!.profile.setOnClickListener {
            _binding!!.drawerlayout.open()
        }
        _binding!!.navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.home-> {
                    _binding!!.drawerlayout.close()
                }
                R.id.transation-> {
                    requireContext().toast("Transaction")
                    ContainerActivity.OpenContainer(requireContext(),"TransactionHistoryFragment","")

                }
                R.id.wallet->{
                    ContainerActivity.OpenContainer(requireContext(),"WalletFragment","")
                }
                R.id.share-> {
                    requireContext().shareAppLink()
                }

                R.id.rateus-> {
                    requireContext().rateUsApp()
                }
                R.id.PrivacyPolicy->{
                    requireContext().openCustomTab(PRIVACYPOLICY)
                }

                R.id.aboutus-> {
                    requireContext().openCustomTab(ABOUTUS)
                }
                R.id.contactus-> {
                    requireContext().openCustomTab(CONTACTUS)
                }
                R.id.LogOut->{
                    requireContext().OpenDialogForLogOut()
                }
            }
            true
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
        if (toggle.onOptionsItemSelected(item)){
            return  true
        }
    }

    fun Context.OpenDialogForLogOut(){

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.logout_dialog_layout)
        val cancel = dialog.findViewById<AppCompatButton>(R.id.cancel)
        val leave = dialog.findViewById<AppCompatButton>(R.id.leave)
        dialog.setCancelable(true)
        cancel.setOnClickListener {
            dialog.cancel()
            _binding!!.drawerlayout.close()
            requireContext().toast("Don't want to exit")
        }
        leave.setOnClickListener {
            dialog.cancel()
            _binding!!.drawerlayout.close()
            requireContext().toast("Exit Successfully")
        }

        dialog.show()
    }


    fun onBackPressed() {
        if (backPressedTime + doubleBackPressDuration > System.currentTimeMillis()) {
            activity?.finish()  // Exit the activity (and the app)
        } else {
            requireContext().toast("Press back again to exit")
        }
        backPressedTime = System.currentTimeMillis()
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            HomeScreenFragment().apply {

            }
    }
}