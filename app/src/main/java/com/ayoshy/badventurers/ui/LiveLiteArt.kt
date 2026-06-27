package com.ayoshy.badventurers.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.ayoshy.badventurers.game.LiveLiteAssetCategories
import com.ayoshy.badventurers.game.LiveLiteAssetKeys
import com.ayoshy.badventurers.game.LiveLiteAssetRef
import com.ayoshy.badventurers.game.LiveLiteStageTheme

internal data class LiveLitePalette(
    val sky: Color,
    val ground: Color,
    val accent: Color,
    val warning: Color,
)

internal data class LiveLitePaintableSpec(
    val key: String,
    val palette: LiveLitePalette,
    val brief: String,
)

internal object LiveLiteArtCatalog {
    val stageSpecs: Map<String, LiveLitePaintableSpec> = listOf(
        LiveLitePaintableSpec(LiveLiteAssetKeys.STAGE_CAVE, cavePalette, "Damp cave corridor, nervous torch, apology paperwork."),
        LiveLitePaintableSpec(LiveLiteAssetKeys.STAGE_FOREST, forestPalette, "Crooked forest path with contradictory signposts."),
        LiveLitePaintableSpec(LiveLiteAssetKeys.STAGE_SWAMP, swampPalette, "Briny swamp platform, cracked basin, salt rings."),
        LiveLitePaintableSpec(LiveLiteAssetKeys.STAGE_CRYPT, cryptPalette, "Ledger-lined crypt with a payment-slot wall."),
        LiveLitePaintableSpec(LiveLiteAssetKeys.STAGE_SIEGE_CAMP, campPalette, "Bread-crate barricade under tired camp light."),
        LiveLitePaintableSpec(LiveLiteAssetKeys.STAGE_RUINS, ruinPalette, "Old ruin with glowing hinges and survey chalk."),
        LiveLitePaintableSpec(LiveLiteAssetKeys.STAGE_CITY, officePalette, "Guild paperwork counter with hostile forms."),
        LiveLitePaintableSpec(LiveLiteAssetKeys.STAGE_FORTRESS, fortressPalette, "Locked gate, brace beams, and a very serious latch."),
        LiveLitePaintableSpec(LiveLiteAssetKeys.STAGE_ROAD, roadPalette, "Guild road fallback with a cart rut and bad map."),
    ).associateBy { it.key }

    val paintableSpecs: Map<String, LiveLitePaintableSpec> = (
        stageSpecs.values + listOf(
            LiveLitePaintableSpec(LiveLiteAssetKeys.POSE_IDLE, guildPalette, "Shared idle pose frame for each hero row."),
            LiveLitePaintableSpec(LiveLiteAssetKeys.POSE_EFFORT, guildPalette, "Shared effort pose frame with forward lean."),
            LiveLitePaintableSpec(LiveLiteAssetKeys.POSE_SUCCESS, successPalette, "Shared success pose frame with brass sparkle."),
            LiveLitePaintableSpec(LiveLiteAssetKeys.POSE_MISTAKE, warningPalette, "Shared mistake pose frame with crooked footing."),
            LiveLitePaintableSpec(LiveLiteAssetKeys.POSE_LOOT, lootPalette, "Shared loot pose frame with chest/coin accent."),
            LiveLitePaintableSpec(LiveLiteAssetKeys.HAZARD_TRAP, warningPalette, "Trap/event card icon."),
            LiveLitePaintableSpec(LiveLiteAssetKeys.HAZARD_PAPERWORK, officePalette, "Paperwork/event card icon."),
            LiveLitePaintableSpec(LiveLiteAssetKeys.HAZARD_CURSE, cryptPalette, "Curse/event card icon."),
            LiveLitePaintableSpec(LiveLiteAssetKeys.HAZARD_BANDIT, cityNightPalette, "Bandit/event card icon."),
            LiveLitePaintableSpec(LiveLiteAssetKeys.HAZARD_UNDEAD, cryptPalette, "Undead/event card icon."),
            LiveLitePaintableSpec(LiveLiteAssetKeys.HAZARD_MAGIC, ruinPalette, "Magic/event card icon."),
            LiveLitePaintableSpec(LiveLiteAssetKeys.HAZARD_SIEGE, campPalette, "Siege/event card icon."),
            LiveLitePaintableSpec(LiveLiteAssetKeys.HAZARD_HEIST, cityNightPalette, "Heist/event card icon."),
            LiveLitePaintableSpec(LiveLiteAssetKeys.HAZARD_OBSTACLE, fortressPalette, "Obstacle/event card icon."),
            LiveLitePaintableSpec(LiveLiteAssetKeys.HAZARD_ROUTE, roadPalette, "Route/event card icon."),
            LiveLitePaintableSpec(LiveLiteAssetKeys.HAZARD_SWAMP, swampPalette, "Swamp/event card icon."),
            LiveLitePaintableSpec(LiveLiteAssetKeys.HAZARD_GUARD, campPalette, "Guard/event card icon."),
            LiveLitePaintableSpec(LiveLiteAssetKeys.CAUSE_PLAN, planPalette, "Plan cause icon."),
            LiveLitePaintableSpec(LiveLiteAssetKeys.CAUSE_HERO_SPECIAL, successPalette, "Hero-special cause icon."),
            LiveLitePaintableSpec(LiveLiteAssetKeys.CAUSE_FACILITY, guildPalette, "Facility cause icon."),
            LiveLitePaintableSpec(LiveLiteAssetKeys.CAUSE_ACHIEVEMENT, lootPalette, "Achievement cause icon."),
            LiveLitePaintableSpec(LiveLiteAssetKeys.PASSIVE_GUILD_INCOME, guildPalette, "Passive income report vignette."),
            LiveLitePaintableSpec(LiveLiteAssetKeys.PASSIVE_CORE_CREW, guildPalette, "Core crew passive report vignette."),
            LiveLitePaintableSpec(LiveLiteAssetKeys.PASSIVE_FACILITY, guildPalette, "Facility passive report vignette."),
            LiveLitePaintableSpec(LiveLiteAssetKeys.PASSIVE_QUEST_REGION, roadPalette, "Quest-region passive report vignette."),
        )
    ).associateBy { it.key }

