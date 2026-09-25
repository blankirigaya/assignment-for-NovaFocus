package com.example.alphabetlauncher

import android.graphics.Paint
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.roundToInt

val BarLetters = ('A'..'Z').toList()

fun yToLetter(y: Float, height: Float): Char {
    val rowH = height / 28f
    val index = ((y - 1.5f * rowH) / rowH).roundToInt().coerceIn(0, 25)
    return BarLetters[index]
}

fun bendOffset(letterY: Float, touchY: Float, radius: Float, maxBend: Float): Float {
    val distance = abs(letterY - touchY)
    val t = (1f - distance / radius).coerceIn(0f, 1f)
    val smooth = t * t * (3f - 2f * t)
    return -maxBend * smooth
}

@Composable
fun AlphabetBar(
    selectedLetter: Char?,
    isDragging: Boolean,
    onLetter: (Char) -> Unit,
    onRelease: () -> Unit,
    modifier: Modifier = Modifier
) {
    val calm by animateFloatAsState(
        targetValue = if (isDragging) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium),
        label = "calm"
    )
    var dragY by remember { mutableStateOf<Float?>(null) }
    val latestLetter by rememberUpdatedState(onLetter)
    val latestRelease by rememberUpdatedState(onRelease)
    val paint = remember {
        Paint().apply {
            color = android.graphics.Color.WHITE
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
    }
    val bubblePaint = remember {
        Paint().apply {
            color = android.graphics.Color.BLACK
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
            isAntiAlias = true
        }
    }
    val names = remember { Array(26) { BarLetters[it].toString() } }
    val selectedName = remember(selectedLetter) { selectedLetter?.toString() }
    val active = dragY != null && (isDragging || calm > 0.01f)

    Canvas(
        modifier = modifier
            .width(56.dp)
            .fillMaxHeight()
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    val h = size.height.toFloat()
                    dragY = down.position.y
                    latestLetter(yToLetter(down.position.y, h))
                    val id = down.id
                    var released = false
                    while (!released) {
                        val event = awaitPointerEvent()
                        for (change in event.changes) {
                            if (change.id != id) continue
                            if (change.pressed) {
                                dragY = change.position.y
                                latestLetter(yToLetter(change.position.y, h))
                            } else {
                                released = true
                            }
                        }
                    }
                    latestRelease()
                }
            }
    ) {
        val h = size.height
        val w = size.width
        val rowH = h / 28f
        val radius = h * 0.16f
        val maxBend = 56.dp.toPx()
        val baseX = w - 14.dp.toPx()
        val y = dragY
        val normalSize = 12.sp.toPx()
        val bigSize = 20.sp.toPx()
        for (row in 0..27) {
            val centerY = rowH * (row + 0.5f)
            val label = when (row) {
                0 -> "*"
                27 -> "."
                else -> names[row - 1]
            }
            val isSelected = row in 1..26 && BarLetters[row - 1] == selectedLetter && isDragging
            val offsetX = if (y != null && active) bendOffset(centerY, y, radius, maxBend) * calm else 0f
            paint.textSize = if (isSelected) bigSize else normalSize
            paint.alpha = if (isSelected) 255 else 180
            paint.isFakeBoldText = isSelected
            drawContext.canvas.nativeCanvas.drawText(
                label,
                baseX + offsetX,
                centerY - (paint.descent() + paint.ascent()) / 2f,
                paint
            )
        }
        if (isDragging && selectedName != null && y != null) {
            val bubbleCx = w - 96.dp.toPx()
            val bubbleCy = y.coerceIn(rowH, h - rowH)
            val bubbleR = 24.dp.toPx()
            drawCircle(color = Color.White, radius = bubbleR, center = Offset(bubbleCx, bubbleCy))
            bubblePaint.textSize = 22.sp.toPx()
            drawContext.canvas.nativeCanvas.drawText(
                selectedName,
                bubbleCx,
                bubbleCy - (bubblePaint.descent() + bubblePaint.ascent()) / 2f,
                bubblePaint
            )
        }
    }
}
