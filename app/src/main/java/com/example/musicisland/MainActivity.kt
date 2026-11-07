package com.example.musicisland

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    var hasOverlay by remember { mutableStateOf(Settings.canDrawOverlays(context)) }
    var serviceRunning by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("MUSIC ISLAND", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(20.dp))

        // Tombol Izin Overlay
        Button(
            onClick = {
                if (!Settings.canDrawOverlays(context)) {
                    context.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}")))
                }
            },
            modifier = Modifier.fillMaxWidth(0.8f),
            enabled = !hasOverlay
        ) {
            Text(if (hasOverlay) "✅ Overlay Diizinkan" else "1. Izinkan Overlay")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tombol Aktifkan Service (Persis seperti di App Anda)
        Button(
            onClick = {
                try {
                    val intent = Intent(context, FloatingService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(intent)
                    } else {
                        context.startService(intent)
                    }
                    serviceRunning = true
                } catch (e: Exception) {
                    Toast.makeText(context, "Gagal: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier.fillMaxWidth(0.8f),
            enabled = hasOverlay && !serviceRunning
        ) {
            Text("2. Aktifkan Music Island")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedButton(
            onClick = {
                context.stopService(Intent(context, FloatingService::class.java))
                serviceRunning = false
            },
            modifier = Modifier.fillMaxWidth(0.8f),
            enabled = serviceRunning
        ) {
            Text("Matikan Service")
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            hasOverlay = Settings.canDrawOverlays(context)
            delay(1000)
        }
    }
}
