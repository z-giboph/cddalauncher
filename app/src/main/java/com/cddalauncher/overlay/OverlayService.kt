package com.cddalauncher.overlay

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import com.cddalauncher.R

class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: View
    private lateinit var dpadView: View
    private lateinit var actionView: View
    private lateinit var hotbarView: View

    // Track drag position for movable overlay toggle button
    private var toggleX = 0f
    private var toggleY = 0f

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        setupOverlayViews()
    }

    private fun setupOverlayViews() {
        val inflater = LayoutInflater.from(this)

        // --- D-PAD (bottom left) ---
        dpadView = inflater.inflate(R.layout.overlay_dpad, null)
        val dpadParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.START
            x = 24
            y = 24
        }
        windowManager.addView(dpadView, dpadParams)
        setupDpadListeners(dpadView)
        makeDraggable(dpadView, dpadParams)

        // --- ACTION PAD (bottom right) ---
        actionView = inflater.inflate(R.layout.overlay_actions, null)
        val actionParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.END
            x = 24
            y = 24
        }
        windowManager.addView(actionView, actionParams)
        setupActionListeners(actionView)
        makeDraggable(actionView, actionParams)

        // --- HOTBAR (bottom center) ---
        hotbarView = inflater.inflate(R.layout.overlay_hotbar, null)
        val hotbarParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            y = 24
        }
        windowManager.addView(hotbarView, hotbarParams)
        setupHotbarListeners(hotbarView)
    }

    // ──────────────────────────────────────────
    //  KEY SENDING
    // ──────────────────────────────────────────

    private fun sendKey(keyCode: Int) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        // Dispatches hardware key events to the focused CDDA window
        val inst = android.app.Instrumentation()
        Thread {
            try {
                inst.sendKeyDownUpSync(keyCode)
            } catch (e: Exception) {
                android.util.Log.w("Overlay", "Key send failed: ${e.message}")
            }
        }.start()
    }

    private fun sendChar(c: Char) {
        val inst = android.app.Instrumentation()
        Thread {
            try {
                inst.sendCharacterSync(c.code)
            } catch (e: Exception) {
                android.util.Log.w("Overlay", "Char send failed: ${e.message}")
            }
        }.start()
    }

    // ──────────────────────────────────────────
    //  BUTTON WIRING
    // ──────────────────────────────────────────

    private fun setupDpadListeners(v: View) {
        v.findViewById<View>(R.id.overlayBtnUp).setOnClickListener    { sendKey(android.view.KeyEvent.KEYCODE_DPAD_UP) }
        v.findViewById<View>(R.id.overlayBtnDown).setOnClickListener  { sendKey(android.view.KeyEvent.KEYCODE_DPAD_DOWN) }
        v.findViewById<View>(R.id.overlayBtnLeft).setOnClickListener  { sendKey(android.view.KeyEvent.KEYCODE_DPAD_LEFT) }
        v.findViewById<View>(R.id.overlayBtnRight).setOnClickListener { sendKey(android.view.KeyEvent.KEYCODE_DPAD_RIGHT) }

        // Long press UP = run north (shift+up in CDDA)
        v.findViewById<View>(R.id.overlayBtnUp).setOnLongClickListener {
            sendKey(android.view.KeyEvent.KEYCODE_DPAD_UP)
            sendKey(android.view.KeyEvent.KEYCODE_DPAD_UP)
            true
        }
    }

    private fun setupActionListeners(v: View) {
        v.findViewById<View>(R.id.overlayBtnConfirm).setOnClickListener   { sendKey(android.view.KeyEvent.KEYCODE_ENTER) }
        v.findViewById<View>(R.id.overlayBtnCancel).setOnClickListener    { sendKey(android.view.KeyEvent.KEYCODE_ESCAPE) }
        v.findViewById<View>(R.id.overlayBtnInventory).setOnClickListener { sendChar('i') }
        v.findViewById<View>(R.id.overlayBtnPickup).setOnClickListener    { sendChar(',') }
        v.findViewById<View>(R.id.overlayBtnDrop).setOnClickListener      { sendChar('d') }
        v.findViewById<View>(R.id.overlayBtnWait).setOnClickListener      { sendChar('.') }
    }

    private fun setupHotbarListeners(v: View) {
        v.findViewById<View>(R.id.overlayBtnCraft).setOnClickListener  { sendChar('&') }
        v.findViewById<View>(R.id.overlayBtnMap).setOnClickListener    { sendChar('m') }
        v.findViewById<View>(R.id.overlayBtnSmash).setOnClickListener  { sendChar('s') }
        v.findViewById<View>(R.id.overlayBtnFire).setOnClickListener   { sendChar('f') }
        v.findViewById<View>(R.id.overlayBtnClose).setOnClickListener  { stopSelf() }
    }

    // ──────────────────────────────────────────
    //  DRAGGABLE OVERLAY PANELS
    // ──────────────────────────────────────────

    private fun makeDraggable(view: View, params: WindowManager.LayoutParams) {
        var startX = 0f; var startY = 0f
        var startParamX = 0; var startParamY = 0

        view.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.rawX; startY = event.rawY
                    startParamX = params.x; startParamY = params.y
                    false // allow clicks to pass through
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (event.rawX - startX).toInt()
                    val dy = (event.rawY - startY).toInt()
                    if (Math.abs(dx) > 8 || Math.abs(dy) > 8) {
                        params.x = startParamX + dx
                        params.y = startParamY - dy
                        windowManager.updateViewLayout(view, params)
                    }
                    true
                }
                else -> false
            }
        }
    }

    // ──────────────────────────────────────────
    //  LIFECYCLE
    // ──────────────────────────────────────────

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> stopSelf()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::dpadView.isInitialized)   windowManager.removeView(dpadView)
        if (::actionView.isInitialized) windowManager.removeView(actionView)
        if (::hotbarView.isInitialized) windowManager.removeView(hotbarView)
    }

    companion object {
        const val ACTION_STOP = "com.cddalauncher.STOP_OVERLAY"
    }
}
