package com.ayoshy.badventurers.game

data class HeroRecommendation(
    val hero: Hero,
    val score: Int,
    val successGainPercent: Int,
    val successChancePercent: Int,
    val activeSpecial: Boolean,
    val matchingStats: List<StatType>,
    val statAffinity: Int,
    val equipmentBonus: Int,
    val power: Int,
) {
    val isStrongFit: Boolean
        get() = score >= 40 || successGainPercent >= 10 || activeSpecial
}

object HeroRecommendationScorer {
    fun rankHeroes(
        roster: List<Hero>,
        quest: Quest,
        selectedParty: List<Hero> = emptyList(),
        equipment: List<EquippedLoot> = emptyList(),
        facilityPowerBonus: Int = 0,
        partySlots: Int = quest.partySlots,
    ): List<HeroRecommendation> {
        val normalizedSlots = partySlots.coerceAtLeast(1)
        val currentParty = selectedParty.distinctBy { it.id }.take(normalizedSlots)
        val baseline = ExpeditionEstimator.estimate(
            party = currentParty,
            quest = quest,
            equipment = equipment,
            facilityPowerBonus = facilityPowerBonus,
        )
        val matchingStats = matchingStatsFor(quest)

        return roster.distinctBy { it.id }
            .map { hero ->
                recommendationFor(
                    hero = hero,
                    quest = quest,
                    currentParty = currentParty,
                    baselineSuccessChance = baseline.successChancePercent,
                    matchingStats = matchingStats,
                    equipment = equipment,
                    facilityPowerBonus = facilityPowerBonus,
                    partySlots = normalizedSlots,
                )
            }
            .sortedWith(
                compareByDescending<HeroRecommendation> { it.isStrongFit }
                    .thenByDescending { it.score }
                    .thenByDescending { it.successGainPercent }
                    .thenByDescending { it.power }
                    .thenBy { it.hero.name },
            )
    }

    fun topHeroIds(
        roster: List<Hero>,
        quest: Quest,
        selectedParty: List<Hero> = emptyList(),
        equipment: List<EquippedLoot> = emptyList(),
        facilityPowerBonus: Int = 0,
        partySlots: Int = quest.partySlots,
        limit: Int = maxOf(3, partySlots),
    ): List<String> =
        rankHeroes(
            roster = roster,
            quest = quest,
            selectedParty = selectedParty,
            equipment = equipment,
            facilityPowerBonus = facilityPowerBonus,
            partySlots = partySlots,
        )
            .take(limit.coerceAtLeast(1))
            .map { it.hero.id }

    private fun recommendationFor(
        hero: Hero,
        quest: Quest,
        currentParty: List<Hero>,
        baselineSuccessChance: Int,
        matchingStats: List<StatType>,
        equipment: List<EquippedLoot>,
        facilityPowerBonus: Int,
        partySlots: Int,
    ): HeroRecommendation {
        val selected = currentParty.any { it.id == hero.id }
        val candidateEstimate = if (selected) {
            ExpeditionEstimator.estimate(
                party = currentParty,
                quest = quest,
                equipment = equipment,
                facilityPowerBonus = facilityPowerBonus,
            )
        } else {
            bestEstimateWithCandidate(
                hero = hero,
                currentParty = currentParty,
                quest = quest,
                equipment = equipment,
                facilityPowerBonus = facilityPowerBonus,
                partySlots = partySlots,
            )
        }
        val withoutHeroSuccessChance = if (selected) {
            ExpeditionEstimator.estimate(
                party = currentParty.filterNot { it.id == hero.id },
                quest = quest,
                equipment = equipment,
                facilityPowerBonus = facilityPowerBonus,
            ).successChancePercent
        } else {
            baselineSuccessChance
        }
        val successGainPercent = if (selected) {
            (candidateEstimate.successChancePercent - withoutHeroSuccessChance).coerceAtLeast(0)
        } else {
            (candidateEstimate.successChancePercent - baselineSuccessChance).coerceAtLeast(0)
        }
        val specialModifiers = HeroSpecialCatalog.modifiersFor(listOf(hero), quest)
        val specialScore = specialScore(specialModifiers)
        val equipmentBonus = PartyPowerCalculator.equipmentBonus(hero.id, equipment)
        val statAffinity = statAffinity(hero, matchingStats, equipment)
        val power = PartyPowerCalculator.basePower(hero) + equipmentBonus
        val score = successGainPercent * 5 +
            statAffinity / 3 +
            specialScore +
            power / 6 +
            equipmentBonus / 2

        return HeroRecommendation(
            hero = hero,
            score = score,
            successGainPercent = successGainPercent,
            successChancePercent = candidateEstimate.successChancePercent,
            activeSpecial = specialScore > 0,
            matchingStats = matchingStats,
            statAffinity = statAffinity,
            equipmentBonus = equipmentBonus,
            power = power,
        )
    }

