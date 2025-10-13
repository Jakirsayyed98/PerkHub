package app.perkhub.Screens.Stores

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import app.perkhub.R
import app.perkhub.Screens.Home.Model.GenerateUrl.GenerateSubId
import app.perkhub.Screens.Home.Model.GenerateUrl.GenerateSubIdRes
import app.perkhub.Screens.Home.Model.Home.MiniApp
import app.perkhub.Screens.Offers.adapter.OffersListAdapter
import app.perkhub.Screens.Offers.model.Offerlistres
import app.perkhub.Screens.Offers.model.OffersData
import app.perkhub.Screens.Offers.model.SearchOfferReq
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.DATA
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.Utils.Share
import app.perkhub.Utils.openCustomTab
import app.perkhub.api.ApiListener
import app.perkhub.databinding.FragmentStoreDetailBinding
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class StoreDetailFragment : BaseFragment<FragmentStoreDetailBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStoreDetailBinding.inflate(layoutInflater,container,false)

        _binding!!.backbtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        var data = Gson().fromJson<MiniApp>(requireActivity().intent.getStringExtra(DATA),MiniApp::class.java)
        searchOffersAndCoupons(data.name)
        _binding!!.storename.text = data.name
        _binding!!.link.setText(data.url)
        _binding!!.storenameterm.text = getString(R.string.complete_your_purchase,data.name)
        _binding!!.point1.text = getString(R.string.we_ll_redirect_you_to,data.name)
        _binding!!.shareearntitle.text = getString(R.string.generate_your_unique_referral_link,data.name)
        Glide.with(requireContext()).load(data.icon).into(_binding!!.icon)

        _binding!!.cashbacktext.text = Html.fromHtml(if (!data.cbPercentage.isNullOrEmpty()) "upto "+data.cbPercentage+" Cashback" else "" , Html.FROM_HTML_MODE_LEGACY)

        if (data.cbActive){
            _binding!!.cahbackdetails.visibility = View.VISIBLE
            _binding!!.sharelink.visibility = View.VISIBLE
            _binding!!.importantNote.visibility = View.VISIBLE
        }else{
            _binding!!.cahbackdetails.visibility = View.GONE
            _binding!!.sharelink.visibility = View.GONE
            _binding!!.importantNote.visibility = View.GONE
            _binding!!.cashbacktext.text =  "Cashback is not active"
        }
         if (data.trending){
                _binding!!.tag.visibility = View.VISIBLE
            }else{
                _binding!!.tag.visibility = View.GONE
            }

        _binding!!.share.setOnClickListener {
            GetUrl(data.id.toString(),"Share")
        }
        _binding!!.shop.setOnClickListener {
            if (data.cbActive) {
                GetUrl(data.id.toString(), "Shop")
            }else{
                requireContext().openCustomTab(data.url)
            }
        }


        return _binding?.root
    }

    private fun GetUrl(name: String, type: String) {
        var request = GenerateSubId(name)
        viewModel.generateUrl(request,requireContext(),object : ApiListener<GenerateSubIdRes>{
            override fun onSuccess(t: GenerateSubIdRes?, mess: String) {

                t!!.let {
                    if (it.status==200){
                        if (type.equals("Share")){
                            t!!.data.get(0).let {
                                requireContext().Share(name,t.data)
                            }
                        }else{
                            requireContext().openCustomTab(t!!.data)
                        }
                    }
                }

            }
        })


    }


    private fun searchOffersAndCoupons(query: String) {
        val request = SearchOfferReq(query)
        viewModel.searchOffersByStore(request, requireContext(), object : ApiListener<Offerlistres> {
            override fun onSuccess(t: Offerlistres?, mess: String) {
                t?.data?.let { data ->
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                        setupRecyclerView(data)  // Show only search results
                    }
                }
            }


        })
    }

    private fun setupRecyclerView(data : List<OffersData>) {
        val offersAdapter = OffersListAdapter<OffersData>(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                GetUrl(type, "Shop")
            }
        })

        binding.offers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = offersAdapter
        }
        offersAdapter.addAllItem(data)
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            StoreDetailFragment().apply {

            }
    }
}