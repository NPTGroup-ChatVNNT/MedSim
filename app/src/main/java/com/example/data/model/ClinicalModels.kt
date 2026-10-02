package com.example.data.model

data class VitalSigns(
    val heartRate: Int,
    val systolicBp: Int,
    val diastolicBp: Int,
    val respiratoryRate: Int,
    val spO2: Int,
    val temperature: Float,
    val gcs: Int = 15,
    val painScore: Int = 8 // 0-10
) {
    val bpString: String get() = "$systolicBp/$diastolicBp mmHg"
    val isCritical: Boolean get() = heartRate > 120 || heartRate < 45 || systolicBp < 90 || systolicBp > 190 || spO2 < 90 || respiratoryRate > 28
}

enum class TriageLevel(val label: String, val colorHex: Long) {
    RED("Cấp cứu khẩn (Cấp 1)", 0xFFEF4444),
    YELLOW("Ưu tiên cao (Cấp 2)", 0xFFF59E0B),
    GREEN("Ít khẩn cấp (Cấp 3)", 0xFF10B981)
}

enum class DiagnosticYield(val label: String, val scoreWeight: Int) {
    GOLD_STANDARD("Tiêu chuẩn vàng / Bắt buộc", 25),
    HIGH("Giá trị chẩn đoán cao", 20),
    MODERATE("Hỗ trợ chẩn đoán", 10),
    LOW("Ít giá trị / Thừa thãi", -5),
    UNNECESSARY("Lãng phí ngân sách", -15)
}

data class PhysicalExamItem(
    val id: String,
    val systemName: String, // Tim mạch, Hô hấp, v.v.
    val actionName: String,
    val timeCostMinutes: Int,
    val findings: String,
    val isHighYield: Boolean
)

data class DiagnosticTest(
    val id: String,
    val name: String,
    val category: String, // ECG, Hình ảnh, Huyết học, Sinh hóa, Chuyên khoa
    val costVnd: Int, // e.g. 150_000 VND
    val turnaroundMinutes: Int,
    val diagnosticYield: DiagnosticYield,
    val normalRange: String = "",
    val resultText: String,
    val clinicalSignificance: String
)

data class MedicalIntervention(
    val id: String,
    val name: String,
    val category: String, // Hô hấp, Dịch truyền, Thuốc, Can thiệp chuyên khoa
    val timeCostMinutes: Int,
    val isAppropriate: Boolean,
    val isHazardous: Boolean,
    val hazardReason: String? = null,
    val benefitDescription: String,
    val deltaHr: Int = 0,
    val deltaSystolicBp: Int = 0,
    val deltaDiastolicBp: Int = 0,
    val deltaSpO2: Int = 0,
    val deltaPain: Int = 0
)

data class ClinicalCase(
    val id: String,
    val title: String,
    val specialty: String, // Tim mạch, Hô hấp, Cấp cứu
    val difficulty: String, // Cơ bản, Nâng cao, Chuyên gia
    val patientName: String,
    val patientAge: Int,
    val patientGender: String,
    val chiefComplaint: String,
    val triageLevel: TriageLevel,
    val admissionStory: String,
    val pastHistory: String,
    val medications: String,
    val allergies: String,
    val socialHistory: String,
    val initialVitals: VitalSigns,
    val budgetVnd: Int,
    val timeLimitMinutes: Int,
    val physicalExams: List<PhysicalExamItem>,
    val diagnosticTests: List<DiagnosticTest>,
    val interventions: List<MedicalIntervention>,
    val correctDiagnosis: String,
    val differentialDiagnoses: List<String>,
    val correctPathophysiology: String,
    val pathophysiologyOptions: List<String>,
    val guidelineCitation: String,
    val takeHomePearl: String,
    val aiPersonaPrompt: String
)

data class CaseActionRecord(
    val timestampMinutes: Int,
    val actionType: String, // EXAM, TEST, INTERVENTION, DIAGNOSIS
    val name: String,
    val description: String,
    val isGoodChoice: Boolean,
    val isHazardous: Boolean,
    val isRedundant: Boolean,
    val feedback: String
)

data class DebriefResult(
    val caseId: String,
    val caseTitle: String,
    val overallScore: Int, // 0-100
    val historyScore: Int,
    val physicalExamScore: Int,
    val clinicalReasoningScore: Int,
    val diagnosticStewardshipScore: Int,
    val diagnosticAccuracyScore: Int,
    val therapeuticScore: Int,
    val patientSafetyScore: Int,
    val timeUsedMinutes: Int,
    val budgetUsedVnd: Int,
    val actionTimeline: List<CaseActionRecord>,
    val expertCommentary: String,
    val pathophysiologyNotes: String,
    val guidelineRecommendations: String,
    val xpGained: Int
)
