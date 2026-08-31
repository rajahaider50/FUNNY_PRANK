package com.funnyprank.app.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import com.funnyprank.app.AppContainer
import com.funnyprank.app.MainActivity
import com.funnyprank.app.R

/**
 * Real draggable floating overlay rendered above other apps.
 * Only runs while the SYSTEM_ALERT_WINDOW permission is granted.
 */
class FloatingOverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private var overlayView: View? = null
    private var params: WindowManager.LayoutParams? = null

    private val container by lazy { AppContainer.get(this) }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        addOverlay()
    }

    private fun addOverlay() {
        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE

        val size = resources.getDimensionPixelSize(R.dimen.overlay_size)
        val lp = WindowManager.LayoutParams(
            size, size,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            if (container.settings.hasStoredOverlayPosition()) {
                x = container.settings.overlayX
                y = container.settings.overlayY
            } else {
                x = (resources.displayMetrics.widthPixels / 2) - (size / 2)
                y = (resources.displayMetrics.heightPixels / 2) - (size)
            }
        }

        val view = buildOverlayView(this, lp)
        overlayView = view
        params = lp
        try {
            windowManager.addView(view, lp)
        } catch (_: Exception) {
            stopSelf()
        }
    }

    private fun buildOverlayView(context: Context, lp: WindowManager.LayoutParams): View {
        val size = context.resources.getDimensionPixelSize(R.dimen.overlay_size)
        val child = ImageView(context).apply {
            setImageResource(R.drawable.logo)
            scaleType = ImageView.ScaleType.FIT_CENTER
            background = null
        }
        return FrameLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(size, size)
            addView(child, FrameLayout.LayoutParams(size * 3 / 4, size * 3 / 4, Gravity.CENTER))
            setOnTouchListener(createDragTouchListener(lp))
        }
    }

    private fun createDragTouchListener(lp: WindowManager.LayoutParams): View.OnTouchListener {
        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f
        var isDragging = false
        var movedDistance = 0f

        return View.OnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = lp.x
                    initialY = lp.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    movedDistance = 0f
                    isDragging = false
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx = event.rawX - initialTouchX
                    val dy = event.rawY - initialTouchY
                    movedDistance = kotlin.math.hypot(dx, dy)
                    if (movedDistance > 8f) {
                        isDragging = true
                        val viewWidth = view.width
                        val viewHeight = view.height
                        val display = resources.displayMetrics
                        lp.x = (initialX + dx).toInt().coerceIn(0, display.widthPixels - viewWidth)
                        lp.y = (initialY + dy).toInt().coerceIn(0, display.heightPixels - viewHeight)
                        runCatching { windowManager.updateViewLayout(view, lp) }
                    }
                    true
                }

                MotionEvent.ACTION_UP -> {
                    if (!isDragging || movedDistance < 15f) {
                        // treat as a tap
                        container.settings.overlayX = lp.x
                        container.settings.overlayY = lp.y
                        openApp()
                    } else {
                        container.settings.overlayX = lp.x
                        container.settings.overlayY = lp.y
                    }
                    isDragging = false
                    true
                }

                else -> false
            }
        }
    }

    private fun openApp() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { startActivity(intent) }
    }

    override fun onDestroy() {
        overlayView?.let { runCatching { windowManager.removeView(it) } }
        overlayView = null
        super.onDestroy()
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, FloatingOverlayService::class.java)
            context.startService(intent)
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, FloatingOverlayService::class.java))
        }
    }
}
