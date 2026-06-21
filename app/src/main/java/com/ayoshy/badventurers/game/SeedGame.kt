package com.ayoshy.badventurers.game

object SeedGame {
    val heroes = listOf(
        Hero(
            id = "brugg",
            name = "Brugg",
            heroClass = HeroClass.Bruiser,
            level = 3,
            stats = HeroStats(might = 18, wits = 4, sneak = 3, grit = 14, luck = 6, ego = 22),
            trait = Trait.Overconfident,
        ),
        Hero(
            id = "mira",
            name = "Mira",
            heroClass = HeroClass.ApprenticeMage,
            level = 2,
            stats = HeroStats(might = 3, wits = 18, sneak = 5, grit = 6, luck = 8, ego = 10),
            trait = Trait.ReadsManual,
        ),
        Hero(
            id = "nell",
            name = "Nell",
            heroClass = HeroClass.Rogueish,
            level = 2,
            stats = HeroStats(might = 6, wits = 8, sneak = 18, grit = 7, luck = 16, ego = 9),
            trait = Trait.SuspiciouslyLucky,
        ),
    )

    val firstQuest = Quest(
        id = "cave_minor_regrets",
        durationSeconds = 45,
        difficulty = 120,
        risk = QuestRisk.Low,
        baseGold = 320,
        pityGold = 25,
    )
}

