package app.perkhub.Screens.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import app.perkhub.R
import app.perkhub.Screens.Home.Adapter.CategoryAdapter
import app.perkhub.Screens.Home.Adapter.PopularStoresAdapter
import app.perkhub.Screens.Home.Model.Home.Category
import app.perkhub.Screens.Home.Model.Home.HomeRes
import app.perkhub.Screens.Home.Model.Home.MiniApp
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.Utils.USERDATA
import app.perkhub.Utils.toast
import app.perkhub.api.ApiListener
import app.perkhub.databinding.FragmentHomeScreenBinding


class HomeScreenFragment : BaseFragment<FragmentHomeScreenBinding>() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =FragmentHomeScreenBinding.inflate(layoutInflater,container,false)


        getHomeData()

        return _binding?.root
    }

    private fun getHomeData() {
        viewModel.getHomeData(getUserID()!!,object : ApiListener<HomeRes>{
            override fun onSuccess(t: HomeRes?, mess: String) {
                if (t!!.errorCode.equals("0")){
                    t.data.get(0).let {
                        setMainCategoryLayout(it.categories)
                        setPopularStore(it.popular)
                    }
                }
            }

            override fun onError(mess: String) {
                super.onError(mess)

            }
        })
    }

    private fun setPopularStore(popular: java.util.ArrayList<MiniApp>) {
        val mAdapter = PopularStoresAdapter<MiniApp>(object :RecyclerClickListener{
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                requireContext().toast(popular.get(posi).name)
            }
        })
        _binding!!.popularstore.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = mAdapter
        }
        popular.shuffled()
        mAdapter.addAllItem(if (popular.size<=12) popular else popular.subList(0,12))
    }

    private fun setMainCategoryLayout(categories: ArrayList<Category>) {
        val mAdapter = CategoryAdapter<Category>(object : RecyclerClickListener{
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {

            }

        })
        _binding!!.categories.apply {
            layoutManager = GridLayoutManager(requireContext(),4)
            adapter = mAdapter
        }
        mAdapter.addAllItem(if(categories.size<=8) categories else categories.subList(0,8))
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            HomeScreenFragment().apply {

            }
    }
}