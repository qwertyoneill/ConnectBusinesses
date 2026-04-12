package com.tiagovaz.connectbusinesses.data.network.auth

import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import java.io.IOException
import java.net.SocketTimeoutException

object AuthErrorMapper {

    fun toUserMessage(error: Throwable?): String {
        if (error == null) return "Ocorreu um erro inesperado."

        return when (error) {
            is NoCredentialException ->
                "Nenhuma conta Google disponível neste dispositivo."

            is GetCredentialCancellationException ->
                "Login cancelado."

            is SocketTimeoutException ->
                "Sem ligação à internet."

            is IOException ->
                "Sem ligação à internet."

            is IllegalStateException ->
                mapIllegalState(error)

            else -> {
                val message = error.message.orEmpty()

                when {
                    message.contains("canceled", ignoreCase = true) ->
                        "Login cancelado."

                    message.contains("cancelled", ignoreCase = true) ->
                        "Login cancelado."

                    message.contains("network", ignoreCase = true) ->
                        "Sem ligação à internet."

                    message.contains("timeout", ignoreCase = true) ->
                        "Sem ligação à internet."

                    message.contains("unable to resolve host", ignoreCase = true) ->
                        "Sem ligação à internet."

                    message.contains("credential", ignoreCase = true) ->
                        "Falha na autenticação."

                    else ->
                        "Não foi possível iniciar sessão. Tenta novamente."
                }
            }
        }
    }

    fun fromServerMessage(message: String?): String {
        val safeMessage = message.orEmpty()

        return when {
            safeMessage.contains("invalid credentials", ignoreCase = true) ->
                "Email ou palavra-passe incorretos."

            safeMessage.contains("invalid user credentials", ignoreCase = true) ->
                "Não foi possível iniciar sessão."

            safeMessage.contains("timeout", ignoreCase = true) ->
                "Sem ligação à internet."

            safeMessage.contains("unable to resolve host", ignoreCase = true) ->
                "Sem ligação à internet."

            safeMessage.contains("401") ->
                "Sessão inválida. Tenta novamente."

            safeMessage.contains("403") ->
                "Não tens permissão para entrar."

            safeMessage.contains("500") ->
                "Erro no servidor. Tenta novamente mais tarde."

            else ->
                "Não foi possível iniciar sessão. Tenta novamente."
        }
    }

    private fun mapIllegalState(error: IllegalStateException): String {
        val message = error.message.orEmpty()

        return when {
            message.contains("credencial", ignoreCase = true) ->
                "Falha na autenticação."

            message.contains("credential", ignoreCase = true) ->
                "Falha na autenticação."

            message.contains("firebase token", ignoreCase = true) ->
                "Falha na autenticação."

            message.contains("firebase user", ignoreCase = true) ->
                "Falha na autenticação."

            else ->
                "Não foi possível iniciar sessão. Tenta novamente."
        }
    }
}