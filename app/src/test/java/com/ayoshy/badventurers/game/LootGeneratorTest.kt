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
    fun generatedItemsStayTiedToCatalogDefinitions() {
        val items = LootGenerator.generate(100, seed = 17)

        assertTrue(items.isNotEmpty())
        items.forEach { item ->
            val definition = LootCatalog.byId[item.id]
            assertTrue("Generated loot '${item.id}' is missing from LootCatalog.", definition != null)
            requireNotNull(definition)

            assertEquals(definition.name, item.name)
            assertEquals(definition.slot, item.slot)
            assertEquals(definition.icon, item.icon)
        }
    }
}
