package com.ayoshy.badventurers.ui

import com.ayoshy.badventurers.game.PlayPhase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GuildHomeLayoutTest {
    @Test
    fun frameLayoutReservesStatusNavigationTopAndBottomBars() {
        val frame = guildHomeFrameLayout(
            widthDp = 360,
            heightDp = 640,
            statusBarDp = 24,
            navigationBarDp = 24,
        )

        assertEquals(592, frame.safeHeightDp)
        assertEquals(480, frame.guildContentHeightDp)
        assertTrue(frame.guildContentHeightDp >= GuildHomeMinimumContentHeightDp)
        assertTrue(GuildHomeBottomNavigationHeightDp >= GuildHomeMinimumTouchTargetDp)
    }

    @Test
    fun drawerLayoutKeepsSmallPortraitCompactAndLeavesArtVisible() {
        val frame = guildHomeFrameLayout(
            widthDp = 360,
            heightDp = 640,
            statusBarDp = 24,
            navigationBarDp = 24,
        )
        val layout = guildHomeDrawerLayout(frame.widthDp, frame.guildContentHeightDp)

        assertEquals(GuildHomeViewportClass.SmallPortrait, layout.viewportClass)
        assertEquals(1, layout.bodyMaxLines)
        assertEquals(GuildHomeMinimumEdgePaddingDp, layout.horizontalPaddingDp)
        assertEquals(GuildHomeMinimumEdgePaddingDp, layout.bottomPaddingDp)
        assertTrue(layout.maxHeightDp <= 132)
        assertTrue(layout.minArtFocusHeightDp >= GuildHomeMinimumArtFocusHeightDp)
    }

    @Test
    fun drawerLayoutUsesStandardPortraitForCommonPhoneBounds() {
        val frame = guildHomeFrameLayout(
            widthDp = 390,
            heightDp = 844,
            statusBarDp = 24,
            navigationBarDp = 24,
        )
        val layout = guildHomeDrawerLayout(frame.widthDp, frame.guildContentHeightDp)

        assertEquals(GuildHomeViewportClass.StandardPortrait, layout.viewportClass)
        assertEquals(2, layout.bodyMaxLines)
        assertEquals(12, layout.horizontalPaddingDp)
        assertEquals(12, layout.bottomPaddingDp)
        assertTrue(layout.maxWidthDp <= 416)
        assertTrue(layout.minArtFocusHeightDp > GuildHomeMinimumArtFocusHeightDp)
    }

    @Test
    fun drawerLayoutGivesTallPortraitMoreBreathingRoomWithoutCoveringTheHub() {
        val frame = guildHomeFrameLayout(
            widthDp = 430,
            heightDp = 960,
            statusBarDp = 24,
            navigationBarDp = 24,
        )
        val layout = guildHomeDrawerLayout(frame.widthDp, frame.guildContentHeightDp)

        assertEquals(GuildHomeViewportClass.TallPortrait, layout.viewportClass)
        assertEquals(2, layout.bodyMaxLines)
        assertEquals(16, layout.horizontalPaddingDp)
        assertEquals(16, layout.bottomPaddingDp)
        assertTrue(layout.maxHeightDp <= 164)
        assertTrue(layout.minArtFocusHeightDp >= 600)
    }
    @Test
    fun firstUseHintLabelsEveryLandmarkBeforeAnyTap() {
        val state = guildHubHintState(
            visitedHotspots = emptySet(),
            selectedHotspot = GuildHubHotspot.QuestTable,
        )

        assertTrue(state.showIntroHint)
        assertEquals(GuildHubHotspot.values().toList(), state.labelledHotspots)
    }

    @Test
    fun firstUseHintReducesToSelectedLandmarkAfterATap() {
        val state = guildHubHintState(
            visitedHotspots = setOf(GuildHubHotspot.LootCache),
            selectedHotspot = GuildHubHotspot.LootCache,
        )

        assertTrue(!state.showIntroHint)
        assertEquals(listOf(GuildHubHotspot.LootCache), state.labelledHotspots)
    }

    @Test
    fun statusBadgesSurfaceRecoveryClaimsQuestReportsAndFacilityAttention() {
        val badges = guildHubStatusBadges(
            phase = PlayPhase.ResultReady,
            pendingLootCount = 3,
            claimableAchievementCount = 2,
            achievementSeals = 4,
            nextMilestoneSeals = 8,
            availableFacilityUpgradeCount = 1,
        )

        assertEquals(4, badges.size)
        assertTrue(badges.any {
            it.hotspot == GuildHubHotspot.LootCache &&
                it.kind == GuildHubStatusBadgeKind.LootRecovery &&
                it.tone == GuildHubStatusTone.Attention &&
                it.value == 3
        })
        assertTrue(badges.any {
            it.hotspot == GuildHubHotspot.Charter &&
                it.kind == GuildHubStatusBadgeKind.CharterClaim &&
                it.tone == GuildHubStatusTone.Attention &&
                it.value == 2
        })
        assertTrue(badges.any {
            it.hotspot == GuildHubHotspot.QuestTable &&
                it.kind == GuildHubStatusBadgeKind.QuestReportReady &&
                it.tone == GuildHubStatusTone.Attention
        })
        assertTrue(badges.any {
            it.hotspot == GuildHubHotspot.Facilities &&
                it.kind == GuildHubStatusBadgeKind.FacilityUpgradeReady &&
                it.value == 1
        })
    }

    @Test
    fun statusBadgesUseCharterProgressWhenNothingIsClaimable() {
        val badges = guildHubStatusBadges(
            phase = PlayPhase.Idle,
            pendingLootCount = 0,
            claimableAchievementCount = 0,
            achievementSeals = 5,
            nextMilestoneSeals = 8,
            availableFacilityUpgradeCount = 0,
        )
        val charterBadge = badges.single { it.hotspot == GuildHubHotspot.Charter }
        val questBadge = badges.single { it.hotspot == GuildHubHotspot.QuestTable }

        assertEquals(GuildHubStatusBadgeKind.CharterProgress, charterBadge.kind)
        assertEquals(GuildHubStatusTone.Progress, charterBadge.tone)
        assertEquals(5, charterBadge.value)
        assertEquals(8, charterBadge.targetValue)
        assertEquals(GuildHubStatusBadgeKind.QuestReady, questBadge.kind)
        assertEquals(GuildHubStatusTone.Calm, questBadge.tone)
    }

    @Test
    fun statusBadgesTrackRunningQuestSecondsAndClampNoisyCounts() {
        val badges = guildHubStatusBadges(
            phase = PlayPhase.Running,
            pendingLootCount = -1,
            claimableAchievementCount = -1,
            achievementSeals = -1,
            nextMilestoneSeals = null,
            availableFacilityUpgradeCount = -1,
            runningSecondsRemaining = 42,
        )

        assertEquals(1, badges.size)
        assertEquals(GuildHubHotspot.QuestTable, badges.first().hotspot)
        assertEquals(GuildHubStatusBadgeKind.QuestRunning, badges.first().kind)
        assertEquals(42, badges.first().value)
    }

    @Test
    fun firstUseHintAnchorsStayInsideReadableHubBounds() {
        GuildHubHotspot.values().forEach { hotspot ->
            val anchor = guildHubHintAnchor(hotspot)
            val statusAnchor = guildHubStatusAnchor(hotspot)
            assertTrue(anchor.xFraction in 0.15f..0.85f)
            assertTrue(anchor.yFraction in 0.15f..0.85f)
            assertTrue(statusAnchor.xFraction in 0.15f..0.85f)
            assertTrue(statusAnchor.yFraction in 0.15f..0.85f)
        }
    }
}