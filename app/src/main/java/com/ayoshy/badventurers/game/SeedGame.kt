package com.ayoshy.badventurers.game

object SeedGame {
    val heroes = HeroCatalog.starterHeroes

    val firstQuest = Quest(
        id = "cave_minor_regrets",
        durationSeconds = 45,
        difficulty = 120,
        risk = QuestRisk.Low,
        baseGold = 320,
        pityGold = 25,
        partySlots = 3,
    )

    val quests = listOf(firstQuest)
    val questById = quests.associateBy { it.id }
}

