package app.perkhub.Screens.Home

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Orientation
import app.perkhub.ContainerActivity
import app.perkhub.R
import app.perkhub.Screens.Home.Adapter.*
import app.perkhub.Screens.Home.Model.Games.GamesRes
import app.perkhub.Screens.Home.Model.Home.*
import app.perkhub.Screens.Home.Model.LootProduct.Data
import app.perkhub.Screens.Home.Model.LootProduct.LootProductRes
import app.perkhub.Screens.Home.Model.MiniAppByCategory.MiniAppbyCategory
import app.perkhub.Utils.*
import app.perkhub.api.ApiListener
import app.perkhub.databinding.FragmentHomeScreenBinding
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.launch

class HomeScreenFragment : BaseFragment<FragmentHomeScreenBinding>() {

    private lateinit var toggle: ActionBarDrawerToggle
    private var backPressedTime: Long = 0
    private val doubleBackPressDuration = 3000L
    private val bannerHandler = Handler(Looper.getMainLooper())
    private var bannerRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeScreenBinding.inflate(inflater, container, false)
        binding.progressbar.visibility = View.VISIBLE
        binding.mainlayout.visibility = View.GONE
        setupMenu()
        setupClickEvents()

        viewLifecycleOwner.lifecycleScope.launch { getHomeData() }
        // Uncomment when needed
        // viewLifecycleOwner.lifecycleScope.launch { getLootProduct() }
         viewLifecycleOwner.lifecycleScope.launch { getGamesData() }
        // viewLifecycleOwner.lifecycleScope.launch { updateFirebaseToken() }



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applySafeAreaInsets()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bannerRunnable?.let { bannerHandler.removeCallbacks(it) }
        _binding = null
    }

    // ---------------- SAFE AREA ---------------- //

    private fun applySafeAreaInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            binding.toolbar.updatePadding(top = systemBars.top)
            binding.navView.updatePadding(top = systemBars.top)
            binding.mainlayout.updatePadding(bottom = systemBars.bottom)

            insets
        }
    }

    // ---------------- CLICK EVENTS ---------------- //

    private fun setupClickEvents() = with(binding) {
        search.setOnClickListener {
            ContainerActivity.OpenContainerType2(
                requireContext(),
                "SelectedCategoryFragment",
                "Search",
                null
            )
        }
        notification.setOnClickListener {
            ContainerActivity.OpenContainer(
                requireContext(),
                "NotificationListFragment",
                null
            )
        }
    }

    // ---------------- API CALLS ---------------- //

    private fun getHomeData() {
        viewModel.getHomeData(requireContext(), object : ApiListener<HomeRes> {
            override fun onSuccess(t: HomeRes?, mess: String) {
                val data = t?.data ?: return
                binding.progressbar.visibility = View.GONE
                binding.mainlayout.visibility = View.VISIBLE

                setMainCategoryLayout(data.categories)

                if (data.popular.isEmpty()) {
//                    binding.popularlayout.visibility = View.GONE
                } else {
//                    binding.popularlayout.visibility = View.VISIBLE
                    setPopularStore(data.popular)

                }

                setTrendingStores1(data.popular)

                if (data.banner1.isNullOrEmpty()) {
                    binding.banner1layout.visibility = View.GONE
                } else {
                    binding.banner1layout.visibility = View.VISIBLE
                    Banner1Func(data.banner1)
                }

//                if (data.banner2.isNullOrEmpty()) {
//                    binding.banner2layout.visibility = View.GONE
//                } else {
//                    binding.banner2layout.visibility = View.VISIBLE
//                    Glide.with(requireContext()).load(data.banner2[0].image).centerCrop().into(binding.banner2)
//                }

                if (data.trending.isEmpty()) {
//                    binding.trendingStorelayout.visibility = View.GONE
                } else {
//                    binding.trendingStorelayout.visibility = View.VISIBLE
                    setTrendingStores(data.trending)
                }

                if (!data.category_list.isEmpty()){
                    setActiveHomeCateory(data.category_list)
                }
            }

            override fun onError(mess: String) {
                Toast.makeText(
                    requireContext(),
                    "Home API error: $mess",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun setActiveHomeCateory(categoryList: List<Category>) {
        val mAdapter = CategoryActiveAdapter<Category>(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
//                if (data is Category) {
//                    ContainerActivity.OpenContainerType2(
//                        requireContext(),
//                        "SelectedCategoryFragment",
//                        "CategoryMiniApp",
//                        data
//                    )
//                }
            }
        })

        binding.sectionActiveCategories.recyclerActiveCategories.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
            adapter = mAdapter
        }

        mAdapter.addAllItem(categoryList)
    }

    private fun getLootProduct() {
        viewModel.getLootProduct("1", requireContext(), object : ApiListener<LootProductRes> {
            override fun onSuccess(t: LootProductRes?, mess: String) {
                t?.data?.let { if (it.isNotEmpty()) setLootProductLayout(it) }
            }
        })
    }

    private fun getGamesData() {
        viewModel.getPopularGames(requireContext(), object : ApiListener<GamesRes> {
            override fun onSuccess(t: GamesRes?, mess: String) {
                if (t?.status == 200) setPopularGamesLayout(t.data)
            }
        })
    }

    private fun updateFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) return@addOnCompleteListener
            val token = task.result
            viewModel.updateNotification(token, requireContext(), object : ApiListener<MiniAppbyCategory> {
                override fun onSuccess(t: MiniAppbyCategory?, mess: String) {
                    requireContext().toast("Token updated")
                }
            })
        }
    }

    // ---------------- RECYCLERS ---------------- //

    private fun setMainCategoryLayout(categories: List<Category>) {
        val mAdapter = CategoryAdapter<Category>(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                if (data is Category) {
                    ContainerActivity.OpenContainerType2(
                        requireContext(),
                        "SelectedCategoryFragment",
                        "CategoryMiniApp",
                        data
                    )
                }
            }
        })

        binding.sectionCategories.recyclerCategories.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL,false)
            adapter = mAdapter
        }
