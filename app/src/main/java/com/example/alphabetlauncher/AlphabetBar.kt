package com.example.alphabetlauncher

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val BarLetters = ('A'..'Z').toList()

@Composable
fun AlphabetBar(modifier: Modifier = Modifier) {
    val paint = remember {
        Paint().apply {
            color = android.graphics.Color.WHITE
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
    }
    val names = remember { Array(26) { BarLetters[it].toString() } }
    Canvas(modifier = modifier.width(56.dp).fillMaxHeight()) {
        val rowH = size.height / 28f
        val baseX = size.width - 14.dp.toPx()
        paint.textSize = 12.sp.toPx()
        paint.alpha = 180
        for (row in 0..27) {
            val centerY = rowH * (row + 0.5f)
            val label = when (row) {
                0 -> "*"
                27 -> "."
                else -> names[row - 1]
            }
            drawContext.canvas.nativeCanvas.drawText(
                label,
                baseX,
                centerY - (paint.descent() + paint.ascent()) / 2f,
                paint
            )
        }
    }
}
