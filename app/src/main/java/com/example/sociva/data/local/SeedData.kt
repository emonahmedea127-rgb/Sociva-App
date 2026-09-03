package com.example.sociva.data.local

object SeedData {
  val now = System.currentTimeMillis()

  val users = listOf(
    UserEntity(
      id = "user_me",
      username = "alexrivera",
      fullName = "Alex Rivera",
      avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&h=300&fit=crop",
      coverUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=1200&h=400&fit=crop",
      bio = "Product Designer & Android builder. Creating digital spaces where people belong. ☕ & 🎧",
      isVerified = true,
      followersCount = 4280,
      followingCount = 385,
      friendsCount = 512,
      postsCount = 48,
      work = "Design Lead at Studio Pulse",
      education = "Stanford University - Design Systems",
      location = "San Francisco, California",
      joinedDate = "March 2023",
      isOnline = true,
      isFriend = true,
      isFollowing = true
    ),
    UserEntity(
      id = "user_sarah",
      username = "sarahchen",
      fullName = "Sarah Chen",
      avatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=300&h=300&fit=crop",
      coverUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1200&h=400&fit=crop",
      bio = "AI Research scientist & landscape photographer. Capturing the Pacific Northwest. 🌲📸",
      isVerified = true,
      followersCount = 8920,
      followingCount = 420,
      friendsCount = 340,
      postsCount = 86,
      work = "Senior Research Scientist at DeepGraph",
      education = "University of Washington",
      location = "Seattle, Washington",
      joinedDate = "January 2022",
      isOnline = true,
      isFriend = true,
      isFollowing = true
    ),
    UserEntity(
      id = "user_marcus",
      username = "marcusvance",
      fullName = "Marcus Vance",
      avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&h=300&fit=crop",
      coverUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=1200&h=400&fit=crop",
      bio = "Music producer & modular synth explorer. New ambient EP dropping this Friday. 🎹✨",
      isVerified = true,
      followersCount = 12400,
      followingCount = 610,
      friendsCount = 420,
      postsCount = 112,
      work = "Audio Director at Subwave Records",
      education = "Berklee College of Music",
      location = "Austin, Texas",
      joinedDate = "August 2021",
      isOnline = false,
      isFriend = true,
      isFollowing = true
    ),
    UserEntity(
      id = "user_elena",
      username = "elenarostova",
      fullName = "Elena Rostova",
      avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300&h=300&fit=crop",
      coverUrl = "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=1200&h=400&fit=crop",
      bio = "Architectural historian & urban sketcher. Exploring modernist structures around the globe.",
      isVerified = false,
      followersCount = 3120,
      followingCount = 290,
      friendsCount = 195,
      postsCount = 34,
      work = "Architect at Studio Form",
      education = "Columbia GSAPP",
      location = "Chicago, Illinois",
      joinedDate = "June 2023",
      isOnline = true,
      isFriend = false,
      isFollowing = false
    ),
    UserEntity(
      id = "user_david",
      username = "davidkim",
      fullName = "David Kim",
      avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300&h=300&fit=crop",
      coverUrl = "https://images.unsplash.com/photo-1442512595331-e89e73853f31?w=1200&h=400&fit=crop",
      bio = "Specialty coffee roaster & open-source developer. Sharing brewing recipes and clean code.",
      isVerified = false,
      followersCount = 1840,
      followingCount = 310,
      friendsCount = 210,
      postsCount = 28,
      work = "Founder at Drift Coffee Lab",
      education = "Oregon State University",
      location = "Portland, Oregon",
      joinedDate = "November 2023",
      isOnline = true,
      isFriend = false,
      isFollowing = false
    ),
    UserEntity(
      id = "user_maya",
      username = "mayapatel",
      fullName = "Maya Patel",
      avatarUrl = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=300&h=300&fit=crop",
      coverUrl = "https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?w=1200&h=400&fit=crop",
      bio = "Digital artist & 3D motion designer. Vibrant color gradients & micro-interactions.",
      isVerified = true,
      followersCount = 15800,
      followingCount = 540,
      friendsCount = 680,
      postsCount = 145,
      work = "Creative Technologist at Neon Flow",
      education = "RISD",
      location = "Brooklyn, New York",
      joinedDate = "February 2022",
      isOnline = true,
      isFriend = true,
      isFollowing = true
    )
  )

