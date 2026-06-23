package com.ayoshy.badventurers.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuestCatalogueBalanceTest {
    @Test
    fun starterRosterCanAttemptEarlyCatalogueButHighRiskStillBites() {
        val chances = SeedGame.quests.associate { quest ->
            quest.id to bestEstimate(SeedGame.heroes, quest).successChancePercent
        }

        assertEquals(100, chances.getValue("cave_minor_regrets"))
        assertEquals(100, chances.getValue("forest_of_wrong_turns"))
        assertInRange(chances.getValue("bandit_tax_office"), 75, 95)
        assertInRange(chances.getValue("salted_swamp_chapel"), 70, 90)
        assertInRange(chances.getValue("moonlit_smuggler_run"), 70, 90)
        assertInRange(chances.getValue("the_hungry_siege"), 45, 70)
        assertInRange(chances.getValue("the_last_locked_door"), 35, 60)
        assertTrue(chances.getValue("crypt_of_unpaid_debts") <= 20)
    }

    @Test
    fun recruitedRosterHasGoodOddsAcrossExpandedCatalogue() {
        val recruitedRoster = heroesById(
            "brugg",
            "mira",
            "nell",
            "darrik",
            "quill",
            "orla",
            "bramble",
            "pax",
            "sable",
            "comptable",
            "jardinier",
            "chef_cuistot",
            "expert_en_demolition",
            "elementaire_de_sel",
            "troll_stupide",
        )

        SeedGame.quests.forEach { quest ->
            val estimate = bestEstimate(recruitedRoster, quest)

            assertTrue("${quest.id} was only ${estimate.successChancePercent}%", estimate.successChancePercent >= 85)
        }
    }

    @Test
    fun upgradedRosterCanSafelyClearEveryQuest() {
        val upgradedRoster = HeroCatalog.heroes.map { it.toHero() }
        val equipment = upgradedRoster.map { hero -> testGear(hero.id, bonus = 18) }

        SeedGame.quests.forEach { quest ->
            val estimate = bestEstimate(
                roster = upgradedRoster,
                quest = quest,
                equipment = equipment,
                facilityPowerBonus = 24,
            )

            assertTrue("${quest.id} was only ${estimate.successChancePercent}%", estimate.successChancePercent >= 95)
        }
    }

    @Test
    fun questsReferenceExistingRecommendedHeroes() {
        SeedGame.quests.forEach { quest ->
            assertTrue("${quest.id} needs recommended heroes", quest.recommendedHeroIds.isNotEmpty())
            quest.recommendedHeroIds.forEach { heroId ->
                assertTrue("${quest.id} recommends unknown hero $heroId", heroId in HeroCatalog.byId)
            }
        }
    }

    private fun bestEstimate(
        roster: List<Hero>,
        quest: Quest,
        equipment: List<EquippedLoot> = emptyList(),
        facilityPowerBonus: Int = 0,
    ): ExpeditionEstimate =
        combinations(roster, quest.partySlots.coerceAtMost(roster.size))
            .map { party ->
                ExpeditionEstimator.estimate(
                    party = party,
                    quest = quest,
                    equipment = equipment,
                    facilityPowerBonus = facilityPowerBonus,
                )
            }
            .maxBy { it.successChancePercent }

    private fun combinations(roster: List<Hero>, size: Int): List<List<Hero>> {
        if (size <= 0) return listOf(emptyList())
        if (size >= roster.size) return listOf(roster)

        val result = mutableListOf<List<Hero>>()

        fun collect(startIndex: Int, party: List<Hero>) {
            if (party.size == size) {
                result += party
                return
            }

            for (index in startIndex..roster.lastIndex) {
                collect(index + 1, party + roster[index])
            }
        }

        collect(0, emptyList())
        return result
    }

    private fun heroesById(vararg ids: String): List<Hero> =
        ids.map { HeroCatalog.byId.getValue(it).toHero() }

    private fun testGear(heroId: String, bonus: Int): EquippedLoot =
        EquippedLoot(
            heroId = heroId,
            item = LootItem(
                id = "balance_test_gear_$heroId",
                name = "Balance Test Gear",
                rarity = LootRarity.Rare,
                slot = LootSlot.Weapon,
                bonus = bonus,
                icon = LootIcon.Weapon,
            ),
        )

    private fun assertInRange(actual: Int, min: Int, max: Int) {
        assertTrue("$actual was below $min", actual >= min)
        assertTrue("$actual was above $max", actual <= max)
    }
}
