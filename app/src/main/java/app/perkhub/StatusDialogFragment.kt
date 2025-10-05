package app.perkhub

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import app.perkhub.Utils.DATA
import app.perkhub.Utils.SUCCESS
import app.perkhub.Utils.toast
import com.airbnb.lottie.LottieAnimationView


class StatusDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Make it fullscreen
        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)



        // Inflate the custom layout
        val view: View = LayoutInflater.from(requireActivity()).inflate(R.layout.fragment_status_dialog, null)
        dialog.setContentView(view)

        var animatedView : LottieAnimationView = view.findViewById(R.id.animationView)

        arguments?.getString(DATA)?.let { text ->
                if (text.equals(SUCCESS)){
                    animatedView.setAnimation(R.raw.success)
                }else{
                    animatedView.setAnimation(R.raw.failed)
                }
            }

        // Set dialog to full screen
        val window = dialog.window
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        return dialog
    }

    companion object {

        @JvmStatic
        fun newInstance(text: String) =
            StatusDialogFragment().apply {
                val fragment = StatusDialogFragment()
                val args = Bundle()
                args.putString(DATA, text)
                fragment.arguments = args
                return fragment
            }
    }
}