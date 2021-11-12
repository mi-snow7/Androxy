package com.android.example.androxy

import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

/**
 * QS Tile.
 */
class ProxyQuickSettingsTile : TileService() {

    private lateinit var appPreference: AppPreference

    override fun onCreate() {
        super.onCreate()

        appPreference = AppPreference(applicationContext)
    }

    override fun onClick() {
        super.onClick()
        toggleProxy()
    }

    override fun onStartListening() {
        super.onStartListening()
        updateTileUi()
    }

    private fun toggleProxy() {
        val proxy = kotlin.runCatching {
            contentResolver.getGlobalHttpProxy()
        }.getOrNull()
        if (!proxy.isNullOrEmpty() && proxy != ":0") {
            kotlin.runCatching {
                contentResolver.clearGlobalHttpProxy()
            }
        } else {
            kotlin.runCatching {
                contentResolver.setGlobalHttpProxy(appPreference.host, appPreference.port)
            }
        }
        updateTileUi()
    }

    private fun updateTileUi() {
        val proxy = kotlin.runCatching {
            contentResolver.getGlobalHttpProxy()
        }.getOrNull()
        if (!proxy.isNullOrEmpty() && proxy != ":0") {
            qsTile.apply {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    label = "Global Proxy"
                    subtitle = proxy
                } else {
                    label = proxy
                }

                icon = Icon.createWithResource(
                    this@ProxyQuickSettingsTile,
                    R.drawable.ic_baseline_public_24
                )
                state = Tile.STATE_ACTIVE
                updateTile()
            }
        } else {
            qsTile.apply {

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    label = "Global Proxy"
                    subtitle = "No global proxy"
                } else {
                    label = "No global proxy"
                }
                icon = Icon.createWithResource(
                    this@ProxyQuickSettingsTile,
                    R.drawable.ic_baseline_public_off_24
                )
                state = Tile.STATE_INACTIVE
                updateTile()
            }
        }
    }
}