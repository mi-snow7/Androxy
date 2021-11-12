package com.android.example.androxy

import android.content.ContentResolver
import android.content.Context
import android.database.ContentObserver
import android.provider.Settings
import android.widget.Toast

@Throws(SecurityException::class, Exception::class)
fun ContentResolver.setGlobalHttpProxy(host: String, port: Int) =
    Settings.Global.putString(this, Settings.Global.HTTP_PROXY, "$host:$port")

@Throws(SecurityException::class, Exception::class)
fun ContentResolver.clearGlobalHttpProxy() =
    Settings.Global.putString(this, Settings.Global.HTTP_PROXY, ":0")

@Throws(SecurityException::class, Exception::class)
fun ContentResolver.getGlobalHttpProxy(): String? =
    Settings.Global.getString(this, Settings.Global.HTTP_PROXY)

fun ContentResolver.registerGlobalHttpProxyObserver(observer: ContentObserver) {
    registerContentObserver(
        Settings.Global.getUriFor(Settings.Global.HTTP_PROXY),
        true,
        observer
    )
}

fun ContentResolver.unregisterGlobalHttpProxyObserver(observer: ContentObserver) {
    unregisterContentObserver(observer)
}

fun Context.showErrorToast(throwable: Throwable) {
    Toast.makeText(applicationContext, "An error occurred: ${throwable.message}", Toast.LENGTH_LONG)
        .show()
}
