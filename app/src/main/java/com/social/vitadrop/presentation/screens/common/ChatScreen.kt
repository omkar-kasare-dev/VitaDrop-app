package com.social.vitadrop.presentation.screens.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Adb
import androidx.compose.material.icons.rounded.Adb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.social.vitadrop.domain.model.ChatMessage
import com.social.vitadrop.presentation.viewmodel.ChatViewModel
import androidx.compose.material.icons.rounded.Send
import androidx.compose.runtime.Composable



// PROFESSIONAL AI CHAT SCREEN


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    viewModel: ChatViewModel = viewModel()
) {

    val messages by
    viewModel.messages.collectAsStateWithLifecycle()

    var inputText by remember {
        mutableStateOf("")
    }

    val listState = rememberLazyListState()

    // Auto Scroll
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        // SPACE BETWEEN ICON AND TITLE
                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            verticalArrangement = Arrangement.Center
                        ) {

                            Text(
                                text = "Hemora AI",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF111827)
                            )

                            Text(
                                text = "Smart Blood Donation Assistant",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }
                },

                navigationIcon = {

                    Surface(
                        modifier = Modifier
                            .padding(start = 14.dp)
                            .size(42.dp),

                        shape = CircleShape,

                        shadowElevation = 6.dp,

                        color = Color(0xFFE0F7FA)
                    ) {

                        Box(
                            contentAlignment = Alignment.Center
                        ) {

                            Icon(
                                imageVector = Icons.Rounded.Adb,

                                contentDescription = null,

                                tint = Color(0xFF29B6F6),

                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },

        bottomBar = {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(
                        horizontal = 14.dp,
                        vertical = 12.dp
                    ),


                verticalAlignment = Alignment.CenterVertically
            ) {

                OutlinedTextField(

                    value = inputText,

                    onValueChange = {
                        inputText = it
                    },

                    modifier = Modifier
                        .weight(1f)
                        .height(58.dp),

                    placeholder = {
                        Text(
                            text = "Ask Hemora AI...",
                            color = Color(0xFF9CA3AF)
                        )
                    },

                    shape = RoundedCornerShape(30.dp),

                    singleLine = true,

                    colors = OutlinedTextFieldDefaults.colors(

                        focusedContainerColor =
                            Color(0xFFF8FAFC),

                        unfocusedContainerColor =
                            Color(0xFFF8FAFC),

                        focusedBorderColor =
                            Color(0xFF29B6F6),

                        unfocusedBorderColor =
                            Color(0xFFE5E7EB),

                        cursorColor =
                            Color(0xFF29B6F6)
                    )
                )

                Spacer(modifier = Modifier.width(12.dp))

                FloatingActionButton(

                    onClick = {

                        if (inputText.isNotBlank()) {

                            viewModel.sendMessage(inputText)

                            inputText = ""
                        }
                    },

                    modifier = Modifier.size(56.dp),

                    shape = CircleShape,

                    containerColor = Color(0xFF29B6F6),

                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 4.dp
                    )
                ) {

                    Icon(
                        imageVector = Icons.Rounded.Send,

                        contentDescription = null,

                        tint = Color.White,

                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color(0xFFF8F9FB)
                )
                .padding(paddingValues)
        ) {

            if (messages.isEmpty()) {

                EmptyChatUI()

            } else {

                LazyColumn(

                    state = listState,

                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp),

                    contentPadding = PaddingValues(
                        vertical = 12.dp
                    )
                ) {

                    items(messages) { message ->

                        AnimatedVisibility(
                            visible = true
                        ) {

                            MessageItem(message)
                        }
                    }
                }
            }
        }
    }
}

// ============================================
// EMPTY SCREEN
// ============================================

@Composable
fun EmptyChatUI() {

    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center
    ) {

        Box(

            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF29B6F6),
                            Color(0xFFEF5350)
                        )
                    )
                ),

            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector =
                    Icons.Outlined.Adb,

                contentDescription = null,

                tint = Color.White,

                modifier = Modifier.size(45.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Welcome to Hemora AI",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text =
                "Ask anything about blood donation, eligibility, health guidance, emergency requests and more.",

            style = MaterialTheme.typography.bodyMedium,

            color = Color.Gray
        )
    }
}

// ============================================
// CHAT BUBBLE
// ============================================

@Composable
fun MessageItem(
    message: ChatMessage
) {

    Row(

        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),

        horizontalArrangement =
            if (message.isUser)
                Arrangement.End
            else
                Arrangement.Start
    ) {

        Card(

            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart =
                    if (message.isUser) 18.dp else 4.dp,

                bottomEnd =
                    if (message.isUser) 4.dp else 18.dp
            ),

            colors = CardDefaults.cardColors(

                containerColor =

                    if (message.isUser)
                        Color(0xFF8FCDE2)
                    else
                        Color.White
            ),

            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            ),

            modifier = Modifier
                .widthIn(max = 320.dp)
        ) {

            Column(
                modifier = Modifier.padding(14.dp)
            ) {

                Text(

                    text =
                        if (message.isUser)
                            "You"
                        else
                            "VitaDrop AI",

                    style =
                        MaterialTheme.typography.labelMedium,

                    fontWeight = FontWeight.Bold,

                    color =
                        if (message.isUser)
                            Color.White
                        else
                            Color(0xFF475C5F)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(

                    text = message.text,

                    style =
                        MaterialTheme.typography.bodyMedium,

                    color =
                        if (message.isUser)
                            Color.White
                        else
                            Color.Black
                )
            }
        }
    }
}