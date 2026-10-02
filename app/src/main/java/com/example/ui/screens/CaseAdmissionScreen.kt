package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseAdmissionScreen(
    clinicalCase: ClinicalCase,
    onStartCase: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MedDeepBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tiếp Nhận Bệnh Nhân",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedTextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
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
                border = BorderStroke(1.dp, MedCardStroke)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = onStartCase,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("start_simulation_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MedTealPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "BẮT ĐẦU KHÁM & XỬ TRÍ",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Patient Identification Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, MedCardStroke, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = MedSurfaceDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(MedTealDark, RoundedCornerShape(20.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Badge,
                                    contentDescription = null,
                                    tint = MedCyanGlow,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = clinicalCase.patientName,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MedTextPrimary
                                )
                                Text(
                                    text = "${clinicalCase.patientAge} tuổi • Giới: ${clinicalCase.patientGender}",
                                    fontSize = 13.sp,
                                    color = MedTextSecondary
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .background(Color(clinicalCase.triageLevel.colorHex).copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .border(1.dp, Color(clinicalCase.triageLevel.colorHex), RoundedCornerShape(8.dp))
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

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = MedCardStroke, thickness = 0.8.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "LÝ DO VÀO VIỆN (CHIEF COMPLAINT):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedTextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "\"${clinicalCase.chiefComplaint}\"",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MedWarningAmber,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "BỐI CẢNH TIẾP NHẬN BAN ĐẦU:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedTextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = clinicalCase.admissionStory,
                        fontSize = 13.sp,
                        color = MedTextPrimary,
                        lineHeight = 20.sp
                    )
                }
            }

            // Initial Triage Vital Signs
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, MedCardStroke, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1629))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MonitorHeart,
                            contentDescription = null,
                            tint = MedEcgGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DẤU HIỆU SINH TỒN LÚC TIẾP NHẬN (TRIAGE VITALS)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedEcgGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        VitalParam(label = "Mạch (HR)", value = "${clinicalCase.initialVitals.heartRate} l/p")
                        VitalParam(label = "Huyết áp (BP)", value = clinicalCase.initialVitals.bpString)
                        VitalParam(label = "SpO2", value = "${clinicalCase.initialVitals.spO2}%")
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        VitalParam(label = "Nhịp thở (RR)", value = "${clinicalCase.initialVitals.respiratoryRate} l/p")
                        VitalParam(label = "Thân nhiệt", value = "${clinicalCase.initialVitals.temperature}°C")
                        VitalParam(label = "Thang điểm đau", value = "${clinicalCase.initialVitals.painScore}/10")
                    }
                }
            }

            // Clinical Mission & Constraints
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, MedCardStroke, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = MedSurfaceDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AssignmentLate,
                            contentDescription = null,
                            tint = MedCyanGlow,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "MỤC TIÊU & QUY TẮC MÔ PHỎNG",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedCyanGlow
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    RuleRow(
                        icon = Icons.Default.Chat,
                        title = "Hỏi bệnh AI Patient & Khám lâm sàng",
                        desc = "Đặt câu hỏi trực tiếp cho bệnh nhân bằng văn bản hoặc chọn câu hỏi nhanh. Thăm khám từng hệ cơ quan có trọng tâm."
                    )
                    RuleRow(
                        icon = Icons.Default.AttachMoney,
                        title = "Diagnostic Stewardship (Quản trị cận lâm sàng)",
                        desc = "Ngân sách cấp: ${(clinicalCase.budgetVnd / 1000)}k đ. Chỉ định xét nghiệm thừa bừa bãi sẽ bị trừ điểm lãng phí nguồn lực!"
                    )
                    RuleRow(
                        icon = Icons.Default.Timer,
                        title = "Thời gian cấp cứu",
                        desc = "Giới hạn thời gian: ${clinicalCase.timeLimitMinutes} phút. Mỗi hành động khám hoặc xét nghiệm đều tiêu tốn thời gian thực tế."
                    )
                    RuleRow(
                        icon = Icons.Default.Warning,
                        title = "An toàn người bệnh & Chống chỉ định",
                        desc = "Quyết định xử trí sai có thể làm bệnh nhân tụt huyết áp, trụy mạch hoặc suy hô hấp nặng hơn."
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun VitalParam(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 11.sp, color = MedTextMuted)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MedTextPrimary)
    }
}

@Composable
private fun RuleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MedTealPrimary,
            modifier = Modifier
                .size(18.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MedTextPrimary
            )
            Text(
                text = desc,
                fontSize = 12.sp,
                color = MedTextSecondary,
                lineHeight = 16.sp
            )
        }
    }
}
