package app.perkhub.login

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import app.perkhub.ContainerActivity
import app.perkhub.R
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.LOGIN
import app.perkhub.Utils.toast
import app.perkhub.api.ApiListener
import app.perkhub.databinding.FragmentEnterMobileNBinding
import app.perkhub.login.models.LoginRes
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
        _binding = FragmentEnterMobileNBinding.inflate(layoutInflater,container,false)

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
            if (it!!.length==10){
                _binding!!.sendOTP.isSelected = true
            }else{
                _binding!!.sendOTP.isSelected = false
            }
        }

        _binding!!.sendOTP.setOnClickListener {
            if (_binding!!.sendOTP.isSelected){
                showProgress()

                _binding!!.mobilenumberlayout.visibility = View.GONE
                _binding!!.OTPlayout.visibility = View.VISIBLE
                viewModel.SendOTP(_binding!!.mobilenumber.text.toString(),object :ApiListener<LoginRes>{
                    override fun onSuccess(t: LoginRes?, mess: String) {
                        dissmissProgress()
                        requireContext().toast(t!!.data.get(0).otp)
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
            if (_binding!!.OTP.length()==5){
                _binding!!.verifyOTP.isSelected = true
            }else{
                _binding!!.verifyOTP.isSelected = false
            }
        }

        _binding!!.verifyOTP.setOnClickListener {
            if (_binding!!.verifyOTP.isSelected){
                showProgress()
                viewModel.VerifyOTP(_binding!!.mobilenumber.text.toString(),_binding!!.OTP.text.toString(),object:ApiListener<LoginRes>{
                    override fun onSuccess(t: LoginRes?, mess: String) {
                        dissmissProgress()
                        requireContext().toast(t!!.errorMsg)
                        getSharedpref().saveBool(LOGIN,true)
                        ContainerActivity.OpenContainer(requireContext(),"EnterDetailFragment",null)
                        activity?.finish()
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
            if (_binding!!.timerCount.text =="Resend"){
                countDownStart()
            }
        }


    }

    private fun countDownStart() {

        timer.schedule(object :TimerTask(){
            override fun run() {
                if (timeLeft > 0) {
                    val minutes = timeLeft / 60
                    val seconds = timeLeft % 60
                    println(String.format("%02d:%02d", minutes, seconds))

//                        _binding!!.timerCount.text = String.format("%02d:%02d", minutes, seconds)

                    timeLeft--
                } else {
                    println("Time's up!")
                    timer.cancel()
                    _binding!!.timerCount.setText("Resend")
                }
            }

        },0,1000) // Run every 1 second
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