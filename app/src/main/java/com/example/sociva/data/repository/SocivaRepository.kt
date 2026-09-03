package com.example.sociva.data.repository

import com.example.sociva.data.local.*
import com.example.sociva.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID

class SocivaRepository(
  private val dao: SocivaDao,
  private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {

  init {
    scope.launch {
      // Check if seeded
      val existingUsers = dao.getAllUsers().first()
      if (existingUsers.isEmpty()) {
        seedDatabase()
      }
    }
  }

  private suspend fun seedDatabase() {
    dao.insertUsers(SeedData.users)
    dao.insertPosts(SeedData.posts)
    dao.insertComments(SeedData.comments)
    dao.insertStories(SeedData.stories)
    dao.insertReels(SeedData.reels)
    dao.insertConversations(SeedData.conversations)
    dao.insertMessages(SeedData.messages)
    dao.insertNotifications(SeedData.notifications)
    dao.insertFriendRequests(SeedData.friendRequests)
    dao.insertFriendships(SeedData.friendships)
    dao.insertFollows(SeedData.follows)
    dao.insertPages(SeedData.pages)
    dao.insertGroups(SeedData.groups)
    dao.insertReports(SeedData.reports)
  }

  // --- Users ---
  val allUsers: Flow<List<User>> = dao.getAllUsers().map { list ->
    list.map { it.toDomain() }
  }

  fun getFriends(userId: String = "user_me"): Flow<List<User>> =
    dao.getFriendIdsForUser(userId).map { ids ->
      val all = dao.getAllUsers().first()
      all.filter { ids.contains(it.id) }.map { it.toDomain().copy(isFriend = true) }
    }

  val friends: Flow<List<User>> = getFriends("user_me")

  fun getUser(id: String): Flow<User?> = dao.getUserById(id).map { it?.toDomain() }

  suspend fun updateUserProfile(
    userId: String,
    fullName: String,
    bio: String,
    work: String,
    education: String,
    location: String
  ) {
    val user = dao.getUserById(userId).first() ?: return
    dao.updateUser(
      user.copy(
        fullName = fullName,
        bio = bio,
        work = work,
        education = education,
        location = location
      )
    )
  }

  suspend fun followUser(followerId: String = "user_me", targetUserId: String) {
    if (followerId == targetUserId) return
    if (dao.isFollowing(followerId, targetUserId)) return
    dao.insertFollow(
      FollowEntity(
        id = "fol_" + UUID.randomUUID().toString().take(8),
        followerId = followerId,
        followingId = targetUserId,
        createdAt = System.currentTimeMillis()
      )
    )
    val followerUser = dao.getUserById(followerId).first()
    val targetUser = dao.getUserById(targetUserId).first()
    val newFollowing = dao.getFollowingCount(followerId)
    val newFollowers = dao.getFollowersCount(targetUserId)
    if (followerUser != null) {
      dao.updateUser(followerUser.copy(followingCount = newFollowing))
    }
    if (targetUser != null) {
      dao.updateUser(
        targetUser.copy(
          followersCount = newFollowers,
          isFollowing = if (followerId == "user_me") true else targetUser.isFollowing
        )
      )
    }
  }

  suspend fun unfollowUser(followerId: String = "user_me", targetUserId: String) {
    dao.deleteFollow(followerId, targetUserId)
    val followerUser = dao.getUserById(followerId).first()
    val targetUser = dao.getUserById(targetUserId).first()
    val newFollowing = dao.getFollowingCount(followerId)
    val newFollowers = dao.getFollowersCount(targetUserId)
    if (followerUser != null) {
      dao.updateUser(followerUser.copy(followingCount = newFollowing))
    }
    if (targetUser != null) {
      dao.updateUser(
        targetUser.copy(
          followersCount = newFollowers,
          isFollowing = if (followerId == "user_me") false else targetUser.isFollowing
        )
      )
    }
  }

  suspend fun toggleFollow(userId: String) {
    val isFol = dao.isFollowing("user_me", userId)
    if (isFol) {
      unfollowUser("user_me", userId)
    } else {
      followUser("user_me", userId)
    }
  }

  suspend fun removeFriend(userA: String, userB: String) {
    dao.deleteFriendshipBetween(userA, userB)
    val userAEntity = dao.getUserById(userA).first()
    val userBEntity = dao.getUserById(userB).first()
    val countA = dao.getFriendsCount(userA)
    val countB = dao.getFriendsCount(userB)
    if (userAEntity != null) {
      dao.updateUser(
        userAEntity.copy(
          friendsCount = countA,
          isFriend = if (userB == "user_me") false else userAEntity.isFriend
        )
      )
    }
    if (userBEntity != null) {
      dao.updateUser(
        userBEntity.copy(
          friendsCount = countB,
          isFriend = if (userA == "user_me") false else userBEntity.isFriend
        )
      )
    }
  }

  suspend fun toggleFriend(userId: String) {
    val isFr = dao.hasFriendship("user_me", userId)
    if (isFr) {
      removeFriend("user_me", userId)
    } else {
      sendFriendRequest("user_me", userId)
    }
  }

  suspend fun updateProfilePicture(userId: String, newAvatarUrl: String) {
    val now = System.currentTimeMillis()
    dao.updateUserAvatar(userId, newAvatarUrl, now)
    dao.updateAuthorAvatarInPosts(userId, newAvatarUrl)
    dao.updateAuthorAvatarInComments(userId, newAvatarUrl)
    dao.updateUserAvatarInStories(userId, newAvatarUrl)
    dao.updateCreatorAvatarInReels(userId, newAvatarUrl)
    dao.updateParticipantAvatarInConversations(userId, newAvatarUrl)
    val user = dao.getUserById(userId).first()
    if (user != null) {
      dao.updateActorAvatarInNotifications(user.fullName, newAvatarUrl)
    }
  }

  suspend fun removeProfilePicture(userId: String) {
    val now = System.currentTimeMillis()
    dao.updateUserAvatar(userId, "", now)
    dao.updateAuthorAvatarInPosts(userId, "")
    dao.updateAuthorAvatarInComments(userId, "")
    dao.updateUserAvatarInStories(userId, "")
    dao.updateCreatorAvatarInReels(userId, "")
    dao.updateParticipantAvatarInConversations(userId, "")
    val user = dao.getUserById(userId).first()
    if (user != null) {
      dao.updateActorAvatarInNotifications(user.fullName, "")
    }
  }

  suspend fun updateCoverPhoto(userId: String, newCoverUrl: String) {
    val now = System.currentTimeMillis()
    dao.updateUserCover(userId, newCoverUrl, now)
  }

  suspend fun removeCoverPhoto(userId: String) {
    val now = System.currentTimeMillis()
    dao.updateUserCover(userId, "", now)
  }

  // --- Posts ---
  val allPosts: Flow<List<Post>> = dao.getAllPosts().map { list ->
    list.map { it.toDomain() }
  }

  val savedPosts: Flow<List<Post>> = dao.getSavedPosts().map { list ->
    list.map { it.toDomain() }
  }

  fun getPostsByUser(userId: String): Flow<List<Post>> = dao.getPostsByAuthor(userId).map { list ->
    list.map { it.toDomain() }
  }

  suspend fun createPost(
    author: User,
    content: String,
    mediaUrls: List<String>,
    feeling: String?,
    audience: PostAudience
  ) {
    val newPost = PostEntity(
      id = "post_" + UUID.randomUUID().toString().take(8),
      authorId = author.id,
      authorName = author.fullName,
      authorUsername = author.username,
      authorAvatar = author.avatarUrl,
      isAuthorVerified = author.isVerified,
      timestamp = System.currentTimeMillis(),
      content = content,
      mediaUrlsString = mediaUrls.joinToString(","),
      feelingOrActivity = feeling,
      audience = audience.label,
      likesCount = 0,
      commentsCount = 0,
      sharesCount = 0,
      myReaction = null,
      isSaved = false
    )
    dao.insertPost(newPost)
  }

  suspend fun deletePost(postId: String) {
    dao.deletePostById(postId)
  }

  suspend fun setReaction(postId: String, reaction: ReactionType?) {
    val post = dao.getPostById(postId).first() ?: return
    val hadPreviousReaction = post.myReaction != null
    val isSameReaction = post.myReaction == reaction?.name

    val newReaction = if (isSameReaction) null else reaction?.name
    val likeDelta = when {
      isSameReaction -> -1 // removed
      hadPreviousReaction -> 0 // changed reaction
      reaction != null -> 1 // new reaction
      else -> 0
    }

    dao.updatePost(
      post.copy(
        myReaction = newReaction,
        likesCount = (post.likesCount + likeDelta).coerceAtLeast(0)
      )
    )
  }

  suspend fun toggleSavePost(postId: String) {
    val post = dao.getPostById(postId).first() ?: return
    dao.updatePost(post.copy(isSaved = !post.isSaved))
  }

  suspend fun sharePost(postId: String) {
    val post = dao.getPostById(postId).first() ?: return
    dao.updatePost(post.copy(sharesCount = post.sharesCount + 1))
  }

  // --- Comments ---
  fun getComments(postId: String): Flow<List<Comment>> = dao.getCommentsForPost(postId).map { list ->
    list.map { it.toDomain() }
  }

  suspend fun addComment(postId: String, author: User, content: String) {
    val newComment = CommentEntity(
      id = "c_" + UUID.randomUUID().toString().take(8),
      postId = postId,
      authorId = author.id,
      authorName = author.fullName,
      authorAvatar = author.avatarUrl,
      isAuthorVerified = author.isVerified,
      content = content,
      timestamp = System.currentTimeMillis(),
      likesCount = 0,
      isLiked = false
    )
    dao.insertComment(newComment)

    val post = dao.getPostById(postId).first()
    if (post != null) {
      dao.updatePost(post.copy(commentsCount = post.commentsCount + 1))
    }
  }

  suspend fun deleteComment(commentId: String, postId: String) {
    dao.deleteComment(commentId)
    val post = dao.getPostById(postId).first()
    if (post != null) {
      dao.updatePost(post.copy(commentsCount = (post.commentsCount - 1).coerceAtLeast(0)))
    }
  }

  // --- Stories ---
  val activeStories: Flow<List<Story>> = dao.getActiveStories(System.currentTimeMillis()).map { list ->
    list.map { it.toDomain() }
  }

  suspend fun createStory(
    user: User,
    text: String,
    mediaUrl: String?,
    gradientIndex: Int
  ) {
    val now = System.currentTimeMillis()
    val newStory = StoryEntity(
      id = "story_" + UUID.randomUUID().toString().take(8),
      userId = user.id,
      userName = user.fullName,
      userAvatar = user.avatarUrl,
      isUserVerified = user.isVerified,
      mediaUrl = mediaUrl,
      textOverlay = text,
      backgroundGradientIndex = gradientIndex,
      timestamp = now,
      expiresAt = now + (24 * 60 * 60 * 1000L), // 24 hours expiry
      viewsCount = 0,
      isViewed = false
    )
    dao.insertStory(newStory)
  }

  suspend fun markStoryViewed(storyId: String) {
    dao.markStoryViewed(storyId)
  }

  // --- Reels ---
  val allReels: Flow<List<Reel>> = dao.getAllReels().map { list ->
    list.map { it.toDomain() }
  }

  suspend fun toggleReelLike(reelId: String) {
    val reel = dao.getAllReels().first().find { it.id == reelId } ?: return
    val newLiked = !reel.isLiked
    val delta = if (newLiked) 1 else -1
    dao.updateReel(
      reel.copy(
        isLiked = newLiked,
        likesCount = (reel.likesCount + delta).coerceAtLeast(0)
      )
    )
  }

  suspend fun toggleReelSave(reelId: String) {
    val reel = dao.getAllReels().first().find { it.id == reelId } ?: return
    dao.updateReel(reel.copy(isSaved = !reel.isSaved))
  }

  suspend fun toggleReelFollow(reelId: String) {
    val reel = dao.getAllReels().first().find { it.id == reelId } ?: return
    dao.updateReel(reel.copy(isFollowing = !reel.isFollowing))
  }

  // --- Conversations & Messages ---
  val conversations: Flow<List<Conversation>> = dao.getAllConversations().map { list ->
    list.map { it.toDomain() }
  }

  fun getMessages(convId: String): Flow<List<Message>> = dao.getMessagesForConversation(convId).map { list ->
    list.map { it.toDomain() }
  }

  suspend fun sendMessage(convId: String, text: String, mediaUrl: String? = null) {
    val now = System.currentTimeMillis()
    val myMsg = MessageEntity(
      id = "m_" + UUID.randomUUID().toString().take(8),
      conversationId = convId,
      senderId = "user_me",
      text = text,
      mediaUrl = mediaUrl,
      timestamp = now,
      isSeen = true,
      isMine = true
    )
    dao.insertMessage(myMsg)

    // Update conversation last message
    val conv = dao.getAllConversations().first().find { it.id == convId }
    if (conv != null) {
      dao.insertConversation(
        conv.copy(
          lastMessage = text,
          lastMessageTimestamp = now
        )
      )
    }

    // Auto-reply simulation for real-time feel!
    scope.launch {
      delay(1800)
      val replyReplies = listOf(
        "Sounds wonderful! Looking forward to it.",
        "Totally agree! That makes a lot of sense.",
        "Just saw this! Awesome work on Sociva ✨",
        "Haha exactly what I was thinking! 🙌",
        "Thanks for sharing! Let me test that out."
      )
      val replyText = replyReplies.random()
      val replyTime = System.currentTimeMillis()
      val replyMsg = MessageEntity(
        id = "m_" + UUID.randomUUID().toString().take(8),
        conversationId = convId,
        senderId = conv?.participantId ?: "other",
        text = replyText,
        mediaUrl = null,
        timestamp = replyTime,
        isSeen = true,
        isMine = false
      )
      dao.insertMessage(replyMsg)
      if (conv != null) {
        dao.insertConversation(
          conv.copy(
            lastMessage = replyText,
            lastMessageTimestamp = replyTime
          )
        )
      }
    }
  }

  suspend fun deleteMessage(messageId: String) {
    dao.deleteMessage(messageId)
  }

  // --- Notifications ---
  fun getNotifications(recipientId: String = "user_me"): Flow<List<NotificationItem>> =
    dao.getNotificationsForRecipient(recipientId).map { list -> list.map { it.toDomain() } }

  val notifications: Flow<List<NotificationItem>> = getNotifications("user_me")

  suspend fun markNotificationRead(id: String) {
    dao.markNotificationAsRead(id)
  }

  suspend fun markAllNotificationsRead() {
    dao.markAllNotificationsAsRead()
  }

  // --- Friend Requests & Relationships ---
  fun getIncomingFriendRequests(receiverId: String = "user_me"): Flow<List<FriendRequestItem>> =
    dao.getIncomingFriendRequests(receiverId).map { list -> list.map { it.toDomain() } }

  fun getSentFriendRequests(senderId: String = "user_me"): Flow<List<FriendRequestItem>> =
    dao.getSentFriendRequests(senderId).map { list -> list.map { it.toDomain() } }

  val friendRequests: Flow<List<FriendRequestItem>> = getIncomingFriendRequests("user_me")

  fun getFriendStatusFlow(currentUserId: String, targetUserId: String): Flow<FriendStatus> {
    if (currentUserId == targetUserId) {
      return flowOf(FriendStatus.NONE)
    }
    return combine(
      dao.hasFriendshipFlow(currentUserId, targetUserId),
      dao.getPendingRequestBetween(currentUserId, targetUserId)
    ) { hasFriendship, pendingReq ->
      when {
        hasFriendship -> FriendStatus.FRIENDS
        pendingReq == null -> FriendStatus.NONE
        pendingReq.senderId == currentUserId && pendingReq.receiverId == targetUserId -> FriendStatus.REQUEST_SENT
        pendingReq.senderId == targetUserId && pendingReq.receiverId == currentUserId -> FriendStatus.REQUEST_RECEIVED
        else -> FriendStatus.NONE
      }
    }
  }

  fun isFollowingFlow(followerId: String, followingId: String): Flow<Boolean> =
    dao.isFollowingFlow(followerId, followingId)

  suspend fun sendFriendRequest(senderId: String, targetUserId: String): Boolean {
    if (senderId == targetUserId) return false
    if (dao.hasFriendship(senderId, targetUserId)) return false
    val existingReq = dao.getPendingRequest(senderId, targetUserId)
    if (existingReq != null) return false

    // If target user already sent a request, auto-accept
    val reverseReq = dao.getPendingRequest(targetUserId, senderId)
    if (reverseReq != null) {
      return acceptFriendRequest(reverseReq.id, senderId)
    }

    val senderUser = dao.getUserById(senderId).first() ?: return false
    val targetUser = dao.getUserById(targetUserId).first() ?: return false
    val now = System.currentTimeMillis()

    // 1. Create pending request (DO NOT create friendship record!)
    val requestId = "req_" + UUID.randomUUID().toString().take(8)
    val request = FriendRequestEntity(
      id = requestId,
      senderId = senderId,
      receiverId = targetUserId,
      status = "pending",
      createdAt = now,
      updatedAt = now,
      senderName = senderUser.fullName,
      senderUsername = senderUser.username,
      senderAvatar = senderUser.avatarUrl,
      receiverName = targetUser.fullName,
      receiverUsername = targetUser.username,
      receiverAvatar = targetUser.avatarUrl,
      mutualFriendsCount = 5
    )
    dao.insertFriendRequest(request)

    // 2. User A automatically follows User B
    followUser(senderId, targetUserId)

    // 3. User B receives notification: "User A sent you a friend request."
    dao.insertNotification(
      NotificationEntity(
        id = "notif_" + UUID.randomUUID().toString().take(8),
        type = "FRIEND_REQUEST",
        actorName = senderUser.fullName,
        actorAvatar = senderUser.avatarUrl,
        isActorVerified = senderUser.isVerified,
        messageSnippet = "${senderUser.fullName} sent you a friend request.",
        timestamp = now,
        isRead = false,
        recipientId = targetUserId
      )
    )

    return true
  }

  suspend fun acceptFriendRequest(requestId: String, currentUserId: String = "user_me"): Boolean {
    val request = dao.getFriendRequestById(requestId) ?: return false
    if (request.status != "pending") return false

    val senderId = request.senderId
    val receiverId = request.receiverId
    val now = System.currentTimeMillis()

    // 1. Remove pending friend request
    dao.deleteFriendRequest(requestId)

    // 2. Create friendship between User A and User B
    if (!dao.hasFriendship(senderId, receiverId)) {
      dao.insertFriendships(
        listOf(
          FriendshipEntity(
            id = "fr_" + UUID.randomUUID().toString().take(8),
            userId = senderId,
            friendId = receiverId,
            createdAt = now
          ),
          FriendshipEntity(
            id = "fr_" + UUID.randomUUID().toString().take(8),
            userId = receiverId,
            friendId = senderId,
            createdAt = now
          )
        )
      )
    }

    // 3. Update both users' friend counts
    val senderUser = dao.getUserById(senderId).first()
    val receiverUser = dao.getUserById(receiverId).first()
    val senderFriendsCount = dao.getFriendsCount(senderId)
    val receiverFriendsCount = dao.getFriendsCount(receiverId)

    if (senderUser != null) {
      dao.updateUser(
        senderUser.copy(
          friendsCount = senderFriendsCount,
          isFriend = if (receiverId == "user_me") true else senderUser.isFriend
        )
      )
    }
    if (receiverUser != null) {
      dao.updateUser(
        receiverUser.copy(
          friendsCount = receiverFriendsCount,
          isFriend = if (senderId == "user_me") true else receiverUser.isFriend
        )
      )
    }

    // 4. Send notification to User A: "User B accepted your friend request."
    val receiverName = receiverUser?.fullName ?: request.receiverName
    val receiverAvatar = receiverUser?.avatarUrl ?: request.receiverAvatar
    dao.insertNotification(
      NotificationEntity(
        id = "notif_" + UUID.randomUUID().toString().take(8),
        type = "ACCEPT_REQUEST",
        actorName = receiverName,
        actorAvatar = receiverAvatar,
        isActorVerified = receiverUser?.isVerified ?: false,
        messageSnippet = "$receiverName accepted your friend request.",
        timestamp = now,
        isRead = false,
        recipientId = senderId
      )
    )

    return true
  }

  suspend fun rejectFriendRequest(requestId: String): Boolean {
    val request = dao.getFriendRequestById(requestId) ?: return false
    // DO NOT create friendship
    // Remove pending friend request
    dao.deleteFriendRequest(requestId)
    // User A remains following User B
    return true
  }

  suspend fun cancelFriendRequest(senderId: String, targetUserId: String): Boolean {
    val req = dao.getPendingRequest(senderId, targetUserId) ?: return false
    // Remove pending friend request
    dao.deleteFriendRequest(req.id)
    // User A continues following User B
    // User A is NOT a friend
    return true
  }

  suspend fun cancelFriendRequestById(requestId: String): Boolean {
    val req = dao.getFriendRequestById(requestId) ?: return false
    dao.deleteFriendRequest(req.id)
    return true
  }

  // --- Pages ---
  val pages: Flow<List<SocivaPage>> = dao.getAllPages().map { list ->
    list.map { it.toDomain() }
  }

  suspend fun togglePageLike(pageId: String) {
    val page = dao.getAllPages().first().find { it.id == pageId } ?: return
    val newLiked = !page.isLiked
    val delta = if (newLiked) 1 else -1
    dao.updatePage(
      page.copy(
        isLiked = newLiked,
        followersCount = (page.followersCount + delta).coerceAtLeast(0)
      )
    )
  }

  suspend fun createPage(name: String, category: String, description: String) {
    val newPage = PageEntity(
      id = "page_" + UUID.randomUUID().toString().take(8),
      name = name,
      category = category,
      description = description,
      avatarUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=300&h=300&fit=crop",
      coverUrl = "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?w=900&h=300&fit=crop",
      followersCount = 1,
      isLiked = true,
      isAdmin = true
    )
    dao.insertPage(newPage)
  }

  // --- Groups ---
  val groups: Flow<List<SocivaGroup>> = dao.getAllGroups().map { list ->
    list.map { it.toDomain() }
  }

  suspend fun toggleGroupJoin(groupId: String) {
    val group = dao.getAllGroups().first().find { it.id == groupId } ?: return
    val newJoined = !group.isJoined
    val delta = if (newJoined) 1 else -1
    dao.updateGroup(
      group.copy(
        isJoined = newJoined,
        membersCount = (group.membersCount + delta).coerceAtLeast(0)
      )
    )
  }

  suspend fun createGroup(name: String, privacy: String, description: String) {
    val newGroup = GroupEntity(
      id = "grp_" + UUID.randomUUID().toString().take(8),
      name = name,
      privacy = privacy,
      description = description,
      avatarUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=300&h=300&fit=crop",
      membersCount = 1,
      isJoined = true,
      role = "Admin"
    )
    dao.insertGroup(newGroup)
  }

  // --- Reports & Moderation ---
  val reports: Flow<List<ReportItem>> = dao.getAllReports().map { list ->
    list.map {
      ReportItem(
        id = it.id,
        targetType = it.targetType,
        targetId = it.targetId,
        targetTitle = it.targetTitle,
        reason = it.reason,
        reportedBy = it.reportedBy,
        timestamp = it.timestamp,
        status = it.status
      )
    }
  }

  suspend fun submitReport(targetType: String, targetId: String, title: String, reason: String) {
    val newReport = ReportEntity(
      id = "rep_" + UUID.randomUUID().toString().take(8),
      targetType = targetType,
      targetId = targetId,
      targetTitle = title,
      reason = reason,
      reportedBy = "alexrivera",
      timestamp = System.currentTimeMillis(),
      status = "Pending"
    )
    dao.insertReport(newReport)
  }

  suspend fun resolveReport(reportId: String) {
    dao.resolveReport(reportId)
  }

  suspend fun deleteReport(reportId: String) {
    dao.deleteReport(reportId)
  }
}

// Entity to Domain mappers
private fun UserEntity.toDomain() = User(
  id = id,
  username = username,
  fullName = fullName,
  avatarUrl = avatarUrl,
  coverUrl = coverUrl,
  bio = bio,
  isVerified = isVerified,
  followersCount = followersCount,
  followingCount = followingCount,
  friendsCount = friendsCount,
  postsCount = postsCount,
  work = work,
  education = education,
  location = location,
  joinedDate = joinedDate,
  isOnline = isOnline,
  isFriend = isFriend,
  isFollowing = isFollowing,
  profilePictureUpdatedAt = profilePictureUpdatedAt,
  coverPhotoUpdatedAt = coverPhotoUpdatedAt
)

private fun PostEntity.toDomain() = Post(
  id = id,
  authorId = authorId,
  authorName = authorName,
  authorUsername = authorUsername,
  authorAvatar = authorAvatar,
  isAuthorVerified = isAuthorVerified,
  timestamp = timestamp,
  content = content,
  mediaUrls = if (mediaUrlsString.isBlank()) emptyList() else mediaUrlsString.split(","),
  feelingOrActivity = feelingOrActivity,
  audience = when (audience) {
    "Friends" -> PostAudience.FRIENDS
    "Only Me" -> PostAudience.ONLY_ME
    else -> PostAudience.PUBLIC
  },
  likesCount = likesCount,
  commentsCount = commentsCount,
  sharesCount = sharesCount,
  myReaction = myReaction?.let {
    try { ReactionType.valueOf(it) } catch (e: Exception) { null }
  },
  isSaved = isSaved
)

private fun CommentEntity.toDomain() = Comment(
  id = id,
  postId = postId,
  authorId = authorId,
  authorName = authorName,
  authorAvatar = authorAvatar,
  isAuthorVerified = isAuthorVerified,
  content = content,
  timestamp = timestamp,
  likesCount = likesCount,
  isLiked = isLiked
)

private fun StoryEntity.toDomain() = Story(
  id = id,
  userId = userId,
  userName = userName,
  userAvatar = userAvatar,
  isUserVerified = isUserVerified,
  mediaUrl = mediaUrl,
  textOverlay = textOverlay,
  backgroundGradientIndex = backgroundGradientIndex,
  timestamp = timestamp,
  expiresAt = expiresAt,
  viewsCount = viewsCount,
  isViewed = isViewed
)

private fun ReelEntity.toDomain() = Reel(
  id = id,
  creatorId = creatorId,
  creatorName = creatorName,
  creatorUsername = creatorUsername,
  creatorAvatar = creatorAvatar,
  isCreatorVerified = isCreatorVerified,
  videoThumbnail = videoThumbnail,
  caption = caption,
  audioTitle = audioTitle,
  likesCount = likesCount,
  commentsCount = commentsCount,
  sharesCount = sharesCount,
  isLiked = isLiked,
  isSaved = isSaved,
  isFollowing = isFollowing
)

private fun ConversationEntity.toDomain() = Conversation(
  id = id,
  participantId = participantId,
  participantName = participantName,
  participantUsername = participantUsername,
  participantAvatar = participantAvatar,
  isParticipantVerified = isParticipantVerified,
  lastMessage = lastMessage,
  lastMessageTimestamp = lastMessageTimestamp,
  unreadCount = unreadCount,
  isOnline = isOnline
)

private fun MessageEntity.toDomain() = Message(
  id = id,
  conversationId = conversationId,
  senderId = senderId,
  text = text,
  mediaUrl = mediaUrl,
  timestamp = timestamp,
  isSeen = isSeen,
  isMine = isMine
)

private fun NotificationEntity.toDomain() = NotificationItem(
  id = id,
  type = try { NotificationType.valueOf(type) } catch (e: Exception) { NotificationType.LIKE },
  actorName = actorName,
  actorAvatar = actorAvatar,
  isActorVerified = isActorVerified,
  messageSnippet = messageSnippet,
  timestamp = timestamp,
  isRead = isRead,
  targetPostId = targetPostId
)

private fun FriendRequestEntity.toDomain() = FriendRequestItem(
  id = id,
  senderId = senderId,
  receiverId = receiverId,
  status = status,
  senderName = senderName,
  senderUsername = senderUsername,
  senderAvatar = senderAvatar,
  receiverName = receiverName,
  receiverUsername = receiverUsername,
  receiverAvatar = receiverAvatar,
  mutualFriendsCount = mutualFriendsCount,
  createdAt = createdAt
)

private fun PageEntity.toDomain() = SocivaPage(
  id = id,
  name = name,
  category = category,
  description = description,
  avatarUrl = avatarUrl,
  coverUrl = coverUrl,
  followersCount = followersCount,
  isLiked = isLiked,
  isAdmin = isAdmin
)

private fun GroupEntity.toDomain() = SocivaGroup(
  id = id,
  name = name,
  privacy = privacy,
  description = description,
  avatarUrl = avatarUrl,
  membersCount = membersCount,
  isJoined = isJoined,
  role = role
)
