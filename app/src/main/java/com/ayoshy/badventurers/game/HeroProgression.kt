package com.ayoshy.badventurers.game

enum class HeroLevelRewardType {
    SpecialistLootRecovery,
    VeteranLootRecovery,
}

data class HeroLevelRewardUnlock(
    val level: Int,
    val type: HeroLevelRewardType,
)

data class HeroXpPreview(
    val beforeLevel: Int,
    val afterLevel: Int,
    val xpBefore: Int,
    val xpAfter: Int,
    val xpForNextLevel: Int,
    val levelsGained: Int,
    val statGains: List<StatBonus>,
    val rewardUnlocks: List<HeroLevelRewardUnlock> = emptyList(),
)

object HeroProgression {
    const val MIN_LEVEL = 1
    const val SPECIALIST_LOOT_RECOVERY_LEVEL = 3
    const val VETERAN_LOOT_RECOVERY_LEVEL = 5

    fun xpForNextLevel(level: Int): Int {
        val n = level.coerceAtLeast(MIN_LEVEL) - MIN_LEVEL
        return 45 + 20 * n + 5 * n * (n - 1) / 2
    }

    fun grantXp(hero: Hero, amount: Int): Hero {
        var advanced = hero.copy(
            level = hero.level.coerceAtLeast(MIN_LEVEL),
            xp = hero.xp.coerceAtLeast(0) + amount.coerceAtLeast(0),
        )

        while (advanced.xp >= xpForNextLevel(advanced.level)) {
            val cost = xpForNextLevel(advanced.level)
            advanced = levelUp(advanced.copy(xp = advanced.xp - cost), advanced.level + 1)
        }

        return advanced
    }

    fun previewGrantXp(hero: Hero, amount: Int): HeroXpPreview {
        val normalized = hero.copy(
            level = hero.level.coerceAtLeast(MIN_LEVEL),
            xp = hero.xp.coerceAtLeast(0),
        )
        val advanced = grantXp(normalized, amount)
        val statGains = StatType.values().mapNotNull { type ->
            val delta = advanced.stats.valueOf(type) - normalized.stats.valueOf(type)
            if (delta > 0) StatBonus(type = type, value = delta) else null
        }

        return HeroXpPreview(
            beforeLevel = normalized.level,
            afterLevel = advanced.level,
            xpBefore = normalized.xp,
            xpAfter = advanced.xp,
            xpForNextLevel = xpForNextLevel(advanced.level),
            levelsGained = (advanced.level - normalized.level).coerceAtLeast(0),
            statGains = statGains,
            rewardUnlocks = rewardUnlocksBetween(normalized, normalized.level, advanced.level),
        )
    }

    fun rewardUnlocksBetween(hero: Hero, beforeLevel: Int, afterLevel: Int): List<HeroLevelRewardUnlock> =
        (beforeLevel + 1..afterLevel).mapNotNull { level -> rewardUnlockAtLevel(hero, level) }

    fun rewardUnlockAtLevel(hero: Hero, level: Int): HeroLevelRewardUnlock? = when {
        level == SPECIALIST_LOOT_RECOVERY_LEVEL && HeroSpecialCatalog.isLootRecoverySpecial(hero.special) -> {
            HeroLevelRewardUnlock(level, HeroLevelRewardType.SpecialistLootRecovery)
        }
        level == VETERAN_LOOT_RECOVERY_LEVEL -> HeroLevelRewardUnlock(level, HeroLevelRewardType.VeteranLootRecovery)
        else -> null
    }

    fun withProgress(hero: Hero, level: Int, xp: Int): Hero {
        val targetLevel = level.coerceAtLeast(hero.level.coerceAtLeast(MIN_LEVEL))
        var restored = hero.copy(level = hero.level.coerceAtLeast(MIN_LEVEL), xp = 0)

        while (restored.level < targetLevel) {
            restored = levelUp(restored, restored.level + 1)
        }

        return grantXp(restored, xp.coerceAtLeast(0))
    }

