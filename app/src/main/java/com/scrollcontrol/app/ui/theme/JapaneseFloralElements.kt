package com.scrollcontrol.app.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.scrollcontrol.app.R
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// Reference Color Palette extracted directly from the user's photograph
val TwigWoodColor = Color(0xFF4A252C)
val TwigWoodDark = Color(0xFF2E151A)
val SakuraPetalSoft = Color(0xFFFBE3E9)
val SakuraPetalMid = Color(0xFFEDA3B4)
val SakuraPetalDeep = Color(0xFFC74360)
val SakuraPollenGold = Color(0xFFFFBF42)
val SakuraBudCrimson = Color(0xFF9E233E)
val LeafBronze = Color(0xFF5E2730)

/**
 * Hero Banner displaying the authentic cherry blossom branch photo with
 * elegant dark vignette and gradient scrims that blend naturally into the UI.
 */
@Composable
fun SakuraBranchHero(
    modifier: Modifier = Modifier,
    height: Int = 140
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .clip(RoundedCornerShape(22.dp))
    ) {
        Image(
            painter = painterResource(id = R.drawable.sakura_branch),
            contentDescription = "Japanese cherry blossom branch",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient overlay for positive, serene dark atmosphere and seamless edge blending
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x800F0E13),
                            Color(0xF00F0E13)
                        )
                    )
                )
        )

        // Horizontal vignette overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0x990F0E13),
                            Color.Transparent,
                            Color(0x990F0E13)
                        )
                    )
                )
        )
    }
}

/**
 * Procedural Canvas drawing an organic woody twig growing across the screen
 * with delicate 5-petal cherry blossoms and pink buds sprouting directly from it.
 */
@Composable
fun SakuraTwigCanvas(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // 1. Draw Main Woody Branch weaving across the screen
        val branchPath = Path().apply {
            moveTo(0f, h * 0.65f)
            cubicTo(
                w * 0.30f, h * 0.68f,
                w * 0.50f, h * 0.52f,
                w * 0.75f, h * 0.40f
            )
            quadraticTo(w * 0.88f, h * 0.35f, w, h * 0.28f)
        }

        // Branch Shadow / Base
        drawPath(
            path = branchPath,
            color = TwigWoodDark,
            style = Stroke(
                width = 5.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Branch Highlight
        drawPath(
            path = branchPath,
            brush = Brush.linearGradient(
                colors = listOf(TwigWoodColor, Color(0xFF6B3640), TwigWoodDark),
                start = Offset(0f, h * 0.65f),
                end = Offset(w, h * 0.28f)
            ),
            style = Stroke(
                width = 3.5.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // 2. Offshoot twigs
        // Offshoot 1: Upper left
        drawOffshootTwig(
            startX = w * 0.28f, startY = h * 0.64f,
            controlX = w * 0.32f, controlY = h * 0.45f,
            endX = w * 0.38f, endY = h * 0.36f,
            thickness = 2.5.dp.toPx()
        )

        // Offshoot 2: Mid-right going downward
        drawOffshootTwig(
            startX = w * 0.68f, startY = h * 0.44f,
            controlX = w * 0.72f, controlY = h * 0.55f,
            endX = w * 0.78f, endY = h * 0.68f,
            thickness = 2.2.dp.toPx()
        )

        // Offshoot 3: Top right ending
        drawOffshootTwig(
            startX = w * 0.82f, startY = h * 0.36f,
            controlX = w * 0.88f, controlY = h * 0.22f,
            endX = w * 0.94f, endY = h * 0.16f,
            thickness = 1.8.dp.toPx()
        )

        // 3. Sprouting Unopened Buds (tight teardrop shapes with crimson sepals)
        drawFlowerBud(cx = w * 0.18f, cy = h * 0.66f, radius = 5.dp.toPx(), angle = -0.6f)
        drawFlowerBud(cx = w * 0.38f, cy = h * 0.35f, radius = 6.dp.toPx(), angle = -1.2f)
        drawFlowerBud(cx = w * 0.42f, cy = h * 0.38f, radius = 5.dp.toPx(), angle = -0.4f)
        drawFlowerBud(cx = w * 0.75f, cy = h * 0.41f, radius = 6.5.dp.toPx(), angle = 0.5f)
        drawFlowerBud(cx = w * 0.94f, cy = h * 0.16f, radius = 5.5.dp.toPx(), angle = -0.8f)

        // 4. Young Bronze Leaves on the twig
        drawBronzeLeaf(cx = w * 0.86f, cy = h * 0.33f, size = 16.dp.toPx(), angle = -0.5f)

        // 5. Open Cherry Blossoms budding directly from the branch nodes
        // Centerpiece fully opened bloom
        drawBloomingFlower(
            cx = w * 0.52f,
            cy = h * 0.50f,
            flowerRadius = 26.dp.toPx(),
            rotation = 0.2f
        )

        // Left side blooming flower
        drawBloomingFlower(
            cx = w * 0.26f,
            cy = h * 0.63f,
            flowerRadius = 20.dp.toPx(),
            rotation = -0.4f
        )

        // Right side downward blooming flower
        drawBloomingFlower(
            cx = w * 0.77f,
            cy = h * 0.66f,
            flowerRadius = 22.dp.toPx(),
            rotation = 0.8f
        )

        // Top right blooming flower
        drawBloomingFlower(
            cx = w * 0.86f,
            cy = h * 0.24f,
            flowerRadius = 18.dp.toPx(),
            rotation = -0.2f
        )
    }
}

private fun DrawScope.drawOffshootTwig(
    startX: Float, startY: Float,
    controlX: Float, controlY: Float,
    endX: Float, endY: Float,
    thickness: Float
) {
    val path = Path().apply {
        moveTo(startX, startY)
        quadraticTo(controlX, controlY, endX, endY)
    }
    drawPath(
        path = path,
        color = TwigWoodColor,
        style = Stroke(width = thickness, cap = StrokeCap.Round)
    )
}

private fun DrawScope.drawFlowerBud(
    cx: Float, cy: Float,
    radius: Float,
    angle: Float
) {
    // Crimson Sepal cup
    drawCircle(
        color = TwigWoodDark,
        radius = radius * 0.65f,
        center = Offset(cx, cy)
    )
    // Pink bud tip
    val tipX = cx + cos(angle) * radius * 0.8f
    val tipY = cy + sin(angle) * radius * 0.8f
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(SakuraPetalSoft, SakuraBudCrimson),
            center = Offset(tipX, tipY),
            radius = radius
        ),
        radius = radius,
        center = Offset(tipX, tipY)
    )
}

private fun DrawScope.drawBronzeLeaf(
    cx: Float, cy: Float,
    size: Float,
    angle: Float
) {
    val path = Path().apply {
        moveTo(cx, cy)
        val tipX = cx + cos(angle) * size
        val tipY = cy + sin(angle) * size
        val perpX = -sin(angle) * (size * 0.35f)
        val perpY = cos(angle) * (size * 0.35f)

        quadraticTo(cx + perpX, cy + perpY, tipX, tipY)
        quadraticTo(cx - perpX, cy - perpY, cx, cy)
        close()
    }
    drawPath(
        path = path,
        color = LeafBronze.copy(alpha = 0.85f)
    )
}

private fun DrawScope.drawBloomingFlower(
    cx: Float, cy: Float,
    flowerRadius: Float,
    rotation: Float
) {
    val petalCount = 5
    val angleStep = (2 * PI / petalCount).toFloat()

    // 5 soft blush petals
    for (i in 0 until petalCount) {
        val angle = i * angleStep + rotation
        drawRealisticPetal(
            cx = cx,
            cy = cy,
            radius = flowerRadius,
            angle = angle
        )
    }

    // Deep Magenta Center Eye
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(SakuraBudCrimson, Color.Transparent),
            center = Offset(cx, cy),
            radius = flowerRadius * 0.38f
        ),
        radius = flowerRadius * 0.38f,
        center = Offset(cx, cy)
    )

    // Golden pollen stamens radiating from center
    val stamenCount = 10
    for (s in 0 until stamenCount) {
        val stamenAngle = (s * 2 * PI / stamenCount).toFloat() + rotation
        val length = flowerRadius * 0.38f
        val sx = cx + cos(stamenAngle) * length
        val sy = cy + sin(stamenAngle) * length

        drawLine(
            color = SakuraPetalSoft.copy(alpha = 0.9f),
            start = Offset(cx, cy),
            end = Offset(sx, sy),
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawCircle(
            color = SakuraPollenGold,
            radius = 1.8.dp.toPx(),
            center = Offset(sx, sy)
        )
    }
}

