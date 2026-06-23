package com.ayoshy.badventurers.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FakeRewardedAdServiceTest {
    @Test
    fun rewardIsUnavailableWithoutReadyResult() {
        assertNull(FakeRewardedAdService.rewardFor(PlaySessionState.initial()))
    }

    @Test
    fun rewardDoublesVisibleQuestGoldAndLootRolls() {
        val ready = PlaySessionState.initial().copy(
            noticeBoardLevel = 2,
            expedition = ExpeditionRun(
                quest = SeedGame.firstQuest,
                startedAtMillis = 1_000L,
                endsAtMillis = 2_000L,
                result = ExpeditionResult(
                    outcome = ExpeditionOutcome.Success,
                    reward = Reward(gold = 100, xp = 10, lootRolls = 2),
                    scoreMargin = 10,
                ),
            ),
        )

        val reward = requireNotNull(FakeRewardedAdService.rewardFor(ready))

        assertEquals(110, reward.extraGold)
        assertEquals(2, reward.extraLootRolls)
    }
}
