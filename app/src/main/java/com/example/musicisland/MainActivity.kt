package com.example.musicisland

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var btnStart: Button
    private lateinit var switchPill: Switch
    private lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStart = findViewById(R.id.btnStart)
        switchPill = findViewById(R.id.switchPill)
        tvStatus = findViewById(R.id.tvStatus)

        // 1. Cek Status Izin Overlay Saat Aplikasi Dibuka
        updateUIStatus()

        // 2. Tombol untuk Meminta Izin Overlay
        btnStart.setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
                Toast.makeText(this, "Silakan izinkan, lalu kembali ke aplikasi.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Izin Overlay sudah diberikan.", Toast.LENGTH_SHORT).show()
                updateUIStatus()
            }
        }

        // 3. Logika Switch (Menyalakan / Mematikan Service)
        switchPill.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!Settings.canDrawOverlays(this)) {
                    // Jika belum ada izin, kembalikan switch ke Off
                    switchPill.isChecked = false
                    Toast.makeText(this, "Izinkan Overlay terlebih dahulu!", Toast.LENGTH_SHORT).show()
                } else {
                    // Cek Izin Notifikasi (Android 13+)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                            // Minta izin notifikasi
                            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
                            return@setOnCheckedChangeListener
                        }
                    }
                    // Jalankan Service
                    startFloatingService()
                }
            } else {
                // Matikan Service
                stopFloatingService()
            }
        }
    }

    // Fungsi Menjalankan Service
    private fun startFloatingService() {
        try {
            val intent = Intent(this, FloatingService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
            Toast.makeText(this, "Pill Diaktifkan!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal menyalakan: ${e.message}", Toast.LENGTH_LONG).show()
            switchPill.isChecked = false
        }
    }

    // Fungsi Mematikan Service
    private fun stopFloatingService() {
        val intent = Intent(this, FloatingService::class.java)
        stopService(intent)
        Toast.makeText(this, "Pill Dimatikan!", Toast.LENGTH_SHORT).show()
    }

    // Update Tampilan Teks Status
    private fun updateUIStatus() {
        if (Settings.canDrawOverlays(this)) {
            tvStatus.text = "Status: Siap"
            btnStart.text = "Overlay Diizinkan"
            btnStart.isEnabled = false
            switchPill.isEnabled = true
        } else {
            tvStatus.text = "Status: Butuh Izin Overlay"
            btnStart.text = "1. Izinkan Overlay"
            btnStart.isEnabled = true
            switchPill.isEnabled = false
        }
    }

    // Callback setelah pengguna merespon permintaan izin notifikasi
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Jika diizinkan, jalankan service
                startFloatingService()
            } else {
                Toast.makeText(this, "Izin notifikasi ditolak. Pill mungkin tidak muncul.", Toast.LENGTH_LONG).show()
                switchPill.isChecked = false
            }
        }
    }

    // Cek status izin setiap kali pengguna kembali ke aplikasi
    override fun onResume() {
        super.onResume()
        updateUIStatus()
    }
}
