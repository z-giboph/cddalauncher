package com.cddalauncher.overlay

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cddalauncher.databinding.ActivityOverlayPermissionBinding

class OverlayPermissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOverlayPermissionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOverlayPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Settings.canDrawOverlays(this)) {
            launchOverlayAndFinish()
            return
        }

        binding.tvMessage.text =
            "Para gumana ang overlay controls sa ibabaw ng CDDA,\nkailangan ng \"Draw over other apps\" permission."

        binding.btnGrant.setOnClickListener {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, REQ_OVERLAY)
        }

        binding.btnCancel.setOnClickListener { finish() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_OVERLAY) {
            if (Settings.canDrawOverlays(this)) {
                launchOverlayAndFinish()
            } else {
                Toast.makeText(this, "Permission denied. Overlay hindi gagana.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun launchOverlayAndFinish() {
        startService(Intent(this, OverlayService::class.java))

        // Launch CDDA after starting overlay
        val cdda = packageManager.getLaunchIntentForPackage("com.cleverraven.cataclysmdda")
        if (cdda != null) startActivity(cdda)

        finish()
    }

    companion object {
        private const val REQ_OVERLAY = 1001
    }
}
