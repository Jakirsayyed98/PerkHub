package app.perkhub.Screens.Games

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import app.perkhub.R
import app.perkhub.Screens.Games.adapter.SearchGameAdapter
import app.perkhub.Screens.Games.model.Data
import app.perkhub.Screens.Games.model.GamesRes
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.Utils.openCustomTab
import app.perkhub.Utils.toast
import app.perkhub.api.ApiListener
import app.perkhub.databinding.FragmentSearchGameBinding
import app.perkhub.room.games.GamesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchGameFragment : BaseFragment<FragmentSearchGameBinding>() {

    private lateinit var searchGameAdapter: SearchGameAdapter<Data>
    private var allGames: List<Data> = emptyList() // To hold all games

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSearchGameBinding.inflate(inflater,container,false)
        binding.toolbar.name.text = "Search Games"
        binding.toolbar.name.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.toolbar.root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.games_bg))
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.games_bg)

        binding.toolbar.backbtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        observeGames()
        setupSearch()
        return _binding?.root
    }

    private fun setupSearch() {
        // Add a TextWatcher to the EditText
        binding.search.doOnTextChanged { text, _, _, _ ->
            // Filter the adapter list based on the text
            searchGameAdapter.filter.filter(text)
            // Call a function to check and toggle the empty view
            toggleEmptyView(searchGameAdapter.getFilteredListSize())
        }
    }


    private fun observeGames() {
        val repository = GamesRepository(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            repository.getGames().collect { games ->
                allGames = games // Store the full list
                setSearchGame(games)
                toggleEmptyView(games.size) // Check visibility on initial load
            }
        }

        fetchGamesList(forceRefresh = true)
    }

    private fun toggleEmptyView(itemCount: Int) {
        if (itemCount == 0) {
            binding.games.visibility = View.GONE
            binding.tvNoGamesFound.visibility = View.VISIBLE
        } else {
            binding.games.visibility = View.VISIBLE
            binding.tvNoGamesFound.visibility = View.GONE
        }
    }


    // In SearchGameFragment.kt

    private fun setSearchGame(games: List<Data>) {
        // 1. Initialize the adapter
        searchGameAdapter = SearchGameAdapter<Data>(object :RecyclerClickListener{
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                if (data is Data) requireContext().openCustomTab(data.url)
            }
        })

        _binding!!.games.apply {
            layoutManager = GridLayoutManager(requireContext(),2)
            adapter = searchGameAdapter
        }

        // 2. IMPORTANT: Set the originalList in the adapter manually
        (searchGameAdapter as SearchGameAdapter<Data>).originalList = games.toList()

        // 3. Call the final method to update the displayed list
        searchGameAdapter.addAllItem(games)
    }

    private fun fetchGamesList(forceRefresh: Boolean = false) {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getGamesList(
                requireContext(),
                forceRefresh,
                object : ApiListener<GamesRes> {
                    override fun onSuccess(t: GamesRes?, mess: String) {}
                    override fun onError(mess: String) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            requireContext().toast("Failed to fetch games: $mess")
                        }
                    }
                })
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SearchGameFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}