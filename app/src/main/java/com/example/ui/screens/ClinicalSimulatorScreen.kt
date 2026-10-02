package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.example.data.model.*
import com.example.data.repository.MedSimRepository
import com.example.ui.components.VitalSignsMonitorBar
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinicalSimulatorScreen(
    clinicalCase: ClinicalCase,
    repository: MedSimRepository,
    onCompleteCase: (DebriefResult) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    // Dynamic Patient Vitals State
    var currentVitals by remember { mutableStateOf(clinicalCase.initialVitals) }
    var elapsedMinutes by remember { mutableIntStateOf(0) }
    var remainingBudgetVnd by remember { mutableIntStateOf(clinicalCase.budgetVnd) }

    // Active Tab (0: Khám & Hỏi bệnh, 1: Chẩn đoán & Lập luận, 2: Cận lâm sàng, 3: Xử trí & Cấp cứu)
    var selectedTab by remember { mutableIntStateOf(0) }

    // Actions & History Log
    val performedActions = remember { mutableStateListOf<CaseActionRecord>() }

    // Physical Exam Done States
    val examinedSystems = remember { mutableStateListOf<String>() }

    // Diagnostic Tests Ordered States
    val orderedTests = remember { mutableStateListOf<DiagnosticTest>() }

    // Interventions Applied States
    val appliedInterventions = remember { mutableStateListOf<MedicalIntervention>() }

    // Diagnostic Reasoning State
    var selectedPrimaryDiagnosis by remember { mutableStateOf<String?>(null) }
    val selectedDifferentials = remember { mutableStateListOf<String>() }
    var selectedPathophysiology by remember { mutableStateOf<String?>(null) }

    // AI Patient Chat State
    var patientChatInput by remember { mutableStateOf("") }
    var isChatLoading by remember { mutableStateOf(false) }
    val chatMessages = remember {
        mutableStateListOf(
            Pair("patient", "Bác sĩ ơi... ngực tôi đau quá, cứu tôi với..."),
            Pair("student", "Chào bác, bác bình tĩnh nhé. Cháu đang kiểm tra cho bác ngay đây.")
        )
    }

    // Hazard Alert Dialog State
    var hazardAlertMessage by remember { mutableStateOf<String?>(null) }
    var showQuitConfirmDialog by remember { mutableStateOf(false) }

    // Custom Back Handler
    BackHandler {
        showQuitConfirmDialog = true
    }

    if (showQuitConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showQuitConfirmDialog = false },
            title = { Text("Tạm dừng ca bệnh?", fontWeight = FontWeight.Bold) },
            text = { Text("Bạn có muốn thoát khỏi phòng cấp cứu? Tiến trình hiện tại của ca bệnh này sẽ không được lưu.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showQuitConfirmDialog = false
                        onBack()
                    },
                    modifier = Modifier.testTag("confirm_quit_button")
                ) {
                    Text("Thoát", color = MedCriticalRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showQuitConfirmDialog = false }) {
                    Text("Tiếp tục điều trị")
                }
            }
        )
    }

    // Hazardous Event Warning Dialog
    if (hazardAlertMessage != null) {
        AlertDialog(
            onDismissRequest = { hazardAlertMessage = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MedCriticalRed,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "CẢNH BÁO BIẾN CHỨNG NGUY HIỂM!",
                    color = MedCriticalRed,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = hazardAlertMessage ?: "",
                    color = MedTextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { hazardAlertMessage = null },
                    colors = ButtonDefaults.buttonColors(containerColor = MedCriticalRed)
                ) {
                    Text("Đã hiểu - Cần xử trí cấp cứu bù đắp", color = Color.White)
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MedDeepBackground,
        topBar = {
            Column(modifier = Modifier.background(MedSurfaceDark)) {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = clinicalCase.patientName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MedTextPrimary
                            )
                            Text(
                                text = "${clinicalCase.title} • ${clinicalCase.patientAge}t ${clinicalCase.patientGender}",
                                fontSize = 11.sp,
                                color = MedTealPrimary
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { showQuitConfirmDialog = true }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Thoát ca",
                                tint = MedTextPrimary
                            )
                        }
                    },
                    actions = {
                        // Finish case button
                        FilledTonalButton(
                            onClick = {
                                val debrief = calculateDebrief(
                                    clinicalCase = clinicalCase,
                                    currentVitals = currentVitals,
                                    elapsedMinutes = elapsedMinutes,
                                    remainingBudgetVnd = remainingBudgetVnd,
                                    performedActions = performedActions,
                                    examinedSystemsCount = examinedSystems.size,
                                    orderedTests = orderedTests,
                                    appliedInterventions = appliedInterventions,
                                    primaryDiagnosis = selectedPrimaryDiagnosis,
                                    differentials = selectedDifferentials,
                                    pathophysiology = selectedPathophysiology
                                )
                                onCompleteCase(debrief)
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MedTealPrimary,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .testTag("finish_case_button")
                        ) {
                            Icon(imageVector = Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "KẾT THÚC CA", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MedSurfaceDark)
                )

                // Bedside Realtime Patient Monitor Bar
                VitalSignsMonitorBar(
                    vitals = currentVitals,
                    elapsedMinutes = elapsedMinutes,
                    remainingBudgetVnd = remainingBudgetVnd,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )

                // 4 Core Clinical Workflow Tabs
                PrimaryTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MedSurfaceDark,
                    contentColor = MedCyanGlow
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("1. Khám & Hỏi", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.RecordVoiceOver, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("2. Lập luận", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("3. Cận lâm sàng", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.Biotech, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = { Text("4. Xử trí", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.Vaccines, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> HistoryAndPhysicalTab(
                    clinicalCase = clinicalCase,
                    chatMessages = chatMessages,
                    isChatLoading = isChatLoading,
                    inputMessage = patientChatInput,
                    onInputChange = { patientChatInput = it },
                    onSendMessage = { question ->
                        if (question.isNotBlank()) {
                            val userMsg = question.trim()
                            chatMessages.add(Pair("student", userMsg))
                            patientChatInput = ""
                            isChatLoading = true
                            elapsedMinutes += 1
                            coroutineScope.launch {
                                val reply = repository.chatWithPatient(
                                    clinicalCase = clinicalCase,
                                    question = userMsg,
                                    history = chatMessages.takeLast(6)
                                )
                                chatMessages.add(Pair("patient", reply))
                                isChatLoading = false
                                performedActions.add(
                                    CaseActionRecord(
                                        timestampMinutes = elapsedMinutes,
                                        actionType = "HỎI BỆNH",
                                        name = "Hỏi bệnh nhân: \"$userMsg\"",
                                        description = "Câu trả lời: \"$reply\"",
                                        isGoodChoice = true,
                                        isHazardous = false,
                                        isRedundant = false,
                                        feedback = "Khai thác triệu chứng trực tiếp từ bệnh nhân."
                                    )
                                )
                            }
                        }
                    },
                    examinedSystems = examinedSystems,
                    onExamineSystem = { examItem ->
                        if (!examinedSystems.contains(examItem.id)) {
                            examinedSystems.add(examItem.id)
                            elapsedMinutes += examItem.timeCostMinutes
                            performedActions.add(
                                CaseActionRecord(
                                    timestampMinutes = elapsedMinutes,
                                    actionType = "KHÁM LÂM SÀNG",
                                    name = "Khám ${examItem.systemName} (${examItem.actionName})",
                                    description = examItem.findings,
                                    isGoodChoice = examItem.isHighYield,
                                    isHazardous = false,
                                    isRedundant = !examItem.isHighYield,
                                    feedback = if (examItem.isHighYield) "Thăm khám có giá trị định hướng cao!" else "Thăm khám ít thay đổi quyết định điều trị."
                                )
                            )
                        }
                    }
                )

                1 -> ClinicalReasoningTab(
                    clinicalCase = clinicalCase,
                    selectedPrimary = selectedPrimaryDiagnosis,
                    onSelectPrimary = { selectedPrimaryDiagnosis = it },
                    selectedDifferentials = selectedDifferentials,
                    onToggleDifferential = { diff ->
                        if (selectedDifferentials.contains(diff)) {
                            selectedDifferentials.remove(diff)
                        } else {
                            selectedDifferentials.add(diff)
                        }
                    },
                    selectedPathophysiology = selectedPathophysiology,
                    onSelectPathophysiology = { selectedPathophysiology = it }
                )

                2 -> ParaclinicalTestsTab(
                    clinicalCase = clinicalCase,
                    remainingBudgetVnd = remainingBudgetVnd,
                    orderedTests = orderedTests,
                    onOrderTest = { test ->
                        if (!orderedTests.any { it.id == test.id }) {
                            orderedTests.add(test)
                            remainingBudgetVnd = maxOf(0, remainingBudgetVnd - test.costVnd)
                            elapsedMinutes += test.turnaroundMinutes
                            val isGood = test.diagnosticYield == DiagnosticYield.GOLD_STANDARD || test.diagnosticYield == DiagnosticYield.HIGH
                            val isRedundant = test.diagnosticYield == DiagnosticYield.UNNECESSARY || test.diagnosticYield == DiagnosticYield.LOW
                            performedActions.add(
                                CaseActionRecord(
                                    timestampMinutes = elapsedMinutes,
                                    actionType = "CẬN LÂM SÀNG",
                                    name = "Chỉ định: ${test.name}",
                                    description = test.resultText,
                                    isGoodChoice = isGood,
                                    isHazardous = false,
                                    isRedundant = isRedundant,
                                    feedback = test.clinicalSignificance
                                )
                            )
                        }
                    }
                )

                3 -> ManagementAndInterventionTab(
                    clinicalCase = clinicalCase,
                    appliedInterventions = appliedInterventions,
                    onApplyIntervention = { intervention ->
                        if (!appliedInterventions.any { it.id == intervention.id }) {
                            appliedInterventions.add(intervention)
                            elapsedMinutes += intervention.timeCostMinutes

                            // Dynamic Vitals Response
                            currentVitals = currentVitals.copy(
                                heartRate = maxOf(30, currentVitals.heartRate + intervention.deltaHr),
                                systolicBp = maxOf(40, currentVitals.systolicBp + intervention.deltaSystolicBp),
                                diastolicBp = maxOf(25, currentVitals.diastolicBp + intervention.deltaDiastolicBp),
                                spO2 = minOf(100, maxOf(60, currentVitals.spO2 + intervention.deltaSpO2)),
                                painScore = minOf(10, maxOf(0, currentVitals.painScore + intervention.deltaPain))
                            )

                            if (intervention.isHazardous) {
                                hazardAlertMessage = intervention.hazardReason
                            }

                            performedActions.add(
                                CaseActionRecord(
                                    timestampMinutes = elapsedMinutes,
                                    actionType = "XỬ TRÍ CẤP CỨU",
                                    name = intervention.name,
                                    description = intervention.benefitDescription.ifEmpty { intervention.hazardReason ?: "" },
                                    isGoodChoice = intervention.isAppropriate,
                                    isHazardous = intervention.isHazardous,
                                    isRedundant = !intervention.isAppropriate && !intervention.isHazardous,
                                    feedback = if (intervention.isHazardous) "CHỐNG CHỈ ĐỊNH: ${intervention.hazardReason}" else intervention.benefitDescription
                                )
                            )
                        }
                    }
                )
            }
        }
    }
}

// =================================================================
// TAB 1: HỎI BỆNH & KHÁM LÂM SÀNG
// =================================================================
@Composable
private fun HistoryAndPhysicalTab(
    clinicalCase: ClinicalCase,
    chatMessages: List<Pair<String, String>>,
    isChatLoading: Boolean,
    inputMessage: String,
    onInputChange: (String) -> Unit,
    onSendMessage: (String) -> Unit,
    examinedSystems: List<String>,
    onExamineSystem: (PhysicalExamItem) -> Unit
) {
    val chatListState = rememberLazyListState()

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            chatListState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    LazyColumn(
        state = chatListState,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Section 1: AI Patient Dialog
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MedCardStroke, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = MedSurfaceDark)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(MedTealDark, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.SmartToy, contentDescription = null, tint = MedCyanGlow, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "HỎI BỆNH VỚI BỆNH NHÂN (AI PATIENT)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MedCyanGlow
                            )
                        }
                        Text(text = "Gemini Powered", fontSize = 10.sp, color = MedTextMuted)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick inquiry chips
                    Text(text = "Gợi ý câu hỏi khai thác bệnh sử nhanh:", fontSize = 11.sp, color = MedTextMuted)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val quickQuestions = listOf(
                            "Bác đau ở đâu và đau có lan đi đâu không?",
                            "Bác bị từ lúc mấy giờ và đau bao lâu rồi?",
                            "Cơn đau có kèm khó thở hay vã mồ hôi không?",
                            "Bác có tiền sử bệnh tim mạch hay huyết áp gì?",
                            "Bác đang uống những loại thuốc gì hàng ngày?",
                            "Bác có tiền sử dị ứng thuốc gì không?",
                            "Bác có hút thuốc lá, uống rượu bia không?"
                        )
                        items(quickQuestions) { qq ->
                            SuggestionChip(
                                onClick = { onSendMessage(qq) },
                                label = { Text(text = qq, fontSize = 11.sp) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = Color(0xFF1E3258),
                                    labelColor = MedTextPrimary
                                ),
                                border = SuggestionChipDefaults.suggestionChipBorder(
                                    enabled = true,
                                    borderColor = MedCardStroke
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Conversation message thread
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp)
                            .background(Color(0xFF0A1222), RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for ((role, text) in chatMessages.takeLast(6)) {
                            val isStudent = role == "student"
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (isStudent) Arrangement.End else Arrangement.Start
                            ) {
                                Box(
                                    modifier = Modifier
                                        .widthIn(max = 280.dp)
                                        .background(
                                            if (isStudent) MedTealDark else Color(0xFF1F2F4D),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "${if (isStudent) "BS: " else "Bệnh nhân: "}$text",
                                        fontSize = 12.sp,
                                        color = if (isStudent) Color.White else Color(0xFFFFD166),
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                        if (isChatLoading) {
                            Text(
                                text = "Bệnh nhân đang trả lời...",
                                fontSize = 11.sp,
                                color = MedTextMuted,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Chat Input Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputMessage,
                            onValueChange = onInputChange,
                            placeholder = { Text("Gõ câu hỏi cho bệnh nhân...", fontSize = 12.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("patient_chat_input"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MedTealPrimary,
                                unfocusedBorderColor = MedCardStroke,
                                focusedTextColor = MedTextPrimary,
                                unfocusedTextColor = MedTextPrimary
                            ),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { onSendMessage(inputMessage) },
                            modifier = Modifier
                                .size(44.dp)
                                .background(MedTealPrimary, RoundedCornerShape(10.dp))
                                .testTag("send_question_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Gửi", tint = Color.Black)
                        }
                    }
                }
            }
        }

        // Section 2: Physical Exam by Organ Systems
        item {
            Text(
                text = "THĂM KHÁM CÁC HỆ CƠ QUAN",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MedTextMuted
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        items(clinicalCase.physicalExams) { examItem ->
            val isExamined = examinedSystems.contains(examItem.id)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (isExamined) MedTealPrimary.copy(alpha = 0.6f) else MedCardStroke,
                        RoundedCornerShape(12.dp)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isExamined) Color(0xFF0F2038) else MedSurfaceDark
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = examItem.systemName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MedTextPrimary
                            )
                            Text(
                                text = examItem.actionName,
                                fontSize = 12.sp,
                                color = MedTextSecondary
                            )
                        }

                        if (!isExamined) {
                            Button(
                                onClick = { onExamineSystem(examItem) },
                                colors = ButtonDefaults.buttonColors(containerColor = MedTealDark),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("examine_${examItem.id}")
                            ) {
                                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Khám (+${examItem.timeCostMinutes}p)", fontSize = 11.sp)
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MedEcgGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Đã khám", fontSize = 11.sp, color = MedEcgGreen, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (isExamined) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = MedCardStroke, thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "KẾT QUẢ KHÁM LÂM SÀNG:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedCyanGlow
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = examItem.findings,
                            fontSize = 13.sp,
                            color = MedTextPrimary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// =================================================================
// TAB 2: LẬP LUẬN LÂM SÀNG & CHẨN ĐOÁN
// =================================================================
@Composable
private fun ClinicalReasoningTab(
    clinicalCase: ClinicalCase,
    selectedPrimary: String?,
    onSelectPrimary: (String) -> Unit,
    selectedDifferentials: List<String>,
    onToggleDifferential: (String) -> Unit,
    selectedPathophysiology: String?,
    onSelectPathophysiology: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "1. CHẨN ĐOÁN XÁC ĐỊNH / SƠ BỘ (PRIMARY DIAGNOSIS)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MedCyanGlow
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Chọn chẩn đoán phù hợp nhất dựa trên bệnh sử, dấu hiệu sinh tồn và lâm sàng:",
                fontSize = 12.sp,
                color = MedTextSecondary
            )
        }

        items(clinicalCase.differentialDiagnoses) { diag ->
            val isSelected = selectedPrimary == diag
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.5.dp,
                        if (isSelected) MedCyanGlow else MedCardStroke,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onSelectPrimary(diag) }
                    .testTag("primary_diag_$diag"),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFF0D253F) else MedSurfaceDark
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onSelectPrimary(diag) },
                        colors = RadioButtonDefaults.colors(selectedColor = MedCyanGlow)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = diag,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MedTextPrimary else MedTextSecondary
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "2. CHẨN ĐOÁN PHÂN BIỆT CẦN LOẠI TRỪ (DIFFERENTIAL DIAGNOSES)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MedWarningAmber
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Chọn 2-3 chẩn đoán có nguy cơ đe dọa tính mạng cần phải loại trừ ngay:",
                fontSize = 12.sp,
                color = MedTextSecondary
            )
        }

        items(clinicalCase.differentialDiagnoses) { diff ->
            val isChecked = selectedDifferentials.contains(diff)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (isChecked) MedWarningAmber else MedCardStroke,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onToggleDifferential(diff) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isChecked) Color(0xFF281C0F) else MedSurfaceDark
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { onToggleDifferential(diff) },
                        colors = CheckboxDefaults.colors(checkedColor = MedWarningAmber)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = diff,
                        fontSize = 13.sp,
                        color = if (isChecked) MedTextPrimary else MedTextSecondary
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "3. GIẢI THÍCH CƠ CHẾ SINH LÝ BỆNH (PATHOPHYSIOLOGY)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MedTealPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Chọn cơ chế giải phẫu - sinh lý - bệnh học then chốt dẫn đến biểu hiện của bệnh nhân:",
                fontSize = 12.sp,
                color = MedTextSecondary
            )
        }

        items(clinicalCase.pathophysiologyOptions) { mech ->
            val isSelected = selectedPathophysiology == mech
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (isSelected) MedTealPrimary else MedCardStroke,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onSelectPathophysiology(mech) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFF0F2636) else MedSurfaceDark
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onSelectPathophysiology(mech) },
                        colors = RadioButtonDefaults.colors(selectedColor = MedTealPrimary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = mech,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = if (isSelected) MedTextPrimary else MedTextSecondary
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

// =================================================================
// TAB 3: CHỈ ĐỊNH CẬN LÂM SÀNG (PARACLINICAL TESTS)
// =================================================================
@Composable
private fun ParaclinicalTestsTab(
    clinicalCase: ClinicalCase,
    remainingBudgetVnd: Int,
    orderedTests: List<DiagnosticTest>,
    onOrderTest: (DiagnosticTest) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("Tất cả") }
    val categories = remember { listOf("Tất cả", "Điện tim", "Hình ảnh", "Sinh hóa", "Huyết học") }

    val filteredTests = remember(selectedCategory) {
        if (selectedCategory == "Tất cả") clinicalCase.diagnosticTests
        else clinicalCase.diagnosticTests.filter { it.category == selectedCategory }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Diagnostic Stewardship Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MedCardStroke, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1930))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CHIẾN LƯỢC CẬN LÂM SÀNG CÓ ĐỊNH HƯỚNG",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedCyanGlow
                        )
                        Text(
                            text = "Còn lại: ${remainingBudgetVnd / 1000}k đ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (remainingBudgetVnd > 1_000_000) MedEcgGreen else MedCriticalRed
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Không nên 'bấm chọn tất cả' xét nghiệm. Hãy chỉ định cận lâm sàng dựa trên chẩn đoán sơ bộ để tiết kiệm ngân sách và thời gian cấp cứu.",
                        fontSize = 11.sp,
                        color = MedTextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Category Filter Row
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    val isSel = selectedCategory == cat
                    FilterChip(
                        selected = isSel,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MedSurfaceDark,
                            selectedContainerColor = MedTealDark,
                            labelColor = MedTextSecondary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Tests List
        items(filteredTests) { test ->
            val isOrdered = orderedTests.any { it.id == test.id }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (isOrdered) MedTealPrimary.copy(alpha = 0.7f) else MedCardStroke,
                        RoundedCornerShape(12.dp)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isOrdered) Color(0xFF0E223B) else MedSurfaceDark
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = test.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MedTextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Chi phí: ${test.costVnd / 1000}k đ",
                                    fontSize = 11.sp,
                                    color = MedCyanGlow
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Thời gian: ${test.turnaroundMinutes} phút",
                                    fontSize = 11.sp,
                                    color = MedWarningAmber
                                )
                            }
                        }

                        if (!isOrdered) {
                            Button(
                                onClick = { onOrderTest(test) },
                                colors = ButtonDefaults.buttonColors(containerColor = MedTealPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("order_${test.id}")
                            ) {
                                Text("Chỉ định", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .background(MedEcgGreen.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Có kết quả", color = MedEcgGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Result Display if Ordered
                    if (isOrdered) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = MedCardStroke, thickness = 0.8.dp)
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "KẾT QUẢ XÉT NGHIỆM / CẬN LÂM SÀNG:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedCyanGlow
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = test.resultText,
                            fontSize = 13.sp,
                            color = Color(0xFFFFD166),
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Ý nghĩa lâm sàng: ${test.clinicalSignificance}",
                            fontSize = 11.sp,
                            color = MedTextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// =================================================================
// TAB 4: XỬ TRÍ & CẤP CỨU (MANAGEMENT)
// =================================================================
@Composable
private fun ManagementAndInterventionTab(
    clinicalCase: ClinicalCase,
    appliedInterventions: List<MedicalIntervention>,
    onApplyIntervention: (MedicalIntervention) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MedCardStroke, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131F37))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "XỬ TRÍ CẤP CỨU & CAN THIỆP ĐIỀU TRỊ BAN ĐẦU",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedTealPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Lựa chọn các phác đồ điều trị, đường truyền, thuốc hoặc kích hoạt can thiệp chuyên khoa. Hãy lưu ý các chống chỉ định có thể gây tử vong.",
                        fontSize = 11.sp,
                        color = MedTextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        items(clinicalCase.interventions) { intervention ->
            val isApplied = appliedInterventions.any { it.id == intervention.id }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (isApplied) {
                            if (intervention.isHazardous) MedCriticalRed else MedEcgGreen
                        } else MedCardStroke,
                        RoundedCornerShape(12.dp)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isApplied) {
                        if (intervention.isHazardous) Color(0xFF331114) else Color(0xFF0F2628)
                    } else MedSurfaceDark
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = intervention.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MedTextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Phân loại: ${intervention.category} • Thời gian: ${intervention.timeCostMinutes} phút",
                                fontSize = 11.sp,
                                color = MedTextMuted
                            )
                        }

                        if (!isApplied) {
                            Button(
                                onClick = { onApplyIntervention(intervention) },
                                colors = ButtonDefaults.buttonColors(containerColor = MedTealDark),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("apply_${intervention.id}")
                            ) {
                                Text("Thực hiện", fontSize = 11.sp)
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (intervention.isHazardous) MedCriticalRed.copy(alpha = 0.2f) else MedEcgGreen.copy(alpha = 0.2f),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (intervention.isHazardous) "ĐÃ GÂY TÁC HẠI!" else "ĐÃ THỰC HIỆN",
                                    color = if (intervention.isHazardous) MedCriticalRed else MedEcgGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    if (isApplied) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = MedCardStroke, thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(6.dp))

                        if (intervention.isHazardous) {
                            Text(
                                text = "⚠️ NGUY HIỂM: ${intervention.hazardReason}",
                                fontSize = 12.sp,
                                color = MedCriticalRed,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 16.sp
                            )
                        } else {
                            Text(
                                text = "✓ HIỆU QUẢ LÂM SÀNG: ${intervention.benefitDescription}",
                                fontSize = 12.sp,
                                color = MedEcgGreen,
                                fontWeight = FontWeight.Normal,
                                lineHeight = 16.sp
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

// =================================================================
// SCORING ENGINE: TÍNH ĐIỂM 7 TRỤ CỘT NĂNG LỰC LÂM SÀNG
// =================================================================
private fun calculateDebrief(
    clinicalCase: ClinicalCase,
    currentVitals: VitalSigns,
    elapsedMinutes: Int,
    remainingBudgetVnd: Int,
    performedActions: List<CaseActionRecord>,
    examinedSystemsCount: Int,
    orderedTests: List<DiagnosticTest>,
    appliedInterventions: List<MedicalIntervention>,
    primaryDiagnosis: String?,
    differentials: List<String>,
    pathophysiology: String?
): DebriefResult {
    // 1. History Taking Score (0-100)
    val questionsAskedCount = performedActions.count { it.actionType == "HỎI BỆNH" }
    val historyScore = minOf(100, questionsAskedCount * 25)

    // 2. Physical Exam Score (0-100)
    val physicalExamScore = minOf(100, examinedSystemsCount * 25)

    // 3. Clinical Reasoning Score (0-100)
    val hasCorrectPrimary = primaryDiagnosis == clinicalCase.correctDiagnosis
    val diffCorrectCount = differentials.count { clinicalCase.differentialDiagnoses.contains(it) }
    val pathCorrect = pathophysiology == clinicalCase.correctPathophysiology
    val clinicalReasoningScore = (if (hasCorrectPrimary) 40 else 10) +
            minOf(30, diffCorrectCount * 15) +
            (if (pathCorrect) 30 else 0)

    // 4. Diagnostic Stewardship / Cost-effectiveness Score (0-100)
    val totalTestsCount = orderedTests.size
    val highYieldTestsCount = orderedTests.count { it.diagnosticYield == DiagnosticYield.GOLD_STANDARD || it.diagnosticYield == DiagnosticYield.HIGH }
    val unnecessaryTestsCount = orderedTests.count { it.diagnosticYield == DiagnosticYield.UNNECESSARY }
    val budgetScore = if (totalTestsCount == 0) 50 else {
        val yieldRatio = highYieldTestsCount.toFloat() / totalTestsCount
        ((yieldRatio * 80) - (unnecessaryTestsCount * 20) + (if (remainingBudgetVnd > 1_500_000) 20 else 5)).toInt()
    }
    val diagnosticStewardshipScore = minOf(100, maxOf(10, budgetScore))

    // 5. Diagnostic Accuracy Score (0-100)
    val diagnosticAccuracyScore = if (hasCorrectPrimary) 100 else if (differentials.contains(clinicalCase.correctDiagnosis)) 60 else 20

    // 6. Therapeutic Management Score (0-100)
    val appropriateInterventions = appliedInterventions.count { it.isAppropriate }
    val totalRequiredAppropriate = clinicalCase.interventions.count { it.isAppropriate }
    val therapeuticScore = if (totalRequiredAppropriate == 0) 70 else {
        minOf(100, ((appropriateInterventions.toFloat() / totalRequiredAppropriate) * 100).toInt())
    }

    // 7. Patient Safety Score (0-100)
    val hazardousActions = appliedInterventions.count { it.isHazardous }
    val patientSafetyScore = maxOf(0, 100 - (hazardousActions * 50))

    // Overall Score (Weighted)
    val overallScore = (
            (historyScore * 0.10) +
                    (physicalExamScore * 0.15) +
                    (clinicalReasoningScore * 0.15) +
                    (diagnosticStewardshipScore * 0.15) +
                    (diagnosticAccuracyScore * 0.20) +
                    (therapeuticScore * 0.15) +
                    (patientSafetyScore * 0.10)
            ).toInt()

    val xpGained = maxOf(50, overallScore * 4)

    return DebriefResult(
        caseId = clinicalCase.id,
        caseTitle = clinicalCase.title,
        overallScore = overallScore,
        historyScore = historyScore,
        physicalExamScore = physicalExamScore,
        clinicalReasoningScore = clinicalReasoningScore,
        diagnosticStewardshipScore = diagnosticStewardshipScore,
        diagnosticAccuracyScore = diagnosticAccuracyScore,
        therapeuticScore = therapeuticScore,
        patientSafetyScore = patientSafetyScore,
        timeUsedMinutes = elapsedMinutes,
        budgetUsedVnd = clinicalCase.budgetVnd - remainingBudgetVnd,
        actionTimeline = performedActions,
        expertCommentary = "",
        pathophysiologyNotes = clinicalCase.correctPathophysiology,
        guidelineRecommendations = clinicalCase.guidelineCitation,
        xpGained = xpGained
    )
}
