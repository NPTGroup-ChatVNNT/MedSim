package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VitalSigns
import com.example.ui.theme.*

@Composable
fun VitalSignsMonitorBar(
    vitals: VitalSigns,
    elapsedMinutes: Int,
    remainingBudgetVnd: Int,
    modifier: Modifier = Modifier
) {
    // Pulse animation for ECG sweep
    val infiniteTransition = rememberInfiniteTransition(label = "ecg_sweep")
    val sweepProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep_progress"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF070D18))
            .border(1.dp, MedCardStroke, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        // Monitor Top Row: Title & Timer & Budget
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(MedEcgGreen, RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "BEDSIDE MONITOR LEAD II",
                    color = MedTextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Timer
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Thời gian",
                        tint = MedWarningAmber,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "T+ ${elapsedMinutes}m",
                        color = MedWarningAmber,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Budget
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = "Ngân sách",
                        tint = MedCyanGlow,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${remainingBudgetVnd / 1000}k đ",
                        color = if (remainingBudgetVnd > 1_000_000) MedCyanGlow else MedCriticalRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Animated ECG Canvas
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .background(Color(0xFF03070E), RoundedCornerShape(6.dp))
        ) {
            val width = size.width
            val height = size.height
            val midY = height / 2f

            val path = Path()
            val pointsCount = 40
            val sweepX = sweepProgress * width

            path.moveTo(0f, midY)
            for (i in 0 until pointsCount) {
                val x = (i.toFloat() / pointsCount) * width
                val seg = i % 10
                val y = when (seg) {
                    2 -> midY - 6f // P wave
                    4 -> midY + 4f // Q wave
                    5 -> midY - 26f // R peak
                    6 -> midY + 12f // S wave
                    8 -> midY - 9f // T wave
                    else -> midY
                }
                path.lineTo(x, y)
            }

            // Draw background grid lines
            for (gx in 0..(width.toInt()) step 40) {
                drawLine(
                    color = Color(0x1500FFB3),
                    start = Offset(gx.toFloat(), 0f),
                    end = Offset(gx.toFloat(), height),
                    strokeWidth = 0.5f
                )
            }

            // Draw ECG wave
            drawPath(
                path = path,
                color = MedEcgGreen,
                style = Stroke(width = 2.5f)
            )

            // Draw glow scanner head
            drawLine(
                color = Color.White.copy(alpha = 0.85f),
                start = Offset(sweepX, 0f),
                end = Offset(sweepX, height),
                strokeWidth = 2.5f
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Vitals Parameter Readouts Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Heart Rate
            VitalBox(
                label = "HR (bpm)",
                value = vitals.heartRate.toString(),
                unit = "l/p",
                color = if (vitals.heartRate in 60..100) MedEcgGreen else MedCriticalRed,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = MedCriticalRed,
                        modifier = Modifier.size(12.dp)
                    )
                }
            )

            // Blood Pressure
            VitalBox(
                label = "NIBP (mmHg)",
                value = "${vitals.systolicBp}/${vitals.diastolicBp}",
                unit = "MAP ${((2 * vitals.diastolicBp + vitals.systolicBp) / 3)}",
                color = if (vitals.systolicBp in 100..139) MedCyanGlow else MedWarningAmber
            )

            // SpO2
            VitalBox(
                label = "SpO2 (%)",
                value = "${vitals.spO2}%",
                unit = if (vitals.spO2 >= 95) "Khí phòng" else "Hạ oxy!",
                color = if (vitals.spO2 >= 94) MedTealPrimary else MedCriticalRed
            )

            // RR
            VitalBox(
                label = "RR (/phút)",
                value = vitals.respiratoryRate.toString(),
                unit = "Nhịp thở",
                color = if (vitals.respiratoryRate in 12..20) MedTextPrimary else MedWarningAmber
            )

            // Temp
            VitalBox(
                label = "TEMP (°C)",
                value = "${vitals.temperature}°",
                unit = "Nhiệt độ",
                color = if (vitals.temperature < 38.0f) MedTextSecondary else MedCriticalRed
            )
        }
    }
}

@Composable
private fun VitalBox(
    label: String,
    value: String,
    unit: String,
    color: Color,
    icon: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .background(Color(0xFF0F1B2E), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon?.invoke()
            if (icon != null) Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = label,
                fontSize = 9.sp,
                color = MedTextMuted,
                fontWeight = FontWeight.SemiBold
            )
        }
        Text(
            text = value,
            fontSize = 15.sp,
            color = color,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = unit,
            fontSize = 8.sp,
            color = MedTextSecondary
        )
    }
}
