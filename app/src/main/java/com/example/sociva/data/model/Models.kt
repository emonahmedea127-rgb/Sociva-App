package com.example.sociva.data.model

enum class PostAudience(val label: String) {
  PUBLIC("Public"),
  FRIENDS("Friends"),
  ONLY_ME("Only Me")
}

enum class ReactionType(val emoji: String, val label: String, val colorHex: Long) {
  LIKE("👍", "Like", 0xFF2563EB),
  LOVE("❤️", "Love", 0xFFEF4444),
  HAHA("😆", "Haha", 0xFFF59E0B),
  WOW("😮", "Wow", 0xFFF59E0B),
  SAD("😢", "Sad", 0xFFF59E0B),
  ANGRY("😡", "Angry", 0xFFDC2626)
}

enum class NotificationType(val title: String) {
  LIKE("liked your post"),
  COMMENT("commented on your post"),
  SHARE("shared your post"),
  FRIEND_REQUEST("sent you a friend request"),
  ACCEPT_REQUEST("accepted your friend request"),
  FOLLOW("started following you"),
  MESSAGE("sent you a message"),
  MENTION("mentioned you in a comment"),
  STORY_REACTION("reacted to your story"),
  COMMENT_REACTION("reacted to your comment"),
  COMMENT_REPLY("replied to your comment"),
  RELATIONSHIP_REQUEST("wants to list you as their partner"),
  RELATIONSHIP_ACCEPTED("accepted your relationship request"),
  RELATIONSHIP_DECLINED("declined your relationship request"),
  TAG("tagged you in a post")
}

data class TaggedUser(
  val id: String,
  val fullName: String,
  val username: String,
  val avatarUrl: String = ""
)

data class StructuredLocation(
  val city: String = "",
  val region: String = "",
  val country: String = "",
  val countryCode: String = "",
  val latitude: Double = 0.0,
  val longitude: Double = 0.0
) {
  fun format(): String {
    val parts = listOf(city, region, country).filter { it.isNotBlank() }.distinct()
    return parts.joinToString(", ")
  }
}

data class RelationshipItem(
  val id: String,
  val requesterId: String,
  val receiverId: String,
  val relationshipType: String,
  val customText: String? = null,
  val status: String = "pending", // "pending", "accepted", "declined", "cancelled"
  val privacy: String = "Public",
  val requesterName: String = "",
  val requesterAvatar: String = "",
  val receiverName: String = "",
  val receiverAvatar: String = "",
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
)

data class User(
  val id: String,
  val username: String,
  val fullName: String,
  val avatarUrl: String,
  val coverUrl: String,
  val bio: String,
  val isVerified: Boolean = false,
  val followersCount: Int = 0,
  val followingCount: Int = 0,
  val friendsCount: Int = 0,
  val postsCount: Int = 0,
  val work: String = "",
  val education: String = "",
  val location: String = "",
  val joinedDate: String = "September 2024",
  val isOnline: Boolean = false,
  val lastActiveAt: Long = 0L,
  val isFriend: Boolean = false,
  val isFollowing: Boolean = false,
  val profilePictureUpdatedAt: Long = 0L,
  val coverPhotoUpdatedAt: Long = 0L,
  // Section A: Basic Information
  val firstName: String = "",
  val lastName: String = "",
  val pronouns: String = "",
  val nickname: String = "",
  val otherNames: String = "",
  // Section B: Personal Information
  val dateOfBirth: String = "",
  val gender: String = "",
  val interestedIn: String = "",
  val hometown: String = "",
  val currentCity: String = "",
  val country: String = "",
  val currentRegion: String = "",
  val currentCountryCode: String = "",
  val currentLatitude: Double? = null,
  val currentLongitude: Double? = null,
  val hometownRegion: String = "",
  val hometownCountryCode: String = "",
  val hometownLatitude: Double? = null,
  val hometownLongitude: Double? = null,
  val countryCode: String = "",
  // Work Information
  val workplace: String = "",
  val workPosition: String = "",
  val workStartDate: String = "",
  val workEndDate: String = "",
  // Education Information
  val school: String = "",
  val college: String = "",
  val university: String = "",
  val degree: String = "",
  val fieldOfStudy: String = "",
  val graduationYear: String = "",
  // Contact Information
  val website: String = "",
  val email: String = "",
  val phone: String = "",
  // Relationship Status & Partner
  val relationshipStatus: String = "",
  val relationshipPartnerId: String? = null,
  val relationshipPartnerName: String? = null,
  val customRelationshipText: String? = null,
  // Privacy Controls
  val birthdayPrivacy: String = "Public",
  val currentCityPrivacy: String = "Public",
  val hometownPrivacy: String = "Public",
  val relationshipPrivacy: String = "Public",
  val emailPrivacy: String = "Friends",
  val taggingPermission: String = "ALLOW_ANYONE",
  val reviewTagsBeforeAppearing: Boolean = false
)

