package com.social.vitadrop.presentation.screens.common
/*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.social.vitadrop.domain.model.ChatMessage
import com.social.vitadrop.presentation.viewmodel.ChatViewModel

// Color Palette based on Prana & VitaDrop Design System
private val PranaBgColor = Color(0xFFFAF7F2)       // Warm Off-white / Cream
private val PranaGreenDark = Color(0xFF1B4D3E)    // Deep Forest Green
private val PranaGreenLight = Color(0xFFE8F3EE)   // Soft Tint Green
private val PranaUserBubble = Color(0xFFE2EFE9)   // User Chat Tint
private val PranaGold = Color(0xFFC5A059)         // Gold Accent
private val PranaRed = Color(0xFFD32F2F)          // Blood Accent
private val PranaTextDark = Color(0xFF2C3E35)     // Deep Slate Text
private val PranaTextMuted = Color(0xFF7A8B83)    // Muted Subtitle Text

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    viewModel: ChatViewModel = viewModel()
) {
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Fixed Auto Scroll Bug
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        containerColor = PranaBgColor,
        /*
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = "VitaDrop Logo",
                            tint = PranaRed,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "VitaDrop",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PranaRed
                            )
                            Text(
                                text = "Blood & Organ Emergency Network",
                                fontSize = 9.sp,
                                color = PranaTextMuted,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Box {
                            Icon(
                                imageVector = Icons.Rounded.NotificationsNone,
                                contentDescription = "Notifications",
                                tint = PranaTextDark,
                                modifier = Modifier.size(22.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(PranaRed)
                                    .align(Alignment.TopEnd)
                            )
                        }
                    }
                    IconButton(onClick = {}) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEFE8DD)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PersonOutline,
                                contentDescription = "Profile",
                                tint = PranaTextDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PranaBgColor
                )
            )
        },

         */
        bottomBar = {
            Surface(
                color = PranaBgColor,
                tonalElevation = 3.dp,
                shadowElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // "+" Attachment Button
                    Surface(
                        onClick = {},
                        shape = CircleShape,
                        color = Color(0xFFF0EBE1),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add attachment",
                                tint = PranaTextDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Input Text Field
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        placeholder = {
                            Text(
                                text = "Type your message...",
                                fontSize = 13.sp,
                                color = PranaTextMuted
                            )
                        },
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = PranaGreenDark,
                            unfocusedBorderColor = Color(0xFFE8E2D5),
                            cursorColor = PranaGreenDark
                        ),
                        //contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Send Button
                    FloatingActionButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                viewModel.sendMessage(inputText)
                                inputText = ""
                            }
                        },
                        modifier = Modifier.size(42.dp),
                        shape = CircleShape,
                        containerColor = PranaGreenDark,
                        elevation = FloatingActionButtonDefaults.elevation(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // HERO HEADER SECTION
            item {
                PranaHeroBanner()
            }

            // ASSISTANT GREETING BUBBLE
            item {
                PranaAssistantGreeting()
            }

            // QUICK ACTION CHIPS
            item {
                QuickActionGrid(onOptionClick = { option ->
                    viewModel.sendMessage(option)
                })
            }

            // CHAT MESSAGES
            items(messages) { message ->
                AnimatedVisibility(visible = true) {
                    MessageItem(message)
                }
            }
        }
    }
}

@Composable
private fun PranaHeroBanner() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = PranaGreenLight,
            modifier = Modifier.size(68.dp),
            border = BorderStroke(1.dp, PranaGold.copy(alpha = 0.5f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = "Prana Logo",
                    tint = PranaGreenDark,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Prana",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = PranaGreenDark
        )

        Text(
            text = "Your Personal Health Assistant",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = PranaTextMuted
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "—  प्राणः सर्वेषां जीवनम्  —",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = PranaGold
        )

        Text(
            text = "Prana is the life force in all beings",
            fontSize = 10.sp,
            color = PranaTextMuted
        )
    }
}

@Composable
private fun PranaAssistantGreeting() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = CircleShape,
            color = PranaGreenDark,
            modifier = Modifier.size(34.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = null,
                    tint = PranaGold,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Card(
            shape = RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE8E2D5)),
            elevation = CardDefaults.cardElevation(0.dp),
            modifier = Modifier.widthIn(max = 310.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Namaste! 🙏",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PranaTextDark
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "I'm Prana, your personal health assistant. I'm here to help you with health guidance, wellness tips, and blood donation support.",
                    fontSize = 12.sp,
                    color = PranaTextDark,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "How can I support you today?",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = PranaGreenDark
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "9:41 AM",
                    fontSize = 9.sp,
                    color = PranaTextMuted,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

private data class QuickActionItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconTint: Color
)