  val posts = listOf(
    PostEntity(
      id = "post_1",
      authorId = "user_sarah",
      authorName = "Sarah Chen",
      authorUsername = "sarahchen",
      authorAvatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=300&h=300&fit=crop",
      isAuthorVerified = true,
      timestamp = now - (1000 * 60 * 25), // 25 mins ago
      content = "Just hiked up to Lake Serene at sunrise. The alpine fog clearing above the turquoise water felt like stepping into an oil painting. What are your favorite mountain escapes this season?",
      mediaUrlsString = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=900&h=600&fit=crop,https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=900&h=600&fit=crop",
      feelingOrActivity = "feeling peaceful 🌲",
      audience = "Public",
      likesCount = 248,
      commentsCount = 38,
      sharesCount = 14,
      myReaction = "LOVE",
      isSaved = true
    ),
    PostEntity(
      id = "post_2",
      authorId = "user_me",
      authorName = "Alex Rivera",
      authorUsername = "alexrivera",
      authorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&h=300&fit=crop",
      isAuthorVerified = true,
      timestamp = now - (1000 * 60 * 90), // 1.5 hours ago
      content = "Excited to welcome everyone to Sociva! We designed this space to celebrate genuine connection, thoughtful conversations, and inspiring craft. Looking forward to hearing all your thoughts on the new interface! ✨🚀",
      mediaUrlsString = "",
      feelingOrActivity = "feeling excited 🎉",
      audience = "Public",
      likesCount = 512,
      commentsCount = 74,
      sharesCount = 46,
      myReaction = "LIKE",
      isSaved = false
    ),
    PostEntity(
      id = "post_3",
      authorId = "user_marcus",
      authorName = "Marcus Vance",
      authorUsername = "marcusvance",
      authorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&h=300&fit=crop",
      isAuthorVerified = true,
      timestamp = now - (1000 * 60 * 180), // 3 hours ago
      content = "Late night patch session in the studio. Channelling vintage tape warmth with modular resonators. Putting the finishing touches on the new track! 🎛️🎧",
      mediaUrlsString = "https://images.unsplash.com/photo-1598488035139-bdbb2231ce04?w=900&h=600&fit=crop",
      feelingOrActivity = "listening to Ambient Tape Loops 🎶",
      audience = "Friends",
      likesCount = 186,
      commentsCount = 22,
      sharesCount = 9,
      myReaction = null,
      isSaved = false
    ),
    PostEntity(
      id = "post_4",
      authorId = "user_maya",
      authorName = "Maya Patel",
      authorUsername = "mayapatel",
      authorAvatar = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=300&h=300&fit=crop",
      isAuthorVerified = true,
      timestamp = now - (1000 * 60 * 360), // 6 hours ago
      content = "Finished this generative color study for Sociva's identity system. Notice how the fluid indigo and ultraviolet transition creates warmth without losing clarity. Swipe to explore the color breakdowns!",
      mediaUrlsString = "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?w=900&h=600&fit=crop,https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?w=900&h=600&fit=crop,https://images.unsplash.com/photo-1541701494587-cb58502866ab?w=900&h=600&fit=crop",
      feelingOrActivity = "feeling creative 🎨",
      audience = "Public",
      likesCount = 372,
      commentsCount = 49,
      sharesCount = 31,
      myReaction = "LIKE",
      isSaved = true
    ),
    PostEntity(
      id = "post_5",
      authorId = "user_david",
      authorName = "David Kim",
      authorUsername = "davidkim",
      authorAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300&h=300&fit=crop",
      isAuthorVerified = false,
      timestamp = now - (1000 * 60 * 720), // 12 hours ago
      content = "Morning ritual: dialed in an Ethiopian Yirgacheffe at 93°C with a 1:16 brew ratio. Tasting notes of bergamot, jasmine blossom, and honey peach. What bean are you brewing today?",
      mediaUrlsString = "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=900&h=600&fit=crop",
      feelingOrActivity = "drinking specialty coffee ☕",
      audience = "Public",
      likesCount = 142,
      commentsCount = 27,
      sharesCount = 5,
      myReaction = null,
      isSaved = false
    )
  )

