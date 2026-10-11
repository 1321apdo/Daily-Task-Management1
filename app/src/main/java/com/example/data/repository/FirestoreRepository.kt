package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.model.UserProfile
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreRepository(private val dbProvider: () -> FirebaseFirestore? = {
    try {
        FirebaseFirestore.getInstance()
    } catch (_: Throwable) {
        null
    }
}) {

    private val db: FirebaseFirestore? get() = dbProvider()

    constructor(context: Context) : this({
        try {
            val dbId = context.applicationContext.getString(R.string.firestore_database_id)
            if (dbId.isNotBlank() && dbId != "default") {
                FirebaseFirestore.getInstance(dbId)
            } else {
                FirebaseFirestore.getInstance()
            }
        } catch (_: Throwable) {
            try {
                FirebaseFirestore.getInstance()
            } catch (_: Throwable) {
                null
            }
        }
    })

    fun observeUserProfile(userId: String): Flow<UserProfile?> {
        val currentDb = db ?: return kotlinx.coroutines.flow.flowOf(null)
        return currentDb.collection("users").document(userId)
            .snapshots()
            .map { snapshot ->
                if (snapshot.exists()) {
                    snapshot.toObject(UserProfile::class.java)
                } else {
                    null
                }
            }
    }

    suspend fun createOrUpdateProfile(profile: UserProfile) {
        val currentDb = db ?: return
        try {
            val userRef = currentDb.collection("users").document(profile.userId)
            val data = mapOf(
                "userId" to profile.userId,
                "email" to profile.email,
                "displayName" to profile.displayName,
                "rewardPoints" to profile.rewardPoints,
                "vipUnlockedUntilMs" to profile.vipUnlockedUntilMs,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            userRef.set(data, SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update profile", e)
        }
    }

    suspend fun syncPointsToCloud(userId: String, points: Long, vipUntilMs: Long) {
        val currentDb = db ?: return
        try {
            val userRef = currentDb.collection("users").document(userId)
            val data = mapOf(
                "userId" to userId,
                "rewardPoints" to points,
                "vipUnlockedUntilMs" to vipUntilMs,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            userRef.set(data, SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync points to cloud", e)
        }
    }

    companion object {
        private const val TAG = "FirestoreRepository"
    }
}
