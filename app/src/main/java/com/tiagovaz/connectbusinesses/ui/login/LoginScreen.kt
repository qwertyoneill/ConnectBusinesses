package com.tiagovaz.connectbusinesses.ui.login

import android.util.Log
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.tiagovaz.connectbusinesses.auth.FirebaseGoogleAuth
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
import androidx.compose.ui.focus.FocusManager
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
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onCreateAccount: () -> Unit,   // 👈 ADICIONADO AQUI
    viewModel: AuthViewModel = hiltViewModel()
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) onLoginSuccess()
    }

    val focusManager = LocalFocusManager.current

    Box(modifier = Modifier.fillMaxSize()) {

        // BACKGROUND
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

        // CONTENT
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(160.dp).padding(bottom = 32.dp)
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
                LoginFields(
                    viewModel = viewModel,
                    focusManager = focusManager,
                    onCreateAccount = onCreateAccount // 👈 PASSADO AQUI
                )
            }
        }
    }
}

@Composable
fun LoginFields(
    viewModel: AuthViewModel,
    focusManager: FocusManager,
    onCreateAccount: () -> Unit // 👈 ADICIONADO AQUI
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val loginError by viewModel.loginError.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        OutlinedTextField(
            value = email,
            onValueChange = viewModel::onEmailChange,
            label = { Text("Email", color = Color.White.copy(alpha = 0.9f)) },
            leadingIcon = {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_email),
                    contentDescription = "Email",
                    tint = Color.White.copy(alpha = 0.9f)
                )
            },
            singleLine = true,
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

        OutlinedTextField(
            value = password,
            onValueChange = viewModel::onPasswordChange,
            label = { Text("Password", color = Color.White.copy(alpha = 0.9f)) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(
                            if (passwordVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed
                        ),
                        contentDescription = "Toggle Password",
                        tint = Color.White.copy(alpha = 0.9f)
                    )
                }
            },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                focusManager.clearFocus()
                viewModel.login()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF007FFF)
            )
        ) {
            if (isLoading)
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
            else
                Text("Entrar", color = Color.White, fontWeight = FontWeight.Bold)
        }

        if (loginError.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(loginError, color = Color.Red, fontSize = 14.sp)
        }

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = Color.White.copy(alpha = 0.4f),
                thickness = 1.dp
            )
            Text(
                "  Entrar com  ",
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodySmall
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = Color.White.copy(alpha = 0.4f),
                thickness = 1.dp
            )
        }
        Spacer(Modifier.height(16.dp))
        val context = LocalContext.current
        val googleAuth = remember { FirebaseGoogleAuth(context) }
        val scope = rememberCoroutineScope()
        // Ícones sociais
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                onClick = {
                    scope.launch {
                        googleAuth.signInWithGoogle(
                            onSuccess = { email, firstName, lastName, firebaseToken ->
                                viewModel.loginWithFirebaseIdToken(firebaseToken)
                            },
                            onError = {
                            }
                        )
                    }
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_google),
                    contentDescription = "Google",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(40.dp)
                )
            }

            IconButton(onClick = { /* TODO login Facebook */ }) {
                Icon(
                    painter = painterResource(R.drawable.ic_facebook),
                    contentDescription = "Facebook",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(40.dp)
                )
            }
            IconButton(onClick = { /* TODO login Microsoft */ }) {
                Icon(
                    painter = painterResource(R.drawable.ic_microsoft),
                    contentDescription = "Microsoft",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Ainda não tens conta?",
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.width(8.dp))

            TextButton(onClick = { onCreateAccount() }) { // 👈 AGORA FUNCIONA
                Text(
                    "Criar conta",
                    color = Color(0xFF42A5F5),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
