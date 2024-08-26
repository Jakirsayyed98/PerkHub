package app.perkhub.Utils

import android.R
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat


fun Context.toast(text:String){
    Toast.makeText(this,text,Toast.LENGTH_LONG).show()
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
    return "₹"+text
}

fun Context.Share(name:String,url:String){
    val shareData = Intent(Intent.ACTION_SEND)
    shareData.type = "text/plain"
    val dataToShare = "Hi,\n\n I found best offer on $name just shop now bu using below url Shop now :)\n\n $url"
    shareData.putExtra(Intent.EXTRA_TEXT, dataToShare)
    startActivity(Intent.createChooser(shareData, "Share Via"))
}

