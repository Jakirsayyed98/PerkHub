package app.perkhub.Screens.Profile

import android.app.DatePickerDialog
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import app.perkhub.ContainerActivity
import app.perkhub.R
import app.perkhub.Screens.login.models.LoginRes
import app.perkhub.Screens.login.models.SaveUserDetail
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.LOGIN
import app.perkhub.Utils.Snackbar
import app.perkhub.Utils.USERDATA
import app.perkhub.api.ApiListener
import app.perkhub.api.BaseRes
import app.perkhub.databinding.FragmentEditPersonalInfoBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditPersonalInfoFragment : BaseFragment<FragmentEditPersonalInfoBinding>() {

    private var gender = "male"
    private var isRequestInFlight = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEditPersonalInfoBinding.inflate(layoutInflater, container, false)
        statusBarColor(R.color.white)
        setStatusBarIconsDark(true)
        _binding!!.toolbar.name.text = "Edit Profile"
        _binding!!.toolbar.backbtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        AutoFillForm()
        setupForm()
        setupClickListeners()
        return _binding?.root
    }

    private fun AutoFillForm() {
        val userName = getSharedpref().getUser(USERDATA)?.data
        _binding!!.Profilename.text = userName?.name!!.replaceFirstChar { it.uppercase() }.orEmpty()
        _binding!!.email.text = userName.email
        _binding!!.etFullName.setText(userName.name)
        _binding!!.etEmail.setText(userName.email)
        _binding!!.etDob.setText(userName.dob)
        _binding!!.number.text = "+91 " + userName.number
        _binding!!.ProfilenameShort.text = userName.email.toCharArray()[0].toString().uppercase()

        when (userName.gender) {
            "male" -> selectGender(0)
            "female" -> selectGender(1)
            "other" -> selectGender(2)
        }
    }

    private fun setupForm() {

        val calendar = Calendar.getInstance()

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, month, day ->

                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)

                val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.US)
                binding.etDob.setText(sdf.format(calendar.time))
            }

        binding.etEmail.addTextChangedListener {

            if (it != null && (it.length < 15 || !it.endsWith("@gmail.com"))) {
                binding.etEmail.error = "Please Enter Valid Email Id"
            }
        }

        binding.etDob.setOnClickListener {

            val datePickerDialog = DatePickerDialog(
                requireActivity(),
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

// Maximum selectable DOB = Today - 18 Years
            val maxDate = Calendar.getInstance().apply {
                add(Calendar.YEAR, -18)
            }

            datePickerDialog.datePicker.maxDate = maxDate.timeInMillis

            datePickerDialog.show()
        }
    }

    private fun selectGender(type: Int) {

        val selectedBg =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_gender_selected)

        val normalBg =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_gender_container)

        val selectedColor =
            ContextCompat.getColor(requireContext(), R.color.colorPrimary)

        val normalColor =
            ContextCompat.getColor(requireContext(), R.color.gray_600)

        // Reset Backgrounds
        binding.layoutMale.background = normalBg
        binding.layoutFemale.background = normalBg
        binding.layoutOther.background = normalBg

        // Reset Text Colors
        binding.txtMale.setTextColor(normalColor)
        binding.txtFemale.setTextColor(normalColor)
        binding.txtOther.setTextColor(normalColor)

        // Reset Icon Colors
        ImageViewCompat.setImageTintList(
            binding.imgMale,
            ColorStateList.valueOf(normalColor)
        )

        ImageViewCompat.setImageTintList(
            binding.imgFemale,
            ColorStateList.valueOf(normalColor)
        )

        ImageViewCompat.setImageTintList(
            binding.imgOther,
            ColorStateList.valueOf(normalColor)
        )

        when (type) {

            0 -> {
                binding.layoutMale.background = selectedBg
                binding.txtMale.setTextColor(selectedColor)

                ImageViewCompat.setImageTintList(
                    binding.imgMale,
                    ColorStateList.valueOf(selectedColor)
                )
            }

            1 -> {
                binding.layoutFemale.background = selectedBg
                binding.txtFemale.setTextColor(selectedColor)

                ImageViewCompat.setImageTintList(
                    binding.imgFemale,
                    ColorStateList.valueOf(selectedColor)
                )
            }

            2 -> {
                binding.layoutOther.background = selectedBg
                binding.txtOther.setTextColor(selectedColor)

                ImageViewCompat.setImageTintList(
                    binding.imgOther,
                    ColorStateList.valueOf(selectedColor)
                )
            }
        }
    }


    private fun saveUserDetails() {
        setLoading(true)
        val request = SaveUserDetail(
            binding.etFullName.text.toString().trim(),
            binding.etEmail.text.toString().trim(),
            gender,
            binding.etDob.text.toString()
        )

        viewModel.saveDetail(request, requireContext(), object : ApiListener<BaseRes> {
            override fun onSuccess(t: BaseRes?, mess: String) {
                lifecycleScope.launch(Dispatchers.Main) {
                    getUserDetail()
                }
            }

            override fun onError(mess: String) {
                setLoading(false)
            }
        })
    }

    private fun getUserDetail() {

        viewModel.getUserDetail(requireContext(), object : ApiListener<LoginRes> {

            override fun onSuccess(t: LoginRes?, mess: String) {

                requireActivity().runOnUiThread {
                    t?.let {
                        getSharedpref().saveUser(USERDATA, it)
                        setLoading(false)
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }

            }

            override fun onError(mess: String) {
                super.onError(mess)
                setLoading(false)
            }
        })
    }


    private fun setupClickListeners() {

        binding.layoutMale.setOnClickListener {
            gender = "male"
            selectGender(0)
        }

        binding.layoutFemale.setOnClickListener {
            gender = "female"
            selectGender(1)
        }

        binding.layoutOther.setOnClickListener {
            gender = "other"
            selectGender(2)
        }

        binding.btnContinue.setOnClickListener {
            val name = binding.etFullName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val dob = binding.etDob.text.toString().trim()

            when {

                name.isEmpty() -> {

                    binding.etFullName.error = "Please Enter Name"
                    binding.etFullName.requestFocus()
                }

                email.isEmpty() -> {
                    binding.etEmail.error = "Please Enter Email Id"
                    binding.etEmail.requestFocus()
                }

                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    binding.etEmail.error = "Please Enter Valid Email Id"
                    binding.etEmail.requestFocus()
                }

                dob.isEmpty() -> {
                    binding.etDob.error = "Please Select Date of Birth"
                    binding.etDob.requestFocus()
                }

                !isAge18OrAbove(dob) -> {
                    binding.etDob.error = "You must be at least 18 years old"
                    binding.etDob.requestFocus()
                }

                else -> {
                    if (!binding.btnContinue.isEnabled)
                        return@setOnClickListener

                    // Prevent multiple clicks
                    binding.btnContinue.isEnabled = false
                    saveUserDetails()
                }
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        isRequestInFlight = loading
        if (_binding == null) return
        binding.btnContinue.isEnabled = !loading
        binding.btnContinue.alpha = if (loading) 0.6f else 1f

        // Verify Button UI
        binding.progressVerify.visibility =
            if (loading) View.VISIBLE else View.GONE

        binding.txtContinue.visibility =
            if (loading) View.INVISIBLE else View.VISIBLE
    }

    private fun isAge18OrAbove(dob: String): Boolean {

        return try {

            val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.US)
            sdf.isLenient = false

            val dobDate = sdf.parse(dob) ?: return false

            val dobCalendar = Calendar.getInstance().apply {
                time = dobDate
            }

            val today = Calendar.getInstance()

            var age = today.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR)

            if (today.get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
                age--
            }

            age >= 18

        } catch (e: Exception) {
            false
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            EditPersonalInfoFragment().apply {

            }
    }
}