package com.example.data.repository

import android.content.Context
import com.example.data.cases.ClinicalCaseRepository
import com.example.data.firebase.FirebaseService
import com.example.data.gemini.GeminiService
import com.example.data.local.AppDatabase
import com.example.data.local.CaseAttemptEntity
import com.example.data.local.UserProfileEntity
import com.example.data.model.ClinicalCase
import com.example.data.model.DebriefResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class MedSimRepository(
    private val context: Context,
    private val database: AppDatabase = AppDatabase.getDatabase(context),
    private val geminiService: GeminiService = GeminiService(),
    private val firebaseService: FirebaseService = FirebaseService(context)
) {
    private val attemptDao = database.caseAttemptDao()
    private val profileDao = database.userProfileDao()

    fun getAllAttempts(): Flow<List<CaseAttemptEntity>> = attemptDao.getAllAttempts()

    fun getBestAttemptForCase(caseId: String): Flow<CaseAttemptEntity?> =
        attemptDao.getBestAttemptForCase(caseId)

    fun getUserProfile(): Flow<UserProfileEntity?> = profileDao.getUserProfile()

    suspend fun chatWithPatient(
        clinicalCase: ClinicalCase,
        question: String,
        history: List<Pair<String, String>>
    ): String {
        return geminiService.chatWithPatient(clinicalCase, question, history)
    }

    suspend fun generateClinicalDebrief(
        clinicalCase: ClinicalCase,
        debrief: DebriefResult
    ): String {
        return geminiService.generateClinicalDebrief(clinicalCase, debrief)
    }

    suspend fun recordCaseCompletion(debrief: DebriefResult): UserProfileEntity {
        // 1. Save Attempt to Room
        val attempt = CaseAttemptEntity(
            caseId = debrief.caseId,
            caseTitle = debrief.caseTitle,
            specialty = ClinicalCaseRepository.getCaseById(debrief.caseId)?.specialty ?: "Nội khoa",
            score = debrief.overallScore,
            historyScore = debrief.historyScore,
            examScore = debrief.physicalExamScore,
            reasoningScore = debrief.clinicalReasoningScore,
            stewardshipScore = debrief.diagnosticStewardshipScore,
            accuracyScore = debrief.diagnosticAccuracyScore,
            therapeuticScore = debrief.therapeuticScore,
            safetyScore = debrief.patientSafetyScore,
            timeUsedMinutes = debrief.timeUsedMinutes,
            budgetUsedVnd = debrief.budgetUsedVnd
        )
        attemptDao.insertAttempt(attempt)

        // 2. Update User Profile & XP
        val currentProfile = profileDao.getUserProfile().firstOrNull() ?: UserProfileEntity()
        val newXp = currentProfile.currentXp + debrief.xpGained
        val newTotalScore = currentProfile.totalScore + debrief.overallScore
        val newCasesCount = currentProfile.casesCompleted + 1

        val newLevel = (newXp / 500) + 1
        val newRank = when {
            newLevel >= 10 -> "Trưởng khoa Lâm sàng (Chief Attending)"
            newLevel >= 7 -> "Bác sĩ Chuyên khoa II (Senior Specialist)"
            newLevel >= 5 -> "Bác sĩ Chuyên khoa I (Specialist)"
            newLevel >= 3 -> "Bác sĩ Nội trú (Resident Physician)"
            newLevel >= 2 -> "Bác sĩ Thực hành (Junior Doctor)"
            else -> "Sinh viên Y khoa Lâm sàng (Medical Student)"
        }

        val updatedProfile = currentProfile.copy(
            level = newLevel,
            currentXp = newXp,
            totalScore = newTotalScore,
            casesCompleted = newCasesCount,
            clinicalRank = newRank,
            lastUpdated = System.currentTimeMillis()
        )
        profileDao.insertOrUpdateProfile(updatedProfile)

        // 3. Sync to Firebase
        val currentUser = firebaseService.getCurrentUser()
        if (currentUser != null) {
            firebaseService.syncUserProfileToFirestore(
                uid = currentUser.uid,
                displayName = currentUser.displayName ?: updatedProfile.displayName,
                email = currentUser.email ?: "",
                level = newLevel,
                xp = newXp,
                title = newRank,
                casesCompleted = newCasesCount
            )
            firebaseService.saveCaseResultToFirestore(currentUser.uid, debrief)
        }

        return updatedProfile
    }

    suspend fun signInAnonymous() {
        firebaseService.signInAnonymously()
    }
}
