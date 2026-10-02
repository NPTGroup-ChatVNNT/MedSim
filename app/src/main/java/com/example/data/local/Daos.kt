package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CaseAttemptDao {
    @Query("SELECT * FROM case_attempts ORDER BY timestamp DESC")
    fun getAllAttempts(): Flow<List<CaseAttemptEntity>>

    @Query("SELECT * FROM case_attempts WHERE caseId = :caseId ORDER BY score DESC LIMIT 1")
    fun getBestAttemptForCase(caseId: String): Flow<CaseAttemptEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: CaseAttemptEntity): Long

    @Query("SELECT COUNT(*) FROM case_attempts")
    fun getCompletedCasesCount(): Flow<Int>

    @Query("SELECT AVG(score) FROM case_attempts")
    fun getAverageScore(): Flow<Double?>
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE uid = :uid LIMIT 1")
    fun getUserProfile(uid: String = "local_doctor"): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)
}
