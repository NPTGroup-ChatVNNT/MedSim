package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClinicalCase
import com.example.ui.theme.*

@Composable
fun CaseCard(
    clinicalCase: ClinicalCase,
    bestScore: Int?,
    onCaseSelected: (ClinicalCase) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, MedCardStroke, RoundedCornerShape(16.dp))
            .clickable { onCaseSelected(clinicalCase) }
            .testTag("case_card_${clinicalCase.id}"),
        colors = CardDefaults.cardColors(containerColor = MedSurfaceDark)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Specialty & Triage & Difficulty
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Specialty badge
                Box(
                    modifier = Modifier
                        .background(MedTealDark.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                        .border(1.dp, MedTealPrimary.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = clinicalCase.specialty,
                        color = MedTealPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Triage Badge
                Box(
                    modifier = Modifier
                        .background(Color(clinicalCase.triageLevel.colorHex).copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = clinicalCase.triageLevel.label,
                        color = Color(clinicalCase.triageLevel.colorHex),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title
            Text(
                text = clinicalCase.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MedTextPrimary,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Patient summary
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MedTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${clinicalCase.patientName} • ${clinicalCase.patientAge} tuổi • ${clinicalCase.patientGender}",
                    fontSize = 13.sp,
                    color = MedTextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Chief Complaint quote
            Text(
                text = "💬 \"${clinicalCase.chiefComplaint}\"",
                fontSize = 13.sp,
                color = MedWarningAmber,
                fontWeight = FontWeight.Normal,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = MedCardStroke, thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Footer: Best Score & Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Điểm cao nhất",
                        tint = if (bestScore != null && bestScore > 0) Color(0xFFFFD166) else MedTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (bestScore != null && bestScore > 0) "Điểm cao nhất: $bestScore/100" else "Chưa làm ca này",
                        fontSize = 12.sp,
                        color = if (bestScore != null && bestScore > 0) Color(0xFFFFD166) else MedTextMuted,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = "Vào nhận bệnh",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedTealPrimary
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MedTealPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
