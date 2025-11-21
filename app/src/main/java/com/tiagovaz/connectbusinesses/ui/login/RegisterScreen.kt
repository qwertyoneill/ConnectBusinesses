package com.tiagovaz.connectbusinesses.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tiagovaz.connectbusinesses.R
import com.tiagovaz.connectbusinesses.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passVisible by remember { mutableStateOf(false) }
    var confirmPassVisible by remember { mutableStateOf(false) }

    // NOVO: observar estado do ViewModel
    val isLoading by authViewModel.isLoading.collectAsState()

    var errorMessage by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {

        // Fundo igual ao Login
        Image(
            painter = painterResource(id = R.drawable.login_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color(0xCC001F3F))
                .blur(25.dp)
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.45f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                "Criar Conta",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .border(
                        1.dp,
                        Color.White.copy(alpha = 0.25f),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(24.dp)
            ) {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    RegisterInputField("Primeiro Nome", firstName, { firstName = it }, null)
                    RegisterInputField("Último Nome", lastName, { lastName = it }, null)
                    RegisterInputField("Email", email, { email = it }, ImageVector.vectorResource(R.drawable.ic_email))

                    RegisterInputField(
                        label = "Password",
                        value = password,
                        onChange = { password = it },
                        icon = ImageVector.vectorResource(R.drawable.ic_eye_open),
                        isPassword = true,
                        visible = passVisible,
                        onToggleVisibility = { passVisible = !passVisible }
                    )

                    RegisterInputField(
                        label = "Confirmar Password",
                        value = confirmPassword,
                        onChange = { confirmPassword = it },
                        icon = ImageVector.vectorResource(R.drawable.ic_eye_open),
                        isPassword = true,
                        visible = confirmPassVisible,
                        onToggleVisibility = { confirmPassVisible = !confirmPassVisible }
                    )

                    if (errorMessage.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text(errorMessage, color = Color.Red, fontSize = 14.sp)
                    }

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            errorMessage = ""

                            if (password != confirmPassword) {
                                errorMessage = "As passwords não coincidem."
                                return@Button
                            }

                            authViewModel.register(
                                firstName,
                                lastName,
                                email,
                                password,
                                onSuccess = {
                                    onRegisterSuccess()
                                },
                                onError = {
                                    errorMessage = it
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF007FFF)
                        )
                    ) {
                        if (isLoading)
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                        else
                            Text("Registar", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(20.dp))

                    TextButton(onClick = { onBackToLogin() }) {
                        Text(
                            "Voltar ao Login",
                            color = Color(0xFF42A5F5),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
@Composable
private fun RegisterInputField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    icon: ImageVector?,
    isPassword: Boolean = false,
    visible: Boolean = false,
    onToggleVisibility: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label, color = Color.White.copy(alpha = 0.9f)) },
        leadingIcon = {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = Color.White)
            }
        },
        trailingIcon = {
            if (isPassword && onToggleVisibility != null) {
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        imageVector = ImageVector.vectorResource(
                            if (visible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed
                        ),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        },
        singleLine = true,
        visualTransformation = if (isPassword && !visible) PasswordVisualTransformation() else VisualTransformation.None,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(Modifier.height(16.dp))
}
