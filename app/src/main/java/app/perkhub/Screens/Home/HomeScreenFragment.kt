package app.perkhub.Screens.Home

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import app.perkhub.ContainerActivity
import app.perkhub.R
import app.perkhub.Screens.Games.adapter.PopularGameAdapter
import app.perkhub.Screens.Games.model.Data
import app.perkhub.Screens.Games.model.GamesRes
import app.perkhub.Screens.Home.Adapter.*
import app.perkhub.Screens.Home.Model.CustomCategories
import app.perkhub.Screens.Home.Model.Home.*
import app.perkhub.Screens.Offers.model.Offerlistres
import app.perkhub.Screens.Offers.model.OffersData
import app.perkhub.Utils.*
import app.perkhub.api.ApiListener
import app.perkhub.databinding.FragmentHomeScreenBinding
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import app.perkhub.Screens.login.models.LoginRes
import app.perkhub.api.BaseRes
import app.perkhub.room.games.GamesRepository

class HomeScreenFragment : BaseFragment<FragmentHomeScreenBinding>() {

    private var backPressedTime: Long = 0
    private val doubleBackPressDuration = 3000L
    private var blockedDialog: Dialog? = null
    private var firebaseTaskActive = false

    val offers = ArrayList<OffersData>()
    val coupons = ArrayList<OffersData>()


    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var couponAdapter: CouponCodeAdapter<OffersData>
    private lateinit var offerAdapter: OffersAdapter<OffersData>

    private val bannerHandler = Handler(Looper.getMainLooper())

    private val bannerRunnable = object : Runnable {

        override fun run() {

            if (!::bannerAdapter.isInitialized)
                return

            val count = bannerAdapter.itemCount

            if (count <= 1) {
                bannerHandler.removeCallbacks(this)
                return
            }
            val currentBinding = _binding ?: return
            val next =
                if (currentBinding.bannerLayout.viewPagerBanner.currentItem == count - 1)
                    0
                else
                    currentBinding.bannerLayout.viewPagerBanner.currentItem + 1

            currentBinding.bannerLayout.viewPagerBanner.currentItem = next

            bannerHandler.postDelayed(this, 3000)
        }
    }

    private val pageCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            selectIndicator(position)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeScreenBinding.inflate(inflater, container, false)
        statusBarColor(R.color.main_bg)
        setStatusBarIconsDark(true)

