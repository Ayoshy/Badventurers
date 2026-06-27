package com.ayoshy.badventurers.ui

import com.ayoshy.badventurers.game.AchievementCategory
import kotlin.math.max
import kotlin.math.min

internal const val AchievementLedgerHotspotMaskResourceName = "achievement_ledger_hotspot_mask"
internal const val AchievementLedgerBaseArtWidth = 1003
internal const val AchievementLedgerBaseArtHeight = 1568
internal const val AchievementLedgerHotspotSquareSize = 316
internal val AchievementLedgerAspectRatio: Float = AchievementLedgerBaseArtWidth.toFloat() / AchievementLedgerBaseArtHeight.toFloat()

internal enum class AchievementLedgerHotspot(val category: AchievementCategory) {
    QuestRecords(AchievementCategory.Quest),
    GuildCharter(AchievementCategory.Guild),
    HeroRoster(AchievementCategory.Hero),
    LootDisplay(AchievementCategory.Loot),
    ResultTrophy(AchievementCategory.Result),
    Miscellaneous(AchievementCategory.Idle),
}

internal enum class AchievementLedgerImageScale {
    Fit,
    Crop,
}

internal data class AchievementLedgerRenderedImageRect(
    val left: Float,
    val top: Float,
    val width: Float,
    val height: Float,
)

internal data class AchievementLedgerMaskPixel(val x: Int, val y: Int)

internal data class AchievementLedgerHotspotBounds(
    val left: Int,
    val top: Int,
    val size: Int,
) {
    val rightExclusive: Int get() = left + size
    val bottomExclusive: Int get() = top + size

    fun contains(x: Int, y: Int): Boolean =
        x in left until rightExclusive && y in top until bottomExclusive
}

internal val AchievementLedgerHotspotBoundsByHotspot: Map<AchievementLedgerHotspot, AchievementLedgerHotspotBounds> = mapOf(
    AchievementLedgerHotspot.QuestRecords to AchievementLedgerHotspotBounds(left = 141, top = 251, size = AchievementLedgerHotspotSquareSize),
    AchievementLedgerHotspot.GuildCharter to AchievementLedgerHotspotBounds(left = 539, top = 251, size = AchievementLedgerHotspotSquareSize),
    AchievementLedgerHotspot.HeroRoster to AchievementLedgerHotspotBounds(left = 141, top = 655, size = AchievementLedgerHotspotSquareSize),
    AchievementLedgerHotspot.LootDisplay to AchievementLedgerHotspotBounds(left = 539, top = 655, size = AchievementLedgerHotspotSquareSize),
    AchievementLedgerHotspot.ResultTrophy to AchievementLedgerHotspotBounds(left = 141, top = 1055, size = AchievementLedgerHotspotSquareSize),
    AchievementLedgerHotspot.Miscellaneous to AchievementLedgerHotspotBounds(left = 539, top = 1055, size = AchievementLedgerHotspotSquareSize),
)

internal fun achievementLedgerHotspotBounds(hotspot: AchievementLedgerHotspot): AchievementLedgerHotspotBounds =
    AchievementLedgerHotspotBoundsByHotspot.getValue(hotspot)

internal fun achievementLedgerHotspotAtMaskPixel(x: Int, y: Int): AchievementLedgerHotspot? =
    AchievementLedgerHotspotBoundsByHotspot.entries.firstOrNull { (_, bounds) -> bounds.contains(x, y) }?.key

internal val AchievementLedgerHotspotMaskPalette: Map<Int, AchievementLedgerHotspot> = mapOf(
    0xFF3B30 to AchievementLedgerHotspot.QuestRecords,
    0x007AFF to AchievementLedgerHotspot.GuildCharter,
    0x34C759 to AchievementLedgerHotspot.HeroRoster,
    0xFFCC00 to AchievementLedgerHotspot.LootDisplay,
    0xAF52DE to AchievementLedgerHotspot.ResultTrophy,
    0xFF9500 to AchievementLedgerHotspot.Miscellaneous,
)

internal fun achievementLedgerHotspotMaskRgb(hotspot: AchievementLedgerHotspot): Int =
    AchievementLedgerHotspotMaskPalette.entries.first { it.value == hotspot }.key

internal fun achievementLedgerHotspotMaskArgb(hotspot: AchievementLedgerHotspot, alpha: Int = 0xFF): Int =
    (alpha.coerceIn(0x00, 0xFF) shl 24) or achievementLedgerHotspotMaskRgb(hotspot)

