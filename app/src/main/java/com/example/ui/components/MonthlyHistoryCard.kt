package com.example.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WaterLog
import com.example.ui.theme.BrightBlue
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun MonthlyHistoryCard(
    allLogs: List<WaterLog>,
    dailyTargetMl: Int,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(20.dp)

    // Current month info
    val calendar = Calendar.getInstance()
    val monthName = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time)
    val currentMonthPrefix = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(calendar.time)

    val currentMonthLogs = allLogs.filter { it.dateString.startsWith(currentMonthPrefix) }
    val totalMl = currentMonthLogs.sumOf { it.amountMl }
    val daysWithLogs = currentMonthLogs.groupBy { it.dateString }
    val targetMetDays = daysWithLogs.count { (_, logs) -> logs.sumOf { it.amountMl } >= dailyTargetMl && dailyTargetMl > 0 }
    val activeDays = daysWithLogs.size
    val dailyAvgMl = if (activeDays > 0) totalMl / activeDays else 0

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_monthly_history"),
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
                        text = "Monthly History",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = monthName,
                        style = MaterialTheme.typography.labelMedium,
                        color = BrightBlue
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MonthStatItem(
                        label = "Total Intake",
                        value = String.format(Locale.US, "%.1f L", totalMl / 1000f)
                    )
                    MonthStatItem(
                        label = "Goals Met",
                        value = "$targetMetDays days"
                    )
                    MonthStatItem(
                        label = "Daily Avg",
                        value = String.format(Locale.US, "%.1f L", dailyAvgMl / 1000f)
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
