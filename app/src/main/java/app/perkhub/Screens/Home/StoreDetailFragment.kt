package app.perkhub.Screens.Home

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
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
        applySafeAreaInsets()
        var data = Gson().fromJson<MiniApp>(requireActivity().intent.getStringExtra(DATA),MiniApp::class.java)
        Log.d("this is miniapp data",data.toString())
        if (data.cbActive){
            _binding!!.cashbackactiveSection.visibility = View.VISIBLE
        }else{
            _binding!!.cashbackactiveSection.visibility = View.GONE
        }
        _binding!!.storename.text = data.name
        _binding!!.link.setText(data.url)
        _binding!!.storenameterm.text = getString(R.string.complete_your_purchase,data.name)
        _binding!!.point1.text = getString(R.string.we_ll_redirect_you_to,data.name)
        _binding!!.shareearntitle.text = getString(R.string.generate_your_unique_referral_link,data.name)
        Glide.with(requireContext()).load(data.icon).into(_binding!!.icon)

        _binding!!.cashbacktext.text = Html.fromHtml(if (!data.cbPercentage.isNullOrEmpty()) "upto "+data.cbPercentage+" Cashback" else "" , Html.FROM_HTML_MODE_LEGACY)

        if (!data.cbActive){
            _binding!!.cashbackactiveSection.visibility = View.GONE
            _binding!!.cashbacktext.text =  "Cashback is not active"
//            _binding!!.cashbacktext.setTextColor(R.color.red)
//            _binding!!.cashbacktext.setBackgroundColor(R.color.red_100)

        }

        _binding!!.share.setOnClickListener {
            GetUrl(data.name.toString(),"Share")
        }
        _binding!!.shop.setOnClickListener {
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

            binding.mainlayout.setPadding(
                binding.mainlayout.paddingLeft,
                systemBars.top,
                binding.mainlayout.paddingRight,
                binding.mainlayout.paddingBottom
            )

            // Apply bottom inset to recycler or main scrollable container
//            binding.buttonLayout.setPadding(
//                binding.buttonLayout.paddingLeft,
//                binding.buttonLayout.paddingTop,
//                binding.buttonLayout.paddingRight,
//                systemBars.bottom
//            )

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