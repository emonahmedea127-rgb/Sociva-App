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
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sociva.data.model.SocivaGroup
import com.example.sociva.ui.SocivaViewModel
import com.example.sociva.ui.components.UserAvatar
import com.example.ui.theme.SocivaBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(
  viewModel: SocivaViewModel,
  onBack: () -> Unit
) {
  val groups by viewModel.groups.collectAsState()
  var showCreateDialog by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Groups", fontWeight = FontWeight.Bold) },
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
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      items(groups, key = { it.id }) { group ->
        GroupCard(
          group = group,
          onToggleJoin = { viewModel.toggleGroupJoin(group.id) }
        )
      }
    }
  }

  if (showCreateDialog) {
    CreateGroupDialog(
      onDismiss = { showCreateDialog = false },
      onCreate = { name, privacy, desc ->
        viewModel.createGroup(name, privacy, desc)
        showCreateDialog = false
      }
    )
  }
}

@Composable
fun GroupCard(
  group: SocivaGroup,
  onToggleJoin: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      UserAvatar(avatarUrl = group.avatarUrl, name = group.name, size = 52.dp)

      Column(modifier = Modifier.weight(1f)) {
        Text(group.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Text("${group.membersCount} members • ${group.privacy}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(4.dp))
        Text(group.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
      }

      Button(
        onClick = onToggleJoin,
        colors = ButtonDefaults.buttonColors(
          containerColor = if (group.isJoined) MaterialTheme.colorScheme.surfaceVariant else SocivaBlue
        ),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
      ) {
        Icon(
          imageVector = if (group.isJoined) Icons.Default.Check else Icons.Default.GroupAdd,
          contentDescription = null,
          tint = if (group.isJoined) MaterialTheme.colorScheme.onSurface else Color.White,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = if (group.isJoined) "Joined" else "Join",
          color = if (group.isJoined) MaterialTheme.colorScheme.onSurface else Color.White,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

@Composable
fun CreateGroupDialog(
  onDismiss: () -> Unit,
  onCreate: (name: String, privacy: String, description: String) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var privacy by remember { mutableStateOf("Public group") }
  var description by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Create a Group", fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Group Name") },
          modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          FilterChip(
            selected = privacy == "Public group",
            onClick = { privacy = "Public group" },
            label = { Text("Public") }
          )
          FilterChip(
            selected = privacy == "Private group",
            onClick = { privacy = "Private group" },
            label = { Text("Private") }
          )
        }
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
        onClick = { if (name.isNotBlank()) onCreate(name, privacy, description) },
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
