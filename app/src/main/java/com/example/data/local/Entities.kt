package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "case_attempts")
data class CaseAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val caseId: String,
    val caseTitle: String,
    val specialty: String,
    val score: Int,
    val historyScore: Int,
    val examScore: Int,
    val reasoningScore: Int,
    val stewardshipScore: Int,
    val accuracyScore: Int,
    val therapeuticScore: Int,
    val safetyScore: Int,
    val timeUsedMinutes: Int,
    val budgetUsedVnd: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val uid: String = "local_doctor",
    val displayName: String = "BS. Sinh viên Y khoa",
    val email: String = "",
    val level: Int = 1,
    val currentXp: Int = 0,
    val totalScore: Int = 0,
    val casesCompleted: Int = 0,
    val clinicalRank: String = "Bác sĩ Thực tập (Intern)",
    val lastUpdated: Long = System.currentTimeMillis()
)
