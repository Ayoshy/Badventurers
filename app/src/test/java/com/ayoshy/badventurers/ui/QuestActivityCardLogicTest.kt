package com.ayoshy.badventurers.ui

import com.ayoshy.badventurers.game.PlaySessionState
import com.ayoshy.badventurers.game.RecruitmentTicketCatalog
import com.ayoshy.badventurers.game.SeedGame
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QuestActivityCardLogicTest {
    @Test
    fun activityRegionsAreExclusiveAndBalancedAcrossQuestMaps() {
        val localIds = SeedGame.quests
            .filter { quest -> quest.matchesActivityRegion(QuestActivityRegion.LocalJobs) }
            .map { it.id }
        val dangerousIds = SeedGame.quests
            .filter { quest -> quest.matchesActivityRegion(QuestActivityRegion.DangerousWork) }
            .map { it.id }
        val contractIds = SeedGame.quests
            .filter { quest -> quest.matchesActivityRegion(QuestActivityRegion.SpecialContracts) }
            .map { it.id }

        assertEquals(
            listOf(
                "cave_minor_regrets",
                "forest_of_wrong_turns",
                "salted_swamp_chapel",
                "moonlit_smuggler_run",
                "the_hungry_siege",
            ),
            localIds,
        )
        assertEquals(
            listOf(
                "the_last_locked_door",
                "crypt_of_unpaid_debts",
                "the_tower_built_sideways",
            ),
            dangerousIds,
        )
        assertEquals(
            listOf(
                "bandit_tax_office",
                "paperwork_toll_of_chaos",
                "licensed_guild_caravan_haunt",
                "notary_night_patrol",
                "inspectorate_cove_banquet",
                "wedding_with_too_many_oaths",
                "the_sunken_toll_booth",
                "the_crowns_missing_receipt",
            ),
            contractIds,
        )

        SeedGame.quests.forEach { quest ->
            val visibleRegions = QuestActivityRegion.values().count { region -> quest.matchesActivityRegion(region) }
            assertEquals("${quest.id} should appear in exactly one activity region", 1, visibleRegions)
        }
    }

    @Test
    fun activityBadgesExposeFirstClearContractsAndClaimedState() {
        val quest = SeedGame.questById.getValue("wedding_with_too_many_oaths")
        val ready = PlaySessionState.initial().copy(
            completedQuestCount = 12,
            reputation = 48,
            noticeBoardLevel = 3,
            trainingYardLevel = 3,
            bunkRoomLevel = 2,
            specialContracts = 1,
        )

        val firstClear = firstClearRewardPreview(ready, quest)
        val badges = questActivityIntentBadges(ready, quest, QuestActivityRegion.SpecialContracts)

        assertTrue(ready.isQuestUnlocked(quest))
        assertEquals(FirstClearRewardState.Available, firstClear.state)
        assertEquals(
            mapOf(
                RecruitmentTicketCatalog.RARE_CONTRACT_TICKET_ID to 1,
                RecruitmentTicketCatalog.BLANK_CONTRACT_ID to 1,
            ),
            firstClear.ticketRewards,
        )
        assertTrue(QuestActivityIntent.FirstClear in badges)
        assertTrue(QuestActivityIntent.Contract in badges)

        val claimed = ready.copy(clearedQuestIds = setOf(quest.id))

        assertEquals(FirstClearRewardState.Claimed, firstClearRewardPreview(claimed, quest).state)
        assertFalse(QuestActivityIntent.FirstClear in questActivityIntentBadges(claimed, quest, QuestActivityRegion.SpecialContracts))
    }

    @Test
    fun lockedDangerousWorkCallsOutReputationGateAndLootIntent() {
        val state = PlaySessionState.initial()
        val quest = SeedGame.questById.getValue("the_last_locked_door")
        val badges = questActivityIntentBadges(state, quest, QuestActivityRegion.DangerousWork)

        assertFalse(state.isQuestUnlocked(quest))
        assertTrue(QuestActivityIntent.Reputation in badges)
        assertTrue(QuestActivityIntent.Loot in badges)
    }
}