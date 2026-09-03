package com.example.sociva.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.sociva.data.model.SocivaPage
import com.example.sociva.ui.SocivaViewModel
import com.example.sociva.ui.components.UserAvatar
import com.example.ui.theme.SocivaBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagesScreen(
  viewModel: SocivaViewModel,
  onBack: () -> Unit
) {
  val pages by viewModel.pages.collectAsState()
  var showCreateDialog by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Pages", fontWeight = FontWeight.Bold) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          FilledTonalButton(
            onClick = { showCreateDialog = true },
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            modifier = Modifier.padding(end = 8.dp)
          ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Create")
          }
        }
      )
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      items(pages, key = { it.id }) { page ->
        PageCard(
          page = page,
          onToggleLike = { viewModel.togglePageLike(page.id) }
        )
      }
    }
  }

  if (showCreateDialog) {
    CreatePageDialog(
      onDismiss = { showCreateDialog = false },
      onCreate = { name, cat, desc ->
        viewModel.createPage(name, cat, desc)
        showCreateDialog = false
      }
    )
  }
}

@Composable
fun PageCard(
  page: SocivaPage,
  onToggleLike: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column {
      AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
          .data(page.coverUrl)
          .crossfade(true)
          .build(),
        contentDescription = "Cover",
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .fillMaxWidth()
          .height(110.dp)
      )

      Column(modifier = Modifier.padding(14.dp)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          UserAvatar(avatarUrl = page.avatarUrl, name = page.name, size = 48.dp)
          Column(modifier = Modifier.weight(1f)) {
            Text(page.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text("${page.followersCount} followers • ${page.category}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
          Button(
            onClick = onToggleLike,
            colors = ButtonDefaults.buttonColors(
              containerColor = if (page.isLiked) MaterialTheme.colorScheme.surfaceVariant else SocivaBlue
            ),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(
              imageVector = if (page.isLiked) Icons.Default.Check else Icons.Default.ThumbUp,
              contentDescription = null,
              tint = if (page.isLiked) MaterialTheme.colorScheme.onSurface else Color.White,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (page.isLiked) "Following" else "Follow",
              color = if (page.isLiked) MaterialTheme.colorScheme.onSurface else Color.White
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = page.description,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}

@Composable
fun CreatePageDialog(
  onDismiss: () -> Unit,
  onCreate: (name: String, category: String, description: String) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Create a Page", fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Page Name") },
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = category,
          onValueChange = { category = it },
          label = { Text("Category (e.g. Technology, Art)") },
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Description") },
          modifier = Modifier.fillMaxWidth(),
          maxLines = 3
        )
      }
    },
    confirmButton = {
      Button(
        onClick = { if (name.isNotBlank()) onCreate(name, category, description) },
        enabled = name.isNotBlank()
      ) {
        Text("Create")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel") }
    }
  )
}
