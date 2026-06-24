package com.ayoshy.badventurers.game

import kotlin.math.abs

enum class ResultCauseKind {
    Plan,
    HeroSpecial,
    Facility,
    Achievement,
}

enum class ResultCauseFacility {
    NoticeBoard,
    TrainingYard,
}

data class ResultCause(
    val id: String,
    val kind: ResultCauseKind,
    val planId: String? = null,
    val heroId: String? = null,
    val heroName: String? = null,
    val facility: ResultCauseFacility? = null,
    val achievementFeature: AchievementFeature? = null,
    val scoreBonus: Int = 0,
    val riskDelta: Int = 0,
    val durationDeltaSeconds: Int = 0,
    val goldBonusPercent: Int = 0,
    val lootBonus: Int = 0,
    val xpBonus: Int = 0,
    val minimumRoll: Int = 0,
    val greatSuccessMarginDelta: Int = 0,
    val preventsRidiculousFailure: Boolean = false,
)

object ResultCauseGenerator {
    fun generate(
        session: PlaySessionState,
        run: ExpeditionRun,
        party: List<Hero>,
        maxCauses: Int = DEFAULT_MAX_CAUSES,
    ): List<ResultCause> {
        val result = run.result ?: return emptyList()
        val causes = mutableListOf<ResultCause>()

        planCause(run)?.let(causes::add)
        causes += heroSpecialCauses(party, run.quest).take(2)
        causes += facilityCauses(session)
        causes += achievementCauses(session, result)

        return causes
            .distinctBy { it.id }
            .take(maxCauses.coerceAtLeast(0))
    }

    private fun planCause(run: ExpeditionRun): ResultCause? {
        val plan = ExpeditionPlanCatalog.byId(run.planId)
        if (plan.id == ExpeditionPlanCatalog.defaultPlanId) return null
        val modifiers = ExpeditionPlanCatalog.modifiersFor(plan.id, run.quest)
        val plannedDuration = ExpeditionPlanCatalog.durationSeconds(run.quest, plan.id)
        return ResultCause(
            id = "plan-${plan.id}",
            kind = ResultCauseKind.Plan,
            planId = plan.id,
            scoreBonus = modifiers.scoreBonus,
            riskDelta = modifiers.riskPenaltyDelta,
            durationDeltaSeconds = plannedDuration - run.quest.durationSeconds,
            goldBonusPercent = modifiers.goldBonusPercent,
            lootBonus = modifiers.successLootBonus,
            greatSuccessMarginDelta = modifiers.greatSuccessMarginDelta,
        )
    }

    private fun heroSpecialCauses(party: List<Hero>, quest: Quest): List<ResultCause> =
        party.mapNotNull { hero ->
            val modifiers = HeroSpecialCatalog.modifiersFor(listOf(hero), quest)
            if (modifiers == ExpeditionSpecialModifiers()) return@mapNotNull null
            ResultCause(
                id = "hero-${hero.id}-${hero.special.name.lowercase()}",
                kind = ResultCauseKind.HeroSpecial,
                heroId = hero.id,
                heroName = hero.name,
                scoreBonus = modifiers.scoreBonus,
                riskDelta = -modifiers.riskPenaltyReduction,
                goldBonusPercent = modifiers.goldBonusPercent,
                lootBonus = modifiers.successLootBonus,
                xpBonus = modifiers.xpBonus,
                minimumRoll = modifiers.minimumRoll,
                preventsRidiculousFailure = modifiers.preventsRidiculousFailure,
            )
        }.sortedByDescending { it.effectWeight() }

    private fun facilityCauses(session: PlaySessionState): List<ResultCause> {
        val causes = mutableListOf<ResultCause>()
        val trainingPower = session.trainingYardPowerBonus()
        if (trainingPower > 0) {
            causes += ResultCause(
                id = "facility-training-yard",
                kind = ResultCauseKind.Facility,
                facility = ResultCauseFacility.TrainingYard,
                scoreBonus = trainingPower,
            )
        }
        val noticeGold = session.noticeBoardGoldBonusPercent()
        if (noticeGold > 0) {
            causes += ResultCause(
                id = "facility-notice-board",
                kind = ResultCauseKind.Facility,
                facility = ResultCauseFacility.NoticeBoard,
                goldBonusPercent = noticeGold,
            )
        }
        return causes
    }

    private fun achievementCauses(session: PlaySessionState, result: ExpeditionResult): List<ResultCause> {
        val causes = mutableListOf<ResultCause>()
        if (
            session.isAchievementFeatureUnlocked(AchievementFeature.InsuranceDesk) &&
            (result.outcome == ExpeditionOutcome.Failure || result.outcome == ExpeditionOutcome.RidiculousFailure)
        ) {
            causes += ResultCause(
                id = "achievement-insurance-desk",
                kind = ResultCauseKind.Achievement,
                achievementFeature = AchievementFeature.InsuranceDesk,
                goldBonusPercent = 30,
            )
        }
        if (
            session.isAchievementFeatureUnlocked(AchievementFeature.RewardChoice) &&
            result.outcome == ExpeditionOutcome.GreatSuccess
        ) {
            causes += ResultCause(
                id = "achievement-reward-choice",
                kind = ResultCauseKind.Achievement,
                achievementFeature = AchievementFeature.RewardChoice,
                lootBonus = 1,
            )
        }
        if (session.isAchievementFeatureUnlocked(AchievementFeature.GuildCharterBonuses)) {
            causes += ResultCause(
                id = "achievement-charter-gold",
                kind = ResultCauseKind.Achievement,
                achievementFeature = AchievementFeature.GuildCharterBonuses,
                goldBonusPercent = 10,
            )
        }
        return causes
    }

    private fun ResultCause.effectWeight(): Int =
        abs(scoreBonus) +
            abs(riskDelta) +
            goldBonusPercent +
            lootBonus * 12 +
            xpBonus * 3 +
            minimumRoll / 2 +
            if (preventsRidiculousFailure) 18 else 0

    private const val DEFAULT_MAX_CAUSES = 4
}