    fun stageSpec(stage: LiveLiteStageTheme): LiveLitePaintableSpec =
        stageSpecs[stage.asset.key] ?: stageSpecs.getValue(LiveLiteAssetKeys.STAGE_ROAD)

    fun assetSpec(asset: LiveLiteAssetRef): LiveLitePaintableSpec =
        paintableSpecs[asset.key]
            ?: paintableSpecs[asset.key.substringBeforeLast('.', missingDelimiterValue = asset.key)]
            ?: LiveLitePaintableSpec(asset.key, guildPalette, "Code-native placeholder for ${asset.key}.")
}

@Composable
internal fun LiveLiteStageScene(
    stage: LiveLiteStageTheme,
    progress: Float,
    heroCount: Int,
    modifier: Modifier = Modifier,
) {
    val spec = LiveLiteArtCatalog.stageSpec(stage)
    val palette = spec.palette
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(156.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(palette.sky)
            .border(1.dp, palette.accent.copy(alpha = 0.65f), RoundedCornerShape(8.dp))
            .padding(1.dp),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(palette.sky, palette.ground.copy(alpha = 0.72f), palette.ground),
                ),
            )
            drawRect(
                color = palette.ground,
                topLeft = Offset(0f, size.height * 0.68f),
                size = Size(size.width, size.height * 0.32f),
            )
            repeat(4) { index ->
                val x = size.width * (0.12f + index * 0.23f)
                val height = size.height * (0.22f + index * 0.035f)
                drawRoundRect(
                    color = palette.ground.copy(alpha = 0.48f),
                    topLeft = Offset(x, size.height * 0.56f - height),
                    size = Size(size.width * 0.11f, height),
                    cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
                )
            }
            val path = Path().apply {
                moveTo(0f, size.height * 0.76f)
                cubicTo(size.width * 0.24f, size.height * 0.62f, size.width * 0.56f, size.height * 0.92f, size.width, size.height * 0.72f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(path, palette.accent.copy(alpha = 0.22f))
            val markerX = size.width * (0.12f + 0.76f * progress.coerceIn(0f, 1f))
            drawCircle(palette.accent, radius = 7.dp.toPx(), center = Offset(markerX, size.height * 0.58f))
            drawCircle(palette.warning, radius = 3.dp.toPx(), center = Offset(markerX, size.height * 0.58f))
            val visibleHeroes = heroCount.coerceIn(1, 4)
            repeat(visibleHeroes) { index ->
                val x = size.width * (0.22f + index * 0.16f)
                val y = size.height * 0.79f
                drawCircle(Color(0x66000000), radius = 11.dp.toPx(), center = Offset(x + 3.dp.toPx(), y + 7.dp.toPx()))
                drawCircle(palette.accent, radius = 10.dp.toPx(), center = Offset(x, y))
                drawRoundRect(
                    color = Color(0xFF211F1A),
                    topLeft = Offset(x - 7.dp.toPx(), y + 7.dp.toPx()),
                    size = Size(14.dp.toPx(), 18.dp.toPx()),
                    cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
                )
            }
        }
    }
}

