package com.ayoshy.badventurers.ui

import com.ayoshy.badventurers.game.PlayPhase
import kotlin.math.roundToInt

internal const val GuildHomeTopBarReserveDp = 44
internal const val GuildHomeBottomNavigationHeightDp = 68
internal const val GuildHomeMinimumTouchTargetDp = 48
internal const val GuildHomeMinimumContentHeightDp = 420
internal const val GuildHomeMinimumArtFocusHeightDp = 300
internal const val GuildHomeMinimumEdgePaddingDp = 8

private const val GuildHomeSmallContentHeightDp = 560
private const val GuildHomeTallContentHeightDp = 760

internal enum class GuildHomeViewportClass {
    SmallPortrait,
    StandardPortrait,
    TallPortrait,
}

internal data class GuildHomeFrameLayout(
    val widthDp: Int,
    val heightDp: Int,
    val statusBarDp: Int,
    val navigationBarDp: Int,
    val topBarDp: Int = GuildHomeTopBarReserveDp,
    val bottomNavigationDp: Int = GuildHomeBottomNavigationHeightDp,
) {
    val safeHeightDp: Int = (heightDp - statusBarDp - navigationBarDp).coerceAtLeast(0)
    val guildContentHeightDp: Int = (safeHeightDp - topBarDp - bottomNavigationDp).coerceAtLeast(0)
}

internal data class GuildHomeDrawerLayout(
    val viewportClass: GuildHomeViewportClass,
    val horizontalPaddingDp: Int,
    val bottomPaddingDp: Int,
    val maxWidthDp: Int,
    val maxHeightDp: Int,
    val bodyMaxLines: Int,
    val minArtFocusHeightDp: Int,
)

internal fun guildHomeFrameLayout(
    widthDp: Int,
    heightDp: Int,
    statusBarDp: Int,
    navigationBarDp: Int,
    topBarDp: Int = GuildHomeTopBarReserveDp,
    bottomNavigationDp: Int = GuildHomeBottomNavigationHeightDp,
): GuildHomeFrameLayout = GuildHomeFrameLayout(
    widthDp = widthDp.coerceAtLeast(0),
    heightDp = heightDp.coerceAtLeast(0),
    statusBarDp = statusBarDp.coerceAtLeast(0),
    navigationBarDp = navigationBarDp.coerceAtLeast(0),
    topBarDp = topBarDp.coerceAtLeast(0),
    bottomNavigationDp = bottomNavigationDp.coerceAtLeast(0),
)

internal fun guildHomeDrawerLayout(contentWidthDp: Int, contentHeightDp: Int): GuildHomeDrawerLayout {
    val safeWidth = contentWidthDp.coerceAtLeast(0)
    val safeHeight = contentHeightDp.coerceAtLeast(0)
    val viewportClass = when {
        safeHeight < GuildHomeSmallContentHeightDp -> GuildHomeViewportClass.SmallPortrait
        safeHeight >= GuildHomeTallContentHeightDp -> GuildHomeViewportClass.TallPortrait
        else -> GuildHomeViewportClass.StandardPortrait
    }
    val horizontalPadding = when (viewportClass) {
        GuildHomeViewportClass.SmallPortrait -> 8
        GuildHomeViewportClass.StandardPortrait -> 12
        GuildHomeViewportClass.TallPortrait -> 16
    }
    val bottomPadding = when (viewportClass) {
        GuildHomeViewportClass.SmallPortrait -> 8
        GuildHomeViewportClass.StandardPortrait -> 12
        GuildHomeViewportClass.TallPortrait -> 16
    }
    val maxWidthCap = when (viewportClass) {
        GuildHomeViewportClass.SmallPortrait -> 384
        GuildHomeViewportClass.StandardPortrait -> 416
        GuildHomeViewportClass.TallPortrait -> 440
    }
    val maxHeightCap = when (viewportClass) {
        GuildHomeViewportClass.SmallPortrait -> 132
        GuildHomeViewportClass.StandardPortrait -> 148
        GuildHomeViewportClass.TallPortrait -> 164
    }
    val maxWidth = minOf(safeWidth - horizontalPadding * 2, maxWidthCap).coerceAtLeast(280)
    val proportionalHeight = (safeHeight * 0.34f).roundToInt()
    val maxHeight = minOf(maxHeightCap, proportionalHeight).coerceAtLeast(112)
    return GuildHomeDrawerLayout(
        viewportClass = viewportClass,
        horizontalPaddingDp = horizontalPadding,
        bottomPaddingDp = bottomPadding,
        maxWidthDp = maxWidth,
        maxHeightDp = maxHeight,
        bodyMaxLines = if (viewportClass == GuildHomeViewportClass.SmallPortrait) 1 else 2,
        minArtFocusHeightDp = (safeHeight - bottomPadding - maxHeight).coerceAtLeast(0),
    )
}
internal data class GuildHubHintState(
    val showIntroHint: Boolean,
    val labelledHotspots: List<GuildHubHotspot>,
)

internal data class GuildHubHintAnchor(
    val xFraction: Float,
    val yFraction: Float,
)

internal fun guildHubHintState(
    visitedHotspots: Set<GuildHubHotspot>,
    selectedHotspot: GuildHubHotspot,
): GuildHubHintState {
    val hasTappedAHotspot = visitedHotspots.isNotEmpty()
    return GuildHubHintState(
        showIntroHint = !hasTappedAHotspot,
        labelledHotspots = if (hasTappedAHotspot) {
            listOf(selectedHotspot)
        } else {
            GuildHubHotspot.values().toList()
        },
    )
}

