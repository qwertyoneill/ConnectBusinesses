package com.tiagovaz.connectbusinesses.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.tiagovaz.connectbusinesses.R
import com.tiagovaz.connectbusinesses.data.network.ConversationMessageItem
import com.tiagovaz.connectbusinesses.ui.components.LeadContextCard
import com.tiagovaz.connectbusinesses.viewmodel.ChatViewModel
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

private val ChatBg = Color(0xFFF7F2FA)
private val MyBubble = Color(0xFFDCCBFF)
private val OtherBubble = Color(0xFFE8E0EC)
private val BrandBlue = Color(0xFF1671C7)
private val BrandCyan = Color(0xFF1EB7D8)
private val OnlineGreen = Color(0xFF2DBE60)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatScreen(
    navController: NavController,
    conversationId: Int,
    otherUserName: String,
    leadId: Int? = null,
    leadName: String? = null,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var messageText by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val imeBottom = WindowInsets.ime.getBottom(density)

    val displayName = otherUserName
        .replace("+", " ")
        .trim()
        .ifBlank { "Chat" }

    LaunchedEffect(conversationId) {
        viewModel.openChat(conversationId)
        viewModel.startPolling(2000L)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopPolling()
        }
    }

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.lastIndex)
        }
    }

    LaunchedEffect(imeBottom) {
        if (imeBottom > 0 && state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.lastIndex)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ChatBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        ChatHeader(name = displayName)

        LeadContextCard(
            leadName = leadName,
            onOpenLead = if (leadId != null) {
                { navController.navigate("leadDetails/$leadId") }
            } else {
                null
            }
        )

        QuickActionsRow(
            onActionSelected = { quickMessage ->
                messageText = quickMessage
            }
        )

        when {
            state.initialLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.error != null && state.messages.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.error ?: "Erro ao abrir chat",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 8.dp)
                ) {
                    items(state.messages, key = { it.id }) { msg ->
                        MessageBubble(
                            message = msg,
                            isMine = msg.sender_user_id == state.currentUserId
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }

        state.sendError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        ChatComposer(
            messageText = messageText,
            onMessageChange = { messageText = it },
            onSend = {
                val textToSend = messageText.trim()
                if (textToSend.isNotBlank()) {
                    viewModel.sendMessage(textToSend)
                    messageText = ""
                }
            },
            sending = state.sending
        )
    }
}

@Composable
private fun ChatHeader(name: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 10.dp, end = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BrandAvatar()

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(OnlineGreen)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "online",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun BrandAvatar() {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(BrandBlue, BrandCyan)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.size(30.dp),
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.95f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = R.drawable.icon_logo),
                    contentDescription = "Avatar",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun QuickActionsRow(
    onActionSelected: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickActionChip(
            text = "Tenho interesse",
            onClick = { onActionSelected("Tenho interesse") },
            icon = { Text("👍") }
        )
        QuickActionChip(
            text = "Quero mais detalhes",
            onClick = { onActionSelected("Quero mais detalhes") },
            icon = { Icon(Icons.Outlined.Info, contentDescription = null) }
        )
        QuickActionChip(
            text = "Agendar conversa",
            onClick = { onActionSelected("Podemos agendar uma conversa?") },
            icon = { Icon(Icons.Outlined.Schedule, contentDescription = null) }
        )
    }
}

@Composable
private fun QuickActionChip(
    text: String,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = Color.Transparent,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        modifier = Modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant,
            shape = RoundedCornerShape(18.dp)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text)
        }
    }
}

@Composable
private fun ChatComposer(
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSend: () -> Unit,
    sending: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.ime.union(WindowInsets.navigationBars))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        OutlinedTextField(
            value = messageText,
            onValueChange = onMessageChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Escreve uma mensagem...") },
            maxLines = 4,
            shape = RoundedCornerShape(22.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = onSend,
            enabled = !sending && messageText.isNotBlank()
        ) {
            if (sending) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(18.dp)
                        .height(18.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Enviar")
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: ConversationMessageItem,
    isMine: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = if (isMine) 20.dp else 8.dp,
                bottomEnd = if (isMine) 8.dp else 20.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isMine) MyBubble else OtherBubble
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = message.message_text,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formatChatTime(message.created_at),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatChatTime(raw: String?): String {
    if (raw.isNullOrBlank()) return ""

    return try {
        val date = OffsetDateTime.parse(raw)
        date.format(DateTimeFormatter.ofPattern("HH:mm"))
    } catch (_: Exception) {
        raw
    }
}