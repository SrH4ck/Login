// En AuthResult.kt
package com.ams.mylogin.model

sealed class AuthResult {
    object LoadingGoogle: AuthResult()
    object Idle : AuthResult()
    object Loading : AuthResult()
    data class Success(val user: User) : AuthResult()
    data class Error(val message: String) : AuthResult()
}
