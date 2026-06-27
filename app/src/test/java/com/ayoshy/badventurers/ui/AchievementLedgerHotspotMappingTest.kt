package com.ayoshy.badventurers.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AchievementLedgerHotspotMappingTest {
    @Test
    fun maskPaletteMatchesRuntimeHotspots() {
        assertEquals(0xFF3B30, achievementLedgerHotspotMaskRgb(AchievementLedgerHotspot.QuestRecords))
        assertEquals(0x007AFF, achievementLedgerHotspotMaskRgb(AchievementLedgerHotspot.GuildCharter))
        assertEquals(0x34C759, achievementLedgerHotspotMaskRgb(AchievementLedgerHotspot.HeroRoster))
        assertEquals(0xFFCC00, achievementLedgerHotspotMaskRgb(AchievementLedgerHotspot.LootDisplay))
        assertEquals(0xAF52DE, achievementLedgerHotspotMaskRgb(AchievementLedgerHotspot.ResultTrophy))
        assertEquals(0xFF9500, achievementLedgerHotspotMaskRgb(AchievementLedgerHotspot.Miscellaneous))
    }

    @Test
    fun maskColorsResolveToAchievementCategories() {
        assertEquals(AchievementLedgerHotspot.QuestRecords, achievementLedgerHotspotForMaskColor(0xFFFF3B30.toInt()))
        assertEquals(AchievementLedgerHotspot.GuildCharter, achievementLedgerHotspotForMaskColor(0x66007AFF))
        assertEquals(AchievementLedgerHotspot.HeroRoster, achievementLedgerHotspotForMaskColor(0xFF34C759.toInt()))
        assertEquals(AchievementLedgerHotspot.LootDisplay, achievementLedgerHotspotForMaskColor(0xFFFFCC00.toInt()))
        assertEquals(AchievementLedgerHotspot.ResultTrophy, achievementLedgerHotspotForMaskColor(0xFFAF52DE.toInt()))
        assertEquals(AchievementLedgerHotspot.Miscellaneous, achievementLedgerHotspotForMaskColor(0xFFFF9500.toInt()))
    }

    @Test
    fun transparentAndUnknownColorsAreDeadZones() {
        assertNull(achievementLedgerHotspotForMaskColor(0x00000000))
        assertNull(achievementLedgerHotspotForMaskColor(0x00FF3B30))
        assertNull(achievementLedgerHotspotForMaskColor(0xFF000000.toInt()))
        assertNull(achievementLedgerHotspotForMaskColor(0xFFFFFFFF.toInt()))
    }

    @Test
    fun hotspotBoundsAreSquareAndInsideBaseArt() {
        assertEquals(1003, AchievementLedgerBaseArtWidth)
        assertEquals(1568, AchievementLedgerBaseArtHeight)
        AchievementLedgerHotspot.values().forEach { hotspot ->
            val bounds = achievementLedgerHotspotBounds(hotspot)
            assertEquals(AchievementLedgerHotspotSquareSize, bounds.size)
            assertEquals(AchievementLedgerHotspotSquareSize, bounds.rightExclusive - bounds.left)
            assertEquals(AchievementLedgerHotspotSquareSize, bounds.bottomExclusive - bounds.top)
            assertTrue(bounds.left >= 0)
            assertTrue(bounds.top >= 0)
            assertTrue(bounds.rightExclusive <= AchievementLedgerBaseArtWidth)
            assertTrue(bounds.bottomExclusive <= AchievementLedgerBaseArtHeight)
        }
    }

    @Test
    fun sourcePixelsResolveThroughSquareBounds() {
        assertEquals(AchievementLedgerHotspot.QuestRecords, achievementLedgerHotspotAtMaskPixel(299, 409))
        assertEquals(AchievementLedgerHotspot.GuildCharter, achievementLedgerHotspotAtMaskPixel(697, 409))
        assertEquals(AchievementLedgerHotspot.HeroRoster, achievementLedgerHotspotAtMaskPixel(299, 813))
        assertEquals(AchievementLedgerHotspot.LootDisplay, achievementLedgerHotspotAtMaskPixel(697, 813))
        assertEquals(AchievementLedgerHotspot.ResultTrophy, achievementLedgerHotspotAtMaskPixel(299, 1213))
        assertEquals(AchievementLedgerHotspot.Miscellaneous, achievementLedgerHotspotAtMaskPixel(697, 1213))
        assertNull(achievementLedgerHotspotAtMaskPixel(500, 409))
        assertNull(achievementLedgerHotspotAtMaskPixel(141, 250))
        assertNull(achievementLedgerHotspotAtMaskPixel(457, 251))
    }

    @Test
    fun renderedPointUsesMaskPixelLookup() {
        val colorAt: (Int, Int) -> Int = { x, y ->
            achievementLedgerHotspotAtMaskPixel(x, y)?.let(::achievementLedgerHotspotMaskArgb) ?: 0x00000000
        }

        assertEquals(
            AchievementLedgerHotspot.ResultTrophy,
            achievementLedgerHotspotAtRenderedPoint(
                pointX = 299f,
                pointY = 1213f,
                containerWidth = AchievementLedgerBaseArtWidth.toFloat(),
                containerHeight = AchievementLedgerBaseArtHeight.toFloat(),
                sourceWidth = AchievementLedgerBaseArtWidth,
                sourceHeight = AchievementLedgerBaseArtHeight,
                maskColorAt = colorAt,
            ),
        )
        assertNull(
            achievementLedgerHotspotAtRenderedPoint(
                pointX = 500f,
                pointY = 409f,
                containerWidth = AchievementLedgerBaseArtWidth.toFloat(),
                containerHeight = AchievementLedgerBaseArtHeight.toFloat(),
                sourceWidth = AchievementLedgerBaseArtWidth,
                sourceHeight = AchievementLedgerBaseArtHeight,
                maskColorAt = colorAt,
            ),
        )
    }
}