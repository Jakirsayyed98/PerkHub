package app.perkhub.Screens.Profile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import app.perkhub.ContainerActivity
import app.perkhub.R
import app.perkhub.Screens.Profile.model.CreateNewTicketRequest
import app.perkhub.Screens.Profile.model.CreateNewTicketResponse
import app.perkhub.Screens.Profile.model.Data
import app.perkhub.Utils.BaseFragment
import app.perkhub.api.ApiListener
import app.perkhub.databinding.FragmentNewTicketBinding

class NewTicketFragment : BaseFragment<FragmentNewTicketBinding>() {

    private var selectedCategory: String? = null
    private var selectedPriority: String = "Low" // default

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewTicketBinding.inflate(layoutInflater, container, false)
        binding.toolbar.name.text = "Create New Ticket"

        setupCategorySpinner()
        setupPrioritySelection()
        setupTextWatchers()
        updateButtonState()

        binding.createTicketBtn.setOnClickListener {
            // TODO: Submit API logic when all fields are filled
        }

        return binding.root
    }

    private fun setupCategorySpinner() {
        val items = arrayOf(
            "Cashback Issues",
            "Withdrawal Issues",
            "Account Problems",
            "Games",
            "Store Issues",
            "Technical Support",
            "Other"
        )

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            items
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.category.adapter = adapter

        binding.category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedCategory = items[position]
                updateButtonState()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedCategory = null
                updateButtonState()
            }
        }
    }

    private fun setupPrioritySelection() {
        binding.priorityGroup.setOnCheckedChangeListener { _, checkedId ->
            val radio: RadioButton = binding.root.findViewById(checkedId)
            selectedPriority = radio.text.toString()
            updateButtonState()
        }
    }

    private fun setupTextWatchers() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.subject.addTextChangedListener(watcher)
        binding.description.addTextChangedListener(watcher)
    }

    private fun updateButtonState() {
        val subjectFilled = !binding.subject.text.isNullOrBlank()
        val descriptionFilled = !binding.description.text.isNullOrBlank()
        val categorySelected = !selectedCategory.isNullOrBlank()
        val allFilled = subjectFilled && descriptionFilled && categorySelected

        binding.createTicketBtn.isEnabled = allFilled

        val context = requireContext()
        val bg = if (allFilled)
            _binding!!.createTicketBtn.isSelected = true
        else
            _binding!!.createTicketBtn.isSelected = false

        binding.createTicketBtn.setOnClickListener {
            if (binding.createTicketBtn.isSelected){
                val subject = binding.subject.text.toString().trim()
                val description = binding.description.text.toString().trim()
                val category = selectedCategory ?: ""
                val priority = selectedPriority
                val requestody = CreateNewTicketRequest(subject,description,priority,category)
                createTicket(requestody)
            }
        }
    }

    private fun createTicket(requestody: CreateNewTicketRequest) {
        viewModel.createTicket(requestody,requireContext(),object :ApiListener<CreateNewTicketResponse>{
            override fun onSuccess(t: CreateNewTicketResponse?, mess: String) {
                val data = Data(t!!.data, requestody.subject,"open",requestody.priority,requestody.category,"","","")
                ContainerActivity.OpenContainer(requireContext(),"TicketMessageFragment",data)
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = NewTicketFragment()
    }
}