@Composable
private fun QuickActionGrid(onOptionClick: (String) -> Unit) {
    val options = listOf(
        QuickActionItem("Find a blood donor", "Request or donate blood", Icons.Default.WaterDrop, PranaRed),
        QuickActionItem("Health advice", "Symptoms, nutrition, wellness", Icons.Default.FavoriteBorder, Color(0xFF2E7D32)),
        QuickActionItem("Ayurvedic tips", "Natural & traditional care", Icons.Default.Spa, Color(0xFF15803D)),
        QuickActionItem("Ask Prana", "General health questions", Icons.Default.ChatBubbleOutline, PranaGold)
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickActionCard(options[0], modifier = Modifier.weight(1f), onClick = { onOptionClick(options[0].title) })
            QuickActionCard(options[1], modifier = Modifier.weight(1f), onClick = { onOptionClick(options[1].title) })
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickActionCard(options[2], modifier = Modifier.weight(1f), onClick = { onOptionClick(options[2].title) })
            QuickActionCard(options[3], modifier = Modifier.weight(1f), onClick = { onOptionClick(options[3].title) })
        }
    }
}

@Composable
private fun QuickActionCard(
    item: QuickActionItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE8E2D5)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = item.iconTint.copy(alpha = 0.1f),
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = item.iconTint,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = item.title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PranaTextDark
                )
                Text(
                    text = item.subtitle,
                    fontSize = 9.sp,
                    color = PranaTextMuted,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun MessageItem(message: ChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isUser) {
            /*
            Surface(
                shape = CircleShape,
                color = PranaGreenDark,
                modifier = Modifier.size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = null,
                        tint = PranaGold,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

             */
            //Spacer(modifier = Modifier.width(8.dp))
        }

        Card(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isUser) 16.dp else 2.dp,
                bottomEnd = if (message.isUser) 2.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isUser) PranaUserBubble else Color.White
            ),
            border = BorderStroke(
                1.dp,
                if (message.isUser) Color(0xFFCDE2D8) else Color(0xFFE8E2D5)
            ),
            elevation = CardDefaults.cardElevation(0.dp),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.text,
                    fontSize = 12.sp,
                    color = PranaTextDark,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "9:42 AM",
                        fontSize = 9.sp,
                        color = PranaTextMuted
                    )
                    if (message.isUser) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Delivered",
                            tint = PranaGreenDark,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}



 */


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.social.vitadrop.domain.model.ChatMessage
import com.social.vitadrop.presentation.viewmodel.ChatViewModel

// Color Palette based on Prana & VitaDrop Design System
private val PranaBgColor = Color(0xFFFBFBFC)       // Ultra Light Clean Canvas
private val PranaGreenDark = Color(0xFF004D40)    // Premium Clinical Deep Teal Green
private val PranaGreenLight = Color(0xFFE8F5E9)   // Fresh Tint Green
private val PranaUserBubble = Color(0xFF004D40)   // Deep Green Accent for User Message
private val PranaGold = Color(0xFFD4AF37)         // Refined Muted Gold
private val PranaRed = Color(0xFFE53935)          // Blood Life Accent
private val PranaTextDark = Color(0xFF1E293B)     // High-Contrast Slate
private val PranaTextMuted = Color(0xFF64748B)    // Clean Subtitle Grey
private val PranaBorderColor = Color(0xFFE2E8F0)   // Subtle Divider Border

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    viewModel: ChatViewModel = viewModel()
) {
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Fixed Auto Scroll Bug
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        containerColor = PranaBgColor,
        /*
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = "VitaDrop Logo",
                            tint = PranaRed,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "VitaDrop",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PranaRed
                            )
                            Text(
                                text = "Blood & Organ Emergency Network",
                                fontSize = 9.sp,
                                color = PranaTextMuted,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Box {
                            Icon(
                                imageVector = Icons.Rounded.NotificationsNone,
                                contentDescription = "Notifications",
                                tint = PranaTextDark,
                                modifier = Modifier.size(22.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(PranaRed)
                                    .align(Alignment.TopEnd)
                            )
                        }
                    }
                    IconButton(onClick = {}) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEFE8DD)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PersonOutline,
                                contentDescription = "Profile",
                                tint = PranaTextDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PranaBgColor
                )
            )
        },
        */
        bottomBar = {
            Surface(
                color = Color.White,
                tonalElevation = 0.dp,
                shadowElevation = 8.dp,
                border = BorderStroke(0.8.dp, PranaBorderColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // "+" Attachment Button
                    Surface(
                        onClick = {},
                        shape = CircleShape,
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add attachment",
                                tint = PranaTextDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Input Text Field - Fixed vertical clipping bug
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp),
                        placeholder = {
                            Text(
                                text = "Type your message...",
                                fontSize = 13.sp,
                                color = PranaTextMuted
                            )
                        },
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (inputText.isNotBlank()) {
                                    viewModel.sendMessage(inputText)
                                    inputText = ""
                                }
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF8FAFC),
                            unfocusedContainerColor = Color(0xFFF8FAFC),
                            focusedBorderColor = PranaGreenDark,
                            unfocusedBorderColor = PranaBorderColor,
                            cursorColor = PranaGreenDark
                        ),
                        //contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // Send Button
                    FloatingActionButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                viewModel.sendMessage(inputText)
                                inputText = ""
                            }
                        },
                        modifier = Modifier.size(42.dp),
                        shape = CircleShape,
                        containerColor = PranaGreenDark,
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // HERO HEADER SECTION
            item {
                PranaHeroBanner()
            }

            // ASSISTANT GREETING BUBBLE
            item {
                PranaAssistantGreeting()
            }

            // QUICK ACTION CHIPS
            item {
                QuickActionGrid(onOptionClick = { option ->
                    viewModel.sendMessage(option)
                })
            }

            // CHAT MESSAGES
            items(messages) { message ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { 20 })
                ) {
                    MessageItem(message)
                }
            }
        }
    }
}

