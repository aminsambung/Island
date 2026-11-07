package com.example.musicisland

import android.app.*
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast

class FloatingService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View

    override fun onCreate() {
        super.onCreate()
        
        // 1. Buat Notifikasi (Wajib untuk Foreground Service)
        createNotificationChannel()
        startForeground(1, buildNotification())

        try {
            // 2. Siapkan WindowManager
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            floatingView = LayoutInflater.from(this).inflate(R.layout.view_floating, null)

            // 3. Atur Parameter Overlay
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) 
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY 
                else 
                    WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                y = 100
            }

            // 4. Hubungkan Tombol
            val tvTitle = floatingView.findViewById<TextView>(R.id.tvTitle)
            val btnPlay = floatingView.findViewById<ImageButton>(R.id.btnPlay)

            btnPlay.setOnClickListener {
                val title = MusicManager.playRandomSong(this)
                tvTitle.text = title
            }

            // 5. Tampilkan ke Layar
            windowManager.addView(floatingView, params)
            Toast.makeText(this, "Pill berhasil ditampilkan!", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            // Jika gagal, tampilkan pesan error di layar HP
            Toast.makeText(this, "Error Overlay: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("island", "Music Island", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) 
            Notification.Builder(this, "island") 
        else 
            @Suppress("DEPRECATION") Notification.Builder(this)
            
        return builder.setContentTitle("Music Island Aktif").setSmallIcon(android.R.drawable.ic_media_play).build()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::floatingView.isInitialized) {
            try {
                windowManager.removeView(floatingView)
            } catch (e: Exception) {}
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
