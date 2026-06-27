package com.ayoshy.badventurers.ui

import com.ayoshy.badventurers.game.ExpeditionPlan
import com.ayoshy.badventurers.game.ExpeditionPlanCatalog
import com.ayoshy.badventurers.game.PlaySessionState
import com.ayoshy.badventurers.game.ProgressionAdviceKind
import com.ayoshy.badventurers.game.ProgressionAdvisor
import com.ayoshy.badventurers.game.Quest
import com.ayoshy.badventurers.game.QuestDifficultyTier
import com.ayoshy.badventurers.game.QuestRisk
import com.ayoshy.badventurers.game.QuestTag

internal enum class QuestActivityRegion {
    LocalJobs,
    DangerousWork,
    SpecialContracts,
}

internal enum class QuestActivityIntent {
    RecommendedNext,
    FirstClear,
    Contract,
    Gold,
    Loot,
    Xp,
    Reputation,
}

internal enum class FirstClearRewardState {
    None,
    Available,
    Claimed,
}

internal data class FirstClearRewardPreview(
    val state: FirstClearRewardState,
    val ticketRewards: Map<String, Int>,
)

internal fun questActivityIntentBadges(
    session: PlaySessionState,
    quest: Quest,
    region: QuestActivityRegion,
): List<QuestActivityIntent> = buildList {
    val recommended = ProgressionAdvisor.recommend(session)
    if (recommended.kind == ProgressionAdviceKind.StartQuest && recommended.questId == quest.id) {
        add(QuestActivityIntent.RecommendedNext)
    }
    if (firstClearRewardPreview(session, quest).state == FirstClearRewardState.Available) {
        add(QuestActivityIntent.FirstClear)
    }
    if (region == QuestActivityRegion.SpecialContracts || quest.hasAny(QuestTag.Contract)) {
        add(QuestActivityIntent.Contract)
    }
    if (!session.isQuestUnlocked(quest) && quest.unlockRequirement.conditions.any { it.minReputation > session.reputation }) {
        add(QuestActivityIntent.Reputation)
    }

    if (quest.baseGold >= 900) {
        add(QuestActivityIntent.Gold)
    }
    if (quest.risk == QuestRisk.High || quest.difficultyTier >= QuestDifficultyTier.Disaster || quest.hasAny(
            QuestTag.Heist,
            QuestTag.Ancient,
            QuestTag.Magic,
        )
    ) {
        add(QuestActivityIntent.Loot)
    }
    if (quest.durationSeconds >= 240 || quest.difficultyTier >= QuestDifficultyTier.Disaster || quest.hasAny(
            QuestTag.LongQuest,
            QuestTag.Siege,
            QuestTag.Camp,
            QuestTag.Undead,
        )
    ) {
        add(QuestActivityIntent.Xp)
    }

}.distinct().take(4)

internal fun firstClearRewardPreview(session: PlaySessionState, quest: Quest): FirstClearRewardPreview {
    val ticketRewards = quest.firstClearTicketRewards.filterValues { count -> count > 0 }
    val state = when {
        ticketRewards.isEmpty() -> FirstClearRewardState.None
        quest.id in session.clearedQuestIds -> FirstClearRewardState.Claimed
        else -> FirstClearRewardState.Available
    }
    return FirstClearRewardPreview(state = state, ticketRewards = ticketRewards)
}

internal fun Quest.matchesActivityRegion(region: QuestActivityRegion): Boolean =
    when (region) {
        QuestActivityRegion.LocalJobs -> !hasAny(QuestTag.Contract) && risk != QuestRisk.High
        QuestActivityRegion.DangerousWork -> !hasAny(QuestTag.Contract) && risk == QuestRisk.High
        QuestActivityRegion.SpecialContracts -> hasAny(QuestTag.Contract) && specialContractPlan() != null
    }

internal fun Quest.specialContractPlan(): ExpeditionPlan? =
    ExpeditionPlanCatalog.availableFor(this).firstOrNull { plan ->
        id in plan.questIds && plan.requiresSpecialContract
    }