enum class PostType {
  NORMAL,
  PROFILE_PICTURE_UPDATE,
  COVER_PHOTO_UPDATE,
  SHARED_POST
}

data class SharedPostPreview(
  val id: String,
  val authorId: String,
  val authorName: String,
  val authorUsername: String,
  val authorAvatar: String,
  val isAuthorVerified: Boolean = false,
  val timestamp: Long,
  val content: String,
  val mediaUrls: List<String> = emptyList(),
  val feelingOrActivity: String? = null,
  val postType: PostType = PostType.NORMAL,
  val actionContextText: String? = null,
  val audience: PostAudience = PostAudience.PUBLIC,
  val isUnavailable: Boolean = false
)

data class Post(
  val id: String,
  val authorId: String,
  val authorName: String,
  val authorUsername: String,
  val authorAvatar: String,
  val isAuthorVerified: Boolean = false,
  val timestamp: Long,
  val content: String,
  val mediaUrls: List<String> = emptyList(),
  val feelingOrActivity: String? = null,
  val audience: PostAudience = PostAudience.PUBLIC,
  val likesCount: Int = 0,
  val commentsCount: Int = 0,
  val sharesCount: Int = 0,
  val myReaction: ReactionType? = null,
  val isSaved: Boolean = false,
  val taggedUsers: List<TaggedUser> = emptyList(),
  val topReactionEmojis: List<String> = emptyList(),
  val reactionTypeCounts: Map<ReactionType, Int> = emptyMap(),
  val postType: PostType = PostType.NORMAL,
  val originalPostId: String? = null,
  val actionContextText: String? = null,
  val sharedPost: SharedPostPreview? = null
)

data class Comment(
  val id: String,
  val postId: String,
  val authorId: String,
  val authorName: String,
  val authorAvatar: String,
  val isAuthorVerified: Boolean = false,
  val content: String,
  val timestamp: Long,
  val updatedAt: Long = timestamp,
  val parentCommentId: String? = null,
  val replyToAuthorName: String? = null,
  val likesCount: Int = 0,
  val isLiked: Boolean = false,
  val myReaction: ReactionType? = null,
  val reactionsCount: Int = likesCount,
  val repliesCount: Int = 0,
  val replies: List<Comment> = emptyList()
)

data class CommentReaction(
  val id: String,
  val commentId: String,
  val userId: String,
  val reactionType: ReactionType,
  val createdAt: Long
)

data class PostReaction(
  val id: String,
  val postId: String,
  val userId: String,
  val reactionType: ReactionType,
  val createdAt: Long
)

enum class ReactionRelationshipStatus {
  YOU,
  FRIEND,
  REQUEST_SENT,
  REQUEST_RECEIVED,
  FOLLOWING,
  CAN_ADD_FRIEND
}

data class PostReactionUser(
  val reactionId: String,
  val postId: String,
  val user: User,
  val reactionType: ReactionType,
  val createdAt: Long,
  val relationshipStatus: ReactionRelationshipStatus,
  val incomingRequestId: String? = null
)

data class Story(
  val id: String,
  val userId: String,
  val userName: String,
  val userAvatar: String,
  val isUserVerified: Boolean = false,
  val mediaUrl: String? = null,
  val textOverlay: String = "",
  val backgroundGradientIndex: Int = 0,
  val timestamp: Long,
  val expiresAt: Long,
  val viewsCount: Int = 12,
  val isViewed: Boolean = false
)

data class Reel(
  val id: String,
  val creatorId: String,
  val creatorName: String,
  val creatorUsername: String,
  val creatorAvatar: String,
  val isCreatorVerified: Boolean = false,
  val videoThumbnail: String,
  val caption: String,
  val audioTitle: String,
  val likesCount: Int = 0,
  val commentsCount: Int = 0,
  val sharesCount: Int = 0,
  val isLiked: Boolean = false,
  val isSaved: Boolean = false,
  val isFollowing: Boolean = false
)

data class Conversation(
  val id: String,
  val participantId: String,
  val participantName: String,
  val participantUsername: String,
  val participantAvatar: String,
  val isParticipantVerified: Boolean = false,
  val lastMessage: String,
  val lastMessageTimestamp: Long,
  val unreadCount: Int = 0,
  val isOnline: Boolean = false,
  val lastActiveAt: Long = 0L
)

data class Message(
  val id: String,
  val conversationId: String,
  val senderId: String,
  val receiverId: String = "",
  val messageType: String = "TEXT",
  val text: String,
  val mediaUrl: String? = null,
  val timestamp: Long,
  val isSeen: Boolean = false,
  val isDeleted: Boolean = false,
  val isMine: Boolean = true
)

data class NotificationItem(
  val id: String,
  val type: NotificationType,
  val actorName: String,
  val actorAvatar: String,
  val isActorVerified: Boolean = false,
  val messageSnippet: String,
  val timestamp: Long,
  val isRead: Boolean = false,
  val targetPostId: String? = null,
  val actionData: String? = null,
  val senderId: String = ""
)

