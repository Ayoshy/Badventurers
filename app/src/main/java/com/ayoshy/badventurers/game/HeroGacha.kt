package com.ayoshy.badventurers.game

import kotlin.random.Random

data class HeroDefinition(
    val id: String,
    val name: String,
    val heroClass: HeroClass,
    val rarity: HeroRarity,
    val level: Int,
    val stats: HeroStats,
    val trait: Trait,
) {
    fun toHero(): Hero = Hero(
        id = id,
        name = name,
        heroClass = heroClass,
        rarity = rarity,
        level = level,
        stats = stats,
        trait = trait,
    )
}

object HeroCatalog {
    val heroes = listOf(
        HeroDefinition(
            id = "brugg",
            name = "Brugg",
            heroClass = HeroClass.Bruiser,
            rarity = HeroRarity.Common,
            level = 3,
            stats = HeroStats(force = 95, magic = 8, luck = 36, ego = 92, hygiene = 20, badFaith = 64, orientation = 38, paperwork = 10, endurance = 88, charisma = 45),
            trait = Trait.Overconfident,
        ),
        HeroDefinition(
            id = "mira",
            name = "Mira",
            heroClass = HeroClass.ApprenticeMage,
            rarity = HeroRarity.Uncommon,
            level = 2,
            stats = HeroStats(force = 16, magic = 90, luck = 48, ego = 54, hygiene = 72, badFaith = 38, orientation = 44, paperwork = 76, endurance = 32, charisma = 52),
            trait = Trait.ReadsManual,
        ),
        HeroDefinition(
            id = "nell",
            name = "Nell",
            heroClass = HeroClass.Rogueish,
            rarity = HeroRarity.Uncommon,
            level = 2,
            stats = HeroStats(force = 34, magic = 28, luck = 88, ego = 58, hygiene = 46, badFaith = 74, orientation = 84, paperwork = 26, endurance = 48, charisma = 62),
            trait = Trait.SuspiciouslyLucky,
        ),
        HeroDefinition(
            id = "darrik",
            name = "Darrik",
            heroClass = HeroClass.Bruiser,
            rarity = HeroRarity.Common,
            level = 1,
            stats = HeroStats(force = 80, magic = 10, luck = 30, ego = 78, hygiene = 26, badFaith = 58, orientation = 35, paperwork = 12, endurance = 72, charisma = 40),
            trait = Trait.Overconfident,
        ),
        HeroDefinition(
            id = "quill",
            name = "Quill",
            heroClass = HeroClass.BardAccountant,
            rarity = HeroRarity.Rare,
            level = 2,
            stats = HeroStats(force = 28, magic = 42, luck = 55, ego = 64, hygiene = 70, badFaith = 52, orientation = 58, paperwork = 96, endurance = 44, charisma = 82),
            trait = Trait.PainfullyOrganized,
        ),
        HeroDefinition(
            id = "orla",
            name = "Orla",
            heroClass = HeroClass.Bruiser,
            rarity = HeroRarity.Rare,
            level = 2,
            stats = HeroStats(force = 92, magic = 22, luck = 45, ego = 66, hygiene = 50, badFaith = 48, orientation = 52, paperwork = 35, endurance = 86, charisma = 48),
            trait = Trait.ReadsManual,
        ),
        HeroDefinition(
            id = "pippa",
            name = "Pippa",
            heroClass = HeroClass.ApprenticeMage,
            rarity = HeroRarity.Epic,
            level = 3,
            stats = HeroStats(force = 18, magic = 104, luck = 74, ego = 62, hygiene = 78, badFaith = 42, orientation = 64, paperwork = 72, endurance = 42, charisma = 70),
            trait = Trait.SuspiciouslyLucky,
        ),
        HeroDefinition(
            id = "vex",
            name = "Vex",
            heroClass = HeroClass.Rogueish,
            rarity = HeroRarity.Epic,
            level = 3,
            stats = HeroStats(force = 42, magic = 44, luck = 96, ego = 68, hygiene = 54, badFaith = 86, orientation = 98, paperwork = 24, endurance = 60, charisma = 64),
            trait = Trait.SuspiciouslyLucky,
        ),
        HeroDefinition(
            id = "bramble",
            name = "Bramble",
            heroClass = HeroClass.Hunter,
            rarity = HeroRarity.Common,
            level = 1,
            stats = HeroStats(force = 64, magic = 24, luck = 58, ego = 34, hygiene = 48, badFaith = 36, orientation = 94, paperwork = 28, endurance = 76, charisma = 40),
            trait = Trait.ReadsManual,
        ),
        HeroDefinition(
            id = "pax",
            name = "Pax",
            heroClass = HeroClass.Priest,
            rarity = HeroRarity.Uncommon,
            level = 2,
            stats = HeroStats(force = 24, magic = 78, luck = 70, ego = 38, hygiene = 92, badFaith = 22, orientation = 56, paperwork = 66, endurance = 62, charisma = 76),
            trait = Trait.PainfullyOrganized,
        ),
        HeroDefinition(
            id = "sable",
            name = "Sable",
            heroClass = HeroClass.Ninja,
            rarity = HeroRarity.Rare,
            level = 2,
            stats = HeroStats(force = 48, magic = 48, luck = 82, ego = 74, hygiene = 64, badFaith = 78, orientation = 106, paperwork = 32, endurance = 66, charisma = 58),
            trait = Trait.SuspiciouslyLucky,
        ),
        HeroDefinition(
            id = "morrow",
            name = "Morrow",
            heroClass = HeroClass.Necromancer,
            rarity = HeroRarity.Epic,
            level = 3,
            stats = HeroStats(force = 18, magic = 110, luck = 52, ego = 86, hygiene = 30, badFaith = 70, orientation = 60, paperwork = 88, endurance = 46, charisma = 42),
            trait = Trait.Overconfident,
        ),
        HeroDefinition(
            id = "ledger",
            name = "Sir Ledger",
            heroClass = HeroClass.BardAccountant,
            rarity = HeroRarity.Legendary,
            level = 4,
            stats = HeroStats(force = 52, magic = 82, luck = 74, ego = 90, hygiene = 86, badFaith = 64, orientation = 76, paperwork = 120, endurance = 70, charisma = 92),
            trait = Trait.PainfullyOrganized,
        ),
        HeroDefinition(
            id = "paladin",
            name = "Sir Brindle",
            heroClass = HeroClass.Paladin,
            rarity = HeroRarity.Legendary,
            level = 4,
            stats = HeroStats(force = 88, magic = 54, luck = 42, ego = 66, hygiene = 70, badFaith = 18, orientation = 60, paperwork = 84, endurance = 92, charisma = 78),
            trait = Trait.ReadsManual,
        ),
        HeroDefinition(
            id = "comptable",
            name = "Tally Noakes",
            heroClass = HeroClass.Accountant,
            rarity = HeroRarity.Rare,
            level = 2,
            stats = HeroStats(force = 18, magic = 32, luck = 60, ego = 64, hygiene = 78, badFaith = 30, orientation = 86, paperwork = 104, endurance = 40, charisma = 54),
            trait = Trait.PainfullyOrganized,
        ),
        HeroDefinition(
            id = "jardinier",
            name = "Moss Fenlow",
            heroClass = HeroClass.Gardener,
            rarity = HeroRarity.Common,
            level = 1,
            stats = HeroStats(force = 46, magic = 20, luck = 70, ego = 36, hygiene = 88, badFaith = 24, orientation = 92, paperwork = 26, endurance = 68, charisma = 44),
            trait = Trait.ReadsManual,
        ),
        HeroDefinition(
            id = "chevalier_de_la_mort",
            name = "Grave Odo",
            heroClass = HeroClass.DeathKnight,
            rarity = HeroRarity.Epic,
            level = 3,
            stats = HeroStats(force = 74, magic = 84, luck = 40, ego = 70, hygiene = 16, badFaith = 82, orientation = 50, paperwork = 62, endurance = 76, charisma = 36),
            trait = Trait.Overconfident,
        ),
        HeroDefinition(
            id = "chef_cuistot",
            name = "Basil Bouillon",
            heroClass = HeroClass.Chef,
            rarity = HeroRarity.Uncommon,
            level = 2,
            stats = HeroStats(force = 40, magic = 26, luck = 58, ego = 48, hygiene = 94, badFaith = 22, orientation = 64, paperwork = 72, endurance = 60, charisma = 84),
            trait = Trait.PainfullyOrganized,
        ),
        HeroDefinition(
            id = "expert_en_demolition",
            name = "Nix Powderkeg",
            heroClass = HeroClass.DemolitionExpert,
            rarity = HeroRarity.Rare,
            level = 2,
            stats = HeroStats(force = 98, magic = 20, luck = 38, ego = 74, hygiene = 26, badFaith = 84, orientation = 34, paperwork = 24, endurance = 92, charisma = 42),
            trait = Trait.Overconfident,
        ),
        HeroDefinition(
            id = "elementaire_de_sel",
            name = "Salina Brine",
            heroClass = HeroClass.SaltElemental,
            rarity = HeroRarity.Uncommon,
            level = 3,
            stats = HeroStats(force = 18, magic = 84, luck = 74, ego = 38, hygiene = 92, badFaith = 28, orientation = 76, paperwork = 46, endurance = 54, charisma = 40),
            trait = Trait.SuspiciouslyLucky,
        ),
        HeroDefinition(
            id = "troll_stupide",
            name = "Grum",
            heroClass = HeroClass.StupidTroll,
            rarity = HeroRarity.Common,
            level = 1,
            stats = HeroStats(force = 100, magic = 12, luck = 54, ego = 44, hygiene = 16, badFaith = 52, orientation = 28, paperwork = 10, endurance = 100, charisma = 34),
            trait = Trait.Overconfident,
        ),
    )

