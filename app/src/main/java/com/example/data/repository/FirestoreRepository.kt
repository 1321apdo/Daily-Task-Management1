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

class FirestoreRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    fun observeUserProfile(userId: String): Flow<UserProfile?> {
        return db.collection("users").document(userId)
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
        try {
            val userRef = db.collection("users").document(profile.userId)
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
        try {
            val userRef = db.collection("users").document(userId)
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
