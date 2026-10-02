package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.data.model.DebriefResult
import com.example.data.repository.MedSimRepository
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebriefScreen(
    debrief: DebriefResult,
    repository: MedSimRepository,
    onBackToHome: () -> Unit,
    onRetryCase: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clinicalCase = remember { ClinicalCaseRepository.getCaseById(debrief.caseId) }
    var aiCommentary by remember { mutableStateOf<String?>(null) }
    var isLoadingAi by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    BackHandler {
        onBackToHome()
    }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            if (clinicalCase != null) {
                // Save attempt to Room and sync to Firebase
                repository.recordCaseCompletion(debrief)
                // Generate AI debrief
                val advice = repository.generateClinicalDebrief(clinicalCase, debrief)
                aiCommentary = advice
                isLoadingAi = false
            }
        }
    }

    val gradeTitle = when {
        debrief.overallScore >= 85 -> "XUẤT SẮC • MASTER CLINICIAN"
        debrief.overallScore >= 70 -> "ĐẠT CHUẨN • COMPETENT"
        debrief.overallScore >= 50 -> "CẦN RÈN LUYỆN • DEVELOPING"
        else -> "NGUY CƠ BIẾN CHỨNG • HAZARDOUS"
    }

    val gradeColor = when {
        debrief.overallScore >= 85 -> MedEcgGreen
        debrief.overallScore >= 70 -> MedCyanGlow
        debrief.overallScore >= 50 -> MedWarningAmber
        else -> MedCriticalRed
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MedDeepBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tổng Kết Lâm Sàng (Debrief)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedTextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackToHome) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Trang chủ",
                            tint = MedTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MedSurfaceDark)
            )
        },
        bottomBar = {
            Surface(
                color = MedSurfaceDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, MedCardStroke)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onRetryCase,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("retry_case_button"),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MedTealPrimary)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = MedTealPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Chơi lại ca", color = MedTealPrimary, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onBackToHome,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("back_home_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MedTealPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Home, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Về Trang Chủ", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Overall Score Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.5.dp, gradeColor, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1B33))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = debrief.caseTitle,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MedTextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = gradeTitle,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = gradeColor
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Large Score Circle
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(gradeColor.copy(alpha = 0.15f))
                                .border(2.5.dp, gradeColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${debrief.overallScore}",
                                    fontSize = 34.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MedTextPrimary
                                )
                                Text(
                                    text = "/100",
                                    fontSize = 12.sp,
                                    color = MedTextMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // XP and Resource stats
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = MedCyanGlow, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "+${debrief.xpGained} XP", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MedCyanGlow)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccessTime, contentDescription = null, tint = MedWarningAmber, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "${debrief.timeUsedMinutes} phút", fontSize = 13.sp, color = MedWarningAmber)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = MedTealPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "${debrief.budgetUsedVnd / 1000}k đ", fontSize = 13.sp, color = MedTealPrimary)
                            }
                        }
                    }
                }
            }

            // 7 Clinical Competency Breakdown Bars
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, MedCardStroke, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = MedSurfaceDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "ĐÁNH GIÁ 7 NĂNG LỰC LÂM SÀNG (COMPETENCIES)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedCyanGlow
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        CompetencyBar(title = "1. Khai thác bệnh sử", score = debrief.historyScore, color = MedTealPrimary)
                        CompetencyBar(title = "2. Khám lâm sàng", score = debrief.physicalExamScore, color = MedCyanGlow)
                        CompetencyBar(title = "3. Lập luận sinh lý bệnh", score = debrief.clinicalReasoningScore, color = MedEcgGreen)
                        CompetencyBar(title = "4. Quản trị cận lâm sàng (Chi phí)", score = debrief.diagnosticStewardshipScore, color = Color(0xFFFFD166))
                        CompetencyBar(title = "5. Độ chính xác chẩn đoán", score = debrief.diagnosticAccuracyScore, color = MedTealDark)
                        CompetencyBar(title = "6. Xử trí & Cấp cứu", score = debrief.therapeuticScore, color = Color(0xFF64DFDF))
                        CompetencyBar(title = "7. An toàn người bệnh", score = debrief.patientSafetyScore, color = if (debrief.patientSafetyScore >= 70) MedEcgGreen else MedCriticalRed)
                    }
                }
            }

            // Decision Tree Reconstruction
            item {
                Text(
                    text = "DỰNG LẠI CÂY QUYẾT ĐỊNH (DECISION TREE RECONSTRUCTION)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedTextMuted
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            if (debrief.actionTimeline.isEmpty()) {
                item {
                    Text(
                        text = "Không có quyết định nào được ghi lại.",
                        fontSize = 12.sp,
                        color = MedTextMuted
                    )
                }
            } else {
                items(debrief.actionTimeline) { action ->
                    val badgeColor = when {
                        action.isHazardous -> MedCriticalRed
                        action.isRedundant -> MedWarningAmber
                        else -> MedEcgGreen
                    }
                    val badgeText = when {
                        action.isHazardous -> "NGUY HIỂM / CHỐNG CHỈ ĐỊNH"
                        action.isRedundant -> "THỪA / LÃNG PHÍ NGUỒN LỰC"
                        else -> "CHỈ ĐỊNH HỢP LÝ / TIÊU CHUẨN"
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, badgeColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = MedSurfaceDark)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Phút ${action.timestampMinutes} • ${action.actionType}",
                                    fontSize = 11.sp,
                                    color = MedTextMuted,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Box(
                                    modifier = Modifier
                                        .background(badgeColor.copy(alpha = 0.18f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = badgeText,
                                        color = badgeColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = action.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MedTextPrimary
                            )

                            if (action.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = action.description,
                                    fontSize = 12.sp,
                                    color = MedTextSecondary,
                                    lineHeight = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "💡 Phân tích sư phạm: ${action.feedback}",
                                fontSize = 11.sp,
                                color = if (action.isHazardous) MedCriticalRed else Color(0xFFFFD166),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Senior Attending AI Debrief Commentary
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, MedTealPrimary, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E38))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(MedTealDark, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.School, contentDescription = null, tint = MedCyanGlow, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "NHẬN XÉT CỦA GIẢNG VIÊN LÂM SÀNG (AI TUTOR)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MedCyanGlow
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (isLoadingAi) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = MedCyanGlow,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Đang tổng hợp phân tích tư duy lâm sàng...",
                                    fontSize = 12.sp,
                                    color = MedTextMuted
                                )
                            }
                        } else {
                            Text(
                                text = aiCommentary ?: "",
                                fontSize = 13.sp,
                                color = MedTextPrimary,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }

            // Core Pathophysiology & Guidelines
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, MedCardStroke, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = MedSurfaceDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, tint = MedWarningAmber, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CƠ CHẾ SINH LÝ BỆNH & GUIDELINE QUỐC TẾ",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MedWarningAmber
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "CƠ CHẾ BỆNH SINH:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedTextMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = debrief.pathophysiologyNotes,
                            fontSize = 13.sp,
                            color = MedTextPrimary,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "HƯỚNG DẪN ĐIỀU TRỊ (EVIDENCE-BASED GUIDELINE):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedTextMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = debrief.guidelineRecommendations,
                            fontSize = 13.sp,
                            color = MedTealPrimary,
                            lineHeight = 18.sp
                        )

                        if (clinicalCase != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF1E3258), RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "💎 CLINICAL PEARL: ${clinicalCase.takeHomePearl}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD166),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun CompetencyBar(
    title: String,
    score: Int,
    color: Color
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, fontSize = 11.sp, color = MedTextSecondary)
            Text(text = "$score/100", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { score / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = Color(0xFF15223A)
        )
    }
}
