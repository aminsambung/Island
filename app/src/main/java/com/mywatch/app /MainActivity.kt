package com.mywatch.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyWatchApp()
        }
    }
}

@Composable
fun MyWatchApp() {
    var time by remember { mutableStateOf(getCurrentTime()) }

    LaunchedEffect(Unit) {
        while (true) {
            time = getCurrentTime()
            delay(1000)
        }
    }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF101820)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "MY WATCH",
                    color = Color.White,
                    fontSize = 28.sp
                )

                Spacer(modifier = Modifier.height(35.dp))

                Text(
                    text = time,
                    color = Color.White,
                    fontSize = 64.sp
                )

                Spacer(modifier = Modifier.height(30.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    WatchCard("❤️", "Detak jantung")
                    WatchCard("👟", "Langkah")
                }

                Spacer(modifier = Modifier.height(20.dp))

                WatchCard("🌙", "Tidur")

                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = "My Watch • Versi 1.0",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
            }
        }
    }
}

fun getCurrentTime(): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault())
        .format(Date())
}

@Composable
fun WatchCard(icon: String, title: String) {
    Card(
        modifier = Modifier.width(145.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF263746)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 30.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, color = Color.White)
        }
    }
}
