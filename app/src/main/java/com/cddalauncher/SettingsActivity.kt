package com.cddalauncher

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        enableFullscreen()

        prefs = getSharedPreferences("cdda_launcher", Context.MODE_PRIVATE)

        val switchOverlay: Switch = findViewById(R.id.switch_overlay)
        val switchFullscreen: Switch = findViewById(R.id.switch_fullscreen)
        val switchLandscape: Switch = findViewById(R.id.switch_landscape)
        val btnClearCache: Button = findViewById(R.id.btn_clear_cache)
        val btnBack: Button = findViewById(R.id.btn_back)
        val versionText: TextView = findViewById(R.id.version_text)

        switchOverlay.isChecked = prefs.getBoolean("overlay_enabled", true)
        switchFullscreen.isChecked = prefs.getBoolean("fullscreen_enabled", true)
        switchLandscape.isChecked = prefs.getBoolean("landscape_locked", true)

        switchOverlay.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("overlay_enabled", isChecked).apply()
            Toast.makeText(this, "Overlay ${if (isChecked) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
        }

        switchFullscreen.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("fullscreen_enabled", isChecked).apply()
            if (isChecked) {
                enableFullscreen()
            }
        }

        switchLandscape.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("landscape_locked", isChecked).apply()
        }

        btnClearCache.setOnClickListener {
            clearCache()
            Toast.makeText(this, "Cache cleared!", Toast.LENGTH_SHORT).show()
        }

        try {
            val pkgInfo = packageManager.getPackageInfo(packageName, 0)
            versionText.text = "Version ${pkgInfo.versionName} (${pkgInfo.versionCode})"
        } catch (e: Exception) {
            versionText.text = "Version 1.0.0"
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun enableFullscreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            )
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            enableFullscreen()
        }
    }

    private fun clearCache() {
        try {
            val cacheDir = cacheDir
            if (cacheDir.exists()) {
                deleteRecursive(cacheDir)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteRecursive(file: java.io.File) {
        if (file.isDirectory) {
            file.listFiles()?.forEach { deleteRecursive(it) }
        }
        file.delete()
    }
}