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
}

