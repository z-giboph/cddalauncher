package com.cddalauncher

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.cddalauncher.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupImmersiveMode()
        setupButtons()
        checkCDDAInstallation()
    }

    private fun setupImmersiveMode() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun setupButtons() {
        binding.btnPlay.setOnClickListener {
            // Launch via overlay permission flow
            startActivity(Intent(this, com.cddalauncher.overlay.OverlayPermissionActivity::class.java))
        }
        binding.btnDownload.setOnClickListener { openDownloadPage() }
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        binding.btnControls.setOnClickListener {
            startActivity(Intent(this, ControlsActivity::class.java))
        }
        binding.btnMods.setOnClickListener {
            startActivity(Intent(this, ModsActivity::class.java))
        }
    }

    private fun checkCDDAInstallation() {
        val cddaDir = getCDDADirectory()
        if (cddaDir.exists() && cddaDir.listFiles()?.isNotEmpty() == true) {
            binding.btnPlay.isEnabled = true
            binding.tvStatus.text = "✓ CDDA Installed — Ready to Survive"
        } else {
            binding.btnPlay.isEnabled = false
            binding.tvStatus.text = "⚠ CDDA not found. Download the game files first."
        }
    }

    private fun launchCDDA() {
        val cddaApk = File(getCDDADirectory(), "com.cleverraven.cataclysmdda")
        val launchIntent = packageManager.getLaunchIntentForPackage("com.cleverraven.cataclysmdda")
        if (launchIntent != null) {
            startActivity(launchIntent)
        } else {
            Toast.makeText(this, "CDDA app not found. Please install via ZombDroid or official APK.", Toast.LENGTH_LONG).show()
        }
    }

    private fun openDownloadPage() {
        val url = "https://github.com/CleverRaven/Cataclysm-DDA/releases"
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    private fun getCDDADirectory(): File {
        return File(Environment.getExternalStorageDirectory(), "cdda")
    }
}
