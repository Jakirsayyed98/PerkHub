package app.perkhub.Screens.login

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import app.perkhub.ContainerActivity
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.LOGIN
import app.perkhub.Utils.USERDATA
import app.perkhub.Utils.toast
import app.perkhub.api.ApiListener
import app.perkhub.api.BaseRes
import app.perkhub.databinding.FragmentEnterDetailBinding
import app.perkhub.Screens.login.models.LoginRes
import app.perkhub.Screens.login.models.SaveUserDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EnterDetailFragment : BaseFragment<FragmentEnterDetailBinding>() {

    private var gender = "male"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnterDetailBinding.inflate(inflater, container, false)

        setupForm()
        setupClickListeners()

        return binding.root
    }

    private fun setupClickListeners() {
        binding.detail.setOnClickListener {
            if (binding.detail.isSelected) {
                val name = binding.name.text.toString()
                val email = binding.emailid.text.toString()

                when {
                    name.isEmpty() -> binding.name.error = "Please Enter name"
                    email.isEmpty() -> binding.emailid.error = "Please Enter Email id"
                    !email.endsWith("@gmail.com") -> binding.emailid.error = "Please Enter valid Email Id"
                    else -> saveUserDetails()
                }
            }
        }
    }

    private fun saveUserDetails() {
        val name = binding.name.text.toString()
        val email = binding.emailid.text.toString()
        val dob = binding.DOB.text.toString()
        val genderSelected = gender

        showProgress()
        val request = SaveUserDetail(name, email, genderSelected, dob)

        viewModel.saveDetail(request, requireContext(), object : ApiListener<BaseRes> {
            override fun onSuccess(t: BaseRes?, mess: String) {
                lifecycleScope.launch(Dispatchers.Main) {
                    dissmissProgress()
                    t?.Message?.let { requireContext().toast(it) }

                    getSharedpref().saveBool(LOGIN, true)
                    getUserDetail()
////                    getSharedpref().saveUser(USERDATA, t!!)
//                    ContainerActivity.OpenContainer(requireContext(), "HomeScreenFragment", null)
                    activity?.finish()
                }
            }

            override fun onError(mess: String) {
                lifecycleScope.launch(Dispatchers.Main) {
                    dissmissProgress()
                    requireContext().toast(mess)
                }
            }
        })
    }

    private fun getUserDetail() {
        viewModel.getUserDetail(requireContext(), object : ApiListener<LoginRes> {
            override fun onSuccess(t: LoginRes?, mess: String) {
                t!!.let {
                    getSharedpref().saveUser(USERDATA, t)
                    ContainerActivity.OpenContainer(requireContext(), "HomeScreenFragment", null)
                }
            }

            override fun onError(mess: String) {
                super.onError(mess)

            }
        })
    }

    private fun setupForm() {
        val cal = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.US)
            binding.DOB.text = sdf.format(cal.time)
        }

        binding.emailid.addTextChangedListener {
            if (it != null && (it.length < 15 || !it.endsWith("@gmail.com"))) {
                binding.emailid.error = "Please Enter valid Email id"
            }
        }

        binding.DOB.setOnClickListener {
            binding.detail.isSelected = true
            DatePickerDialog(
                requireActivity(),
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.gender.setOnClickListener { onGenderSelected(it) }
    }

    private fun onGenderSelected(view: View) {
        when (view.id) {
            binding.male.id -> gender = "male"
            binding.female.id -> gender = "female"
            binding.other.id -> gender = "other"
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = EnterDetailFragment()
    }
}
