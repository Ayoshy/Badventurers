package com.ayoshy.badventurers.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressionAdvisorTest {
    @Test
    fun readyResultRecommendsViewingTheReportBeforeOtherProgression() {
        val ready = PlaySessionState.initial().copy(
            expedition = ExpeditionRun(
                quest = SeedGame.firstQuest,
                partyHeroIds = SeedGame.heroes.map { it.id },
                startedAtMillis = 1_000L,
                endsAtMillis = 2_000L,
                result = ExpeditionResult(
                    outcome = ExpeditionOutcome.Success,
                    reward = Reward(gold = 100, xp = 16, lootRolls = 1),
                    scoreMargin = 30,
                ),
            ),
        )

        val advice = ProgressionAdvisor.recommend(ready)

        assertEquals(ProgressionAdviceKind.ViewQuestReport, advice.kind)
        assertEquals(SeedGame.firstQuest.id, advice.questId)
    }

    @Test
    fun pendingLootRecommendsHandlingRewardsBeforeNewSpending() {
        val item = testItem("test_reward_hat", bonus = 4)
        val state = PlaySessionState.initial().copy(
            pendingLootItems = listOf(item),
            gold = 10_000,
        )

        val advice = ProgressionAdvisor.recommend(state)

        assertEquals(ProgressionAdviceKind.HandleLootRewards, advice.kind)
        assertEquals(item.id, advice.itemId)
    }

    @Test
    fun lockedPartySizeQuestRecommendsBunkRoomWhenAffordable() {
        val quest = SeedGame.questById.getValue("the_hungry_siege")
        val state = PlaySessionState.initial().copy(
            gold = 2_000,
            reputation = 0,
            completedQuestCount = 0,
            bunkRoomLevel = 1,
        )

        val advice = ProgressionAdvisor.recommend(state, selectedQuest = quest)

        assertEquals(ProgressionAdviceKind.UpgradeBunkRoom, advice.kind)
        assertEquals(GuildFacility.BunkRoom, advice.facility)
        assertEquals(0, advice.missingGold)
    }

    @Test
    fun lockedFacilityQuestReportsMissingGoldForExactUpgrade() {
        val quest = SeedGame.questById.getValue("salted_swamp_chapel")
        val state = PlaySessionState.initial().copy(
            gold = 100,
            reputation = 0,
            completedQuestCount = 0,
            trainingYardLevel = 1,
        )

        val advice = ProgressionAdvisor.recommend(state, selectedQuest = quest)

        assertEquals(ProgressionAdviceKind.UpgradeTrainingYard, advice.kind)
        assertEquals(GuildFacility.TrainingYard, advice.facility)
        assertTrue(advice.missingGold > 0)
    }

    @Test
    fun lowOddsQuestRecommendsBetterFitPartyWhenItImprovesSuccess() {
        val weak = testHero("weak", stat = 8)
        val strong = testHero("strong", stat = 120)
        val quest = SeedGame.firstQuest.copy(
            difficulty = 120,
            risk = QuestRisk.Low,
            partySlots = 1,
            tags = listOf(QuestTag.Cave),
            unlockRequirement = QuestUnlockRequirement(),
        )
        val state = PlaySessionState.initial().copy(
            gold = 0,
            heroes = listOf(weak, strong),
        )

        val advice = ProgressionAdvisor.recommend(
            state = state,
            selectedQuest = quest,
            selectedParty = listOf(weak),
        )

        assertEquals(ProgressionAdviceKind.ImprovePartyFit, advice.kind)
        assertEquals(listOf(strong.id), advice.heroIds)
        assertTrue(requireNotNull(advice.projectedSuccessChancePercent) > requireNotNull(advice.currentSuccessChancePercent))
    }

    @Test
    fun lowOddsQuestWithoutBetterPartyRecommendsTrainingYard() {
        val quest = SeedGame.firstQuest.copy(
            difficulty = 500,
            risk = QuestRisk.Low,
            tags = emptyList(),
            recommendedHeroIds = emptyList(),
        )
        val state = PlaySessionState.initial().copy(
            gold = 1_000,
        )

        val advice = ProgressionAdvisor.recommend(
            state = state,
            selectedQuest = quest,
            selectedParty = SeedGame.heroes,
        )

        assertEquals(ProgressionAdviceKind.UpgradeTrainingYard, advice.kind)
        assertEquals(GuildFacility.TrainingYard, advice.facility)
    }

    @Test
    fun idleStateWithGoldRecommendsEarlyNoticeBoardUpgrade() {
        val state = PlaySessionState.initial().copy(gold = 700)

        val advice = ProgressionAdvisor.recommend(state)

        assertEquals(ProgressionAdviceKind.UpgradeNoticeBoard, advice.kind)
        assertEquals(GuildFacility.NoticeBoard, advice.facility)
    }

    @Test
    fun usefulInventoryRecommendsEquipWhenNoFacilityIsAffordable() {
        val item = testItem("test_boots", bonus = 8)
        val state = PlaySessionState.initial().copy(
            gold = 0,
            lootItems = listOf(item),
        )

        val advice = ProgressionAdvisor.recommend(state)

        assertEquals(ProgressionAdviceKind.EquipLoot, advice.kind)
        assertEquals(item.id, advice.itemId)
        assertTrue(advice.heroIds.isNotEmpty())
    }

    private fun testHero(id: String, stat: Int): Hero =
        Hero(
            id = id,
            name = id.replaceFirstChar { it.uppercase() },
            heroClass = HeroClass.Bruiser,
            rarity = HeroRarity.Common,
            level = 1,
            stats = HeroStats(
                force = stat,
                magic = stat,
                luck = stat,
                ego = stat,
                hygiene = stat,
                badFaith = stat,
                orientation = stat,
                paperwork = stat,
                endurance = stat,
                charisma = stat,
            ),
            trait = Trait.ReadsManual,
            special = HeroSpecial.RamshackleCharge,
        )

    private fun testItem(id: String, bonus: Int): LootItem =
        LootItem(
            id = id,
            name = id,
            rarity = LootRarity.Uncommon,
            slot = LootSlot.Footwear,
            stats = listOf(StatBonus(StatType.Orientation, bonus)),
            icon = LootIcon.Boots,
        )
}
