package com.ayoshy.badventurers.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HeroRecommendationScorerTest {
    @Test
    fun questTagsAndSpecialsCanBeatStaticRecommendationHints() {
        val quest = SeedGame.firstQuest.copy(
            difficulty = 130,
            risk = QuestRisk.Low,
            partySlots = 1,
            tags = listOf(QuestTag.Paperwork, QuestTag.Contract),
            recommendedHeroIds = listOf("brugg"),
        )

        val ranked = HeroRecommendationScorer.rankHeroes(
            roster = listOf(
                HeroCatalog.byId.getValue("brugg").toHero(),
                HeroCatalog.byId.getValue("quill").toHero(),
            ),
            quest = quest,
            partySlots = 1,
        )

        assertEquals("quill", ranked.first().hero.id)
        assertTrue(ranked.first().activeSpecial)
        assertTrue(ranked.first().successGainPercent > 0)
    }

    @Test
    fun equippedStatsImproveQuestFitAndEstimatedGain() {
        val plain = testHero("plain")
        val geared = testHero("geared")
        val magicHat = EquippedLoot(
            heroId = geared.id,
            item = LootItem(
                id = "test_magic_hat",
                name = "Test Magic Hat",
                rarity = LootRarity.Rare,
                slot = LootSlot.Headgear,
                stats = listOf(StatBonus(StatType.Magic, 80)),
                icon = LootIcon.Helmet,
            ),
        )
        val quest = SeedGame.firstQuest.copy(
            difficulty = 140,
            risk = QuestRisk.Low,
            partySlots = 1,
            tags = listOf(QuestTag.Magic),
            recommendedHeroIds = emptyList(),
        )

        val ranked = HeroRecommendationScorer.rankHeroes(
            roster = listOf(plain, geared),
            quest = quest,
            equipment = listOf(magicHat),
            partySlots = 1,
        )

        assertEquals(geared.id, ranked.first().hero.id)
        assertEquals(80, ranked.first().equipmentBonus)
        assertTrue(ranked.first().successGainPercent > ranked.last().successGainPercent)
    }

    private fun testHero(id: String): Hero =
        Hero(
            id = id,
            name = id.replaceFirstChar { it.uppercase() },
            heroClass = HeroClass.ApprenticeMage,
            rarity = HeroRarity.Common,
            level = 1,
            stats = HeroStats(
                force = 40,
                magic = 40,
                luck = 40,
                ego = 40,
                hygiene = 40,
                badFaith = 40,
                orientation = 40,
                paperwork = 40,
                endurance = 40,
                charisma = 40,
            ),
            trait = Trait.ReadsManual,
            special = HeroSpecial.RamshackleCharge,
        )
}
