package com.example.aiexamevaluator.repository

import com.example.aiexamevaluator.data.model.UserModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun getCurrentUser(): Result<UserModel> {
        return try {

            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("User not logged in"))

            val snapshot = firestore
                .collection("users")
                .document(uid)
                .get()
                .await()

            val user = snapshot.toObject(UserModel::class.java)

            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("User data not found"))
            }

        } catch (e: Exception) {
            Result.failure(
                Exception(
                    e.message ?: "Failed to load user data"
                )
            )
        }
    }

    suspend fun updateFullName(newName: String): Result<Unit> {
        return try {

            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("User not found"))

            firestore
                .collection("users")
                .document(uid)
                .update("fullName", newName.trim())
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Result<Unit> {
        return try {
            val user = auth.currentUser
                ?: return Result.failure(Exception("User not found"))

            val credential = EmailAuthProvider.getCredential(
                user.email ?: "",
                currentPassword
            )

            user.reauthenticate(credential).await()
            user.updatePassword(newPassword).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Failed to change password"))
        }
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }

    suspend fun deleteAccount(): Result<Unit> {
        return try {
            val user = FirebaseAuth.getInstance().currentUser
                ?: return Result.failure(Exception("User not found"))

            val uid = user.uid

            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .delete()
                .await()

            user.delete().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}