  val comments = listOf(
    CommentEntity(
      id = "c_1",
      postId = "post_1",
      authorId = "user_me",
      authorName = "Alex Rivera",
      authorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&h=300&fit=crop",
      isAuthorVerified = true,
      content = "The lighting in that first shot is unreal Sarah! Need to visit Lake Serene next month.",
      timestamp = now - (1000 * 60 * 18),
      updatedAt = now - (1000 * 60 * 18),
      parentCommentId = null,
      likesCount = 14,
      isLiked = true
    ),
    CommentEntity(
      id = "c_1_rep_1",
      postId = "post_1",
      authorId = "user_sarah",
      authorName = "Sarah Chen",
      authorAvatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=300&h=300&fit=crop",
      isAuthorVerified = true,
      content = "@Alex Rivera Thank you Alex! Definitely let me know when you go, I know some secret viewpoint spots!",
      timestamp = now - (1000 * 60 * 15),
      updatedAt = now - (1000 * 60 * 15),
      parentCommentId = "c_1",
      likesCount = 5,
      isLiked = true
    ),
    CommentEntity(
      id = "c_1_rep_2",
      postId = "post_1",
      authorId = "user_maya",
      authorName = "Maya Patel",
      authorAvatar = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=300&h=300&fit=crop",
      isAuthorVerified = true,
      content = "Count me in for the hike as well! 🌄",
      timestamp = now - (1000 * 60 * 10),
      updatedAt = now - (1000 * 60 * 10),
      parentCommentId = "c_1",
      likesCount = 2,
      isLiked = false
    ),
    CommentEntity(
      id = "c_2",
      postId = "post_1",
      authorId = "user_maya",
      authorName = "Maya Patel",
      authorAvatar = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=300&h=300&fit=crop",
      isAuthorVerified = true,
      content = "Those reflections are pure magic. Did you use an ND filter?",
      timestamp = now - (1000 * 60 * 12),
      updatedAt = now - (1000 * 60 * 12),
      parentCommentId = null,
      likesCount = 6,
      isLiked = false
    ),
    CommentEntity(
      id = "c_2_rep_1",
      postId = "post_1",
      authorId = "user_sarah",
      authorName = "Sarah Chen",
      authorAvatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=300&h=300&fit=crop",
      isAuthorVerified = true,
      content = "@Maya Patel Yes! 6-stop ND filter and a tripod for the calm water effect.",
      timestamp = now - (1000 * 60 * 8),
      updatedAt = now - (1000 * 60 * 8),
      parentCommentId = "c_2",
      likesCount = 3,
      isLiked = false
    ),
    CommentEntity(
      id = "c_3",
      postId = "post_2",
      authorId = "user_sarah",
      authorName = "Sarah Chen",
      authorAvatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=300&h=300&fit=crop",
      isAuthorVerified = true,
      content = "Congratulations Alex! The fluidity of the UI and the purple gradients look stunning.",
      timestamp = now - (1000 * 60 * 60),
      updatedAt = now - (1000 * 60 * 60),
      parentCommentId = null,
      likesCount = 28,
      isLiked = true
    ),
    CommentEntity(
      id = "c_4",
      postId = "post_4",
      authorId = "user_marcus",
      authorName = "Marcus Vance",
      authorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&h=300&fit=crop",
      isAuthorVerified = true,
      content = "This color palette has such great rhythm. Would make an amazing album sleeve too!",
      timestamp = now - (1000 * 60 * 200),
      updatedAt = now - (1000 * 60 * 200),
      parentCommentId = null,
      likesCount = 11,
      isLiked = false
    )
  )

  val commentReactions = listOf(
    CommentReactionEntity(
      id = "cr_1",
      commentId = "c_1",
      userId = "user_me",
      reactionType = "LOVE",
      createdAt = now - (1000 * 60 * 16)
    ),
    CommentReactionEntity(
      id = "cr_2",
      commentId = "c_1",
      userId = "user_sarah",
      reactionType = "LIKE",
      createdAt = now - (1000 * 60 * 15)
    ),
    CommentReactionEntity(
      id = "cr_3",
      commentId = "c_1_rep_1",
      userId = "user_me",
      reactionType = "LOVE",
      createdAt = now - (1000 * 60 * 14)
    ),
    CommentReactionEntity(
      id = "cr_4",
      commentId = "c_2",
      userId = "user_sarah",
      reactionType = "LIKE",
      createdAt = now - (1000 * 60 * 9)
    )
  )

