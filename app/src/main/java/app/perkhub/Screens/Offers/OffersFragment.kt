package app.perkhub.Screens.Offers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
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
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OffersFragment : BaseFragment<FragmentOffersBinding>() {

    private val gson by lazy { Gson() }
    private var searchJob: Job? = null
    private lateinit var offersAdapter: OffersListAdapter<OffersData>
    private var fullOffersList: List<OffersData> = listOf()  // Full offers from API

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOffersBinding.inflate(inflater, container, false)

        setupToolbar()
        setupRecyclerView()
        setupSearchListener()
        getOffersListHomeApi()

        return binding.root
    }

    private fun setupToolbar() {
        binding.toolbar.name.text = "Offers & Coupons"
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.offers_bg)

        binding.toolbar.backbtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.toolbar.toolbar.elevation = 0f
        binding.toolbar.name.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.toolbar.root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.offers_bg))
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
        }
    }

    private fun setupSearchListener() {
        binding.search.addTextChangedListener { text ->
            searchJob?.cancel()
            val query = text.toString().trim()

            when {
                query.isEmpty() -> {
                    // Show full offers list
                    offersAdapter.addAllItem(fullOffersList)
                }
                query.length >= 3 -> {
                    // Debounce search input
                    searchJob = viewLifecycleOwner.lifecycleScope.launch {
                        delay(300)
                        searchOffersAndCoupons(query)
                    }
                }
            }
        }
    }

    private fun searchOffersAndCoupons(query: String) {
        val request = SearchOfferReq(query)
        viewModel.searchOffersByStore(request, requireContext(), object : ApiListener<Offerlistres> {
            override fun onSuccess(t: Offerlistres?, mess: String) {
                t?.data?.let { data ->
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                        offersAdapter.addAllItem(data)  // Show only search results
                    }
                }
            }

           
        })
    }

    private fun getOffersListHomeApi() {
        viewModel.getOffersListHomePage(requireContext(), object : ApiListener<Offerlistres> {
            override fun onSuccess(t: Offerlistres?, mess: String) {
                t?.data?.let { data ->
                    fullOffersList = data  // Save full list
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                        offersAdapter.addAllItem(fullOffersList)
                    }
                }
            }

           
        })
    }

    private fun generateUrl(name: String) {
        val request = GenerateSubId(name)
        viewModel.generateUrl(request, requireContext(), object : ApiListener<GenerateSubIdRes> {
            override fun onSuccess(t: GenerateSubIdRes?, mess: String) {
                if (t!!.status==200){
                    requireContext().openCustomTab(t.data)
                }
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = OffersFragment()
    }
}
