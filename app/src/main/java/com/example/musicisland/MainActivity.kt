package com.example.musicisland

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Kita pakai XML layout untuk Main Menu

        val btnStart = findViewById<Button>(R.id.btnStart)
        val tvStatus = findViewById<TextView>(R.id.tvStatus)

        // Cek Status Izin
        if (Settings.canDrawOverlays(this)) {
            tvStatus.text = "Status: Siap"
            btnStart.text = "Aktifkan Music Island"
        } else {
            tvStatus.text = "Status: Butuh Izin Overlay"
            btnStart.text = "Izinkan Overlay"
        }

        btnStart.setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                // Minta izin overlay
                startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
                Toast.makeText(this, "Silakan izinkan, lalu kembali ke aplikasi.", Toast.LENGTH_LONG).show()
            } else {
                // Jalankan Service
                val intent = Intent(this, FloatingService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent)
                } else {
                    startService(intent)
                }
                Toast.makeText(this, "Music Island Aktif!", Toast.LENGTH_SHORT).show()
                moveTaskToBack(true) // Minimize aplikasi
            }
        }
    }
}
