package com.ayoshy.badventurers.game

import org.junit.Assert.assertEquals
import org.junit.Test

class PlaySessionSnapshotTest {
    @Test
    fun snapshotRoundTripsModifiedIdleState() {
        val state = PlaySessionState.initial().copy(
            gold = 2_000,
            reputation = 29,
            guildLevel = 4,
            noticeBoardLevel = 3,
            lootRolls = 7,
        )

        val snapshot = PlaySessionSnapshot.fromState(state)
        val restored = snapshot.toState()

        assertEquals(PlaySessionSnapshot.CURRENT_VERSION, snapshot.version)
        assertEquals(state, restored)
    }

    @Test
    fun snapshotRestoresIdleEvenWhenSourceHasExpedition() {
        val running = PlaySessionState.initial().startQuest(1_000L, SeedGame.firstQuest)

        val restored = PlaySessionSnapshot.fromState(running).toState()

        assertEquals(PlayPhase.Idle, restored.phase)
        assertEquals(running.gold, restored.gold)
        assertEquals(running.reputation, restored.reputation)
        assertEquals(running.guildLevel, restored.guildLevel)
        assertEquals(running.noticeBoardLevel, restored.noticeBoardLevel)
        assertEquals(running.lootRolls, restored.lootRolls)
        assertEquals(null, restored.expedition)
    }

    @Test
    fun snapshotVersionIsExplicitlyCurrent() {
        val snapshot = PlaySessionSnapshot.initial()

        assertEquals(PlaySessionSnapshot.CURRENT_VERSION, snapshot.version)
    }
}
