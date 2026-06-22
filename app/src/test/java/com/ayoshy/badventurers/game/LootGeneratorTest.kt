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
}
