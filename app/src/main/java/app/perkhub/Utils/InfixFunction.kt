package app.perkhub.Utils

import android.R
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale


fun Context.toast(text:String){
    Toast.makeText(this,text,Toast.LENGTH_LONG).show()
}

fun Context.Snackbar(text:String,view:View){
    if (view == null) return
    if (!view.isAttachedToWindow) return

    if (Looper.myLooper() == Looper.getMainLooper()) {
        com.google.android.material.snackbar.Snackbar
            .make(view, text, com.google.android.material.snackbar.Snackbar.LENGTH_LONG)
            .show()
    } else {
        Handler(Looper.getMainLooper()).post {
            if (view.isAttachedToWindow) {
                com.google.android.material.snackbar.Snackbar
                    .make(view, text, com.google.android.material.snackbar.Snackbar.LENGTH_LONG)
                    .show()
            }
        }
    }


}
fun Context.openCustomTab(url: String?) {
    try {
        val builder = androidx.browser.customtabs.CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, android.net.Uri.parse(url))
    } catch (e: Exception) {
        e.printStackTrace()
    }
//    val customIntent = CustomTabsIntent.Builder()
//    val customTabsIntent = customIntent.build()
//    customIntent.setToolbarColor(ContextCompat.getColor(this, R.color.white))
//    val packageName = "com.android.chrome"
//    if (packageName != null) {
//        customTabsIntent.intent.setPackage(packageName)
//        customTabsIntent.launchUrl(this, Uri.parse(uri))
//    } else {
//        this.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
//    }


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

fun formatDate(date: String): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM, yyyy", Locale.ENGLISH)

    return Instant.parse(date)
        .atZone(ZoneId.of("Asia/Kolkata"))
        .format(formatter)
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

fun copyToClipboard(context: Context, text: String, bindView: View) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Copied Text", text)
    clipboard.setPrimaryClip(clip)
    context.Snackbar("copied successfully!",bindView)
}

fun Activity.hideKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val view = currentFocus ?: View(this)
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun getMaskedMobileNumber(mobile: String): String {
    return if (mobile.length == 10) {
        "+91 ******${mobile.takeLast(4)}"
    } else {
        mobile
    }
}

fun getGreeting(name: String): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    val greeting = when (hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }

    val formattedName = name.trim().replaceFirstChar {
        if (it.isLowerCase()) it.titlecase() else it.toString()
    }
    return "$greeting, $formattedName 👋"
}

fun getAppVersion(context: Context): String {
    return try {
        context.packageManager
            .getPackageInfo(context.packageName, 0)
            .versionName ?: ""
    } catch (e: Exception) {
        ""
    }
}

fun getCopyrightText(): String {
    val year = Calendar.getInstance().get(Calendar.YEAR)
    return "© $year PerkHub Pvt Ltd"
}

fun getStoreCacheExpiry(): Long {

    val calendar = java.util.Calendar.getInstance()
    calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
    calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
    calendar.set(java.util.Calendar.MINUTE, 0)
    calendar.set(java.util.Calendar.SECOND, 0)
    calendar.set(java.util.Calendar.MILLISECOND, 0)

    return calendar.timeInMillis
}