package app.perkhub.Utils

import android.R
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Locale


fun Context.toast(text:String){
    Toast.makeText(this,text,Toast.LENGTH_LONG).show()
}

fun Context.Snackbar(text:String,view:View){
    Snackbar.make(view, text, Snackbar.LENGTH_LONG).show();

}

fun Context.openCustomTab(uri: String?) {
    val customIntent = CustomTabsIntent.Builder()
    val customTabsIntent = customIntent.build()
    customIntent.setToolbarColor(ContextCompat.getColor(this, R.color.white))
    val packageName = "com.android.chrome"
    if (packageName != null) {
        customTabsIntent.intent.setPackage(packageName)
        customTabsIntent.launchUrl(this, Uri.parse(uri))
    } else {
        this.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
    }
}

fun setSuffixAmount(text : String):String{
//    return "₹"+text
    val count = text.toDouble()
    return String.format("₹%.2f", count)
}

fun Context.Share(name:String,url:String){
    val shareData = Intent(Intent.ACTION_SEND)
    shareData.type = "text/plain"
    val dataToShare = "Hi,\n\n I found best offer on $name just shop now bu using below url Shop now :)\n\n $url"
    shareData.putExtra(Intent.EXTRA_TEXT, dataToShare)
    startActivity(Intent.createChooser(shareData, "Share Via"))
}

fun onlyDatewithMonth(date: String?): String? {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX", Locale.ENGLISH)
    val parsedDate = inputFormat.parse(date)

    // Now format the parsed date into the desired format.
    val outputFormat = SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH)
    return parsedDate?.let { outputFormat.format(it) }
}

fun isValidUpiId(upiId: String): Boolean {
    val validUpiHandles = setOf(
        "apl", "yapl", "rapl",
        "abfspay",
        "okaxis", "okhdfcbank", "okicici", "oksbi",
        "icici", "idfcbank", "jupiteraxis", "goaxb",
        "ikwik", "naviaxis", "niyoicici", "ybl", "ibl", "axl",
        "pingpay", "shriramhdfcbank", "sliceaxis", "tapicici",
        "timecosmos", "freoicici", "superyes", "seyes",
        "bpunity", "kphdfc", "paytm", "ptyes", "ptaxis", "pthdfc", "ptsbi",
        "yesfam", "yespop", "abcdicici", "waicici", "waaxis", "wahdfcbank", "wasbi"
    )

    val regex = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+$"
    if (!upiId.matches(Regex(regex))) {
        return false
    }

    val upiIdParts = upiId.split('@')
    if (upiIdParts.size != 2) {
        return false
    }

    val domainPart = upiIdParts[1]
    return domainPart in validUpiHandles
}

fun Context.shareAppLink() {
    val appUrl = "https://play.google.com/store/apps/details?id="+applicationContext.packageName // Replace with your actual Play Store URL
    val shareMessage = """
        Discover PerkHub – Your gateway to exclusive perks, rewards, and deals! 🌟
        Download now: $appUrl
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_TEXT, shareMessage)

    startActivity(Intent.createChooser(intent, "Share PerkHub via"))
}

fun Context.rateUsApp() {
    val appPackageName = applicationContext.packageName
    try {
        // Try to open the Play Store app directly
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName"))
        startActivity(intent)
    } catch (e: android.content.ActivityNotFoundException) {
        // Fallback if Play Store app is not available, open in browser
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName"))
        startActivity(intent)
    }
}


