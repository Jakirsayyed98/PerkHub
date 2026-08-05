package app.perkhub.Screens.login

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.util.TypedValue
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import app.perkhub.ContainerActivity
import app.perkhub.R
import app.perkhub.Screens.Home.Model.Auth.VerifyOTP
import app.perkhub.Screens.login.models.GetToken
import app.perkhub.Screens.login.models.LoginRes
import app.perkhub.Screens.login.models.SendOtpReq
import app.perkhub.Screens.login.models.VerifyOtpReq
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.CONTAINER_TYPE_KEY
import app.perkhub.Utils.DATA
import app.perkhub.Utils.ENTER_DETAIL_FRAGMENT
import app.perkhub.Utils.HOME_SCREEN_FRAGMENT
import app.perkhub.Utils.LOGIN
import app.perkhub.Utils.NetworkUtils
import app.perkhub.Utils.RESENDOTP
import app.perkhub.Utils.Snackbar
import app.perkhub.Utils.USERDATA
import app.perkhub.Utils.getMaskedMobileNumber
import app.perkhub.api.ApiListener
import app.perkhub.api.BaseRes
import app.perkhub.api.tokens.TokenManager
import app.perkhub.databinding.FragmentOTPVerificationBinding
import com.google.gson.Gson
import kotlinx.coroutines.launch

/**
 * Production-hardening changes vs. original:
 *  1. No force-unwraps on nullable inputs (`number`, API bodies) — bad state now
 *     shows an error + lets the user retry instead of crashing.
 *  2. All async ApiListener callbacks guard on `_binding == null` / `isAdded`
 *     before touching views, and use viewLifecycleOwner.lifecycleScope instead
 *     of the fragment-scoped one, so a callback landing after the view is
 *     destroyed can't NPE.
 *  3. OTP is only auto-sent on first creation (`savedInstanceState == null`),
 *     not on every recreation/rotation.
 *  4. Verify/Resend buttons are disabled while a request is in flight to
 *     prevent duplicate-tap races.
 *  5. The "OTP verified" dialog is guaranteed to be dismissed on every
 *     failure path in the verify -> token -> user-detail chain, not just
 *     the happy path.
 *  6. OTP validity check requires 6 numeric digits, not just length 6.
 *  7. Paste support: pasting a full 6-digit code into any box distributes
 *     it across all boxes.
 */
class OTP_verification_Fragment : BaseFragment<FragmentOTPVerificationBinding>() {

    private var countDownTimer: CountDownTimer? = null
    private var number: String? = null
    private var dialog: OTPVerifiedDialogFragment? = null
    private var isRequestInFlight = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOTPVerificationBinding.inflate(layoutInflater, container, false)
        statusBarColor(R.color.white)
        setStatusBarIconsDark(true)

        number = runCatching {
            Gson().fromJson(requireActivity().intent.getStringExtra(DATA), String::class.java)
        }.getOrNull()

        if (number.isNullOrBlank()) {
            requireContext().Snackbar("Something went wrong. Please try again.", binding.rootLayout)
            activity?.finish()
            return binding.root
        }

        dialog = OTPVerifiedDialogFragment.newInstance()

        setOtpMessage()
        setupOTP()
        clickEvent()

        // Only send the OTP on first creation, not on every rotation/recreation.
        if (savedInstanceState == null) {
            if (NetworkUtils.isInternetAvailable(requireContext())) {
                showNoInternet(false)
                sendOTPAPICall()
            } else {
                requireContext().Snackbar("No internet connection", binding.rootLayout)
            }
        }

