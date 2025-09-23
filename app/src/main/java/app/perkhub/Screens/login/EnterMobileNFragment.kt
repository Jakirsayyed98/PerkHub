package app.perkhub.Screens.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import app.perkhub.ContainerActivity
import app.perkhub.R
import app.perkhub.Screens.Home.Model.Auth.VerifyOTP
import app.perkhub.Screens.login.models.GetToken
import app.perkhub.Screens.login.models.LoginRes
import app.perkhub.Screens.login.models.SendOtpReq
import app.perkhub.Screens.login.models.VerifyOtpReq
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.LOGIN
import app.perkhub.Utils.TOKEN
import app.perkhub.Utils.USERDATA
import app.perkhub.Utils.toast
import app.perkhub.api.ApiListener
import app.perkhub.api.BaseRes
import app.perkhub.api.tokens.TokenManager
import app.perkhub.databinding.FragmentEnterMobileNBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EnterMobileNFragment : BaseFragment<FragmentEnterMobileNBinding>() {

    private val countdownTimeInSeconds = 120
    private var timeLeft = countdownTimeInSeconds
    private var resend = false
    private var countdownJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnterMobileNBinding.inflate(inflater, container, false)

        setupClickListeners()
        handleBackPress()

        return binding.root
    }

    private fun setupClickListeners() {
        binding.mobilenumber.addTextChangedListener {
            binding.sendOTP.isSelected = it?.length == 10
        }

        binding.sendOTP.setOnClickListener {
            if (binding.sendOTP.isSelected) sendOTPAPICall()
        }

        binding.OTP.addTextChangedListener {
            binding.verifyOTP.isSelected = it?.length == 6
        }

        binding.verifyOTP.setOnClickListener {
            if (binding.verifyOTP.isSelected) verifyOTP()
        }

        binding.timerCount.setOnClickListener {
            if (resend) {
                resend = false
                startCountdown()
                sendOTPAPICall()
            }
        }
    }

    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (binding.OTPlayout.isVisible) {
                binding.mobilenumberlayout.visibility = View.VISIBLE
                binding.OTPlayout.visibility = View.GONE
                countdownJob?.cancel()
            } else {
                countdownJob?.cancel()
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    private fun sendOTPAPICall() {
        binding.sendOTP.isSelected = false
        binding.sendOtpText.text = "Sending..."
        val request = SendOtpReq(binding.mobilenumber.text.toString())
        val numberText = "+91 ${binding.mobilenumber.text}"

        binding.dis1.text = getString(R.string.enter_the_6_digit_code_sent_to, numberText)

        viewModel.sendOTP(request, requireContext(), object : ApiListener<BaseRes> {
            override fun onSuccess(t: BaseRes?, mess: String) {
                lifecycleScope.launch(Dispatchers.Main) {
                    binding.mobilenumberlayout.visibility = View.GONE
                    binding.OTPlayout.visibility = View.VISIBLE
                    startCountdown()
                }
            }

            override fun onError(mess: String) {
                lifecycleScope.launch(Dispatchers.Main) {
                    requireContext().toast(mess)
                }
            }
        })
    }

    private fun verifyOTP() {
        showProgress()
        val request = VerifyOtpReq(
            Number = binding.mobilenumber.text.toString(),
            Otp = binding.OTP.text.toString()
        )

        viewModel.verifyOTP(request, requireContext(), object : ApiListener<VerifyOTP> {
            override fun onSuccess(t: VerifyOTP?, mess: String) {
                lifecycleScope.launch(Dispatchers.Main) {
                    dissmissProgress()
                    countdownJob?.cancel()
                    redirectAfterVerification()
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

    private fun redirectAfterVerification() {
        val request = GetToken(binding.mobilenumber.text.toString())
        viewModel.getAuthorizationToken(request, requireContext(), object : ApiListener<BaseRes> {
            override fun onSuccess(t: BaseRes?, mess: String) {
                lifecycleScope.launch(Dispatchers.Main) {
                    t?.Data?.Token?.let { token ->
                        TokenManager.saveToken(requireContext(), token)
                        getSharedpref().saveString(TOKEN, token)
                        fetchUserDetails()
                    }
                }
            }

            override fun onError(mess: String) {
                lifecycleScope.launch(Dispatchers.Main) {
                    requireContext().toast(mess)
                }
            }
        })
    }

    private fun fetchUserDetails() {
        viewModel.getUserDetail(requireContext(), object : ApiListener<LoginRes> {
            override fun onSuccess(t: LoginRes?, mess: String) {
                lifecycleScope.launch(Dispatchers.Main) {
                    t?.let {
                        getSharedpref().saveUser(USERDATA, it)
                        if (it.data.name.isNotEmpty()) {
                            getSharedpref().saveBool(LOGIN, true)
                            ContainerActivity.OpenContainer(requireContext(), "HomeScreenFragment", null)
                        } else {
                            ContainerActivity.OpenContainer(requireContext(), "EnterDetailFragment", null)
                        }
                        activity?.finish()
                    }
                }
            }

            override fun onError(mess: String) {
                lifecycleScope.launch(Dispatchers.Main) {
                    requireContext().toast(mess)
                }
            }
        })
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        timeLeft = countdownTimeInSeconds

        countdownJob = lifecycleScope.launch(Dispatchers.Main) {
            while (timeLeft > 0) {
                val minutes = timeLeft / 60
                val seconds = timeLeft % 60
                binding.timerCount.text = "Resend OTP in %02d:%02d".format(minutes, seconds)
                delay(1000)
                timeLeft--
            }
            binding.timerCount.text = "Resend OTP"
            resend = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countdownJob?.cancel()
    }

    companion object {
        @JvmStatic
        fun newInstance() = EnterMobileNFragment()
    }
}
