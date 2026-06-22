package com.ayoshy.badventurers.game

data class PlaySessionState(
    val gold: Int = 1284,
    val reputation: Int = 17,
    val guildLevel: Int = 3,
    val noticeBoardLevel: Int = 1,
    val lootRolls: Int = 0,
    val expedition: ExpeditionRun? = null,
) {
    val phase: PlayPhase
        get() = when {
            expedition == null -> PlayPhase.Idle
            expedition.result == null -> PlayPhase.Running
            else -> PlayPhase.ResultReady
        }

    fun progress(nowMillis: Long): Double {
        val run = expedition ?: return 0.0
        val durationMillis = (run.endsAtMillis - run.startedAtMillis).coerceAtLeast(1L)
        return when {
            nowMillis <= run.startedAtMillis -> 0.0
            nowMillis >= run.endsAtMillis -> 1.0
            else -> ((nowMillis - run.startedAtMillis).toDouble() / durationMillis).coerceIn(0.0, 1.0)
        }
    }

    fun startQuest(nowMillis: Long, quest: Quest): PlaySessionState {
        if (phase != PlayPhase.Idle) return this
        return copy(
            expedition = ExpeditionRun(
                quest = quest,
                startedAtMillis = nowMillis,
                endsAtMillis = nowMillis + quest.durationSeconds * 1000L,
            ),
        )
    }

    fun tick(
        nowMillis: Long,
        engine: ExpeditionEngine,
        party: List<Hero>,
    ): PlaySessionState {
        val run = expedition ?: return this
        if (run.result != null || nowMillis < run.endsAtMillis) return this
        return copy(expedition = run.copy(result = engine.resolve(party = party, quest = run.quest)))
    }

    fun collectResult(): PlaySessionState {
        val run = expedition ?: return this
        val result = run.result ?: return this
        val noticeBoardBonus = result.reward.gold * (noticeBoardLevel - 1) / 10
        return copy(
            gold = gold + result.reward.gold + noticeBoardBonus,
            lootRolls = lootRolls + result.reward.lootRolls,
            expedition = null,
        )
    }

    fun upgradeNoticeBoard(cost: Int = 600): PlaySessionState {
        if (gold < cost) return this
        return copy(
            gold = gold - cost,
            noticeBoardLevel = noticeBoardLevel + 1,
        )
    }

    companion object {
        fun initial(): PlaySessionState = PlaySessionState()
    }
}

data class ExpeditionRun(
    val quest: Quest,
    val startedAtMillis: Long,
    val endsAtMillis: Long,
    val result: ExpeditionResult? = null,
)

sealed interface PlayPhase {
    data object Idle : PlayPhase
    data object Running : PlayPhase
    data object ResultReady : PlayPhase
}
