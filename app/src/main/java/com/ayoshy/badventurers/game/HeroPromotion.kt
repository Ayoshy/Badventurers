package com.ayoshy.badventurers.game

object HeroPromotion {
    const val MIN_RANK = 0
    const val MAX_RANK = 4

    fun normalizedRank(hero: Hero): Int = hero.promotionRank.coerceIn(MIN_RANK, MAX_RANK)

    fun canPromote(hero: Hero): Boolean = normalizedRank(hero) < MAX_RANK

    fun nextRank(hero: Hero): Int = (normalizedRank(hero) + 1).coerceAtMost(MAX_RANK)

    fun promote(hero: Hero): Hero? =
        if (canPromote(hero)) previewPromoted(hero, nextRank(hero)) else null

    fun bonusesForRank(hero: Hero): List<StatBonus> {
        val profile = classPromotionProfile(hero.heroClass)
        val pickedStats = listOf(
            profile.primary,
            profile.primary,
            profile.primary,
            profile.secondary,
            profile.secondary,
            traitPromotionStat(hero.trait),
        )

        return StatType.values().mapNotNull { type ->
            val value = pickedStats.count { it == type }
            if (value > 0) StatBonus(type = type, value = value) else null
        }
    }

    fun previewPromoted(hero: Hero, targetRank: Int): Hero {
        val currentRank = normalizedRank(hero)
        val rank = targetRank.coerceIn(MIN_RANK, MAX_RANK)
        if (rank <= currentRank) return hero.copy(promotionRank = currentRank)

        return ((currentRank + 1)..rank).fold(hero.copy(promotionRank = currentRank)) { promoted, _ ->
            promoted.copy(
                stats = promoted.stats.plus(bonusesForRank(promoted)),
                promotionRank = (promoted.promotionRank + 1).coerceAtMost(MAX_RANK),
            )
        }
    }

    private fun classPromotionProfile(heroClass: HeroClass): PromotionProfile = when (heroClass) {
        HeroClass.Bruiser -> PromotionProfile(StatType.Force, StatType.Endurance)
        HeroClass.ApprenticeMage -> PromotionProfile(StatType.Magic, StatType.Paperwork)
        HeroClass.Rogueish -> PromotionProfile(StatType.Luck, StatType.Orientation)
        HeroClass.BardAccountant -> PromotionProfile(StatType.Paperwork, StatType.Charisma)
        HeroClass.Ninja -> PromotionProfile(StatType.Orientation, StatType.Luck)
        HeroClass.Hunter -> PromotionProfile(StatType.Orientation, StatType.Endurance)
        HeroClass.Priest -> PromotionProfile(StatType.Hygiene, StatType.Magic)
        HeroClass.Necromancer -> PromotionProfile(StatType.Magic, StatType.BadFaith)
        HeroClass.Paladin -> PromotionProfile(StatType.Endurance, StatType.Hygiene)
        HeroClass.Accountant -> PromotionProfile(StatType.Paperwork, StatType.Orientation)
        HeroClass.Gardener -> PromotionProfile(StatType.Hygiene, StatType.Orientation)
        HeroClass.DeathKnight -> PromotionProfile(StatType.Endurance, StatType.Magic)
        HeroClass.Chef -> PromotionProfile(StatType.Hygiene, StatType.Charisma)
        HeroClass.DemolitionExpert -> PromotionProfile(StatType.Force, StatType.BadFaith)
        HeroClass.SaltElemental -> PromotionProfile(StatType.Magic, StatType.Hygiene)
        HeroClass.StupidTroll -> PromotionProfile(StatType.Force, StatType.Endurance)
    }

    private fun traitPromotionStat(trait: Trait): StatType = when (trait) {
        Trait.Overconfident -> StatType.Ego
        Trait.ReadsManual -> StatType.Paperwork
        Trait.SuspiciouslyLucky -> StatType.Luck
        Trait.PainfullyOrganized -> StatType.Paperwork
    }

    private fun HeroStats.plus(bonuses: List<StatBonus>): HeroStats = copy(
        force = force + bonuses.sumFor(StatType.Force),
        magic = magic + bonuses.sumFor(StatType.Magic),
        luck = luck + bonuses.sumFor(StatType.Luck),
        ego = ego + bonuses.sumFor(StatType.Ego),
        hygiene = hygiene + bonuses.sumFor(StatType.Hygiene),
        badFaith = badFaith + bonuses.sumFor(StatType.BadFaith),
        orientation = orientation + bonuses.sumFor(StatType.Orientation),
        paperwork = paperwork + bonuses.sumFor(StatType.Paperwork),
        endurance = endurance + bonuses.sumFor(StatType.Endurance),
        charisma = charisma + bonuses.sumFor(StatType.Charisma),
    )

    private fun List<StatBonus>.sumFor(type: StatType): Int =
        filter { it.type == type }.sumOf { it.value }

    private data class PromotionProfile(
        val primary: StatType,
        val secondary: StatType,
    )
}