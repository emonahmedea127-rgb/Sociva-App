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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

enum class SocivaScreen {
  MAIN,
  PROFILE,
  EDIT_PROFILE,
  MESSAGES,
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

@OptIn(ExperimentalCoroutinesApi::class)
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

  // Story & Post Media Selection for native picker
  private val _pendingStoryMedia = MutableStateFlow<StoryMediaSelection?>(null)
  val pendingStoryMedia: StateFlow<StoryMediaSelection?> = _pendingStoryMedia.asStateFlow()

  private val _pendingPostUris = MutableStateFlow<List<android.net.Uri>>(emptyList())
  val pendingPostUris: StateFlow<List<android.net.Uri>> = _pendingPostUris.asStateFlow()

  private val _activeStoryIndex = MutableStateFlow(0)
  val activeStoryIndex: StateFlow<Int> = _activeStoryIndex.asStateFlow()

  private val _commentsPostId = MutableStateFlow<String?>(null)
  val commentsPostId: StateFlow<String?> = _commentsPostId.asStateFlow()

  private val _reactionsModalPostId = MutableStateFlow<String?>(null)
  val reactionsModalPostId: StateFlow<String?> = _reactionsModalPostId.asStateFlow()

  // Share Post Composer State
  private val _sharingPost = MutableStateFlow<Post?>(null)
  val sharingPost: StateFlow<Post?> = _sharingPost.asStateFlow()

  // Edit Post Dialog State
  private val _editingPost = MutableStateFlow<Post?>(null)
  val editingPost: StateFlow<Post?> = _editingPost.asStateFlow()

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

  private val prefs = getApplication<Application>().getSharedPreferences("sociva_session", android.content.Context.MODE_PRIVATE)

  // Authentication & Current User State
  private val _currentUserId = MutableStateFlow(prefs.getString("auth_user_id", "user_me") ?: "user_me")
  val currentUserId: StateFlow<String> = _currentUserId.asStateFlow()

  private val _isLoggedIn = MutableStateFlow(prefs.getBoolean("is_logged_in", true))
  val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

  val userSettings: StateFlow<UserSettings> = _currentUserId.flatMapLatest { uid ->
    repository.getUserSettings(uid)
  }.stateIn(
    viewModelScope, SharingStarted.Eagerly, UserSettings(userId = _currentUserId.value)
  )

