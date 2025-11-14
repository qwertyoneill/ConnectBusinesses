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
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    var passVisible by remember { mutableStateOf(false) }
    var confirmPassVisible by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    Box(modifier = Modifier.fillMaxSize()) {

        // 🖼️ Mesmo fundo do Login
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

        // 📦 FORMULÁRIO
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

                    // ------------------- CAMPOS -------------------

                    RegisterInputField(
                        label = "Primeiro Nome",
                        value = firstName,
                        onChange = { firstName = it },
                        icon = null
                    )

                    RegisterInputField(
                        label = "Último Nome",
                        value = lastName,
                        onChange = { lastName = it },
                        icon = null
                    )

                    RegisterInputField(
                        label = "Email",
                        value = email,
                        onChange = { email = it },
                        icon = ImageVector.vectorResource(R.drawable.ic_email)
                    )

                    // PASSWORD
                    RegisterInputField(
                        label = "Password",
                        value = password,
                        onChange = { password = it },
                        icon = ImageVector.vectorResource(R.drawable.ic_eye_open),
                        isPassword = true,
                        visible = passVisible,
                        onToggleVisibility = { passVisible = !passVisible }
                    )

                    // CONFIRM PASSWORD
                    RegisterInputField(
                        label = "Confirmar Password",
                        value = confirmPassword,
                        onChange = { confirmPassword = it },
                        icon = ImageVector.vectorResource(R.drawable.ic_eye_open),
                        isPassword = true,
                        visible = confirmPassVisible,
                        onToggleVisibility = { confirmPassVisible = !confirmPassVisible }
                    )

                    // ---------------- VALIDATIONS -----------------

                    if (error.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text(error, color = Color.Red, fontSize = 14.sp)
                    }

                    Spacer(Modifier.height(24.dp))

                    // ---------------- BOTÃO REGISTAR -----------------

                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            error = ""

                            // VALIDAR PASSWORDS
                            if (password.length < 6) {
                                error = "A password deve ter pelo menos 6 caracteres."
                                return@Button
                            }

                            if (password != confirmPassword) {
                                error = "As passwords não coincidem."
                                return@Button
                            }

                            // VALIDAR EMAIL
                            if (!email.contains("@") || !email.contains(".")) {
                                error = "Email inválido."
                                return@Button
                            }

                            loading = true

                            authViewModel.register(
                                firstName,
                                lastName,
                                email,
                                password,
                                onSuccess = {
                                    loading = false
                                    onRegisterSuccess()
                                },
                                onError = {
                                    loading = false
                                    error = it
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF007FFF)
                        )
                    ) {
                        if (loading)
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                        else
                            Text("Registar", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(20.dp))

                    // VOLTAR AO LOGIN
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
