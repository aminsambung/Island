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
    private lateinit var floatingLayout: View

    override fun onCreate() {
        super.onCreate()
        
        // 1. Notifikasi Foreground Service
        val channelId = "island_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Music Island", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        val notifBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(this, channelId) else @Suppress("DEPRECATION") Notification.Builder(this)
        startForeground(1, notifBuilder.setContentTitle("Music Island").setSmallIcon(android.R.drawable.ic_media_play).build())

        // 2. Membuat UI Overlay dari Kode (Tanpa XML)
        try {
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            
            val layout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setBackgroundColor(Color.BLACK)
                setPadding(40, 40, 40, 40)
                gravity = Gravity.CENTER_VERTICAL
            }

            // VARIABEL TEXT VIEW YANG BARU
            val tvTitle = TextView(this).apply {
                text = "Music Island (Test)"
                setTextColor(Color.WHITE)
                textSize = 16f
            }

            // TOMBOL PLAY
            val btnPlay = Button(this).apply {
                text = "Play"
                setOnClickListener {
                    val title = MusicManager.playRandomSong(this@FloatingService)
                    tvTitle.text = title // <--- INI SUDAH DIPERBAIKI
                }
            }

            layout.addView(tvTitle)
            layout.addView(btnPlay)

            // Parameter Overlay
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

            floatingLayout = layout
            windowManager.addView(floatingLayout, params)
            
            Toast.makeText(this, "BERHASIL TAMPILKAN PILL!", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            Toast.makeText(this, "GAGAL: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::floatingLayout.isInitialized) {
            try { windowManager.removeView(floatingLayout) } catch (e: Exception) {}
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
