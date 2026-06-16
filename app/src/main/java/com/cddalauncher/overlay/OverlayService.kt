package com.cddalauncher.overlay

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import com.cddalauncher.R

class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: View
    private var isOverlayVisible = true

    companion object {
        private var instance: OverlayService? = null
        
        fun toggleOverlay() {
            instance?.let {
                it.isOverlayVisible = !it.isOverlayVisible
                it.overlayView.visibility = if (it.isOverlayVisible) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        createOverlayView()
    }

    private fun createOverlayView() {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        overlayView = inflater.inflate(R.layout.overlay_controls, null)

        setupButtonListeners()

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        params.y = 80

        windowManager.addView(overlayView, params)
        startForegroundService()
    }

    private fun setupButtonListeners() {
        // D-PAD
        overlayView.findViewById<Button>(R.id.overlay_btn_up).setOnClickListener {
            sendKeyEvent(19)
        }
        overlayView.findViewById<Button>(R.id.overlay_btn_down).setOnClickListener {
            sendKeyEvent(20)
        }
        overlayView.findViewById<Button>(R.id.overlay_btn_left).setOnClickListener {
            sendKeyEvent(21)
        }
        overlayView.findViewById<Button>(R.id.overlay_btn_right).setOnClickListener {
            sendKeyEvent(22)
        }

        // Action Buttons
        overlayView.findViewById<Button>(R.id.overlay_btn_enter).setOnClickListener {
            sendKeyEvent(66)
        }
        overlayView.findViewById<Button>(R.id.overlay_btn_escape).setOnClickListener {
            sendKeyEvent(111)
        }
        overlayView.findViewById<Button>(R.id.overlay_btn_space).setOnClickListener {
            sendKeyEvent(62)
        }
        overlayView.findViewById<Button>(R.id.overlay_btn_comma).setOnClickListener {
            sendKeyEvent(55)
        }
        overlayView.findViewById<Button>(R.id.overlay_btn_period).setOnClickListener {
            sendKeyEvent(56)
        }

        // Hotbar
        overlayView.findViewById<Button>(R.id.overlay_btn_i).setOnClickListener {
            sendKeyEvent(25)
        }
        overlayView.findViewById<Button>(R.id.overlay_btn_m).setOnClickListener {
            sendKeyEvent(51)
        }
        overlayView.findViewById<Button>(R.id.overlay_btn_s).setOnClickListener {
            sendKeyEvent(47)
        }
        overlayView.findViewById<Button>(R.id.overlay_btn_f).setOnClickListener {
            sendKeyEvent(41)
        }
        overlayView.findViewById<Button>(R.id.overlay_btn_c).setOnClickListener {
            sendKeyEvent(54)
        }
        overlayView.findViewById<Button>(R.id.overlay_btn_e).setOnClickListener {
            sendKeyEvent(44)
        }
        overlayView.findViewById<Button>(R.id.overlay_btn_g).setOnClickListener {
            sendKeyEvent(38)
        }
        overlayView.findViewById<Button>(R.id.overlay_btn_d).setOnClickListener {
            sendKeyEvent(42)
        }
        overlayView.findViewById<Button>(R.id.overlay_btn_a).setOnClickListener {
            sendKeyEvent(29)
        }
        overlayView.findViewById<Button>(R.id.overlay_btn_w).setOnClickListener {
            sendKeyEvent(50)
        }

        // Zoom
        overlayView.findViewById<Button>(R.id.overlay_btn_zoom_in).setOnClickListener {
            performZoom(true)
        }
        overlayView.findViewById<Button>(R.id.overlay_btn_zoom_out).setOnClickListener {
            performZoom(false)
        }

        // Hide
        overlayView.findViewById<Button>(R.id.overlay_btn_hide).setOnClickListener {
            toggleOverlay()
            Toast.makeText(this, "Overlay hidden!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendKeyEvent(keyCode: Int) {
        try {
            val downTime = System.currentTimeMillis()
            val downEvent = android.view.KeyEvent(
                downTime, downTime,
                android.view.KeyEvent.ACTION_DOWN,
                keyCode, 0
            )
            val upEvent = android.view.KeyEvent(
                downTime, downTime,
                android.view.KeyEvent.ACTION_UP,
                keyCode, 0
            )

            val inputManager = getSystemService(Context.INPUT_SERVICE) as android.hardware.input.InputManager
            inputManager.injectInputEvent(downEvent, 0)
            inputManager.injectInputEvent(upEvent, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun performZoom(zoomIn: Boolean) {
        Toast.makeText(this, if (zoomIn) "🔍 Zoom In" else "🔍 Zoom Out", Toast.LENGTH_SHORT).show()
    }

    private fun startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "overlay_channel",
                "CDDA Overlay",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)

            val notification = Notification.Builder(this, "overlay_channel")
                .setContentTitle("🎮 CDDA Overlay Active")
                .setContentText("Tap to show/hide controls")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(false)
                .build()

            startForeground(1, notification)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        if (::overlayView.isInitialized) {
            windowManager.removeView(overlayView)
        }
    }
}