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
    ): ExpeditionResult {
        val specialModifiers = HeroSpecialCatalog.modifiersFor(party, quest)
        val partyPower = PartyPowerCalculator.totalPower(party, equipment) +
            facilityBonusFor(party, facilityPowerBonus) +
            specialModifiers.scoreBonus
        val effectiveRoll = max(roll, specialModifiers.minimumRoll)
        val riskPenalty = ExpeditionEstimator.riskPenalty(quest.risk, specialModifiers)
        val margin = partyPower + effectiveRoll - quest.difficulty - riskPenalty
        val outcome = adjustedOutcome(outcomeForMargin(margin), specialModifiers)

        return ExpeditionResult(
            outcome = outcome,
            reward = rewardFor(outcome, quest, specialModifiers),
            scoreMargin = margin,
        )
    }

    private fun outcomeForMargin(margin: Int): ExpeditionOutcome = when {
        margin >= 80 -> ExpeditionOutcome.GreatSuccess
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

    private fun rewardFor(outcome: ExpeditionOutcome, quest: Quest, modifiers: ExpeditionSpecialModifiers): Reward {
        val multiplier = when (outcome) {
            ExpeditionOutcome.GreatSuccess -> 1.75
            ExpeditionOutcome.Success -> 1.0
            ExpeditionOutcome.PartialSuccess -> 0.65
            ExpeditionOutcome.Failure -> 0.25
            ExpeditionOutcome.RidiculousFailure -> 0.12
        }
        val lootRolls = when (outcome) {
            ExpeditionOutcome.GreatSuccess -> 3
            ExpeditionOutcome.Success -> 2
            ExpeditionOutcome.PartialSuccess -> 1
            ExpeditionOutcome.Failure -> 0
            ExpeditionOutcome.RidiculousFailure -> 0
        } + if (outcome.isAtLeastSuccess()) modifiers.successLootBonus else 0
        val xp = when (outcome) {
            ExpeditionOutcome.GreatSuccess -> 24
            ExpeditionOutcome.Success -> 16
            ExpeditionOutcome.PartialSuccess -> 10
            ExpeditionOutcome.Failure -> 5
            ExpeditionOutcome.RidiculousFailure -> 3
        } + if (outcome.isAtLeastPartial()) modifiers.xpBonus else 0
        val baseGold = max(quest.pityGold, (quest.baseGold * multiplier).roundToInt())
        val goldBonus = if (outcome.isAtLeastSuccess()) baseGold * modifiers.goldBonusPercent / 100 else 0

        return Reward(
            gold = baseGold + goldBonus,
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
)

object ExpeditionEstimator {
    fun estimate(
        party: List<Hero>,
        quest: Quest,
        equipment: List<EquippedLoot> = emptyList(),
        facilityPowerBonus: Int = 0,
    ): ExpeditionEstimate {
        val specialModifiers = HeroSpecialCatalog.modifiersFor(party, quest)
        val partyPower = PartyPowerCalculator.totalPower(party, equipment) +
            facilityBonusFor(party, facilityPowerBonus) +
            specialModifiers.scoreBonus
        val riskPenalty = riskPenalty(quest.risk, specialModifiers)
        val targetPower = targetPower(quest, specialModifiers)
        return ExpeditionEstimate(
            partyPower = partyPower,
            targetPower = targetPower,
            successChancePercent = successChancePercent(partyPower, quest, specialModifiers),
            riskPenalty = riskPenalty,
            specialPowerBonus = specialModifiers.scoreBonus,
            specialRiskReduction = specialModifiers.riskPenaltyReduction.coerceAtMost(riskPenalty(quest.risk)),
            minimumRoll = specialModifiers.minimumRoll,
            goldBonusPercent = specialModifiers.goldBonusPercent,
            bonusLootRolls = specialModifiers.successLootBonus,
        )
    }

    fun targetPower(quest: Quest): Int = quest.difficulty + riskPenalty(quest.risk)

    fun targetPower(quest: Quest, modifiers: ExpeditionSpecialModifiers): Int =
        quest.difficulty + riskPenalty(quest.risk, modifiers)

    fun successChancePercent(partyPower: Int, quest: Quest): Int =
        successChancePercent(partyPower, quest, ExpeditionSpecialModifiers())

    private fun successChancePercent(partyPower: Int, quest: Quest, modifiers: ExpeditionSpecialModifiers): Int {
        val requiredRoll = targetPower(quest, modifiers) - partyPower
        return when {
            requiredRoll <= modifiers.minimumRoll -> 100
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
