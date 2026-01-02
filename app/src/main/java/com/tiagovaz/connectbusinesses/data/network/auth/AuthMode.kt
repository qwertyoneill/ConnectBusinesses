package com.tiagovaz.connectbusinesses.data.network.auth

enum class AuthMode { PASSWORD, FIREBASE }

object AuthConfig {
    // mete false para “desligar” a auth Google/Firebase sem remover código
    const val ENABLE_FIREBASE_LOGIN = true
}
