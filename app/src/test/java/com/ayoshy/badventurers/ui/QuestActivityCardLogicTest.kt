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
    fun activityRegionsSeparateLocalDangerousAndContractWork() {
        val local = SeedGame.firstQuest
        val dangerous = SeedGame.questById.getValue("the_last_locked_door")

        assertTrue(local.matchesActivityRegion(QuestActivityRegion.LocalJobs))
        assertFalse(local.matchesActivityRegion(QuestActivityRegion.DangerousWork))
        assertTrue(dangerous.matchesActivityRegion(QuestActivityRegion.DangerousWork))
        assertFalse(dangerous.matchesActivityRegion(QuestActivityRegion.LocalJobs))
        assertTrue(SeedGame.quests.all { quest -> quest.matchesActivityRegion(QuestActivityRegion.SpecialContracts) })
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