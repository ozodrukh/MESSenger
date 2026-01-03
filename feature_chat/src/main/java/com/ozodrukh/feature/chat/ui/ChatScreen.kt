package com.ozodrukh.feature.chat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.ozodrukh.core.domain.model.ChatId
import com.ozodrukh.feature.chat.models.ChatUiState
import com.ozodrukh.feature.chat.models.Message
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatId: ChatId,
    onBackClick: () -> Unit,
    viewModel: ChatViewModel = koinViewModel { parametersOf(chatId) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(uiState) {
        if (uiState is ChatUiState.Success) {
            val count = (uiState as ChatUiState.Success).data.messages.size
            if (count > 0) {
                listState.animateScrollToItem(0)
            }
        }
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            val state = uiState
            val title = if (state is ChatUiState.Success) state.data.chatName else "Chat ${chatId.value}"
            val subtitle = if (state is ChatUiState.Success) {
                 if (state.data.isOnline) "Online" else formatLastSeen(state.data.lastSeen)
            } else null
            
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                         if (state is ChatUiState.Success) {
                             AsyncImage(
                                 model = state.data.chatAvatarUrl,
                                 contentDescription = null,
                                 contentScale = ContentScale.Crop,
                                 modifier = Modifier
                                     .size(40.dp)
                                     .clip(CircleShape)
                                     .background(MaterialTheme.colorScheme.surfaceVariant)
                             )
                             Spacer(Modifier.width(12.dp))
                         }
                         Column {
                             Text(text = title, style = MaterialTheme.typography.titleMedium)
                             if (subtitle != null) {
                                 Text(text = subtitle, style = MaterialTheme.typography.bodySmall)
                             }
                         }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            ChatInput(onSend = viewModel::sendMessage)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                ChatUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ChatUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ChatUiState.Success -> {
                    LazyColumn(
                        state = listState,
                        reverseLayout = true,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.data.messages.asReversed(), key = { it.id }) { message ->
                            MessageItem(message = message)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageItem(message: Message) {
    val alignment = if (message.isMe) Alignment.CenterEnd else Alignment.CenterStart
    val containerColor = if (message.isMe) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
    val contentColor = if (message.isMe) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
    
    val shape = if (message.isMe) {
        RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(shape)
                .background(containerColor)
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                color = contentColor,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = formatTime(message.timestamp),
                color = contentColor.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.End),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun ChatInput(onSend: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    Column(Modifier.navigationBarsPadding()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                placeholder = { Text("Type a message...") },
                maxLines = 3,
                shape = RoundedCornerShape(24.dp)
            )
            IconButton(
                onClick = {
                    if (text.isNotBlank()) {
                        onSend(text)
                        text = ""
                    }
                },
                enabled = text.isNotBlank()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

private fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

private fun formatLastSeen(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val minutes = diff / (1000 * 60)
    return when {
        minutes < 1 -> "Last seen just now"
        minutes < 60 -> "Last seen $minutes minutes ago"
        minutes < 24 * 60 -> "Last seen ${minutes / 60} hours ago"
        else -> "Last seen recently"
    }
}
