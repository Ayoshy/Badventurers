package com.ayoshy.badventurers.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FirstThirtyMinutePacingTest {
    private val engine = ExpeditionEngine()

    @Test
    fun newPlayerStartUsesBalancedEconomyInsteadOfDebugResources() {
        val state = PlaySessionState.initial()

        assertEquals(0, state.gold)
        assertEquals(0, state.reputation)
        assertEquals(1, state.guildLevel)
        assertEquals(1, state.noticeBoardLevel)
        assertEquals(1, state.trainingYardLevel)
        assertEquals(1, state.bunkRoomLevel)
        assertEquals(HeroCatalog.starterHeroes, state.heroes)
    }

    @Test
    fun firstThirtyMinutesReachCoreUpgradeChoicesAndFirstHighRiskUnlock() {
        var run = PacingRun(PlaySessionState.initial())

        assertEquals(ProgressionAdviceKind.StartQuest, ProgressionAdvisor.recommend(run.session).kind)

        run = run.complete("cave_minor_regrets", roll = 80)

        assertTrue("first quest should finish inside the 0-3 minute band", run.elapsedSeconds <= 180)
        assertTrue("first reward should make the first facility affordable", run.session.canUpgradeFacility(GuildFacility.NoticeBoard))
        assertTrue("first reward should also make recruitment visible as a choice", run.session.gold >= HeroGacha.RECRUIT_COST)
        assertTrue("participating heroes should have visible XP progress", run.session.heroes.any { it.xp > 0 })
        assertEquals(ProgressionAdviceKind.HandleLootRewards, ProgressionAdvisor.recommend(run.session).kind)

        run = run.keepPendingLoot()
        assertEquals(ProgressionAdviceKind.UpgradeNoticeBoard, ProgressionAdvisor.recommend(run.session).kind)

        run = run.copy(session = run.session.upgradeNoticeBoard())
        assertEquals(2, run.session.noticeBoardLevel)

        run = run.complete("forest_of_wrong_turns", roll = 60).keepPendingLoot()
        assertTrue(run.session.canUpgradeFacility(GuildFacility.TrainingYard))

        run = run.copy(session = run.session.upgradeTrainingYard())
        assertEquals(2, run.session.trainingYardLevel)

        run = run.complete("bandit_tax_office", roll = 60).keepPendingLoot()
        assertTrue(run.session.canUpgradeFacility(GuildFacility.BunkRoom))

        run = run.copy(session = run.session.upgradeBunkRoom())
        assertEquals(2, run.session.bunkRoomLevel)

        run = run.complete("salted_swamp_chapel", roll = 60).keepPendingLoot()
        run = run.complete("moonlit_smuggler_run", roll = 60).keepPendingLoot()
        run = run.complete("the_hungry_siege", roll = 60).keepPendingLoot()

        assertTrue("six clears should land well before the 30-minute prototype target", run.elapsedSeconds <= 30 * 60)
        assertEquals(6, run.session.completedQuestCount)
        assertTrue(run.session.isQuestUnlocked(SeedGame.questById.getValue("the_last_locked_door")))
        assertTrue(run.session.isQuestUnlocked(SeedGame.questById.getValue("crypt_of_unpaid_debts")))
        assertTrue("at least one starter should level during the first pacing route", run.session.heroes.any { it.level > 3 })
        assertTrue("the route should leave enough gold for another meaningful action", run.session.gold >= HeroGacha.RECRUIT_COST)
        assertTrue("early quest achievements should introduce reputation pressure gently", run.session.reputation >= 1)
    }

    private fun PacingRun.keepPendingLoot(): PacingRun {
        val kept = session.pendingLootItems.fold(session) { nextSession, item ->
            nextSession.keepPendingLoot(item)
        }
        return copy(session = kept)
    }

    private fun PacingRun.complete(questId: String, roll: Int): PacingRun {
        val quest = SeedGame.questById.getValue(questId)
        val party = session.selectedPartyForQuest(quest, session.heroes)
        val startedAt = elapsedSeconds * 1_000L
        val running = session.startQuest(startedAt, quest, party)

        assertEquals("$questId should be unlocked in this pacing route", PlayPhase.Running, running.phase)

        val finished = running.finishQuestNow(engine, party, roll = roll)
        assertEquals(PlayPhase.ResultReady, finished.phase)

        val collected = finished.collectResult(party = party)
        val claimed = collected.claimAllAchievements(nowMillis = startedAt + quest.durationSeconds * 1_000L)
        return copy(
            session = claimed,
            elapsedSeconds = elapsedSeconds + quest.durationSeconds,
        )
    }

    private data class PacingRun(
        val session: PlaySessionState,
        val elapsedSeconds: Int = 0,
    )
}