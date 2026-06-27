package com.ayoshy.badventurers.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GuildReputationLadderTest {
    @Test
    fun reputationMapsToNamedGuildRanks() {
        assertEquals("folding_table_charter", GuildReputationLadder.tierFor(-5).id)
        assertEquals("folding_table_charter", GuildReputationLadder.tierFor(0).id)
        assertEquals("back_room_permit", GuildReputationLadder.tierFor(3).id)
        assertEquals("notice_board_office", GuildReputationLadder.tierFor(8).id)
        assertEquals("licensed_guild_desk", GuildReputationLadder.tierFor(15).id)
        assertEquals("regional_liability_hall", GuildReputationLadder.tierFor(30).id)
        assertEquals("crown_adjacent_franchise", GuildReputationLadder.tierFor(48).id)
        assertEquals("sideways_tower_chapter", GuildReputationLadder.tierFor(60).id)
    }

    @Test
    fun progressTracksCurrentXpAndNextRankThreshold() {
        val progress = GuildReputationLadder.progressFor(14)

        assertEquals("notice_board_office", progress.currentTier.id)
        assertEquals("licensed_guild_desk", progress.nextTier?.id)
        assertEquals(14, progress.reputation)
        assertEquals(6, progress.pointsIntoTier)
        assertEquals(1, progress.pointsToNextTier)
        assertEquals(6f / 7f, progress.progressFraction)
    }

    @Test
    fun maxRankHasNoNextThreshold() {
        val progress = GuildReputationLadder.progressFor(90)

        assertEquals("sideways_tower_chapter", progress.currentTier.id)
        assertNull(progress.nextTier)
        assertEquals(30, progress.pointsIntoTier)
        assertEquals(0, progress.pointsToNextTier)
        assertEquals(1f, progress.progressFraction)
    }

    @Test
    fun passiveGoldUsesReputationRankInsteadOfLegacyGuildLevel() {
        val base = PlaySessionState.initial().copy(reputation = 0, guildLevel = 99)
        val ranked = base.copy(reputation = 30)

        assertEquals(1, base.guildReputationRank())
        assertEquals(5, ranked.guildReputationRank())
        assertEquals(base.passiveGoldPerHour() + 24, ranked.passiveGoldPerHour())
    }
}