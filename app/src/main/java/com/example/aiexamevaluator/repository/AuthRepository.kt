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
            val user = authResult.user
                ?: return Result.failure(Exception("Unable to create account. Please try again."))

            val userModel = UserModel(
                uid = user.uid,
                fullName = fullName.trim(),
                email = email.trim(),
            )

            firestore
                .collection("users")
                .document(user.uid)
                .set(userModel)
                .await()

            // Send a verification link to the email they typed. Firebase only checks
            // that the email is well-formed at signup time, not that it's real — this
            // verification step is what actually confirms the person owns the inbox.
            user.sendEmailVerification().await()

            // Sign out immediately so an unverified (possibly fake) email doesn't end
            // up sitting in a logged-in session.
            auth.signOut()

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
            val authResult = auth.signInWithEmailAndPassword(email.trim(), password).await()
            val user = authResult.user
                ?: return Result.failure(Exception("Login failed. Please try again."))

            if (!user.isEmailVerified) {
                // Don't leave an unverified user in a signed-in session.
                auth.signOut()
                return Result.failure(
                    Exception("Please verify your email before logging in. Check your inbox for the verification link.")
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Login failed. Please try again."))
        }
    }

    /**
     * Re-sends the verification email for an account that signed up but never
     * verified. Needs the password because the user isn't currently signed in
     * (signUp/login both sign unverified users back out).
     */
    suspend fun resendVerificationEmail(
        email: String,
        password: String,
    ): Result<Unit> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email.trim(), password).await()
            val user = authResult.user
                ?: return Result.failure(Exception("Unable to resend verification email."))

            if (user.isEmailVerified) {
                auth.signOut()
                return Result.failure(Exception("This email is already verified. You can log in now."))
            }

            user.sendEmailVerification().await()
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Failed to resend verification email."))
        }
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email.trim()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Failed to send reset email. Please try again."))
        }
    }

    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    fun logout() {
        auth.signOut()
    }
}