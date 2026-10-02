package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.cases.ClinicalCaseRepository
import com.example.data.local.CaseAttemptEntity
import com.example.data.local.UserProfileEntity
import com.example.data.model.ClinicalCase
import com.example.ui.components.CaseCard
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    userProfile: UserProfileEntity?,
    pastAttempts: List<CaseAttemptEntity>,
    onCaseSelected: (ClinicalCase) -> Unit,
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cases = remember { ClinicalCaseRepository.cases }
    var selectedCategory by remember { mutableStateOf("Tất cả") }

    val categories = remember {
        listOf("Tất cả", "Tim mạch & Cấp cứu", "Hô hấp & Hồi sức", "Tim mạch & Phẫu thuật lồng ngực")
    }

    val filteredCases = remember(selectedCategory) {
        if (selectedCategory == "Tất cả") cases
        else cases.filter { it.specialty.contains(selectedCategory) }
    }

    val bestScoresMap = remember(pastAttempts) {
        pastAttempts.groupBy { it.caseId }
            .mapValues { entry -> entry.value.maxOfOrNull { it.score } }
    }

    val level = userProfile?.level ?: 1
    val currentXp = userProfile?.currentXp ?: 0
    val xpInLevel = currentXp % 500
    val xpProgress = xpInLevel / 500f
    val rankTitle = userProfile?.clinicalRank ?: "Sinh viên Y khoa Lâm sàng"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MedDeepBackground,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MedSurfaceDark)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Header with Profile & Doctor Rank
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { onOpenProfile() }
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MedTealDark)
                                .border(1.5.dp, MedCyanGlow, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MedicalServices,
                                contentDescription = "Bác sĩ",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = userProfile?.displayName ?: "Bác sĩ Lâm sàng",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MedTextPrimary
                            )
                            Text(
                                text = rankTitle,
                                fontSize = 12.sp,
                                color = MedTealPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Level Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF1E3258))
                            .border(1.dp, MedTealPrimary, RoundedCornerShape(20.dp))
                            .clickable { onOpenProfile() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "Level",
                                tint = MedCyanGlow,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Lv.$level",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MedCyanGlow
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // XP Progress Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Tiến độ thăng hạng chức danh",
                        fontSize = 11.sp,
                        color = MedTextMuted
                    )
                    Text(
                        text = "$xpInLevel / 500 XP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MedCyanGlow
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { xpProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MedCyanGlow,
                    trackColor = Color(0xFF0F1B2E)
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Clinical Simulator Mission Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, MedCardStroke, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E38))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalHospital,
                                contentDescription = null,
                                tint = MedCriticalRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PHÒNG CẤP CỨU & HỒI SỨC TÍCH CỰC (ER/ICU)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MedCriticalRed
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Mô phỏng tư duy chẩn đoán & ra quyết định lâm sàng",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Hỏi bệnh với AI Patient, thăm khám từng hệ cơ quan, chỉ định cận lâm sàng hợp lý, xử trí cấp cứu theo phác đồ và phân tích debrief chuyên sâu.",
                            fontSize = 12.sp,
                            color = MedTextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Quick Stats Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Ca hoàn thành",
                        value = "${pastAttempts.map { it.caseId }.distinct().size}/${cases.size}",
                        icon = Icons.Default.CheckCircle,
                        iconTint = MedEcgGreen,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Điểm TB",
                        value = if (pastAttempts.isNotEmpty()) "${pastAttempts.map { it.score }.average().toInt()}/100" else "--",
                        icon = Icons.Default.Star,
                        iconTint = Color(0xFFFFD166),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Tổng XP",
                        value = "${userProfile?.currentXp ?: 0}",
                        icon = Icons.Default.EmojiEvents,
                        iconTint = MedCyanGlow,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Category Filter Chips
            item {
                Text(
                    text = "DANH MỤC CA BỆNH LÂM SÀNG",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedTextMuted
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = cat },
                            label = {
                                Text(
                                    text = cat,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = MedSurfaceDark,
                                labelColor = MedTextSecondary,
                                selectedContainerColor = MedTealDark,
                                selectedLabelColor = Color.White
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = MedCardStroke,
                                selectedBorderColor = MedTealPrimary
                            )
                        )
                    }
                }
            }

            // Cases List
            items(filteredCases) { clinicalCase ->
                CaseCard(
                    clinicalCase = clinicalCase,
                    bestScore = bestScoresMap[clinicalCase.id],
                    onCaseSelected = onCaseSelected
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, MedCardStroke, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = MedSurfaceDark)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MedTextPrimary
            )
            Text(
                text = title,
                fontSize = 10.sp,
                color = MedTextMuted
            )
        }
    }
}