  val stories = listOf(
    StoryEntity(
      id = "story_me",
      userId = "user_me",
      userName = "Your Story",
      userAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&h=300&fit=crop",
      isUserVerified = true,
      mediaUrl = "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=600&h=900&fit=crop",
      textOverlay = "Shipping Sociva 1.0! 🚀💫",
      backgroundGradientIndex = 0,
      timestamp = now - (1000 * 60 * 45),
      expiresAt = now + (1000 * 60 * 60 * 23),
      viewsCount = 48,
      isViewed = true
    ),
    StoryEntity(
      id = "story_sarah",
      userId = "user_sarah",
      userName = "Sarah Chen",
      userAvatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=300&h=300&fit=crop",
      isUserVerified = true,
      mediaUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=600&h=900&fit=crop",
      textOverlay = "Dawn patrol at 5,000 ft 🏔️",
      backgroundGradientIndex = 1,
      timestamp = now - (1000 * 60 * 90),
      expiresAt = now + (1000 * 60 * 60 * 22),
      viewsCount = 89,
      isViewed = false
    ),
    StoryEntity(
      id = "story_marcus",
      userId = "user_marcus",
      userName = "Marcus Vance",
      userAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&h=300&fit=crop",
      isUserVerified = true,
      mediaUrl = "https://images.unsplash.com/photo-1598488035139-bdbb2231ce04?w=600&h=900&fit=crop",
      textOverlay = "Analog synths warming up 🎹⚡",
      backgroundGradientIndex = 2,
      timestamp = now - (1000 * 60 * 150),
      expiresAt = now + (1000 * 60 * 60 * 21),
      viewsCount = 112,
      isViewed = false
    ),
    StoryEntity(
      id = "story_maya",
      userId = "user_maya",
      userName = "Maya Patel",
      userAvatar = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=300&h=300&fit=crop",
      isUserVerified = true,
      mediaUrl = null,
      textOverlay = "Good design is making something intelligible and memorable. Great design is making it memorable and meaningful. ✨",
      backgroundGradientIndex = 3,
      timestamp = now - (1000 * 60 * 240),
      expiresAt = now + (1000 * 60 * 60 * 20),
      viewsCount = 64,
      isViewed = false
    ),
    StoryEntity(
      id = "story_david",
      userId = "user_david",
      userName = "David Kim",
      userAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300&h=300&fit=crop",
      isUserVerified = false,
      mediaUrl = "https://images.unsplash.com/photo-1442512595331-e89e73853f31?w=600&h=900&fit=crop",
      textOverlay = "Fresh batch of Geisha just roasted! ☕🔥",
      backgroundGradientIndex = 4,
      timestamp = now - (1000 * 60 * 320),
      expiresAt = now + (1000 * 60 * 60 * 18),
      viewsCount = 37,
      isViewed = false
    )
  )