internal fun achievementLedgerHotspotForMaskColor(argb: Int): AchievementLedgerHotspot? {
    val alpha = (argb ushr 24) and 0xFF
    val rgb = argb and 0x00FFFFFF
    if (alpha == 0 || rgb == 0x000000) return null
    return AchievementLedgerHotspotMaskPalette[rgb]
}

internal fun achievementLedgerRenderedImageRect(
    containerWidth: Float,
    containerHeight: Float,
    sourceWidth: Int,
    sourceHeight: Int,
    contentScale: AchievementLedgerImageScale = AchievementLedgerImageScale.Fit,
): AchievementLedgerRenderedImageRect? {
    if (containerWidth <= 0f || containerHeight <= 0f || sourceWidth <= 0 || sourceHeight <= 0) return null

    val widthScale = containerWidth / sourceWidth.toFloat()
    val heightScale = containerHeight / sourceHeight.toFloat()
    val scale = when (contentScale) {
        AchievementLedgerImageScale.Fit -> min(widthScale, heightScale)
        AchievementLedgerImageScale.Crop -> max(widthScale, heightScale)
    }
    val imageWidth = sourceWidth * scale
    val imageHeight = sourceHeight * scale
    return AchievementLedgerRenderedImageRect(
        left = (containerWidth - imageWidth) / 2f,
        top = (containerHeight - imageHeight) / 2f,
        width = imageWidth,
        height = imageHeight,
    )
}

internal fun achievementLedgerMaskPixelForContainerPoint(
    pointX: Float,
    pointY: Float,
    containerWidth: Float,
    containerHeight: Float,
    sourceWidth: Int,
    sourceHeight: Int,
    contentScale: AchievementLedgerImageScale = AchievementLedgerImageScale.Fit,
): AchievementLedgerMaskPixel? {
    val imageRect = achievementLedgerRenderedImageRect(
        containerWidth = containerWidth,
        containerHeight = containerHeight,
        sourceWidth = sourceWidth,
        sourceHeight = sourceHeight,
        contentScale = contentScale,
    ) ?: return null
    return achievementLedgerMaskPixelForImageRect(
        pointX = pointX,
        pointY = pointY,
        imageRect = imageRect,
        sourceWidth = sourceWidth,
        sourceHeight = sourceHeight,
    )
}

internal fun achievementLedgerHotspotAtRenderedPoint(
    pointX: Float,
    pointY: Float,
    containerWidth: Float,
    containerHeight: Float,
    sourceWidth: Int,
    sourceHeight: Int,
    contentScale: AchievementLedgerImageScale = AchievementLedgerImageScale.Fit,
    maskColorAt: (x: Int, y: Int) -> Int,
): AchievementLedgerHotspot? {
    val pixel = achievementLedgerMaskPixelForContainerPoint(
        pointX = pointX,
        pointY = pointY,
        containerWidth = containerWidth,
        containerHeight = containerHeight,
        sourceWidth = sourceWidth,
        sourceHeight = sourceHeight,
        contentScale = contentScale,
    ) ?: return null
    return achievementLedgerHotspotForMaskColor(maskColorAt(pixel.x, pixel.y))
}

private fun achievementLedgerMaskPixelForImageRect(
    pointX: Float,
    pointY: Float,
    imageRect: AchievementLedgerRenderedImageRect,
    sourceWidth: Int,
    sourceHeight: Int,
): AchievementLedgerMaskPixel? {
    if (imageRect.width <= 0f || imageRect.height <= 0f || sourceWidth <= 0 || sourceHeight <= 0) return null
    if (pointX < imageRect.left || pointY < imageRect.top) return null
    if (pointX > imageRect.left + imageRect.width || pointY > imageRect.top + imageRect.height) return null

    val normalizedX = (pointX - imageRect.left) / imageRect.width
    val normalizedY = (pointY - imageRect.top) / imageRect.height
    if (normalizedX !in 0f..1f || normalizedY !in 0f..1f) return null

    val pixelX = (normalizedX * sourceWidth).toInt().coerceIn(0, sourceWidth - 1)
    val pixelY = (normalizedY * sourceHeight).toInt().coerceIn(0, sourceHeight - 1)
    return AchievementLedgerMaskPixel(pixelX, pixelY)
}