  val blockedUsers: StateFlow<List<BlockedUser>> = _currentUserId.flatMapLatest { uid ->
    repository.getBlockedUsers(uid)
  }.stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
  )

  init {
    viewModelScope.launch {
      repository.setUserPresence(_currentUserId.value, isOnline = true)
    }
    viewModelScope.launch {
      userSettings.collect { settings ->
        _isDarkTheme.value = settings.darkTheme
      }
    }
  }

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

  val conversations: StateFlow<List<Conversation>> = _currentUserId.flatMapLatest { uid ->
    repository.getConversations(uid)
  }.stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
  )

  val activeNowUsers: StateFlow<List<User>> = _currentUserId.flatMapLatest { uid ->
    repository.getOnlineUsers(uid)
  }.stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
  )

  val notifications: StateFlow<List<NotificationItem>> = _currentUserId.flatMapLatest { uid ->
    repository.getNotifications(uid)
  }.stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
  )

  val friendRequests: StateFlow<List<FriendRequestItem>> = _currentUserId.flatMapLatest { uid ->
    repository.getFriendRequests(uid)
  }.stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
  )

  val incomingRelationshipRequests: StateFlow<List<RelationshipItem>> = _currentUserId.flatMapLatest { uid ->
    repository.getIncomingRelationshipRequests(uid)
  }.stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
  )

  val sentRelationshipRequests: StateFlow<List<RelationshipItem>> = _currentUserId.flatMapLatest { uid ->
    repository.getSentRelationshipRequests(uid)
  }.stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
  )

  val sentFriendRequests: StateFlow<List<FriendRequestItem>> = _currentUserId.flatMapLatest { uid ->
    repository.getSentFriendRequests(uid)
  }.stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
  )

  val friends: StateFlow<List<User>> = _currentUserId.flatMapLatest { uid ->
    repository.getFriends(uid)
  }.stateIn(
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

  val currentUser: StateFlow<User?> = _currentUserId.flatMapLatest { uid ->
    repository.getUser(uid)
  }.stateIn(
    viewModelScope, SharingStarted.Eagerly, null
  )

  fun getPostComments(postId: String): Flow<List<Comment>> {
    val currentUserId = currentUser.value?.id
    return repository.getComments(postId, currentUserId)
  }

  fun getConversationMessages(convId: String): Flow<List<Message>> {
    val uid = _currentUserId.value
    return repository.getMessages(convId, uid)
  }

  fun getConversation(convId: String): Flow<Conversation?> {
    val uid = _currentUserId.value
    return repository.getConversation(convId, uid)
  }

  fun getUser(userId: String): Flow<User?> = repository.getUser(userId)

  fun isUserBlockedFlow(userId: String): Flow<Boolean> {
    val uid = _currentUserId.value
    return repository.isUserBlockedFlow(uid, userId)
  }

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

  fun openReactionsModal(postId: String) {
    _reactionsModalPostId.value = postId
  }

  fun closeReactionsModal() {
    _reactionsModalPostId.value = null
  }

  fun getPostReactionUsers(postId: String): Flow<List<PostReactionUser>> {
    return repository.getPostReactionUsers(postId, _currentUserId.value)
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
    val enabled = dark == true
    _isDarkTheme.value = enabled
    val uid = _currentUserId.value
    viewModelScope.launch {
      repository.updateDarkTheme(uid, enabled)
    }
    if (enabled) {
      showToast("Dark mode enabled 🌙")
    } else {
      showToast("Light mode enabled ☀️")
    }
  }

  fun updateDataSaver(enabled: Boolean) {
    val uid = _currentUserId.value
    viewModelScope.launch {
      repository.updateDataSaver(uid, enabled)
    }
    showToast(if (enabled) "Data Saver enabled" else "Data Saver disabled")
  }

  fun updatePushNotifications(enabled: Boolean) {
    val uid = _currentUserId.value
    viewModelScope.launch {
      repository.updatePushNotifications(uid, enabled)
    }
    showToast(if (enabled) "Push notifications enabled" else "Push notifications disabled")
  }

  fun updateInAppSounds(enabled: Boolean) {
    val uid = _currentUserId.value
    viewModelScope.launch {
      repository.updateInAppSounds(uid, enabled)
    }
    showToast(if (enabled) "In-app sounds enabled 🔊" else "In-app sounds muted 🔇")
  }

  fun updateProfileVisibility(visibility: String) {
    val uid = _currentUserId.value
    viewModelScope.launch {
      repository.updateProfileVisibility(uid, visibility)
    }
    showToast("Profile visibility set to $visibility")
  }

  fun updateTwoFactor(enabled: Boolean, method: String = "AUTHENTICATOR") {
    val uid = _currentUserId.value
    viewModelScope.launch {
      repository.updateTwoFactor(uid, enabled, method)
    }
    showToast(if (enabled) "Two-Factor Authentication activated ($method) 🛡️" else "2FA deactivated")
  }

  fun changePassword() {
    val uid = _currentUserId.value
    viewModelScope.launch {
      repository.updatePassword(uid)
    }
    showToast("Password updated successfully 🔒")
  }

  fun blockUser(targetUserId: String) {
    val uid = _currentUserId.value
    viewModelScope.launch {
      val targetUser = repository.getUser(targetUserId).first()
      val name = targetUser?.fullName ?: "User"
      val success = repository.blockUser(uid, targetUserId)
      if (success) {
        showToast("Blocked $name. They cannot view your profile or message you.")
      } else {
        showToast("Could not block user.")
      }
    }
  }

  fun unblockUser(targetUserId: String) {
    val uid = _currentUserId.value
    viewModelScope.launch {
      val targetUser = repository.getUser(targetUserId).first()
      val name = targetUser?.fullName ?: "User"
      repository.unblockUser(uid, targetUserId)
      showToast("Unblocked $name.")
    }
  }

  fun setLanguage(lang: String) {
    _currentLanguage.value = lang
    showToast("Language changed to $lang")
  }

  // Auth Operations
  fun login(email: String) {
    _isLoggedIn.value = true
    viewModelScope.launch {
      val users = repository.allUsers.first()
      val usernamePart = email.substringBefore("@")
      val match = users.find {
        it.username.equals(usernamePart, ignoreCase = true) ||
        it.fullName.contains(usernamePart, ignoreCase = true) ||
        it.email.equals(email, ignoreCase = true)
      }
      val targetId = if (match != null) {
        match.id
      } else {
        val newId = "user_" + usernamePart.lowercase().replace("[^a-z0-9]".toRegex(), "").ifBlank { "member" }
        val newUser = com.example.sociva.data.local.UserEntity(
          id = newId,
          username = usernamePart.lowercase(),
          fullName = usernamePart.replaceFirstChar { it.uppercase() },
          email = email,
          joinedDate = "Joined recently"
        )
        repository.insertUser(newUser)
        newId
      }
      _currentUserId.value = targetId
      _activeProfileUserId.value = targetId
      prefs.edit().putString("auth_user_id", targetId).putBoolean("is_logged_in", true).apply()
      repository.setUserPresence(targetId, isOnline = true)
    }
    showToast("Welcome to Sociva, $email!")
  }

  fun logout() {
    val uid = _currentUserId.value
    viewModelScope.launch {
      repository.setUserPresence(uid, isOnline = false)
    }
    _isLoggedIn.value = false
    prefs.edit().putBoolean("is_logged_in", false).apply()
    showToast("You have been logged out.")
  }

  fun switchUser(userId: String) {
    val prev = _currentUserId.value
    viewModelScope.launch {
      repository.setUserPresence(prev, isOnline = false)
      _currentUserId.value = userId
      _activeProfileUserId.value = userId
      prefs.edit().putString("auth_user_id", userId).apply()
      repository.setUserPresence(userId, isOnline = true)
      val user = repository.getUser(userId).first()
      showToast("Active user: ${user?.fullName ?: userId}")
    }
  }

  // Actions
  fun createPost(
    content: String,
    mediaUrls: List<String>,
    feeling: String?,
    audience: PostAudience,
    taggedUserIds: List<String> = emptyList()
  ) {
    val author = currentUser.value ?: return
    viewModelScope.launch {
      repository.createPost(author, content, mediaUrls, feeling, audience, taggedUserIds)
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

  fun openShareComposer(post: Post) {
    if (post.audience == PostAudience.ONLY_ME && post.authorId != _currentUserId.value) {
      showToast("This post's privacy settings prevent sharing.")
      return
    }
    _sharingPost.value = post
  }

  fun closeShareComposer() {
    _sharingPost.value = null
  }

  fun createSharedPost(originalPost: Post, caption: String, audience: PostAudience) {
    val author = currentUser.value ?: return
    viewModelScope.launch {
      repository.createSharedPost(
        sharer = author,
        originalPost = originalPost,
        caption = caption,
        audience = audience
      )
      _sharingPost.value = null
      showToast("Post shared to your timeline! 🚀")
    }
  }

  fun openEditPost(post: Post) {
    _editingPost.value = post
  }

  fun closeEditPost() {
    _editingPost.value = null
  }

  fun updatePostContent(postId: String, newContent: String) {
    viewModelScope.launch {
      repository.updatePostContent(postId, newContent.trim())
      _editingPost.value = null
      showToast("Post updated! ✏️")
    }
  }

  fun sharePost(postId: String) {
    viewModelScope.launch {
      val p = allPosts.value.find { it.id == postId }
      if (p != null) {
        openShareComposer(p)
      } else {
        repository.sharePost(postId)
        showToast("Post shared to your timeline! 🚀")
      }
    }
  }

  fun addComment(postId: String, text: String, parentCommentId: String? = null) {
    val author = currentUser.value ?: return
    if (text.isBlank()) return
    viewModelScope.launch {
      repository.addComment(postId, author, text.trim(), parentCommentId)
    }
  }

  fun reactToComment(commentId: String, reactionType: ReactionType) {
    val user = currentUser.value ?: return
    viewModelScope.launch {
      repository.reactToComment(commentId, user, reactionType)
    }
  }

  fun removeCommentReaction(commentId: String) {
    val user = currentUser.value ?: return
    viewModelScope.launch {
      repository.removeCommentReaction(commentId, user.id)
    }
  }

  fun editComment(commentId: String, newContent: String) {
    val user = currentUser.value ?: return
    if (newContent.isBlank()) return
    viewModelScope.launch {
      repository.editComment(commentId, user.id, newContent.trim())
    }
  }

  fun deleteComment(commentId: String, postId: String) {
    val user = currentUser.value ?: return
    viewModelScope.launch {
      repository.deleteComment(
        commentId = commentId,
        postId = postId,
        userId = user.id,
        isPostOwnerOrAdmin = false
      )
    }
  }

  fun reportComment(commentId: String, snippet: String, reason: String) {
    viewModelScope.launch {
      repository.submitReport(
        targetType = "comment",
        targetId = commentId,
        title = "Comment: ${snippet.take(40)}",
        reason = reason
      )
    }
  }

  fun selectStoryMedia(uri: android.net.Uri, isVideo: Boolean, mimeType: String) {
    _pendingStoryMedia.value = StoryMediaSelection(uri, isVideo, mimeType)
    _activeScreen.value = SocivaScreen.STORY_EDITOR
  }

  fun clearStoryMedia() {
    _pendingStoryMedia.value = null
  }

  fun setPendingPostUris(uris: List<android.net.Uri>) {
    _pendingPostUris.value = uris
  }

  fun appendPendingPostUris(uris: List<android.net.Uri>) {
    val current = _pendingPostUris.value.toMutableList()
    uris.forEach { if (!current.contains(it)) current.add(it) }
    _pendingPostUris.value = current
  }

  fun removePendingPostUri(uri: android.net.Uri) {
    _pendingPostUris.value = _pendingPostUris.value.filter { it != uri }
  }

  fun clearPendingPostUris() {
    _pendingPostUris.value = emptyList()
  }

  fun createStory(text: String, mediaUrl: String?, gradientIndex: Int) {
    val user = currentUser.value ?: return
    viewModelScope.launch {
      repository.createStory(user, text, mediaUrl, gradientIndex)
      _pendingStoryMedia.value = null
      _activeScreen.value = SocivaScreen.MAIN
      showToast("Your story is live for 24 hours! ✨")
    }
  }

  fun uploadAndCreateStory(
    uri: android.net.Uri,
    text: String,
    gradientIndex: Int,
    isVideo: Boolean = false
  ) {
    val user = currentUser.value ?: return
    viewModelScope.launch {
      _uploadState.value = UploadState.Validating("Validating story media...")
      val validation = mediaService.validateMediaUri(uri)
      if (!validation.isValid) {
        _uploadState.value = UploadState.Error(validation.errorMessage ?: "Invalid media file.")
        showToast(validation.errorMessage ?: "Invalid media file.")
        return@launch
      }

      val mediaType = MediaType.STORY_MEDIA
      val result = mediaService.uploadMediaFromUri(
        uri = uri,
        userId = user.id,
        type = mediaType,
        onProgress = { p ->
          _uploadState.value = UploadState.Uploading(p)
        }
      )

      result.fold(
        onSuccess = { processed ->
          repository.createStory(
            user = user,
            text = text,
            mediaUrl = processed.url,
            gradientIndex = gradientIndex
          )
          _uploadState.value = UploadState.Success(processed.url, mediaType)
          _pendingStoryMedia.value = null
          _activeScreen.value = SocivaScreen.MAIN
          showToast("Story shared! 🌟")
          delay(800)
          _uploadState.value = UploadState.Idle
        },
        onFailure = { err ->
          _uploadState.value = UploadState.Error(err.localizedMessage ?: "Failed to upload story media.")
          showToast("Upload failed: ${err.localizedMessage ?: "Unknown error"}")
        }
      )
    }
  }

  fun uploadAndCreatePost(
    content: String,
    uris: List<android.net.Uri>,
    additionalUrls: List<String> = emptyList(),
    feeling: String?,
    audience: PostAudience,
    taggedUserIds: List<String> = emptyList()
  ) {
    val user = currentUser.value ?: return
    viewModelScope.launch {
      val uploadedUrls = mutableListOf<String>()
      uploadedUrls.addAll(additionalUrls)

      if (uris.isNotEmpty()) {
        _uploadState.value = UploadState.Validating("Preparing ${uris.size} media file(s)...")
        for ((idx, uri) in uris.withIndex()) {
          val validation = mediaService.validateMediaUri(uri)
          if (!validation.isValid) {
            _uploadState.value = UploadState.Error(validation.errorMessage ?: "Media file #$idx is invalid.")
            showToast("Error with media: ${validation.errorMessage}")
            return@launch
          }
          val mediaType = MediaType.POST_MEDIA

          val uploadRes = mediaService.uploadMediaFromUri(
            uri = uri,
            userId = user.id,
            type = mediaType,
            onProgress = { p ->
              val overall = (idx.toFloat() + p) / uris.size.toFloat()
              _uploadState.value = UploadState.Uploading(overall)
            }
          )

          uploadRes.fold(
            onSuccess = { processed ->
              uploadedUrls.add(processed.url)
            },
            onFailure = { err ->
              _uploadState.value = UploadState.Error("Upload failed for item #${idx + 1}: ${err.localizedMessage}")
              showToast("Failed to upload media: ${err.localizedMessage}")
              return@launch
            }
          )
        }
      }

      // Save post to repository
      repository.createPost(
        author = user,
        content = content,
        mediaUrls = uploadedUrls,
        feeling = feeling,
        audience = audience,
        taggedUserIds = taggedUserIds
      )

      _uploadState.value = UploadState.Success(uploadedUrls.firstOrNull() ?: "", MediaType.POST_MEDIA)
      _pendingPostUris.value = emptyList()
      _activeScreen.value = SocivaScreen.MAIN
      showToast("Your post has been published to Sociva! 🎉")
      delay(800)
      _uploadState.value = UploadState.Idle
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

  fun sendMessage(convId: String, text: String, mediaUrl: String? = null, messageType: String = "TEXT") {
    if (text.isBlank() && mediaUrl.isNullOrBlank()) return
    val senderId = _currentUserId.value
    viewModelScope.launch {
      repository.sendMessage(convId, senderId, text.trim(), mediaUrl, messageType)
    }
  }

  fun markConversationAsRead(convId: String) {
    val myId = _currentUserId.value
    viewModelScope.launch {
      repository.markConversationAsRead(convId, myId)
    }
  }

  fun setTyping(convId: String, isTyping: Boolean) {
    val myId = _currentUserId.value
    repository.setTyping(convId, myId, isTyping)
  }

  fun getTypingUsers(convId: String): Flow<List<String>> {
    val myId = _currentUserId.value
    return repository.getTypingUsers(convId, myId)
  }

  fun softDeleteMessage(messageId: String) {
    viewModelScope.launch {
      repository.softDeleteMessage(messageId)
    }
  }

  fun deleteMessage(messageId: String) {
    viewModelScope.launch {
      repository.deleteMessage(messageId)
    }
  }

  fun openOrCreateConversationWithUser(targetUserId: String) {
    val myId = _currentUserId.value
    viewModelScope.launch {
      val convId = repository.getOrCreateConversation(myId, targetUserId)
      _activeConversationId.value = convId
      _activeScreen.value = SocivaScreen.CHAT_DETAIL
    }
  }

  fun searchUsers(query: String): Flow<List<User>> {
    val myId = _currentUserId.value
    return repository.searchUsers(query, myId)
  }

  fun getAllUsersExceptMe(): Flow<List<User>> {
    val myId = _currentUserId.value
    return repository.getAllUsersExcept(myId)
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

  fun updateFullUserProfile(updated: User) {
    viewModelScope.launch {
      repository.updateFullUserProfile(updated)
      showToast("Profile updated successfully! ✨")
    }
  }

  suspend fun saveUserProfile(updated: User): Result<Unit> {
    return try {
      repository.updateFullUserProfile(updated)
      showToast("Profile updated successfully! ✨")
      Result.success(Unit)
    } catch (e: Exception) {
      showToast("Error updating profile: ${e.message}")
      Result.failure(e)
    }
  }

  fun navigateToMyProfile() {
    _activeProfileUserId.value = _currentUserId.value
    _activeScreen.value = SocivaScreen.PROFILE
  }

  // --- Relationship Management ---
  fun sendRelationshipRequest(
    targetUserId: String,
    relationshipType: String,
    customText: String? = null,
    privacy: String = "Public"
  ) {
    val user = currentUser.value ?: return
    viewModelScope.launch {
      repository.sendRelationshipRequest(user, targetUserId, relationshipType, customText, privacy)
        .onSuccess {
          showToast("Relationship request sent")
        }
        .onFailure {
          showToast(it.message ?: "Failed to send relationship request")
        }
    }
  }

  fun acceptRelationshipRequest(requestId: String) {
    viewModelScope.launch {
      repository.acceptRelationshipRequest(requestId, _currentUserId.value)
        .onSuccess {
          showToast("Relationship accepted! 💕")
        }
        .onFailure {
          showToast(it.message ?: "Failed to accept relationship")
        }
    }
  }

  fun declineRelationshipRequest(requestId: String) {
    viewModelScope.launch {
      repository.declineRelationshipRequest(requestId, _currentUserId.value)
        .onSuccess {
          showToast("Relationship request declined")
        }
        .onFailure {
          showToast(it.message ?: "Failed to decline request")
        }
    }
  }

  fun cancelRelationshipRequest(requestId: String) {
    viewModelScope.launch {
      repository.cancelRelationshipRequest(requestId, _currentUserId.value)
        .onSuccess {
          showToast("Relationship request cancelled")
        }
    }
  }

  fun removeRelationship(newStatus: String = "Single") {
    viewModelScope.launch {
      repository.removeOrResetRelationship(_currentUserId.value, newStatus)
      showToast("Relationship status updated")
    }
  }

  // --- Post Tagging Actions ---
  fun getTaggedPostsByUser(userId: String): Flow<List<Post>> = repository.getTaggedPostsByUser(userId)

  fun removePostTag(postId: String) {
    viewModelScope.launch {
      repository.removePostTag(postId, _currentUserId.value)
      showToast("You were removed from this post")
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

  fun saveBitmapToTempUri(bitmap: android.graphics.Bitmap): android.net.Uri? {
    return try {
      val file = java.io.File(getApplication<Application>().cacheDir, "temp_capture_${System.currentTimeMillis()}.jpg")
      java.io.FileOutputStream(file).use { out ->
        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, out)
      }
      android.net.Uri.fromFile(file)
    } catch (e: Exception) {
      null
    }
  }
}
