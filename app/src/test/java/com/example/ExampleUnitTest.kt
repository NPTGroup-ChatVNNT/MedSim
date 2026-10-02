package com.example

import com.example.data.cases.ClinicalCaseRepository
import com.example.data.model.DiagnosticYield
import org.junit.Assert.*
import org.junit.Test

class ClinicalCaseTest {

    @Test
    fun verifyCaseRepositoryIntegrity() {
        val cases = ClinicalCaseRepository.cases
        assertTrue("Case repository should contain at least 4 clinical cases", cases.size >= 4)

        for (c in cases) {
            assertNotNull("Case title should not be null", c.title)
            assertTrue("Case should have physical exams", c.physicalExams.isNotEmpty())
            assertTrue("Case should have diagnostic tests", c.diagnosticTests.isNotEmpty())
            assertTrue("Case should have available interventions", c.interventions.isNotEmpty())
            assertTrue("Case should have differential diagnoses", c.differentialDiagnoses.isNotEmpty())
            assertNotNull("Correct diagnosis should be defined", c.correctDiagnosis)
            assertTrue(
                "Differential diagnoses must contain the correct diagnosis",
                c.differentialDiagnoses.contains(c.correctDiagnosis)
            )
            assertTrue(
                "Pathophysiology options must contain correct pathophysiology",
                c.pathophysiologyOptions.contains(c.correctPathophysiology)
            )
        }
    }

    @Test
    fun verifyStemiCaseHazardsAndPreloadDependence() {
        val stemiCase = ClinicalCaseRepository.getCaseById("cardio_stemi_rv")
        assertNotNull("STEMI case must exist", stemiCase)

        val nitroHazard = stemiCase!!.interventions.find { it.id == "rx_nitroglycerin" }
        assertNotNull("Nitroglycerin must be present as a key clinical test", nitroHazard)
        assertTrue("Nitroglycerin in RV infarction must be marked hazardous", nitroHazard!!.isHazardous)
        assertFalse("Nitroglycerin must not be appropriate in RV infarction", nitroHazard.isAppropriate)

        val ecgTest = stemiCase.diagnosticTests.find { it.id == "test_ecg_12" }
        assertNotNull("12-lead ECG must be available", ecgTest)
        assertEquals(DiagnosticYield.GOLD_STANDARD, ecgTest!!.diagnosticYield)

        val rightLeadEcg = stemiCase.diagnosticTests.find { it.id == "test_ecg_right" }
        assertNotNull("Right lead ECG (V4R) must be available", rightLeadEcg)
        assertEquals(DiagnosticYield.GOLD_STANDARD, rightLeadEcg!!.diagnosticYield)
    }

    @Test
    fun verifyAdhfCaseHazards() {
        val adhfCase = ClinicalCaseRepository.getCaseById("cardio_adhf_pulmonary_edema")
        assertNotNull("ADHF case must exist", adhfCase)

        val bbHazard = adhfCase!!.interventions.find { it.id == "rx_betablocker_hazard" }
        assertNotNull("Beta-blocker in acute pulmonary edema must be present", bbHazard)
        assertTrue("Beta-blocker in acute pulmonary edema must be marked hazardous", bbHazard!!.isHazardous)
    }
}
