package com.ayoshy.badventurers.ui

import kotlin.math.max
import kotlin.math.min

internal const val GuildHubHotspotMaskResourceName = "guild_hub_hotspot_mask"
internal const val GuildHubBaseArtWidth = 941
internal const val GuildHubBaseArtHeight = 1672
internal val GuildHubAspectRatio: Float = GuildHubBaseArtWidth.toFloat() / GuildHubBaseArtHeight.toFloat()

internal enum class GuildHubHotspot {
    Charter,
    CoreCrew,
    QuestTable,
    LootCache,
    Facilities,
}

internal enum class GuildHubImageScale {
    Fit,
    Crop,
}

internal data class GuildHubRenderedImageRect(
    val left: Float,
    val top: Float,
    val width: Float,
    val height: Float,
)

internal data class GuildHubMaskPixel(val x: Int, val y: Int)

internal val GuildHubHotspotMaskPalette: Map<Int, GuildHubHotspot> = mapOf(
    0xFF3B30 to GuildHubHotspot.Charter,
    0x007AFF to GuildHubHotspot.CoreCrew,
    0x34C759 to GuildHubHotspot.QuestTable,
    0xFFCC00 to GuildHubHotspot.LootCache,
    0xAF52DE to GuildHubHotspot.Facilities,
)

internal fun guildHubHotspotMaskRgb(hotspot: GuildHubHotspot): Int =
    GuildHubHotspotMaskPalette.entries.first { it.value == hotspot }.key

internal fun guildHubHotspotMaskArgb(hotspot: GuildHubHotspot, alpha: Int = 0xFF): Int =
    (alpha.coerceIn(0x00, 0xFF) shl 24) or guildHubHotspotMaskRgb(hotspot)

internal fun guildHubHotspotForMaskColor(argb: Int): GuildHubHotspot? {
    val alpha = (argb ushr 24) and 0xFF
    val rgb = argb and 0x00FFFFFF
    if (alpha == 0 || rgb == 0x000000) return null
    return GuildHubHotspotMaskPalette[rgb]
}

internal fun guildHubRenderedImageRect(
    containerWidth: Float,
    containerHeight: Float,
    sourceWidth: Int,
    sourceHeight: Int,
    contentScale: GuildHubImageScale = GuildHubImageScale.Fit,
): GuildHubRenderedImageRect? {
    if (containerWidth <= 0f || containerHeight <= 0f || sourceWidth <= 0 || sourceHeight <= 0) return null

    val widthScale = containerWidth / sourceWidth.toFloat()
    val heightScale = containerHeight / sourceHeight.toFloat()
    val scale = when (contentScale) {
        GuildHubImageScale.Fit -> min(widthScale, heightScale)
        GuildHubImageScale.Crop -> max(widthScale, heightScale)
    }
    val imageWidth = sourceWidth * scale
    val imageHeight = sourceHeight * scale
    return GuildHubRenderedImageRect(
        left = (containerWidth - imageWidth) / 2f,
        top = (containerHeight - imageHeight) / 2f,
        width = imageWidth,
        height = imageHeight,
    )
}

internal fun guildHubMaskPixelForRenderedPoint(
    pointX: Float,
    pointY: Float,
    renderedWidth: Float,
    renderedHeight: Float,
    sourceWidth: Int,
    sourceHeight: Int,
): GuildHubMaskPixel? = guildHubMaskPixelForImageRect(
    pointX = pointX,
    pointY = pointY,
    imageRect = GuildHubRenderedImageRect(
        left = 0f,
        top = 0f,
        width = renderedWidth,
        height = renderedHeight,
    ),
    sourceWidth = sourceWidth,
    sourceHeight = sourceHeight,
)

internal fun guildHubMaskPixelForContainerPoint(
    pointX: Float,
    pointY: Float,
    containerWidth: Float,
    containerHeight: Float,
    sourceWidth: Int,
    sourceHeight: Int,
    contentScale: GuildHubImageScale = GuildHubImageScale.Fit,
): GuildHubMaskPixel? {
    val imageRect = guildHubRenderedImageRect(
        containerWidth = containerWidth,
        containerHeight = containerHeight,
        sourceWidth = sourceWidth,
        sourceHeight = sourceHeight,
        contentScale = contentScale,
    ) ?: return null
    return guildHubMaskPixelForImageRect(
        pointX = pointX,
        pointY = pointY,
        imageRect = imageRect,
        sourceWidth = sourceWidth,
        sourceHeight = sourceHeight,
    )
}

internal fun guildHubHotspotAtRenderedPoint(
    pointX: Float,
    pointY: Float,
    containerWidth: Float,
    containerHeight: Float,
    sourceWidth: Int,
    sourceHeight: Int,
    contentScale: GuildHubImageScale = GuildHubImageScale.Fit,
    maskColorAt: (x: Int, y: Int) -> Int,
): GuildHubHotspot? {
    val pixel = guildHubMaskPixelForContainerPoint(
        pointX = pointX,
        pointY = pointY,
        containerWidth = containerWidth,
        containerHeight = containerHeight,
        sourceWidth = sourceWidth,
        sourceHeight = sourceHeight,
        contentScale = contentScale,
    ) ?: return null
    return guildHubHotspotForMaskColor(maskColorAt(pixel.x, pixel.y))
}

private fun guildHubMaskPixelForImageRect(
    pointX: Float,
    pointY: Float,
    imageRect: GuildHubRenderedImageRect,
    sourceWidth: Int,
    sourceHeight: Int,
): GuildHubMaskPixel? {
    if (imageRect.width <= 0f || imageRect.height <= 0f || sourceWidth <= 0 || sourceHeight <= 0) return null
    if (pointX < imageRect.left || pointY < imageRect.top) return null
    if (pointX > imageRect.left + imageRect.width || pointY > imageRect.top + imageRect.height) return null

    val normalizedX = (pointX - imageRect.left) / imageRect.width
    val normalizedY = (pointY - imageRect.top) / imageRect.height
    if (normalizedX !in 0f..1f || normalizedY !in 0f..1f) return null

    val pixelX = (normalizedX * sourceWidth).toInt().coerceIn(0, sourceWidth - 1)
    val pixelY = (normalizedY * sourceHeight).toInt().coerceIn(0, sourceHeight - 1)
    return GuildHubMaskPixel(pixelX, pixelY)
}