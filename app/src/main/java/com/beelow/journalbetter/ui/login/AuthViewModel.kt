package com.beelow.journalbetter.ui.login

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false
)

class AuthViewModel : ViewModel() {

    // 1. Get the Firebase Auth instance
    private val auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore

    // 2. Setup UI State
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        // Check if user is ALREADY logged in when app opens
        if (auth.currentUser != null) {
            _uiState.update { it.copy(isLoggedIn = true) }
        }
    }

    // 3. Function to Sign Up (Create new user)
    fun signUp(email: String, pass: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        if (!email.isBlank() && !pass.isBlank()) {
            auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = task.result.user?.uid
                        if (userId != null) {
                            createUserDocument(userId, email)
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = task.exception?.message ?: "Sign up failed"
                            )
                        }
                    }
                }
        } else {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = "Sign up failed"
                )
            }
        }
    }

    private fun createUserDocument(uid: String, email: String) {
        val userMap = hashMapOf(
            "email" to email,
            "createdAt" to com.google.firebase.Timestamp.now(),
            "uid" to uid
        )

        // This creates the document at /users/{uid}
        db.collection("users").document(uid)
            .set(userMap)
            .addOnSuccessListener {
                _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
            }
            .addOnFailureListener { e ->
                _uiState.update { it.copy(isLoading = false, isLoggedIn = false) }
            }
    }

    // 4. Function to Login (Existing user)
    fun login(email: String, pass: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        if (!email.isBlank() && !pass.isBlank()) {
            auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = task.exception?.message ?: "Login failed"
                            )
                        }
                    }
                }
        } else {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = "Login failed"
                )
            }
        }
    }

    fun logout() {
        auth.signOut()
        _uiState.update { it.copy(isLoggedIn = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

}