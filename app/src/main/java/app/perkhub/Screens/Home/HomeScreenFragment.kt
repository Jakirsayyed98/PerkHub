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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.perkhub.ContainerActivity
import app.perkhub.R
import app.perkhub.Screens.Home.Adapter.*
import app.perkhub.Screens.Home.Model.Games.GamesRes
import app.perkhub.Screens.Home.Model.Home.*
import app.perkhub.Screens.Home.Model.MiniAppByCategory.MiniAppbyCategory
import app.perkhub.Utils.*
import app.perkhub.api.ApiListener
import app.perkhub.api.MyApi
import app.perkhub.databinding.FragmentHomeScreenBinding
import app.perkhub.room.HomeDatabase
import app.perkhub.room.HomeRepository
import app.perkhub.room.HomeViewModel
import app.perkhub.room.RequestViewModelFactory
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider

class HomeScreenFragment : BaseFragment<FragmentHomeScreenBinding>() {

    private lateinit var toggle: ActionBarDrawerToggle
    private var backPressedTime: Long = 0
    private val doubleBackPressDuration = 3000L
    private val bannerHandler = Handler(Looper.getMainLooper())
    private var bannerRunnable: Runnable? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeScreenBinding.inflate(inflater, container, false)
        binding.progressbar.visibility = View.VISIBLE
        binding.mainlayout.visibility = View.GONE

        setupMenu()
        setupClickEvents()
        applySafeAreaInsets()

        // Fetch data
        lifecycleScope.launch { fetchHomeData() }       // HomeViewModel
        lifecycleScope.launch { fetchGamesData() }      // requestViewModel
        lifecycleScope.launch { updateFirebaseToken() } // requestViewModel

        return binding.root
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
            ContainerActivity.OpenContainerType2(requireContext(), "SelectedCategoryFragment", "Search", null)
        }
        notification.setOnClickListener {
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
                        binding.mainlayout.visibility = View.VISIBLE

                        setMainCategoryLayout(data.categories)
                        if (data.popular.isNotEmpty()) setPopularStore(data.popular)
                        setTrendingStores1(data.popular)
                        if (data.banner1.isNullOrEmpty()) binding.banner1layout.visibility = View.GONE
                        else Banner1Func(data.banner1)
                        if (data.trending.isNotEmpty()) setTrendingStores(data.trending)
                        if (data.category_list.isNotEmpty()) setActiveHomeCategory(data.category_list)
                    }
                }
            }

            override fun onError(mess: String) {
                lifecycleScope.launch(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Home API error: $mess", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun fetchGamesData() {
        viewModel.getPopularGames(requireContext(), object : ApiListener<GamesRes> {
            override fun onSuccess(t: GamesRes?, mess: String) {
                t?.data?.let { setPopularGamesLayout(it) }
            }

            override fun onError(mess: String) {}
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

    private fun setPopularGamesLayout(data: List<app.perkhub.Screens.Home.Model.Games.Data>) {
//        Log.d("TAG",data.toString())
        val adapter = PopularGameAdapter<app.perkhub.Screens.Home.Model.Games.Data>(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                if (data is app.perkhub.Screens.Home.Model.Games.Data) requireContext().openCustomTab(data.url)
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

        bannerRunnable = object : Runnable {
            override fun run() {
                val current = binding.bannerKocMarket.currentItem
                val next = if (current == imageList.size - 1) 0 else current + 1
                binding.bannerKocMarket.setCurrentItem(next, true)
                bannerHandler.postDelayed(this, 3000)
            }
        }
        bannerHandler.postDelayed(bannerRunnable!!, 3000)
    }

    // ---------------- DRAWER MENU ---------------- //
    private fun setupMenu() {
        toggle = ActionBarDrawerToggle(requireActivity(), binding.drawerlayout, R.string.open, R.string.close)
        binding.drawerlayout.addDrawerListener(toggle)
        toggle.syncState()

        val header = binding.navView.getHeaderView(0)
        header.findViewById<TextView>(R.id.Profilename).text = getSharedpref().getUser(USERDATA).data.name.toString()

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
                R.id.logout -> requireContext().showLogoutDialog()
            }
            true
        }
    }

    private fun Context.showLogoutDialog() {
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
