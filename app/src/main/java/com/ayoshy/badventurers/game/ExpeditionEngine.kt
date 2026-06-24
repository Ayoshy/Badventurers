package com.ayoshy.badventurers.game

import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.random.Random

class ExpeditionEngine(
    private val random: Random = Random.Default,
) {
    fun resolve(
        party: List<Hero>,
        quest: Quest,
        roll: Int = random.nextInt(0, 101),
        equipment: List<EquippedLoot> = emptyList(),
        facilityPowerBonus: Int = 0,
        planId: String = ExpeditionPlanCatalog.defaultPlanId,
    ): ExpeditionResult {
        val specialModifiers = HeroSpecialCatalog.modifiersFor(party, quest)
        val planModifiers = ExpeditionPlanCatalog.modifiersFor(planId, quest)
        val partyPower = PartyPowerCalculator.totalPower(party, equipment) +
            facilityBonusFor(party, facilityPowerBonus) +
            specialModifiers.scoreBonus +
            planModifiers.scoreBonus
        val effectiveRoll = max(roll, specialModifiers.minimumRoll)
        val riskPenalty = ExpeditionEstimator.riskPenalty(quest.risk, specialModifiers, planModifiers)
        val margin = partyPower + effectiveRoll - quest.difficulty - riskPenalty
        val outcome = adjustedOutcome(outcomeForMargin(margin, planModifiers), specialModifiers)

        return ExpeditionResult(
            outcome = outcome,
            reward = rewardFor(outcome, quest, specialModifiers, planModifiers),
            scoreMargin = margin,
        )
    }

    private fun outcomeForMargin(
        margin: Int,
        planModifiers: ExpeditionPlanModifiers = ExpeditionPlanModifiers(),
    ): ExpeditionOutcome = when {
        margin >= 80 + planModifiers.greatSuccessMarginDelta -> ExpeditionOutcome.GreatSuccess
        margin >= 25 -> ExpeditionOutcome.Success
        margin >= 0 -> ExpeditionOutcome.PartialSuccess
        margin >= -35 -> ExpeditionOutcome.Failure
        else -> ExpeditionOutcome.RidiculousFailure
    }

    private fun adjustedOutcome(outcome: ExpeditionOutcome, modifiers: ExpeditionSpecialModifiers): ExpeditionOutcome =
        if (outcome == ExpeditionOutcome.RidiculousFailure && modifiers.preventsRidiculousFailure) {
            ExpeditionOutcome.Failure
        } else {
            outcome
        }

    private fun rewardFor(
        outcome: ExpeditionOutcome,
        quest: Quest,
        specialModifiers: ExpeditionSpecialModifiers,
        planModifiers: ExpeditionPlanModifiers,
    ): Reward {
        val multiplier = when (outcome) {
            ExpeditionOutcome.GreatSuccess -> 1.75
            ExpeditionOutcome.Success -> 1.0
            ExpeditionOutcome.PartialSuccess -> 0.65
            ExpeditionOutcome.Failure -> 0.25
            ExpeditionOutcome.RidiculousFailure -> 0.12
        }
        val lootRolls = (when (outcome) {
            ExpeditionOutcome.GreatSuccess -> 3
            ExpeditionOutcome.Success -> 2
            ExpeditionOutcome.PartialSuccess -> 1
            ExpeditionOutcome.Failure -> 0
            ExpeditionOutcome.RidiculousFailure -> 0
        } + if (outcome.isAtLeastSuccess()) {
            specialModifiers.successLootBonus + planModifiers.successLootBonus
        } else {
            0
        }).coerceAtLeast(0)
        val xp = when (outcome) {
            ExpeditionOutcome.GreatSuccess -> 24
            ExpeditionOutcome.Success -> 16
            ExpeditionOutcome.PartialSuccess -> 10
            ExpeditionOutcome.Failure -> 5
            ExpeditionOutcome.RidiculousFailure -> 3
        } + if (outcome.isAtLeastPartial()) specialModifiers.xpBonus else 0
        val baseGold = max(quest.pityGold, (quest.baseGold * multiplier).roundToInt())
        val totalGoldBonusPercent = specialModifiers.goldBonusPercent + planModifiers.goldBonusPercent
        val goldBonus = if (outcome.isAtLeastSuccess()) baseGold * totalGoldBonusPercent / 100 else 0

        return Reward(
            gold = max(quest.pityGold, baseGold + goldBonus),
            xp = xp,
            lootRolls = lootRolls,
        )
    }
}

data class ExpeditionEstimate(
    val partyPower: Int,
    val targetPower: Int,
    val successChancePercent: Int,
    val riskPenalty: Int,
    val specialPowerBonus: Int = 0,
    val specialRiskReduction: Int = 0,
    val minimumRoll: Int = 0,
    val goldBonusPercent: Int = 0,
    val bonusLootRolls: Int = 0,
    val durationSeconds: Int = 0,
    val planPowerModifier: Int = 0,
    val planRiskModifier: Int = 0,
    val planGoldBonusPercent: Int = 0,
    val planBonusLootRolls: Int = 0,
    val rewardGoldBonusPercent: Int = 0,
    val rewardBonusLootRolls: Int = 0,
    val greatSuccessTargetMargin: Int = 80,
)

