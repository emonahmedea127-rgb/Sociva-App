package com.example.sociva.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sociva.ui.SocivaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  viewModel: SocivaViewModel,
  onBack: () -> Unit
) {
  var is2FAEnabled by remember { mutableStateOf(false) }
  var isDataSaverEnabled by remember { mutableStateOf(false) }
  var isPushNotificationsEnabled by remember { mutableStateOf(true) }
  var isSoundsEnabled by remember { mutableStateOf(true) }
  var profileVisibility by remember { mutableStateOf("Public") }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Settings & Privacy", fontWeight = FontWeight.Bold) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        }
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(MaterialTheme.colorScheme.background)
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Security
      Text("Security & Login", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column {
          SettingSwitchRow(
            title = "Two-Factor Authentication (2FA)",
            subtitle = "Require security SMS or authenticator code",
            icon = Icons.Default.Security,
            checked = is2FAEnabled,
            onCheckedChange = {
              is2FAEnabled = it
              viewModel.showToast(if (it) "Two-Factor Authentication activated" else "2FA deactivated")
            }
          )
          HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
          SettingClickRow(
            title = "Change Password",
            subtitle = "Last updated 3 months ago",
            icon = Icons.Default.Lock,
            onClick = { viewModel.showToast("Password change dialog") }
          )
        }
      }

      // Privacy
      Text("Privacy", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column {
          SettingClickRow(
            title = "Profile Visibility",
            subtitle = profileVisibility,
            icon = Icons.Default.Visibility,
            onClick = {
              profileVisibility = if (profileVisibility == "Public") "Friends Only" else "Public"
              viewModel.showToast("Profile visibility set to $profileVisibility")
            }
          )
          HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
          SettingClickRow(
            title = "Blocked Users",
            subtitle = "0 users blocked",
            icon = Icons.Default.Block,
            onClick = { viewModel.showToast("Block list is empty") }
          )
        }
      }

      // Media & Data
      Text("Preferences & Media", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        val isDarkTheme by viewModel.isDarkTheme.collectAsState()
        Column {
          SettingSwitchRow(
            title = "Dark Theme",
            subtitle = if (isDarkTheme == true) "Dark theme enabled" else "Light theme enabled (Default)",
            icon = if (isDarkTheme == true) Icons.Default.DarkMode else Icons.Default.LightMode,
            checked = isDarkTheme == true,
            onCheckedChange = { viewModel.toggleTheme(it) }
          )
          HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
          SettingSwitchRow(
            title = "Data Saver",
            subtitle = "Lower image resolution on cellular networks",
            icon = Icons.Default.DataSaverOn,
            checked = isDataSaverEnabled,
            onCheckedChange = {
              isDataSaverEnabled = it
              viewModel.showToast(if (it) "Data Saver enabled" else "Data Saver disabled")
            }
          )
          HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
          SettingSwitchRow(
            title = "Push Notifications",
            subtitle = "Receive alerts for reactions and direct chats",
            icon = Icons.Default.Notifications,
            checked = isPushNotificationsEnabled,
            onCheckedChange = { isPushNotificationsEnabled = it }
          )
          HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
          SettingSwitchRow(
            title = "In-App Sounds",
            subtitle = "Play sound when sending messages or reacting",
            icon = Icons.Default.VolumeUp,
            checked = isSoundsEnabled,
            onCheckedChange = { isSoundsEnabled = it }
          )
        }
      }

      // About
      Text("About Sociva", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text("Sociva v2.4.0 (Build 2026)", fontWeight = FontWeight.Bold)
          Text("Connect. Share. Belong.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Spacer(modifier = Modifier.height(8.dp))
          Text("Designed with clean typography, rounded cards, and smooth responsiveness across Android devices.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }
    }
  }
}

@Composable
fun SettingSwitchRow(
  title: String,
  subtitle: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      modifier = Modifier.weight(1f),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
      Column {
        Text(title, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium)
        Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    }
    Switch(checked = checked, onCheckedChange = onCheckedChange)
  }
}

@Composable
fun SettingClickRow(
  title: String,
  subtitle: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .padding(horizontal = 16.dp, vertical = 14.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      modifier = Modifier.weight(1f),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
      Column {
        Text(title, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium)
        Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    }
    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
  }
}
