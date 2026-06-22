package com.ayoshy.badventurers.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaySessionStateTest {
    private val engine = ExpeditionEngine()
    private val party = SeedGame.heroes

    @Test
    fun startQuestEntersRunningAndDefinesEnd() {
        val startedAt = 1_000L
        val state = PlaySessionState.initial().startQuest(startedAt, SeedGame.firstQuest)

        assertEquals(PlayPhase.Running, state.phase)
        assertNotNull(state.expedition)
        assertEquals(
            startedAt + SeedGame.firstQuest.durationSeconds * 1000L,
            state.expedition?.endsAtMillis,
        )
    }

    @Test
    fun progressIsBoundedBetweenZeroAndOne() {
        val startedAt = 1_000L
        val state = PlaySessionState.initial().startQuest(startedAt, SeedGame.firstQuest)
        val beforeStart = state.progress(startedAt - 500L)
        val midQuest = state.progress(startedAt + 20_000L)
        val afterEnd = state.progress(startedAt + SeedGame.firstQuest.durationSeconds * 1000L + 500L)

        assertTrue(beforeStart in 0.0..1.0)
        assertTrue(midQuest in 0.0..1.0)
        assertTrue(afterEnd in 0.0..1.0)
    }

    @Test
    fun tickBeforeEndDoesNotResolve() {
        val startedAt = 1_000L
        val state = PlaySessionState.initial().startQuest(startedAt, SeedGame.firstQuest)
        val advanced = state.tick(startedAt + 10_000L, engine, party)

        assertEquals(PlayPhase.Running, advanced.phase)
        assertEquals(null, advanced.expedition?.result)
    }

    @Test
    fun tickAfterEndProducesReadyResult() {
        val startedAt = 1_000L
        val state = PlaySessionState.initial().startQuest(startedAt, SeedGame.firstQuest)
        val finished = state.tick(startedAt + SeedGame.firstQuest.durationSeconds * 1000L, engine, party)

        assertEquals(PlayPhase.ResultReady, finished.phase)
        assertNotNull(finished.expedition?.result)
        assertNotNull(finished.expedition?.result?.reward)
    }

    @Test
    fun collectCreditsGoldAndLootOnlyOnce() {
        val startedAt = 1_000L
        val running = PlaySessionState.initial().startQuest(startedAt, SeedGame.firstQuest)
        val finished = running.tick(startedAt + SeedGame.firstQuest.durationSeconds * 1000L, engine, party)
        val reward = finished.expedition!!.result!!.reward
        val collected = finished.collectResult()

        assertEquals(1284 + reward.gold, collected.gold)
        assertEquals(reward.lootRolls, collected.lootRolls)
        assertEquals(PlayPhase.Idle, collected.phase)
        assertEquals(collected, collected.collectResult())
    }

    @Test
    fun upgradeNoticeBoardConsumesGoldAndIncreasesLevel() {
        val upgraded = PlaySessionState.initial().upgradeNoticeBoard()

        assertEquals(684, upgraded.gold)
        assertEquals(2, upgraded.noticeBoardLevel)
    }

    @Test
    fun upgradeNoticeBoardFailsWithoutEnoughGold() {
        val state = PlaySessionState.initial().copy(gold = 599)

        assertEquals(state, state.upgradeNoticeBoard())
    }

    @Test
    fun collectAppliesNoticeBoardBonus() {
        val ready = PlaySessionState.initial().copy(
            noticeBoardLevel = 2,
            expedition = ExpeditionRun(
                quest = SeedGame.firstQuest,
                startedAtMillis = 1_000L,
                endsAtMillis = 2_000L,
                result = ExpeditionResult(
                    outcome = ExpeditionOutcome.Success,
                    reward = Reward(gold = 100, xp = 10, lootRolls = 1),
                    scoreMargin = 10,
                ),
            ),
        )

        val collected = ready.collectResult()

        assertEquals(1_394, collected.gold)
        assertEquals(1, collected.lootRolls)
    }

    @Test
    fun collectGeneratesLootItemsAndJournalEntries() {
        val ready = PlaySessionState.initial().copy(
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

        val collected = ready.collectResult(SeedGame.heroes)

        assertEquals(2, collected.lootItems.size)
        assertTrue(collected.journalEntries.size >= 2)
        assertEquals(PlayPhase.Idle, collected.phase)
    }

    @Test
    fun finishQuestNowProducesResultBeforeEnd() {
        val startedAt = 1_000L
        val state = PlaySessionState.initial().startQuest(startedAt, SeedGame.firstQuest)
        val finished = state.finishQuestNow(engine, party, roll = 100)

        assertEquals(PlayPhase.ResultReady, finished.phase)
        assertNotNull(finished.expedition?.result)
        assertEquals(PlayPhase.Running, state.phase)
    }
}