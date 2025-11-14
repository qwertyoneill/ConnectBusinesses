package com.tiagovaz.connectbusinesses

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.tiagovaz.connectbusinesses.ui.ConnectBusinessesRoot
import com.tiagovaz.connectbusinesses.ui.theme.ConnectBusinessesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)// 👈 essencial para o teclado empurrar o layout
        setContent {
            ConnectBusinessesRoot()
        }
    }
}

