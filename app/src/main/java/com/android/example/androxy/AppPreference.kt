package com.android.example.androxy

import android.content.Context
import android.preference.PreferenceManager

class AppPreference(
    context: Context,
) {

    companion object {
        private const val KEY_HOST = "KEY_HOST"
        private const val KEY_PORT = "KEY_PORT"
    }

    private val pref = PreferenceManager.getDefaultSharedPreferences(context)

    var host: String
        get() = requireNotNull(pref.getString(KEY_HOST, ""))
        set(value) {
            pref.edit().putString(KEY_HOST, value).apply()
        }

    var port: Int
        get() = pref.getInt(KEY_PORT, 0)
        set(value) {
            pref.edit().putInt(KEY_PORT, value).apply()
        }

}