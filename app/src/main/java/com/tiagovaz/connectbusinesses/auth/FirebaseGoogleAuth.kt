package com.tiagovaz.connectbusinesses.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.tiagovaz.connectbusinesses.R

class FirebaseGoogleAuth(
    private val context: Context
) {

    private val credentialManager = CredentialManager.create(context)
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun signInWithGoogle(
        onSuccess: (
            email: String,
            firstName: String?,
            lastName: String?,
            firebaseIdToken: String
        ) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        try {
            val request = GetCredentialRequest(
                listOf(
                    GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(
                            context.getString(R.string.default_web_client_id)
                        )
                        .build()
                )
            )

            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            val googleIdToken = GoogleIdTokenCredential
                .createFrom(result.credential.data)
                .idToken

            val firebaseCredential =
                GoogleAuthProvider.getCredential(googleIdToken, null)

            firebaseAuth.signInWithCredential(firebaseCredential)
                .addOnSuccessListener {

                    val user = firebaseAuth.currentUser
                    if (user == null) {
                        onError(IllegalStateException("Firebase user is null"))
                        return@addOnSuccessListener
                    }

                    val nameParts = user.displayName
                        ?.trim()
                        ?.split(" ", limit = 2)

                    user.getIdToken(true)
                        .addOnSuccessListener { tokenResult ->
                            val firebaseIdToken = tokenResult.token

                            if (firebaseIdToken.isNullOrBlank()) {
                                onError(
                                    IllegalStateException("Firebase ID token is null")
                                )
                                return@addOnSuccessListener
                            }
                            Log.d("FIREBASE_ID_TOKEN", firebaseIdToken)

                            onSuccess(
                                user.email.orEmpty(),
                                nameParts?.getOrNull(0),
                                nameParts?.getOrNull(1),
                                firebaseIdToken
                            )
                        }
                        .addOnFailureListener(onError)
                }
                .addOnFailureListener(onError)

        } catch (e: GetCredentialException) {
            onError(e)
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }
}
