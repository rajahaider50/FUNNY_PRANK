package com.funnyprank.app.floating

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.os.Build
import android.os.IBinder
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.funnyprank.app.FunnyPrankApp
import com.funnyprank.app.R
import com.funnyprank.app.audio.AudioEngine
import com.funnyprank.app.data.db.SoundItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Foreground overlay service: a draggable glass bubble that expands into a
 * professional floating sound panel (window-style). Playback uses [AudioEngine]
 * so routing (speaker / wired / bluetooth) stays identical to the in-app path.
 */
class FloatingOverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val engine by lazy { AudioEngine(applicationContext) }

    private var bubble: FrameLayout? = null
    private var panel: FrameLayout? = null

    private var lastX = 0
    private var lastY = 300

    private var isExpanded = false

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        startForegroundWithNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (bubble == null) showBubble()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startForegroundWithNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Floating Soundboard",
                NotificationManager.IMPORTANCE_LOW
            )
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Funny Prank")
            .setContentText("Floating soundboard is active")
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .build()
        startForeground(1, notification)
    }

    private fun windowType(): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

    @Suppress("DEPRECATION")
    private fun windowFlags(): Int =
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS

    // ---------------- BUBBLE ----------------

    private fun showBubble() {
        removePanel()
        bubble?.let { runCatching { windowManager.removeView(it) } }

        val iv = ImageView(this).apply {
            setBackgroundResource(R.drawable.bubble_bg)
            elevation = 18f
        }
        // overlay bolt glyph
        val bolt = ImageView(this).apply {
            setImageResource(R.drawable.ic_bolt_white)
        }
        val container = FrameLayout(this)
        container.addView(iv, FrameLayout.LayoutParams(dp(60), dp(60)))
        container.addView(
            bolt,
            FrameLayout.LayoutParams(dp(30), dp(30), Gravity.CENTER)
        )

        val params = WindowManager.LayoutParams(
            dp(60), dp(60),
            windowType(),
            windowFlags(),
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = lastX
            y = lastY
        }

        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f
        var dragging = false

        container.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    dragging = false
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (event.rawX - initialTouchX).toInt()
                    val dy = (event.rawY - initialTouchY).toInt()
                    if (abs(dx) > 8 || abs(dy) > 8) dragging = true
                    params.x = initialX + dx
                    params.y = initialY + dy
                    runCatching { windowManager.updateViewLayout(container, params) }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    lastX = params.x
                    lastY = params.y
                    if (!dragging) {
                        if (isExpanded) collapsePanel() else expandPanel()
                    }
                    true
                }
                else -> false
            }
        }

        windowManager.addView(container, params)
        bubble = container
    }

    // ---------------- PANEL ----------------

    private fun expandPanel() {
        if (isExpanded) return
        isExpanded = true
        removeBubble()
        panel?.let { runCatching { windowManager.removeView(it) } }

        val root = FrameLayout(this)
        root.setBackgroundResource(R.drawable.panel_bg)

        // Header
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(14), dp(10), dp(6))
        }
        val title = TextView(this).apply {
            text = "Funny Prank 🎵"
            setTextColor(Color.WHITE)
            setTypeface(typeface, Typeface.BOLD)
            textSize = 16f
        }
        header.addView(title, LinearLayout.LayoutParams(0, dp(40), 1f))

        val closeBtn = TextView(this).apply {
            text = "✕"
            setTextColor(Color.WHITE)
            textSize = 20f
            gravity = Gravity.CENTER
        }
        closeBtn.setOnClickListener { collapsePanel() }
        header.addView(closeBtn, LinearLayout.LayoutParams(dp(40), dp(40)))

        // Sounds row (horizontal scroll)
        val soundRow = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
        }
        val rowInner = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp(16), dp(6), dp(16), dp(6))
        }
        loadSoundsInto(rowInner)

        root.addView(header, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, dp(52)
        ))
        root.addView(soundRow, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, dp(110),
            Gravity.TOP or Gravity.CENTER_HORIZONTAL
        ).apply { topMargin = dp(50) })

        // Stop strip at bottom
        val stopBtn = TextView(this).apply {
            text = "⬤ Stop"
            setTextColor(Color.WHITE)
            textSize = 14f
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#33FF4D6D"))
        }
        stopBtn.setOnClickListener { engine.stop() }
        root.addView(stopBtn, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, dp(44),
            Gravity.BOTTOM
        ))

        val panelW = (resources.displayMetrics.widthPixels * 0.86f).toInt()
        val panelH = dp(210)
        val params = WindowManager.LayoutParams(
            panelW, panelH,
            windowType(),
            windowFlags(),
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = lastX.coerceAtLeast(8)
            y = lastY.coerceAtLeast(8)
        }

        windowManager.addView(root, params)
        panel = root
    }

    private fun loadSoundsInto(container: LinearLayout) {
        scope.launch {
            val repo = (applicationContext as FunnyPrankApp).repository
            val list = repo.observeSounds().first()
            container.removeAllViews()
            if (list.isEmpty()) {
                container.addView(TextView(this@FloatingOverlayService).apply {
                    text = "No sounds yet — add some from the app"
                    setTextColor(Color.WHITE)
                    textSize = 14f
                })
            } else {
                list.forEach { s -> container.addView(soundChip(s)) }
            }
        }
    }

    private fun soundChip(item: SoundItem): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(10), dp(10), dp(10), dp(10))
            setBackgroundResource(R.drawable.panel_bg)
        }
        val lp = LinearLayout.LayoutParams(dp(96), dp(92)).apply {
            marginEnd = dp(10)
        }
        card.layoutParams = lp

        val play = TextView(this).apply {
            text = "▶"
            textSize = 26f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }
        val name = TextView(this).apply {
            text = item.displayName
            setTextColor(Color.WHITE)
            textSize = 12f
            maxLines = 1
            gravity = Gravity.CENTER
        }
        card.addView(
            play,
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(48))
        )
        card.addView(
            name,
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(30))
        )

        card.setOnClickListener {
            scope.launch {
                val repo = (applicationContext as FunnyPrankApp).repository
                val fresh = repo.getById(item.id) ?: item
                engine.stop()
                engine.play(fresh)
            }
        }
        return card
    }

    private fun collapsePanel() {
        isExpanded = false
        panel?.let { runCatching { windowManager.removeView(it) } }
        panel = null
        showBubble()
    }

    private fun removeBubble() {
        bubble?.let { runCatching { windowManager.removeView(it) } }
        bubble = null
    }

    private fun removePanel() {
        panel?.let { runCatching { windowManager.removeView(it) } }
        panel = null
    }

    override fun onDestroy() {
        scope.cancel()
        engine.release()
        removeBubble()
        removePanel()
        super.onDestroy()
    }

    private fun dp(v: Int): Int =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            v.toFloat(),
            resources.displayMetrics
        ).toInt()

    companion object {
        private const val CHANNEL_ID = "funny_prank_channel"
        fun start(context: android.content.Context) {
            val intent = Intent(context, FloatingOverlayService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }
        fun stop(context: android.content.Context) {
            context.stopService(Intent(context, FloatingOverlayService::class.java))
        }
    }
}
