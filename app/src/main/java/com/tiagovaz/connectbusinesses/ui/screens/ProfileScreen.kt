package com.tiagovaz.connectbusinesses.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tiagovaz.connectbusinesses.viewmodel.AuthViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Perfil do utilizador")
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { authViewModel.logout() }) {
            Text("Terminar Sessão")
        }
    }
}
