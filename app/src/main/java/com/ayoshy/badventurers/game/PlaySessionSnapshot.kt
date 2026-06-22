package com.ayoshy.badventurers.game

data class PlaySessionSnapshot(
    val version: Int,
    val gold: Int,
    val reputation: Int,
    val guildLevel: Int,
    val noticeBoardLevel: Int,
    val lootRolls: Int,
) {
    fun toState(): PlaySessionState {
        return PlaySessionState(
            gold = gold,
            reputation = reputation,
            guildLevel = guildLevel,
            noticeBoardLevel = noticeBoardLevel,
            lootRolls = lootRolls,
            expedition = null,
        )
    }

    companion object {
        const val CURRENT_VERSION = 1

        fun initial(): PlaySessionSnapshot {
            return fromState(PlaySessionState.initial())
        }

        fun fromState(state: PlaySessionState): PlaySessionSnapshot {
            return PlaySessionSnapshot(
                version = CURRENT_VERSION,
                gold = state.gold,
                reputation = state.reputation,
                guildLevel = state.guildLevel,
                noticeBoardLevel = state.noticeBoardLevel,
                lootRolls = state.lootRolls,
            )
        }
    }
}
