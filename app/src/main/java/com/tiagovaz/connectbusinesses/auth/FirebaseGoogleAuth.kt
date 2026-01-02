package com.tiagovaz.connectbusinesses.auth

import android.content.Context
import androidx.credentials.*
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.tiagovaz.connectbusinesses.R

class FirebaseGoogleAuth(
    private val context: Context
) {

    private val credentialManager = CredentialManager.create(context)
    private val firebaseAuth = FirebaseAuth.getInstance()

    suspend fun signInWithGoogle(
        onSuccess: (email: String, firstName: String?, lastName: String?, firebaseIdToken: String) -> Unit,
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
                    val nameParts = user?.displayName?.split(" ", limit = 2)

                    user?.getIdToken(true)
                        ?.addOnSuccessListener { result ->
                            val firebaseIdToken = result.token

                            if (firebaseIdToken != null) {
                                onSuccess(
                                    user.email.orEmpty(),
                                    nameParts?.getOrNull(0),
                                    nameParts?.getOrNull(1),
                                    firebaseIdToken
                                )
                            }
                        }
                        ?.addOnFailureListener(onError)
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
