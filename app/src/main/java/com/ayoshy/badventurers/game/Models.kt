package com.ayoshy.badventurers.game

enum class HeroClass {
    Bruiser,
    ApprenticeMage,
    Rogueish,
    BardAccountant,
    Ninja,
    Hunter,
    Priest,
    Necromancer,
    Paladin,
    Accountant,
    Gardener,
    DeathKnight,
    Chef,
    DemolitionExpert,
    SaltElemental,
    StupidTroll,
}

enum class HeroRarity {
    Common,
    Uncommon,
    Rare,
    Epic,
    Legendary,
}

enum class Trait {
    Overconfident,
    ReadsManual,
    SuspiciouslyLucky,
    PainfullyOrganized,
}

enum class QuestRisk {
    Low,
    Medium,
    High,
}

enum class QuestTag {
    Ancient,
    Bandit,
    Breach,
    Camp,
    Cave,
    Collapse,
    Contract,
    Curse,
    Debt,
    Escort,
    Exploration,
    Guard,
    Heist,
    Holy,
    Hunt,
    LongQuest,
    Magic,
    Obstacle,
    Paperwork,
    Poison,
    Rot,
    Siege,
    Simple,
    Stealth,
    Swamp,
    Trap,
    Undead,
    Urban,
    Wall,
    Wilderness,
}

enum class QuestDifficultyTier {
    Errand,
    Trouble,
    Hazard,
    Disaster,
    LegendaryMess,
}

data class QuestUnlockCondition(
    val minReputation: Int = 0,
    val minCompletedQuestCount: Int = 0,
    val minNoticeBoardLevel: Int = 0,
    val minTrainingYardLevel: Int = 0,
    val minBunkRoomLevel: Int = 0,
)

data class QuestUnlockRequirement(
    val conditions: List<QuestUnlockCondition> = emptyList(),
)

enum class HeroSpecial {
    RamshackleCharge,
    GlyphReader,
    LightFingers,
    HumanWall,
    AggressiveMinutes,
    TerrainManual,
    UnstableLuck,
    DirtyJackpot,
    FreshTrail,
    CleanBlessing,
    NoTrace,
    NecroLever,
    HostileAudit,
    UnbreakableOath,
    BalancedBooks,
    GreenThumb,
    DeathDiscount,
    MoraleRations,
    PlanBExplosives,
    PreservationSalt,
    CreativeMisunderstanding,
}

enum class ExpeditionOutcome {
    GreatSuccess,
    Success,
    PartialSuccess,
    Failure,
    RidiculousFailure,
}

enum class StatType {
    Force,
    Magic,
    Luck,
    Ego,
    Hygiene,
    BadFaith,
    Orientation,
    Paperwork,
    Endurance,
    Charisma,
}

data class StatBonus(
    val type: StatType,
    val value: Int,
)

data class HeroStats(
    val force: Int,
    val magic: Int,
    val luck: Int,
    val ego: Int,
    val hygiene: Int,
    val badFaith: Int,
    val orientation: Int,
    val paperwork: Int,
    val endurance: Int,
    val charisma: Int,
) {
    val total: Int
        get() = StatType.values().sumOf { valueOf(it) }

    val averagePower: Int
        get() = (total + StatType.values().size / 2) / StatType.values().size

    fun valueOf(type: StatType): Int = when (type) {
        StatType.Force -> force
        StatType.Magic -> magic
        StatType.Luck -> luck
        StatType.Ego -> ego
        StatType.Hygiene -> hygiene
        StatType.BadFaith -> badFaith
        StatType.Orientation -> orientation
        StatType.Paperwork -> paperwork
        StatType.Endurance -> endurance
        StatType.Charisma -> charisma
    }

    fun entries(): List<StatBonus> = StatType.values().map { type -> StatBonus(type, valueOf(type)) }
}

data class Hero(
    val id: String,
    val name: String,
    val heroClass: HeroClass,
    val rarity: HeroRarity,
    val level: Int,
    val xp: Int = 0,
    val stats: HeroStats,
    val trait: Trait,
    val special: HeroSpecial = HeroSpecialCatalog.specialForHero(id),
)

data class EquippedLoot(
    val heroId: String,
    val item: LootItem,
)

data class Quest(
    val id: String,
    val durationSeconds: Int,
    val difficulty: Int,
    val risk: QuestRisk,
    val baseGold: Int,
    val pityGold: Int,
    val partySlots: Int,
    val title: String = id,
    val summary: String = "",
    val tags: List<QuestTag> = emptyList(),
    val recommendedHeroIds: List<String> = emptyList(),
    val unlockRequirement: QuestUnlockRequirement = QuestUnlockRequirement(),
) {
    val difficultyTier: QuestDifficultyTier
        get() = when {
            difficulty < 100 -> QuestDifficultyTier.Errand
            difficulty < 150 -> QuestDifficultyTier.Trouble
            difficulty < 210 -> QuestDifficultyTier.Hazard
            difficulty < 280 -> QuestDifficultyTier.Disaster
            else -> QuestDifficultyTier.LegendaryMess
        }

    fun hasAny(vararg wantedTags: QuestTag): Boolean = wantedTags.any { it in tags }
}

data class Reward(
    val gold: Int,
    val xp: Int,
    val lootRolls: Int,
)

data class ExpeditionResult(
    val outcome: ExpeditionOutcome,
    val reward: Reward,
    val scoreMargin: Int,
)