        setupMenu()
        setupClickEvents()
        setBestCouponLayout()
        setBestOfferLayout()
//        observePopularGames()
        // Fetch data
        lifecycleScope.launch { fetchHomeData() }       // HomeViewModel
        lifecycleScope.launch { fetchOfferAndCoupons() }       // Coupns and Offers
        if (getSharedpref().getUser(USERDATA) == null) {
            getUserDetail()
        } else {
            val greet = getGreeting(getSharedpref().getUser(USERDATA)!!.data.name)
            binding.txtGreeting.text = greet
        }
        binding.bannerLayout.viewPagerBanner.registerOnPageChangeCallback(pageCallback)
        lifecycleScope.launch { updateFirebaseToken() } // requestViewModel
        (binding.root.parent as? ViewGroup)?.removeView(binding.root)
        setupSwipeRefresh()
        return binding.root
    }

    private fun setupSwipeRefresh() {
        showLoading(true)

        binding.swipeRefresh.setOnRefreshListener {
            lifecycleScope.launch { fetchHomeData() }       // HomeViewModel
            lifecycleScope.launch { fetchOfferAndCoupons() }       // Coupns and Offers
        }

        // Optional: customize refresh indicator colors
        binding.swipeRefresh.setColorSchemeResources(
            R.color.colorPrimary,
            R.color.main2
        )

        // Optional
        binding.swipeRefresh.setProgressViewOffset(
            false,
            0,
            resources.getDimensionPixelSize(R.dimen.dp60)
        )
    }


    private fun showLoading(show: Boolean) {
        binding.loadingView.visibility =
            if (show) View.VISIBLE else View.GONE
    }

    private fun observePopularGames() {
        val repository = GamesRepository(requireContext())

        // CRITICAL FIX: Use viewLifecycleOwner instead of lifecycleScope
        // This prevents memory leaks when Fragment is destroyed
        viewLifecycleOwner.lifecycleScope.launch {
            repository.getAllPopularGamesFlow().collect { games ->
                // This will be called automatically when:
                // 1. Fragment opens (shows cached data)
                // 2. Server data is fetched (shows fresh data)
                // 3. Push notification arrives (shows new notification)
            }
        }

        // Trigger refresh from server in background
        fetchGamesList(forceRefresh = true)
    }

    private fun getUserDetail() {
        viewModel.getUserDetail(requireContext(), object : ApiListener<LoginRes> {
            override fun onSuccess(t: LoginRes?, mess: String) {
                t!!.let {
                    val greet = getGreeting(it.data.name)
                    binding.txtGreeting.text = greet
                    getSharedpref().saveUser(USERDATA, it)
                    setupMenu()
                }
            }

            override fun onError(mess: String) {
                super.onError(mess)

            }
        })
    }


    override fun onDestroyView() {

        bannerHandler.removeCallbacks(bannerRunnable)

        binding.bannerLayout.viewPagerBanner.adapter = null
        binding.bannerLayout.viewPagerBanner.unregisterOnPageChangeCallback(pageCallback)
        _binding = null

        super.onDestroyView()
    }

    // ---------------- CLICK EVENTS ---------------- //
    private fun setupClickEvents() = with(binding) {
        layoutSearch.root.setOnClickListener {
            ContainerActivity.OpenContainerType2(
                requireContext(),
                "SelectedCategoryFragment",
                "Search",
                null
            )
        }
        layoutNotification.setOnClickListener {
            ContainerActivity.OpenContainer(requireContext(), "NotificationListFragment", null)
        }
    }

    // ---------------- API CALLS ---------------- //
    private fun fetchOfferAndCoupons() {
        viewModel.getOffersListHomePage(requireContext(), object : ApiListener<Offerlistres> {
            override fun onSuccess(t: Offerlistres?, mess: String) {
                lifecycleScope.launch(Dispatchers.Main) {

                    coupons.clear()
                    offers.clear()

                    t?.data?.forEach { item ->

                        when (item.type?.trim()?.lowercase()) {

                            "offer" -> offers.add(item)

                            "coupon" -> coupons.add(item)
                        }
                    }
                    coupons.random()
                    offers.random()
                    couponAdapter.addAllItem(if (coupons.size > 20) coupons.subList(0, 19) else coupons)
                    offerAdapter.addAllItem(if (offers.size > 20) offers.subList(0, 19) else offers)
                }
            }

            override fun onError(mess: String) {
                super.onError(mess)
                lifecycleScope.launch(Dispatchers.Main) {
                    requireContext().Snackbar("Home API error: $mess", _binding!!.root)
                }
            }
        })
    }

    // ---------------- API CALLS ---------------- //
    private suspend fun fetchHomeData() {
        viewModel.getHomeData(object : ApiListener<HomeRes> {
            override fun onSuccess(t: HomeRes?, mess: String) {
                t?.data?.let { data ->
                    lifecycleScope.launch(Dispatchers.Main) {

                        setMainCategoryLayout(data.categories)
                        setBanner1(data.banner1)
                        setTopCashbackProvider(data.topCashback)
                        setPopularStores(data.popular)
                        setTrendingStores(data.trending)
                        setTopCategores()
                        binding.swipeRefresh.isRefreshing = false
                        showLoading(false)

                    }
                }
            }

            override fun onError(mess: String) {
                lifecycleScope.launch(Dispatchers.Main) {
                    requireContext().Snackbar("Home API error: $mess", _binding!!.root)
                }
            }
        })
    }

    private fun setTopCashbackProvider(topCashback: List<MiniApp>) {
        binding.storeLayout.root.visibility = View.VISIBLE
        binding.storeLayout.title.text = "Top Cashback Providers"
        val mAdapter = TrendingStoresAdapter<MiniApp>(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                if (data is MiniApp) {
                    ContainerActivity.OpenContainerType2(
                        requireContext(),
                        "StoreDetailFragment",
                        data.name,
                        data
                    )
                }
            }
        })

        binding.storeLayout.stores.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = mAdapter
        }

        mAdapter.addAllItem(if (topCashback.size > 14) topCashback.subList(0, 14) else topCashback)
    }

    private fun setPopularStores(popular: List<MiniApp>) {
        binding.popularStoreLayout.root.visibility = View.VISIBLE
        binding.popularStoreLayout.title.text = "Popular Stores"
        val mAdapter = PopularStoresAdapter<MiniApp>(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                if (data is MiniApp) {
                    ContainerActivity.OpenContainerType2(
                        requireContext(),
                        "StoreDetailFragment",
                        data.name,
                        data
                    )
                }
            }
        })

        binding.popularStoreLayout.stores.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = mAdapter
        }

        mAdapter.addAllItem(if (popular.size > 14) popular.subList(0, 14) else popular)
    }

    private fun setTrendingStores(trending: List<MiniApp>) {
        binding.trendingStoreLayout.root.visibility = View.VISIBLE
        binding.trendingStoreLayout.title.text = "Trending now"
        val mAdapter = TrendingNowStoresAdapter<MiniApp>(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                if (data is MiniApp) {
                    ContainerActivity.OpenContainerType2(
                        requireContext(),
                        "StoreDetailFragment",
                        data.name,
                        data
                    )
                }
            }
        })

        binding.trendingStoreLayout.stores.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = mAdapter
        }

        mAdapter.addAllItem(if (trending.size > 14) trending.subList(0, 14) else trending)
    }

    private fun setBestCouponLayout() {
        binding.bestCouponLayout.root.visibility = View.VISIBLE
        binding.bestCouponLayout.title.text = "Today's Best Coupons"
        binding.bestCouponLayout.viewall.visibility = View.GONE
        couponAdapter = CouponCodeAdapter<OffersData>(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                if (data is OffersData) {
                    ContainerActivity.OpenContainerType2(
                        requireContext(),
                        "",
                        "",
                        data
                    )
                }
            }
        })

        binding.bestCouponLayout.stores.apply {
//            layoutManager = GridLayoutManager(requireContext(),3)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = couponAdapter
        }
    }


    private fun setBestOfferLayout() {
        binding.limitOfferLayout.root.visibility = View.VISIBLE
        binding.limitOfferLayout.title.text = "Limited Time Offer"
        binding.limitOfferLayout.viewall.visibility = View.GONE
        offerAdapter = OffersAdapter<OffersData>(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                if (data is OffersData) {
                    requireContext().openCustomTab(data.url)
                }
            }
        })

        binding.limitOfferLayout.stores.apply {
//            layoutManager = GridLayoutManager(requireContext(),3)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = offerAdapter
        }
    }


    private fun setTopCategores() {
        binding.topCategoriesLayout.root.visibility = View.VISIBLE
        binding.topCategoriesLayout.title.text = "Top Categories"
        binding.topCategoriesLayout.viewall.visibility = View.GONE
        val mAdapter = CustomCategoryAdapter<CustomCategories>(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                if (data is CustomCategories) {
                    if (data.click == "GamesFragment") {
                        ContainerActivity.OpenContainerType2(requireContext(), data.click, "", null)
                    } else if (data.click == "OffersFragment") {
                        ContainerActivity.OpenContainerType2(requireContext(), data.click, "", null)
                    } else {
                        ContainerActivity.OpenContainerType2(
                            requireContext(),
                            "SelectedCategoryFragment",
                            data.click,
                            null
                        )
                    }

                }
            }
        })

        binding.topCategoriesLayout.stores.apply {
//            layoutManager = GridLayoutManager(requireContext(),3)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = mAdapter
        }
        val topCategories = arrayListOf(
            CustomCategories(1, "Coupon Codes", R.drawable.coupon, "Couponcode"),
            CustomCategories(2, "Offers", R.drawable.offers_img, "OffersFragment"),
            CustomCategories(3, "Vouchers", R.drawable.voucher_img, "Vouchers"),
            CustomCategories(4, "Play Games", R.drawable.games_img, "GamesFragment")
        )

        mAdapter.addAllItem(topCategories)
    }


    private fun setBanner1(banners: List<Banner>?) {
        binding.bannerLayout.viewPagerBanner.visibility = View.VISIBLE
        if (banners.isNullOrEmpty()) return

        if (!::bannerAdapter.isInitialized) {

            bannerAdapter = BannerAdapter(
                banners.toMutableList()
            ) { banner ->

                if (banner.url.isNotEmpty()) {
                    // Open banner
                }
            }

            binding.bannerLayout.viewPagerBanner.adapter = bannerAdapter

        } else {

            bannerAdapter.updateData(banners)
        }

        createIndicator(banners.size)

        bannerHandler.removeCallbacks(bannerRunnable)

        if (banners.size > 1) {
            bannerHandler.postDelayed(bannerRunnable, 3000)
        }
    }

    private fun fetchGamesList(forceRefresh: Boolean = false) {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getPopularGames(
                requireContext(),
                forceRefresh,
                object : ApiListener<GamesRes> {
                    override fun onSuccess(t: GamesRes?, mess: String) {
                        t?.data?.let {
                            lifecycleScope.launch(Dispatchers.Main) {
                                // Don't call setData here if using Flow
                                // The Flow will handle it automatically
                            }
                        }
                    }

                    override fun onError(mess: String) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            requireContext().Snackbar(
                                "Failed to fetch notifications: $mess",
                                _binding!!.root
                            )
                        }
                    }
                })
        }
    }

    private fun updateFirebaseToken() {
        firebaseTaskActive = true
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!firebaseTaskActive) return@addOnCompleteListener
            if (!task.isSuccessful) return@addOnCompleteListener
            val token = task.result ?: return@addOnCompleteListener

            if (!isAdded || activity == null) return@addOnCompleteListener

            viewModel.updateNotification(token, requireContext(), object : ApiListener<BaseRes> {
                override fun onSuccess(t: BaseRes?, mess: String) {

                }

                override fun onError(mess: String) {}
            })
        }
    }

    // ---------------- RECYCLERS ---------------- //
    private fun setMainCategoryLayout(categories: List<Category>) {
        binding.layoutCateory.root.visibility = View.VISIBLE
        val adapter = CategoryAdapter<Category>(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                if (data is Category) ContainerActivity.OpenContainerType2(
                    requireContext(),
                    "SelectedCategoryFragment",
                    "CategoryMiniApp",
                    data
                )
            }
        })
        binding.layoutCateory.category.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            this.adapter = adapter
        }
        adapter.addAllItem(categories)
    }

    // ---------------- DRAWER MENU ---------------- //
    private fun setupMenu() {
        val userName = getSharedpref().getUser(USERDATA)?.data
        binding.imgMenu.setOnClickListener {
            ContainerActivity.OpenContainer(requireContext(), PROFILE_SCREEN_FRAGMENT, "")
//            binding.drawerlayout.open()
        }

        if (userName?.blocked == true) {
            requireActivity().runOnUiThread {
                requireContext().showBlockedUserDialog()
            }
        } else {
            hideBlockedDialog()
        }
    }

    // ---------------- BACK PRESS ---------------- //
    fun onBackPressed() {
        if (backPressedTime + doubleBackPressDuration > System.currentTimeMillis()) {
            activity?.finish()
        } else {
            requireContext().Snackbar("Press back again to exit", _binding!!.drawerLayout)
        }
        backPressedTime = System.currentTimeMillis()
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeScreenFragment()
    }


    private fun Context.showBlockedUserDialog() {
        blockedDialog = Dialog(this)
        blockedDialog!!.setContentView(R.layout.blocked_user_dialog_layout)
        blockedDialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        blockedDialog!!.setCancelable(false)
        blockedDialog!!.show()
    }

    private fun hideBlockedDialog() {
        if (blockedDialog?.isShowing == true) {
            blockedDialog?.dismiss()
        }
        blockedDialog = null
    }

    private fun createIndicator(count: Int) {

        val layout = binding.bannerLayout.layoutIndicator

        layout.removeAllViews()

        repeat(count) {

            val dot = View(requireContext())

            val params = LinearLayout.LayoutParams(

                resources.getDimensionPixelSize(R.dimen.dp6),

                resources.getDimensionPixelSize(R.dimen.dp6)

            )

            params.marginEnd = resources.getDimensionPixelSize(R.dimen.dp8)

            dot.layoutParams = params

            dot.setBackgroundResource(R.drawable.inactive_dot)

            layout.addView(dot)

        }

        selectIndicator(0)

    }

    private fun selectIndicator(position: Int) {

        val layout = binding.bannerLayout.layoutIndicator

        for (i in 0 until layout.childCount) {

            val dot = layout.getChildAt(i)

            val params = dot.layoutParams as LinearLayout.LayoutParams

            if (i == position) {

                params.width = resources.getDimensionPixelSize(R.dimen.dp18)

                params.height = resources.getDimensionPixelSize(R.dimen.dp6)

                dot.setBackgroundResource(R.drawable.active_dot)

            } else {

                params.width = resources.getDimensionPixelSize(R.dimen.dp6)

                params.height = resources.getDimensionPixelSize(R.dimen.dp6)

                dot.setBackgroundResource(R.drawable.inactive_dot)

            }

            dot.layoutParams = params
        }

    }

    override fun onPause() {
        super.onPause()
        bannerHandler.removeCallbacks(bannerRunnable)
    }

    override fun onResume() {
        super.onResume()
        if (::bannerAdapter.isInitialized && bannerAdapter.itemCount > 1) {
            bannerHandler.postDelayed(bannerRunnable, 3000)
        }
    }

}