    val starterHeroes: List<Hero> = heroes.take(3).map { it.toHero() }
    val byId: Map<String, HeroDefinition> = heroes.associateBy { it.id }
    val byRarity: Map<HeroRarity, List<HeroDefinition>> = heroes.groupBy { it.rarity }

    init {
        require(heroes.size == byId.size) { "Hero definition ids must be unique." }
        require(HeroRarity.values().all { byRarity[it]?.isNotEmpty() == true }) { "Every hero rarity needs catalog entries." }
    }
}

object HeroGacha {
    data class RecruitmentProfile(val rarityWeights: List<Pair<HeroRarity, Int>>) {
        val rareOrBetterWeight: Int
            get() = rarityWeights
                .filter { (rarity, _) -> rarity >= HeroRarity.Rare }
                .sumOf { it.second }
    }

    const val RECRUIT_COST = 350
    const val DUPLICATE_REPUTATION_REWARD = 2
    const val LICENSED_TROUBLE_RECRUITMENT_COMPLETED_QUEST_THRESHOLD = 8

    private val baseRarityWeights = listOf(
        HeroRarity.Common to 55,
        HeroRarity.Uncommon to 25,
        HeroRarity.Rare to 13,
        HeroRarity.Epic to 6,
        HeroRarity.Legendary to 1,
    )

