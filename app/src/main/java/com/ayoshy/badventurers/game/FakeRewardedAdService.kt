package com.ayoshy.badventurers.game

data class FakeRewardedAdReward(
    val extraGold: Int,
    val extraLootRolls: Int,
)

object FakeRewardedAdService {
    fun rewardFor(session: PlaySessionState): FakeRewardedAdReward? {
        val result = session.expedition?.result ?: return null
        return FakeRewardedAdReward(
            extraGold = session.questGoldWithNoticeBoard(result.reward.gold),
            extraLootRolls = result.reward.lootRolls,
        )
    }
}
