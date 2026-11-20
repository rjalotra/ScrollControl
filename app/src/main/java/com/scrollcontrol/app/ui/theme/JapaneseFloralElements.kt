package com.scrollcontrol.app.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// Japanese Aesthetic Floral Palette
val SakuraPink = Color(0xFFFF7597)
val SakuraSoft = Color(0xFFFFCCD5)
val SakuraDeep = Color(0xFFE63956)
val SakuraGlow = Color(0x40FF7597)
val ZenDarkBackground = Color(0xFF0F0E13)
val ZenCardSurface = Color(0xFF18151E)
val ZenCardSurfaceLight = Color(0xFF221E2A)
val ZenActiveGreen = Color(0xFF48CA9B)
val ZenMutedText = Color(0xFFA59EAF)
val GoldAccent = Color(0xFFFFC371)

/**
 * Procedural Japanese Sakura (Cherry Blossom) flower drawn close-up with 5 cleft petals,
 * inner pistils, and golden pollen tips for a calming, positive, minimalist vibe.
 */
@Composable
fun SakuraFlower(
    modifier: Modifier = Modifier.size(56.dp),
    petalGradient: List<Color> = listOf(Color(0xFFFF94AF), Color(0xFFFF5277)),
    centerColor: Color = Color(0xFF9E1935),
    stamenColor: Color = Color(0xFFFFD166)
) {
    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val r = size.minDimension / 2f

        // Draw 5 notched Sakura petals
        val petalCount = 5
        val angleStep = (2 * PI / petalCount).toFloat()

        for (i in 0 until petalCount) {
            val baseAngle = i * angleStep - (PI / 2).toFloat()
            drawSakuraPetal(
                cx = cx,
                cy = cy,
                radius = r * 0.90f,
                angle = baseAngle,
                gradient = petalGradient
            )
        }

        // Center Pistil Disc
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(centerColor, Color.Transparent),
                center = Offset(cx, cy),
                radius = r * 0.35f
            ),
            radius = r * 0.30f,
            center = Offset(cx, cy)
        )

        // Radiating Golden Stamens
        val stamenCount = 10
        for (j in 0 until stamenCount) {
            val stamenAngle = (j * 2 * PI / stamenCount).toFloat()
            val stamenLen = r * 0.32f
            val endX = cx + cos(stamenAngle) * stamenLen
            val endY = cy + sin(stamenAngle) * stamenLen

            drawLine(
                color = stamenColor.copy(alpha = 0.8f),
                start = Offset(cx, cy),
                end = Offset(endX, endY),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawCircle(
                color = stamenColor,
                radius = 2.dp.toPx(),
                center = Offset(endX, endY)
            )
        }
    }
}

/**
 * Draws an individual Sakura petal featuring the iconic notched/heart tip cleft.
 */
private fun DrawScope.drawSakuraPetal(
    cx: Float,
    cy: Float,
    radius: Float,
    angle: Float,
    gradient: List<Color>
) {
    val spreadAngle = 0.38f
    val p1Angle = angle - spreadAngle
    val p2Angle = angle + spreadAngle

    val p1x = cx + cos(p1Angle) * radius * 0.70f
    val p1y = cy + sin(p1Angle) * radius * 0.70f

    val tip1x = cx + cos(angle - 0.12f) * radius
    val tip1y = cy + sin(angle - 0.12f) * radius

    val cleftX = cx + cos(angle) * (radius * 0.85f)
    val cleftY = cy + sin(angle) * (radius * 0.85f)

    val tip2x = cx + cos(angle + 0.12f) * radius
    val tip2y = cy + sin(angle + 0.12f) * radius

    val p2x = cx + cos(p2Angle) * radius * 0.70f
    val p2y = cy + sin(p2Angle) * radius * 0.70f

    val path = Path().apply {
        moveTo(cx, cy)
        cubicTo(cx, cy, p1x, p1y, tip1x, tip1y)
        quadraticTo(cleftX, cleftY, tip2x, tip2y)
        cubicTo(tip2x, tip2y, p2x, p2y, cx, cy)
        close()
    }

    drawPath(
        path = path,
        brush = Brush.linearGradient(
            colors = gradient,
            start = Offset(cx, cy),
            end = Offset(tip1x, tip1y)
        )
    )

    // Subtle delicate petal vein/highlight line
    drawPath(
        path = path,
        color = Color(0x33FFFFFF),
        style = Stroke(width = 1.dp.toPx())
    )
}

/**
 * Minimalist floating petal motif for headers and corner decoration.
 */
@Composable
fun SinglePetal(
    modifier: Modifier = Modifier.size(18.dp),
    tint: Color = SakuraPink
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.5f, 0f)
            cubicTo(w * 0.9f, h * 0.2f, w, h * 0.7f, w * 0.5f, h)
            cubicTo(0f, h * 0.7f, w * 0.1f, h * 0.2f, w * 0.5f, 0f)
            close()
        }
        drawPath(
            path = path,
            color = tint.copy(alpha = 0.85f)
        )
    }
}