    val rarityWeights: List<Pair<HeroRarity, Int>>
        get() = baseRarityWeights

    val baseRecruitmentProfile = RecruitmentProfile(baseRarityWeights)

    val palier2RecruitmentProfile = RecruitmentProfile(
        listOf(
            HeroRarity.Common to 50,
            HeroRarity.Uncommon to 25,
            HeroRarity.Rare to 18,
            HeroRarity.Epic to 6,
            HeroRarity.Legendary to 1,
        ),
    )

    fun recruitmentProfileForProgress(completedQuestCount: Int): RecruitmentProfile =
        if (completedQuestCount >= LICENSED_TROUBLE_RECRUITMENT_COMPLETED_QUEST_THRESHOLD) {
            palier2RecruitmentProfile
        } else {
            baseRecruitmentProfile
        }

    fun summon(pulls: Int, seed: Int = 0): List<Hero> =
        summon(pulls, Random(seed), baseRecruitmentProfile)

    fun summon(
        pulls: Int,
        seed: Int,
        recruitmentProfile: RecruitmentProfile,
    ): List<Hero> = summon(pulls, Random(seed), recruitmentProfile)

    fun summon(
        pulls: Int,
        random: Random,
        recruitmentProfile: RecruitmentProfile = baseRecruitmentProfile,
    ): List<Hero> {
        if (pulls <= 0) return emptyList()

        return List(pulls) {
            val rarity = random.nextWeightedEnum(recruitmentProfile.rarityWeights)
            val pool = HeroCatalog.byRarity.getValue(rarity)
            random.nextEnum(pool).toHero()
        }
    }

    private fun <T> Random.nextEnum(values: List<T>): T =
        values[nextInt(values.size)]

    private fun <T> Random.nextWeightedEnum(values: List<Pair<T, Int>>): T {
        val totalWeight = values.sumOf { it.second }
        var roll = nextInt(totalWeight)

        for ((value, weight) in values) {
            if (roll < weight) return value
            roll -= weight
        }

        return values.last().first
    }
}