  val reels = listOf(
    ReelEntity(
      id = "reel_1",
      creatorId = "user_maya",
      creatorName = "Maya Patel",
      creatorUsername = "mayapatel",
      creatorAvatar = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=300&h=300&fit=crop",
      isCreatorVerified = true,
      videoThumbnail = "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?w=720&h=1280&fit=crop",
      caption = "Creating seamless 3D liquid animations in Blender 4.0. Step-by-step shader node walkthrough! 🎨✨ #CreativeCoding #DesignProcess",
      audioTitle = "Maya Patel • Prism Resonance (Original Audio)",
      likesCount = 3420,
      commentsCount = 215,
      sharesCount = 490,
      isLiked = true,
      isSaved = true,
      isFollowing = true
    ),
    ReelEntity(
      id = "reel_2",
      creatorId = "user_marcus",
      creatorName = "Marcus Vance",
      creatorUsername = "marcusvance",
      creatorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&h=300&fit=crop",
      isCreatorVerified = true,
      videoThumbnail = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=720&h=1280&fit=crop",
      caption = "How I created the atmospheric bass drop using Eurorack oscillator modules 🎚️🔊 Turn up the volume for this one!",
      audioTitle = "Marcus Vance • Subwave Frequencies",
      likesCount = 5890,
      commentsCount = 430,
      sharesCount = 812,
      isLiked = false,
      isSaved = false,
      isFollowing = true
    ),
    ReelEntity(
      id = "reel_3",
      creatorId = "user_sarah",
      creatorName = "Sarah Chen",
      creatorUsername = "sarahchen",
      creatorAvatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=300&h=300&fit=crop",
      isCreatorVerified = true,
      videoThumbnail = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=720&h=1280&fit=crop",
      caption = "Hyperlapse of the northern lights dancing over Snoqualmie Pass last midnight. Pure natural wonder 🌌✨ #AuroraBorealis",
      audioTitle = "Nordic Ambient Ensemble • Celestial Paths",
      likesCount = 9240,
      commentsCount = 680,
      sharesCount = 1450,
      isLiked = true,
      isSaved = true,
      isFollowing = true
    ),
    ReelEntity(
      id = "reel_4",
      creatorId = "user_david",
      creatorName = "David Kim",
      creatorUsername = "davidkim",
      creatorAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300&h=300&fit=crop",
      isCreatorVerified = false,
      videoThumbnail = "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=720&h=1280&fit=crop",
      caption = "Mastering the slow spiral pour over. Watch the coffee bloom expand into rich caramel foam ☕👌",
      audioTitle = "Lo-Fi Beats • Morning Warmth",
      likesCount = 1840,
      commentsCount = 95,
      sharesCount = 130,
      isLiked = false,
      isSaved = false,
      isFollowing = false
    )
  )

  val conversations = listOf(
    ConversationEntity(
      id = "conv_sarah",
      participantId = "user_sarah",
      participantName = "Sarah Chen",
      participantUsername = "sarahchen",
      participantAvatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=300&h=300&fit=crop",
      isParticipantVerified = true,
      lastMessage = "Yes! I'll send over the RAW files for the design assets tonight.",
      lastMessageTimestamp = now - (1000 * 60 * 8),
      unreadCount = 1,
      isOnline = true
    ),
    ConversationEntity(
      id = "conv_marcus",
      participantId = "user_marcus",
      participantName = "Marcus Vance",
      participantUsername = "marcusvance",
      participantAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&h=300&fit=crop",
      isParticipantVerified = true,
      lastMessage = "Check out the opening synth stabs in the new mix 🎧",
      lastMessageTimestamp = now - (1000 * 60 * 42),
      unreadCount = 0,
      isOnline = false
    ),
    ConversationEntity(
      id = "conv_maya",
      participantId = "user_maya",
      participantName = "Maya Patel",
      participantUsername = "mayapatel",
      participantAvatar = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=300&h=300&fit=crop",
      isParticipantVerified = true,
      lastMessage = "Love the new post composer animations on Sociva! So snappy.",
      lastMessageTimestamp = now - (1000 * 60 * 120),
      unreadCount = 0,
      isOnline = true
    ),
    ConversationEntity(
      id = "conv_david",
      participantId = "user_david",
      participantName = "David Kim",
      participantUsername = "davidkim",
      participantAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300&h=300&fit=crop",
      isParticipantVerified = false,
      lastMessage = "Saved a bag of the anaerobic roast for you next time you stop by!",
      lastMessageTimestamp = now - (1000 * 60 * 360),
      unreadCount = 0,
      isOnline = true
    )
  )

