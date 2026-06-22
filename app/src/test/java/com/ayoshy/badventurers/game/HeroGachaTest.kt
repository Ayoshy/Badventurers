package com.ayoshy.badventurers.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class HeroGachaTest {
    @Test
    fun zeroPullsReturnsEmptyList() {
        assertTrue(HeroGacha.summon(0).isEmpty())
        assertTrue(HeroGacha.summon(-5).isEmpty())
    }

    @Test
    fun oneHeroPerPull() {
        val heroes = HeroGacha.summon(10, seed = 42)

        assertEquals(10, heroes.size)
    }

    @Test
    fun generationIsDeterministicForFixedSeed() {
        val first = HeroGacha.summon(8, seed = 99)
        val second = HeroGacha.summon(8, seed = 99)
        val third = HeroGacha.summon(8, random = Random(99))

        assertEquals(first, second)
        assertEquals(first, third)
    }

    @Test
    fun catalogDefinitionsHaveUniqueIdsAndCoverEveryRarity() {
        assertEquals(HeroCatalog.heroes.size, HeroCatalog.byId.size)
        assertEquals(HeroRarity.values().toSet(), HeroCatalog.byRarity.keys)
        assertTrue(HeroCatalog.byRarity.values.all { it.isNotEmpty() })
    }

    @Test
    fun catalogIncludesExtendedHeroClasses() {
        val classes = HeroCatalog.heroes.map { it.heroClass }.toSet()

        assertTrue(
            classes.containsAll(
                setOf(
                    HeroClass.Ninja,
                    HeroClass.Hunter,
                    HeroClass.Priest,
                    HeroClass.Necromancer,
                ),
            ),
        )
    }

    @Test
    fun catalogIncludesRequestedHeroClasses() {
        val classes = HeroCatalog.heroes.map { it.heroClass }.toSet()

        assertTrue(
            classes.containsAll(
                setOf(
                    HeroClass.Paladin,
                    HeroClass.Accountant,
                    HeroClass.Gardener,
                    HeroClass.DeathKnight,
                    HeroClass.Chef,
                    HeroClass.DemolitionExpert,
                    HeroClass.SaltElemental,
                    HeroClass.StupidTroll,
                ),
            ),
        )
    }
    @Test
    fun summonedHeroesStayTiedToCatalogDefinitions() {
        val heroes = HeroGacha.summon(100, seed = 17)

        assertTrue(heroes.isNotEmpty())
        heroes.forEach { hero ->
            val definition = HeroCatalog.byId[hero.id]
            assertTrue("Summoned hero '${hero.id}' is missing from HeroCatalog.", definition != null)
            requireNotNull(definition)

            assertEquals(definition.name, hero.name)
            assertEquals(definition.heroClass, hero.heroClass)
            assertEquals(definition.rarity, hero.rarity)
            assertEquals(definition.stats, hero.stats)
            assertEquals(definition.trait, hero.trait)
        }
    }

    @Test
    fun rarityWeightsRepresentPercentOdds() {
        assertEquals(100, HeroGacha.rarityWeights.sumOf { it.second })
    }
}