@Composable
private fun PranaHeroBanner() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Glowing Icon Ring Background
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(72.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(PranaGreenLight, Color.Transparent)
                    ),
                    shape = CircleShape
                )
        ) {
            Surface(
                shape = CircleShape,
                color = Color.White,
                modifier = Modifier.size(60.dp),
                border = BorderStroke(1.dp, PranaGreenDark.copy(alpha = 0.15f)),
                shadowElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = "Prana Logo",
                        tint = PranaGreenDark,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Prana",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = PranaGreenDark,
            letterSpacing = 0.5.sp
        )

        Text(
            text = "Your Personal Health Assistant",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = PranaTextMuted
        )

        Spacer(modifier = Modifier.height(6.dp))

        Surface(
            color = Color(0xFFFFFBEB),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(0.8.dp, PranaGold.copy(alpha = 0.3f)),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "—  प्राणः सर्वेषां जीवनम्  —",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB45309)
                )

                Text(
                    text = "Prana is the life force in all beings",
                    fontSize = 10.sp,
                    color = PranaTextMuted
                )
            }
        }
    }
}

@Composable
private fun PranaAssistantGreeting() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = CircleShape,
            color = PranaGreenDark,
            modifier = Modifier.size(36.dp),
            shadowElevation = 2.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = null,
                    tint = PranaGold,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Card(
            shape = RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 18.dp,
                bottomStart = 18.dp,
                bottomEnd = 18.dp
            ),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(0.8.dp, PranaBorderColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.widthIn(max = 310.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Namaste! 🙏",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PranaTextDark
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "I'm Prana, your personal health assistant. I'm here to help you with health guidance, wellness tips, and blood donation support.",
                    fontSize = 12.sp,
                    color = PranaTextDark,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "How can I support you today?",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PranaGreenDark
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "9:41 AM",
                    fontSize = 9.sp,
                    color = PranaTextMuted,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

private data class QuickActionItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconTint: Color
)

@Composable
private fun QuickActionGrid(onOptionClick: (String) -> Unit) {
    val options = listOf(
        QuickActionItem("Find a blood donor", "Request or donate blood", Icons.Default.WaterDrop, PranaRed),
        QuickActionItem("Health advice", "Symptoms, nutrition, wellness", Icons.Default.FavoriteBorder, Color(0xFF2E7D32)),
        QuickActionItem("Ayurvedic tips", "Natural & traditional care", Icons.Default.Spa, Color(0xFF15803D)),
        QuickActionItem("Ask Prana", "General health questions", Icons.Default.ChatBubbleOutline, PranaGold)
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickActionCard(options[0], modifier = Modifier.weight(1f), onClick = { onOptionClick(options[0].title) })
            QuickActionCard(options[1], modifier = Modifier.weight(1f), onClick = { onOptionClick(options[1].title) })
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickActionCard(options[2], modifier = Modifier.weight(1f), onClick = { onOptionClick(options[2].title) })
            QuickActionCard(options[3], modifier = Modifier.weight(1f), onClick = { onOptionClick(options[3].title) })
        }
    }
}

@Composable
private fun QuickActionCard(
    item: QuickActionItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(0.8.dp, PranaBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = item.iconTint.copy(alpha = 0.12f),
                modifier = Modifier.size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = item.iconTint,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = item.title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PranaTextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.subtitle,
                    fontSize = 9.sp,
                    color = PranaTextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun MessageItem(message: ChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isUser) {
            /*
            Surface(
                shape = CircleShape,
                color = PranaGreenDark,
                modifier = Modifier.size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = null,
                        tint = PranaGold,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            */
            //Spacer(modifier = Modifier.width(8.dp))
        }

        Card(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isUser) 16.dp else 2.dp,
                bottomEnd = if (message.isUser) 2.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isUser) PranaUserBubble else Color.White
            ),
            border = BorderStroke(
                0.8.dp,
                if (message.isUser) PranaGreenDark else PranaBorderColor
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = if (message.isUser) 1.dp else 0.5.dp),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.text,
                    fontSize = 12.sp,
                    color = if (message.isUser) Color.White else PranaTextDark,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "9:42 AM",
                        fontSize = 9.sp,
                        color = if (message.isUser) Color.White.copy(alpha = 0.7f) else PranaTextMuted
                    )
                    if (message.isUser) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Delivered",
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}