  val conversationMembers = listOf(
    ConversationMemberEntity(conversationId = "conv_sarah", userId = "user_me", joinedAt = now - (1000 * 60 * 60 * 24), lastReadAt = now - (1000 * 60 * 15)),
    ConversationMemberEntity(conversationId = "conv_sarah", userId = "user_sarah", joinedAt = now - (1000 * 60 * 60 * 24), lastReadAt = now - (1000 * 60 * 8)),
    ConversationMemberEntity(conversationId = "conv_marcus", userId = "user_me", joinedAt = now - (1000 * 60 * 60 * 24), lastReadAt = now),
    ConversationMemberEntity(conversationId = "conv_marcus", userId = "user_marcus", joinedAt = now - (1000 * 60 * 60 * 24), lastReadAt = now - (1000 * 60 * 42)),
    ConversationMemberEntity(conversationId = "conv_maya", userId = "user_me", joinedAt = now - (1000 * 60 * 60 * 24), lastReadAt = now),
    ConversationMemberEntity(conversationId = "conv_maya", userId = "user_maya", joinedAt = now - (1000 * 60 * 60 * 24), lastReadAt = now - (1000 * 60 * 120)),
    ConversationMemberEntity(conversationId = "conv_david", userId = "user_me", joinedAt = now - (1000 * 60 * 60 * 24), lastReadAt = now),
    ConversationMemberEntity(conversationId = "conv_david", userId = "user_david", joinedAt = now - (1000 * 60 * 60 * 24), lastReadAt = now - (1000 * 60 * 360))
  )

  val messages = listOf(
    MessageEntity(
      id = "m_1",
      conversationId = "conv_sarah",
      senderId = "user_me",
      receiverId = "user_sarah",
      text = "Hey Sarah! Saw your Lake Serene photos, they look absolutely breathtaking.",
      mediaUrl = null,
      timestamp = now - (1000 * 60 * 30),
      isSeen = true,
      isMine = true
    ),
    MessageEntity(
      id = "m_2",
      conversationId = "conv_sarah",
      senderId = "user_sarah",
      receiverId = "user_me",
      text = "Thank you Alex! It was worth waking up at 4am to catch that morning haze.",
      mediaUrl = null,
      timestamp = now - (1000 * 60 * 20),
      isSeen = true,
      isMine = false
    ),
    MessageEntity(
      id = "m_3",
      conversationId = "conv_sarah",
      senderId = "user_me",
      receiverId = "user_sarah",
      text = "Are you still open to using one of them as a backdrop for the community spotlight banner?",
      mediaUrl = null,
      timestamp = now - (1000 * 60 * 15),
      isSeen = true,
      isMine = true
    ),
    MessageEntity(
      id = "m_4",
      conversationId = "conv_sarah",
      senderId = "user_sarah",
      receiverId = "user_me",
      text = "Yes! I'll send over the RAW files for the design assets tonight.",
      mediaUrl = null,
      timestamp = now - (1000 * 60 * 8),
      isSeen = false,
      isMine = false
    ),
    MessageEntity(
      id = "m_marcus_1",
      conversationId = "conv_marcus",
      senderId = "user_me",
      receiverId = "user_marcus",
      text = "Hey Marcus, how is the new track coming along?",
      mediaUrl = null,
      timestamp = now - (1000 * 60 * 60),
      isSeen = true,
      isMine = true
    ),
    MessageEntity(
      id = "m_marcus_2",
      conversationId = "conv_marcus",
      senderId = "user_marcus",
      receiverId = "user_me",
      text = "Check out the opening synth stabs in the new mix 🎧",
      mediaUrl = null,
      timestamp = now - (1000 * 60 * 42),
      isSeen = true,
      isMine = false
    ),
    MessageEntity(
      id = "m_maya_1",
      conversationId = "conv_maya",
      senderId = "user_maya",
      receiverId = "user_me",
      text = "Love the new post composer animations on Sociva! So snappy.",
      mediaUrl = null,
      timestamp = now - (1000 * 60 * 120),
      isSeen = true,
      isMine = false
    ),
    MessageEntity(
      id = "m_david_1",
      conversationId = "conv_david",
      senderId = "user_david",
      receiverId = "user_me",
      text = "Saved a bag of the anaerobic roast for you next time you stop by!",
      mediaUrl = null,
      timestamp = now - (1000 * 60 * 360),
      isSeen = true,
      isMine = false
    )
  )

