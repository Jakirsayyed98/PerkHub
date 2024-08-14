package app.perkhub.Screens.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import app.perkhub.R
import app.perkhub.Screens.Home.Adapter.MiniAppCategoryAdapter
import app.perkhub.Screens.Home.Model.Home.Category
import app.perkhub.Screens.Home.Model.Home.MiniApp
import app.perkhub.Screens.Home.Model.LootProduct.LootProductRes
import app.perkhub.Screens.Home.Model.MiniAppByCategory.MiniAppbyCategory
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.DATA
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.Utils.openCustomTab
import app.perkhub.Utils.toast
import app.perkhub.api.ApiListener
import app.perkhub.databinding.FragmentSelectedCategoryBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson


class SelectedCategoryFragment : BaseFragment<FragmentSelectedCategoryBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSelectedCategoryBinding.inflate(layoutInflater, container, false)
        _binding!!.toolbar.backbtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        val value = requireActivity().intent.getStringExtra(DATA)
        if (Gson().fromJson<Category>(value, Category::class.java)!=null) {

            val data = Gson().fromJson<Category>(value, Category::class.java)
            _binding!!.toolbar.name.text = data.name
            _binding!!.searchLayout.visibility = View.GONE
            getListOfMiniApps(data.id)
        } else {
            _binding!!.searchLayout.visibility = View.VISIBLE
            _binding!!.toolbar.name.text = "Search"
        }

        _binding!!.search.addTextChangedListener {
            SearchMiniApps(it.toString())
        }
        return _binding?.root
    }



    private fun SearchMiniApps(name: String) {
        viewModel.searchminiApps(
            getUserID()!!,
            name,
            object : ApiListener<MiniAppbyCategory> {
                override fun onSuccess(t: MiniAppbyCategory?, mess: String) {
                    t!!.let {
                        setLayout(it.data)
                    }
                }
            })

    }

    private fun getListOfMiniApps(id: Int) {
        viewModel.getMiniAppByCategory(
            getUserID()!!,
            id.toString(),
            object : ApiListener<MiniAppbyCategory> {
                override fun onSuccess(t: MiniAppbyCategory?, mess: String) {
                    t!!.let {
                        setLayout(it.data)
                    }
                }
            })

    }

    private fun setLayout(data: List<MiniApp>) {
        var mAdapter = MiniAppCategoryAdapter<MiniApp>(object : RecyclerClickListener {
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                requireContext().openCustomTab(type)
            }
        })
        _binding!!.listOfStores.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = mAdapter
        }
        mAdapter.clear()
        mAdapter.notifyDataSetChanged()
        mAdapter.addAllItem(data)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            SelectedCategoryFragment().apply {

            }
    }
}