enum class FriendStatus {
  NONE,
  REQUEST_SENT,
  REQUEST_RECEIVED,
  FRIENDS
}

data class FriendRequestItem(
  val id: String,
  val senderId: String,
  val receiverId: String,
  val status: String = "pending", // "pending", "accepted", "rejected", "cancelled"
  val senderName: String,
  val senderUsername: String,
  val senderAvatar: String,
  val receiverName: String = "",
  val receiverUsername: String = "",
  val receiverAvatar: String = "",
  val mutualFriendsCount: Int = 0,
  val createdAt: Long = System.currentTimeMillis()
) {
  // Backward compatibility accessors
  val userId: String get() = senderId
  val fullName: String get() = senderName
  val username: String get() = senderUsername
  val avatarUrl: String get() = senderAvatar
  val timestamp: Long get() = createdAt
}

data class Friendship(
  val id: String,
  val userId: String,
  val friendId: String,
  val createdAt: Long
)

data class Follow(
  val id: String,
  val followerId: String,
  val followingId: String,
  val createdAt: Long
)

data class SocivaPage(
  val id: String,
  val name: String,
  val category: String,
  val description: String,
  val avatarUrl: String,
  val coverUrl: String,
  val followersCount: Int,
  val isLiked: Boolean = false,
  val isAdmin: Boolean = false
)

data class SocivaGroup(
  val id: String,
  val name: String,
  val privacy: String, // "Public group" or "Private group"
  val description: String,
  val avatarUrl: String,
  val membersCount: Int,
  val isJoined: Boolean = false,
  val role: String = "Member"
)

data class ReportItem(
  val id: String,
  val targetType: String, // "Post" or "User"
  val targetId: String,
  val targetTitle: String,
  val reason: String,
  val reportedBy: String,
  val timestamp: Long,
  val status: String = "Pending" // "Pending" or "Resolved"
)

data class UserSettings(
  val userId: String = "user_me",
  val twoFactorEnabled: Boolean = false,
  val twoFactorMethod: String = "AUTHENTICATOR", // "AUTHENTICATOR" or "SMS"
  val profileVisibility: String = "Public", // "Public", "Friends", "Only Me"
  val darkTheme: Boolean = false,
  val dataSaver: Boolean = false,
  val pushNotifications: Boolean = true,
  val inAppSounds: Boolean = true,
  val language: String = "English",
  val passwordLastUpdated: Long = System.currentTimeMillis() - (90L * 24 * 60 * 60 * 1000),
  val profileViewHistoryEnabled: Boolean = true
)

data class ProfileViewStats(
  val todayCount: Int = 0,
  val thisWeekCount: Int = 0,
  val thisMonthCount: Int = 0,
  val totalCount: Int = 0,
  val unseenCount: Int = 0
)

data class ProfileVisitorItem(
  val viewId: String,
  val user: User,
  val viewedAt: Long,
  val isSeen: Boolean,
  val friendStatus: FriendStatus = FriendStatus.NONE,
  val isFollowing: Boolean = false,
  val mutualFriendsCount: Int = 0
)

data class BlockedUser(
  val id: String,
  val blockerId: String,
  val blockedId: String,
  val blockedUser: User,
  val createdAt: Long = System.currentTimeMillis()
)

data class RelationshipContext(
  val friendIds: Set<String>,
  val sentReqTargetIds: Set<String>,
  val incomingReqMap: Map<String, com.example.sociva.data.local.FriendRequestEntity>,
  val followingIds: Set<String>
)

enum class AnalyticsTimeWindow(val label: String) {
  LAST_7_DAYS("7 Days"),
  LAST_30_DAYS("30 Days"),
  ALL_TIME("All Time")
}

data class PostAnalytics(
  val postId: String,
  val totalViews: Int = 0,
  val uniqueViewers: Int = 0,
  val reactionsCount: Int = 0,
  val commentsCount: Int = 0,
  val repliesCount: Int = 0,
  val sharesCount: Int = 0,
  val savesCount: Int = 0,
  val profileVisitsFromPost: Int = 0,
  val engagementRate: Double = 0.0,
  val reactionBreakdown: Map<String, Int> = emptyMap(),
  val recentViewers: List<User> = emptyList(),
  val viewsOverTime: List<Pair<String, Int>> = emptyList()
)

data class ProfileAnalytics(
  val userId: String,
  val timeWindow: AnalyticsTimeWindow = AnalyticsTimeWindow.LAST_7_DAYS,
  val totalPostViews: Int = 0,
  val totalReactions: Int = 0,
  val totalComments: Int = 0,
  val totalShares: Int = 0,
  val totalSaves: Int = 0,
  val profileVisits: Int = 0,
  val followersGained: Int = 0,
  val engagementRate: Double = 0.0,
  val bestPerformingPosts: List<Post> = emptyList(),
  val dailyViewsTrend: List<Pair<String, Int>> = emptyList()
)
