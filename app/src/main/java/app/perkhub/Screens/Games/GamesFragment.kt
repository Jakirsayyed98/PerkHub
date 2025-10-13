package app.perkhub.Screens.Games

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import app.perkhub.R
import app.perkhub.Screens.Games.adapter.PopularGameAdapter
import app.perkhub.Screens.Games.adapter.TrendingGameAdapter
import app.perkhub.Screens.Games.model.Data
import app.perkhub.Screens.Games.model.GamesRes
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.Utils.openCustomTab
import app.perkhub.Utils.toast
import app.perkhub.api.ApiListener
import app.perkhub.databinding.FragmentGamesBinding
import app.perkhub.room.games.GamesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class GamesFragment : BaseFragment<FragmentGamesBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentGamesBinding.inflate(layoutInflater,container,false)
        setToolBar()
        observePopularGames()
        return _binding?.root
    }

    private fun setToolBar() {
        binding.toolbar.name.text = "Games"
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.games_bg)

        binding.toolbar.backbtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.toolbar.toolbar.elevation = 0f
        binding.toolbar.name.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.toolbar.root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.games_bg))
    }

    private fun observePopularGames() {
        val repository = GamesRepository(requireContext())

        // CRITICAL FIX: Use viewLifecycleOwner instead of lifecycleScope
        // This prevents memory leaks when Fragment is destroyed
        viewLifecycleOwner.lifecycleScope.launch {
            repository.getGames().collect { games ->
                // This will be called automatically when:
                // 1. Fragment opens (shows cached data)
                // 2. Server data is fetched (shows fresh data)
                // 3. Push notification arrives (shows new notification)
                setPopularGamesLayout(games.shuffled())
                setTrendingGamesLayout(games.shuffled())
            }
        }

        // Trigger refresh from server in background
        fetchGamesList(forceRefresh = true)
    }

    private fun setPopularGamesLayout(games: List<Data>) {
        val mAdapter = PopularGameAdapter<Data>(object : RecyclerClickListener{
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                if (data is Data) requireContext().openCustomTab(data.url)
            }
        })

        binding.populargame.apply {
            layoutManager = GridLayoutManager(requireContext(), 3, GridLayoutManager.HORIZONTAL, false)
            adapter = mAdapter
        }

        mAdapter.addAllItem(if (games.size>20) games.subList(0,21) else games)
    }

    private fun setTrendingGamesLayout(games: List<Data>) {
        val mAdapter = TrendingGameAdapter<Data>(object : RecyclerClickListener{
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                if (data is Data) requireContext().openCustomTab(data.url)
            }
        })

        binding.trendinggame.apply {
            layoutManager = GridLayoutManager(requireContext(), 3, GridLayoutManager.HORIZONTAL, false)
            adapter = mAdapter
        }

        mAdapter.addAllItem(if (games.size>20) games.subList(0,21) else games)
    }

    private fun fetchGamesList(forceRefresh: Boolean = false) {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getGamesList(
                requireContext(),
                forceRefresh,
                object : ApiListener<GamesRes> {
                    override fun onSuccess(t: GamesRes?, mess: String) {
                        t?.data?.let {
                            lifecycleScope.launch(Dispatchers.Main) {
                                // Don't call setData here if using Flow
                                // The Flow will handle it automatically
                            }
                        }
                    }

                    override fun onError(mess: String) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            requireContext().toast("Failed to fetch notifications: $mess")
                        }
                    }
                })
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            GamesFragment().apply {
            }
    }
}