@Composable
internal fun LiveLiteAssetGlyph(asset: LiveLiteAssetRef, modifier: Modifier = Modifier) {
    val spec = LiveLiteArtCatalog.assetSpec(asset)
    val palette = spec.palette
    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(palette.ground)
            .border(1.dp, palette.accent.copy(alpha = 0.55f), RoundedCornerShape(8.dp)),
    ) {
        drawRoundRect(
            color = palette.sky,
            topLeft = Offset(size.width * 0.12f, size.height * 0.12f),
            size = Size(size.width * 0.76f, size.height * 0.76f),
            cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx()),
        )
        when (asset.category) {
            LiveLiteAssetCategories.HAZARD -> {
                val warning = Path().apply {
                    moveTo(size.width * 0.5f, size.height * 0.22f)
                    lineTo(size.width * 0.78f, size.height * 0.72f)
                    lineTo(size.width * 0.22f, size.height * 0.72f)
                    close()
                }
                drawPath(warning, palette.warning)
                drawLine(
                    color = Color(0xFF211F1A),
                    start = Offset(size.width * 0.5f, size.height * 0.38f),
                    end = Offset(size.width * 0.5f, size.height * 0.58f),
                    strokeWidth = 2.dp.toPx(),
                )
            }
            LiveLiteAssetCategories.PLAN -> {
                drawRoundRect(
                    color = palette.accent,
                    topLeft = Offset(size.width * 0.3f, size.height * 0.22f),
                    size = Size(size.width * 0.4f, size.height * 0.56f),
                    cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
                )
                repeat(3) { index ->
                    drawLine(
                        color = Color(0xFF211F1A),
                        start = Offset(size.width * 0.38f, size.height * (0.36f + index * 0.12f)),
                        end = Offset(size.width * 0.62f, size.height * (0.36f + index * 0.12f)),
                        strokeWidth = 1.2.dp.toPx(),
                    )
                }
            }
            LiveLiteAssetCategories.POSE -> {
                drawCircle(palette.accent, radius = size.minDimension * 0.16f, center = Offset(size.width * 0.5f, size.height * 0.32f))
                drawLine(palette.accent, Offset(size.width * 0.5f, size.height * 0.47f), Offset(size.width * 0.5f, size.height * 0.72f), strokeWidth = 4.dp.toPx())
                drawLine(palette.warning, Offset(size.width * 0.35f, size.height * 0.55f), Offset(size.width * 0.65f, size.height * 0.55f), strokeWidth = 3.dp.toPx())
            }
            else -> {
                drawCircle(palette.accent, radius = size.minDimension * 0.25f, center = Offset(size.width * 0.5f, size.height * 0.5f))
                drawCircle(palette.warning, radius = size.minDimension * 0.12f, center = Offset(size.width * 0.58f, size.height * 0.42f))
                drawRoundRect(
                    color = Color(0x55211F1A),
                    topLeft = Offset(size.width * 0.25f, size.height * 0.68f),
                    size = Size(size.width * 0.5f, 3.dp.toPx()),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
                )
            }
        }
        drawRoundRect(
            color = palette.accent.copy(alpha = 0.9f),
            topLeft = Offset(size.width * 0.18f, size.height * 0.82f),
            size = Size(size.width * 0.64f, 3.dp.toPx()),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
            style = Stroke(width = 1.2.dp.toPx()),
        )
    }
}

private val cavePalette = LiveLitePalette(Color(0xFF211D28), Color(0xFF5A5047), Color(0xFFE0B45E), Color(0xFFB85C5B))
private val forestPalette = LiveLitePalette(Color(0xFF1E3324), Color(0xFF4F7141), Color(0xFFC9A35E), Color(0xFFB86D4A))
private val swampPalette = LiveLitePalette(Color(0xFF173633), Color(0xFF3F6B4B), Color(0xFF9AD4A2), Color(0xFFB9A24A))
private val cryptPalette = LiveLitePalette(Color(0xFF202433), Color(0xFF46505D), Color(0xFF77C8B5), Color(0xFFC9A35E))
private val campPalette = LiveLitePalette(Color(0xFF2F2D25), Color(0xFF6D5B3C), Color(0xFFFFC46A), Color(0xFFD56D54))
private val ruinPalette = LiveLitePalette(Color(0xFF252A38), Color(0xFF536074), Color(0xFF8BC6EA), Color(0xFFD08A5D))
private val officePalette = LiveLitePalette(Color(0xFF2C2520), Color(0xFF6B5439), Color(0xFFD2B978), Color(0xFFB85C5B))
private val fortressPalette = LiveLitePalette(Color(0xFF252933), Color(0xFF5D5948), Color(0xFFD9C37A), Color(0xFFC26C5B))
private val roadPalette = LiveLitePalette(Color(0xFF263128), Color(0xFF5E6B42), Color(0xFFD2B978), Color(0xFFB85C5B))
private val guildPalette = LiveLitePalette(Color(0xFF27342A), Color(0xFF5E6B42), Color(0xFFD2B978), Color(0xFFB85C5B))
private val successPalette = LiveLitePalette(Color(0xFF23392E), Color(0xFF2F695C), Color(0xFFFFD36D), Color(0xFF8FD6A4))
private val warningPalette = LiveLitePalette(Color(0xFF382322), Color(0xFF6A3E35), Color(0xFFE0B45E), Color(0xFFD56D54))
private val lootPalette = LiveLitePalette(Color(0xFF312C1F), Color(0xFF745E2F), Color(0xFFFFD36D), Color(0xFF8AD7C2))
private val planPalette = LiveLitePalette(Color(0xFF2D2B25), Color(0xFF5D5341), Color(0xFFE7C477), Color(0xFFB85C5B))
private val cityNightPalette = LiveLitePalette(Color(0xFF1C2535), Color(0xFF404D63), Color(0xFF91A7D8), Color(0xFFB85C5B))