package com.cddalauncher

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var gameStatusText: TextView
    private lateinit var launchButton: Button
    private var cddaPackageName: String = ""

    private val cddaPackages = listOf(
        "com.cleverraven.cataclysmdda",
        "com.cleverraven.cataclysmdda.experimental",
        "cataclysm.dda",
        "com.cleverraven.cataclysmdda.debug"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        enableFullscreen()

        gameStatusText = findViewById(R.id.game_status_text)
        launchButton = findViewById(R.id.btn_launch_game)
        val controlsButton: Button = findViewById(R.id.btn_controls)
        val settingsButton: Button = findViewById(R.id.btn_settings)
        val modsButton: Button = findViewById(R.id.btn_mods)

        detectAndUpdateCDDAStatus()

        launchButton.setOnClickListener {
            if (cddaPackageName.isNotEmpty()) {
                launchCDDA()
            } else {
                openCDDAReleases()
            }
        }

        controlsButton.setOnClickListener {
            startActivity(Intent(this, ControlsActivity::class.java))
        }

        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        modsButton.setOnClickListener {
            startActivity(Intent(this, ModsActivity::class.java))
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

    private fun detectAndUpdateCDDAStatus() {
        cddaPackageName = findCDDAPackage()
        
        if (cddaPackageName.isNotEmpty()) {
            gameStatusText.text = "✅ CDDA is installed"
            gameStatusText.setTextColor(getColor(R.color.accent_green))
            launchButton.text = "LAUNCH CDDA"
        } else {
            gameStatusText.text = "⚠️ CDDA is not installed"
            gameStatusText.setTextColor(getColor(R.color.accent_red))
            launchButton.text = "DOWNLOAD CDDA"
        }
    }

    private fun findCDDAPackage(): String {
        for (pkg in cddaPackages) {
            try {
                packageManager.getPackageInfo(pkg, 0)
                return pkg
            } catch (e: PackageManager.NameNotFoundException) {
                // Continue
            }
        }
        return ""
    }

    private fun launchCDDA() {
        try {
            val launchIntent = packageManager.getLaunchIntentForPackage(cddaPackageName)
            if (launchIntent != null) {
                startActivity(launchIntent)
            } else {
                Toast.makeText(this, "Error: Cannot launch CDDA", Toast.LENGTH_LONG).show()
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_LAUNCHER)
                intent.setPackage(cddaPackageName)
                startActivity(intent)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error launching CDDA: ${e.message}", Toast.LENGTH_LONG).show()
            openCDDAReleases()
        }
    }

    private fun openCDDAReleases() {
        val browserIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://github.com/CleverRaven/Cataclysm-DDA/releases")
        )
        startActivity(browserIntent)
    }

    override fun onResume() {
        super.onResume()
        detectAndUpdateCDDAStatus()
    }
}