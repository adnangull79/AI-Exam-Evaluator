package com.example.aiexamevaluator.repository

import com.example.aiexamevaluator.data.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {

    suspend fun signUp(
        fullName: String,
        email: String,
        password: String,
    ): Result<Unit> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email.trim(), password).await()
            val uid = authResult.user?.uid
                ?: return Result.failure(Exception("Unable to create account. Please try again."))

            val userModel = UserModel(
                uid = uid,
                fullName = fullName.trim(),
                email = email.trim(),
            )

            firestore
                .collection("users")
                .document(uid)
                .set(userModel)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Sign up failed. Please try again."))
        }
    }

    suspend fun login(
        email: String,
        password: String,
    ): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email.trim(), password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Login failed. Please try again."))
        }
    }

    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    fun logout() {
        auth.signOut()
    }
}
