package com.ayoshy.badventurers.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
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
                    HeroClass.Bard,
                    HeroClass.Paladin,
                    HeroClass.Quartermaster,
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
    fun paladinUsesMasculineNameForCurrentPortrait() {
        assertEquals("Sir Brindle", HeroCatalog.byId.getValue("paladin").name)
    }

    @Test
    fun catalogUsesPortraitMatchingClassesForQuillLedgerAndTally() {
        assertEquals(HeroClass.Bard, HeroCatalog.byId.getValue("quill").heroClass)
        assertEquals(HeroClass.BardAccountant, HeroCatalog.byId.getValue("ledger").heroClass)
        assertEquals(HeroClass.Quartermaster, HeroCatalog.byId.getValue("comptable").heroClass)
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

    @Test
    fun summonUsesBaseProfileByDefault() {
        val expected = HeroGacha.summon(20, seed = 77)
        val baseProfileSummons = HeroGacha.summon(
            pulls = 20,
            seed = 77,
            recruitmentProfile = HeroGacha.baseRecruitmentProfile,
        )

        assertEquals(expected, baseProfileSummons)
        assertEquals(HeroGacha.rarityWeights, HeroGacha.baseRecruitmentProfile.rarityWeights)
    }

    @Test
    fun palier2RecruitmentProfileHasHigherRareOrBetterOdds() {
        assertEquals(20, HeroGacha.baseRecruitmentProfile.rareOrBetterWeight)
        assertEquals(25, HeroGacha.palier2RecruitmentProfile.rareOrBetterWeight)
        assertTrue(HeroGacha.palier2RecruitmentProfile.rareOrBetterWeight > HeroGacha.baseRecruitmentProfile.rareOrBetterWeight)
    }

    @Test
    fun recruitmentProfileUnlocksAtPalier2QuestThreshold() {
        assertEquals(HeroGacha.baseRecruitmentProfile, HeroGacha.recruitmentProfileForProgress(7))
        assertEquals(HeroGacha.palier2RecruitmentProfile, HeroGacha.recruitmentProfileForProgress(8))
        assertEquals(HeroGacha.palier2RecruitmentProfile, HeroGacha.recruitmentProfileForProgress(20))
        assertNotEquals(HeroGacha.baseRecruitmentProfile, HeroGacha.palier2RecruitmentProfile)
    }
}