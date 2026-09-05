package com.example.sociva.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sociva.data.model.PostAnalytics
import com.example.sociva.data.model.User
import com.example.sociva.ui.SocivaViewModel
import com.example.sociva.ui.components.UserAvatar
import com.example.sociva.ui.components.VerifiedBadge
import com.example.ui.theme.SocivaBlue
import com.example.ui.theme.SocivaIndigo
import com.example.ui.theme.SocivaPurple
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostAnalyticsBottomSheet(
  postId: String,
  viewModel: SocivaViewModel,
  onDismiss: () -> Unit
) {
  val analyticsState by viewModel.getPostAnalytics(postId).collectAsState(initial = null)
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
    modifier = Modifier.testTag("post_analytics_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 24.dp)
    ) {
      // Header Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(
                Brush.linearGradient(listOf(SocivaBlue, SocivaIndigo))
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Insights,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(22.dp)
            )
          }
          Column {
            Text(
              text = "Post Analytics",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Private to you as the post creator",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        IconButton(
          onClick = onDismiss,
          modifier = Modifier.testTag("close_post_analytics_btn")
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      HorizontalDivider(
        modifier = Modifier.padding(vertical = 10.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
      )

      val analytics = analyticsState
      if (analytics == null) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            CircularProgressIndicator(
              color = SocivaBlue,
              modifier = Modifier.size(36.dp)
            )
            Text(
              text = "Calculating insights...",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // 1. Hero Performance Overview Card
          item {
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .testTag("analytics_hero_card"),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
              )
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "Engagement Rate",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "${DecimalFormat("#,##0.1").format(analytics.engagementRate)}%",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = SocivaBlue
                  )
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = "Based on reactions, comments, shares & visits",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }

                Box(
                  modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(SocivaBlue.copy(alpha = 0.15f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = SocivaBlue,
                    modifier = Modifier.size(36.dp)
                  )
                }
              }
            }
          }

          // 2. Primary Metrics Grid
          item {
            Text(
              text = "Reach & Impressions",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              MetricCard(
                title = "Total Views",
                value = formatCount(analytics.totalViews),
                icon = Icons.Outlined.Visibility,
                iconTint = SocivaBlue,
                subtitle = "Total impressions",
                modifier = Modifier.weight(1f).testTag("metric_total_views")
              )
              MetricCard(
                title = "Reach",
                value = formatCount(analytics.uniqueViewers),
                icon = Icons.Outlined.People,
                iconTint = SocivaIndigo,
                subtitle = "Unique viewers",
                modifier = Modifier.weight(1f).testTag("metric_unique_viewers")
              )
            }
          }

          // 3. Interactions Grid
          item {
            Text(
              text = "Interactions & Engagement",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.padding(start = 4.dp, top = 6.dp, bottom = 4.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                MetricCard(
                  title = "Reactions",
                  value = formatCount(analytics.reactionsCount),
                  icon = Icons.Outlined.ThumbUp,
                  iconTint = Color(0xFF1877F2),
                  subtitle = "Likes & reactions",
                  modifier = Modifier.weight(1f).testTag("metric_reactions")
                )
                MetricCard(
                  title = "Comments",
                  value = formatCount(analytics.commentsCount),
                  icon = Icons.Outlined.ChatBubbleOutline,
                  iconTint = Color(0xFF10B981),
                  subtitle = if (analytics.repliesCount > 0) "${analytics.repliesCount} replies" else "Discussion",
                  modifier = Modifier.weight(1f).testTag("metric_comments")
                )
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                MetricCard(
                  title = "Shares",
                  value = formatCount(analytics.sharesCount),
                  icon = Icons.Outlined.Share,
                  iconTint = Color(0xFFF59E0B),
                  subtitle = "Shared to feed",
                  modifier = Modifier.weight(1f).testTag("metric_shares")
                )
                MetricCard(
                  title = "Saves",
                  value = formatCount(analytics.savesCount),
                  icon = Icons.Outlined.BookmarkBorder,
                  iconTint = SocivaPurple,
                  subtitle = "Bookmarked",
                  modifier = Modifier.weight(1f).testTag("metric_saves")
                )
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                MetricCard(
                  title = "Profile Visits",
                  value = formatCount(analytics.profileVisitsFromPost),
                  icon = Icons.Outlined.AccountCircle,
                  iconTint = Color(0xFFEC4899),
                  subtitle = "Generated from post",
                  modifier = Modifier.fillMaxWidth().testTag("metric_profile_visits")
                )
              }
            }
          }

          // 4. Reactions Breakdown (if any reactions exist)
          if (analytics.reactionBreakdown.isNotEmpty()) {
            item {
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("reaction_breakdown_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                  containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  Text(
                    text = "Reaction Breakdown",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Spacer(modifier = Modifier.height(10.dp))
                  analytics.reactionBreakdown.forEach { (type, count) ->
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                      ) {
                        Text(
                          text = reactionEmojiForType(type),
                          fontSize = 18.sp
                        )
                        Text(
                          text = type,
                          style = MaterialTheme.typography.bodyMedium,
                          fontWeight = FontWeight.Medium,
                          color = MaterialTheme.colorScheme.onSurface
                        )
                      }
                      Text(
                        text = "$count",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = SocivaBlue
                      )
                    }
                  }
                }
              }
            }
          }

          // 5. Views Over Time Chart / Trend
          if (analytics.viewsOverTime.isNotEmpty()) {
            item {
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("views_over_time_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                  containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  Text(
                    text = "Views (Last 7 Days)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Spacer(modifier = Modifier.height(12.dp))

                  val maxVal = (analytics.viewsOverTime.maxOfOrNull { it.second } ?: 1).coerceAtLeast(1)

                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(110.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                  ) {
                    analytics.viewsOverTime.forEach { (label, count) ->
                      Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                      ) {
                        Text(
                          text = if (count > 0) "$count" else "",
                          style = MaterialTheme.typography.labelSmall,
                          fontSize = 9.sp,
                          fontWeight = FontWeight.Bold,
                          color = SocivaBlue
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        val barHeightFactor = (count.toFloat() / maxVal.toFloat()).coerceIn(0.08f, 1f)
                        Box(
                          modifier = Modifier
                            .fillMaxWidth(0.55f)
                            .height((70 * barHeightFactor).dp)
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(
                              if (count > 0) Brush.verticalGradient(
                                listOf(SocivaBlue, SocivaIndigo)
                              ) else Brush.verticalGradient(
                                listOf(
                                  MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                                  MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                                )
                              )
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                          text = label,
                          style = MaterialTheme.typography.labelSmall,
                          fontSize = 9.sp,
                          color = MaterialTheme.colorScheme.onSurfaceVariant,
                          maxLines = 1,
                          overflow = TextOverflow.Ellipsis
                        )
                      }
                    }
                  }
                }
              }
            }
          }

          // 6. Recent Viewers (Audience transparency)
          if (analytics.recentViewers.isNotEmpty()) {
            item {
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("recent_viewers_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                  containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "Recent Viewers",
                      style = MaterialTheme.typography.titleSmall,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                      text = "${analytics.uniqueViewers} total",
                      style = MaterialTheme.typography.labelSmall,
                      color = SocivaBlue,
                      fontWeight = FontWeight.SemiBold
                    )
                  }
                  Spacer(modifier = Modifier.height(8.dp))

                  analytics.recentViewers.forEach { viewer ->
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                          onDismiss()
                          viewModel.navigateToProfile(viewer.id)
                        }
                        .padding(vertical = 6.dp, horizontal = 4.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                      UserAvatar(
                        avatarUrl = viewer.avatarUrl,
                        name = viewer.fullName,
                        size = 36.dp
                      )
                      Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                          Text(
                            text = viewer.fullName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                          )
                          if (viewer.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            VerifiedBadge(size = 12.dp)
                          }
                        }
                        Text(
                          text = "@${viewer.username}",
                          style = MaterialTheme.typography.labelSmall,
                          color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                      }
                      Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "View Profile",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun MetricCard(
  title: String,
  value: String,
  icon: ImageVector,
  iconTint: Color,
  subtitle: String,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Box(
          modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(iconTint.copy(alpha = 0.12f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(16.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = value,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(2.dp))

      Text(
        text = subtitle,
        style = MaterialTheme.typography.labelSmall,
        fontSize = 10.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

private fun formatCount(count: Int): String {
  return when {
    count >= 1_000_000 -> "${DecimalFormat("#,##0.1").format(count / 1_000_000.0)}M"
    count >= 1_000 -> "${DecimalFormat("#,##0.#").format(count / 1_000.0)}K"
    else -> DecimalFormat("#,##0").format(count)
  }
}

private fun reactionEmojiForType(type: String): String {
  return when (type.lowercase()) {
    "like" -> "👍"
    "love" -> "❤️"
    "haha" -> "😆"
    "wow" -> "😮"
    "sad" -> "😢"
    "angry" -> "😡"
    "care" -> "🥰"
    else -> "👍"
  }
}
