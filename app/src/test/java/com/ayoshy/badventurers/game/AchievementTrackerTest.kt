package com.ayoshy.badventurers.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AchievementTrackerTest {
    @Test
    fun collectResultCompletesQuestAndOutcomeAchievements() {
        val ready = readyState(
            gold = 0,
            outcome = ExpeditionOutcome.GreatSuccess,
            reward = Reward(gold = 100, xp = 10, lootRolls = 2),
        )

        val collected = ready.collectResult()

        assertTrue(collected.isCompleted("first_expedition"))
        assertTrue(collected.isCompleted("came_back_alive"))
        assertTrue(collected.isCompleted("almost_competent"))
        assertEquals(3, collected.claimableAchievements().count {
            it.id in setOf("first_expedition", "came_back_alive", "almost_competent")
        })
    }

    @Test
    fun claimAchievementGrantsRewardsAndSealsOnlyOnce() {
        val collected = readyState(
            gold = 0,
            outcome = ExpeditionOutcome.Success,
            reward = Reward(gold = 100, xp = 10, lootRolls = 0),
        ).collectResult()

        val claimed = collected.claimAchievement("first_expedition", nowMillis = 5_000L)
        val claimedAgain = claimed.claimAchievement("first_expedition", nowMillis = 6_000L)

        assertEquals(collected.gold + 100, claimed.gold)
        assertEquals(1, claimed.achievementSeals())
        assertEquals(claimed, claimedAgain)
    }

    @Test
    fun insuranceDeskImprovesFailurePityGold() {
        val insured = PlaySessionState.initial().copy(
            gold = 0,
            achievementProgress = claimedProgress("first_expedition", "came_back_alive", "professional_regret_ii"),
        )
        val result = ExpeditionResult(
            outcome = ExpeditionOutcome.Failure,
            reward = Reward(gold = 100, xp = 5, lootRolls = 0),
            scoreMargin = -5,
        )

        assertEquals(true, insured.isAchievementFeatureUnlocked(AchievementFeature.InsuranceDesk))
        assertEquals(130, insured.collectableRewardGold(result))
    }

    @Test
    fun rewardChoiceAddsOneLootRollOnGreatSuccess() {
        val choiceReady = PlaySessionState.initial().copy(
            achievementProgress = claimedProgress(
                "first_expedition",
                "came_back_alive",
                "professional_regret_ii",
                "all_risk_no_notes",
                "tour_of_bad_ideas",
                "specialist_roster",
                "very_much_legendary",
            ),
        )
        val result = ExpeditionResult(
            outcome = ExpeditionOutcome.GreatSuccess,
            reward = Reward(gold = 100, xp = 10, lootRolls = 3),
            scoreMargin = 100,
        )

        assertEquals(true, choiceReady.isAchievementFeatureUnlocked(AchievementFeature.RewardChoice))
        assertEquals(4, choiceReady.collectableLootRolls(result))
    }

    @Test
    fun snapshotPreservesAchievementProgress() {
        val state = readyState(
            gold = 0,
            outcome = ExpeditionOutcome.Success,
            reward = Reward(gold = 100, xp = 10, lootRolls = 0),
        ).collectResult().claimAchievement("first_expedition", nowMillis = 5_000L)

        val restored = PlaySessionSnapshot.fromState(state).toState()

        assertEquals(state.achievementProgress, restored.achievementProgress)
        assertEquals(state.achievementSeals(), restored.achievementSeals())
    }

    private fun PlaySessionState.isCompleted(achievementId: String): Boolean {
        val definition = AchievementCatalog.byId.getValue(achievementId)
        return achievementProgressFor(definition).isCompleted(definition)
    }

    private fun readyState(
        gold: Int,
        outcome: ExpeditionOutcome,
        reward: Reward,
    ): PlaySessionState = PlaySessionState.initial().copy(
        gold = gold,
        expedition = ExpeditionRun(
            quest = SeedGame.firstQuest,
            startedAtMillis = 1_000L,
            endsAtMillis = 2_000L,
            result = ExpeditionResult(
                outcome = outcome,
                reward = reward,
                scoreMargin = 100,
            ),
        ),
    )

    private fun claimedProgress(vararg achievementIds: String): List<AchievementProgress> {
        val claimedIds = achievementIds.toSet()
        return AchievementCatalog.definitions.map { definition ->
            if (definition.id in claimedIds) {
                AchievementProgress(
                    achievementId = definition.id,
                    current = definition.target,
                    completedAtMillis = 1_000L,
                    claimedAtMillis = 2_000L,
                    seen = true,
                )
            } else {
                AchievementProgress(achievementId = definition.id)
            }
        }
    }
}
