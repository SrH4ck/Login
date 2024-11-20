package com.ams.mylogin.screens.login

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.ams.mylogin.model.AuthResult
import com.ams.mylogin.model.User
import com.ams.mylogin.navigation.NavRoutes
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _authResult = MutableStateFlow<AuthResult>(AuthResult.Idle)


    val authResult: StateFlow<AuthResult> get() = _authResult

    init {
        checkCurrentUser()
    }
    private fun checkCurrentUser() {
        viewModelScope.launch {
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                try {
                    // Obtener la información del usuario desde Firestore
                    val db = FirebaseFirestore.getInstance()
                    val document = db.collection("users").document(currentUser.uid).get().await()

                    if (document != null && document.exists()) {
                        val user = User(
                            id = currentUser.uid,
                            userId = currentUser.uid, // Ajusta según tu lógica de ID
                            displayName = document.getString("display_name"),
                            avatarUrl = document.getString("avatar"),
                            quote = document.getString("quote"),
                            profession = document.getString("profession")
                        )
                        _authResult.value = AuthResult.Success(user)
                    } else {
                        _authResult.value = AuthResult.Error("")
                    }
                } catch (e: Exception) {
                    _authResult.value = AuthResult.Error("Error al obtener datos de Firestore: ${e.message}")
                }
            } else {
                _authResult.value = AuthResult.Idle
            }
        }
    }

    // Método de inicio de sesión con Google
    fun signInWithGoogle(idToken: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    viewModelScope.launch {
                        try {
                            val firebaseUser = firebaseAuth.currentUser
                            if (firebaseUser != null) {
                                val user = User(
                                    id = firebaseUser.uid,
                                    userId = firebaseUser.uid,
                                    displayName = firebaseUser.displayName,
                                    avatarUrl = firebaseUser.photoUrl?.toString(),
                                    quote = null,
                                    profession = null
                                )
                                _authResult.value = AuthResult.Success(user)
                                onSuccess()
                            } else {
                                _authResult.value = AuthResult.Error("No se pudo obtener la información del usuario.")
                            }
                        } catch (e: Exception) {
                            _authResult.value = AuthResult.Error("Error al obtener datos de usuario: ${e.message}")
                        }
                    }
                } else {
                    task.exception?.let { onFailure(it) }
                    _authResult.value = AuthResult.Error("Error en inicio de sesión con Google.")
                }
            }
    }


    // Método de inicio de sesión con email y contraseña
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authResult.value = AuthResult.Loading

            try {
                val authResultTask = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                val firebaseUser = authResultTask.user
                val uid = firebaseUser?.uid

                if (uid != null) {
                    // Obtener la información del usuario desde Firestore
                    val db = FirebaseFirestore.getInstance()
                    val document = db.collection("users").document(uid).get().await()

                    if (document != null && document.exists()) {
                        val user = User(
                            id = uid,
                            userId = uid,
                            displayName = document.getString("display_name"),
                            avatarUrl = document.getString("avatar"),
                            quote = document.getString("quote"),
                            profession = document.getString("profession")
                        )
                        _authResult.value = AuthResult.Success(user)
                    } else {
                        _authResult.value = AuthResult.Error("No se encontró la información del usuario")
                    }
                } else {
                    _authResult.value = AuthResult.Error("Error: UID del usuario no encontrado")
                }
            } catch (e: Exception) {
                _authResult.value = AuthResult.Error(e.message ?: "Email o contraseña incorrectos")
            }
        }
    }


    // Método de registro de usuario
    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _authResult.value = AuthResult.Loading

            try {
                // Crear el usuario con email y contraseña
                val authResultTask = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = authResultTask.user

                if (firebaseUser != null) {
                    // Obtener el siguiente ID secuencial
                    val newId = getNextSequentialId()

                    // Actualizar el perfil del usuario
                    val profileUpdates = userProfileChangeRequest {
                        displayName = name
                    }
                    firebaseUser.updateProfile(profileUpdates).await()

                    // Crear el objeto User con el nuevo ID secuencial
                    val user = User(
                        id = firebaseUser.uid,
                        userId = newId.toString(), // Asignar el ID secuencial como `userId`
                        displayName = name,
                        avatarUrl = null,
                        quote = null,
                        profession = null
                    )

                    // Guardar la información del usuario en Firestore
                    val db = FirebaseFirestore.getInstance()
                    db.collection("users").document(firebaseUser.uid).set(user.toMap()).await()

                    _authResult.value = AuthResult.Success(user)
                } else {
                    _authResult.value = AuthResult.Error("Error: UID del usuario no encontrado")
                }
            } catch (e: Exception) {
                _authResult.value = AuthResult.Error(e.message ?: "Error al registrar")
            }
        }
    }


    // Método para obtener el próximo ID secuencial
    private suspend fun getNextSequentialId(): Int {
        val db = FirebaseFirestore.getInstance()
        val counterRef = db.collection("counters").document("user_counter")

        return try {
            val snapshot = db.runTransaction { transaction ->
                val doc = transaction.get(counterRef)
                val lastId = doc.getLong("last_id") ?: 0
                val newId = lastId + 1
                transaction.update(counterRef, "last_id", newId)
                newId.toInt()
            }.await()
            snapshot
        } catch (e: Exception) {
            _authResult.value = AuthResult.Error("Error al obtener ID secuencial: ${e.message}")
            0
        }
    }

    // Método para cerrar sesión
    fun logout() {
        firebaseAuth.signOut()
        _authResult.value = AuthResult.Idle
    }

    // Método para resetear el estado de autenticación
    fun resetAuthResult() {
        _authResult.value = AuthResult.Idle
    }

    // Método para establecer manualmente el estado de autenticación (usado en Google Sign-In)
    fun setAuthResult(result: AuthResult) {
        _authResult.value = result
    }
}