  val notifications = listOf(
    NotificationEntity(
      id = "notif_1",
      type = "LIKE",
      actorName = "Sarah Chen",
      actorAvatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=300&h=300&fit=crop",
      isActorVerified = true,
      messageSnippet = "loved your post 'Excited to welcome everyone to Sociva!'",
      timestamp = now - (1000 * 60 * 15),
      isRead = false,
      targetPostId = "post_2"
    ),
    NotificationEntity(
      id = "notif_2",
      type = "COMMENT",
      actorName = "Maya Patel",
      actorAvatar = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=300&h=300&fit=crop",
      isActorVerified = true,
      messageSnippet = "commented: 'Congratulations Alex! The fluidity of the UI...'",
      timestamp = now - (1000 * 60 * 45),
      isRead = false,
      targetPostId = "post_2"
    ),
    NotificationEntity(
      id = "notif_3",
      type = "FRIEND_REQUEST",
      actorName = "Elena Rostova",
      actorAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300&h=300&fit=crop",
      isActorVerified = false,
      messageSnippet = "sent you a friend request. 12 mutual friends.",
      timestamp = now - (1000 * 60 * 110),
      isRead = false,
      targetPostId = null
    ),
    NotificationEntity(
      id = "notif_4",
      type = "STORY_REACTION",
      actorName = "Marcus Vance",
      actorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&h=300&fit=crop",
      isActorVerified = true,
      messageSnippet = "reacted 🔥 to your story",
      timestamp = now - (1000 * 60 * 180),
      isRead = true,
      targetPostId = null
    ),
    NotificationEntity(
      id = "notif_5",
      type = "SHARE",
      actorName = "David Kim",
      actorAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300&h=300&fit=crop",
      isActorVerified = false,
      messageSnippet = "shared your post to the 'Design Systems' group",
      timestamp = now - (1000 * 60 * 300),
      isRead = true,
      targetPostId = "post_2"
    )
  )

  val friendRequests = listOf(
    FriendRequestEntity(
      id = "req_1",
      senderId = "user_elena",
      receiverId = "user_me",
      status = "pending",
      createdAt = now - (1000 * 60 * 110),
      updatedAt = now - (1000 * 60 * 110),
      senderName = "Elena Rostova",
      senderUsername = "elenarostova",
      senderAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300&h=300&fit=crop",
      receiverName = "Alex Rivera",
      receiverUsername = "alexrivera",
      receiverAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&h=300&fit=crop",
      mutualFriendsCount = 14
    ),
    FriendRequestEntity(
      id = "req_2",
      senderId = "user_david",
      receiverId = "user_me",
      status = "pending",
      createdAt = now - (1000 * 60 * 240),
      updatedAt = now - (1000 * 60 * 240),
      senderName = "David Kim",
      senderUsername = "davidkim",
      senderAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300&h=300&fit=crop",
      receiverName = "Alex Rivera",
      receiverUsername = "alexrivera",
      receiverAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&h=300&fit=crop",
      mutualFriendsCount = 8
    )
  )

  val friendships = listOf(
    FriendshipEntity(id = "fr_1", userId = "user_me", friendId = "user_sarah", createdAt = now - (1000L * 60 * 60 * 24 * 60)),
    FriendshipEntity(id = "fr_2", userId = "user_sarah", friendId = "user_me", createdAt = now - (1000L * 60 * 60 * 24 * 60)),
    FriendshipEntity(id = "fr_3", userId = "user_me", friendId = "user_marcus", createdAt = now - (1000L * 60 * 60 * 24 * 90)),
    FriendshipEntity(id = "fr_4", userId = "user_marcus", friendId = "user_me", createdAt = now - (1000L * 60 * 60 * 24 * 90)),
    FriendshipEntity(id = "fr_5", userId = "user_me", friendId = "user_maya", createdAt = now - (1000L * 60 * 60 * 24 * 30)),
    FriendshipEntity(id = "fr_6", userId = "user_maya", friendId = "user_me", createdAt = now - (1000L * 60 * 60 * 24 * 30))
  )

