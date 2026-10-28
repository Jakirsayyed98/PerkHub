package app.perkhub.Screens.Games

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import app.perkhub.ContainerActivity
import app.perkhub.R
import app.perkhub.Screens.Games.adapter.GameBannerAdapter
import app.perkhub.Screens.Games.adapter.GameCategoriesAdapter
import app.perkhub.Screens.Games.adapter.PopularGameAdapter
import app.perkhub.Screens.Games.adapter.TrendingGameAdapter
import app.perkhub.Screens.Games.model.Data
import app.perkhub.Screens.Games.model.GameCategoryData
import app.perkhub.Screens.Games.model.GamesCategoryRes
import app.perkhub.Screens.Games.model.GamesRes
import app.perkhub.Screens.Home.Adapter.BannerAdapter
import app.perkhub.Screens.Home.Adapter.BannerViewPagerAdapter
import app.perkhub.Screens.Home.Adapter.SliderModelMain
import app.perkhub.Screens.Home.Model.Home.Banner
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.Utils.SEARCH
import app.perkhub.Utils.SELECTED_CATEGORY_FRAGMENT
import app.perkhub.Utils.Snackbar
import app.perkhub.Utils.openCustomTab
import app.perkhub.api.ApiListener
import app.perkhub.databinding.FragmentGamesBinding
import app.perkhub.room.games.GamesCategoryRepository
import app.perkhub.room.games.GamesRepository
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GamesFragment : BaseFragment<FragmentGamesBinding>() {

    private val bannerHandler2 = Handler(Looper.getMainLooper())
    private var bannerRunnable2: Runnable? = null

    private val gamesList = ArrayList<Data>()

    // Local caches for combining
    private var allCategories = emptyList<GameCategoryData>()
    private var allGames = emptyList<Data>()

    private lateinit var bannerAdapter: GameBannerAdapter
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
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View? {
        _binding = FragmentGamesBinding.inflate(inflater, container, false)
        binding.layoutSearch.edtSearch.text = "Search games by name,category...."
        setToolBar()
        observeGames()
        observeGameCategories()
        binding.bannerLayout.viewPagerBanner.registerOnPageChangeCallback(pageCallback)
        return _binding?.root
    }

    private fun setBanner1(banners: List<Data>?) {
        binding.bannerLayout.viewPagerBanner.visibility = View.VISIBLE
        if (banners.isNullOrEmpty()) return
        if (!::bannerAdapter.isInitialized) {
            bannerAdapter = GameBannerAdapter(banners.toMutableList()) { banner ->
                if (banner.url.isNotEmpty()) {
                    requireContext().openCustomTab(banner.url)
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


    private fun createIndicator(count: Int) {
        val layout = binding.bannerLayout.layoutIndicator
        layout.removeAllViews()
        repeat(count) {
            val dot = View(requireContext())
            val params = LinearLayout.LayoutParams(resources.getDimensionPixelSize(R.dimen.dp6), resources.getDimensionPixelSize(R.dimen.dp6))
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

    private fun Banner2Func(banners: List<Data>) {
        val imageList = banners.filter { it.assets.wall.isNotEmpty() }
            .map { SliderModelMain(it.assets.wall, "", it.url, it.id.toString()) }
        if (imageList.isEmpty()) return

        binding.bannerKocMarket2.adapter = BannerViewPagerAdapter(
            imageList.toMutableList(),
            binding.bannerKocMarket2,
            object : RecyclerClickListener {
                override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                    requireContext().openCustomTab(type)
                }
            }
        )

        binding.bannerKocMarket2.apply {
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 3
            setPadding(1, 0, 1, 0)
            (getChildAt(0) as? RecyclerView)?.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }

        bannerRunnable2 = object : Runnable {
            override fun run() {
                val current = binding.bannerKocMarket2.currentItem
                val next = if (current == imageList.size - 1) 0 else current + 1
                binding.bannerKocMarket2.setCurrentItem(next, true)
                bannerHandler2.postDelayed(this, 4000)
            }
        }
        bannerHandler2.postDelayed(bannerRunnable2!!, 4000)
    }

    private fun setToolBar() {
        binding.toolbar.name.text = "PerkHub Games"
//        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.games_bg)
        binding.layoutSearch.imgQr.visibility = View.GONE
        binding.toolbar.backbtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.layoutSearch.searchBar.setOnClickListener {
            ContainerActivity.OpenContainer(requireContext(),"SearchGameFragment",null)
        }
        binding.layoutSearch.edtSearch.setOnClickListener {
            ContainerActivity.OpenContainer(requireContext(),"SearchGameFragment",null)
        }

//        binding.toolbar.toolbar.elevation = 0f
//        binding.toolbar.name.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
//        binding.toolbar.root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.games_bg))
    }

    private fun observeGames() {
        val repository = GamesRepository(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            repository.getGames().collect { games ->
                allGames = games
                setBanner1(if (games.size > 10) games.shuffled().subList(0, 10) else games)
                Banner2Func(if (games.size > 10) games.shuffled().subList(0, 10) else games)
                setPopularGamesLayout(games.shuffled())
                setGamesLayout(games.shuffled())

                gamesList.clear()
                gamesList.addAll(games)

                // Combine after games are fetched
                combineCategoriesWithGames()
            }
        }

        fetchGamesList(forceRefresh = true)
    }

    private fun setPopularGamesLayout(games: List<Data>) {
        val mAdapter = PopularGameAdapter<Data>(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                if (data is Data) requireContext().openCustomTab(data.url)
            }
        })

        binding.populargame.apply {
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)
            adapter = mAdapter
        }

        mAdapter.addAllItem(if (games.size > 14) games.subList(0, 14) else games)
    }

    private fun setGamesLayout(games: List<Data>) {
        val mAdapter = TrendingGameAdapter<Data>(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                if (data is Data) requireContext().openCustomTab(data.url)
            }
        })

        binding.trendinggame.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = mAdapter
        }

        mAdapter.addAllItem(if (games.size > 14) games.shuffled().subList(0, 14) else games.shuffled())
    }

    private fun fetchGamesList(forceRefresh: Boolean = false) {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getGamesList(
                requireContext(),
                forceRefresh,
                object : ApiListener<GamesRes> {
                    override fun onSuccess(t: GamesRes?, mess: String) {}
                    override fun onError(mess: String) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            requireContext().Snackbar("Failed to fetch games: $mess",_binding!!.populargame)
                        }
                    }
                })
        }
    }

    private fun observeGameCategories() {
        val repository = GamesCategoryRepository(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            repository.getGamesCategory().collect { gameCategory ->
                allCategories = gameCategory
                combineCategoriesWithGames()
            }
        }

        fetchGameCategoriesList(forceRefresh = true)
    }

    private fun combineCategoriesWithGames() {
        if (allCategories.isEmpty() || allGames.isEmpty()) return

        val mergedCategories = allCategories.map { category ->
            val relatedGames = allGames.filter { game ->
                // assuming `Data` has `category_id`
                game.category_id == category.id
            }
            category.apply {
                games = relatedGames
            }
        }

        // Filter only categories that have games
        val validCategories = mergedCategories.filter { it.games.isNotEmpty() }
        setGameCategoriesWithLayout(validCategories)
    }

    private fun setGameCategoriesWithLayout(gamesCategory: List<GameCategoryData>) {
        val mAdapter = GameCategoriesAdapter<GameCategoryData>(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                if (data is Data) requireContext().openCustomTab(data.url)
            }
        })

        binding.gameCategories.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = mAdapter
        }

        mAdapter.addAllItem(gamesCategory)
    }

    private fun fetchGameCategoriesList(forceRefresh: Boolean = false) {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getAllGamesCategory(
                requireContext(),
                forceRefresh,
                object : ApiListener<GamesCategoryRes> {
                    override fun onSuccess(t: GamesCategoryRes?, mess: String) {}
                    override fun onError(mess: String) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            requireContext().Snackbar("Failed to fetch game categories: $mess",_binding!!.gameCategories)
                        }
                    }
                })
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

    companion object {
        @JvmStatic
        fun newInstance() = GamesFragment()
    }
}
