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
  MENTION("mentioned you in a post"),
  STORY_REACTION("reacted to your story"),
  COMMENT_REACTION("reacted to your comment"),
  COMMENT_REPLY("replied to your comment")
}

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
  val coverPhotoUpdatedAt: Long = 0L
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
  val isSaved: Boolean = false
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
  val targetPostId: String? = null
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
  val language: String = "English",
  val appearance: String = "System", // Light, Dark, System
  val audienceDefault: String = "Public",
  val twoFactorEnabled: Boolean = false,
  val dataSaverEnabled: Boolean = false
)
