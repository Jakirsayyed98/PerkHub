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

    private var backPressedTime: Long = 0
    private val doubleBackPressDuration = 3000L
    private var blockedDialog: Dialog? = null
    private var firebaseTaskActive = false

    private lateinit var bannerAdapter: BannerAdapter

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
//        observePopularGames()
        // Fetch data
        lifecycleScope.launch { fetchHomeData() }       // HomeViewModel
        if (getSharedpref().getUser(USERDATA)==null){
            getUserDetail()
        }
        binding.bannerLayout.viewPagerBanner.registerOnPageChangeCallback(pageCallback)
        lifecycleScope.launch { updateFirebaseToken() } // requestViewModel
        (binding.root.parent as? ViewGroup)?.removeView(binding.root)

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

        bannerHandler.removeCallbacks(bannerRunnable)

        binding.bannerLayout.viewPagerBanner.adapter = null
        binding.bannerLayout.viewPagerBanner.unregisterOnPageChangeCallback(pageCallback)
        _binding = null

        super.onDestroyView()
    }

    // ---------------- CLICK EVENTS ---------------- //
    private fun setupClickEvents() = with(binding) {
        layoutSearch.root.setOnClickListener {
            ContainerActivity.OpenContainerType2(requireContext(), "SelectedCategoryFragment", "Search", null)
        }
        layoutNotification.setOnClickListener {
            ContainerActivity.OpenContainer(requireContext(), "NotificationListFragment", null)
        }
    }

    // ---------------- API CALLS ---------------- //
    private suspend fun fetchHomeData() {
        viewModel.getHomeData(object : ApiListener<HomeRes> {
            override fun onSuccess(t: HomeRes?, mess: String) {
                t?.data?.let { data ->
                    lifecycleScope.launch(Dispatchers.Main) {

                        setMainCategoryLayout(data.categories)
                        setBanner1(data.banner1)
                       }
                }
            }

            override fun onError(mess: String) {
                lifecycleScope.launch(Dispatchers.Main) {
                    requireContext().Snackbar("Home API error: $mess",_binding!!.root)
                }
            }
        })
    }

    private fun setBanner1(banners: List<Banner>?) {

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
                            requireContext().Snackbar("Failed to fetch notifications: $mess",_binding!!.root)
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
            ContainerActivity.OpenContainer(requireContext(), "ProfileFragment", "")
//            binding.drawerlayout.open()
        }

        if (userName?.blocked == true){
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
            requireContext().Snackbar("Press back again to exit",_binding!!.drawerLayout)
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
