package com.example.sociva.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sociva.data.local.SocivaDatabase
import com.example.sociva.data.model.*
import com.example.sociva.data.repository.SocivaRepository
import com.example.sociva.data.service.MediaService
import com.example.sociva.data.service.MediaType
import com.example.sociva.data.service.UploadState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class SocivaScreen {
  MAIN,
  PROFILE,
  CHAT_DETAIL,
  CREATE_POST,
  STORY_VIEWER,
  STORY_EDITOR,
  SEARCH,
  PAGES,
  GROUPS,
  SETTINGS,
  ADMIN,
  SAVED_POSTS
}

data class StoryMediaSelection(
  val uri: android.net.Uri,
  val isVideo: Boolean = false,
  val mimeType: String = ""
)

class SocivaViewModel(application: Application) : AndroidViewModel(application) {

  private val database = SocivaDatabase.getDatabase(application)
  private val repository = SocivaRepository(database.socivaDao(), viewModelScope)
  private val mediaService = MediaService(application)

  // Photo Upload & Management State
  private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
  val uploadState: StateFlow<UploadState> = _uploadState.asStateFlow()
  private var lastUploadTask: (suspend () -> Unit)? = null

  // Navigation State
  private val _selectedTab = MutableStateFlow(0) // 0: Home, 1: Reels, 2: Friends, 3: Notifications, 4: Menu
  val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

  private val _activeScreen = MutableStateFlow(SocivaScreen.MAIN)
  val activeScreen: StateFlow<SocivaScreen> = _activeScreen.asStateFlow()

  private val _activeProfileUserId = MutableStateFlow<String?>("user_me")
  val activeProfileUserId: StateFlow<String?> = _activeProfileUserId.asStateFlow()

  private val _activeConversationId = MutableStateFlow<String?>("conv_sarah")
  val activeConversationId: StateFlow<String?> = _activeConversationId.asStateFlow()

  private val _activeStoryIndex = MutableStateFlow(0)
  val activeStoryIndex: StateFlow<Int> = _activeStoryIndex.asStateFlow()

  private val _commentsPostId = MutableStateFlow<String?>(null)
  val commentsPostId: StateFlow<String?> = _commentsPostId.asStateFlow()

  // Search State
  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _searchCategory = MutableStateFlow("All")
  val searchCategory: StateFlow<String> = _searchCategory.asStateFlow()

  // Toast / Status Message
  private val _toastMessage = MutableStateFlow<String?>(null)
  val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

  // Appearance & Settings - default to Light Mode (false) as preferred
  private val _isDarkTheme = MutableStateFlow<Boolean?>(false) // false = Light Mode
  val isDarkTheme: StateFlow<Boolean?> = _isDarkTheme.asStateFlow()

  private val _currentLanguage = MutableStateFlow("English")
  val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

  // Authentication State
  private val _isLoggedIn = MutableStateFlow(true)
  val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