    private fun bestEstimateWithCandidate(
        hero: Hero,
        currentParty: List<Hero>,
        quest: Quest,
        equipment: List<EquippedLoot>,
        facilityPowerBonus: Int,
        partySlots: Int,
    ): ExpeditionEstimate {
        val candidateParties = when {
            currentParty.isEmpty() -> listOf(listOf(hero))
            currentParty.size < partySlots -> listOf(currentParty + hero)
            else -> currentParty.indices.map { index ->
                currentParty.toMutableList().also { it[index] = hero }.distinctBy { it.id }.take(partySlots)
            }
        }

        return candidateParties
            .map { party ->
                ExpeditionEstimator.estimate(
                    party = party,
                    quest = quest,
                    equipment = equipment,
                    facilityPowerBonus = facilityPowerBonus,
                )
            }
            .maxBy { it.successChancePercent }
    }

    private fun specialScore(modifiers: ExpeditionSpecialModifiers): Int =
        modifiers.scoreBonus +
            modifiers.riskPenaltyReduction * 2 +
            modifiers.minimumRoll / 4 +
            modifiers.goldBonusPercent / 3 +
            modifiers.successLootBonus * 10 +
            modifiers.xpBonus * 2 +
            if (modifiers.preventsRidiculousFailure) 8 else 0

    private fun statAffinity(
        hero: Hero,
        matchingStats: List<StatType>,
        equipment: List<EquippedLoot>,
    ): Int {
        if (matchingStats.isEmpty()) return 0

        return matchingStats.sumOf { stat ->
            hero.stats.valueOf(stat) + equipmentStatBonus(hero.id, stat, equipment)
        } / matchingStats.size
    }

    private fun equipmentStatBonus(heroId: String, stat: StatType, equipment: List<EquippedLoot>): Int =
        equipment
            .filter { it.heroId == heroId }
            .sumOf { equipped -> equipped.item.stats.filter { it.type == stat }.sumOf { it.value } }

    private fun matchingStatsFor(quest: Quest): List<StatType> =
        quest.tags.flatMap { tag ->
            when (tag) {
                QuestTag.Ancient -> listOf(StatType.Magic, StatType.Paperwork)
                QuestTag.Bandit -> listOf(StatType.Force, StatType.BadFaith)
                QuestTag.Breach -> listOf(StatType.Force, StatType.Ego)
                QuestTag.Camp -> listOf(StatType.Endurance, StatType.Charisma)
                QuestTag.Cave -> listOf(StatType.Orientation, StatType.Endurance)
                QuestTag.Collapse -> listOf(StatType.Force, StatType.Endurance)
                QuestTag.Contract -> listOf(StatType.Paperwork, StatType.BadFaith)
                QuestTag.Curse -> listOf(StatType.Magic, StatType.Hygiene)
                QuestTag.Debt -> listOf(StatType.Paperwork, StatType.BadFaith)
                QuestTag.Escort -> listOf(StatType.Endurance, StatType.Charisma)
                QuestTag.Exploration -> listOf(StatType.Orientation, StatType.Luck)
                QuestTag.Guard -> listOf(StatType.Endurance, StatType.Force)
                QuestTag.Heist -> listOf(StatType.Luck, StatType.BadFaith)
                QuestTag.Holy -> listOf(StatType.Hygiene, StatType.Magic)
                QuestTag.Hunt -> listOf(StatType.Orientation, StatType.Force)
                QuestTag.LongQuest -> listOf(StatType.Endurance, StatType.Charisma)
                QuestTag.Magic -> listOf(StatType.Magic, StatType.Luck)
                QuestTag.Obstacle -> listOf(StatType.Force, StatType.Orientation)
                QuestTag.Paperwork -> listOf(StatType.Paperwork, StatType.Charisma)
                QuestTag.Poison -> listOf(StatType.Hygiene, StatType.Endurance)
                QuestTag.Rot -> listOf(StatType.Hygiene, StatType.Magic)
                QuestTag.Siege -> listOf(StatType.Force, StatType.Endurance)
                QuestTag.Simple -> listOf(StatType.Ego, StatType.Force)
                QuestTag.Stealth -> listOf(StatType.Luck, StatType.BadFaith)
                QuestTag.Swamp -> listOf(StatType.Hygiene, StatType.Orientation)
                QuestTag.Trap -> listOf(StatType.Luck, StatType.Orientation)
                QuestTag.Undead -> listOf(StatType.Magic, StatType.Hygiene)
                QuestTag.Urban -> listOf(StatType.Paperwork, StatType.Charisma)
                QuestTag.Wall -> listOf(StatType.Force, StatType.Endurance)
                QuestTag.Wilderness -> listOf(StatType.Orientation, StatType.Endurance)
            }
        }.distinct()
}
