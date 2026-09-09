package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrightBlue
import com.example.ui.theme.DeepBluePrimary
import com.example.ui.theme.LightSkyBlue
import com.example.ui.theme.PaleSkyBlue
import java.util.Locale

@Composable
fun HydrationProgressCircle(
    consumedMl: Int,
    targetMl: Int,
    remainingMl: Int,
    percentage: Int,
    modifier: Modifier = Modifier
) {
    val targetProgress = if (targetMl > 0) (consumedMl.toFloat() / targetMl).coerceIn(0f, 1f) else 0f

    // Smooth progress animation optimized for low-end devices
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "water_progress_animation"
    )

    val consumedLitres = String.format(Locale.US, "%.1f", consumedMl / 1000f)
    val targetLitres = String.format(Locale.US, "%.1f", targetMl / 1000f)
    val remainingLitres = String.format(Locale.US, "%.1f", remainingMl / 1000f)

    val trackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
    val circleGradient = Brush.sweepGradient(
        colors = listOf(
            DeepBluePrimary,
            BrightBlue,
            LightSkyBlue,
            BrightBlue,
            DeepBluePrimary
        )
    )

    Box(
        modifier = modifier
            .size(260.dp)
            .testTag("hydration_progress_circle"),
        contentAlignment = Alignment.Center
    ) {
        // Inner subtle water tint circle that visually fills up
        Box(
            modifier = Modifier
                .size(216.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            // Simulated water fill level inside circle
            Canvas(modifier = Modifier.fillMaxSize()) {
                val fillHeight = size.height * animatedProgress
                val startY = size.height - fillHeight
                if (fillHeight > 0f) {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                LightSkyBlue.copy(alpha = 0.18f),
                                BrightBlue.copy(alpha = 0.32f)
                            ),
                            startY = startY,
                            endY = size.height
                        ),
                        topLeft = Offset(0f, startY),
                        size = Size(size.width, fillHeight)
                    )
                }
            }
        }

        // Circular Progress Ring
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 14.dp.toPx()
            val diameter = size.minDimension - strokeWidth
            val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)
            val arcSize = Size(diameter, diameter)

            // Background Track
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Animated Foreground Progress Arc
            if (animatedProgress > 0f) {
                drawArc(
                    brush = circleGradient,
                    startAngle = -90f,
                    sweepAngle = animatedProgress * 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }

        // Center Content hierarchy adhering strictly to user guidelines:
        // 💧
        // 1.4 L
        // of 3.0 L
        // 47% completed
        // 1.6 L remaining
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.WaterDrop,
                contentDescription = "Water drop indicator",
                tint = BrightBlue,
                modifier = Modifier
                    .size(28.dp)
                    .testTag("water_drop_icon")
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Visually dominant consumed amount
            Text(
                text = "$consumedLitres L",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 34.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("text_consumed_amount")
            )

            Text(
                text = "of $targetLitres L",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("text_target_amount")
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "$percentage% completed",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = BrightBlue,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("text_percentage_completed")
            )

            Text(
                text = "$remainingLitres L remaining",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("text_remaining_amount")
            )
        }
    }
}
