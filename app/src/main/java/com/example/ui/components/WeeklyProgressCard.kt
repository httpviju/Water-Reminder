package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DayProgress
import com.example.ui.theme.BrightBlue
import com.example.ui.theme.DeepBluePrimary
import com.example.ui.theme.LightSkyBlue

@Composable
fun WeeklyProgressCard(
    weeklyProgress: List<DayProgress>,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(20.dp)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_weekly_progress"),
        shape = cardShape,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    shape = cardShape
                )
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Weekly Progress",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    val metDays = weeklyProgress.count { it.amountMl >= it.targetMl && it.targetMl > 0 }
                    Text(
                        text = "$metDays / ${weeklyProgress.size} Days Met",
                        style = MaterialTheme.typography.labelMedium,
                        color = BrightBlue
                    )
                }

                // 7-day Bar Chart
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    weeklyProgress.forEach { day ->
                        DayBarColumn(
                            dayProgress = day,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DayBarColumn(
    dayProgress: DayProgress,
    modifier: Modifier = Modifier
) {
    val isGoalMet = dayProgress.amountMl >= dayProgress.targetMl && dayProgress.targetMl > 0
    val barRatio = dayProgress.completionRatio

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Bar Container
        Box(
            modifier = Modifier
                .width(18.dp)
                .height(90.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
            contentAlignment = Alignment.BottomCenter
        ) {
            val barHeightFraction = barRatio.coerceIn(0.04f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(barHeightFraction)
                    .clip(RoundedCornerShape(9.dp))
                    .background(
                        if (isGoalMet) BrightBlue
                        else if (dayProgress.isToday) DeepBluePrimary
                        else LightSkyBlue
                    )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = dayProgress.dayLabel.take(3),
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                fontWeight = if (dayProgress.isToday) FontWeight.Bold else FontWeight.Normal
            ),
            color = if (dayProgress.isToday) BrightBlue else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
