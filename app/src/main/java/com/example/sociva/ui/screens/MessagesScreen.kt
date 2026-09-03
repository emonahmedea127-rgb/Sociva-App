package com.example.sociva.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sociva.data.model.Conversation
import com.example.sociva.data.model.Message
import com.example.sociva.data.model.User
import com.example.sociva.ui.SocivaViewModel
import com.example.sociva.ui.components.UserAvatar
import com.example.sociva.ui.components.VerifiedBadge
import com.example.sociva.ui.components.formatRelativeTime
import com.example.ui.theme.SocivaBlue

@Composable
fun MessagesScreen(
  viewModel: SocivaViewModel,
  onOpenConversation: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val conversations by viewModel.conversations.collectAsState()
  val friends by viewModel.friends.collectAsState()
  var searchQuery by remember { mutableStateOf("") }

  val filteredConversations = remember(conversations, searchQuery) {
    if (searchQuery.isBlank()) conversations
    else conversations.filter {
      it.participantName.contains(searchQuery, ignoreCase = true) ||
      it.lastMessage.contains(searchQuery, ignoreCase = true)
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("messages_screen")
  ) {
    // Search Bar
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      placeholder = { Text("Search messages...") },
      leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
      shape = RoundedCornerShape(24.dp),
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 10.dp),
      singleLine = true
    )

    // Online Friends Row ("Active Now")
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
      Text(
        text = "Active Now",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
      )

      LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        items(friends) { friend ->
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable {
              val conv = conversations.find { it.participantId == friend.id }
              if (conv != null) {
                onOpenConversation(conv.id)
              } else {
                onOpenConversation("conv_sarah")
              }
            }
          ) {
            UserAvatar(
              avatarUrl = friend.avatarUrl,
              name = friend.fullName,
              size = 52.dp,
              showOnlineBadge = true,
              isOnline = true
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = friend.fullName.substringBefore(" "),
              style = MaterialTheme.typography.labelSmall,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }
    }

    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

    // Conversations List
    LazyColumn(
      contentPadding = PaddingValues(vertical = 8.dp),
      modifier = Modifier.fillMaxSize()
    ) {
      items(filteredConversations, key = { it.id }) { conv ->
        ConversationListItem(
          conversation = conv,
          onClick = { onOpenConversation(conv.id) }
        )
      }
    }
  }
}

@Composable
fun ConversationListItem(
  conversation: Conversation,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .padding(horizontal = 16.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    UserAvatar(
      avatarUrl = conversation.participantAvatar,
      name = conversation.participantName,
      size = 54.dp,
      showOnlineBadge = true,
      isOnline = conversation.isOnline
    )

    Column(modifier = Modifier.weight(1f)) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = conversation.participantName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (conversation.unreadCount > 0) FontWeight.Bold else FontWeight.SemiBold
          )
          if (conversation.isParticipantVerified) {
            Spacer(modifier = Modifier.width(4.dp))
            VerifiedBadge(size = 14.dp)
          }
        }

        Text(
          text = formatRelativeTime(conversation.lastMessageTimestamp),
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(2.dp))

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = conversation.lastMessage,
          style = MaterialTheme.typography.bodyMedium,
          color = if (conversation.unreadCount > 0) MaterialTheme.colorScheme.onSurface
                  else MaterialTheme.colorScheme.onSurfaceVariant,
          fontWeight = if (conversation.unreadCount > 0) FontWeight.Bold else FontWeight.Normal,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.weight(1f)
        )

        if (conversation.unreadCount > 0) {
          Spacer(modifier = Modifier.width(8.dp))
          Box(
            modifier = Modifier
              .size(20.dp)
              .clip(CircleShape)
              .background(SocivaBlue),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "${conversation.unreadCount}",
              color = Color.White,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
  conversationId: String,
  viewModel: SocivaViewModel,
  onBack: () -> Unit
) {
  val conversations by viewModel.conversations.collectAsState()
  val conversation = conversations.find { it.id == conversationId } ?: conversations.firstOrNull()
  val messages by viewModel.getConversationMessages(conversationId).collectAsState(initial = emptyList())

  var messageText by remember { mutableStateOf("") }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            UserAvatar(
              avatarUrl = conversation?.participantAvatar,
              name = conversation?.participantName ?: "Chat",
              size = 38.dp,
              showOnlineBadge = true,
              isOnline = conversation?.isOnline == true
            )
            Column {
              Text(
                text = conversation?.participantName ?: "Chat",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = if (conversation?.isOnline == true) "Active now" else "Offline",
                style = MaterialTheme.typography.labelSmall,
                color = if (conversation?.isOnline == true) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          IconButton(onClick = { viewModel.showToast("Starting audio call...") }) {
            Icon(Icons.Default.Phone, contentDescription = "Audio Call")
          }
          IconButton(onClick = { viewModel.showToast("Starting video call...") }) {
            Icon(Icons.Default.Videocam, contentDescription = "Video Call")
          }
        }
      )
    },
    bottomBar = {
      Surface(
        tonalElevation = 3.dp,
        modifier = Modifier
          .fillMaxWidth()
          .imePadding()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .navigationBarsPadding(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          IconButton(
            onClick = {
              viewModel.sendMessage(
                conversationId,
                "Shared a photo from camera 📷",
                mediaUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800&h=600&fit=crop"
              )
            }
          ) {
            Icon(
              imageVector = Icons.Default.CameraAlt,
              contentDescription = "Camera",
              tint = SocivaBlue
            )
          }

          OutlinedTextField(
            value = messageText,
            onValueChange = { messageText = it },
            placeholder = { Text("Message...") },
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
              .weight(1f)
              .testTag("chat_input_field"),
            maxLines = 4
          )

          IconButton(
            onClick = {
              if (messageText.isNotBlank()) {
                viewModel.sendMessage(conversationId, messageText)
                messageText = ""
              }
            },
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(SocivaBlue)
              .testTag("chat_send_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Send,
              contentDescription = "Send",
              tint = Color.White,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 14.dp),
      reverseLayout = false,
      verticalArrangement = Arrangement.spacedBy(10.dp),
      contentPadding = PaddingValues(vertical = 12.dp)
    ) {
      items(messages, key = { it.id }) { msg ->
        MessageBubble(message = msg)
      }
    }
  }
}

@Composable
fun MessageBubble(message: Message) {
  val isMine = message.isMine
  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
  ) {
    Box(
      modifier = Modifier
        .widthIn(max = 280.dp)
        .clip(
          RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = if (isMine) 16.dp else 4.dp,
            bottomEnd = if (isMine) 4.dp else 16.dp
          )
        )
        .background(
          if (isMine) SocivaBlue
          else MaterialTheme.colorScheme.surfaceVariant
        )
        .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
      Text(
        text = message.text,
        color = if (isMine) Color.White else MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.bodyMedium
      )
    }

    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = formatRelativeTime(message.timestamp),
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      fontSize = 10.sp
    )
  }
}
