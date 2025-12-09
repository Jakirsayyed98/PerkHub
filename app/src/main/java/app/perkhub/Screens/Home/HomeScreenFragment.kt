package app.perkhub.Screens.Home

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.perkhub.ContainerActivity
import app.perkhub.R
import app.perkhub.Screens.Games.model.GamesRes
import app.perkhub.Screens.Home.Adapter.*
import app.perkhub.Screens.Home.Model.Home.*
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

    private lateinit var toggle: ActionBarDrawerToggle
    private var backPressedTime: Long = 0
    private val doubleBackPressDuration = 3000L
    private val bannerHandler = Handler(Looper.getMainLooper())
    private var bannerRunnable: Runnable? = null
    private var blockedDialog: Dialog? = null

    private var banner1Runnable: Runnable? = null
    private var promoBannerRunnable: Runnable? = null
    private var exclusiveBannerRunnable: Runnable? = null
    private var firebaseTaskActive = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeScreenBinding.inflate(inflater, container, false)
        binding.progressbar.visibility = View.VISIBLE
        binding.mainlayout1.visibility = View.GONE
        statusBarColor(R.color.light_blue)
        setupMenu()
        setupClickEvents()
        observePopularGames()
        // Fetch data
        lifecycleScope.launch { fetchHomeData() }       // HomeViewModel
//        if (getSharedpref().getUser(USERDATA)==null){
            getUserDetail()
