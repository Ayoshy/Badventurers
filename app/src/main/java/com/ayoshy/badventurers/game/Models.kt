package com.ayoshy.badventurers.game

enum class HeroClass {
    Bruiser,
    ApprenticeMage,
    Rogueish,
    BardAccountant,
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

enum class ExpeditionOutcome {
    GreatSuccess,
    Success,
    PartialSuccess,
    Failure,
    RidiculousFailure,
}

data class HeroStats(
    val might: Int,
    val wits: Int,
    val sneak: Int,
    val grit: Int,
    val luck: Int,
    val ego: Int,
)

data class Hero(
    val id: String,
    val name: String,
    val heroClass: HeroClass,
    val level: Int,
    val stats: HeroStats,
    val trait: Trait,
)

data class Quest(
    val id: String,
    val durationSeconds: Int,
    val difficulty: Int,
    val risk: QuestRisk,
    val baseGold: Int,
    val pityGold: Int,
)

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

