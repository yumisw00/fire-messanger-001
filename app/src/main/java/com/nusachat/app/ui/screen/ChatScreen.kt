package com.nusachat.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.icons.filled
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nusachat.app.domain.model.ChatMessage
import com.nusachat.app.ui.viewmodel.ChatViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ChatScreen(
    conversationId: String,
    contactName: String,
    container: com.nusachat.app.di.AppContainer
) {
    val viewModel: ChatViewModel = container.createChatViewModel()
    viewModel.setConversationId(conversationId)

    val messages = viewModel.messages
    var messageText by remember { mutableStateOf<String>("") }

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(contactName = contactName)
        LazyColumn(
            modifier = Modifier.weight(1f).padding(16.dp)
        ) {
            items(items = messages, key = { it.id }) { message ->
                MessageBubble(message = message, isSentByMe = message.isSentByMe)
            }
        }
        InputBar(
            messageText = messageText,
            onTextChange = { messageText = it },
            onSend = {
                if (messageText.isNotBlank()) {
                    viewModel.sendMessage(senderId = "current-user", ciphertext = messageText)
                    messageText = ""
                }
            }
        )
    }
}

@Composable
private fun TopBar(contactName: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp).background(MaterialTheme.colorScheme.surface)
    ) {
        IconButton(onClick = { }) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
        }
        Column(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Text(text = contactName, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Text(text = "Online", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.width(48.dp))
    }
}

@Composable
private fun MessageBubble(message: ChatMessage, isSentByMe: Boolean) {
    val isSent = isSentByMe
    val bubbleColor = if (isSent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isSent) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    val alignment = if (isSent) Alignment.End else Alignment.Start
    val shape = if (isSent) {
        RoundedCornerShape(18.dp, 0.dp, 18.dp, 18.dp)
    } else {
        RoundedCornerShape(18.dp, 18.dp, 18.dp, 0.dp)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = if (isSent) 48.dp else 8.dp)
            .padding(vertical = 4.dp)
            .align(alignment)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .background(bubbleColor, shape)
                .maxWidth(280.dp)
        ) {
            Text(
                text = message.ciphertext,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                modifier = Modifier.fillMaxWidth().textAlign(if (isSent) TextAlign.End else TextAlign.Start)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatTimestamp(message.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = textColor.copy(alpha = 0.7f),
                modifier = Modifier.align(if (isSent) Alignment.End else Alignment.Start).textAlign(if (isSent) TextAlign.End else TextAlign.Start)
            )
        }
    }
}

@Composable
private fun InputBar(
    messageText: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            androidx.compose.material3.TextField(
                value = messageText,
                onValueChange = onTextChange,
                label = { Text("Ketik pesan...") },
                singleLine = false,
                maxLines = 3,
                modifier = Modifier.weight(1f).padding(8.dp).clip(RoundedCornerShape(24.dp)).background(MaterialTheme.colorScheme.surfaceVariant),
                colors = androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onSend, modifier = Modifier.size(48.dp)) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return if (diff < 60000) "Baru saja"
    else if (diff < 3600000) "${diff / 60000}m"
    else if (diff < 86400000) "${diff / 3600000}jam"
    else java.text.SimpleDateFormat("dd MMM", java.util.Locale.getDefault()).format(timestamp)
}
