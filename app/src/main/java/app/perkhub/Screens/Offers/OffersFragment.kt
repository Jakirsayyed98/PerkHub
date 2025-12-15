package app.perkhub.Screens.Offers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import app.perkhub.R
import app.perkhub.Screens.Home.Model.GenerateUrl.GenerateSubId
import app.perkhub.Screens.Home.Model.GenerateUrl.GenerateSubIdRes
import app.perkhub.Screens.Offers.adapter.OffersListAdapter
import app.perkhub.Screens.Offers.model.Offerlistres
import app.perkhub.Screens.Offers.model.OffersData
import app.perkhub.Screens.Offers.model.SearchOfferReq
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.Utils.openCustomTab
import app.perkhub.api.ApiListener
import app.perkhub.databinding.FragmentOffersBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OffersFragment : BaseFragment<FragmentOffersBinding>() {

    private var searchJob: Job? = null
    private var loadingJob: Job? = null // To manage loading state
    private lateinit var offersAdapter: OffersListAdapter
    private var fullOffersList: List<OffersData> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOffersBinding.inflate(inflater, container, false)

        setupToolbar()
        setupRecyclerView()
        setupSearchListener()
        getOffersListHomeApi(showLoading = true)

        return binding.root
    }

    override fun onDestroyView() {
        searchJob?.cancel()
        loadingJob?.cancel()
        super.onDestroyView()
    }

    private fun setupToolbar() {
        binding.toolbar.apply {
            name.text = "Offers & Coupons"
            name.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.offers_bg))
            toolbar.elevation = 0f

            backbtn.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.offers_bg)
    }

    private fun setupRecyclerView() {
        offersAdapter = OffersListAdapter(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                generateUrl(type)
            }
        })

        binding.offers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = offersAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupSearchListener() {
        binding.search.addTextChangedListener { editable ->
            val query = editable?.toString()?.trim().orEmpty()

            searchJob?.cancel() // Cancel any ongoing search

            if (query.isEmpty()) {
                updateList(fullOffersList)
                return@addTextChangedListener
            }

            if (query.length < 2) {
                return@addTextChangedListener // Wait for at least 2 chars
            }

            searchJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(500) // Debounce 500ms
                searchOffersAndCoupons(query)
            }
        }
    }

    private fun searchOffersAndCoupons(query: String) {
        showLoading(true)
        val request = SearchOfferReq(query)

        viewModel.searchOffersByStore(
            request,
            requireContext(),
            object : ApiListener<Offerlistres> {
                override fun onSuccess(t: Offerlistres?, mess: String) {
                    if (!isAdded) return
                    val result = t?.data.orEmpty()
                    updateList(result)
                    showLoading(false)
                }

                override fun onError(mess: String) {
                    super.onError(mess)
                    updateList(emptyList())
                    showLoading(false)
                }
            }
        )
    }

    private fun getOffersListHomeApi(showLoading: Boolean = false) {
        if (showLoading) showLoading(true)

        viewModel.getOffersListHomePage(
            requireContext(),
            object : ApiListener<Offerlistres> {
                override fun onSuccess(t: Offerlistres?, mess: String) {
                    if (!isAdded) return
                    fullOffersList = t?.data.orEmpty()
                    updateList(fullOffersList)
                    if (showLoading) showLoading(false)
                }

                override fun onError(mess: String) {
                    super.onError(mess)
                    updateList(emptyList())
                    if (showLoading) showLoading(false)
                }
            }
        )
    }

    private fun updateList(newList: List<OffersData>) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            // Use submitList directly – ListAdapter handles DiffUtil internally
            offersAdapter.submitList(newList)

            // Show/hide no data view
            binding.noDataFound.visibility = if (newList.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun showLoading(show: Boolean) {
        // Implement your loading indicator here (e.g., ProgressBar in toolbar or full screen)
        // Example: binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun generateUrl(name: String) {
        val request = GenerateSubId(name)

        viewModel.generateUrl(
            request,
            requireContext(),
            object : ApiListener<GenerateSubIdRes> {
                override fun onSuccess(t: GenerateSubIdRes?, mess: String) {
                    val url = t?.data ?: return
                    if (t.status == 200 && isAdded) {
                        requireContext().openCustomTab(url)
                    }
                }

                override fun onError(mess: String) {
                    super.onError(mess)
                }
            }
        )
    }

    companion object {
        fun newInstance() = OffersFragment()
    }
}