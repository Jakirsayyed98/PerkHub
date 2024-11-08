package app.perkhub.Screens.login

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import app.perkhub.ContainerActivity
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.NUMBER
import app.perkhub.Utils.toast
import app.perkhub.api.ApiListener
import app.perkhub.databinding.FragmentEnterDetailBinding
import app.perkhub.Screens.login.models.LoginRes
import app.perkhub.Utils.LOGIN
import app.perkhub.Utils.USERDATA
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class EnterDetailFragment : BaseFragment<FragmentEnterDetailBinding>() {

    var gender = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEnterDetailBinding.inflate(layoutInflater, container, false)

        FormHandel()
        clickEvent()

        return _binding?.root
    }

    private fun clickEvent() {
        _binding!!.detail.setOnClickListener {
            if (_binding!!.detail.isSelected == true) {
                if (_binding!!.name.text.toString().isEmpty()) {
                    _binding!!.name.setError("Please Enter name")
                } else if (_binding!!.emailid.text.toString().isEmpty()) {
                    _binding!!.emailid.setError("Please Enter Email id ")
                } else if ( _binding!!.emailid.text.toString().endsWith("@gmail.com").not()) {
                    _binding!!.emailid.error = "Please Enter valid Email Id "
                } else {
                    saveAllDetail()
                }

            }
        }
    }

    private fun saveAllDetail() {
        var name = _binding!!.name.text.toString()
        var email = _binding!!.emailid.text.toString()
        var dob = _binding!!.DOB.text.toString()
        var gender1 = gender
        var number = getSharedpref().getString(NUMBER)!!
        showProgress()

        viewModel.SaveDetail(name, number, email, dob, gender1,requireContext(), object : ApiListener<LoginRes> {
            override fun onSuccess(t: LoginRes?, mess: String) {
                dissmissProgress()
                getSharedpref().saveBool(LOGIN, true)
                getSharedpref().saveUser(USERDATA, t!!)

                ContainerActivity.OpenContainer(requireContext(), "HomeScreenFragment", null)
                activity?.finish()
            }

            override fun onError(mess: String) {
                super.onError(mess)
                dissmissProgress()
                requireContext().toast(mess)
            }
        })
    }


    private fun FormHandel() {
        var cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "dd/MM/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)

                _binding!!.DOB.text = sdf.format(cal.time)
//            DOB.text = sdf.format(cal.time)

            }

        with(_binding!!) {

            emailid.addTextChangedListener {
                if (it!!.length >= 15 && it.endsWith("@gmail.com")) {
                } else {
                    _binding!!.emailid.error = "Please Enter valid Email id "
                }
            }

            DOB.setOnClickListener {
                _binding!!.detail.isSelected = true
                DatePickerDialog(
                    requireActivity(), dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()

            }
            gender.setOnClickListener {
                onGenderButtonClicked(it)
            }
        }


    }


    fun onGenderButtonClicked(view: View?) {
        if (_binding!!.male.isPressed()) {
            _binding!!.male.setEnabled(false)

            gender = "0"
        } else if (_binding!!.female.isPressed()) {
            _binding!!.female.setEnabled(false)

            gender = "1"
        } else if (_binding!!.other.isPressed()) {
            _binding!!.other.setEnabled(false)

            gender = "2"
        } else {
            gender = "0"
        }


    }

    companion object {

        @JvmStatic
        fun newInstance() =
            EnterDetailFragment().apply {

            }
    }
}