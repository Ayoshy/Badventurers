package com.ayoshy.badventurers.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExpeditionEngineTest {
    private val engine = ExpeditionEngine()

    @Test
    fun highRollProducesGreatSuccess() {
        val result = engine.resolve(
            party = SeedGame.heroes,
            quest = SeedGame.firstQuest.copy(difficulty = 80),
            roll = 100,
        )

        assertEquals(ExpeditionOutcome.GreatSuccess, result.outcome)
        assertTrue(result.reward.gold > SeedGame.firstQuest.baseGold)
        assertEquals(3, result.reward.lootRolls)
    }

    @Test
    fun failureStillPaysPityGold() {
        val result = engine.resolve(
            party = SeedGame.heroes.take(1),
            quest = SeedGame.firstQuest.copy(difficulty = 260, pityGold = 25),
            roll = 0,
        )

        assertEquals(ExpeditionOutcome.RidiculousFailure, result.outcome)
        assertTrue(result.reward.gold >= 25)
        assertTrue(result.reward.xp > 0)
    }

    @Test
    fun facilityPowerBonusImprovesEstimateAndMargin() {
        val party = SeedGame.heroes.take(1)
        val quest = SeedGame.firstQuest.copy(difficulty = 100, tags = emptyList(), recommendedHeroIds = emptyList())
        val partyPower = PartyPowerCalculator.totalPower(party)
        val specialBonus = HeroSpecialCatalog.modifiersFor(party, quest).scoreBonus

        val estimate = ExpeditionEstimator.estimate(
            party = party,
            quest = quest,
            facilityPowerBonus = 8,
        )
        val result = engine.resolve(
            party = party,
            quest = quest,
            roll = 0,
            facilityPowerBonus = 8,
        )

        assertEquals(partyPower + 8, estimate.partyPower)
        assertEquals(partyPower + 8 - quest.difficulty, result.scoreMargin)
        assertEquals(0, ExpeditionEstimator.estimate(emptyList(), quest, facilityPowerBonus = 8).partyPower)
    }
    @Test
    fun estimatorCalculatesSuccessChanceFromQuestTarget() {
        val quest = SeedGame.firstQuest.copy(difficulty = 100, risk = QuestRisk.Medium)

        assertEquals(108, ExpeditionEstimator.targetPower(quest))
        assertEquals(100, ExpeditionEstimator.successChancePercent(partyPower = 108, quest = quest))
        assertEquals(50, ExpeditionEstimator.successChancePercent(partyPower = 58, quest = quest))
        assertEquals(0, ExpeditionEstimator.successChancePercent(partyPower = 7, quest = quest))
    }

    @Test
    fun matchingHeroSpecialAddsScoreToTaggedQuest() {
        val party = listOf(HeroCatalog.byId.getValue("expert_en_demolition").toHero())
        val quest = SeedGame.firstQuest.copy(
            difficulty = 180,
            risk = QuestRisk.Low,
            tags = listOf(QuestTag.Wall, QuestTag.Obstacle),
        )
        val specialBonus = HeroSpecialCatalog.modifiersFor(party, quest).scoreBonus

        val result = engine.resolve(party = party, quest = quest, roll = 0)

        assertEquals(30, specialBonus)
        assertEquals(PartyPowerCalculator.totalPower(party) + specialBonus - quest.difficulty, result.scoreMargin)
    }

    @Test
    fun paperworkSpecialsIncreaseSuccessfulGoldRewards() {
        val party = listOf(HeroCatalog.byId.getValue("ledger").toHero())
        val quest = SeedGame.questById.getValue("bandit_tax_office").copy(difficulty = 80)

        val result = engine.resolve(party = party, quest = quest, roll = 100)

        assertEquals(20, HeroSpecialCatalog.modifiersFor(party, quest).goldBonusPercent)
        assertTrue(result.reward.gold > (quest.baseGold * 1.75).toInt())
    }


    @Test
    fun rushPlanShortensDurationAndRaisesRisk() {
        val party = SeedGame.heroes.take(2)
        val quest = SeedGame.firstQuest.copy(tags = emptyList(), recommendedHeroIds = emptyList())

        val standard = ExpeditionEstimator.estimate(party = party, quest = quest)
        val rushed = ExpeditionEstimator.estimate(
            party = party,
            quest = quest,
            planId = ExpeditionPlanCatalog.rushTheJobId,
        )

        assertTrue(rushed.durationSeconds < quest.durationSeconds)
        assertEquals(standard.riskPenalty + 12, rushed.riskPenalty)
        assertEquals(standard.partyPower - 5, rushed.partyPower)
    }

    @Test
    fun lootPriorityAddsLootOnSuccessButCostsMargin() {
        val quest = SeedGame.firstQuest.copy(difficulty = 60, risk = QuestRisk.Low, tags = emptyList(), recommendedHeroIds = emptyList())
        val standard = engine.resolve(party = SeedGame.heroes, quest = quest, roll = 100)
        val greedy = engine.resolve(
            party = SeedGame.heroes,
            quest = quest,
            roll = 100,
            planId = ExpeditionPlanCatalog.lootPriorityId,
        )

        assertEquals(standard.reward.lootRolls + 1, greedy.reward.lootRolls)
        assertTrue(greedy.scoreMargin < standard.scoreMargin)
    }
    @Test
    fun specialContractClausesAddXxlGoldAndLootPayoff() {
        val quest = SeedGame.questById.getValue("paperwork_toll_of_chaos").copy(
            difficulty = 60,
            risk = QuestRisk.Low,
            tags = emptyList(),
            recommendedHeroIds = emptyList(),
        )
        val standard = engine.resolve(party = SeedGame.heroes, quest = quest, roll = 100)
        val contracted = engine.resolve(
            party = SeedGame.heroes,
            quest = quest,
            roll = 100,
            planId = ExpeditionPlanCatalog.paperworkTollId,
        )

        assertTrue(contracted.reward.gold > standard.reward.gold)
        assertTrue(contracted.reward.lootRolls >= standard.reward.lootRolls + 2)
    }
    @Test
    fun protectiveSpecialCanStopRidiculousFailureOnCursedQuest() {
        val party = listOf(HeroCatalog.byId.getValue("pax").toHero())
        val quest = SeedGame.questById.getValue("crypt_of_unpaid_debts").copy(difficulty = 999)

        val result = engine.resolve(party = party, quest = quest, roll = 0)

        assertEquals(ExpeditionOutcome.Failure, result.outcome)
    }}
