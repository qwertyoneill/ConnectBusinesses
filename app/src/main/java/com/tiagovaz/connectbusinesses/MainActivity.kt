package com.tiagovaz.connectbusinesses

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.tiagovaz.connectbusinesses.ui.ConnectBusinessesRoot
import com.tiagovaz.connectbusinesses.ui.theme.ConnectBusinessesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // O conteúdo respeita as barras do sistema
        WindowCompat.setDecorFitsSystemWindows(window, true)

        // Ícones escuros na barra de estado e navegação
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }

        setContent {
            ConnectBusinessesTheme {
                ConnectBusinessesRoot()
            }
        }
    }
}