package com.example.musicisland

import android.app.*
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class FloatingService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: ComposeView

    override fun onCreate() {
        super.onCreate()
        
        try {
            // 1. Buat Notifikasi Sederhana
            val channelId = "music_island_channel"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(channelId, "Music Island", NotificationManager.IMPORTANCE_LOW)
                getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
            }

            val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Notification.Builder(this, channelId)
            } else {
                @Suppress("DEPRECATION")
                Notification.Builder(this)
            }.setContentTitle("Music Island")
             .setSmallIcon(android.R.drawable.ic_media_play)
             .build()

            // 2. Panggil startForeground standar (sama seperti app asli Anda)
            startForeground(1, notification)

            // 3. Siapkan Window Manager
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            
            // 4. Buat UI Compose
            floatingView = ComposeView(this).apply {
                setContent {
                    PillUI()
                }
            }

            // 5. Parameter Overlay (sama seperti app asli Anda)
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

            // 6. Tampilkan Pill
            windowManager.addView(floatingView, params)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Gagal Service: ${e.message}", Toast.LENGTH_LONG).show()
            stopSelf()
        }
    }

    @Composable
    fun PillUI() {
        Row(
            modifier = Modifier
                .background(Color.Black, RoundedCornerShape(30.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(30.dp).background(Color(0xFFFF6B9D), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) { Text("🎵", fontSize = 16.sp) }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text("Music Island", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("Siap Memutar", color = Color(0xFFAAAAAA), fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Box(
                modifier = Modifier
                    .background(Color(0xFFFF6B9D), RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .clickable {
                        val title = MusicManager.playRandomSong(this@FloatingService)
                        Toast.makeText(this@FloatingService, "Memutar: $title", Toast.LENGTH_SHORT).show()
                    },
                contentAlignment = Alignment.Center
            ) { Text("Play", color = Color.White, fontSize = 12.sp) }
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
