package com.ayoshy.badventurers.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HeroProgressionTest {
    @Test
    fun xpCurveUsesAgreedSoftRamp() {
        assertEquals(45, HeroProgression.xpForNextLevel(1))
        assertEquals(65, HeroProgression.xpForNextLevel(2))
        assertEquals(90, HeroProgression.xpForNextLevel(3))
        assertEquals(405, HeroProgression.xpForNextLevel(10))
    }

    @Test
    fun grantXpCarriesAcrossMultipleLevelUps() {
        val hero = HeroCatalog.byId.getValue("darrik").toHero()
        val earnedXp = HeroProgression.xpForNextLevel(hero.level) +
            HeroProgression.xpForNextLevel(hero.level + 1) +
            3

        val advanced = HeroProgression.grantXp(hero, earnedXp)

        assertEquals(hero.level + 2, advanced.level)
        assertEquals(3, advanced.xp)
    }

    @Test
    fun previewGrantXpReportsLevelUpsAndStatGains() {
        val hero = HeroCatalog.byId.getValue("darrik").toHero()
        val earnedXp = HeroProgression.xpForNextLevel(hero.level) + 7

        val preview = HeroProgression.previewGrantXp(hero, earnedXp)
        val advanced = HeroProgression.grantXp(hero, earnedXp)

        assertEquals(hero.level, preview.beforeLevel)
        assertEquals(advanced.level, preview.afterLevel)
        assertEquals(1, preview.levelsGained)
        assertEquals(7, preview.xpAfter)
        assertEquals(HeroProgression.xpForNextLevel(advanced.level), preview.xpForNextLevel)
        assertTrue(preview.statGains.isNotEmpty())
        assertEquals(advanced.stats.total - hero.stats.total, preview.statGains.sumOf { it.value })
    }

    @Test
    fun levelUpAppliesClassTraitAndQuirkStatGrowth() {
        val hero = HeroCatalog.byId.getValue("mira").toHero()
        val advanced = HeroProgression.grantXp(hero, HeroProgression.xpForNextLevel(hero.level))

        assertEquals(hero.level + 1, advanced.level)
        assertTrue(advanced.stats.total > hero.stats.total)
        assertTrue(advanced.stats.magic > hero.stats.magic)
        assertTrue(advanced.stats.paperwork > hero.stats.paperwork)
    }

    @Test
    fun withProgressNormalizesStoredProgressAndRebuildsStats() {
        val hero = HeroCatalog.byId.getValue("brugg").toHero()
        val targetLevel = hero.level + 1
        val storedXp = HeroProgression.xpForNextLevel(targetLevel) + 7
        val restored = HeroProgression.withProgress(
            hero = hero,
            level = targetLevel,
            xp = storedXp,
        )
        val expected = HeroProgression.grantXp(
            hero = HeroProgression.withProgress(hero, targetLevel, 0),
            amount = storedXp,
        )

        assertEquals(expected, restored)
        assertEquals(targetLevel + 1, restored.level)
        assertEquals(7, restored.xp)
    }
}