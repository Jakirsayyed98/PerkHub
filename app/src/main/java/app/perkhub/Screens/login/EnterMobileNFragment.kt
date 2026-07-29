package app.perkhub.Screens.login

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.method.DigitsKeyListener
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import app.perkhub.ContainerActivity
import app.perkhub.R
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.PRIVACYPOLICY
import app.perkhub.Utils.VERIFY_OTP_FRAGMENT
import app.perkhub.Utils.hideKeyboard
import app.perkhub.Utils.openCustomTab
import app.perkhub.databinding.FragmentEnterMobileNBinding

class EnterMobileNFragment : BaseFragment<FragmentEnterMobileNBinding>() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEnterMobileNBinding.inflate(inflater, container, false)

        statusBarColor(R.color.white)
        setStatusBarIconsDark(true)

        setupTerms()
        setupClickListeners()
        handleBackPress()

        updateContinueButtonState()

        return binding.root
    }

    private fun setupClickListeners() {

        // Allow digits only
        binding.mobilenumber.keyListener =
            DigitsKeyListener.getInstance("0123456789")

        binding.mobilenumber.addTextChangedListener {
            updateContinueButtonState()
        }

        binding.checkTerms.setOnCheckedChangeListener { _, _ ->
            updateContinueButtonState()
        }

        binding.sendOTP.setOnClickListener {

            if (!binding.sendOTP.isEnabled)
                return@setOnClickListener

            // Prevent multiple clicks
            binding.sendOTP.isEnabled = false

            ContainerActivity.OpenContainer(
                requireContext(),
                VERIFY_OTP_FRAGMENT,
                binding.mobilenumber.text.toString().trim()
            )
        }
    }

    /**
     * Enable Continue button only when:
     * 1. Mobile number is valid
     * 2. User has accepted Terms
     */
    private fun updateContinueButtonState() {

        val enabled = isValidMobile() &&
                binding.checkTerms.isChecked

        binding.sendOTP.isEnabled = enabled
        binding.sendOTP.isSelected = enabled
        binding.sendOTP.alpha = if (enabled) 1f else 0.5f
    }

    /**
     * Indian Mobile Validation
     */
    private fun isValidMobile(): Boolean {

        val mobile = binding.mobilenumber.text
            .toString()
            .trim()

        return mobile.matches(Regex("^[6-9]\\d{9}$"))
    }

    private fun handleBackPress() {

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun setupTerms() {

        binding.rootLayout.setOnClickListener {
            requireActivity().hideKeyboard()
            binding.mobilenumber.clearFocus()
        }

        val text =
            "I agree to the Terms & Conditions and Privacy Policy."

        val spannable = SpannableString(text)

        val terms = "Terms & Conditions"
        val privacy = "Privacy Policy"

        val termsStart = text.indexOf(terms)
        val termsEnd = termsStart + terms.length

        val privacyStart = text.indexOf(privacy)
        val privacyEnd = privacyStart + privacy.length

        val brandColor = Color.parseColor("#47E1B5")

        // Terms
        spannable.setSpan(object : ClickableSpan() {

            override fun onClick(widget: View) {
                requireContext().openCustomTab(PRIVACYPOLICY)
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = brandColor
                ds.isUnderlineText = false
            }

        }, termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            termsStart,
            termsEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Privacy
        spannable.setSpan(object : ClickableSpan() {

            override fun onClick(widget: View) {
                requireContext().openCustomTab(PRIVACYPOLICY)
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = brandColor
                ds.isUnderlineText = false
            }

        }, privacyStart, privacyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            privacyStart,
            privacyEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvTerms.text = spannable
        binding.tvTerms.movementMethod = LinkMovementMethod.getInstance()
        binding.tvTerms.highlightColor = Color.TRANSPARENT
    }

    companion object {

        @JvmStatic
        fun newInstance() = EnterMobileNFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}