//        binding.sectionCategories.recyclerCategories.apply {
//            layoutManager = GridLayoutManager(requireContext(), 4)
//            adapter = mAdapter
//        }

//        mAdapter.addAllItem(categories.take(8))
        mAdapter.addAllItem(categories)
//        binding.sectionCategories.btnAllCategories.setOnClickListener {
//            ContainerActivity.OpenContainerType2(
//                requireContext(),
//                "SelectedCategoryFragment",
//                "Categories",
//                categories
//            )
//        }
    }

    private fun setPopularStore(popular: List<MiniApp>) {
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


        binding.sectionPopularStores.recyclerPopularStores.apply {
//            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)
            adapter = mAdapter
        }
        mAdapter.addAllItem(popular.take(24))

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

        binding.sectionTrendingStores.recyclerTrendingStores.apply {
            layoutManager = LinearLayoutManager(requireContext(),  LinearLayoutManager.HORIZONTAL, false)
            adapter = mAdapter
        }
        mAdapter.addAllItem(trending.take(16))

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
        trending.shuffled()
        val mAdapter = TrendingStoresAdapter1<MiniApp>(object : RecyclerClickListener {
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

        binding.recyclerTrendingStores1.apply {
            layoutManager = LinearLayoutManager(requireContext(),  LinearLayoutManager.HORIZONTAL, false)
            adapter = mAdapter
        }
        mAdapter.addAllItem(trending.take(16))

        binding.sectionTrendingStores.btnAllTrendingStores.setOnClickListener {
            ContainerActivity.OpenContainerType2(
                requireContext(),
                "SelectedCategoryFragment",
                "Trending",
                trending.take(18)
            )
        }
    }


    private fun setLootProductLayout(data: List<Data>) {
//        val mAdapter = LootProductAdapter<Data>(object : RecyclerClickListener {
//            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
//                requireContext().openCustomTab(type)
//            }
//        })
//        binding.sectionDiscountedProducts.recyclerDiscountedProducts.apply {
//            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//            adapter = mAdapter
//        }
//        mAdapter.addAllItem(data)
    }

    private fun setPopularGamesLayout(data: List<app.perkhub.Screens.Home.Model.Games.Data>) {
        val mAdapter = PopularGameAdapter<app.perkhub.Screens.Home.Model.Games.Data>(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                if (data is app.perkhub.Screens.Home.Model.Games.Data) {
                    requireContext().openCustomTab(data.url)
                }
            }
        })

        binding.sectionPopularGames.recyclerPopularGames.apply {
            layoutManager = GridLayoutManager(requireContext(), 3, GridLayoutManager.HORIZONTAL, false)
            adapter = mAdapter
        }
        mAdapter.addAllItem(data.take(24))
    }

    // ---------------- BANNER ---------------- //

    private fun Banner1Func(banners: List<Banner>) {
        val imageList = banners.filter { !it.image.isNullOrEmpty() }
            .map { SliderModelMain(it.image, "", it.url, it.id.toString()) }

        if (imageList.isEmpty()) {
            binding.banner1layout.visibility = View.GONE
            return
        }

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

        // Auto scroll
        bannerRunnable = object : Runnable {
            override fun run() {
                val current = binding.bannerKocMarket.currentItem
                val next = if (current == imageList.size - 1) 0 else current + 1
                binding.bannerKocMarket.setCurrentItem(next, true)
                bannerHandler.postDelayed(this, 3000)
            }
        }
        bannerHandler.postDelayed(bannerRunnable!!, 3000)

//        TabLayoutMediator(binding.tabDots, binding.bannerKocMarket) { _, _ -> }.attach()
    }

    // ---------------- DRAWER MENU ---------------- //

    private fun setupMenu() {
        toggle = ActionBarDrawerToggle(requireActivity(), binding.drawerlayout, R.string.open, R.string.close)
        binding.drawerlayout.addDrawerListener(toggle)
        toggle.syncState()

        val header = binding.navView.getHeaderView(0)
        header.findViewById<TextView>(R.id.Profilename).text =
            getSharedpref().getUser(USERDATA).data.name.toString()

        binding.profile.setOnClickListener { binding.drawerlayout.open() }

        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> binding.drawerlayout.close()
                R.id.transaction -> ContainerActivity.OpenContainer(requireContext(), "TransactionHistoryFragment", "")
                R.id.wallet -> ContainerActivity.OpenContainer(requireContext(), "WalletFragment", "")
                R.id.share -> requireContext().shareAppLink()
                R.id.rate_us -> requireContext().rateUsApp()
                R.id.privacy_policy -> requireContext().openCustomTab(PRIVACYPOLICY)
                R.id.about_us -> requireContext().openCustomTab(ABOUTUS)
                R.id.contact_us -> requireContext().openCustomTab(CONTACTUS)
                R.id.logout -> requireContext().OpenDialogForLogOut()
            }
            true
        }
    }

    // ---------------- LOGOUT DIALOG ---------------- //

    fun Context.OpenDialogForLogOut() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.logout_dialog_layout)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val cancel = dialog.findViewById<AppCompatButton>(R.id.cancel)
        val leave = dialog.findViewById<AppCompatButton>(R.id.leave)
        dialog.setCancelable(true)

        cancel.setOnClickListener {
            dialog.dismiss()
            binding.drawerlayout.close()
            toast("Stay logged in")
        }
        leave.setOnClickListener {
            dialog.dismiss()
            binding.drawerlayout.close()
            toast("Logged out successfully")
        }

        dialog.show()
    }

    // ---------------- BACK PRESS ---------------- //

    fun onBackPressed() {
        if (backPressedTime + doubleBackPressDuration > System.currentTimeMillis()) {
            activity?.finish()
        } else {
            requireContext().toast("Press back again to exit")
        }
        backPressedTime = System.currentTimeMillis()
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeScreenFragment()
    }
}
