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
    ): ExpeditionResult {
        val partyPower = PartyPowerCalculator.totalPower(party)
        val riskPenalty = when (quest.risk) {
            QuestRisk.Low -> 0
            QuestRisk.Medium -> 8
            QuestRisk.High -> 16
        }
        val margin = partyPower + roll - quest.difficulty - riskPenalty
        val outcome = when {
            margin >= 80 -> ExpeditionOutcome.GreatSuccess
            margin >= 25 -> ExpeditionOutcome.Success
            margin >= 0 -> ExpeditionOutcome.PartialSuccess
            margin >= -35 -> ExpeditionOutcome.Failure
            else -> ExpeditionOutcome.RidiculousFailure
        }

        return ExpeditionResult(
            outcome = outcome,
            reward = rewardFor(outcome, quest),
            scoreMargin = margin,
        )
    }

    private fun rewardFor(outcome: ExpeditionOutcome, quest: Quest): Reward {
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
        }
        val xp = when (outcome) {
            ExpeditionOutcome.GreatSuccess -> 24
            ExpeditionOutcome.Success -> 16
            ExpeditionOutcome.PartialSuccess -> 10
            ExpeditionOutcome.Failure -> 5
            ExpeditionOutcome.RidiculousFailure -> 3
        }

        return Reward(
            gold = max(quest.pityGold, (quest.baseGold * multiplier).roundToInt()),
            xp = xp,
            lootRolls = lootRolls,
        )
    }
}

object PartyPowerCalculator {
    fun totalPower(party: List<Hero>): Int =
        party.sumOf { hero ->
            with(hero.stats) {
                might * 2 + wits * 2 + sneak + grit + luck + ego / 2 + hero.level * 4
            } + traitBonus(hero.trait)
        }

    private fun traitBonus(trait: Trait): Int =
        when (trait) {
            Trait.Overconfident -> 4
            Trait.ReadsManual -> 6
            Trait.SuspiciouslyLucky -> 5
            Trait.PainfullyOrganized -> 7
        }
}

