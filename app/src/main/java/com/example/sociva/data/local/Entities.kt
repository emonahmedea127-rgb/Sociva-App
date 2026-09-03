package com.example.sociva.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
  @PrimaryKey val id: String,
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

@Entity(tableName = "posts")
data class PostEntity(
  @PrimaryKey val id: String,
  val authorId: String,
  val authorName: String,
  val authorUsername: String,
  val authorAvatar: String,
  val isAuthorVerified: Boolean = false,
  val timestamp: Long,
  val content: String,
  val mediaUrlsString: String = "", // Comma-separated or empty
  val feelingOrActivity: String? = null,
  val audience: String = "Public",
  val likesCount: Int = 0,
  val commentsCount: Int = 0,
  val sharesCount: Int = 0,
  val myReaction: String? = null,
  val isSaved: Boolean = false
)

@Entity(
  tableName = "comments",
  indices = [
    Index(value = ["post_id"]),
    Index(value = ["parent_comment_id"]),
    Index(value = ["user_id"])
  ]
)
data class CommentEntity(
  @PrimaryKey val id: String,
  @ColumnInfo(name = "post_id") val postId: String,
  @ColumnInfo(name = "user_id") val authorId: String,
  val authorName: String,
  val authorAvatar: String,
  val isAuthorVerified: Boolean = false,
  val content: String,
  @ColumnInfo(name = "created_at") val timestamp: Long,
  @ColumnInfo(name = "updated_at") val updatedAt: Long = timestamp,
  @ColumnInfo(name = "parent_comment_id") val parentCommentId: String? = null,
  val likesCount: Int = 0,
  val isLiked: Boolean = false
)

@Entity(
  tableName = "comment_reactions",
  indices = [
    Index(value = ["comment_id", "user_id"], unique = true),
    Index(value = ["comment_id"]),
    Index(value = ["user_id"])
  ]
)
data class CommentReactionEntity(
  @PrimaryKey val id: String,
  @ColumnInfo(name = "comment_id") val commentId: String,
  @ColumnInfo(name = "user_id") val userId: String,
  @ColumnInfo(name = "reaction_type") val reactionType: String,
  @ColumnInfo(name = "created_at") val createdAt: Long
)

@Entity(tableName = "stories")
data class StoryEntity(
  @PrimaryKey val id: String,
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

@Entity(tableName = "reels")
data class ReelEntity(
  @PrimaryKey val id: String,
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

@Entity(tableName = "conversations")
data class ConversationEntity(
  @PrimaryKey val id: String,
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis(),
  val participantId: String = "",
  val participantName: String = "",
  val participantUsername: String = "",
  val participantAvatar: String = "",
  val isParticipantVerified: Boolean = false,
  val lastMessage: String = "",
  val lastMessageTimestamp: Long = System.currentTimeMillis(),
  val unreadCount: Int = 0,
  val isOnline: Boolean = false
)

@Entity(
  tableName = "conversation_members",
  primaryKeys = ["conversationId", "userId"],
  indices = [
    Index(value = ["conversationId"]),
    Index(value = ["userId"])
  ]
)
data class ConversationMemberEntity(
  val conversationId: String,
  val userId: String,
  val joinedAt: Long = System.currentTimeMillis(),
  val lastReadAt: Long = System.currentTimeMillis()
)

@Entity(
  tableName = "messages",
  indices = [
    Index(value = ["conversationId"]),
    Index(value = ["senderId"]),
    Index(value = ["receiverId"]),
    Index(value = ["timestamp"])
  ]
)
data class MessageEntity(
  @PrimaryKey val id: String,
  val conversationId: String,
  val senderId: String,
  val receiverId: String = "",
  val messageType: String = "TEXT",
  val text: String,
  val mediaUrl: String? = null,
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis(),
  val timestamp: Long = System.currentTimeMillis(),
  val isSeen: Boolean = false,
  val isDeleted: Boolean = false,
  val isMine: Boolean = true
)

data class ConversationWithParticipant(
  val conversationId: String,
  val lastMessage: String,
  val lastMessageTimestamp: Long,
  val participantId: String,
  val participantName: String,
  val participantUsername: String,
  val participantAvatar: String,
  val isParticipantVerified: Boolean,
  val isOnline: Boolean,
  val lastActiveAt: Long,
  val unreadCount: Int
)

@Entity(tableName = "notifications")
data class NotificationEntity(
  @PrimaryKey val id: String,
  val type: String,
  val actorName: String,
  val actorAvatar: String,
  val isActorVerified: Boolean = false,
  val messageSnippet: String,
  val timestamp: Long,
  val isRead: Boolean = false,
  val targetPostId: String? = null,
  val recipientId: String = "user_me"
)

@Entity(
  tableName = "friend_requests",
  indices = [
    Index(value = ["senderId", "receiverId"]),
    Index(value = ["receiverId", "status"]),
    Index(value = ["senderId", "status"])
  ]
)
data class FriendRequestEntity(
  @PrimaryKey val id: String,
  val senderId: String,
  val receiverId: String,
  val status: String, // "pending", "accepted", "rejected", "cancelled"
  val createdAt: Long,
  val updatedAt: Long = createdAt,
  val senderName: String = "",
  val senderUsername: String = "",
  val senderAvatar: String = "",
  val receiverName: String = "",
  val receiverUsername: String = "",
  val receiverAvatar: String = "",
  val mutualFriendsCount: Int = 0
) {
  // Backward compatibility accessors
  val userId: String get() = senderId
  val fullName: String get() = senderName
  val username: String get() = senderUsername
  val avatarUrl: String get() = senderAvatar
  val timestamp: Long get() = createdAt
}

@Entity(
  tableName = "friends",
  indices = [
    Index(value = ["userId", "friendId"], unique = true),
    Index(value = ["friendId"])
  ]
)
data class FriendshipEntity(
  @PrimaryKey val id: String,
  val userId: String,
  val friendId: String,
  val createdAt: Long
)

@Entity(
  tableName = "follows",
  indices = [
    Index(value = ["followerId", "followingId"], unique = true),
    Index(value = ["followingId"])
  ]
)
data class FollowEntity(
  @PrimaryKey val id: String,
  val followerId: String,
  val followingId: String,
  val createdAt: Long
)

@Entity(tableName = "pages")
data class PageEntity(
  @PrimaryKey val id: String,
  val name: String,
  val category: String,
  val description: String,
  val avatarUrl: String,
  val coverUrl: String,
  val followersCount: Int,
  val isLiked: Boolean = false,
  val isAdmin: Boolean = false
)

@Entity(tableName = "groups")
data class GroupEntity(
  @PrimaryKey val id: String,
  val name: String,
  val privacy: String,
  val description: String,
  val avatarUrl: String,
  val membersCount: Int,
  val isJoined: Boolean = false,
  val role: String = "Member"
)

@Entity(tableName = "reports")
data class ReportEntity(
  @PrimaryKey val id: String,
  val targetType: String,
  val targetId: String,
  val targetTitle: String,
  val reason: String,
  val reportedBy: String,
  val timestamp: Long,
  val status: String = "Pending"
)
