package com.nusachat.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nusachat.app.di.AppContainer
import com.nusachat.app.ui.screen.ConversationListScreen
import com.nusachat.app.ui.theme.AppTheme

class MainActivity : ComponentActivity() {

    private val appContainer = AppContainer(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ConversationListScreen(
                        container = appContainer,
                        onConversationClick = { conversationId, contactName ->
                            // TODO: Navigate to ChatScreen
                        }
                    )
                }
            }
        }
    }
}
