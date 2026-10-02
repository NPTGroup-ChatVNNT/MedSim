package com.example.data.cases

import com.example.data.model.*

object ClinicalCaseRepository {

    val cases: List<ClinicalCase> = listOf(
        // CASE 1: STEMI THÀNH DƯỚI & THẤT PHẢI
        ClinicalCase(
            id = "cardio_stemi_rv",
            title = "Đau ngực cấp & Nguy cơ tụt huyết áp",
            specialty = "Tim mạch & Cấp cứu",
            difficulty = "Trung cấp",
            patientName = "Nguyễn Văn Hùng",
            patientAge = 67,
            patientGender = "Nam",
            chiefComplaint = "Đau thắt ngực dữ dội sau xương ức 2 giờ, vã mồ hôi lạnh",
            triageLevel = TriageLevel.RED,
            admissionStory = "Bệnh nhân nam 67 tuổi, đột ngột đau ngực sau xương ức kiểu bóp nghẹt lúc 06:00 sáng khi đang tập thể dục nhẹ. Đau lan lên góc hàm và mặt trong cánh tay trái, kèm vã mồ hôi hột, buồn nôn, chóng mặt nhẹ khi đứng. Nghỉ ngơi 30 phút không giảm, được gia đình đưa vào Khoa Cấp cứu.",
            pastHistory = "Tăng huyết áp 10 năm (uống Amlodipine 5mg không đều), Rối loạn lipid máu, Hút thuốc lá 30 gói-năm.",
            medications = "Amlodipine 5mg/ngày, thỉnh thoảng quên uống.",
            allergies = "Chưa ghi nhận dị ứng thuốc.",
            socialHistory = "Hưu trí, hút thuốc lá 1 bao/ngày, ít vận động.",
            initialVitals = VitalSigns(
                heartRate = 54,
                systolicBp = 105,
                diastolicBp = 65,
                respiratoryRate = 22,
                spO2 = 96,
                temperature = 36.8f,
                painScore = 9
            ),
            budgetVnd = 5_000_000,
            timeLimitMinutes = 45,
            physicalExams = listOf(
                PhysicalExamItem(
                    id = "pe_cv",
                    systemName = "Tim mạch",
                    actionName = "Nghe tim & Đánh giá huyết động",
                    timeCostMinutes = 2,
                    findings = "Nhịp chậm đều 54 ck/phút, T1 T2 rõ, không nghe tiếng cọ màng tim, không có T3 gallop. Tĩnh mạch cổ nổi nhẹ ở tư thế 45 độ, dấu hiệu Kussmaul (+).",
                    isHighYield = true
                ),
                PhysicalExamItem(
                    id = "pe_resp",
                    systemName = "Hô hấp",
                    actionName = "Nghe phổi & Khám ngực",
                    timeCostMinutes = 2,
                    findings = "Phổi thông khí rõ 2 bên, không rales, rì rào phế nang êm dịu.",
                    isHighYield = true
                ),
                PhysicalExamItem(
                    id = "pe_abd",
                    systemName = "Bụng - Tiêu hóa",
                    actionName = "Khám bụng",
                    timeCostMinutes = 2,
                    findings = "Bụng mềm, không chướng, gan lách không sờ chạm, ấn không điểm đau khu trú.",
                    isHighYield = false
                ),
                PhysicalExamItem(
                    id = "pe_neuro",
                    systemName = "Thần kinh & Tri giác",
                    actionName = "Đánh giá tri giác & thần kinh khu trú",
                    timeCostMinutes = 1,
                    findings = "Bệnh nhân tỉnh, tiếp xúc tốt, Glasgow 15 điểm, không yếu liệt khu trú.",
                    isHighYield = false
                ),
                PhysicalExamItem(
                    id = "pe_gen",
                    systemName = "Toàn thân",
                    actionName = "Khám da niêm, chi & tưới máu",
                    timeCostMinutes = 1,
                    findings = "Da niêm tái nhẹ, đầu chi lạnh, vã mồ hôi trán, thời gian đổ đầy mao mạch (CRT) = 3 giây.",
                    isHighYield = true
                )
            ),
            diagnosticTests = listOf(
                DiagnosticTest(
                    id = "test_ecg_12",
                    name = "Điện tâm đồ 12 chuyển đạo (ECG)",
                    category = "Điện tim",
                    costVnd = 120_000,
                    turnaroundMinutes = 5,
                    diagnosticYield = DiagnosticYield.GOLD_STANDARD,
                    resultText = "Nhịp xoang 54 l/p. ST chênh lên dạng vòm 2.5mm ở DII, DIII, aVF (DIII > DII). ST chênh xuống soi gương ở DI, aVL. Cần làm thêm chuyển đạo bên phải V3R, V4R.",
                    clinicalSignificance = "Hình ảnh nhồi máu cơ tim cấp thành dưới (STEMI inferior). Nghi ngờ tắc Động mạch vành phải (RCA)."
                ),
                DiagnosticTest(
                    id = "test_ecg_right",
                    name = "ECG chuyển đạo thất phải (V3R, V4R)",
                    category = "Điện tim",
                    costVnd = 80_000,
                    turnaroundMinutes = 3,
                    diagnosticYield = DiagnosticYield.GOLD_STANDARD,
                    resultText = "ST chênh lên 1.5mm ở chuyển đạo V4R.",
                    clinicalSignificance = "Xác nhận nhồi máu cơ tim thất phải kèm theo! Bệnh nhân phụ thuộc rất lớn vào tiền tải (preload)."
                ),
                DiagnosticTest(
                    id = "test_trop",
                    name = "Định lượng Troponin I độ nhạy cao (hs-cTnI)",
                    category = "Sinh hóa",
                    costVnd = 250_000,
                    turnaroundMinutes = 30,
                    diagnosticYield = DiagnosticYield.HIGH,
                    normalRange = "< 14 ng/L",
                    resultText = "hs-cTnI: 480 ng/L (Tăng rất cao)",
                    clinicalSignificance = "Hoại tử tế bào cơ tim cấp tính."
                ),
                DiagnosticTest(
                    id = "test_cxr",
                    name = "X-quang ngực thẳng tại giường",
                    category = "Hình ảnh",
                    costVnd = 180_000,
                    turnaroundMinutes = 12,
                    diagnosticYield = DiagnosticYield.MODERATE,
                    resultText = "Bóng tim không to, hai phế trường sáng đều, không ứ huyết phổi, trung thất không giãn rộng.",
                    clinicalSignificance = "Loại trừ phù phổi cấp và loại trừ giãn trung thất (bóc tách ĐMC)."
                ),
                DiagnosticTest(
                    id = "test_echo",
                    name = "Siêu âm tim tại giường (Bedside Echo / FoCUS)",
                    category = "Hình ảnh",
                    costVnd = 450_000,
                    turnaroundMinutes = 10,
                    diagnosticYield = DiagnosticYield.HIGH,
                    resultText = "Giảm vận động thành dưới thất trái. Thất phải giãn nhẹ, giảm co bóp thành tự do thất phải (TAPSE = 13mm). Không có tràn dịch màng tim.",
                    clinicalSignificance = "Xác nhận tổn thương thất phải và thành dưới thất trái."
                ),
                DiagnosticTest(
                    id = "test_cbc",
                    name = "Tổng phân tích tế bào máu (CTM)",
                    category = "Huyết học",
                    costVnd = 110_000,
                    turnaroundMinutes = 20,
                    diagnosticYield = DiagnosticYield.MODERATE,
                    normalRange = "WBC 4-10, Hb 13-16",
                    resultText = "WBC: 11.2 x 10^9/L (Neutrophil 78%), Hb: 14.2 g/dL, Tiểu cầu: 245 x 10^9/L.",
                    clinicalSignificance = "Bạch cầu tăng nhẹ do phản ứng viêm cơ tim cấp."
                ),
                DiagnosticTest(
                    id = "test_ct_angio",
                    name = "Chụp CT mạch máu toàn thân cản quang",
                    category = "Hình ảnh",
                    costVnd = 2_800_000,
                    turnaroundMinutes = 45,
                    diagnosticYield = DiagnosticYield.UNNECESSARY,
                    resultText = "Không thấy bất thường động mạch chủ ngực hay bụng.",
                    clinicalSignificance = "Lãng phí thời gian 'vàng' tái tưới máu và lạm dụng thuốc cản quang khi đã có chẩn đoán STEMI rõ trên ECG."
                )
            ),
            interventions = listOf(
                MedicalIntervention(
                    id = "rx_pci_alert",
                    name = "Báo động Đội Can thiệp Mạch vành (Code STEMI / PCI khẩn)",
                    category = "Can thiệp chuyên khoa",
                    timeCostMinutes = 3,
                    isAppropriate = true,
                    isHazardous = false,
                    benefitDescription = "Kích hoạt phòng Cathlab can thiệp mạch vành thì đầu trong 'thời gian cửa-bóng' (Door-to-balloon) < 90 phút.",
                    deltaPain = -2
                ),
                MedicalIntervention(
                    id = "rx_aspirin",
                    name = "Aspirin 300mg uống (nhai nuốt)",
                    category = "Thuốc",
                    timeCostMinutes = 1,
                    isAppropriate = true,
                    isHazardous = false,
                    benefitDescription = "Ức chế ngưng tập tiểu cầu thông qua ức chế COX-1, giảm tử vong tức thì trong nhồi máu cơ tim cấp.",
                    deltaPain = -1
                ),
                MedicalIntervention(
                    id = "rx_p2y12",
                    name = "Ticagrelor 180mg uống (Kháng kết tập tiểu cầu kép DAPT)",
                    category = "Thuốc",
                    timeCostMinutes = 1,
                    isAppropriate = true,
                    isHazardous = false,
                    benefitDescription = "Ức chế thụ thể P2Y12 của tiểu cầu, phối hợp với Aspirin để ngăn tắc stent sau can thiệp.",
                    deltaPain = -1
                ),
                MedicalIntervention(
                    id = "rx_iv_fluid",
                    name = "Bù dịch tĩnh mạch Natri Clorid 0.9% 500ml",
                    category = "Dịch truyền",
                    timeCostMinutes = 5,
                    isAppropriate = true,
                    isHazardous = false,
                    benefitDescription = "Nâng tiền tải (preload) cho thất phải, giúp cải thiện cung lượng tim và huyết áp trong nhồi máu cơ tim thất phải.",
                    deltaHr = 4,
                    deltaSystolicBp = 15,
                    deltaDiastolicBp = 10,
                    deltaPain = -1
                ),
                MedicalIntervention(
                    id = "rx_nitroglycerin",
                    name = "Nitroglycerin 0.4mg ngậm dưới lưỡi",
                    category = "Thuốc",
                    timeCostMinutes = 1,
                    isAppropriate = false,
                    isHazardous = true,
                    hazardReason = "CHỐNG CHỈ ĐỊNH NGUY HIỂM! Nitroglycerin gây giãn tĩnh mạch làm giảm tiền tải. Ở bệnh nhân nhồi máu thất phải, cung lượng tim phụ thuộc hoàn toàn vào áp lực đổ đầy thất phải. Thuốc sẽ gây tụt huyết áp nghiêm trọng và trụy mạch tim mạch!",
                    benefitDescription = "",
                    deltaHr = 15,
                    deltaSystolicBp = -35,
                    deltaDiastolicBp = -25,
                    deltaPain = 2
                ),
                MedicalIntervention(
                    id = "rx_o2",
                    name = "Thở oxy qua gọng mũi 3 lít/phút",
                    category = "Hô hấp",
                    timeCostMinutes = 1,
                    isAppropriate = false,
                    isHazardous = false,
                    hazardReason = "SpO2 của bệnh nhân hiện 96% (> 90%). Thở oxy thường quy khi không hạ oxy máu có thể gây co mạch vành và tăng gốc oxy tự do.",
                    benefitDescription = "Chỉ chỉ định khi SpO2 < 90% theo khuyến cáo ESC/AHA 2023.",
                    deltaSpO2 = 2
                ),
                MedicalIntervention(
                    id = "rx_heparin",
                    name = "Heparin không phân đoạn (UFH) 5000 IU tiêm tĩnh mạch",
                    category = "Thuốc",
                    timeCostMinutes = 2,
                    isAppropriate = true,
                    isHazardous = false,
                    benefitDescription = "Chống đông trong hội chứng vành cấp trước can thiệp PCI.",
                    deltaPain = -1
                )
            ),
            correctDiagnosis = "Nhồi máu cơ tim cấp ST chênh lên (STEMI) thành dưới có biến chứng nhồi máu thất phải",
            differentialDiagnoses = listOf(
                "Nhồi máu cơ tim cấp ST chênh lên (STEMI) thành dưới có biến chứng nhồi máu thất phải",
                "Phình bóc tách động mạch chủ ngực (Type A)",
                "Thuyên tắc động mạch phổi cấp",
                "Viêm màng ngoài tim cấp",
                "Co thắt thực quản / Trào ngược dạ dày thực quản"
            ),
            correctPathophysiology = "Huyết khối tắc nghẽn hoàn toàn động mạch vành phải (RCA) gây thiếu máu hoại tử xuyên thành thất phải và thành dưới thất trái -> Thất phải suy giảm sức co bóp -> Giảm cung lượng máu lên phổi -> Thiếu thể tích đổ đầy thất trái (giảm tiền tải thất trái) -> Tụt huyết áp đặc biệt khi dùng thuốc giãn mạch.",
            pathophysiologyOptions = listOf(
                "Huyết khối tắc nghẽn hoàn toàn động mạch vành phải (RCA) gây thiếu máu hoại tử xuyên thành thất phải và thành dưới thất trái -> Thất phải suy giảm sức co bóp -> Giảm cung lượng máu lên phổi -> Thiếu thể tích đổ đầy thất trái (giảm tiền tải thất trái) -> Tụt huyết áp đặc biệt khi dùng thuốc giãn mạch.",
                "Rách lớp nội mạc động mạch chủ ngực lan xuống quai động mạch chủ làm chèn ép lỗ động mạch vành phải.",
                "Cục máu đông từ chi dưới di chuyển lên gây bít tắc thân chung động mạch phổi làm tăng áp lực phổi cấp tính.",
                "Phản ứng viêm toàn thân kích hoạt cytokin gây giãn mạch ngoại biên và tăng tính thấm thành mạch."
            ),
            guidelineCitation = "Khuyến cáo ESC 2023 / AHA 2023 về Hội chứng vành cấp: Trong STEMI thành dưới, BẮT BUỘC đo thêm V3R, V4R. Chống chỉ định Nitrat, thuốc lợi tiểu và thuốc ức chế beta khi có nhồi máu thất phải; ưu tiên truyền dịch muối đẳng trương để duy trì tiền tải.",
            takeHomePearl = "Bao giờ gặp STEMI DII, DIII, aVF cũng phải đo ngay V4R! Nếu có nhồi máu thất phải: KHÔNG DÙNG NITRATE & HÃY NÂNG TIỀN TẢI BẰNG TRUYỀN DỊCH.",
            aiPersonaPrompt = "Bạn là ông Nguyễn Văn Hùng, 67 tuổi. Bạn đang ở phòng cấp cứu, rất đau tức ngực kiểu đè nặng như đá đè sau xương ức, đau lan lên hàm và cánh tay trái. Bạn hơi choáng, vã mồ hôi và sợ hãi. Trả lời bằng giọng mệt mỏi, chân thật, trả lời đúng các câu hỏi về tiền sử hút thuốc, tăng huyết áp uống thuốc không đều."
        ),

        // CASE 2: ĐỢT CẤP SUY TIM MẤT BÙ & PHÙ PHỔI CẤP (ADHF)
        ClinicalCase(
            id = "cardio_adhf_pulmonary_edema",
            title = "Khó thở kịch phát & Cấp cứu Phù phổi cấp",
            specialty = "Tim mạch & Hồi sức",
            difficulty = "Nâng cao",
            patientName = "Lê Thị Mai",
            patientAge = 72,
            patientGender = "Nữ",
            chiefComplaint = "Khó thở dữ dội, không thể nằm, ho khạc bọt hồng",
            triageLevel = TriageLevel.RED,
            admissionStory = "Bệnh nhân nữ 72 tuổi, tiền sử suy tim mạn phân suất tống máu giảm (HFrEF EF 30%). 2 ngày nay ăn uống mặn và tự ý ngưng thuốc lợi tiểu. Nửa đêm nay đột ngột tỉnh giấc vì khó thở nghẹt thở, phải ngồi dậy thở dốc, ho khạc đờm bọt hồng, lo âu kích thích.",
            pastHistory = "Suy tim mạn do bệnh tim thiếu máu cục bộ (EF 30%), Tăng huyết áp 15 năm, Đái tháo đường type 2.",
            medications = "Furosemide 40mg (đã tự ngưng 2 ngày), Enalapril 10mg, Carvedilol 6.25mg.",
            allergies = "Không ghi nhận dị ứng.",
            socialHistory = "Nội trợ, sống cùng con cháu.",
            initialVitals = VitalSigns(
                heartRate = 118,
                systolicBp = 185,
                diastolicBp = 110,
                respiratoryRate = 34,
                spO2 = 83,
                temperature = 37.0f,
                painScore = 3
            ),
            budgetVnd = 4_500_000,
            timeLimitMinutes = 35,
            physicalExams = listOf(
                PhysicalExamItem(
                    id = "pe_resp_adhf",
                    systemName = "Hô hấp",
                    actionName = "Nghe phổi & Đánh giá kiểu thở",
                    timeCostMinutes = 2,
                    findings = "Thở nhanh nông 34 l/p, co kéo cơ liên sườn và cơ ức đòn chũm. Nghe ran ẩm to hạt dâng nhanh từ 2 đáy phổi lên tận đỉnh phổi cả 2 bên ('sóng thần ran ẩm'), kèm ran rít thì thở ra.",
                    isHighYield = true
                ),
                PhysicalExamItem(
                    id = "pe_cv_adhf",
                    systemName = "Tim mạch",
                    actionName = "Nghe tim & Khám tĩnh mạch cổ",
                    timeCostMinutes = 2,
                    findings = "Tim nhanh đều 118 l/p, T1 T2 mờ, có tiếng T3 Gallop rõ ở mỏm. Tĩnh mạch cổ nổi to ở tư thế ngồi 90 độ, phản hồi gan - tĩnh mạch cổ (+).",
                    isHighYield = true
                ),
                PhysicalExamItem(
                    id = "pe_gen_adhf",
                    systemName = "Toàn thân",
                    actionName = "Khám da niêm mạc & Chi dưới",
                    timeCostMinutes = 1,
                    findings = "Môi và đầu chi tím tái rõ, vã mồ hôi đầm đìa, phù mềm ấn lõm 2 cẳng bàn chân (3+).",
                    isHighYield = true
                ),
                PhysicalExamItem(
                    id = "pe_neuro_adhf",
                    systemName = "Thần kinh",
                    actionName = "Đánh giá tri giác",
                    timeCostMinutes = 1,
                    findings = "Bệnh nhân hốt hoảng, lo lắng cao độ, tiếp xúc khó do khó thở ngắt quãng từng từ.",
                    isHighYield = false
                )
            ),
            diagnosticTests = listOf(
                DiagnosticTest(
                    id = "test_ecg_adhf",
                    name = "ECG 12 chuyển đạo",
                    category = "Điện tim",
                    costVnd = 120_000,
                    turnaroundMinutes = 5,
                    diagnosticYield = DiagnosticYield.HIGH,
                    resultText = "Nhịp nhanh xoang 118 l/p. Dày thất trái theo tiêu chuẩn Sokolow-Lyon (SV1 + RV5 > 35mm). Thay đổi ST-T thứ phát, không có ST chênh lên cấp tính.",
                    clinicalSignificance = "Dày thất trái và nhịp nhanh xoang đáp ứng giao cảm; loại trừ STEMI rõ."
                ),
                DiagnosticTest(
                    id = "test_bnp",
                    name = "Định lượng NT-proBNP",
                    category = "Sinh hóa",
                    costVnd = 380_000,
                    turnaroundMinutes = 25,
                    diagnosticYield = DiagnosticYield.GOLD_STANDARD,
                    normalRange = "< 300 pg/mL",
                    resultText = "NT-proBNP: 8,450 pg/mL (Tăng cực kỳ cao)",
                    clinicalSignificance = "Xác nhận suy tim ứ huyết cấp tính nặng do quá tải thể tích và áp lực buồng tim."
                ),
                DiagnosticTest(
                    id = "test_abg",
                    name = "Khí máu động mạch (ABG)",
                    category = "Sinh hóa",
                    costVnd = 220_000,
                    turnaroundMinutes = 15,
                    diagnosticYield = DiagnosticYield.HIGH,
                    normalRange = "pH 7.35-7.45, PaO2 80-100, PaCO2 35-45",
                    resultText = "pH: 7.28, PaO2: 52 mmHg, PaCO2: 49 mmHg, HCO3-: 23 mmol/L, SaO2: 84%.",
                    clinicalSignificance = "Toan hô hấp kết hợp suy hô hấp giảm oxy máu và ứ CO2 cấp tính."
                ),
                DiagnosticTest(
                    id = "test_cxr_adhf",
                    name = "X-quang ngực thẳng tại giường",
                    category = "Hình ảnh",
                    costVnd = 180_000,
                    turnaroundMinutes = 10,
                    diagnosticYield = DiagnosticYield.GOLD_STANDARD,
                    resultText = "Bóng tim to (chỉ số tim/lồng ngực > 0.65). Hình ảnh 'cánh bướm' mờ phế nang 2 bên rốn phổi, đường Kerley B ở đáy phổi, tái phân bố tuần hoàn đỉnh phổi.",
                    clinicalSignificance = "Phù phổi cấp huyết động điển hình."
                ),
                DiagnosticTest(
                    id = "test_gastroscopy",
                    name = "Nội soi dạ dày cấp cứu",
                    category = "Chuyên khoa",
                    costVnd = 950_000,
                    turnaroundMinutes = 60,
                    diagnosticYield = DiagnosticYield.UNNECESSARY,
                    resultText = "Không có chỉ định.",
                    clinicalSignificance = "Hoàn toàn vô lý và cực kỳ nguy hiểm trên bệnh nhân đang suy hô hấp cấp."
                )
            ),
            interventions = listOf(
                MedicalIntervention(
                    id = "rx_fowler",
                    name = "Đặt bệnh nhân ngồi tư thế Fowler cao (thõng 2 chân)",
                    category = "Hô hấp",
                    timeCostMinutes = 1,
                    isAppropriate = true,
                    isHazardous = false,
                    benefitDescription = "Giảm lượng máu tĩnh mạch trở về tim (giảm tiền tải), hạ áp lực mao mạch phổi bít và giúp cơ hoành cử động dễ hơn.",
                    deltaSpO2 = 3,
                    deltaHr = -5
                ),
                MedicalIntervention(
                    id = "rx_cpap",
                    name = "Thở máy không xâm lấn CPAP / BiPAP (NIV)",
                    category = "Hô hấp",
                    timeCostMinutes = 3,
                    isAppropriate = true,
                    isHazardous = false,
                    benefitDescription = "Tạo áp lực dương cuối thì thở ra (PEEP), đẩy dịch từ phế nang về mô kẽ, giảm công thở và giảm nhu cầu đặt nội khí quản.",
                    deltaSpO2 = 11,
                    deltaHr = -15,
                    deltaSystolicBp = -15
                ),
                MedicalIntervention(
                    id = "rx_furosemide_iv",
                    name = "Furosemide 40mg - 80mg tiêm tĩnh mạch chậm",
                    category = "Thuốc",
                    timeCostMinutes = 2,
                    isAppropriate = true,
                    isHazardous = false,
                    benefitDescription = "Giãn tĩnh mạch tức thì sau 5-15 phút (giảm tiền tải), sau đó gây bài niệu thải muối và nước giải phóng thể tích dịch ứ trệ.",
                    deltaSystolicBp = -15,
                    deltaDiastolicBp = -10,
                    deltaSpO2 = 4
                ),
                MedicalIntervention(
                    id = "rx_nitroglycerin_infusion",
                    name = "Truyền tĩnh mạch Nitroglycerin liều khởi đầu 10-20 mcg/phút",
                    category = "Thuốc",
                    timeCostMinutes = 3,
                    isAppropriate = true,
                    isHazardous = false,
                    benefitDescription = "Hạ áp, giãn tĩnh mạch và tiểu động mạch, giảm mạnh cả tiền tải và hậu tải ở bệnh nhân suy tim có huyết áp cao (185/110 mmHg).",
                    deltaSystolicBp = -30,
                    deltaDiastolicBp = -15,
                    deltaHr = -10
                ),
                MedicalIntervention(
                    id = "rx_betablocker_hazard",
                    name = "Metoprolol / Carvedilol tăng liều tĩnh mạch",
                    category = "Thuốc",
                    timeCostMinutes = 2,
                    isAppropriate = false,
                    isHazardous = true,
                    hazardReason = "CHỐNG CHỈ ĐỊNH TUYỆT ĐỐI TRONG SUY TIM MẤT BÙ CẤP NẶNG! Thuốc ức chế inotrope âm tính sẽ làm sụp đổ hoàn toàn chức năng bóp của tâm thất và đẩy bệnh nhân vào sốc tim tử vong!",
                    benefitDescription = "",
                    deltaHr = -30,
                    deltaSystolicBp = -50,
                    deltaDiastolicBp = -30,
                    deltaSpO2 = -15
                ),
                MedicalIntervention(
                    id = "rx_iv_bolus_saline",
                    name = "Truyền nhanh Natri Clorid 0.9% 500ml",
                    category = "Dịch truyền",
                    timeCostMinutes = 5,
                    isAppropriate = false,
                    isHazardous = true,
                    hazardReason = "Bệnh nhân đang ứ dịch và phù phổi cấp nghiêm trọng, truyền thêm muối nước sẽ làm dịch tràn ngập phế nang và gây ngạt thở!",
                    benefitDescription = "",
                    deltaSpO2 = -10,
                    deltaSystolicBp = 10
                )
            ),
            correctDiagnosis = "Đợt cấp suy tim mất bù (ADHF) thể ướt - ấm biến chứng phù phổi cấp huyết động",
            differentialDiagnoses = listOf(
                "Đợt cấp suy tim mất bù (ADHF) thể ướt - ấm biến chứng phù phổi cấp huyết động",
                "Đợt cấp bệnh phổi tắc nghẽn mạn tính (AECOPD)",
                "Cơn hen tim kết hợp viêm phổi nặng",
                "Thuyên tắc phổi cấp"
            ),
            correctPathophysiology = "Ngưng lợi tiểu và ăn mặn -> Giữ muối nước -> Tăng thể tích dịch tuần hoàn kết hợp tăng hậu tải do co mạch giao cảm -> Áp lực thất trái và áp lực mao mạch phổi bít (PCWP) tăng vượt ngưỡng 25 mmHg -> Dịch thoát ồ ạt qua màng phế nang mao mạch vào lòng phế nang gây cản trở trao đổi khí.",
            pathophysiologyOptions = listOf(
                "Ngưng lợi tiểu và ăn mặn -> Giữ muối nước -> Tăng thể tích dịch tuần hoàn kết hợp tăng hậu tải do co mạch giao cảm -> Áp lực thất trái và áp lực mao mạch phổi bít (PCWP) tăng vượt ngưỡng 25 mmHg -> Dịch thoát ồ ạt qua màng phế nang mao mạch vào lòng phế nang gây cản trở trao đổi khí.",
                "Co thắt phế quản lan tỏa do tăng phản ứng đường thở với dị nguyên ngoại lai.",
                "Tắc nghẽn vi mạch phổi thứ phát sau hoại tử tế bào gan.",
                "Tổn thương phế nang lan tỏa do độc tố vi khuẩn (ARDS)."
            ),
            guidelineCitation = "Khuyến cáo ESC 2021/2023 về Suy tim cấp: Ở bệnh nhân suy tim có phù phổi cấp và HA tâm thu > 110 mmHg, ưu tiên hàng đầu là Thông khí áp lực dương không xâm lấn (NIV), Lợi tiểu quai tiêm TM và Thuốc giãn mạch Nitrat đường tĩnh mạch để giảm nhanh tiền tải và hậu tải. Chống chỉ định khởi trị thuốc ức chế beta trong giai đoạn ứ dịch cấp tính.",
            takeHomePearl = "Phù phổi cấp tăng huyết áp: 'Ngồi cao + NIV + Lợi tiểu quai tiêm TM + Nitrat truyền TM'. Đừng bao giờ cho thêm Ức chế Beta hay truyền dịch!",
            aiPersonaPrompt = "Bạn là cụ bà Lê Thị Mai, 72 tuổi. Bạn đang thở hổn hển, ho khạc ra bọt màu hồng, cảm giác như đang bị dìm dưới nước. Bạn không thể nói trọn câu dài, chỉ thốt lên: 'Bác sĩ ơi... cứu tôi... tôi nghẹt thở quá... không nằm được...'"
        ),

        // CASE 3: THUYÊN TẮC PHỔI CẤP (PULMONARY EMBOLISM)
        ClinicalCase(
            id = "resp_pulmonary_embolism",
            title = "Khó thở đột ngột & Đau ngực màng phổi sau phẫu thuật",
            specialty = "Hô hấp & Hồi sức",
            difficulty = "Nâng cao",
            patientName = "Hoàng Thị Tuyết",
            patientAge = 54,
            patientGender = "Nữ",
            chiefComplaint = "Khó thở khởi phát đột ngột, đau ngực nhói khi hít sâu",
            triageLevel = TriageLevel.RED,
            admissionStory = "Bệnh nhân nữ 54 tuổi, vừa trải qua phẫu thuật thay khớp háng phải cách đây 12 ngày, nằm bất động tại giường nhiều ngày. Sáng nay khi vừa gắng sức đứng dậy tập đi thì đột ngột thấy khó thở dữ dội, đau ngực kiểu màng phổi bên phải tăng khi hít vào, ho khan có dính ít tia máu tươi.",
            pastHistory = "Mổ thay khớp háng 12 ngày trước, Thừa cân (BMI 29), Tĩnh mạch chi dưới giãn nhẹ.",
            medications = "Paracetamol 500mg, đã dừng thuốc chống đông Enoxaparin dự phòng 3 ngày trước theo toa xuất viện.",
            allergies = "Không có.",
            socialHistory = "Nhân viên văn phòng, không hút thuốc.",
            initialVitals = VitalSigns(
                heartRate = 124,
                systolicBp = 92,
                diastolicBp = 62,
                respiratoryRate = 30,
                spO2 = 88,
                temperature = 37.4f,
                painScore = 7
            ),
            budgetVnd = 5_500_000,
            timeLimitMinutes = 40,
            physicalExams = listOf(
                PhysicalExamItem(
                    id = "pe_pe_resp",
                    systemName = "Hô hấp",
                    actionName = "Khám hô hấp",
                    timeCostMinutes = 2,
                    findings = "Thở nhanh nông 30 l/p, rì rào phế nang giảm nhẹ đáy phổi phải, không nghe ran bệnh lý rõ ràng (phổi nghe 'quá sạch' so với mức độ khó thở).",
                    isHighYield = true
                ),
                PhysicalExamItem(
                    id = "pe_pe_cv",
                    systemName = "Tim mạch",
                    actionName = "Khám tim & Tĩnh mạch",
                    timeCostMinutes = 2,
                    findings = "Nhịp tim rất nhanh 124 l/p, T2 mạnh ở van động mạch phổi (T2P > T2A). Tĩnh mạch cổ nổi nhẹ.",
                    isHighYield = true
                ),
                PhysicalExamItem(
                    id = "pe_pe_legs",
                    systemName = "Khám chi dưới",
                    actionName = "Đánh giá bắp chân & Huyết khối tĩnh mạch sâu (DVT)",
                    timeCostMinutes = 2,
                    findings = "Bắp chân phải sưng to hơn bắp chân trái 3cm, nóng, ấn đau dọc theo tĩnh mạch đùi phải, dấu Homans (+).",
                    isHighYield = true
                )
            ),
            diagnosticTests = listOf(
                DiagnosticTest(
                    id = "test_ecg_pe",
                    name = "Điện tâm đồ (ECG)",
                    category = "Điện tim",
                    costVnd = 120_000,
                    turnaroundMinutes = 5,
                    diagnosticYield = DiagnosticYield.HIGH,
                    resultText = "Nhịp nhanh xoang 124 l/p. Dấu hiệu kinh điển McGinn-White (S1Q3T3): S sâu ở DI, Q sâu ở DIII, T âm ở DIII. T âm từ V1 đến V4.",
                    clinicalSignificance = "Gợi ý tăng gánh thất phải cấp tính (acute RV strain)."
                ),
                DiagnosticTest(
                    id = "test_d_dimer",
                    name = "Định lượng D-dimer huyết tương",
                    category = "Huyết học",
                    costVnd = 320_000,
                    turnaroundMinutes = 20,
                    diagnosticYield = DiagnosticYield.HIGH,
                    normalRange = "< 500 ng/mL FEU",
                    resultText = "D-dimer: 4,800 ng/mL FEU (Tăng rất cao)",
                    clinicalSignificance = "Giá trị tiên lượng loại trừ cao; trên bệnh nhân có nguy cơ cao (Wells score > 6) khẳng định quá trình tạo và tiêu cục đông huyết khối."
                ),
                DiagnosticTest(
                    id = "test_ctpa",
                    name = "Chụp CT mạch máu phổi có cản quang (CTPA)",
                    category = "Hình ảnh",
                    costVnd = 2_200_000,
                    turnaroundMinutes = 25,
                    diagnosticYield = DiagnosticYield.GOLD_STANDARD,
                    resultText = "Hình ảnh khuyết thuốc cản quang dạng bít tắc hoàn toàn nhánh chính động mạch phổi phải và tắc một phần nhánh động mạch phổi trái. Tỉ số đường kính RV/LV = 1.3.",
                    clinicalSignificance = "TIÊU CHUẨN VÀNG: Xác chẩn Thuyên tắc phổi cấp tính nguy cơ trung bình - cao!"
                ),
                DiagnosticTest(
                    id = "test_us_doppler_leg",
                    name = "Siêu âm Doppler mạch máu chi dưới",
                    category = "Hình ảnh",
                    costVnd = 400_000,
                    turnaroundMinutes = 15,
                    diagnosticYield = DiagnosticYield.HIGH,
                    resultText = "Tĩnh mạch đùi nông và tĩnh mạch khoeo chân phải không xẹp khi đè ép đầu dò, có tín hiệu cục huyết khối bên trong.",
                    clinicalSignificance = "Xác nhận huyết khối tĩnh mạch sâu (DVT) là ổ xuất phát của thuyên tắc phổi."
                )
            ),
            interventions = listOf(
                MedicalIntervention(
                    id = "rx_o2_pe",
                    name = "Thở oxy qua mặt nạ túi dự trữ 10L/phút",
                    category = "Hô hấp",
                    timeCostMinutes = 1,
                    isAppropriate = true,
                    isHazardous = false,
                    benefitDescription = "Cải thiện ngay tình trạng thiếu oxy máu mô do bất tương xứng thông khí / tưới máu (V/Q mismatch).",
                    deltaSpO2 = 8,
                    deltaHr = -6
                ),
                MedicalIntervention(
                    id = "rx_enoxaparin",
                    name = "Khởi trị Chống đông: Heparin TLPT thấp (LMWH) Enoxaparin 1mg/kg TDD",
                    category = "Thuốc",
                    timeCostMinutes = 2,
                    isAppropriate = true,
                    isHazardous = false,
                    benefitDescription = "Ngăn chặn cục huyết khối lan rộng và hạn chế tái phát thuyên tắc đe dọa tính mạng.",
                    deltaPain = -1
                ),
                MedicalIntervention(
                    id = "rx_fluid_resus_pe",
                    name = "Truyền dịch thận trọng Natri Clorid 0.9% 250ml",
                    category = "Dịch truyền",
                    timeCostMinutes = 5,
                    isAppropriate = true,
                    isHazardous = false,
                    benefitDescription = "Tối ưu hóa tiền tải thất phải mà không gây quá tải căng giãn thất phải thêm.",
                    deltaSystolicBp = 8,
                    deltaDiastolicBp = 5
                ),
                MedicalIntervention(
                    id = "rx_thrombolytic_eval",
                    name = "Đánh giá chỉ định Tiêu sợi huyết (rt-PA / Alteplase)",
                    category = "Can thiệp chuyên khoa",
                    timeCostMinutes = 5,
                    isAppropriate = true,
                    isHazardous = false,
                    benefitDescription = "Chuẩn bị thuốc tiêu sợi huyết nếu bệnh nhân có tụt huyết áp kéo dài (Sốc do thuyên tắc phổi nguy cơ cao).",
                    deltaPain = -1
                ),
                MedicalIntervention(
                    id = "rx_antibiotics_waste",
                    name = "Ceftriaxone 2g tiêm tĩnh mạch",
                    category = "Thuốc",
                    timeCostMinutes = 3,
                    isAppropriate = false,
                    isHazardous = false,
                    hazardReason = "Bệnh nhân không có dấu hiệu nhiễm trùng. Khó thở và đau ngực sau mổ là do huyết khối thuyên tắc, không phải viêm phổi.",
                    benefitDescription = "Không mang lại lợi ích trong thuyên tắc mạch."
                )
            ),
            correctDiagnosis = "Thuyên tắc động mạch phổi cấp tính nguy cơ trung bình - cao do huyết khối tĩnh mạch sâu chi dưới",
            differentialDiagnoses = listOf(
                "Thuyên tắc động mạch phổi cấp tính nguy cơ trung bình - cao do huyết khối tĩnh mạch sâu chi dưới",
                "Nhồi máu cơ tim cấp không ST chênh lên (NSTEMI)",
                "Tràn khí màng phổi tự phát",
                "Viêm phổi thùy biến chứng suy hô hấp",
                "Cơn hoảng loạn (Panic Attack)"
            ),
            correctPathophysiology = "Huyết khối hình thành ở tĩnh mạch sâu chân phải do bất động sau mổ bung ra -> Trôi theo tĩnh mạch chủ dưới về tim phải -> Bị kẹt bít tắc các nhánh động mạch phổi -> Tạo khoảng chết phế nang (V/Q mismatch) gây hạ oxy máu nặng + Tăng kháng lực mạch phổi đột ngột gây suy thất phải cấp và tụt huyết áp.",
            pathophysiologyOptions = listOf(
                "Huyết khối hình thành ở tĩnh mạch sâu chân phải do bất động sau mổ bung ra -> Trôi theo tĩnh mạch chủ dưới về tim phải -> Bị kẹt bít tắc các nhánh động mạch phổi -> Tạo khoảng chết phế nang (V/Q mismatch) gây hạ oxy máu nặng + Tăng kháng lực mạch phổi đột ngột gây suy thất phải cấp và tụt huyết áp.",
                "Rò rỉ dịch mủ từ ổ phẫu thuật háng vào tuần hoàn máu gây sốc phản vệ.",
                "Vi khuẩn phế cầu xâm nhập màng phổi gây tràn dịch màng phổi mủ.",
                "Co thắt cơ trơn phế quản do hít phải dị vật."
            ),
            guidelineCitation = "Khuyến cáo ESC 2019 về Thuyên tắc phổi cấp: Ở bệnh nhân có xác suất lâm sàng cao (Wells > 4 điểm), chỉ định chụp CTPA ngay lập tức. Khởi trị chống đông toàn thân bằng LMWH/UFH ngay trong lúc chờ cận lâm sàng xác chẩn.",
            takeHomePearl = "Bất động sau mổ + Khó thở đột ngột + Đau ngực kiểu màng phổi + Phổi nghe trong = Nghĩ ngay tới THUYÊN TẮC PHỔI CẤP. Hãy cho chống đông sớm!",
            aiPersonaPrompt = "Bạn là cô Hoàng Thị Tuyết, 54 tuổi. Bạn vừa mới mổ chân xong, bây giờ ngực bên phải đau nhói như dao đâm mỗi khi hít thở, bạn thở gấp, người vã mồ hôi và chân phải rất nhức buốt."
        ),

        // CASE 4: PHÌNH TÁCH ĐỘNG MẠCH CHỦ NGỰC (AORTIC DISSECTION)
        ClinicalCase(
            id = "cardio_aortic_dissection",
            title = "Đau ngực xé rách lan sau lưng & Chênh lệch huyết áp",
            specialty = "Tim mạch & Phẫu thuật lồng ngực",
            difficulty = "Chuyên gia",
            patientName = "Vũ Đình Trọng",
            patientAge = 58,
            patientGender = "Nam",
            chiefComplaint = "Đau ngực dữ dội đột ngột như dao xé lan xuyên ra sau 2 bả vai",
            triageLevel = TriageLevel.RED,
            admissionStory = "Bệnh nhân nam 58 tuổi, tiền sử tăng huyết áp phát hiện 7 năm nhưng không tuân thủ điều trị. 1 giờ trước, khi đang ngồi làm việc đột ngột đau ngực mức độ cực đại (10/10) ngay từ giây đầu tiên. Bệnh nhân mô tả cơn đau như bị dao xé rách ở giữa ngực lan xuyên thẳng ra sau lưng giữa 2 xương bả vai.",
            pastHistory = "Tăng huyết áp không kiểm soát (HA thường ngày 160-180 mmHg). Hút thuốc lá.",
            medications = "Amlodipine 5mg (uống ngắt quãng khi thấy nhức đầu).",
            allergies = "Chưa ghi nhận.",
            socialHistory = "Kỹ sư xây dựng, căng thẳng kéo dài.",
            initialVitals = VitalSigns(
                heartRate = 96,
                systolicBp = 195,
                diastolicBp = 115,
                respiratoryRate = 24,
                spO2 = 97,
                temperature = 36.9f,
                painScore = 10
            ),
            budgetVnd = 6_000_000,
            timeLimitMinutes = 35,
            physicalExams = listOf(
                PhysicalExamItem(
                    id = "pe_dissect_bp",
                    systemName = "Huyết áp hai tay",
                    actionName = "Đo huyết áp đối xứng 2 tay và bắt mạch ngoại vi",
                    timeCostMinutes = 2,
                    findings = "Huyết áp tay phải: 195/115 mmHg. Huyết áp tay trái: 155/90 mmHg (Chênh lệch HA tâm thu > 40 mmHg!). Mạch quay tay trái yếu hơn tay phải rõ rệt.",
                    isHighYield = true
                ),
                PhysicalExamItem(
                    id = "pe_dissect_cv",
                    systemName = "Tim mạch",
                    actionName = "Nghe tim",
                    timeCostMinutes = 2,
                    findings = "Nghe tiếng thổi tâm trương sớm dạng phụt 3/6 ở bờ trái xương ức (hở van động mạch chủ cấp tính mới xuất hiện).",
                    isHighYield = true
                ),
                PhysicalExamItem(
                    id = "pe_dissect_neuro",
                    systemName = "Thần kinh",
                    actionName = "Khám thần kinh",
                    timeCostMinutes = 1,
                    findings = "Bệnh nhân tỉnh, kêu đau dữ dội, không liệt vận động.",
                    isHighYield = false
                )
            ),
            diagnosticTests = listOf(
                DiagnosticTest(
                    id = "test_cta_aorta",
                    name = "Chụp CT mạch máu (CTA) toàn bộ động mạch chủ ngực - bụng",
                    category = "Hình ảnh",
                    costVnd = 2_500_000,
                    turnaroundMinutes = 20,
                    diagnosticYield = DiagnosticYield.GOLD_STANDARD,
                    resultText = "TIÊU CHUẨN VÀNG: Rách lớp nội mạc động mạch chủ lên (Stanford Type A), vạt lóc nội mạc (intimal flap) tạo lòng thật và lòng giả lan từ gốc ĐMC đến quai ĐMC, chèn ép lỗ vào nhánh thân cánh tay đầu.",
                    clinicalSignificance = "Chẩn đoán xác định Phình bóc tách ĐMC cấp tính Stanford Type A - CẦN PHẪU THUẬT CẤP CỨU TỐI KHẨN!"
                ),
                DiagnosticTest(
                    id = "test_cxr_dissect",
                    name = "X-quang ngực thẳng",
                    category = "Hình ảnh",
                    costVnd = 180_000,
                    turnaroundMinutes = 10,
                    diagnosticYield = DiagnosticYield.HIGH,
                    resultText = "Trung thất trên giãn rộng (> 8cm), bờ quai động mạch chủ lồi và gập góc bất thường.",
                    clinicalSignificance = "Gợi ý giãn hoặc bóc tách động mạch chủ ngực."
                ),
                DiagnosticTest(
                    id = "test_ecg_dissect",
                    name = "ECG 12 chuyển đạo",
                    category = "Điện tim",
                    costVnd = 120_000,
                    turnaroundMinutes = 5,
                    diagnosticYield = DiagnosticYield.MODERATE,
                    resultText = "Dày thất trái, không có hình ảnh ST chênh lên rõ ràng.",
                    clinicalSignificance = "Rất quan trọng để TRÁNH nhầm lẫn với nhồi máu cơ tim cấp (chống chỉ định dùng thuốc tiêu sợi huyết)."
                )
            ),
            interventions = listOf(
                MedicalIntervention(
                    id = "rx_surgical_consult",
                    name = "Mời hội chẩn Phẫu thuật Tim mạch - Lồng ngực khẩn cấp",
                    category = "Can thiệp chuyên khoa",
                    timeCostMinutes = 2,
                    isAppropriate = true,
                    isHazardous = false,
                    benefitDescription = "Stanford Type A có nguy cơ vỡ và tử vong 1-2% mỗi giờ; phẫu thuật thay đoạn ĐMC lên là phương pháp cứu mạng duy nhất.",
                    deltaPain = -1
                ),
                MedicalIntervention(
                    id = "rx_esmolol_iv",
                    name = "Kiểm soát nhịp tim & huyết áp: Esmolol / Labetalol truyền tĩnh mạch",
                    category = "Thuốc",
                    timeCostMinutes = 3,
                    isAppropriate = true,
                    isHazardous = false,
                    benefitDescription = "Hạ nhịp tim (< 60 bpm) và giảm lực xé dP/dt của dòng máu tống vào thành động mạch chủ trước khi hạ áp, ngăn rách lan rộng.",
                    deltaHr = -26,
                    deltaSystolicBp = -35,
                    deltaDiastolicBp = -20,
                    deltaPain = -3
                ),
                MedicalIntervention(
                    id = "rx_morphine_dissect",
                    name = "Morphin sulfat 2-4mg tiêm tĩnh mạch",
                    category = "Thuốc",
                    timeCostMinutes = 1,
                    isAppropriate = true,
                    isHazardous = false,
                    benefitDescription = "Giảm đau thần tốc, ức chế bùng phát giao cảm làm giảm huyết áp.",
                    deltaPain = -4,
                    deltaHr = -6,
                    deltaSystolicBp = -10
                ),
                MedicalIntervention(
                    id = "rx_thrombolysis_disaster",
                    name = "Dùng thuốc Tiêu sợi huyết (rt-PA) vì nghĩ nhồi máu cơ tim",
                    category = "Thuốc",
                    timeCostMinutes = 5,
                    isAppropriate = false,
                    isHazardous = true,
                    hazardReason = "THẢM HỌA TỬ VONG! Thuốc tiêu sợi huyết làm tan huyết khối bảo vệ tại thành động mạch chủ đang rách, dẫn đến vỡ động mạch chủ vào khoang màng tim, chèn ép tim cấp và tử vong trong vài phút!",
                    benefitDescription = "",
                    deltaSystolicBp = -80,
                    deltaDiastolicBp = -50,
                    deltaHr = 40,
                    deltaPain = 5
                )
            ),
            correctDiagnosis = "Phình bóc tách động mạch chủ ngực cấp tính Stanford Type A (DeBakey I/II)",
            differentialDiagnoses = listOf(
                "Phình bóc tách động mạch chủ ngực cấp tính Stanford Type A (DeBakey I/II)",
                "Nhồi máu cơ tim cấp thành trước",
                "Thủng ổ loét dạ dày tá tràng",
                "Thuyên tắc phổi cấp"
            ),
            correctPathophysiology = "Huyết áp cao không kiểm soát tạo áp lực cơ học mạn tính làm suy thoái lớp áo giữa (tunica media) động mạch chủ -> Rách lớp nội mạc tạo đường cho dòng máu áp lực cao xé toạc lớp áo giữa tạo thành lòng giả (false lumen) -> Chèn ép các nhánh động mạch nuôi não, tay và van ĐMC, nguy cơ vỡ vào khoang màng tim gây tử vong.",
            pathophysiologyOptions = listOf(
                "Huyết áp cao không kiểm soát tạo áp lực cơ học mạn tính làm suy thoái lớp áo giữa (tunica media) động mạch chủ -> Rách lớp nội mạc tạo đường cho dòng máu áp lực cao xé toạc lớp áo giữa tạo thành lòng giả (false lumen) -> Chèn ép các nhánh động mạch nuôi não, tay và van ĐMC, nguy cơ vỡ vào khoang màng tim gây tử vong.",
                "Mảng xơ vữa nứt vỡ trong lòng động mạch vành gây kích hoạt tiểu cầu bít tắc lòng mạch.",
                "Rối loạn thần kinh thực vật gây co thắt mạch máu chi.",
                "Viêm phế quản co thắt phản ứng."
            ),
            guidelineCitation = "Khuyến cáo ESC / AHA về Bệnh lý Động mạch chủ: Khi nghi ngờ bóc tách ĐMC, chụp CTA ngực khẩn cấp. Mục tiêu nội khoa trước mổ: kiểm soát nhịp tim < 60 bpm bằng Ức chế Beta trước, sau đó hạ HA tâm thu xuống 100-120 mmHg. Chống chỉ định dùng thuốc tiêu sợi huyết và thuốc chống đông!",
            takeHomePearl = "Đau ngực xé rách ra sau lưng + HA 2 tay chênh lệch + Âm thổi hở van ĐMC mới = BÓC TÁCH ĐỘNG MẠCH CHỦ TYPE A. Tuyệt đối không cho tiêu sợi huyết!",
            aiPersonaPrompt = "Bạn là chú Vũ Đình Trọng, 58 tuổi. Cơn đau ngực sau lưng của chú đang xé nát người chú, đau kinh hoàng chưa từng thấy trong đời, chú thở gấp và ôm chặt ngực sau lưng kêu la."
        )
    )

    fun getCaseById(id: String): ClinicalCase? {
        return cases.find { it.id == id } ?: cases.firstOrNull()
    }
}
