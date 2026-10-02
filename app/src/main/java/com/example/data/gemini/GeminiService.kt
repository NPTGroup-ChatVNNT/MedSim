package com.example.data.gemini

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.ClinicalCase
import com.example.data.model.DebriefResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun chatWithPatient(
        clinicalCase: ClinicalCase,
        studentQuestion: String,
        conversationHistory: List<Pair<String, String>>
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val isApiKeyValid = apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY"

        if (!isApiKeyValid) {
            return@withContext generateLocalPatientResponse(clinicalCase, studentQuestion)
        }

        try {
            val systemPrompt = """
                Bạn là bệnh nhân trong mô phỏng ca bệnh y khoa lâm sàng cho sinh viên y khoa.
                Thông tin bệnh nhân:
                - Tên: ${clinicalCase.patientName}, ${clinicalCase.patientAge} tuổi, giới tính ${clinicalCase.patientGender}.
                - Lý do vào viện: ${clinicalCase.chiefComplaint}
                - Bệnh sử & Khởi phát: ${clinicalCase.admissionStory}
                - Tiền sử: ${clinicalCase.pastHistory}
                - Thuốc đang dùng: ${clinicalCase.medications}
                - Dị ứng: ${clinicalCase.allergies}
                - Thói quen/Xã hội: ${clinicalCase.socialHistory}
                - Tính cách / Persona: ${clinicalCase.aiPersonaPrompt}
                
                QUY TẮC NHẬP VAI:
                1. Hãy trả lời hoàn toàn bằng tiếng Việt với tư cách là bệnh nhân ${clinicalCase.patientName}.
                2. Bạn KHÔNG BIẾT thuật ngữ y khoa chuyên sâu. Bạn chỉ diễn tả cảm giác đau đớn, khó chịu bằng ngôn từ đời thường của người bệnh.
                3. Chỉ trả lời những gì có trong bệnh án của bạn. Nếu sinh viên hỏi điều gì không có thật, hãy nói bạn không nhớ hoặc không thấy vậy.
                4. Giữ câu trả lời ngắn gọn (1-3 câu), tự nhiên, biểu lộ cảm xúc phù hợp với cơn đau hoặc sự sợ hãi của bệnh nhân.
            """.trimIndent()

            val contentsArray = JSONArray()

            // System instruction
            val systemPart = JSONObject().put("text", systemPrompt)
            val systemContent = JSONObject().put("parts", JSONArray().put(systemPart))

            // History
            for ((role, text) in conversationHistory.takeLast(6)) {
                val part = JSONObject().put("text", text)
                val turn = JSONObject()
                    .put("role", if (role == "student") "user" else "model")
                    .put("parts", JSONArray().put(part))
                contentsArray.put(turn)
            }

            // Current question
            val currentTurn = JSONObject()
                .put("role", "user")
                .put("parts", JSONArray().put(JSONObject().put("text", studentQuestion)))
            contentsArray.put(currentTurn)

            val requestBodyJson = JSONObject()
                .put("contents", contentsArray)
                .put("systemInstruction", systemContent)
                .put(
                    "generationConfig", JSONObject()
                        .put("temperature", 0.7)
                        .put("topP", 0.95)
                        .put("maxOutputTokens", 250)
                )

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .post(requestBodyJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                val json = JSONObject(responseBody)
                val candidates = json.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    val text = parts?.getJSONObject(0)?.optString("text")
                    if (!text.isNullOrBlank()) {
                        return@withContext text.trim()
                    }
                }
            }
            Log.w("GeminiService", "API call fallback: HTTP ${response.code} $responseBody")
            generateLocalPatientResponse(clinicalCase, studentQuestion)
        } catch (e: Exception) {
            Log.e("GeminiService", "Error chatting with patient", e)
            generateLocalPatientResponse(clinicalCase, studentQuestion)
        }
    }

    suspend fun generateClinicalDebrief(
        clinicalCase: ClinicalCase,
        debrief: DebriefResult
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val isApiKeyValid = apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY"

        if (!isApiKeyValid) {
            return@withContext generateLocalDebriefAdvice(clinicalCase, debrief)
        }

        try {
            val prompt = """
                Bạn là Bác sĩ Trưởng khoa Lâm sàng / Giảng viên Y khoa hướng dẫn sinh viên sau khi hoàn thành ca bệnh tương tác:
                Ca bệnh: ${clinicalCase.title}
                Bệnh nhân: ${clinicalCase.patientName}, ${clinicalCase.patientAge} tuổi.
                Chẩn đoán xác định: ${clinicalCase.correctDiagnosis}
                Cơ chế sinh lý bệnh: ${clinicalCase.correctPathophysiology}
                Điểm số sinh viên: ${debrief.overallScore}/100 điểm
                - Khai thác bệnh sử: ${debrief.historyScore}/100
                - Khám lâm sàng: ${debrief.physicalExamScore}/100
                - Lập luận sinh lý bệnh: ${debrief.clinicalReasoningScore}/100
                - Sử dụng cận lâm sàng hợp lý: ${debrief.diagnosticStewardshipScore}/100
                - Độ chính xác chẩn đoán: ${debrief.diagnosticAccuracyScore}/100
                - Xử trí & Cấp cứu: ${debrief.therapeuticScore}/100
                - An toàn người bệnh: ${debrief.patientSafetyScore}/100

                Hãy viết một bài nhận xét debrief sư phạm y khoa súc tích (khoảng 3 đoạn ngắn bằng tiếng Việt):
                1. Khen ngợi điểm làm tốt và phê bình điểm sai sót (đặc biệt các chỉ định thừa hoặc chống chỉ định nguy hiểm nếu có).
                2. Phân tích sâu cơ chế sinh lý bệnh giải thích vì sao quyết định đó lại quyết định tính mạng bệnh nhân.
                3. Đưa ra 1-2 'Clinical Pearl' then chốt và trích dẫn hướng dẫn y khoa (ESC/AHA/Bộ Y Tế) để sinh viên nhớ suốt đời khi đi lâm sàng.
            """.trimIndent()

            val contentsArray = JSONArray()
            val part = JSONObject().put("text", prompt)
            contentsArray.put(JSONObject().put("parts", JSONArray().put(part)))

            val requestBodyJson = JSONObject()
                .put("contents", contentsArray)
                .put("generationConfig", JSONObject().put("temperature", 0.5))

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(requestBodyJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                val json = JSONObject(responseBody)
                val candidates = json.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val text = candidate.optJSONObject("content")?.optJSONArray("parts")?.getJSONObject(0)?.optString("text")
                    if (!text.isNullOrBlank()) {
                        return@withContext text.trim()
                    }
                }
            }
            generateLocalDebriefAdvice(clinicalCase, debrief)
        } catch (e: Exception) {
            Log.e("GeminiService", "Debrief call error", e)
            generateLocalDebriefAdvice(clinicalCase, debrief)
        }
    }

    private fun generateLocalPatientResponse(clinicalCase: ClinicalCase, question: String): String {
        val q = question.lowercase()
        return when {
            q.contains("đau ở đâu") || q.contains("vị trí") || q.contains("chỗ nào") -> {
                "Tôi bị đau dữ dội ở giữa ngực sau xương ức bác sĩ ơi, cảm giác như có tảng đá nặng đè nghiến lên ngực vậy!"
            }
            q.contains("lan") || q.contains("hướng lan") || q.contains("vai") || q.contains("tay") || q.contains("hàm") -> {
                "Cơn đau thắt này nó buốt nhói lan lên tận cằm, góc hàm bên trái và tê dọc mặt trong cánh tay trái của tôi..."
            }
            q.contains("khi nào") || q.contains("từ lúc") || q.contains("bao lâu") || q.contains("thời gian") -> {
                "Tôi bị đau từ khoảng 6 giờ sáng nay, đến giờ là hơn 2 tiếng rồi. Tôi ngồi nghỉ cả buổi mà không thấy đỡ chút nào cả!"
            }
            q.contains("khó thở") || q.contains("thở") -> {
                "Tôi thấy tức ngực nghẹt thở, phải ráng hít sâu từng hơi. Người tôi đang toát hết cả mồ hôi hột lạnh toát đây này..."
            }
            q.contains("tiền sử") || q.contains("bệnh gì") || q.contains("huyết áp") || q.contains("tim") -> {
                "Tôi có bệnh tăng huyết áp cả chục năm nay, bác sĩ kê cho viên thuốc gì màu trắng uống mỗi sáng, nhưng tôi hay quên, khi nào thấy nhức đầu mới uống thôi..."
            }
            q.contains("thuốc") || q.contains("đang uống") -> {
                clinicalCase.medications.ifEmpty { "Tôi chỉ uống thuốc huyết áp Amlodipine khi nhớ thôi, ngoài ra không dùng gì khác." }
            }
            q.contains("dị ứng") -> {
                clinicalCase.allergies.ifEmpty { "Từ xưa tới nay tôi chưa từng bị dị ứng đồ ăn hay thuốc gì cả bác sĩ." }
            }
            q.contains("hút thuốc") || q.contains("rượu") || q.contains("bia") -> {
                clinicalCase.socialHistory.ifEmpty { "Tôi có hút thuốc lá từ hồi trẻ, ngày chừng 1 bao, rượu bia thì thỉnh thoảng dịp lễ tết." }
            }
            q.contains("nôn") || q.contains("buồn nôn") || q.contains("chóng mặt") -> {
                "Từ lúc đau ngực tôi thấy lợm giọng buồn nôn ghê gớm, người lảo đảo choáng váng muốn xỉu khi đứng dậy..."
            }
            else -> {
                "Dạ bác sĩ, tôi đau và mệt lắm, xin bác sĩ xem giùm và cứu tôi với, ngực tôi tức nghẹn quá..."
            }
        }
    }

    private fun generateLocalDebriefAdvice(clinicalCase: ClinicalCase, debrief: DebriefResult): String {
        return buildString {
            append("ĐÁNH GIÁ CHUYÊN MÔN TỪ GIẢNG VIÊN LÂM SÀNG:\n\n")
            if (debrief.overallScore >= 80) {
                append("🌟 Xuất sắc! Bạn đã thể hiện tư duy lâm sàng rất nhạy bén, nhận diện kịp thời bức tranh cấp cứu của ca bệnh ${clinicalCase.title}.")
            } else if (debrief.overallScore >= 60) {
                append("👍 Đạt yêu cầu cơ bản. Bạn đã đưa ra chẩn đoán chính xác nhưng một số bước thăm khám và can thiệp điều trị còn có thể tối ưu hơn.")
            } else {
                append("⚠️ Cần củng cố tư duy lâm sàng! Trong cấp cứu ca bệnh này, việc ra quyết định sai hoặc trì hoãn có thể đe dọa tính mạng người bệnh.")
            }
            append("\n\nCƠ CHẾ SINH LÝ BỆNH THEN CHỐT:\n")
            append(clinicalCase.correctPathophysiology)
            append("\n\nCLINICAL PEARL & GUIDELINE:\n")
            append(clinicalCase.guidelineCitation)
        }
    }
}