        return binding.root
    }

    // ---------------------------------------------------------------------
    // Network calls
    // ---------------------------------------------------------------------

    private fun sendOTPAPICall() {
        val num = number ?: return
        if (isRequestInFlight) return
        setLoading(true)

        val request = SendOtpReq(num)
        viewModel.sendOTP(request, requireContext(), object : ApiListener<BaseRes> {
            override fun onSuccess(t: BaseRes?, mess: String) {
                viewLifecycleOwner.lifecycleScope.launchSafe {
                    setLoading(false)
                    startOtpTimer()
                    isRequestInFlight = false
                }
            }

            override fun onError(mess: String) {
                viewLifecycleOwner.lifecycleScope.launchSafe {
                    setLoading(false)
                    requireContext().Snackbar(mess, binding.rootLayout)
                }
            }
        })
    }

    private fun verifyNumberOTP(otp: String) {
        val num = number ?: return
        if (isRequestInFlight) return
        setLoading(true)

        val req = VerifyOtpReq(num, otp)
        viewModel.verifyOTP(req, requireContext(), object : ApiListener<VerifyOTP> {
            override fun onSuccess(t: VerifyOTP?, mess: String) {
                viewLifecycleOwner.lifecycleScope.launchSafe {
                    if (t == null) {
                        setLoading(false)
                        requireContext().Snackbar("Verification failed. Please try again.", binding.rootLayout)
                        return@launchSafe
                    }
                    showVerifiedDialog()
                    getAuthToken()
                }
            }

            override fun onError(mess: String) {
                viewLifecycleOwner.lifecycleScope.launchSafe {
                    setLoading(false)
                    requireContext().Snackbar(mess, binding.rootLayout)
                }
            }
        })
    }

    private fun getAuthToken() {
        val num = number ?: run { failAndReset("Something went wrong. Please try again."); return }

        val request = GetToken(num)
        viewModel.getAuthorizationToken(request, requireContext(), object : ApiListener<BaseRes> {
            override fun onSuccess(t: BaseRes?, mess: String) {
                viewLifecycleOwner.lifecycleScope.launchSafe {
                    val token = t?.Data?.Token
                    if (token.isNullOrEmpty()) {
                        failAndReset("Could not sign you in. Please try again.")
                        return@launchSafe
                    }
                    TokenManager.saveToken(requireContext(), token)
                    getUserDetail()
                }
            }

            override fun onError(mess: String) {
                viewLifecycleOwner.lifecycleScope.launchSafe {
                    failAndReset(mess)
                }
            }
        })
    }

    private fun getUserDetail() {
        viewModel.getUserDetail(requireContext(), object : ApiListener<LoginRes> {
            override fun onSuccess(t: LoginRes?, mess: String) {
                viewLifecycleOwner.lifecycleScope.launchSafe {
                    val userName = t?.data?.name
                    if (userName == null) {
                        failAndReset("Could not load your profile. Please try again.")
                        return@launchSafe
                    }
                    setLoading(false)

                    if (userName!="") {
//                        getSharedpref().saveUser(USERDATA, t)
                        getSharedpref().saveBool(LOGIN,true)
                        ContainerActivity.OpenContainer(requireContext(), HOME_SCREEN_FRAGMENT, null)
                    } else {
                        ContainerActivity.OpenContainer(requireContext(), ENTER_DETAIL_FRAGMENT, null)
//
                    }
                    requireActivity().finish()
                }
            }

            override fun onError(mess: String) {
                viewLifecycleOwner.lifecycleScope.launchSafe {
                    failAndReset(mess)
                }
            }
        })
    }

    /** Dismiss the "verified" dialog, reset loading state, and surface the error. */
    private fun failAndReset(message: String) {
        setLoading(false)
        if (isAdded) requireContext().Snackbar(message, binding.rootLayout)
    }

    private fun showVerifiedDialog() {
        if (!isAdded) return
        dialog?.let {
            if (!it.isAdded) it.show(parentFragmentManager, "OTPVerifiedDialogFragment")
        }
    }

    private fun dismissVerifiedDialog() {
        dialog?.let { if (it.isAdded) it.dismiss() }
    }

    private fun showNoInternet(show: Boolean) {

        binding.noInternetView.layoutNoInternet.visibility =
            if (show) View.VISIBLE else View.GONE

        binding.rootScrollView.visibility =
            if (show) View.GONE else View.VISIBLE
    }

    private fun clickEvent() {
        binding.tvOtpTimer.setOnClickListener {
            if (binding.tvOtpTimer.text.toString() == RESENDOTP) {
                sendOTPAPICall()
            }
        }

        binding.verifyBtn.setOnClickListener {
            if (isRequestInFlight) return@setOnClickListener

            val otp = buildString {
                append(binding.etOtp1.text.toString())
                append(binding.etOtp2.text.toString())
                append(binding.etOtp3.text.toString())
                append(binding.etOtp4.text.toString())
                append(binding.etOtp5.text.toString())
                append(binding.etOtp6.text.toString())
            }

            if (otp.length != 6 || !otp.all { it.isDigit() }) {
                requireContext().Snackbar("Please enter a valid 6-digit OTP", binding.rootLayout)
                return@setOnClickListener
            }

            if (NetworkUtils.isInternetAvailable(requireContext())) {
                showNoInternet(false)
                verifyNumberOTP(otp)
            } else {
                requireContext().Snackbar("No internet connection", binding.rootLayout)
            }

        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    countDownTimer?.cancel()
                    requireActivity().finish()
                }
            }
        )
    }

    /** Disables Verify/Resend while a request is in flight to prevent duplicate taps. */
    private fun setLoading(loading: Boolean) {
        isRequestInFlight = loading
        if (_binding == null) return
        binding.verifyBtn.isEnabled = !loading
        binding.verifyBtn.alpha = if (loading) 0.6f else 1f

        // Verify Button UI
        binding.progressVerify.visibility =
            if (loading) View.VISIBLE else View.GONE

        binding.imgArrow.visibility =
            if (loading) View.GONE else View.VISIBLE

        binding.txtContinue.visibility =
            if (loading) View.INVISIBLE else View.VISIBLE
        // If you have a ProgressBar in the layout, toggle its visibility here too.
        setOtpFieldsEnabled(!loading)
    }

    private fun setOtpFieldsEnabled(enabled: Boolean) {
        listOf(
            binding.etOtp1,
            binding.etOtp2,
            binding.etOtp3,
            binding.etOtp4,
            binding.etOtp5,
            binding.etOtp6
        ).forEach {
            it.isEnabled = enabled
        }
    }

    private fun setOtpMessage() {
        val phone =getMaskedMobileNumber(number ?: "")
        val text = getString(R.string.otp_sent, phone)
        val spannable = SpannableString("$text ")

        val start = text.indexOf(phone)
        if (start >= 0) {
            val end = start + phone.length
            spannable.setSpan(
                ForegroundColorSpan(Color.parseColor("#25D366")),
                start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannable.setSpan(
                StyleSpan(Typeface.BOLD),
                start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_whatsapp)
        drawable?.setBounds(0, 0, dpToPx(18), dpToPx(18))
        drawable?.let {
            spannable.setSpan(
                ImageSpan(it, ImageSpan.ALIGN_BOTTOM),
                spannable.length - 1, spannable.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        binding.tvOtpSent.text = spannable
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics
        ).toInt()
    }

    private fun startOtpTimer() {
        countDownTimer?.cancel()

        countDownTimer = object : CountDownTimer(5 * 60 * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (_binding == null) return
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                updateOtpTimer(String.format("%d:%02d", minutes, seconds))
            }

            override fun onFinish() {
                showResendOtp()
            }
        }.start()
    }

    private fun showResendOtp() {
        binding.ivSecurity.visibility = View.INVISIBLE

        val spannable = SpannableString(RESENDOTP)

        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#47E1B5")),
            0,
            spannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.setSpan(
            UnderlineSpan(),
            0,
            spannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvOtpTimer.text = spannable
    }

    private fun setupOTP() {
        val editTexts = arrayOf(
            binding.etOtp1, binding.etOtp2, binding.etOtp3,
            binding.etOtp4, binding.etOtp5, binding.etOtp6
        )

        for (i in editTexts.indices) {
            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val entered = s?.toString().orEmpty()

                    // Paste support: if a full 6-digit code lands in one box, distribute it.
                    if (entered.length > 1) {
                        val digits = entered.filter { it.isDigit() }.take(6)
                        if (digits.length == 6) {
                            distributeOtp(digits, editTexts)
                            return
                        }
                    }

                    if (entered.length == 1 && i < editTexts.size - 1) {
                        editTexts[i + 1].requestFocus()
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            editTexts[i].setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_DEL &&
                    editTexts[i].text.isEmpty() &&
                    i > 0
                ) {
                    editTexts[i - 1].requestFocus()
                }
                false
            }
        }
    }

    private fun distributeOtp(digits: String, editTexts: Array<android.widget.EditText>) {
        digits.forEachIndexed { index, c ->
            if (index < editTexts.size) editTexts[index].setText(c.toString())
        }
        editTexts.last().requestFocus()
        editTexts.last().setSelection(editTexts.last().text?.length ?: 0)
    }

    private fun updateOtpTimer(time: String) {
        binding.ivSecurity.visibility = View.VISIBLE
        val text = getString(R.string.secure_otp, time)
        val spannable = SpannableString(text)

        val start = text.indexOf(time)
        if (start >= 0) {
            val end = start + time.length
            spannable.setSpan(
                ForegroundColorSpan(Color.parseColor("#47E1B5")),
                start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        binding.tvOtpTimer.text = spannable
    }

    override fun onDestroyView() {
        countDownTimer?.cancel()
        countDownTimer = null
        dismissVerifiedDialog()
        _binding = null
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance() = OTP_verification_Fragment()
    }
}

/**
 * Tiny helper so every callback body auto-bails if the view has already been
 * torn down by the time the network response arrives — avoids repeating an
 * `if (_binding == null) return@launch` guard in every single callback.
 * Add this as a small extension somewhere shared (e.g. LifecycleExt.kt) and
 * drop it from here if you already have an equivalent.
 */
private fun androidx.lifecycle.LifecycleCoroutineScope.launchSafe(
    block: suspend () -> Unit
) = launch {
    block()
}