package com.cddalauncher

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cddalauncher.overlay.OverlayPermissionActivity
import com.cddalauncher.overlay.OverlayService

class ControlsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controls)

        enableFullscreen()

        findViewById<Button>(R.id.btn_overlay).setOnClickListener {
            val intent = Intent(this, OverlayPermissionActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_hide_overlay).setOnClickListener {
            OverlayService.toggleOverlay()
            Toast.makeText(this, "Overlay toggled!", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btn_back).setOnClickListener {
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
}