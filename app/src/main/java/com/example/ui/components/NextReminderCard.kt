package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrightBlue
import com.example.ui.theme.DeepBluePrimary
import com.example.ui.theme.SpotifyGreen

@Composable
fun NextReminderCard(
    nextReminderTime: String,
    isEnabled: Boolean,
    intervalMinutes: Int,
    countdownText: String = "",
    isAlarmRinging: Boolean = false,
    onToggle: (Boolean) -> Unit,
    onOpenTimerDialog: () -> Unit = {},
    onDismissAlarm: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(20.dp)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .clickable(enabled = isEnabled) { onOpenTimerDialog() }
            .testTag("card_next_reminder"),
        shape = cardShape,
        color = if (isAlarmRinging) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = if (isAlarmRinging) 2.dp else 1.dp,
                    color = if (isAlarmRinging) SpotifyGreen else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    shape = cardShape
                )
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isAlarmRinging) SpotifyGreen else MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isAlarmRinging) Icons.Default.NotificationsActive else if (isEnabled) Icons.Default.Timer else Icons.Default.Alarm,
                                    contentDescription = "Reminder clock",
                                    tint = if (isAlarmRinging) MaterialTheme.colorScheme.onPrimary else BrightBlue,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isAlarmRinging) "💧 ALARM RINGING!" else "Water Alarm Timer",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isAlarmRinging) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isAlarmRinging) SpotifyGreen else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (isEnabled && countdownText.isNotBlank() && !isAlarmRinging) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = countdownText,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            ),
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Text(
                                text = if (!isEnabled) {
                                    "Reminders paused"
                                } else if (isAlarmRinging) {
                                    "Time to drink water! 💧"
                                } else {
                                    "$intervalMinutes min ($nextReminderTime)"
                                },
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                ),
                                color = if (isEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.testTag("text_next_reminder_time")
                            )

                            if (isEnabled && !isAlarmRinging) {
                                Text(
                                    text = "Timer: $intervalMinutes min • Tap to set custom ⚙️",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Switch(
                        checked = isEnabled,
                        onCheckedChange = onToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = SpotifyGreen
                        ),
                        modifier = Modifier.testTag("switch_reminder_toggle")
                    )
                }

                if (isAlarmRinging) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onDismissAlarm,
                        colors = ButtonDefaults.buttonColors(containerColor = SpotifyGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                    ) {
                        Text(
                            text = "Stop / Dismiss Alarm ✕",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

