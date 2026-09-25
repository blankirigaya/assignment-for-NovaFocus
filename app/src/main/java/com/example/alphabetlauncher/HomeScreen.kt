package com.example.alphabetlauncher

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AppRow(app: AppInfo, onClick: () -> Unit) {
    val image = remember(app.packageName) {
        app.icon.toBitmap(config = Bitmap.Config.ARGB_8888).asImageBitmap()
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(bitmap = image, contentDescription = app.name, modifier = Modifier.size(34.dp))
        Text(
            text = app.name,
            color = Color.White,
            fontSize = 15.sp,
            modifier = Modifier.padding(start = 14.dp)
        )
    }
}

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    var apps by remember { mutableStateOf(emptyList<AppInfo>()) }
    var now by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        apps = withContext(Dispatchers.IO) { AppRepository.load(context).first }
    }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            now = System.currentTimeMillis()
        }
    }

    val timeText = remember(now) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(now))
    }
    val dateText = remember(now) {
        SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date(now))
    }
    val favourites = remember(apps) { apps.take(6) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 24.dp, top = 64.dp, end = 8.dp, bottom = 24.dp)
            ) {
                Text(text = timeText, color = Color.White, fontSize = 64.sp, fontWeight = FontWeight.Light)
                Text(text = dateText, color = Color(0xFFB0B0B0), fontSize = 15.sp)
                Spacer(modifier = Modifier.height(28.dp))
                favourites.forEach { app ->
                    AppRow(app = app) { AppLauncher.launch(context, app) }
                }
            }
            AlphabetBar()
        }
    }
}
