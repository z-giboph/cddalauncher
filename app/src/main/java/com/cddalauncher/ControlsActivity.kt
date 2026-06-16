package com.cddalauncher

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.cddalauncher.databinding.ActivityControlsBinding

class ControlsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityControlsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityControlsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupImmersiveMode()
        setupControls()
    }

    private fun setupImmersiveMode() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let {
            it.hide(WindowInsetsCompat.Type.systemBars())
            it.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun setupControls() {
        // D-pad movement
        binding.btnUp.setOnClickListener    { sendKey("UP") }
        binding.btnDown.setOnClickListener  { sendKey("DOWN") }
        binding.btnLeft.setOnClickListener  { sendKey("LEFT") }
        binding.btnRight.setOnClickListener { sendKey("RIGHT") }

        // Actions
        binding.btnConfirm.setOnClickListener  { sendKey("CONFIRM") }
        binding.btnCancel.setOnClickListener   { sendKey("ESCAPE") }
        binding.btnInventory.setOnClickListener { sendKey("i") }
        binding.btnPickup.setOnClickListener   { sendKey(",") }
        binding.btnDrop.setOnClickListener     { sendKey("d") }
        binding.btnWait.setOnClickListener     { sendKey(".") }
        binding.btnCraft.setOnClickListener    { sendKey("&") }
        binding.btnMap.setOnClickListener      { sendKey("m") }
        binding.btnSmash.setOnClickListener    { sendKey("s") }
        binding.btnFire.setOnClickListener     { sendKey("f") }

        // Back to launcher
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun sendKey(key: String) {
        // In a full implementation, this sends key events to the CDDA process
        // via ADB input or an accessibility service bridge
        android.util.Log.d("CDDAControls", "Key sent: $key")
    }
}
