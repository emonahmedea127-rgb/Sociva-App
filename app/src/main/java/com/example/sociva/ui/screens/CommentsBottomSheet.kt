package com.example.sociva.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sociva.data.model.Comment
import com.example.sociva.ui.SocivaViewModel
import com.example.sociva.ui.components.UserAvatar
import com.example.sociva.ui.components.VerifiedBadge
import com.example.sociva.ui.components.formatRelativeTime
import com.example.ui.theme.SocivaBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsBottomSheet(
  postId: String,
  viewModel: SocivaViewModel,
  onDismiss: () -> Unit
) {
  val comments by viewModel.getPostComments(postId).collectAsState(initial = emptyList())
  val currentUser by viewModel.currentUser.collectAsState()
  var newCommentText by remember { mutableStateOf("") }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    containerColor = MaterialTheme.colorScheme.surface
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.75f)
        .navigationBarsPadding()
        .imePadding()
    ) {
      // Header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Comments (${comments.size})",
          fontWeight = FontWeight.Bold,
          style = MaterialTheme.typography.titleMedium
        )

        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close")
        }
      }

      HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

      // Comments List
      if (comments.isEmpty()) {
        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "No comments yet. Be the first to comment!",
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          contentPadding = PaddingValues(vertical = 12.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          items(comments, key = { it.id }) { comment ->
            CommentRow(
              comment = comment,
              isMine = comment.authorId == currentUser?.id,
              onDelete = { viewModel.deleteComment(comment.id, postId) },
              onAuthorClick = {
                onDismiss()
                viewModel.navigateToProfile(comment.authorId)
              }
            )
          }
        }
      }

      // Bottom Input Field
      Surface(
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          UserAvatar(
            avatarUrl = currentUser?.avatarUrl,
            name = currentUser?.fullName ?: "Me",
            size = 36.dp
          )

          OutlinedTextField(
            value = newCommentText,
            onValueChange = { newCommentText = it },
            placeholder = { Text("Write a comment...") },
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
              .weight(1f)
              .testTag("comment_input_field"),
            maxLines = 3
          )

          IconButton(
            onClick = {
              if (newCommentText.isNotBlank()) {
                viewModel.addComment(postId, newCommentText)
                newCommentText = ""
              }
            },
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(SocivaBlue)
              .testTag("comment_submit_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Send,
              contentDescription = "Post Comment",
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
fun CommentRow(
  comment: Comment,
  isMine: Boolean,
  onDelete: () -> Unit,
  onAuthorClick: () -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    UserAvatar(
      avatarUrl = comment.authorAvatar,
      name = comment.authorName,
      size = 38.dp,
      onClick = onAuthorClick
    )

    Column(modifier = Modifier.weight(1f)) {
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(16.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant)
          .padding(horizontal = 12.dp, vertical = 8.dp)
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = comment.authorName,
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.clickable { onAuthorClick() }
            )
            if (comment.isAuthorVerified) {
              Spacer(modifier = Modifier.width(4.dp))
              VerifiedBadge(size = 12.dp)
            }
          }

          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = comment.content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }

      Row(
        modifier = Modifier.padding(start = 8.dp, top = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = formatRelativeTime(comment.timestamp),
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 11.sp
        )

        if (isMine) {
          Text(
            text = "Delete",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onDelete() }
          )
        }
      }
    }
  }
}
