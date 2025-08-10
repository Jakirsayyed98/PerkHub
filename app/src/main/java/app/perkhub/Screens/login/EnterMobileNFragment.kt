package app.perkhub.Screens.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import app.perkhub.ContainerActivity
import app.perkhub.Screens.Home.Model.Auth.VerifyOTP
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.LOGIN
import app.perkhub.Utils.toast
import app.perkhub.api.ApiListener
import app.perkhub.databinding.FragmentEnterMobileNBinding
import app.perkhub.Screens.login.models.SendOtpReq
import app.perkhub.Screens.login.models.VerifyOtpReq
import app.perkhub.Utils.USERDATA
import app.perkhub.api.BaseRes
import app.perkhub.api.tokens.TokenManager
import java.util.Timer
import java.util.TimerTask


class EnterMobileNFragment : BaseFragment<FragmentEnterMobileNBinding>() {

    val countdownTimeInSeconds = 120
    var timeLeft = countdownTimeInSeconds
    var countMinute = countdownTimeInSeconds / 60
    val timer = Timer()

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
        _binding = FragmentEnterMobileNBinding.inflate(layoutInflater, container, false)

        clickEvent()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (_binding!!.OTPlayout.isVisible) {
                _binding!!.mobilenumberlayout.visibility = View.VISIBLE
                _binding!!.OTPlayout.visibility = View.GONE
                timer.cancel()
            } else {
                timer.cancel()
                requireActivity().supportFragmentManager.popBackStack() // Handle parent fragment stack
            }
        }

        return _binding?.root
    }

    private fun clickEvent() {
        _binding!!.mobilenumber.addTextChangedListener {
            if (it!!.length == 10) {
                _binding!!.sendOTP.isSelected = true
            } else {
                _binding!!.sendOTP.isSelected = false
            }
        }
        _binding!!.sendOTP.setOnClickListener {
            if (_binding!!.sendOTP.isSelected) {
                showProgress()
                var request: SendOtpReq = SendOtpReq("")
                request.Number = _binding!!.mobilenumber.text.toString()

                _binding!!.mobilenumberlayout.visibility = View.GONE
                _binding!!.OTPlayout.visibility = View.VISIBLE

                viewModel.SendOTP(request,requireContext(),
                    object : ApiListener<BaseRes> {
                        override fun onSuccess(t: BaseRes?, mess: String) {
                            dissmissProgress()
                            countDownStart()
                        }

                        override fun onError(mess: String) {
                            super.onError(mess)
                            dissmissProgress()
                            requireContext().toast(mess)
                        }
                    })

            }
        }

        _binding!!.OTP.addTextChangedListener {
            if (_binding!!.OTP.length() == 6) {
                _binding!!.verifyOTP.isSelected = true
            } else {
                _binding!!.verifyOTP.isSelected = false
            }
        }

        _binding!!.verifyOTP.setOnClickListener {
            if (_binding!!.verifyOTP.isSelected) {
                showProgress()

                var request: VerifyOtpReq = VerifyOtpReq("","")
                request.Number = _binding!!.mobilenumber.text.toString()
                request.Otp = _binding!!.OTP.text.toString()
                viewModel.VerifyOTP(
                    request,requireContext(),
                    object : ApiListener<VerifyOTP> {
                        override fun onSuccess(t: VerifyOTP?, mess: String) {

                            if (t!!.errorCode == "00"){
                                dissmissProgress()
                                requireContext().toast(t.errorMsg)
                                TokenManager.saveToken(requireContext(), t.data!!.token!!)

                                SetRedirectionToDetail(t.data.isNewUser)
                            }else{
                                dissmissProgress()
                                requireContext().toast(mess)
                            }

                        }

                        override fun onError(mess: String) {
                            super.onError(mess)
                            dissmissProgress()
                            requireContext().toast(mess)
                        }

                    })
            }
        }

        _binding!!.timerCount.setOnClickListener {
            if (_binding!!.timerCount.text == "Resend") {
                countDownStart()
            }
        }


    }

    private fun SetRedirectionToDetail(isNewUser :Boolean) {
        timer.cancel()
        if (isNewUser) {
            ContainerActivity.OpenContainer(
                requireContext(),
                "EnterDetailFragment",
                null
            )
            activity?.finish()
        } else {
            getSharedpref().saveBool(LOGIN, true)
//            getSharedpref().saveUser(USERDATA, t)
            ContainerActivity.OpenContainer(
                requireContext(),
                "HomeScreenFragment",
                null
            )
            activity?.finish()
        }
    }

    private fun countDownStart() {

        timer.schedule(object : TimerTask() {
            @SuppressLint("DefaultLocale")
            override fun run() {
                if (timeLeft > 0) {
                    val minutes = timeLeft / 60
                    val seconds = timeLeft % 60

                    val formattedTime = String.format("%02d:%02d", minutes, seconds)
                    requireActivity().runOnUiThread {
                        _binding?.timerCount?.text = formattedTime
                    }

                    timeLeft--
                } else {
                    timer.cancel()
                    _binding!!.timerCount.setText("Resend")
                }
            }

        }, 0, 1000) // Run every 1 second
    }


    companion object {

        @JvmStatic
        fun newInstance() =
            EnterMobileNFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}