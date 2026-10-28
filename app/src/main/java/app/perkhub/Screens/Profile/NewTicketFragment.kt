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
import app.perkhub.ContainerActivity
import app.perkhub.R
import app.perkhub.Screens.Home.Model.Home.MiniApp
import app.perkhub.Screens.Profile.model.CreateNewTicketRequest
import app.perkhub.Screens.Profile.model.CreateNewTicketResponse
import app.perkhub.Screens.Profile.model.Data
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.DATA
import app.perkhub.api.ApiListener
import app.perkhub.databinding.FragmentNewTicketBinding
import com.google.gson.Gson

class NewTicketFragment : BaseFragment<FragmentNewTicketBinding>() {

    private var selectedCategory: String? = null
    private var selectedPriority: String = "Medium"
    private var isSubmitting = false
    private var categoryInitialized = false

    private val categories = arrayOf(
        "Select a category",
        "Cashback Issues",
        "Withdrawal Issues",
        "Account Problems",
        "Games",
        "Store Issues",
        "Technical Support",
        "Other"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewTicketBinding.inflate(inflater, container, false)

        setupToolbar()
        setupCategorySpinner()
        setupPrioritySelection()
        setupTextWatchers()
        setupSubmitButton()
        updateButtonState()
        populateTicketData()

        return binding.root
    }

    private fun populateTicketData() {
        val json = requireActivity().intent?.getStringExtra(DATA) ?: return
        val data = runCatching {
            Gson().fromJson(json, CreateNewTicketRequest::class.java)
        }.getOrNull() ?: return
        binding.subject.setText(data.subject.orEmpty())
        binding.description.setText(data.body.orEmpty())
        val categoryIndex = categories.indexOf("Cashback Issues")
        if (categoryIndex >= 0 && binding.category.adapter != null) {
            binding.category.setSelection(categoryIndex, false)
            selectedCategory = categories[categoryIndex]
        }
    }

    private fun setupToolbar() {
        binding.toolbar.name.text = "Create Support Ticket"
    }

    private fun setupCategorySpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categories
        ).apply {
            setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
            )
        }

        binding.category.adapter = adapter
        binding.category.setSelection(0, false)

        binding.category.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (!categoryInitialized) {
                        categoryInitialized = true
                        selectedCategory = null
                    } else {
                        selectedCategory =
                            if (position == 0) null else categories[position]
                    }

                    updateButtonState()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    selectedCategory = null
                    updateButtonState()
                }
            }
    }

    private fun setupPrioritySelection() {
        binding.priorityGroup.check(R.id.medium)

        binding.priorityGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != View.NO_ID) {
                val radio = group.findViewById<RadioButton>(checkedId)
                selectedPriority = radio.text.toString()
                updatePriorityAppearance()
                updateButtonState()
            }
        }

        updatePriorityAppearance()
    }

    private fun updatePriorityAppearance() {
        val selectedId = binding.priorityGroup.checkedRadioButtonId

        binding.low.isSelected = binding.low.id == selectedId
        binding.medium.isSelected = binding.medium.id == selectedId
        binding.high.isSelected = binding.high.id == selectedId
    }

    private fun setupTextWatchers() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) = Unit

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                updateButtonState()
            }

            override fun afterTextChanged(s: Editable?) = Unit
        }

        binding.subject.addTextChangedListener(watcher)
        binding.description.addTextChangedListener(watcher)
    }

    private fun setupSubmitButton() {
        binding.createTicketBtn.setOnClickListener {
            if (isSubmitting) return@setOnClickListener

            val subject = binding.subject.text?.toString()?.trim().orEmpty()
            val description =
                binding.description.text?.toString()?.trim().orEmpty()
            val category = selectedCategory

            when {
                subject.isBlank() -> {
                    binding.subject.error = null
                    binding.subject.requestFocus()
                    binding.subject.error = "Enter a subject"
                    binding.subject.requestFocus()
                }

                category.isNullOrBlank() -> {
                    binding.category.requestFocus()
                }

                description.isBlank() -> {
                    binding.description.error = null
                    binding.description.requestFocus()
                    binding.description.error = "Enter a description"
                    binding.description.requestFocus()
                }

                description.length > 500 -> {
                    binding.description.error = null
                    binding.description.requestFocus()
                    binding.description.error =
                        "Description cannot exceed 500 characters"
                }

                else -> {
                    binding.subject.error = null
                    binding.description.error = null

                    val request = CreateNewTicketRequest(
                        subject,
                        description,
                        selectedPriority,
                        category
                    )

                    isSubmitting = true
                    updateButtonState()
                    createTicket(request)
                }
            }
        }
    }

    private fun updateButtonState() {
        if (_binding == null) return

        val subjectFilled =
            !binding.subject.text.isNullOrBlank()

        val descriptionFilled =
            !binding.description.text.isNullOrBlank()

        val categorySelected =
            !selectedCategory.isNullOrBlank()

        val allFilled =
            subjectFilled && descriptionFilled && categorySelected

        binding.createTicketBtn.isEnabled =
            allFilled && !isSubmitting

        binding.createTicketBtn.alpha =
            if (binding.createTicketBtn.isEnabled) 1f else 0.55f
    }

    private fun createTicket(request: CreateNewTicketRequest) {
        viewModel.createTicket(
            request,
            requireContext(),
            object : ApiListener<CreateNewTicketResponse> {

                override fun onSuccess(
                    t: CreateNewTicketResponse?,
                    mess: String
                ) {
                    if (_binding == null) return

                    val response = t
                    if (response?.data == null) {
                        isSubmitting = false
                        updateButtonState()
                        return
                    }

                    val data = Data(
                        response.data,
                        request.subject,
                        "open",
                        request.priority,
                        request.category,
                        "",
                        "",
                        ""
                    )

                    ContainerActivity.OpenContainer(
                        requireContext(),
                        "TicketMessageFragment",
                        data
                    )
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = NewTicketFragment()
    }
}
