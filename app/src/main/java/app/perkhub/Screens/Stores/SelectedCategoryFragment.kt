package app.perkhub.Screens.Stores

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import app.perkhub.ContainerActivity
import app.perkhub.Screens.Home.Adapter.CategoryAdapter
import app.perkhub.Screens.Home.Adapter.MiniAppCategoryAdapter
import app.perkhub.Screens.Home.Model.Home.Category
import app.perkhub.Screens.Home.Model.Home.MiniApp
import app.perkhub.Screens.Home.Model.MiniAppByCategory.MiniAppByCategoryReq
import app.perkhub.Screens.Home.Model.MiniAppByCategory.MiniAppbyCategory
import app.perkhub.Screens.Stores.model.StoresRes
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.CONTAINER_TYPE_KEY1
import app.perkhub.Utils.DATA
import app.perkhub.Utils.POPULAR_STORES
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.Utils.SEARCH
import app.perkhub.Utils.SELECTED_CATEGORY_FRAGMENT
import app.perkhub.Utils.Snackbar
import app.perkhub.Utils.TOP_CASHBACK_PROVIDER
import app.perkhub.Utils.TRENDING_STORES
import app.perkhub.api.ApiListener
import app.perkhub.databinding.FragmentSelectedCategoryBinding
import app.perkhub.room.Store.StoreRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SelectedCategoryFragment :
    BaseFragment<FragmentSelectedCategoryBinding>() {

    private lateinit var storeRepository: StoreRepository

    /**
     * Complete store list loaded from Room.
     *
     * Search is performed locally on this list.
     */
    private var allStores: List<MiniApp> = emptyList()

    private val gson by lazy {
        Gson()
    }

    private var searchJob: Job? = null

    private var currentSearchQuery = ""


    // =========================================================
    // CATEGORY ADAPTER
    // =========================================================

    private val categoryAdapter by lazy {
        CategoryAdapter<Category>(
            object : RecyclerClickListener {

                override fun RecyclerClickListener(
                    posi: Int,
                    type: String,
                    data: Any?
                ) {

                    if (data is Category) {

                        ContainerActivity.OpenContainerType2(
                            requireContext(),
                            SELECTED_CATEGORY_FRAGMENT,
                            "CategoryMiniApp",
                            data
                        )
                    }
                }
            }
        )
    }

    // =========================================================
    // NORMAL MINI APP ADAPTER
    // =========================================================

    private val miniAppAdapter by lazy {

        MiniAppCategoryAdapter<MiniApp>(
            object : RecyclerClickListener {

                override fun RecyclerClickListener(
                    posi: Int,
                    type: String,
                    data: Any?
                ) {

                    if (data is MiniApp) {

                        ContainerActivity.OpenContainerType2(
                            requireContext(),
                            "StoreDetailFragment",
                            data.name,
                            data
                        )
                    }
                }
            }
        )
    }

    // =========================================================
    // SEARCH STORE ADAPTER
    // =========================================================

    private val searchStoreAdapter by lazy {

        SearchStoreAdapter<MiniApp>(
            object : RecyclerClickListener {

                override fun RecyclerClickListener(
                    posi: Int,
                    type: String,
                    data: Any?
                ) {

                    if (data is MiniApp) {

                        ContainerActivity.OpenContainerType2(
                            requireContext(),
                            "StoreDetailFragment",
                            data.name,
                            data
                        )
                    }
                }
            }
        )
    }

    // =========================================================
    // CREATE VIEW
    // =========================================================

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSelectedCategoryBinding.inflate(
            inflater,
            container,
            false
        )

        storeRepository = StoreRepository(requireContext())

        binding.toolbar.backbtn.setOnClickListener {
            requireActivity()
                .onBackPressedDispatcher
                .onBackPressed()
        }

        setupRecyclerViews()
        setupInitialView()
        setupSearchListener()
        setupClearSearch()

        return binding.root
    }

    // =========================================================
    // RECYCLER VIEWS
    // =========================================================

    private fun setupRecyclerViews() {
        // Normal screen
        binding.listOfStores.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = miniAppAdapter
        }

        // Search -> Stores
        binding.searchStoresRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchStoreAdapter
            isNestedScrollingEnabled = false
