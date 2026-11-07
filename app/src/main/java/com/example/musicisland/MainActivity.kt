package com.example.musicisland

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnStart = findViewById<Button>(R.id.btnStart)
        val switchPill = findViewById<Switch>(R.id.switchPill)
        val tvStatus = findViewById<TextView>(R.id.tvStatus)

        // Cek Status Izin
        if (Settings.canDrawOverlays(this)) {
            tvStatus.text = "Status: Siap"
            btnStart.text = "Overlay Diizinkan"
            btnStart.isEnabled = false // Matikan tombol jika izin sudah ada
        } else {
            tvStatus.text = "Status: Butuh Izin Overlay"
            btnStart.text = "Izinkan Overlay"
        }

        // Tombol Minta Izin Overlay
        btnStart.setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
                Toast.makeText(this, "Silakan izinkan, lalu kembali.", Toast.LENGTH_LONG).show()
            }
        }

        // Logika Switch (Nyala = Service Jalan, Mati = Service Stop)
        switchPill.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Nyalakan Service
                val intent = Intent(this, FloatingService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent)
                } else {
                    startService(intent)
                }
                Toast.makeText(this, "Pill Diaktifkan!", Toast.LENGTH_SHORT).show()
            } else {
                // Matikan Service
                stopService(Intent(this, FloatingService::class.java))
                Toast.makeText(this, "Pill Dimatikan!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Cek status service saat aplikasi dibuka kembali
    override fun onResume() {
        super.onResume()
        // Jika izin sudah ada, pastikan tombol tidak error
        if (Settings.canDrawOverlays(this)) {
            findViewById<Button>(R.id.btnStart).text = "Overlay Diizinkan"
            findViewById<Button>(R.id.btnStart).isEnabled = false
            findViewById<TextView>(R.id.tvStatus).text = "Status: Siap"
        }
    }
}
