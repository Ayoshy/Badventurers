package com.ayoshy.badventurers.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ResultCauseGeneratorTest {
    @Test
    fun generateExplainsPlanHeroSpecialsAndFacilities() {
        val party = listOf(
            HeroCatalog.byId.getValue("brugg").toHero(),
            HeroCatalog.byId.getValue("quill").toHero(),
        )
        val quest = SeedGame.firstQuest.copy(
            tags = listOf(QuestTag.Breach, QuestTag.Paperwork, QuestTag.Simple),
            recommendedHeroIds = emptyList(),
        )
        val result = ExpeditionEngine().resolve(
            party = party,
            quest = quest,
            roll = 80,
            planId = ExpeditionPlanCatalog.rushTheJobId,
        )
        val run = ExpeditionRun(
            quest = quest,
            partyHeroIds = party.map { it.id },
            startedAtMillis = 1_000L,
            endsAtMillis = 32_000L,
            result = result,
            planId = ExpeditionPlanCatalog.rushTheJobId,
        )
        val session = PlaySessionState.initial().copy(
            heroes = party,
            noticeBoardLevel = 2,
            trainingYardLevel = 2,
            expedition = run,
        )

        val causes = ResultCauseGenerator.generate(session, run, party, maxCauses = 10)

        val plan = requireNotNull(causes.firstOrNull { it.kind == ResultCauseKind.Plan })
        assertEquals(ExpeditionPlanCatalog.rushTheJobId, plan.planId)
        assertTrue(plan.durationDeltaSeconds < 0)
        assertEquals(12, plan.riskDelta)
        assertEquals(5, plan.goldBonusPercent)

        assertNotNull(causes.firstOrNull { it.kind == ResultCauseKind.HeroSpecial && it.heroId == "brugg" })
        assertNotNull(causes.firstOrNull { it.kind == ResultCauseKind.HeroSpecial && it.heroId == "quill" })
        assertNotNull(causes.firstOrNull { it.facility == ResultCauseFacility.TrainingYard })
        assertNotNull(causes.firstOrNull { it.facility == ResultCauseFacility.NoticeBoard })
    }

    @Test
    fun generateExplainsAchievementUnlocksWhenRelevant() {
        val party = SeedGame.heroes.take(1)
        val quest = SeedGame.firstQuest.copy(tags = emptyList(), recommendedHeroIds = emptyList())
        val session = stateWithClaimedSeals(12)
        val failedRun = ExpeditionRun(
            quest = quest,
            partyHeroIds = party.map { it.id },
            startedAtMillis = 1_000L,
            endsAtMillis = 2_000L,
            result = ExpeditionResult(
                outcome = ExpeditionOutcome.Failure,
                reward = Reward(gold = 20, xp = 5, lootRolls = 0),
                scoreMargin = -10,
            ),
        )
        val greatRun = failedRun.copy(
            result = ExpeditionResult(
                outcome = ExpeditionOutcome.GreatSuccess,
                reward = Reward(gold = 200, xp = 24, lootRolls = 3),
                scoreMargin = 90,
            ),
        )

        val failureCauses = ResultCauseGenerator.generate(session, failedRun, party, maxCauses = 10)
        val greatCauses = ResultCauseGenerator.generate(session, greatRun, party, maxCauses = 10)

        assertNotNull(failureCauses.firstOrNull { it.achievementFeature == AchievementFeature.InsuranceDesk })
        assertNotNull(greatCauses.firstOrNull { it.achievementFeature == AchievementFeature.RewardChoice })
    }

    @Test
    fun defaultCauseListStaysCompactAndStartsWithPlan() {
        val party = SeedGame.heroes
        val quest = SeedGame.firstQuest
        val run = ExpeditionRun(
            quest = quest,
            partyHeroIds = party.map { it.id },
            startedAtMillis = 1_000L,
            endsAtMillis = 32_000L,
            result = ExpeditionEngine().resolve(
                party = party,
                quest = quest,
                roll = 100,
                planId = ExpeditionPlanCatalog.lootPriorityId,
            ),
            planId = ExpeditionPlanCatalog.lootPriorityId,
        )

        val causes = ResultCauseGenerator.generate(PlaySessionState.initial().copy(expedition = run), run, party)

        assertTrue(causes.size <= 4)
        assertEquals(ResultCauseKind.Plan, causes.first().kind)
    }

    private fun stateWithClaimedSeals(minSeals: Int): PlaySessionState {
        var claimedSeals = 0
        val progress = AchievementCatalog.definitions.map { definition ->
            if (claimedSeals < minSeals) {
                claimedSeals += definition.sealReward
                AchievementProgress(
                    achievementId = definition.id,
                    current = definition.target,
                    completedAtMillis = 1L,
                    claimedAtMillis = 1L,
                    seen = true,
                )
            } else {
                AchievementProgress(achievementId = definition.id)
            }
        }
        return PlaySessionState.initial().copy(achievementProgress = progress)
    }
}
