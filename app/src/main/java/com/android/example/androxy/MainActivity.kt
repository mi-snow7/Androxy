package com.android.example.androxy

import android.content.ComponentName
import android.database.ContentObserver
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.service.quicksettings.TileService
import android.util.Patterns
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import com.android.example.androxy.Validator.PORT_REGEX


/**
 * GOD activity.
 */
class MainActivity : AppCompatActivity() {

    // observer for update UI when tapped QS panel during opening this activity
    private lateinit var observer: ContentObserver
    private lateinit var appPreference: AppPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                updateLabel()
            }
        }
        appPreference = AppPreference(applicationContext)

        contentResolver.registerGlobalHttpProxyObserver(observer)

        val host = findViewById<AppCompatEditText>(R.id.host)
        val port = findViewById<AppCompatEditText>(R.id.port)

        host.setText(appPreference.host, TextView.BufferType.NORMAL)
        port.setText("${appPreference.port}", TextView.BufferType.NORMAL)

        updateLabel()

        findViewById<TextView>(R.id.cmdLine).text = getString(R.string.command_line, packageName)

        findViewById<View>(R.id.set).setOnClickListener {
            kotlin.runCatching {
                val hostStr = host.text?.toString().orEmpty()
                val portNum = Integer.parseInt(port.text?.toString().orEmpty())

                if (!validate(hostStr, portNum)) {
                    return@runCatching
                }

                contentResolver.setGlobalHttpProxy(hostStr, portNum)
                appPreference.host = hostStr
                appPreference.port = portNum
            }.onFailure {
                showErrorToast(it)
            }

            TileService.requestListeningState(
                this,
                ComponentName(this, ProxyQuickSettingsTile::class.java)
            )
        }

        findViewById<View>(R.id.clear).setOnClickListener {
            kotlin.runCatching {
                contentResolver.clearGlobalHttpProxy()
            }.onFailure {
                showErrorToast(it)
            }

            TileService.requestListeningState(
                this,
                ComponentName(this, ProxyQuickSettingsTile::class.java)
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        contentResolver.unregisterGlobalHttpProxyObserver(observer)
    }

    private fun updateLabel() {
        findViewById<TextView>(R.id.proxyLabel).text = kotlin.runCatching {
            contentResolver.getGlobalHttpProxy()
        }.getOrNull()
    }

    private fun validate(hostStr: String, portNum: Int): Boolean {
        val host = findViewById<AppCompatEditText>(R.id.host)
        val port = findViewById<AppCompatEditText>(R.id.port)

        var isValid = true

        if (!Patterns.IP_ADDRESS.matcher(hostStr).matches()) {
            host.error = getText(R.string.invalid_ip_address)
            isValid = false
        } else {
            host.error = null
        }

        if (!"$portNum".matches(Regex(PORT_REGEX))) {
            port.error = getText(R.string.invalid_port_number)
            isValid = false
        } else {
            port.error = null
        }

        return isValid
    }
}