object ExpeditionEstimator {
    fun estimate(
        party: List<Hero>,
        quest: Quest,
        equipment: List<EquippedLoot> = emptyList(),
        facilityPowerBonus: Int = 0,
        planId: String = ExpeditionPlanCatalog.defaultPlanId,
    ): ExpeditionEstimate {
        val specialModifiers = HeroSpecialCatalog.modifiersFor(party, quest)
        val planModifiers = ExpeditionPlanCatalog.modifiersFor(planId, quest)
        val partyPower = PartyPowerCalculator.totalPower(party, equipment) +
            facilityBonusFor(party, facilityPowerBonus) +
            specialModifiers.scoreBonus +
            planModifiers.scoreBonus
        val riskPenalty = riskPenalty(quest.risk, specialModifiers, planModifiers)
        val targetPower = targetPower(quest, specialModifiers, planModifiers)
        return ExpeditionEstimate(
            partyPower = partyPower,
            targetPower = targetPower,
            successChancePercent = successChancePercent(partyPower, quest, specialModifiers, planModifiers),
            riskPenalty = riskPenalty,
            specialPowerBonus = specialModifiers.scoreBonus,
            specialRiskReduction = specialModifiers.riskPenaltyReduction.coerceAtMost(riskPenalty(quest.risk)),
            minimumRoll = specialModifiers.minimumRoll,
            goldBonusPercent = specialModifiers.goldBonusPercent,
            bonusLootRolls = specialModifiers.successLootBonus,
            durationSeconds = ExpeditionPlanCatalog.durationSeconds(quest, planId),
            planPowerModifier = planModifiers.scoreBonus,
            planRiskModifier = planModifiers.riskPenaltyDelta,
            planGoldBonusPercent = planModifiers.goldBonusPercent,
            planBonusLootRolls = planModifiers.successLootBonus,
            rewardGoldBonusPercent = specialModifiers.goldBonusPercent + planModifiers.goldBonusPercent,
            rewardBonusLootRolls = specialModifiers.successLootBonus + planModifiers.successLootBonus,
            greatSuccessTargetMargin = 80 + planModifiers.greatSuccessMarginDelta,
        )
    }

    fun targetPower(quest: Quest): Int = quest.difficulty + riskPenalty(quest.risk)

    fun targetPower(quest: Quest, modifiers: ExpeditionSpecialModifiers): Int =
        quest.difficulty + riskPenalty(quest.risk, modifiers)

    fun targetPower(
        quest: Quest,
        specialModifiers: ExpeditionSpecialModifiers,
        planModifiers: ExpeditionPlanModifiers,
    ): Int = quest.difficulty + riskPenalty(quest.risk, specialModifiers, planModifiers)

    fun successChancePercent(partyPower: Int, quest: Quest): Int =
        successChancePercent(partyPower, quest, ExpeditionSpecialModifiers(), ExpeditionPlanModifiers())

    private fun successChancePercent(
        partyPower: Int,
        quest: Quest,
        specialModifiers: ExpeditionSpecialModifiers,
        planModifiers: ExpeditionPlanModifiers,
    ): Int {
        val requiredRoll = targetPower(quest, specialModifiers, planModifiers) - partyPower
        return when {
            requiredRoll <= specialModifiers.minimumRoll -> 100
            requiredRoll > 100 -> 0
            else -> ((101 - requiredRoll) * 100 / 101).coerceIn(0, 100)
        }
    }

    fun riskPenalty(risk: QuestRisk): Int =
        when (risk) {
            QuestRisk.Low -> 0
            QuestRisk.Medium -> 8
            QuestRisk.High -> 16
        }

    fun riskPenalty(risk: QuestRisk, modifiers: ExpeditionSpecialModifiers): Int =
        (riskPenalty(risk) - modifiers.riskPenaltyReduction).coerceAtLeast(0)

    fun riskPenalty(
        risk: QuestRisk,
        specialModifiers: ExpeditionSpecialModifiers,
        planModifiers: ExpeditionPlanModifiers,
    ): Int =
        (riskPenalty(risk) - specialModifiers.riskPenaltyReduction + planModifiers.riskPenaltyDelta).coerceAtLeast(0)
}

private fun facilityBonusFor(party: List<Hero>, facilityPowerBonus: Int): Int =
    if (party.isEmpty()) 0 else facilityPowerBonus

private fun ExpeditionOutcome.isAtLeastPartial(): Boolean = when (this) {
    ExpeditionOutcome.GreatSuccess,
    ExpeditionOutcome.Success,
    ExpeditionOutcome.PartialSuccess -> true
    ExpeditionOutcome.Failure,
    ExpeditionOutcome.RidiculousFailure -> false
}

private fun ExpeditionOutcome.isAtLeastSuccess(): Boolean = when (this) {
    ExpeditionOutcome.GreatSuccess,
    ExpeditionOutcome.Success -> true
    ExpeditionOutcome.PartialSuccess,
    ExpeditionOutcome.Failure,
    ExpeditionOutcome.RidiculousFailure -> false
}

object PartyPowerCalculator {
    fun totalPower(party: List<Hero>, equipment: List<EquippedLoot> = emptyList()): Int =
        party.sumOf { hero ->
            basePower(hero) + equipmentBonus(hero.id, equipment)
        }

    fun basePower(hero: Hero): Int =
        hero.stats.averagePower + hero.level * 4 + traitBonus(hero.trait)

    fun equipmentBonus(heroId: String, equipment: List<EquippedLoot>): Int =
        equipment.filter { it.heroId == heroId }.sumOf { it.item.bonus }

    private fun traitBonus(trait: Trait): Int =
        when (trait) {
            Trait.Overconfident -> 4
            Trait.ReadsManual -> 6
            Trait.SuspiciouslyLucky -> 5
            Trait.PainfullyOrganized -> 7
        }
}