  // Repositories Data Streams
  val allPosts: StateFlow<List<Post>> = repository.allPosts.stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
  )

  val activeStories: StateFlow<List<Story>> = repository.activeStories.stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
  )

  val allReels: StateFlow<List<Reel>> = repository.allReels.stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
  )

  val conversations: StateFlow<List<Conversation>> = repository.conversations.stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
  )

  val notifications: StateFlow<List<NotificationItem>> = repository.notifications.stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
  )

  val friendRequests: StateFlow<List<FriendRequestItem>> = repository.friendRequests.stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
  )

  val sentFriendRequests: StateFlow<List<FriendRequestItem>> = repository.getSentFriendRequests("user_me").stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
  )

  val friends: StateFlow<List<User>> = repository.friends.stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
  )

  val allUsers: StateFlow<List<User>> = repository.allUsers.stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
  )

  val pages: StateFlow<List<SocivaPage>> = repository.pages.stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
  )

  val groups: StateFlow<List<SocivaGroup>> = repository.groups.stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
  )

  val reports: StateFlow<List<ReportItem>> = repository.reports.stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
  )

  val savedPosts: StateFlow<List<Post>> = repository.savedPosts.stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
  )

  val currentUser: StateFlow<User?> = repository.getUser("user_me").stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000), null
  )

  fun getPostComments(postId: String): Flow<List<Comment>> = repository.getComments(postId)

  fun getConversationMessages(convId: String): Flow<List<Message>> = repository.getMessages(convId)

  fun getUser(userId: String): Flow<User?> = repository.getUser(userId)

  fun getPostsByUser(userId: String): Flow<List<Post>> = repository.getPostsByUser(userId)

  // Navigation Methods
  fun selectTab(tabIndex: Int) {
    _selectedTab.value = tabIndex
    _activeScreen.value = SocivaScreen.MAIN
  }

  fun navigateTo(screen: SocivaScreen) {
    _activeScreen.value = screen
  }

  fun navigateToProfile(userId: String) {
    _activeProfileUserId.value = userId
    _activeScreen.value = SocivaScreen.PROFILE
  }

  fun navigateToChat(convId: String) {
    _activeConversationId.value = convId
    _activeScreen.value = SocivaScreen.CHAT_DETAIL
  }

  fun openStoryViewer(index: Int) {
    _activeStoryIndex.value = index
    _activeScreen.value = SocivaScreen.STORY_VIEWER
    val storiesList = activeStories.value
    if (index in storiesList.indices) {
      viewModelScope.launch {
        repository.markStoryViewed(storiesList[index].id)
      }
    }
  }

  fun nextStory() {
    val max = activeStories.value.size
    if (_activeStoryIndex.value < max - 1) {
      _activeStoryIndex.value += 1
      val story = activeStories.value[_activeStoryIndex.value]
      viewModelScope.launch { repository.markStoryViewed(story.id) }
    } else {
      _activeScreen.value = SocivaScreen.MAIN
    }
  }

  fun prevStory() {
    if (_activeStoryIndex.value > 0) {
      _activeStoryIndex.value -= 1
    } else {
      _activeScreen.value = SocivaScreen.MAIN
    }
  }

  fun openComments(postId: String) {
    _commentsPostId.value = postId
  }

  fun closeComments() {
    _commentsPostId.value = null
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setSearchCategory(category: String) {
    _searchCategory.value = category
  }

  fun showToast(msg: String) {
    _toastMessage.value = msg
  }

  fun clearToast() {
    _toastMessage.value = null
  }

  fun toggleTheme(dark: Boolean?) {
    _isDarkTheme.value = dark
    if (dark == false) {
      showToast("Light mode enabled ☀️")
    } else if (dark == true) {
      showToast("Dark mode enabled 🌙")
    }
  }

  fun setLanguage(lang: String) {
    _currentLanguage.value = lang
    showToast("Language changed to $lang")
  }

  // Auth Operations
  fun login(email: String) {
    _isLoggedIn.value = true
    showToast("Welcome back to Sociva, $email!")
  }

  fun logout() {
    _isLoggedIn.value = false
    showToast("You have been logged out.")
  }

  // Actions
  fun createPost(content: String, mediaUrls: List<String>, feeling: String?, audience: PostAudience) {
    val author = currentUser.value ?: return
    viewModelScope.launch {
      repository.createPost(author, content, mediaUrls, feeling, audience)
      _activeScreen.value = SocivaScreen.MAIN
      showToast("Your post has been published to Sociva! 🎉")
    }
  }

  fun deletePost(postId: String) {
    viewModelScope.launch {
      repository.deletePost(postId)
      showToast("Post deleted")
    }
  }

  fun setReaction(postId: String, reaction: ReactionType) {
    viewModelScope.launch {
      repository.setReaction(postId, reaction)
    }
  }

  fun toggleSavePost(postId: String) {
    viewModelScope.launch {
      repository.toggleSavePost(postId)
      showToast("Post bookmark updated")
    }
  }

  fun sharePost(postId: String) {
    viewModelScope.launch {
      repository.sharePost(postId)
      showToast("Post shared to your timeline! 🚀")
    }
  }

  fun addComment(postId: String, text: String) {
    val author = currentUser.value ?: return
    if (text.isBlank()) return
    viewModelScope.launch {
      repository.addComment(postId, author, text.trim())
    }
  }

  fun deleteComment(commentId: String, postId: String) {
    viewModelScope.launch {
      repository.deleteComment(commentId, postId)
    }
  }

  fun createStory(text: String, mediaUrl: String?, gradientIndex: Int) {
    val user = currentUser.value ?: return
    viewModelScope.launch {
      repository.createStory(user, text, mediaUrl, gradientIndex)
      _activeScreen.value = SocivaScreen.MAIN
      showToast("Your story is live for 24 hours! ✨")
    }
  }

  fun toggleReelLike(reelId: String) {
    viewModelScope.launch { repository.toggleReelLike(reelId) }
  }

  fun toggleReelSave(reelId: String) {
    viewModelScope.launch { repository.toggleReelSave(reelId) }
  }

  fun toggleReelFollow(reelId: String) {
    viewModelScope.launch { repository.toggleReelFollow(reelId) }
  }

  fun sendMessage(convId: String, text: String, mediaUrl: String? = null) {
    if (text.isBlank()) return
    viewModelScope.launch {
      repository.sendMessage(convId, text.trim(), mediaUrl)
    }
  }

  fun deleteMessage(messageId: String) {
    viewModelScope.launch {
      repository.deleteMessage(messageId)
    }
  }

  fun getFriendStatusFlow(userId: String): Flow<FriendStatus> =
    repository.getFriendStatusFlow("user_me", userId)

  fun isFollowingFlow(userId: String): Flow<Boolean> =
    repository.isFollowingFlow("user_me", userId)

  fun sendFriendRequest(targetUserId: String) {
    viewModelScope.launch {
      val success = repository.sendFriendRequest("user_me", targetUserId)
      if (success) {
        showToast("Friend request sent! You are now following them.")
      }
    }
  }

  fun cancelFriendRequest(targetUserId: String) {
    viewModelScope.launch {
      val success = repository.cancelFriendRequest("user_me", targetUserId)
      if (success) {
        showToast("Friend request cancelled")
      }
    }
  }

  fun cancelFriendRequestById(requestId: String) {
    viewModelScope.launch {
      val success = repository.cancelFriendRequestById(requestId)
      if (success) {
        showToast("Friend request cancelled")
      }
    }
  }

  fun acceptFriendRequest(request: FriendRequestItem) {
    viewModelScope.launch {
      repository.acceptFriendRequest(request.id, "user_me")
      showToast("You and ${request.fullName} are now friends! 🤝")
    }
  }

  fun acceptFriendRequestById(requestId: String) {
    viewModelScope.launch {
      val req = friendRequests.value.find { it.id == requestId }
      repository.acceptFriendRequest(requestId, "user_me")
      showToast("You and ${req?.fullName ?: "user"} are now friends! 🤝")
    }
  }

  fun rejectFriendRequest(requestId: String) {
    viewModelScope.launch {
      repository.rejectFriendRequest(requestId)
      showToast("Friend request deleted")
    }
  }

  fun removeFriend(userId: String) {
    viewModelScope.launch {
      repository.removeFriend("user_me", userId)
      showToast("Removed from friends")
    }
  }

  fun followUser(userId: String) {
    viewModelScope.launch {
      repository.followUser("user_me", userId)
      showToast("Following")
    }
  }

  fun unfollowUser(userId: String) {
    viewModelScope.launch {
      repository.unfollowUser("user_me", userId)
      showToast("Unfollowed")
    }
  }

  fun toggleFriend(userId: String) {
    viewModelScope.launch {
      repository.toggleFriend(userId)
    }
  }

  fun toggleFollow(userId: String) {
    viewModelScope.launch {
      repository.toggleFollow(userId)
    }
  }

  fun updateUserProfile(fullName: String, bio: String, work: String, education: String, location: String) {
    viewModelScope.launch {
      repository.updateUserProfile("user_me", fullName, bio, work, education, location)
      showToast("Profile updated successfully! ✨")
    }
  }

  fun markNotificationRead(id: String) {
    viewModelScope.launch { repository.markNotificationRead(id) }
  }

  fun markAllNotificationsRead() {
    viewModelScope.launch {
      repository.markAllNotificationsRead()
      showToast("All notifications marked as read")
    }
  }

  fun togglePageLike(pageId: String) {
    viewModelScope.launch { repository.togglePageLike(pageId) }
  }

  fun createPage(name: String, category: String, description: String) {
    viewModelScope.launch {
      repository.createPage(name, category, description)
      showToast("Page '$name' created successfully! 🌟")
    }
  }

  fun toggleGroupJoin(groupId: String) {
    viewModelScope.launch { repository.toggleGroupJoin(groupId) }
  }

  fun createGroup(name: String, privacy: String, description: String) {
    viewModelScope.launch {
      repository.createGroup(name, privacy, description)
      showToast("Group '$name' created! 👥")
    }
  }

  fun submitReport(targetType: String, targetId: String, title: String, reason: String) {
    viewModelScope.launch {
      repository.submitReport(targetType, targetId, title, reason)
      showToast("Report submitted to moderation team. Thank you!")
    }
  }

  fun resolveReport(reportId: String) {
    viewModelScope.launch {
      repository.resolveReport(reportId)
      showToast("Report resolved by admin")
    }
  }

  fun deleteReport(reportId: String) {
    viewModelScope.launch {
      repository.deleteReport(reportId)
      showToast("Content removed by admin")
    }
  }

  // --- Profile Picture & Cover Photo Management ---

  fun uploadAndSetProfilePicture(bitmap: Bitmap) {
    val currentUserId = currentUser.value?.id ?: "user_me"
    val task: suspend () -> Unit = {
      _uploadState.value = UploadState.Validating()
      delay(120)
      _uploadState.value = UploadState.Compressing()
      delay(150)

      val result = mediaService.uploadImage(
        bitmap = bitmap,
        userId = currentUserId,
        type = MediaType.PROFILE_PICTURE,
        onProgress = { progress ->
          _uploadState.value = UploadState.Uploading(progress)
        }
      )

      result.fold(
        onSuccess = { photoUrl ->
          repository.updateProfilePicture(currentUserId, photoUrl)
          _uploadState.value = UploadState.Success(photoUrl, MediaType.PROFILE_PICTURE)
          showToast("Profile picture updated successfully! ✨")
          delay(1200)
          _uploadState.value = UploadState.Idle
        },
        onFailure = { error ->
          _uploadState.value = UploadState.Error(
            message = error.localizedMessage ?: "Failed to upload profile picture. Please check network or file size.",
            canRetry = true
          )
        }
      )
    }

    lastUploadTask = task
    viewModelScope.launch { task() }
  }

  fun uploadAndSetCoverPhoto(bitmap: Bitmap) {
    val currentUserId = currentUser.value?.id ?: "user_me"
    val task: suspend () -> Unit = {
      _uploadState.value = UploadState.Validating()
      delay(120)
      _uploadState.value = UploadState.Compressing()
      delay(150)

      val result = mediaService.uploadImage(
        bitmap = bitmap,
        userId = currentUserId,
        type = MediaType.COVER_PHOTO,
        onProgress = { progress ->
          _uploadState.value = UploadState.Uploading(progress)
        }
      )

      result.fold(
        onSuccess = { photoUrl ->
          repository.updateCoverPhoto(currentUserId, photoUrl)
          _uploadState.value = UploadState.Success(photoUrl, MediaType.COVER_PHOTO)
          showToast("Cover photo updated successfully! 🌄")
          delay(1200)
          _uploadState.value = UploadState.Idle
        },
        onFailure = { error ->
          _uploadState.value = UploadState.Error(
            message = error.localizedMessage ?: "Failed to upload cover photo. Please try again.",
            canRetry = true
          )
        }
      )
    }

    lastUploadTask = task
    viewModelScope.launch { task() }
  }

  fun removeProfilePicture(userId: String) {
    viewModelScope.launch {
      repository.removeProfilePicture(userId)
      showToast("Profile picture removed. Default avatar restored.")
    }
  }

  fun removeCoverPhoto(userId: String) {
    viewModelScope.launch {
      repository.removeCoverPhoto(userId)
      showToast("Cover photo removed. Default cover restored.")
    }
  }

  fun retryLastUpload() {
    val task = lastUploadTask
    if (task != null) {
      viewModelScope.launch { task() }
    }
  }

  fun dismissUploadState() {
    _uploadState.value = UploadState.Idle
  }
}
