package app.perkhub.Screens.Offers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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
import app.perkhub.Utils.Share
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOffersBinding.inflate(layoutInflater,container,false)
        _binding!!.toolbar.name.text = "Offers & Coupons"
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(),R.color.offers_bg)
        _binding!!.toolbar.backbtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.toolbar.toolbar.elevation = 0.0F
        binding.toolbar.name.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
        _binding!!.toolbar.root.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.offers_bg))

        getOffersListHomeApi()

        setupSearchListener()
        return _binding?.root
    }

    private fun setupSearchListener() {
        binding.search.addTextChangedListener { text ->
            searchJob?.cancel()
            val query = text.toString()
            if (query.length >= 3) {
                searchJob = lifecycleScope.launch {
                    delay(300) // debounce
                    searchOffersAndCoupons(query)
                }
            }
        }
    }

    private fun searchOffersAndCoupons(query: String) {
        if (query.length>=3){
            val request = SearchOfferReq(query)
            viewModel.searchOffersByStore(request,requireContext(),object :ApiListener<Offerlistres>{
                override fun onSuccess(t: Offerlistres?, mess: String) {
                    t!!.data.let {
                        lifecycleScope.launch(Dispatchers.Main) {
                            setOffersListData(it)
                        }
                    }
                }
            })
        }
    }

    private fun getOffersListHomeApi() {
        viewModel.getOffersListHomePage(requireContext(),object:ApiListener<Offerlistres>{
            override fun onSuccess(t: Offerlistres?, mess: String) {
                t!!.data.let {
                    lifecycleScope.launch(Dispatchers.Main) {
                        setOffersListData(it)
                    }
                }
            }
        })
    }

    private fun setOffersListData(offerList: List<OffersData>) {
        val mAdapter = OffersListAdapter<OffersData>(object :RecyclerClickListener{
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                GetUrl(type)
            }
        })
        binding.offers.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            adapter = mAdapter
        }
        mAdapter.addAllItem(offerList)
    }


    private fun GetUrl(name: String) {
        var request = GenerateSubId(name)
        viewModel.generateUrl(request,requireContext(),object : ApiListener<GenerateSubIdRes>{
            override fun onSuccess(t: GenerateSubIdRes?, mess: String) {

                t!!.let {
                    if (it.status==200){
                        requireContext().openCustomTab(t!!.data)
                    }
                }

            }
        })


    }


    companion object {
        @JvmStatic
        fun newInstance() =
            OffersFragment().apply {
            }
    }
}