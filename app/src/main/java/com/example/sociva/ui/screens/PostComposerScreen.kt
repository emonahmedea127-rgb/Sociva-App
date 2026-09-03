package com.example.sociva.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.sociva.data.model.PostAudience
import com.example.sociva.data.model.User
import com.example.sociva.ui.SocivaViewModel
import com.example.sociva.ui.components.UserAvatar
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostComposerScreen(
  viewModel: SocivaViewModel,
  onBack: () -> Unit
) {
  val currentUser by viewModel.currentUser.collectAsState()
  var postText by remember { mutableStateOf("") }
  var selectedAudience by remember { mutableStateOf(PostAudience.PUBLIC) }
  var selectedFeeling by remember { mutableStateOf<String?>(null) }
  var showAudienceDropdown by remember { mutableStateOf(false) }
  var showFeelingPicker by remember { mutableStateOf(false) }
  var selectedImages by remember { mutableStateOf(listOf<String>()) }
  var isCreatingStory by remember { mutableStateOf(false) }
  var selectedGradientIndex by remember { mutableStateOf(0) }

  // Sample photos to attach quickly
  val samplePhotos = listOf(
    "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800&h=600&fit=crop",
    "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?w=800&h=600&fit=crop",
    "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=800&h=600&fit=crop",
    "https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=800&h=600&fit=crop",
    "https://images.unsplash.com/photo-1598488035139-bdbb2231ce04?w=800&h=600&fit=crop"
  )

  val gradients = listOf(
    listOf(Color(0xFF2563EB), Color(0xFF7C3AED)),
    listOf(Color(0xFFEC4899), Color(0xFFF43F5E)),
    listOf(Color(0xFF06B6D4), Color(0xFF3B82F6)),
    listOf(Color(0xFF8B5CF6), Color(0xFFEC4899)),
    listOf(Color(0xFF10B981), Color(0xFF06B6D4))
  )

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            if (isCreatingStory) "Create Story" else "Create Post",
            fontWeight = FontWeight.Bold
          )
        },
        navigationIcon = {
          IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("composer_back_button")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          Button(
            onClick = {
              if (isCreatingStory) {
                viewModel.createStory(
                  text = postText,
                  mediaUrl = selectedImages.firstOrNull(),
                  gradientIndex = selectedGradientIndex
                )
              } else {
                viewModel.createPost(
                  content = postText,
                  mediaUrls = selectedImages,
                  feeling = selectedFeeling,
                  audience = selectedAudience
                )
              }
            },
            enabled = postText.isNotBlank() || selectedImages.isNotEmpty(),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = SocivaBlue,
              disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier
              .padding(end = 12.dp)
              .testTag("publish_post_button")
          ) {
            Text(
              if (isCreatingStory) "Share Story" else "Publish",
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Switch between "Feed Post" and "24h Story"
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant)
          .padding(4.dp),
        horizontalArrangement = Arrangement.Center
      ) {
        TabButton(
          title = "Feed Post",
          isSelected = !isCreatingStory,
          onClick = { isCreatingStory = false },
          modifier = Modifier.weight(1f)
        )
        TabButton(
          title = "24h Story",
          isSelected = isCreatingStory,
          onClick = { isCreatingStory = true },
          modifier = Modifier.weight(1f)
        )
      }

      // Author Info Row
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        UserAvatar(
          avatarUrl = currentUser?.avatarUrl,
          name = currentUser?.fullName ?: "User",
          size = 46.dp
        )

        Column {
          Text(
            text = currentUser?.fullName ?: "User",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
          )

          if (!isCreatingStory) {
            // Audience Selector Chip
            Box {
              OutlinedButton(
                onClick = { showAudienceDropdown = true },
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.height(28.dp)
              ) {
                Icon(
                  imageVector = when (selectedAudience) {
                    PostAudience.FRIENDS -> Icons.Default.Group
                    PostAudience.ONLY_ME -> Icons.Default.Lock
                    else -> Icons.Default.Public
                  },
                  contentDescription = null,
                  modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = selectedAudience.label,
                  style = MaterialTheme.typography.labelSmall
                )
                Icon(
                  imageVector = Icons.Default.ArrowDropDown,
                  contentDescription = null,
                  modifier = Modifier.size(14.dp)
                )
              }

              DropdownMenu(
                expanded = showAudienceDropdown,
                onDismissRequest = { showAudienceDropdown = false }
              ) {
                PostAudience.values().forEach { aud ->
                  DropdownMenuItem(
                    text = { Text(aud.label) },
                    onClick = {
                      selectedAudience = aud
                      showAudienceDropdown = false
                    },
                    leadingIcon = {
                      Icon(
                        imageVector = when (aud) {
                          PostAudience.FRIENDS -> Icons.Default.Group
                          PostAudience.ONLY_ME -> Icons.Default.Lock
                          else -> Icons.Default.Public
                        },
                        contentDescription = null
                      )
                    }
                  )
                }
              }
            }
          } else {
            Text(
              text = "Expires in 24 hours",
              style = MaterialTheme.typography.labelSmall,
              color = SocivaPurple
            )
          }
        }
      }

      // Feeling / Activity tag if selected
      if (selectedFeeling != null) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SocivaPurple.copy(alpha = 0.1f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Text(
            text = selectedFeeling ?: "",
            style = MaterialTheme.typography.labelMedium,
            color = SocivaPurple
          )
          Spacer(modifier = Modifier.width(6.dp))
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Remove feeling",
            modifier = Modifier
              .size(14.dp)
              .clickable { selectedFeeling = null },
            tint = SocivaPurple
          )
        }
      }

      // Main Text Input Area
      OutlinedTextField(
        value = postText,
        onValueChange = { postText = it },
        placeholder = {
          Text(
            if (isCreatingStory) "Write your story message..."
            else "What's on your mind, ${currentUser?.fullName?.substringBefore(" ") ?: ""}?"
          )
        },
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(min = 120.dp, max = 220.dp)
          .testTag("post_text_input"),
        colors = OutlinedTextFieldDefaults.colors(
          unfocusedBorderColor = Color.Transparent,
          focusedBorderColor = Color.Transparent
        )
      )

      // Story Gradient Picker if in Story Mode
      if (isCreatingStory) {
        Text(
          text = "Story Background Palette",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold
        )
        Row(
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          gradients.forEachIndexed { index, grad ->
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(grad))
                .border(
                  width = if (selectedGradientIndex == index) 3.dp else 1.dp,
                  color = if (selectedGradientIndex == index) MaterialTheme.colorScheme.primary else Color.Transparent,
                  shape = CircleShape
                )
                .clickable { selectedGradientIndex = index }
            )
          }
        }
      }

      // Attached Images preview
      if (selectedImages.isNotEmpty()) {
        Text(
          text = "Attached Photos (${selectedImages.size})",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          items(selectedImages) { url ->
            Box(
              modifier = Modifier
                .size(110.dp)
                .clip(RoundedCornerShape(12.dp))
            ) {
              AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                  .data(url)
                  .crossfade(true)
                  .build(),
                contentDescription = "Attached photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
              )
              IconButton(
                onClick = { selectedImages = selectedImages - url },
                modifier = Modifier
                  .align(Alignment.TopEnd)
                  .size(28.dp)
                  .padding(4.dp)
                  .clip(CircleShape)
                  .background(Color.Black.copy(alpha = 0.6f))
              ) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Remove",
                  tint = Color.White,
                  modifier = Modifier.size(14.dp)
                )
              }
            }
          }
        }
      }

      // Quick Media Gallery to Attach
      Text(
        text = "Tap to add photos:",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(samplePhotos) { photoUrl ->
          val isAdded = selectedImages.contains(photoUrl)
          Box(
            modifier = Modifier
              .size(70.dp)
              .clip(RoundedCornerShape(10.dp))
              .border(
                width = if (isAdded) 2.5.dp else 0.5.dp,
                color = if (isAdded) SocivaBlue else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(10.dp)
              )
              .clickable {
                selectedImages = if (isAdded) {
                  selectedImages - photoUrl
                } else {
                  selectedImages + photoUrl
                }
              }
          ) {
            AsyncImage(
              model = ImageRequest.Builder(LocalContext.current)
                .data(photoUrl)
                .crossfade(true)
                .build(),
              contentDescription = "Sample photo",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
            if (isAdded) {
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .background(SocivaBlue.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = "Selected",
                  tint = Color.White
                )
              }
            }
          }
        }
      }

      HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

      // Bottom Add Options: Feeling/Activity
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant)
          .clickable { showFeelingPicker = true }
          .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.SentimentSatisfiedAlt,
            contentDescription = null,
            tint = Color(0xFFF59E0B)
          )
          Text(
            text = selectedFeeling ?: "Add Feeling / Activity",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
          )
        }
        Icon(
          imageVector = Icons.Default.ChevronRight,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }

  // Feeling Picker Modal Dialog
  if (showFeelingPicker) {
    val feelings = listOf(
      "feeling happy 😊",
      "feeling excited 🎉",
      "feeling peaceful 🌲",
      "feeling creative 🎨",
      "drinking coffee ☕",
      "listening to music 🎧",
      "traveling ✈️",
      "feeling motivated 🚀",
      "working hard 💼",
      "feeling loved ❤️"
    )
    AlertDialog(
      onDismissRequest = { showFeelingPicker = false },
      title = { Text("How are you feeling?", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          feelings.forEach { feeling ->
            Text(
              text = feeling,
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                  selectedFeeling = feeling
                  showFeelingPicker = false
                }
                .padding(vertical = 8.dp, horizontal = 12.dp),
              style = MaterialTheme.typography.bodyLarge
            )
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showFeelingPicker = false }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
fun TabButton(
  title: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .background(if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent)
      .clickable { onClick() }
      .padding(vertical = 8.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.labelLarge,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
      color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}
