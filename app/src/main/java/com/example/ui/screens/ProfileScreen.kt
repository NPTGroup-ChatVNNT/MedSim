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
import com.example.data.local.CaseAttemptEntity
import com.example.data.local.UserProfileEntity
import com.example.data.repository.MedSimRepository
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userProfile: UserProfileEntity?,
    pastAttempts: List<CaseAttemptEntity>,
    repository: MedSimRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var isSigningIn by remember { mutableStateOf(false) }
    var syncMessage by remember { mutableStateOf<String?>(null) }

    BackHandler {
        onBack()
    }

    val level = userProfile?.level ?: 1
    val currentXp = userProfile?.currentXp ?: 0
    val xpInLevel = currentXp % 500
    val xpProgress = xpInLevel / 500f
    val rankTitle = userProfile?.clinicalRank ?: "Sinh viên Y khoa Lâm sàng"

    val averageScore = remember(pastAttempts) {
        if (pastAttempts.isEmpty()) 0 else pastAttempts.map { it.score }.average().toInt()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MedDeepBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Hồ Sơ Bác Sĩ & Tiến Độ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedTextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = MedTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MedSurfaceDark)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, MedTealPrimary.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = MedSurfaceDark)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(MedTealDark)
                                .border(2.dp, MedCyanGlow, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MedicalServices,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = userProfile?.displayName ?: "Bác sĩ Lâm sàng",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedTextPrimary
                        )

                        Text(
                            text = rankTitle,
                            fontSize = 13.sp,
                            color = MedCyanGlow,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Level & XP Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Cấp bậc: Level $level", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MedTextPrimary)
                            Text(text = "$xpInLevel / 500 XP", fontSize = 12.sp, color = MedCyanGlow)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { xpProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = MedCyanGlow,
                            trackColor = Color(0xFF131F37)
                        )
                    }
                }
            }

            // Stats Overview
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatBox(
                        title = "Ca hoàn thành",
                        value = "${userProfile?.casesCompleted ?: pastAttempts.size}",
                        icon = Icons.Default.CheckCircle,
                        tint = MedEcgGreen,
                        modifier = Modifier.weight(1f)
                    )
                    StatBox(
                        title = "Điểm TB",
                        value = "$averageScore/100",
                        icon = Icons.Default.Star,
                        tint = Color(0xFFFFD166),
                        modifier = Modifier.weight(1f)
                    )
                    StatBox(
                        title = "Tổng XP",
                        value = "$currentXp",
                        icon = Icons.Default.Bolt,
                        tint = MedCyanGlow,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Cloud Sync & Firebase Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, MedCardStroke, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E38))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CloudSync, contentDescription = null, tint = MedTealPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "KẾT NỐI FIREBASE & ĐỒNG BỘ ĐÁM MÂY",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MedTealPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Lưu trữ lịch sử ca bệnh, thành tựu lâm sàng và xếp hạng trực tiếp trên Firebase Cloud Firestore.",
                            fontSize = 12.sp,
                            color = MedTextSecondary,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                isSigningIn = true
                                coroutineScope.launch {
                                    repository.signInAnonymous()
                                    syncMessage = "Đã đồng bộ dữ liệu với Firebase thành công!"
                                    isSigningIn = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MedTealPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("firebase_sync_button")
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isSigningIn) "Đang kết nối..." else "Đăng nhập & Đồng bộ Firebase",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (syncMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = syncMessage ?: "",
                                fontSize = 12.sp,
                                color = MedEcgGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Past Attempts History
            item {
                Text(
                    text = "LỊCH SỬ THỰC HÀNH CA BỆNH (${pastAttempts.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedTextMuted
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            if (pastAttempts.isEmpty()) {
                item {
                    Text(
                        text = "Chưa có lượt thực hành nào. Hãy bắt đầu ca bệnh đầu tiên!",
                        fontSize = 12.sp,
                        color = MedTextMuted
                    )
                }
            } else {
                items(pastAttempts) { attempt ->
                    val dateFormat = remember { SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault()) }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MedCardStroke, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = MedSurfaceDark)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = attempt.caseTitle,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MedTextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${attempt.specialty} • ${dateFormat.format(Date(attempt.timestamp))}",
                                    fontSize = 11.sp,
                                    color = MedTextMuted
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Thời gian: ${attempt.timeUsedMinutes}p • Viện phí: ${attempt.budgetUsedVnd / 1000}k đ",
                                    fontSize = 11.sp,
                                    color = MedTextSecondary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (attempt.score >= 80) MedEcgGreen.copy(alpha = 0.2f)
                                        else if (attempt.score >= 60) MedCyanGlow.copy(alpha = 0.2f)
                                        else MedCriticalRed.copy(alpha = 0.2f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${attempt.score}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (attempt.score >= 80) MedEcgGreen
                                    else if (attempt.score >= 60) MedCyanGlow
                                    else MedCriticalRed
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
private fun StatBox(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
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
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MedTextPrimary)
            Text(title, fontSize = 10.sp, color = MedTextMuted)
        }
    }
}
