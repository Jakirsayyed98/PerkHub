package app.perkhub.Screens.Home

import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import app.perkhub.R
import app.perkhub.Screens.Home.Model.GenerateUrl.GenerateSubId
import app.perkhub.Screens.Home.Model.GenerateUrl.GenerateSubIdRes
import app.perkhub.Screens.Home.Model.Home.MiniApp
import app.perkhub.Screens.Home.Model.MiniAppByCategory.MiniAppbyCategory
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.CONTAINER_TYPE_KEY1
import app.perkhub.Utils.DATA
import app.perkhub.Utils.Share
import app.perkhub.Utils.openCustomTab
import app.perkhub.api.ApiListener
import app.perkhub.databinding.FragmentStoreDetailBinding
import com.bumptech.glide.Glide
import com.google.gson.Gson


class StoreDetailFragment : BaseFragment<FragmentStoreDetailBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStoreDetailBinding.inflate(layoutInflater,container,false)

        _binding!!.toolbar.backbtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        applySafeAreaInsets()
        var data = Gson().fromJson<MiniApp>(requireActivity().intent.getStringExtra(DATA),MiniApp::class.java)
        _binding!!.toolbar.name.text = data.name
        _binding!!.AboutTitle.text = getString(R.string.about_s,data.name)
        Glide.with(requireContext()).load(data.icon).into(_binding!!.logo)
        _binding!!.AboutStore.text = Html.fromHtml(data.about!!, Html.FROM_HTML_MODE_LEGACY)
//        _binding!!.Cashbackrates.text = Html.fromHtml(if (!data.cashbackRates.isNullOrEmpty()) data.cashbackRates else "" , Html.FROM_HTML_MODE_LEGACY)
//        _binding!!.terms.text = Html.fromHtml(data.cashbackTerms!!, Html.FROM_HTML_MODE_LEGACY)
//        _binding!!.description.text = Html.fromHtml(data.description!!, Html.FROM_HTML_MODE_LEGACY)
//        _binding!!.working.text = Html.fromHtml(data.howitsWork, Html.FROM_HTML_MODE_LEGACY)

        _binding!!.Share.setOnClickListener {
            GetUrl(data.name.toString(),"Share")
        }
        _binding!!.Shop.setOnClickListener {
            GetUrl(data.name.toString(),"Shop")
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

    private fun applySafeAreaInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(_binding!!.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Apply top inset to toolbar (so it won’t overlap status bar)

            binding.toolbar2.setPadding(
                binding.toolbar2.paddingLeft,
                systemBars.top,
                binding.toolbar2.paddingRight,
                binding.toolbar2.paddingBottom
            )

            // Apply bottom inset to recycler or main scrollable container
            binding.buttonLayout.setPadding(
                binding.buttonLayout.paddingLeft,
                binding.buttonLayout.paddingTop,
                binding.buttonLayout.paddingRight,
                systemBars.bottom
            )

            // You do NOT pad the whole root drawerLayout, only the children
            insets
        }
    }



    companion object {
        @JvmStatic
        fun newInstance() =
            StoreDetailFragment().apply {

            }
    }
}