  val follows = listOf(
    FollowEntity(id = "fol_1", followerId = "user_me", followingId = "user_sarah", createdAt = now - (1000L * 60 * 60 * 24 * 60)),
    FollowEntity(id = "fol_2", followerId = "user_sarah", followingId = "user_me", createdAt = now - (1000L * 60 * 60 * 24 * 60)),
    FollowEntity(id = "fol_3", followerId = "user_me", followingId = "user_marcus", createdAt = now - (1000L * 60 * 60 * 24 * 90)),
    FollowEntity(id = "fol_4", followerId = "user_marcus", followingId = "user_me", createdAt = now - (1000L * 60 * 60 * 24 * 90)),
    FollowEntity(id = "fol_5", followerId = "user_me", followingId = "user_maya", createdAt = now - (1000L * 60 * 60 * 24 * 30)),
    FollowEntity(id = "fol_6", followerId = "user_maya", followingId = "user_me", createdAt = now - (1000L * 60 * 60 * 24 * 30)),
    FollowEntity(id = "fol_7", followerId = "user_elena", followingId = "user_me", createdAt = now - (1000 * 60 * 110)),
    FollowEntity(id = "fol_8", followerId = "user_david", followingId = "user_me", createdAt = now - (1000 * 60 * 240))
  )

  val pages = listOf(
    SocivaPageEntity(
      id = "page_1",
      name = "Sociva Creators",
      category = "Community & Innovation",
      description = "The official community for creators, designers, and developers shaping the future on Sociva.",
      avatarUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=300&h=300&fit=crop",
      coverUrl = "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?w=900&h=300&fit=crop",
      followersCount = 28400,
      isLiked = true,
      isAdmin = true
    ),
    SocivaPageEntity(
      id = "page_2",
      name = "TechWave Weekly",
      category = "Science & Technology",
      description = "Curated deep dives into AI advancements, spatial computing, and mobile engineering.",
      avatarUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?w=300&h=300&fit=crop",
      coverUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=900&h=300&fit=crop",
      followersCount = 45200,
      isLiked = false,
      isAdmin = false
    ),
    SocivaPageEntity(
      id = "page_3",
      name = "Artisan Roasters Guild",
      category = "Food & Drink",
      description = "Dedicated to sustainable bean sourcing, roast chemistry, and third-wave brewing mastery.",
      avatarUrl = "https://images.unsplash.com/photo-1442512595331-e89e73853f31?w=300&h=300&fit=crop",
      coverUrl = "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=900&h=300&fit=crop",
      followersCount = 12900,
      isLiked = true,
      isAdmin = false
    )
  )

  val groups = listOf(
    GroupEntity(
      id = "grp_1",
      name = "Android & Jetpack Compose Architects",
      privacy = "Public group",
      description = "A thriving hub for modern Android engineers building fluid, reactive, high-performance UIs.",
      avatarUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=300&h=300&fit=crop",
      membersCount = 18450,
      isJoined = true,
      role = "Moderator"
    ),
    GroupEntity(
      id = "grp_2",
      name = "Global Travel Photographers",
      privacy = "Public group",
      description = "Share high-resolution landscapes, gear insights, and secret locations from around the world.",
      avatarUrl = "https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=300&h=300&fit=crop",
      membersCount = 32800,
      isJoined = true,
      role = "Member"
    ),
    GroupEntity(
      id = "grp_3",
      name = "Design Systems & Token Architecture",
      privacy = "Private group",
      description = "Advanced discussions on typography scales, color contrast, multi-platform tokens, and accessibility.",
      avatarUrl = "https://images.unsplash.com/photo-1507238691740-187a5b1d37b8?w=300&h=300&fit=crop",
      membersCount = 9200,
      isJoined = false,
      role = "None"
    )
  )

  val reports = listOf(
    ReportEntity(
      id = "rep_1",
      targetType = "Post",
      targetId = "post_spam_test",
      targetTitle = "Unsolicited cryptocurrency investment link",
      reason = "Spam / Misleading Financial Content",
      reportedBy = "sarahchen",
      timestamp = now - (1000 * 60 * 140),
      status = "Pending"
    ),
    ReportEntity(
      id = "rep_2",
      targetType = "User",
      targetId = "user_bot_42",
      targetTitle = "Suspicious duplicate account @crypto_promoter_22",
      reason = "Impersonation and automated bot behavior",
      reportedBy = "marcusvance",
      timestamp = now - (1000 * 60 * 290),
      status = "Pending"
    )
  )
}

// Type alias helper for Page entity mapping
typealias SocivaPageEntity = PageEntity
