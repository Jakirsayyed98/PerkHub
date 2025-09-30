package app.perkhub.Screens.Stores

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
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
import app.perkhub.Utils.toast
import app.perkhub.api.ApiListener
import app.perkhub.databinding.FragmentSelectedCategoryBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*

class SelectedCategoryFragment : BaseFragment<FragmentSelectedCategoryBinding>() {

    private val gson by lazy { Gson() }

    // Single adapters for production efficiency
    private val categoryAdapter by lazy {
        CategoryAdapter<Category>(object : RecyclerClickListener {
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
    }

    private val miniAppAdapter by lazy {
        MiniAppCategoryAdapter<MiniApp>(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                if (data is MiniApp) {
                    Log.d("MiniApp Store",data.cbActive.toString())
                    ContainerActivity.OpenContainerType2(
                        requireContext(),
                        "StoreDetailFragment",
                        data.name,
                        data
                    )
                }
            }
        })
    }

    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectedCategoryBinding.inflate(inflater, container, false)

        binding.toolbar.backbtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        setupRecyclerViews()
        applySafeAreaInsets()
        setupInitialView()
        setupSearchListener()

        return binding.root
    }

    private fun setupRecyclerViews() {
        binding.listOfStores.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = miniAppAdapter
        }
    }

    private fun applySafeAreaInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.toolbar2.updatePadding(top = systemBars.top)
            binding.listOfStores.updatePadding(bottom = systemBars.bottom)
            insets
        }
    }

    private fun setupInitialView() {
        val type1 = requireActivity().intent.getStringExtra(CONTAINER_TYPE_KEY1)
        val value = requireActivity().intent.getStringExtra(DATA)

        when (type1) {
            "CategoryMiniApp" -> {
                try {
                    val category = gson.fromJson(value, Category::class.java)
                    binding.toolbar.name.text = category?.name ?: ""
                    binding.searchLayout.visibility = View.GONE
                    category?.id?.toString()?.let { getListOfMiniApps(it) }
                } catch (e: Exception) {
                    binding.toolbar.name.text = "Category"
                }
            }
            "Categorires" -> {
                binding.toolbar.name.text = "All Categories"
                binding.searchLayout.visibility = View.GONE
                try {
                    val categories = gson.fromJson<List<Category>>(
                        value,
                        object : TypeToken<List<Category>>() {}.type
                    )
                    setMainCategoryLayout(ArrayList(categories))
                } catch (e: Exception) {
                    // Handle parsing error
                }
            }
            "Popular" -> {
                binding.toolbar.name.text = "Popular Stores"
                binding.searchLayout.visibility = View.GONE
                try {
                    val popular = gson.fromJson<List<MiniApp>>(
                        value,
                        object : TypeToken<List<MiniApp>>() {}.type
                    )
                    setLayout(popular)
                } catch (e: Exception) { }
            }
            "Trending" -> {
                binding.toolbar.name.text = "Trending Stores"
                binding.searchLayout.visibility = View.GONE
                try {
                    val trending = gson.fromJson<List<MiniApp>>(
                        value,
                        object : TypeToken<List<MiniApp>>() {}.type
                    )
                    setLayout(trending)
                } catch (e: Exception) { }
            }
            "Search" -> {
                binding.toolbar.name.text = "Search"
                binding.searchLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun setupSearchListener() {
        binding.search.addTextChangedListener { text ->
            searchJob?.cancel()
            val query = text.toString()
            if (query.length >= 3) {
                searchJob = lifecycleScope.launch {
                    delay(300) // debounce
                    searchMiniApps(query)
                }
            }
        }
    }

    private fun setMainCategoryLayout(categories: ArrayList<Category>) {
        binding.listOfStores.apply {
            layoutManager = GridLayoutManager(requireContext(), 4)
            adapter = categoryAdapter
        }
        categoryAdapter.clear()
        categoryAdapter.addAllItem(categories)
    }

    private fun getListOfMiniApps(categoryId: String) {
        val req = MiniAppByCategoryReq(categoryId)
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getMiniAppByCategory(req, requireContext(), object : ApiListener<MiniAppbyCategory> {
                override fun onSuccess(t: MiniAppbyCategory?, mess: String) {
                    t?.data?.let { data ->
                        lifecycleScope.launch(Dispatchers.Main) { setLayout(data) }
                    }
                }

                override fun onError(mess: String) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        requireContext().toast("Failed to load mini apps: $mess")
                    }
                }
            })
        }
    }

    private fun searchMiniApps(query: String) {
        val req = MiniAppSearchReq(query)
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.searchMiniApps(req, requireContext(), object : ApiListener<MiniAppbyCategory> {
                override fun onSuccess(t: MiniAppbyCategory?, mess: String) {
                    t?.data?.let { data ->
                        lifecycleScope.launch(Dispatchers.Main) { setLayout(data) }
                    }
                }

                override fun onError(mess: String) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        requireContext().toast("Search failed: $mess")
                    }
                }
            })
        }
    }

    private fun setLayout(data: List<MiniApp>) {
        miniAppAdapter.clear()
        miniAppAdapter.addAllItem(data)
    }

    companion object {
        @JvmStatic
        fun newInstance() = SelectedCategoryFragment()
    }
}