    fun statGrowthForLevel(hero: Hero, nextLevel: Int): List<StatBonus> {
        val profile = classGrowthProfile(hero.heroClass)
        val pickedStats = buildList {
            add(profile.primary)
            add(profile.primary)
            add(profile.secondary)
            add(traitGrowthStat(hero.trait))
            add(if (nextLevel % 2 == 0) profile.tertiary else heroQuirkStat(hero.id))
            if (hero.rarity >= HeroRarity.Rare && nextLevel % 3 == 0) add(profile.primary)
            if (hero.rarity >= HeroRarity.Epic && nextLevel % 4 == 0) add(profile.secondary)
            if (hero.rarity == HeroRarity.Legendary && nextLevel % 5 == 0) add(traitGrowthStat(hero.trait))
        }

        return StatType.values().mapNotNull { type ->
            val value = pickedStats.count { it == type }
            if (value > 0) StatBonus(type = type, value = value) else null
        }
    }

    private fun levelUp(hero: Hero, nextLevel: Int): Hero = hero.copy(
        level = nextLevel.coerceAtLeast(MIN_LEVEL),
        stats = hero.stats.plus(statGrowthForLevel(hero, nextLevel)),
    )

    private fun classGrowthProfile(heroClass: HeroClass): GrowthProfile = when (heroClass) {
        HeroClass.Bruiser -> GrowthProfile(StatType.Force, StatType.Endurance, StatType.Ego)
        HeroClass.ApprenticeMage -> GrowthProfile(StatType.Magic, StatType.Paperwork, StatType.Luck)
        HeroClass.Rogueish -> GrowthProfile(StatType.Luck, StatType.Orientation, StatType.BadFaith)
        HeroClass.BardAccountant -> GrowthProfile(StatType.Paperwork, StatType.Charisma, StatType.Luck)
        HeroClass.Ninja -> GrowthProfile(StatType.Orientation, StatType.Luck, StatType.BadFaith)
        HeroClass.Hunter -> GrowthProfile(StatType.Orientation, StatType.Endurance, StatType.Luck)
        HeroClass.Priest -> GrowthProfile(StatType.Hygiene, StatType.Magic, StatType.Charisma)
        HeroClass.Necromancer -> GrowthProfile(StatType.Magic, StatType.BadFaith, StatType.Paperwork)
        HeroClass.Paladin -> GrowthProfile(StatType.Endurance, StatType.Hygiene, StatType.Force)
        HeroClass.Accountant -> GrowthProfile(StatType.Paperwork, StatType.Orientation, StatType.Charisma)
        HeroClass.Gardener -> GrowthProfile(StatType.Hygiene, StatType.Orientation, StatType.Endurance)
        HeroClass.DeathKnight -> GrowthProfile(StatType.Endurance, StatType.Magic, StatType.BadFaith)
        HeroClass.Chef -> GrowthProfile(StatType.Hygiene, StatType.Charisma, StatType.Endurance)
        HeroClass.DemolitionExpert -> GrowthProfile(StatType.Force, StatType.BadFaith, StatType.Endurance)
        HeroClass.SaltElemental -> GrowthProfile(StatType.Magic, StatType.Hygiene, StatType.Luck)
        HeroClass.StupidTroll -> GrowthProfile(StatType.Force, StatType.Endurance, StatType.BadFaith)
    }

    private fun traitGrowthStat(trait: Trait): StatType = when (trait) {
        Trait.Overconfident -> StatType.Ego
        Trait.ReadsManual -> StatType.Paperwork
        Trait.SuspiciouslyLucky -> StatType.Luck
        Trait.PainfullyOrganized -> StatType.Paperwork
    }

    private fun heroQuirkStat(heroId: String): StatType {
        val index = heroId.fold(0) { acc, char -> acc + char.code }.mod(StatType.values().size)
        return StatType.values()[index]
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

    private data class GrowthProfile(
        val primary: StatType,
        val secondary: StatType,
        val tertiary: StatType,
    )
}