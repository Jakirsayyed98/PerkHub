package app.perkhub.Screens.Profile

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import app.perkhub.ContainerActivity
import app.perkhub.R
import app.perkhub.Screens.Profile.adapter.ProfileMenuAdapter
import app.perkhub.Screens.Profile.model.menuList
import app.perkhub.SplashActivity
import app.perkhub.Utils.ABOUTUS
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.CONTACTUS
import app.perkhub.Utils.PRIVACYPOLICY
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.Utils.Snackbar
import app.perkhub.Utils.USERDATA
import app.perkhub.Utils.getAppVersion
import app.perkhub.Utils.getCopyrightText
import app.perkhub.Utils.openCustomTab
import app.perkhub.Utils.rateUsApp
import app.perkhub.Utils.shareAppLink
import app.perkhub.databinding.FragmentProfileBinding


class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(layoutInflater,container,false)
        _binding!!.toolbar.name.text = "My Profile"
        _binding!!.toolbar.backbtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        setMenuList()
        val userName = getSharedpref().getUser(USERDATA)?.data
        _binding!!.Profilename.text= userName?.name!!.replaceFirstChar { it.uppercase() }.orEmpty()
        _binding!!.email.text = userName.email
        _binding!!.ProfilenameShort.text = userName.email.toCharArray()[0].toString().uppercase()
        val appVersion = getAppVersion(requireContext())
        _binding!!.appversion.text = "App Version v${appVersion}"
        _binding!!.companyName.text = getCopyrightText()

        return _binding?.root
    }

    private fun setMenuList() {
        val menulist = ArrayList<menuList>()
        menulist.add(0, menuList(0,R.drawable.store_orders,"My Orders","Track your orders and cashback"))
        menulist.add(0, menuList(3,R.drawable.wallet_ico,"Wallet & Transactions","Check your cashback balance and history"))
        menulist.add(0, menuList(2,R.drawable.games,"Games & Fun","Take a break. Play something fun."))
        menulist.add(0, menuList(2,R.drawable.games,"PerkHub Partner","Partner with us and grow together"))
        menulist.add(0, menuList(4,R.drawable.support,"Help & Support","Get help and contact support"))
        menulist.add(0, menuList(7,R.drawable.policy, "Privacy & Security","Privacy policy and data settings"))
        menulist.add(0, menuList(8,R.drawable.about_us, "About PerkHub","Learn more about us"))
        menulist.add(0, menuList(5,R.drawable.share_icon,"Share","Share PerkHub App"))
        menulist.add(0, menuList(6,R.drawable.rate_us,"Rate PerkHub", "Share your feedback"))
        menulist.add(0, menuList(9,R.drawable.logout, "Logout","Sign out of your account"))

        val adapter = ProfileMenuAdapter<menuList>(object : RecyclerClickListener{
            override fun RecyclerClickListener(posi: Int, type: String, data: Any?) {
                when (type) {
                    "0" -> ContainerActivity.OpenContainer(requireContext(), "TransactionHistoryFragment", "")
                    "1" -> ContainerActivity.OpenContainer(requireContext(), "OffersFragment", "")
                    "2" -> ContainerActivity.OpenContainer(requireContext(), "GamesFragment", "")
                    "3" -> ContainerActivity.OpenContainer(requireContext(), "WalletFragment", "")
                    "4" -> ContainerActivity.OpenContainer(requireContext(), "TicketListFragment", "")
                    "5" -> requireContext().shareAppLink()
                    "6" -> requireContext().rateUsApp()
                    "7" -> requireContext().openCustomTab(PRIVACYPOLICY)
                    "8" -> requireContext().openCustomTab(ABOUTUS)
                    "9" -> requireContext().showLogoutDialog()
                }
            }
        })

        _binding!!.menuList.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,true)
            this.adapter=adapter
        }

        adapter.addAllItem(menulist)
        adapter.notifyDataSetChanged()
    }

    private fun Context.showLogoutDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.logout_dialog_layout)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val cancel = dialog.findViewById<AppCompatButton>(R.id.cancel)
        val leave = dialog.findViewById<AppCompatButton>(R.id.leave)
        dialog.setCancelable(true)

        cancel.setOnClickListener {
            dialog.dismiss()
            Snackbar("Stay logged in",_binding!!.mainlayout)
        }
        leave.setOnClickListener {
            dialog.dismiss()
            getSharedpref().clearAll()
            val intent = Intent(requireContext(), SplashActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            Snackbar("Logged out successfully",_binding!!.mainlayout)
        }
        dialog.show()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ProfileFragment().apply {

            }
    }
}