internal fun guildHubHintAnchor(hotspot: GuildHubHotspot): GuildHubHintAnchor = when (hotspot) {
    GuildHubHotspot.Charter -> GuildHubHintAnchor(xFraction = 0.48f, yFraction = 0.22f)
    GuildHubHotspot.CoreCrew -> GuildHubHintAnchor(xFraction = 0.25f, yFraction = 0.54f)
    GuildHubHotspot.QuestTable -> GuildHubHintAnchor(xFraction = 0.50f, yFraction = 0.58f)
    GuildHubHotspot.LootCache -> GuildHubHintAnchor(xFraction = 0.75f, yFraction = 0.66f)
    GuildHubHotspot.Facilities -> GuildHubHintAnchor(xFraction = 0.55f, yFraction = 0.72f)
}

internal enum class GuildHubStatusBadgeKind {
    LootRecovery,
    CharterClaim,
    CharterProgress,
    QuestReady,
    QuestRunning,
    QuestReportReady,
    FacilityUpgradeReady,
}

internal enum class GuildHubStatusTone {
    Attention,
    Progress,
    Calm,
}

internal data class GuildHubStatusBadge(
    val hotspot: GuildHubHotspot,
    val kind: GuildHubStatusBadgeKind,
    val tone: GuildHubStatusTone,
    val value: Int? = null,
    val targetValue: Int? = null,
)

internal fun guildHubStatusBadges(
    phase: PlayPhase,
    pendingLootCount: Int,
    claimableAchievementCount: Int,
    achievementSeals: Int,
    nextMilestoneSeals: Int?,
    availableFacilityUpgradeCount: Int,
    runningSecondsRemaining: Int = 0,
): List<GuildHubStatusBadge> = buildList {
    val safePendingLootCount = pendingLootCount.coerceAtLeast(0)
    val safeClaimableAchievementCount = claimableAchievementCount.coerceAtLeast(0)
    val safeAchievementSeals = achievementSeals.coerceAtLeast(0)
    val safeFacilityUpgradeCount = availableFacilityUpgradeCount.coerceAtLeast(0)

    if (safePendingLootCount > 0) {
        add(
            GuildHubStatusBadge(
                hotspot = GuildHubHotspot.LootCache,
                kind = GuildHubStatusBadgeKind.LootRecovery,
                tone = GuildHubStatusTone.Attention,
                value = safePendingLootCount,
            ),
        )
    }

    if (safeClaimableAchievementCount > 0) {
        add(
            GuildHubStatusBadge(
                hotspot = GuildHubHotspot.Charter,
                kind = GuildHubStatusBadgeKind.CharterClaim,
                tone = GuildHubStatusTone.Attention,
                value = safeClaimableAchievementCount,
            ),
        )
    } else if (nextMilestoneSeals != null && nextMilestoneSeals > safeAchievementSeals) {
        add(
            GuildHubStatusBadge(
                hotspot = GuildHubHotspot.Charter,
                kind = GuildHubStatusBadgeKind.CharterProgress,
                tone = GuildHubStatusTone.Progress,
                value = safeAchievementSeals,
                targetValue = nextMilestoneSeals,
            ),
        )
    }

    when (phase) {
        PlayPhase.Idle -> add(
            GuildHubStatusBadge(
                hotspot = GuildHubHotspot.QuestTable,
                kind = GuildHubStatusBadgeKind.QuestReady,
                tone = GuildHubStatusTone.Calm,
            ),
        )
        PlayPhase.Running -> add(
            GuildHubStatusBadge(
                hotspot = GuildHubHotspot.QuestTable,
                kind = GuildHubStatusBadgeKind.QuestRunning,
                tone = GuildHubStatusTone.Progress,
                value = runningSecondsRemaining.coerceAtLeast(0),
            ),
        )
        PlayPhase.ResultReady -> add(
            GuildHubStatusBadge(
                hotspot = GuildHubHotspot.QuestTable,
                kind = GuildHubStatusBadgeKind.QuestReportReady,
                tone = GuildHubStatusTone.Attention,
            ),
        )
    }

    if (safeFacilityUpgradeCount > 0) {
        add(
            GuildHubStatusBadge(
                hotspot = GuildHubHotspot.Facilities,
                kind = GuildHubStatusBadgeKind.FacilityUpgradeReady,
                tone = GuildHubStatusTone.Attention,
                value = safeFacilityUpgradeCount,
            ),
        )
    }
}

internal fun guildHubStatusAnchor(hotspot: GuildHubHotspot): GuildHubHintAnchor = when (hotspot) {
    GuildHubHotspot.Charter -> GuildHubHintAnchor(xFraction = 0.62f, yFraction = 0.20f)
    GuildHubHotspot.CoreCrew -> GuildHubHintAnchor(xFraction = 0.34f, yFraction = 0.49f)
    GuildHubHotspot.QuestTable -> GuildHubHintAnchor(xFraction = 0.61f, yFraction = 0.53f)
    GuildHubHotspot.LootCache -> GuildHubHintAnchor(xFraction = 0.67f, yFraction = 0.60f)
    GuildHubHotspot.Facilities -> GuildHubHintAnchor(xFraction = 0.67f, yFraction = 0.70f)
}
