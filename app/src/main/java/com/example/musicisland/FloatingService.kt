package com.example.musicisland

import android.app.*
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class FloatingService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View

    override fun onCreate() {
        super.onCreate()
        
        // 1. Notifikasi
        val channelId = "island_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Music Island", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        val notifBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(this, channelId) else Notification.Builder(this)
        startForeground(1, notifBuilder.setContentTitle("Music Island").setSmallIcon(android.R.drawable.ic_media_play).build())

        // 2. Buat UI Tanpa XML
        try {
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            
            // Bikin Layout Kotak Hitam
            val layout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setBackgroundColor(Color.BLACK)
                setPadding(40, 40, 40, 40)
                gravity = Gravity.CENTER_VERTICAL
            }

            // Teks
            val text = TextView(this).apply {
                text = "Music Island (Test)"
                setTextColor(Color.WHITE)
                textSize = 16f
            }

            // Tombol
            val btn = Button(this).apply {
                text = "Play"
                setOnClickListener {
                    Toast.makeText(this@FloatingService, "Memutar Lagu...", Toast.LENGTH_SHORT).show()
                    val title = MusicManager.playRandomSong(this@FloatingService)
                    text = title // Update teks (Ganti 'text' jadi nama variabel TextView)
                }
            }

            layout.addView(text)
            layout.addView(btn)

            // 3. Parameter Overlay
            val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            }

            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                y = 100
            }

            // 4. Tampilkan!
            windowManager.addView(layout, params)
            Toast.makeText(this, "BERHASIL TAMPILKAN PILL!", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            Toast.makeText(this, "GAGAL: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::floatingView.isInitialized) {
            try { windowManager.removeView(floatingView) } catch (e: Exception) {}
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