private fun DrawScope.drawRealisticPetal(
    cx: Float, cy: Float,
    radius: Float,
    angle: Float
) {
    val spread = 0.34f
    val p1x = cx + cos(angle - spread) * (radius * 0.65f)
    val p1y = cy + sin(angle - spread) * (radius * 0.65f)

    val tipX = cx + cos(angle) * radius
    val tipY = cy + sin(angle) * radius

    val p2x = cx + cos(angle + spread) * (radius * 0.65f)
    val p2y = cy + sin(angle + spread) * (radius * 0.65f)

    val path = Path().apply {
        moveTo(cx, cy)
        cubicTo(cx, cy, p1x, p1y, tipX, tipY)
        cubicTo(tipX, tipY, p2x, p2y, cx, cy)
        close()
    }

    drawPath(
        path = path,
        brush = Brush.radialGradient(
            colors = listOf(SakuraPetalSoft, SakuraPetalMid, SakuraPetalDeep),
            center = Offset(cx, cy),
            radius = radius * 1.1f
        )
    )

    // Delicate translucent petal outline
    drawPath(
        path = path,
        color = Color(0x30FFFFFF),
        style = Stroke(width = 0.8.dp.toPx())
    )
}

/**
 * Close-up single Sakura Flower widget.
 */
@Composable
fun SakuraFlower(
    modifier: Modifier = Modifier.size(56.dp),
    petalGradient: List<Color> = listOf(SakuraPetalSoft, SakuraPetalMid),
    stamenColor: Color = SakuraPollenGold
) {
    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val r = size.minDimension / 2f
        drawBloomingFlower(cx, cy, r * 0.9f, 0f)
    }
}

@Composable
fun SinglePetal(
    modifier: Modifier = Modifier.size(16.dp),
    tint: Color = SakuraPetalSoft
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
