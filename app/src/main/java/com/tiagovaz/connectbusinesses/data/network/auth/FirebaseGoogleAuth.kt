package com.tiagovaz.connectbusinesses.data.network.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.tiagovaz.connectbusinesses.R

class FirebaseGoogleAuth(
    private val context: Context
) {
    private val credentialManager = CredentialManager.Companion.create(context)
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val serverClientId: String
        get() = context.getString(R.string.default_web_client_id)

    suspend fun signInWithGoogleButton(
        onSuccess: (String, String?, String?, String) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        try {
            val request = GetCredentialRequest(
                listOf(
                    GetSignInWithGoogleOption.Builder(serverClientId)
                        .build()
                )
            )

            val result = credentialManager.getCredential(
                context = context,
                request = request
            )

            handleGoogleResult(result, onSuccess, onError)
        } catch (e: GetCredentialException) {
            onError(e)
        } catch (e: Exception) {
            onError(e)
        }
    }

    suspend fun rySilentGoogleSignIn(
        onSuccess: (String, String?, String?, String) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        try {
            val request = GetCredentialRequest(
                listOf(
                    GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(true)
                        .setAutoSelectEnabled(true)
                        .setServerClientId(serverClientId)
                        .build()
                )
            )

            val result = credentialManager.getCredential(
                context = context,
                request = request
            )

            handleGoogleResult(result, onSuccess, onError)
        } catch (_: NoCredentialException) {
            // normal: não há login silencioso disponível
        } catch (e: GetCredentialException) {
            onError(e)
        } catch (e: Exception) {
            onError(e)
        }
    }

    private fun handleGoogleResult(
        result: GetCredentialResponse,
        onSuccess: (String, String?, String?, String) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val credential = result.credential

        if (
            credential !is CustomCredential ||
            credential.type != GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            onError(IllegalStateException("Credencial Google inválida"))
            return
        }

        val googleCredential = GoogleIdTokenCredential.Companion.createFrom(credential.data)
        val idToken = googleCredential.idToken
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)

        firebaseAuth.signInWithCredential(firebaseCredential)
            .addOnSuccessListener {
                val user = firebaseAuth.currentUser
                if (user == null) {
                    onError(IllegalStateException("Utilizador Firebase nulo"))
                    return@addOnSuccessListener
                }

                user.getIdToken(true)
                    .addOnSuccessListener { tokenResult ->
                        val firebaseToken = tokenResult.token
                        if (firebaseToken.isNullOrBlank()) {
                            onError(IllegalStateException("Firebase token nulo"))
                            return@addOnSuccessListener
                        }

                        val parts = user.displayName?.trim()?.split(" ", limit = 2)

                        onSuccess(
                            user.email.orEmpty(),
                            parts?.getOrNull(0),
                            parts?.getOrNull(1),
                            firebaseToken
                        )
                    }
                    .addOnFailureListener(onError)
            }
            .addOnFailureListener(onError)
    }

    fun signOut() {
        firebaseAuth.signOut()
    }
}