//        }

        lifecycleScope.launch { updateFirebaseToken() } // requestViewModel

        return binding.root
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
                setPopularGamesLayout(games)
            }
        }

        // Trigger refresh from server in background
        fetchGamesList(forceRefresh = true)
    }

    private fun getUserDetail() {
        viewModel.getUserDetail(requireContext(), object : ApiListener<LoginRes> {
            override fun onSuccess(t: LoginRes?, mess: String) {
                t!!.let{

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

        banner1Runnable?.let { bannerHandler.removeCallbacks(it) }
        promoBannerRunnable?.let { bannerHandler.removeCallbacks(it) }
        exclusiveBannerRunnable?.let { bannerHandler.removeCallbacks(it) }

        banner1Runnable = null
        promoBannerRunnable = null
        exclusiveBannerRunnable = null
        _binding = null
        super.onDestroyView()
    }

    // ---------------- CLICK EVENTS ---------------- //
    private fun setupClickEvents() = with(binding) {
        search.setOnClickListener {
            ContainerActivity.OpenContainerType2(requireContext(), "SelectedCategoryFragment", "Search", null)
        }
        notificationLayout.setOnClickListener {
            ContainerActivity.OpenContainer(requireContext(), "NotificationListFragment", null)
        }
    }

    // ---------------- API CALLS ---------------- //
    private suspend fun fetchHomeData() {
        viewModel.getHomeData(object : ApiListener<HomeRes> {
            override fun onSuccess(t: HomeRes?, mess: String) {
                t?.data?.let { data ->
                    lifecycleScope.launch(Dispatchers.Main) {
                        binding.progressbar.visibility = View.GONE
                        binding.mainlayout1.visibility = View.VISIBLE

                        setMainCategoryLayout(data.categories)
                        if (data.popular.isNotEmpty()) setPopularStore(data.popular)
                        setTrendingStores1(data.popular)
                        if (data.banner1.isNullOrEmpty()) binding.banner1layout.visibility = View.GONE
                        else Banner1Func(data.banner1)

                        if (data.banner2.isNullOrEmpty()) binding.promotionalBanner.visibility = View.GONE
                        else BannerPromotional(data.banner2)

                        if (data.banner3.isNullOrEmpty()) binding.exclusiveBanner.visibility = View.GONE
                        else BannerExclusive(data.banner3)


                        if (data.trending.isNotEmpty()) setTrendingStores(data.trending)
                        if (data.category_list.isNotEmpty()) setActiveHomeCategory(data.category_list)
                    }
                }
            }

            override fun onError(mess: String) {
                lifecycleScope.launch(Dispatchers.Main) {
                    requireContext().Snackbar("Home API error: $mess",_binding!!.mainlayout)
                }
            }
        })
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
                            requireContext().Snackbar("Failed to fetch notifications: $mess",_binding!!.mainlayout)
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
        val adapter = CategoryAdapter<Category>(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                if (data is Category) ContainerActivity.OpenContainerType2(requireContext(), "SelectedCategoryFragment", "CategoryMiniApp", data)
            }
        })
        binding.sectionCategories.recyclerCategories.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            this.adapter = adapter
        }
        adapter.addAllItem(categories)
    }

    private fun setPopularStore(popular: List<MiniApp>) {
        val adapter = PopularStoresAdapter<MiniApp>(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                if (data is MiniApp) ContainerActivity.OpenContainerType2(requireContext(), "StoreDetailFragment", data.name, data)
            }
        })
        binding.sectionPopularStores.recyclerPopularStores.apply {
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)
            this.adapter = adapter
        }
        adapter.addAllItem(popular.take(24))
        binding.sectionPopularStores.btnAllPopularStores.setOnClickListener {
            ContainerActivity.OpenContainerType2(
                requireContext(),
                "SelectedCategoryFragment",
                "Popular",
                popular.take(18)
            )
        }
    }

    private fun setTrendingStores(trending: List<MiniApp>) {
        val adapter = TrendingStoresAdapter<MiniApp>(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                if (data is MiniApp) ContainerActivity.OpenContainerType2(requireContext(), "StoreDetailFragment", data.name, data)
            }
        })
        binding.sectionTrendingStores.recyclerTrendingStores.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            this.adapter = adapter
        }
        adapter.addAllItem(trending.take(16))
        binding.sectionTrendingStores.btnAllTrendingStores.setOnClickListener {
            ContainerActivity.OpenContainerType2(
                requireContext(),
                "SelectedCategoryFragment",
                "Trending",
                trending.take(18)
            )
        }
    }

    private fun setTrendingStores1(trending: List<MiniApp>) {
        val adapter = TrendingStoresAdapter1<MiniApp>(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                if (data is MiniApp) ContainerActivity.OpenContainerType2(requireContext(), "StoreDetailFragment", data.name, data)
            }
        })
        binding.recyclerTrendingStores1.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            this.adapter = adapter
        }
        adapter.addAllItem(trending.take(16))
        binding.sectionTrendingStores.btnAllTrendingStores.setOnClickListener {
            ContainerActivity.OpenContainerType2(
                requireContext(),
                "SelectedCategoryFragment",
                "Trending",
                trending.take(18)
            )
        }
    }

    private fun setActiveHomeCategory(categoryList: List<Category>) {
        val adapter = CategoryActiveAdapter<Category>(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {}
        })
        binding.sectionActiveCategories.recyclerActiveCategories.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            this.adapter = adapter
        }
        adapter.addAllItem(categoryList)
    }

    private fun setPopularGamesLayout(data: List<app.perkhub.Screens.Games.model.Data>) {
        val adapter = PopularGameAdapter<app.perkhub.Screens.Games.model.Data>(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                if (data is app.perkhub.Screens.Games.model.Data) requireContext().openCustomTab(data.url)
            }
        })
        binding.sectionPopularGames.recyclerPopularGames.apply {
            layoutManager = GridLayoutManager(requireContext(), 3, GridLayoutManager.HORIZONTAL, false)
            this.adapter = adapter
        }
        adapter.addAllItem(data.take(24))
    }

    // ---------------- BANNER ---------------- //
    private fun Banner1Func(banners: List<Banner>) {
        val imageList = banners.filter { !it.image.isNullOrEmpty() }
            .map { SliderModelMain(it.image, "", it.url, it.id.toString()) }
        if (imageList.isEmpty()) return

        binding.bannerKocMarket.adapter = BannerViewPagerAdapter(
            imageList.toMutableList(),
            binding.bannerKocMarket,
            object : RecyclerClickListener {
                override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                    requireContext().openCustomTab(type)
                }
            }
        )

        binding.bannerKocMarket.apply {
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 3
            setPadding(1, 0, 1, 0)
            (getChildAt(0) as? RecyclerView)?.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }

        banner1Runnable = object : Runnable {
            override fun run() {
                val current = binding.bannerKocMarket.currentItem
                val next = if (current == imageList.size - 1) 0 else current + 1
                binding.bannerKocMarket.setCurrentItem(next, true)
                bannerHandler.postDelayed(this, 6000)
            }
        }
        bannerHandler.postDelayed(banner1Runnable!!, 6000)
    }

    // ---------------- BANNER ---------------- //
    private fun BannerPromotional(banners: List<Banner>) {
        val imageList = banners.filter { !it.image.isNullOrEmpty() }
            .map { SliderModelMain(it.image, "", it.url, it.id.toString()) }
        if (imageList.isEmpty()) return

        binding.promotionalBanner.adapter = BannerViewPagerAdapter(
            imageList.toMutableList(),
            binding.promotionalBanner,
            object : RecyclerClickListener {
                override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                    requireContext().openCustomTab(type)
                }
            }
        )

        binding.promotionalBanner.apply {
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 3
            setPadding(1, 0, 1, 0)
            (getChildAt(0) as? RecyclerView)?.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }

        promoBannerRunnable = object : Runnable {
            override fun run() {
                val current = binding.promotionalBanner.currentItem
                val next = if (current == imageList.size - 1) 0 else current + 1
                binding.promotionalBanner.setCurrentItem(next, true)
                bannerHandler.postDelayed(this, 3500)
            }
        }
        bannerHandler.postDelayed(promoBannerRunnable!!, 3500)
    }


    private fun BannerExclusive(banners: List<Banner>) {
        val imageList = banners.filter { !it.image.isNullOrEmpty() }
            .map { SliderModelMain(it.image, "", it.url, it.id.toString()) }
        if (imageList.isEmpty()) return

        binding.exclusiveBanner.adapter = BannerViewPagerAdapter(
            imageList.toMutableList(),
            binding.exclusiveBanner,
            object : RecyclerClickListener {
                override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                    requireContext().openCustomTab(type)
                }
            }
        )

        binding.exclusiveBanner.apply {
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 3
            setPadding(1, 0, 1, 0)
            (getChildAt(0) as? RecyclerView)?.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }

        exclusiveBannerRunnable = object : Runnable {
            override fun run() {
                val current = binding.exclusiveBanner.currentItem
                val next = if (current == imageList.size - 1) 0 else current + 1
                binding.exclusiveBanner.setCurrentItem(next, true)
                bannerHandler.postDelayed(this, 4000)
            }
        }
        bannerHandler.postDelayed(exclusiveBannerRunnable!!, 4000)
    }


    // ---------------- DRAWER MENU ---------------- //
    private fun setupMenu() {
        val userName = getSharedpref().getUser(USERDATA)?.data
        binding.profileName.text = userName!!.email.take(2).toString().uppercase()


        binding.profile.setOnClickListener {
            ContainerActivity.OpenContainer(requireContext(), "ProfileFragment", "")
//            binding.drawerlayout.open()
        }

        if (userName.blocked){
            requireActivity().runOnUiThread {
                requireContext().showBlockedUserDialog()
            }
        }else{
            hideBlockedDialog()
        }
    }

    // ---------------- BACK PRESS ---------------- //
    fun onBackPressed() {
        if (backPressedTime + doubleBackPressDuration > System.currentTimeMillis()) {
            activity?.finish()
        } else {
            requireContext().Snackbar("Press back again to exit",_binding!!.mainlayout)
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

}