//            setHasFixedSize(true)
        }
    }

    // =========================================================
    // INITIAL SCREEN
    // =========================================================

    private fun setupInitialView() {
        val type1 = requireActivity().intent.getStringExtra(CONTAINER_TYPE_KEY1)
        val value = requireActivity().intent.getStringExtra(DATA)
        when (type1) {
            "CategoryMiniApp" -> {
                showNormalScreen()
                try {
                    val category = gson.fromJson(value, Category::class.java)
                    binding.toolbar.name.text = category?.name ?: "Category"
                    category?.id?.toString()?.let { categoryId ->
                            getListOfMiniApps(categoryId)
                        }
                } catch (e: Exception) {
                    binding.toolbar.name.text = "Category"
                }
            }

            "Categorires" -> {
                showNormalScreen()
                binding.toolbar.name.text = "All Categories"
                try {
                    val categories = gson.fromJson<List<Category>>(value, object : TypeToken<List<Category>>() {}.type)
                    setMainCategoryLayout(ArrayList(categories))

                } catch (_: Exception) {
                }
            }

            TOP_CASHBACK_PROVIDER -> {
                showNormalScreen()
                binding.toolbar.name.text = "Top Cashback Stores"
                try {
                    val popular = gson.fromJson<List<MiniApp>>(value, object : TypeToken<List<MiniApp>>() {}.type)
                    setLayout(popular)
                } catch (_: Exception) {
                }
            }

            POPULAR_STORES -> {
                showNormalScreen()
                binding.toolbar.name.text = "Popular Stores"
                try {
                    val popular = gson.fromJson<List<MiniApp>>(value, object : TypeToken<List<MiniApp>>() {}.type)
                    setLayout(popular)
                } catch (_: Exception) {
                }
            }

            TRENDING_STORES -> {
                showNormalScreen()
                binding.toolbar.name.text = "Trending Stores"
                try {
                    val trending = gson.fromJson<List<MiniApp>>(value, object : TypeToken<List<MiniApp>>() {}.type)
                    setLayout(trending)
                } catch (_: Exception) {
                }
            }

            SEARCH -> {
                showSearchScreen()
                binding.toolbar.name.text = "Search"
                observeStores()
            }
            "Couponcode" -> {
                showNormalScreen()
                binding.toolbar.name.text = "Coupon Codes"
            }

            "Offers" -> {
                showNormalScreen()
                binding.toolbar.name.text = "Offers"
            }

            "Vouchers" -> {
                showNormalScreen()
                binding.toolbar.name.text = "Vouchers"
            }
        }
    }

    private fun showSearchScreen() {
        binding.normalContent.visibility = View.GONE
        binding.searchContent.visibility = View.VISIBLE
    }

    private fun showNormalScreen() {
        binding.normalContent.visibility = View.VISIBLE
        binding.searchContent.visibility = View.GONE
    }

    private fun setupSearchListener() {
        binding.search.doOnTextChanged { text, _, _, _ ->
            currentSearchQuery = text?.toString()?.trim() ?: ""
            binding.clearSearch.visibility = if (currentSearchQuery.isNotEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

            searchJob?.cancel()
            if (currentSearchQuery.length >= 2) {
                searchJob = viewLifecycleOwner.lifecycleScope.launch {
                        delay(100)
                        filterStoresLocally(currentSearchQuery)
                }
            } else {
                clearSearchResults()
            }
        }
    }

    private fun setupClearSearch() {
        binding.clearSearch.setOnClickListener {
            binding.search.text?.clear()
        }
    }

    private fun clearSearchResults() {
        searchStoreAdapter.clear()
        binding.resultCount.text = "Search stores, offers, coupons and games"
        binding.storeCount.text = "Stores (0)"
    }

    // =========================================================
    // OBSERVE ROOM STORES
    // =========================================================

    private fun observeStores() {
        viewLifecycleOwner.lifecycleScope.launch {
            storeRepository
                .getStoresFlow()
                .collect { stores ->

                    allStores = stores
                    if (currentSearchQuery.length >= 2) {
                        filterStoresLocally(currentSearchQuery)
                    }
                }
        }
        fetchStoresList()
    }

    // =========================================================
    // LOCAL STORE SEARCH
    // =========================================================

    private fun filterStoresLocally(query: String) {
        val searchQuery = query.trim().lowercase()
        if (searchQuery.length < 2) {
            clearSearchResults()
            return
        }
        val filteredStores = allStores.filter { store ->
                store.name.trim().lowercase().contains(searchQuery)
        }
        setSearchStoreData(filteredStores)
    }

    // =========================================================
    // FETCH STORES
    // =========================================================

    private fun fetchStoresList() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getAllStores(requireContext(), object : ApiListener<StoresRes> {
                    override fun onSuccess(t: StoresRes?, mess: String) {
                    }
                    override fun onError(mess: String) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            if (!isAdded) {
                                return@launch
                            }
                            if (allStores.isEmpty()) {
                                requireContext().Snackbar(
                                    "Failed to load stores: $mess",
                                    binding.searchContent
                                )
                            }
                        }
                    }
                }
            )
        }
    }

    private fun setSearchStoreData(data: List<MiniApp>) {
        searchStoreAdapter.clear()
        searchStoreAdapter.addAllItem(data)
        val count = data.size
        binding.storeCount.text = "Stores ($count)"
        binding.resultCount.text = "$count result found for \"$currentSearchQuery\""
    }

    // =========================================================
    // NORMAL CATEGORY LAYOUT
    // =========================================================

    private fun setMainCategoryLayout(categories: ArrayList<Category>) {
        binding.listOfStores.apply {
            layoutManager =GridLayoutManager(requireContext(), 4)
            adapter = categoryAdapter
        }
        categoryAdapter.clear()
        categoryAdapter.addAllItem(categories)
        if (categories.isEmpty()) {
            binding.listOfStores.visibility = View.GONE
            binding.noDataFound.visibility = View.VISIBLE
        } else {
            binding.listOfStores.visibility = View.VISIBLE
            binding.noDataFound.visibility = View.GONE
        }
    }

    // =========================================================
    // CATEGORY MINI APPS
    // =========================================================

    private fun getListOfMiniApps(categoryId: String) {
        val req = MiniAppByCategoryReq(categoryId)
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getMiniAppByCategory(req, requireContext(),
                object : ApiListener<MiniAppbyCategory> {
                    override fun onSuccess(t: MiniAppbyCategory?, mess: String) {
                        val data = t?.data ?: emptyList()
                        lifecycleScope.launch(Dispatchers.Main) {
                            setLayout(data)
                        }
                    }

                    override fun onError(mess: String) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            if (!isAdded) {
                                return@launch
                            }
                            requireContext().Snackbar("Failed to load mini apps: $mess", binding.listOfStores)
                        }
                    }
                }
            )
        }
    }

    // =========================================================
    // NORMAL MINI APP LAYOUT
    // =========================================================

    private fun setLayout(data: List<MiniApp>) {
        miniAppAdapter.clear()
        miniAppAdapter.addAllItem(data)
        if (data.isEmpty()) {
            binding.listOfStores.visibility = View.GONE
            binding.noDataFound.visibility = View.VISIBLE
        } else {
            binding.listOfStores.visibility = View.VISIBLE
            binding.noDataFound.visibility = View.GONE
        }
    }

    // =========================================================
    // DESTROY
    // =========================================================

    override fun onDestroyView() {
        searchJob?.cancel()
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SelectedCategoryFragment()
    }
}