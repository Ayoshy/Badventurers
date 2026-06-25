package com.ayoshy.badventurers.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class LootGeneratorTest {
    @Test
    fun zeroRollsReturnsEmptyList() {
        assertTrue(LootGenerator.generate(0).isEmpty())
        assertTrue(LootGenerator.generate(-3).isEmpty())
    }

    @Test
    fun oneItemPerRoll() {
        val items = LootGenerator.generate(6, seed = 42)

        assertEquals(6, items.size)
    }

    @Test
    fun generationIsDeterministicForFixedSeed() {
        val first = LootGenerator.generate(5, seed = 99)
        val second = LootGenerator.generate(5, seed = 99)
        val third = LootGenerator.generate(5, random = Random(99))

        assertEquals(first, second)
        assertEquals(first, third)
    }

    @Test
    fun catalogDefinitionsHaveUniqueIdsAndNames() {
        assertEquals(LootCatalog.items.size, LootCatalog.byId.size)
        assertEquals(LootCatalog.items.size, LootCatalog.items.map { it.name }.toSet().size)
    }

    @Test
    fun catalogCoversEverySlot() {
        val expectedSlots = LootSlot.values().toSet()
        val catalogSlots = LootCatalog.bySlot.keys

        assertEquals(expectedSlots, catalogSlots)
        assertTrue(LootCatalog.bySlot.values.all { it.isNotEmpty() })
    }

    @Test
    fun catalogUsesFixedRarityPoolsForNewItemRule() {
        val originalItemIds = setOf(
            "weapon_bent_spoon",
            "weapon_fork_spear",
            "weapon_moon_axe",
            "weapon_nibblade",
            "weapon_toast_mace",
            "armor_patch_hood",
            "armor_moss_coat",
            "armor_winged_boots",
            "armor_travel_boots",
            "trinket_lucky_ring",
            "trinket_pocket_ring",
            "trinket_spare_ring",
            "trinket_dusty_ring",
            "trinket_quiet_ring",
            "armor_panic_helm",
            "headgear_soup_helm",
            "headgear_wobble_cap",
            "headgear_paper_crown",
            "headgear_lantern_hat",
            "headgear_grin_hood",
            "consumable_stale_potion",
            "consumable_brave_brew",
            "consumable_tiny_flask",
            "consumable_odd_elixir",
            "consumable_snap_tonic",
        )

        assertTrue(LootCatalog.items.filter { it.id in originalItemIds }.all { it.rarity == LootRarity.Common })
        assertEquals(25, LootCatalog.byRarity.getValue(LootRarity.Common).size)
        assertEquals(8, LootCatalog.byRarity.getValue(LootRarity.Uncommon).size)
        assertEquals(8, LootCatalog.byRarity.getValue(LootRarity.Rare).size)
        assertEquals(8, LootCatalog.byRarity.getValue(LootRarity.Epic).size)
        assertEquals(8, LootCatalog.byRarity.getValue(LootRarity.Relic).size)
    }

    @Test
    fun raritiesStayWithinExpectedEnumValues() {
        val expected = LootRarity.values().toSet()
        val items = LootGenerator.generate(20, seed = 7)

        assertTrue(items.isNotEmpty())
        assertTrue(items.all { it.rarity in expected })
    }

    @Test
    fun generatedItemsHaveExpectedIcons() {
        val expected = LootIcon.values().toSet()
        val items = LootGenerator.generate(20, seed = 11)

        assertTrue(items.isNotEmpty())
        assertTrue(items.all { it.icon in expected })
    }

    @Test
    fun lootProfilesGateRareLootAtPalier2Threshold() {
        assertEquals(100, LootGenerator.baseLootProfile.totalWeight)
        assertEquals(100, LootGenerator.palier2RareLootProfile.totalWeight)
        assertEquals(0, LootGenerator.baseLootProfile.rareOrBetterWeight)
        assertEquals(10, LootGenerator.palier2RareLootProfile.rareWeight)
        assertEquals(10, LootGenerator.palier2RareLootProfile.rareOrBetterWeight)
        assertEquals(LootGenerator.baseLootProfile, LootGenerator.lootProfileForProgress(7))
        assertEquals(LootGenerator.palier2RareLootProfile, LootGenerator.lootProfileForProgress(8))
        assertEquals(false, LootGenerator.isRareLootUnlocked(7))
        assertEquals(true, LootGenerator.isRareLootUnlocked(8))
    }

    @Test
    fun baseGenerationDoesNotRollRareLootBeforeGate() {
        val items = LootGenerator.generate(
            rolls = 500,
            seed = 5,
            lootProfile = LootGenerator.lootProfileForProgress(7),
        )

        assertTrue(items.isNotEmpty())
        assertTrue(items.none { it.rarity >= LootRarity.Rare })
    }

    @Test
    fun palier2GenerationRollsBalancedRareChanceOnly() {
        val items = LootGenerator.generate(
            rolls = 1_000,
            seed = 7,
            lootProfile = LootGenerator.lootProfileForProgress(8),
        )
        val rareCount = items.count { it.rarity == LootRarity.Rare }

        assertTrue("Rare count should stay near the 10% Palier 2 weight, got $rareCount.", rareCount in 70..130)
        assertEquals(0, items.count { it.rarity == LootRarity.Epic || it.rarity == LootRarity.Relic })
    }

    @Test
    fun catalogSlotsMatchIconFamilies() {
        LootCatalog.items.forEach { definition ->
            when (definition.icon) {
                LootIcon.Boots -> assertEquals("Boot icons should use the footwear slot.", LootSlot.Footwear, definition.slot)
                LootIcon.Helmet -> assertEquals("Helmet icons should use the headgear slot.", LootSlot.Headgear, definition.slot)
                LootIcon.Ring -> assertEquals("Ring icons should use the trinket slot.", LootSlot.Trinket, definition.slot)
                LootIcon.Potion,
                LootIcon.Tankard -> assertEquals("Drink icons should use the consumable slot.", LootSlot.Consumable, definition.slot)
                LootIcon.Weapon,
                LootIcon.Spoon,
                LootIcon.Blade -> assertEquals("Weapon-like icons should use the weapon slot.", LootSlot.Weapon, definition.slot)
                LootIcon.Hood -> assertTrue(
                    "Hood and cloak icons should be armor or headgear.",
                    definition.slot == LootSlot.Armor || definition.slot == LootSlot.Headgear,
                )
            }
        }
    }

    @Test
    fun generatedItemsStayTiedToCatalogDefinitions() {
        val items = LootGenerator.generate(100, seed = 17)

        assertTrue(items.isNotEmpty())
        items.forEach { item ->
            val definition = LootCatalog.byId[item.id]
            assertTrue("Generated loot '${item.id}' is missing from LootCatalog.", definition != null)
            requireNotNull(definition)

            assertEquals(definition.name, item.name)
            assertEquals(definition.rarity, item.rarity)
            assertEquals(definition.slot, item.slot)
            assertEquals(definition.icon, item.icon)
        }
    }

    @Test
    fun generatedItemsRollStatsByRarity() {
        val items = LootGenerator.generate(100, seed = 23)

        assertTrue(items.isNotEmpty())
        items.forEach { item ->
            assertEquals(item.rarity.statSlotCount, item.stats.size)
            assertEquals(item.bonus, item.stats.sumOf { it.value })
            assertEquals(item.stats.size, item.stats.map { it.type }.toSet().size)
            assertTrue(item.stats.all { it.value in 1..LootGenerator.MAX_STAT_VALUE })
        }
    }
}
