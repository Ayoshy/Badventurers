package com.ayoshy.badventurers.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GuildHubHotspotMappingTest {
    @Test
    fun documentedMaskPaletteUsesProductionColors() {
        assertEquals(0xFF3B30, guildHubHotspotMaskRgb(GuildHubHotspot.Charter))
        assertEquals(0x007AFF, guildHubHotspotMaskRgb(GuildHubHotspot.CoreCrew))
        assertEquals(0x34C759, guildHubHotspotMaskRgb(GuildHubHotspot.QuestTable))
        assertEquals(0xFFCC00, guildHubHotspotMaskRgb(GuildHubHotspot.LootCache))
        assertEquals(0xAF52DE, guildHubHotspotMaskRgb(GuildHubHotspot.Facilities))
    }

    @Test
    fun maskColorsResolveToExpectedDestinations() {
        assertEquals(GuildHubHotspot.Charter, guildHubHotspotForMaskColor(0xFFFF3B30.toInt()))
        assertEquals(GuildHubHotspot.CoreCrew, guildHubHotspotForMaskColor(0x66007AFF))
        assertEquals(GuildHubHotspot.QuestTable, guildHubHotspotForMaskColor(0xFF34C759.toInt()))
        assertEquals(GuildHubHotspot.LootCache, guildHubHotspotForMaskColor(0xFFFFCC00.toInt()))
        assertEquals(GuildHubHotspot.Facilities, guildHubHotspotForMaskColor(0xFFAF52DE.toInt()))
    }

    @Test
    fun transparentBlackAndUnknownColorsStayDead() {
        assertNull(guildHubHotspotForMaskColor(0x00000000))
        assertNull(guildHubHotspotForMaskColor(0x00FF3B30))
        assertNull(guildHubHotspotForMaskColor(0xFF000000.toInt()))
        assertNull(guildHubHotspotForMaskColor(0xFFFFFFFF.toInt()))
        assertNull(guildHubHotspotForMaskColor(0xFFFF5E6F.toInt()))
    }

    @Test
    fun renderedImageCoordinatesMapToSourceMaskPixels() {
        assertEquals(
            GuildHubMaskPixel(470, 836),
            guildHubMaskPixelForRenderedPoint(
                pointX = 470.5f,
                pointY = 836f,
                renderedWidth = 941f,
                renderedHeight = 1672f,
                sourceWidth = 941,
                sourceHeight = 1672,
            ),
        )
        assertNull(
            guildHubMaskPixelForRenderedPoint(
                pointX = -1f,
                pointY = 836f,
                renderedWidth = 941f,
                renderedHeight = 1672f,
                sourceWidth = 941,
                sourceHeight = 1672,
            ),
        )
    }

    @Test
    fun fitMappingTreatsLetterboxBandsAsDeadZones() {
        assertNull(
            guildHubMaskPixelForContainerPoint(
                pointX = 100f,
                pointY = 500f,
                containerWidth = 1000f,
                containerHeight = 1000f,
                sourceWidth = 500,
                sourceHeight = 1000,
                contentScale = GuildHubImageScale.Fit,
            ),
        )
        assertEquals(
            GuildHubMaskPixel(250, 500),
            guildHubMaskPixelForContainerPoint(
                pointX = 500f,
                pointY = 500f,
                containerWidth = 1000f,
                containerHeight = 1000f,
                sourceWidth = 500,
                sourceHeight = 1000,
                contentScale = GuildHubImageScale.Fit,
            ),
        )
    }

    @Test
    fun cropMappingPreservesAspectRatioWhenImageOverflowsContainer() {
        assertEquals(
            GuildHubRenderedImageRect(left = -250f, top = 0f, width = 1000f, height = 500f),
            guildHubRenderedImageRect(
                containerWidth = 500f,
                containerHeight = 500f,
                sourceWidth = 1000,
                sourceHeight = 500,
                contentScale = GuildHubImageScale.Crop,
            ),
        )
        assertEquals(
            GuildHubMaskPixel(250, 250),
            guildHubMaskPixelForContainerPoint(
                pointX = 0f,
                pointY = 250f,
                containerWidth = 500f,
                containerHeight = 500f,
                sourceWidth = 1000,
                sourceHeight = 500,
                contentScale = GuildHubImageScale.Crop,
            ),
        )
    }

    @Test
    fun renderedPointLookupUsesMaskPixelColorAndDeadZones() {
        val colors = intArrayOf(
            guildHubHotspotMaskArgb(GuildHubHotspot.Charter),
            guildHubHotspotMaskArgb(GuildHubHotspot.QuestTable),
            0xFF000000.toInt(),
            0x00000000,
            guildHubHotspotMaskArgb(GuildHubHotspot.LootCache),
            guildHubHotspotMaskArgb(GuildHubHotspot.Facilities),
        )
        val colorAt: (Int, Int) -> Int = { x, y -> colors[y * 3 + x] }

        assertEquals(
            GuildHubHotspot.QuestTable,
            guildHubHotspotAtRenderedPoint(
                pointX = 150f,
                pointY = 50f,
                containerWidth = 300f,
                containerHeight = 200f,
                sourceWidth = 3,
                sourceHeight = 2,
                maskColorAt = colorAt,
            ),
        )
        assertNull(
            guildHubHotspotAtRenderedPoint(
                pointX = 250f,
                pointY = 50f,
                containerWidth = 300f,
                containerHeight = 200f,
                sourceWidth = 3,
                sourceHeight = 2,
                maskColorAt = colorAt,
            ),
        )
    }
}