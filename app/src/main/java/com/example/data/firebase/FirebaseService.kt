package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.example.data.model.DebriefResult
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class FirebaseService(private val context: Context) {

    private val auth: FirebaseAuth? by lazy {
        try {
            ensureFirebaseInitialized()
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.w("FirebaseService", "Firebase Auth not available: ${e.message}")
            null
        }
    }

    private val firestore: FirebaseFirestore? by lazy {
        try {
            ensureFirebaseInitialized()
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.w("FirebaseService", "Firebase Firestore not available: ${e.message}")
            null
        }
    }

    private fun ensureFirebaseInitialized() {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth?.currentUser
    }

    suspend fun signInAnonymously(): FirebaseUser? {
        return try {
            val result = auth?.signInAnonymously()?.await()
            result?.user
        } catch (e: Exception) {
            Log.e("FirebaseService", "Anonymous sign in failed", e)
            null
        }
    }

    suspend fun syncUserProfileToFirestore(
        uid: String,
        displayName: String,
        email: String,
        level: Int,
        xp: Int,
        title: String,
        casesCompleted: Int
    ): Boolean {
        return try {
            val db = firestore ?: return false
            val data = hashMapOf(
                "uid" to uid,
                "displayName" to displayName,
                "email" to email,
                "level" to level,
                "xp" to xp,
                "clinicalTitle" to title,
                "casesCompleted" to casesCompleted,
                "updatedAt" to System.currentTimeMillis()
            )
            db.collection("users").document(uid)
                .set(data, SetOptions.merge())
                .await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error syncing profile to Firestore", e)
            false
        }
    }

    suspend fun saveCaseResultToFirestore(uid: String, debrief: DebriefResult): Boolean {
        return try {
            val db = firestore ?: return false
            val resultData = hashMapOf(
                "uid" to uid,
                "caseId" to debrief.caseId,
                "caseTitle" to debrief.caseTitle,
                "overallScore" to debrief.overallScore,
                "historyScore" to debrief.historyScore,
                "physicalExamScore" to debrief.physicalExamScore,
                "clinicalReasoningScore" to debrief.clinicalReasoningScore,
                "diagnosticStewardshipScore" to debrief.diagnosticStewardshipScore,
                "diagnosticAccuracyScore" to debrief.diagnosticAccuracyScore,
                "therapeuticScore" to debrief.therapeuticScore,
                "patientSafetyScore" to debrief.patientSafetyScore,
                "timeUsedMinutes" to debrief.timeUsedMinutes,
                "budgetUsedVnd" to debrief.budgetUsedVnd,
                "timestamp" to System.currentTimeMillis()
            )
            db.collection("case_results")
                .add(resultData)
                .await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error saving case result to Firestore", e)
            false
        }
    }

    fun signOut() {
        try {
            auth?.signOut()
        } catch (e: Exception) {
            Log.e("FirebaseService", "Sign out error", e)
        }
    }
}
