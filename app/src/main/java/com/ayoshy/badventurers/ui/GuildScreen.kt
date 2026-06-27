package com.ayoshy.badventurers.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.booleanResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ayoshy.badventurers.R
import com.ayoshy.badventurers.game.AchievementCatalog
import com.ayoshy.badventurers.game.AchievementCategory
import com.ayoshy.badventurers.game.AchievementDefinition
import com.ayoshy.badventurers.game.AchievementFeature
import com.ayoshy.badventurers.game.ExpeditionEngine
import com.ayoshy.badventurers.game.ExpeditionEstimator
import com.ayoshy.badventurers.game.ExpeditionOutcome
import com.ayoshy.badventurers.game.ExpeditionPlan
import com.ayoshy.badventurers.game.ExpeditionPlanCatalog
import com.ayoshy.badventurers.game.ExpeditionResult
import com.ayoshy.badventurers.game.FakeRewardedAdService
import com.ayoshy.badventurers.game.GuildFacility
import com.ayoshy.badventurers.game.ScoutTableIntel
import com.ayoshy.badventurers.game.Hero
import com.ayoshy.badventurers.game.HeroCatalog
import com.ayoshy.badventurers.game.HeroClass
import com.ayoshy.badventurers.game.HeroGacha
import com.ayoshy.badventurers.game.HeroProgression
import com.ayoshy.badventurers.game.HeroRecommendation
import com.ayoshy.badventurers.game.HeroRecommendationScorer
import com.ayoshy.badventurers.game.HeroRarity
import com.ayoshy.badventurers.game.HeroLevelRewardType
import com.ayoshy.badventurers.game.HeroLevelRewardUnlock
import com.ayoshy.badventurers.game.HeroXpPreview
import com.ayoshy.badventurers.game.HeroSpecial
import com.ayoshy.badventurers.game.HeroSpecialCatalog
import com.ayoshy.badventurers.game.HeroRecruitmentResult
import com.ayoshy.badventurers.game.JournalEntry
import com.ayoshy.badventurers.game.LootCarryBreakdown
import com.ayoshy.badventurers.game.LootEconomy
import com.ayoshy.badventurers.game.LootGenerator
import com.ayoshy.badventurers.game.LootIcon
import com.ayoshy.badventurers.game.LootItem
import com.ayoshy.badventurers.game.LootRarity
import com.ayoshy.badventurers.game.LootSlot
import com.ayoshy.badventurers.game.PartyPowerCalculator
import com.ayoshy.badventurers.game.PassiveIncomeReport
import com.ayoshy.badventurers.game.PassiveIncident
import com.ayoshy.badventurers.game.PassiveIncidentReward
import com.ayoshy.badventurers.game.PlayPhase
import com.ayoshy.badventurers.game.PlaySessionState
import com.ayoshy.badventurers.game.ProgressionAdvice
import com.ayoshy.badventurers.game.ProgressionAdviceKind
import com.ayoshy.badventurers.game.ProgressionAdvisor
import com.ayoshy.badventurers.game.RecruitmentTicketCatalog
import com.ayoshy.badventurers.game.RecruitmentTicketMetadata
import com.ayoshy.badventurers.game.Quest
import com.ayoshy.badventurers.game.QuestDifficultyTier
import com.ayoshy.badventurers.game.QuestRisk
import com.ayoshy.badventurers.game.QuestTag
import com.ayoshy.badventurers.game.QuestUnlockCondition
import com.ayoshy.badventurers.game.ResultCause
import com.ayoshy.badventurers.game.ResultCauseFacility
import com.ayoshy.badventurers.game.ResultCauseGenerator
import com.ayoshy.badventurers.game.ResultCauseKind
import com.ayoshy.badventurers.game.SeedGame
import com.ayoshy.badventurers.game.StatBonus
import com.ayoshy.badventurers.game.StatType
import com.ayoshy.badventurers.game.Trait
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
internal fun GuildScreen(
    session: PlaySessionState,
    selectedQuest: Quest,
    nowMillis: Long,
    onViewResult: () -> Unit,
    onNextQuest: () -> Unit,
    onAchievements: () -> Unit,
    onLoot: () -> Unit,
    onFacilities: () -> Unit,
    onToggleCoreCrew: (String) -> Unit,
    onFinishQuestNow: () -> Unit,
    onResetProgress: () -> Unit,
) {
    var selectedHotspotName by rememberSaveable { mutableStateOf(defaultGuildHubHotspot(session.phase).name) }
    var visitedHotspotNames by rememberSaveable { mutableStateOf(emptyList<String>()) }
    var showCoreCrewDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(session.phase) {
        selectedHotspotName = defaultGuildHubHotspot(session.phase).name
    }

    val selectedHotspot = GuildHubHotspot.values()
        .firstOrNull { it.name == selectedHotspotName }
        ?: defaultGuildHubHotspot(session.phase)
    val visitedHotspots = visitedHotspotNames
        .mapNotNull { name -> GuildHubHotspot.values().firstOrNull { it.name == name } }
        .toSet()
    val hintState = guildHubHintState(visitedHotspots, selectedHotspot)
    val statusBadges = guildHubStatusBadges(
        phase = session.phase,
        pendingLootCount = session.pendingLootItems.size,
        claimableAchievementCount = session.claimableAchievementCount(),
        achievementSeals = session.achievementSeals(),
        nextMilestoneSeals = session.nextAchievementMilestone()?.sealsRequired,
        availableFacilityUpgradeCount = GuildFacility.values().count { facility -> session.canUpgradeFacility(facility) },
        runningSecondsRemaining = if (session.phase == PlayPhase.Running) {
            remainingSeconds(session, nowMillis).toInt().coerceAtLeast(0)
        } else {
            0
        },
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF10110F)),
    ) {
        val drawerLayout = guildHomeDrawerLayout(
            contentWidthDp = maxWidth.value.roundToInt(),
            contentHeightDp = maxHeight.value.roundToInt(),
        )

        GuildHubArtwork(
            selectedHotspot = selectedHotspot,
            hintState = hintState,
            statusBadges = statusBadges,
            onHotspotSelected = { hotspot ->
                selectedHotspotName = hotspot.name
                if (hotspot.name !in visitedHotspotNames) {
                    visitedHotspotNames = visitedHotspotNames + hotspot.name
                }
            },
            modifier = Modifier.fillMaxSize(),
        )
        GuildHubSelectionDrawer(
            session = session,
            selectedQuest = selectedQuest,
            nowMillis = nowMillis,
            selectedHotspot = selectedHotspot,
            onViewResult = onViewResult,
            onNextQuest = onNextQuest,
            onAchievements = onAchievements,
            onLoot = onLoot,
            onFacilities = onFacilities,
            onManageCoreCrew = { showCoreCrewDialog = true },
            onFinishQuestNow = onFinishQuestNow,
            bodyMaxLines = drawerLayout.bodyMaxLines,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    horizontal = drawerLayout.horizontalPaddingDp.dp,
                    vertical = drawerLayout.bottomPaddingDp.dp,
                )
                .widthIn(max = drawerLayout.maxWidthDp.dp)
                .heightIn(max = drawerLayout.maxHeightDp.dp),
        )
    }

    if (showCoreCrewDialog) {
        CoreCrewDialog(
            session = session,
            onToggleCoreCrew = onToggleCoreCrew,
            onDismiss = { showCoreCrewDialog = false },
        )
    }
}

private fun defaultGuildHubHotspot(phase: PlayPhase): GuildHubHotspot =
    when (phase) {
        PlayPhase.Idle -> GuildHubHotspot.QuestTable
        PlayPhase.Running -> GuildHubHotspot.QuestTable
        PlayPhase.ResultReady -> GuildHubHotspot.QuestTable
    }

@Composable
private fun GuildHubArtwork(
    selectedHotspot: GuildHubHotspot,
    hintState: GuildHubHintState,
    statusBadges: List<GuildHubStatusBadge>,
    onHotspotSelected: (GuildHubHotspot) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hotspotMaskBitmap = rememberGuildHubHotspotMaskBitmap()

    BoxWithConstraints(
        modifier = modifier.background(Color(0xFF10110F)),
        contentAlignment = Alignment.Center,
    ) {
        val useHeight = maxWidth.value / maxHeight.value > GuildHubAspectRatio
        val hubModifier = if (useHeight) {
            Modifier
                .fillMaxHeight()
                .aspectRatio(GuildHubAspectRatio)
        } else {
            Modifier
                .fillMaxWidth()
                .aspectRatio(GuildHubAspectRatio)
        }

        var pressedHotspotName by remember { mutableStateOf<String?>(null) }
        val pressedHotspot = GuildHubHotspot.values().firstOrNull { it.name == pressedHotspotName }

        Box(
            modifier = hubModifier
                .pointerInput(hotspotMaskBitmap) {
                    detectTapGestures(
                        onPress = { tap ->
                            val mask = hotspotMaskBitmap ?: return@detectTapGestures
                            val hotspot = guildHubHotspotAtRenderedPoint(
                                pointX = tap.x,
                                pointY = tap.y,
                                containerWidth = size.width.toFloat(),
                                containerHeight = size.height.toFloat(),
                                sourceWidth = mask.width,
                                sourceHeight = mask.height,
                                contentScale = GuildHubImageScale.Fit,
                                maskColorAt = { x, y -> mask.getPixel(x, y) },
                            )
                            if (hotspot != null) {
                                pressedHotspotName = hotspot.name
                                try {
                                    tryAwaitRelease()
                                } finally {
                                    pressedHotspotName = null
                                }
                            }
                        },
                        onTap = { tap ->
                            val mask = hotspotMaskBitmap ?: return@detectTapGestures
                            val hotspot = guildHubHotspotAtRenderedPoint(
                                pointX = tap.x,
                                pointY = tap.y,
                                containerWidth = size.width.toFloat(),
                                containerHeight = size.height.toFloat(),
                                sourceWidth = mask.width,
                                sourceHeight = mask.height,
                                contentScale = GuildHubImageScale.Fit,
                                maskColorAt = { x, y -> mask.getPixel(x, y) },
                            )
                            if (hotspot != null) onHotspotSelected(hotspot)
                        },
                    )
                },
        ) {
            Image(
                painter = painterResource(R.drawable.guild_hub_interactive_v3),
                contentDescription = stringResource(R.string.guild_hub_art_content_description),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds,
            )
            if (hotspotMaskBitmap != null) {
                GuildHubMaskHighlights(
                    maskBitmap = hotspotMaskBitmap,
                    selectedHotspot = selectedHotspot,
                    pressedHotspot = pressedHotspot,
                )
            }
            GuildHubStatusOverlay(statusBadges = statusBadges)
            GuildHubHintOverlay(
                hintState = hintState,
                selectedHotspot = selectedHotspot,
            )
        }
    }
}

@Composable
private fun GuildHubHintOverlay(
    hintState: GuildHubHintState,
    selectedHotspot: GuildHubHotspot,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        if (hintState.showIntroHint) {
            GuildHubHintChip(
                text = stringResource(R.string.guild_hub_first_use_hint),
                selected = true,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp, start = 16.dp, end = 16.dp),
            )
        }
        hintState.labelledHotspots.forEach { hotspot ->
            val anchor = guildHubHintAnchor(hotspot)
            GuildHubHintChip(
                text = guildHubHotspotHintLabel(hotspot),
                selected = hotspot == selectedHotspot,
                modifier = Modifier
                    .offset(
                        x = maxWidth * anchor.xFraction - 44.dp,
                        y = maxHeight * anchor.yFraction,
                    )
                    .widthIn(min = 76.dp, max = 112.dp),
            )
        }
    }
}

@Composable
private fun GuildHubStatusOverlay(statusBadges: List<GuildHubStatusBadge>) {
    if (statusBadges.isEmpty()) return

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        statusBadges.forEach { badge ->
            val anchor = guildHubStatusAnchor(badge.hotspot)
            GuildHubStatusBadgeChip(
                text = guildHubStatusBadgeText(badge),
                tone = badge.tone,
                modifier = Modifier
                    .offset(
                        x = maxWidth * anchor.xFraction - 34.dp,
                        y = maxHeight * anchor.yFraction - 12.dp,
                    )
                    .widthIn(min = 40.dp, max = 104.dp),
            )
        }
    }
}

@Composable
private fun GuildHubStatusBadgeChip(
    text: String,
    tone: GuildHubStatusTone,
    modifier: Modifier = Modifier,
) {
    val colors = guildHubStatusBadgeColors(tone)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colors.first)
            .border(BorderStroke(1.dp, colors.second), RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = Color(0xFFFFF7D2),
            fontSize = 9.sp,
            lineHeight = 10.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun guildHubStatusBadgeText(badge: GuildHubStatusBadge): String = when (badge.kind) {
    GuildHubStatusBadgeKind.LootRecovery -> stringResource(R.string.guild_hub_badge_loot_pending, badge.value ?: 0)
    GuildHubStatusBadgeKind.CharterClaim -> stringResource(R.string.guild_hub_badge_charter_claims, badge.value ?: 0)
    GuildHubStatusBadgeKind.CharterProgress -> stringResource(
        R.string.guild_hub_badge_charter_progress,
        badge.value ?: 0,
        badge.targetValue ?: 0,
    )
    GuildHubStatusBadgeKind.QuestReady -> stringResource(R.string.guild_hub_badge_quest_ready)
    GuildHubStatusBadgeKind.QuestRunning -> stringResource(R.string.guild_hub_badge_quest_running, badge.value ?: 0)
    GuildHubStatusBadgeKind.QuestReportReady -> stringResource(R.string.guild_hub_badge_quest_report)
    GuildHubStatusBadgeKind.FacilityUpgradeReady -> stringResource(R.string.guild_hub_badge_facility_upgrades, badge.value ?: 0)
}

private fun guildHubStatusBadgeColors(tone: GuildHubStatusTone): Pair<Color, Color> = when (tone) {
    GuildHubStatusTone.Attention -> Color(0xEAAE5D38) to Color(0xFFFFD66D)
    GuildHubStatusTone.Progress -> Color(0xDA2F695C) to Color(0xFFBEE8A0)
    GuildHubStatusTone.Calm -> Color(0xD1171813) to Color(0xFFD7C891)
}

@Composable
private fun GuildHubHintChip(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    val borderColor = if (selected) Color(0xCCFFD36D) else Color(0x88FFF0BD)
    val backgroundColor = if (selected) Color(0xD62F695C) else Color(0xBB171813)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = Color(0xFFFFF0BD),
            fontSize = 10.sp,
            lineHeight = 11.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun rememberGuildHubHotspotMaskBitmap(): Bitmap? {
    val context = LocalContext.current
    return remember(context) {
        BitmapFactory.decodeResource(context.resources, R.drawable.guild_hub_hotspot_mask)
    }
}

@Composable
private fun GuildHubMaskHighlights(
    maskBitmap: Bitmap,
    selectedHotspot: GuildHubHotspot,
    pressedHotspot: GuildHubHotspot?,
) {
    val overlay = remember(maskBitmap, selectedHotspot, pressedHotspot) {
        buildGuildHubHotspotOverlayBitmap(
            maskBitmap = maskBitmap,
            selectedHotspot = selectedHotspot,
            pressedHotspot = pressedHotspot,
        ).asImageBitmap()
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val destinationSize = IntSize(
            width = size.width.roundToInt().coerceAtLeast(1),
            height = size.height.roundToInt().coerceAtLeast(1),
        )
        drawImage(image = overlay, dstSize = destinationSize)
    }
}

private fun buildGuildHubHotspotOverlayBitmap(
    maskBitmap: Bitmap,
    selectedHotspot: GuildHubHotspot,
    pressedHotspot: GuildHubHotspot?,
): Bitmap {
    val width = maskBitmap.width
    val height = maskBitmap.height
    val sourcePixels = IntArray(width * height)
    val outputPixels = IntArray(width * height)
    maskBitmap.getPixels(sourcePixels, 0, width, 0, 0, width, height)

    for (y in 0 until height) {
        for (x in 0 until width) {
            val hotspot = guildHubHotspotAt(sourcePixels, width, x, y) ?: continue
            val isPressed = hotspot == pressedHotspot
            val isSelected = hotspot == selectedHotspot
            val pixel = y * width + x
            val softEdge = guildHubHotspotEdge(sourcePixels, width, height, x, y, hotspot, radius = 1)
            val wideEdge = (isPressed || isSelected) && guildHubHotspotEdge(sourcePixels, width, height, x, y, hotspot, radius = 2)

            outputPixels[pixel] = when {
                isPressed && wideEdge -> GuildHubHighlightPressedRim
                isPressed -> GuildHubHighlightPressedFill
                isSelected && wideEdge -> GuildHubHighlightSelectedRim
                isSelected -> GuildHubHighlightSelectedFill
                softEdge -> GuildHubHighlightIdleRim
                else -> outputPixels[pixel]
            }

            if (isPressed && softEdge) {
                paintGuildHubHotspotHalo(
                    sourcePixels = sourcePixels,
                    outputPixels = outputPixels,
                    width = width,
                    height = height,
                    x = x,
                    y = y,
                    radius = 3,
                    color = GuildHubHighlightPressedHalo,
                )
            } else if (isSelected && softEdge) {
                paintGuildHubHotspotHalo(
                    sourcePixels = sourcePixels,
                    outputPixels = outputPixels,
                    width = width,
                    height = height,
                    x = x,
                    y = y,
                    radius = 2,
                    color = GuildHubHighlightSelectedHalo,
                )
            }
        }
    }

    return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
        setPixels(outputPixels, 0, width, 0, 0, width, height)
    }
}
private val GuildHubHighlightIdleRim = guildHubHighlightArgb(alpha = 0x22, rgb = 0xFFF0B8)
private val GuildHubHighlightSelectedFill = guildHubHighlightArgb(alpha = 0x12, rgb = 0xFFE7A3)
private val GuildHubHighlightSelectedRim = guildHubHighlightArgb(alpha = 0xA4, rgb = 0xFFE9A7)
private val GuildHubHighlightSelectedHalo = guildHubHighlightArgb(alpha = 0x4E, rgb = 0xFFB75A)
private val GuildHubHighlightPressedFill = guildHubHighlightArgb(alpha = 0x24, rgb = 0xFFF1B8)
private val GuildHubHighlightPressedRim = guildHubHighlightArgb(alpha = 0xC8, rgb = 0xFFF6C8)
private val GuildHubHighlightPressedHalo = guildHubHighlightArgb(alpha = 0x68, rgb = 0xFFC46B)
private const val GuildHubHighlightTransparent = 0x00000000

private fun guildHubHighlightArgb(alpha: Int, rgb: Int): Int =
    (alpha.coerceIn(0x00, 0xFF) shl 24) or (rgb and 0x00FFFFFF)

private fun guildHubHotspotAt(
    sourcePixels: IntArray,
    width: Int,
    x: Int,
    y: Int,
): GuildHubHotspot? = guildHubHotspotForMaskColor(sourcePixels[y * width + x])

private fun guildHubHotspotEdge(
    sourcePixels: IntArray,
    width: Int,
    height: Int,
    x: Int,
    y: Int,
    hotspot: GuildHubHotspot,
    radius: Int,
): Boolean {
    for (dy in -radius..radius) {
        for (dx in -radius..radius) {
            if (dx == 0 && dy == 0) continue
            val nx = x + dx
            val ny = y + dy
            if (nx !in 0 until width || ny !in 0 until height) return true
            if (guildHubHotspotAt(sourcePixels, width, nx, ny) != hotspot) return true
        }
    }
    return false
}

private fun paintGuildHubHotspotHalo(
    sourcePixels: IntArray,
    outputPixels: IntArray,
    width: Int,
    height: Int,
    x: Int,
    y: Int,
    radius: Int,
    color: Int,
) {
    for (dy in -radius..radius) {
        for (dx in -radius..radius) {
            val nx = x + dx
            val ny = y + dy
            if (nx !in 0 until width || ny !in 0 until height) continue
            val pixel = ny * width + nx
            if (guildHubHotspotAt(sourcePixels, width, nx, ny) == null && outputPixels[pixel] == GuildHubHighlightTransparent) {
                outputPixels[pixel] = color
            }
        }
    }
}
@Composable
private fun GuildHubSelectionDrawer(
    session: PlaySessionState,
    selectedQuest: Quest,
    nowMillis: Long,
    selectedHotspot: GuildHubHotspot,
    onViewResult: () -> Unit,
    onNextQuest: () -> Unit,
    onAchievements: () -> Unit,
    onLoot: () -> Unit,
    onFacilities: () -> Unit,
    onManageCoreCrew: () -> Unit,
    onFinishQuestNow: () -> Unit,
    bodyMaxLines: Int = 2,
    modifier: Modifier = Modifier,
) {
    val debugEnabled = booleanResource(R.bool.debug_tools_enabled)
    val title = guildHubHotspotLabel(selectedHotspot)
    val body = guildHubDrawerBody(session, selectedQuest, nowMillis, selectedHotspot)
    val primaryLabel = when (selectedHotspot) {
        GuildHubHotspot.Charter -> stringResource(R.string.guild_hub_open_achievements_action)
        GuildHubHotspot.CoreCrew -> stringResource(R.string.guild_hub_manage_core_crew_action)
        GuildHubHotspot.QuestTable -> if (session.phase == PlayPhase.ResultReady) {
            stringResource(R.string.view_report_action)
        } else {
            stringResource(R.string.guild_hub_open_quests_action)
        }
        GuildHubHotspot.LootCache -> stringResource(R.string.guild_hub_open_loot_action)
        GuildHubHotspot.Facilities -> stringResource(R.string.guild_hub_open_facilities_action)
    }
    val primaryAction: () -> Unit = when (selectedHotspot) {
        GuildHubHotspot.Charter -> onAchievements
        GuildHubHotspot.CoreCrew -> onManageCoreCrew
        GuildHubHotspot.QuestTable -> if (session.phase == PlayPhase.ResultReady) onViewResult else onNextQuest
        GuildHubHotspot.LootCache -> onLoot
        GuildHubHotspot.Facilities -> onFacilities
    }
    val secondaryLabel = when {
        selectedHotspot == GuildHubHotspot.QuestTable && session.phase == PlayPhase.ResultReady -> stringResource(R.string.guild_hub_open_quests_action)
        selectedHotspot == GuildHubHotspot.QuestTable && session.phase == PlayPhase.Running && debugEnabled -> stringResource(R.string.instant_quest_action)
        else -> null
    }
    val secondaryAction: (() -> Unit)? = when {
        selectedHotspot == GuildHubHotspot.QuestTable && session.phase == PlayPhase.ResultReady -> onNextQuest
        selectedHotspot == GuildHubHotspot.QuestTable && session.phase == PlayPhase.Running && debugEnabled -> onFinishQuestNow
        else -> null
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xEA171813)),
        border = BorderStroke(1.dp, guildHubHotspotColor(selectedHotspot).copy(alpha = 0.65f)),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    color = Color(0xFFFFF0BD),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = phaseStatus(session.phase),
                    color = Color(0xFFD7C891),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                )
            }
            Spacer(Modifier.height(5.dp))
            Text(
                text = body,
                color = Color(0xFFE6D8A6),
                fontSize = 12.sp,
                lineHeight = 15.sp,
                maxLines = bodyMaxLines,
                overflow = TextOverflow.Ellipsis,
            )
            if (selectedHotspot == GuildHubHotspot.QuestTable && session.phase == PlayPhase.Running) {
                ProgressBar(progress = session.progress(nowMillis).toFloat())
            }
            ActionRow(
                primaryLabel = primaryLabel,
                secondaryLabel = secondaryLabel,
                onPrimary = primaryAction,
                onSecondary = secondaryAction,
            )
        }
    }
}

@Composable
private fun guildHubDrawerBody(
    session: PlaySessionState,
    selectedQuest: Quest,
    nowMillis: Long,
    selectedHotspot: GuildHubHotspot,
): String = when (selectedHotspot) {
    GuildHubHotspot.Charter -> stringResource(
        R.string.guild_hub_charter_body,
        session.completedAchievementCount(),
        session.claimableAchievementCount(),
        session.achievementSeals(),
    )
    GuildHubHotspot.CoreCrew -> stringResource(
        R.string.guild_hub_core_crew_body,
        session.normalizedCoreCrewHeroIds().size,
        session.coreCrewSlots(),
        session.passiveGoldPerHour(),
    )
    GuildHubHotspot.QuestTable -> when (session.phase) {
        PlayPhase.Idle -> stringResource(R.string.guild_hub_quests_idle_body, selectedQuest.title)
        PlayPhase.Running -> stringResource(R.string.running_quest_summary, remainingSeconds(session, nowMillis))
        PlayPhase.ResultReady -> session.expedition?.result?.let { result ->
            stringResource(
                R.string.result_quest_summary,
                outcomeLabel(result.outcome),
                rewardGoldWithNoticeBoard(session),
                session.collectableLootRolls(result),
            )
        } ?: stringResource(R.string.guild_hub_quests_idle_body, selectedQuest.title)
    }
    GuildHubHotspot.LootCache -> stringResource(
        R.string.guild_hub_loot_body,
        session.lootItems.size,
        session.pendingLootItems.size,
    )
    GuildHubHotspot.Facilities -> stringResource(
        R.string.guild_hub_facilities_body,
        guildFacilityLevelTotal(session),
    )
}

@Composable
private fun guildHubHotspotLabel(hotspot: GuildHubHotspot): String = when (hotspot) {
    GuildHubHotspot.Charter -> stringResource(R.string.guild_hub_charter_title)
    GuildHubHotspot.CoreCrew -> stringResource(R.string.guild_hub_core_crew_title)
    GuildHubHotspot.QuestTable -> stringResource(R.string.guild_hub_quests_title)
    GuildHubHotspot.LootCache -> stringResource(R.string.guild_hub_loot_title)
    GuildHubHotspot.Facilities -> stringResource(R.string.guild_hub_facilities_title)
}
@Composable
private fun guildHubHotspotHintLabel(hotspot: GuildHubHotspot): String = when (hotspot) {
    GuildHubHotspot.Charter -> stringResource(R.string.guild_hub_hint_charter)
    GuildHubHotspot.CoreCrew -> stringResource(R.string.guild_hub_hint_core_crew)
    GuildHubHotspot.QuestTable -> stringResource(R.string.guild_hub_hint_quests)
    GuildHubHotspot.LootCache -> stringResource(R.string.guild_hub_hint_loot)
    GuildHubHotspot.Facilities -> stringResource(R.string.guild_hub_hint_facilities)
}

private fun guildHubHotspotColor(hotspot: GuildHubHotspot): Color = when (hotspot) {
    GuildHubHotspot.Charter -> Color(0xFFE7A177)
    GuildHubHotspot.CoreCrew -> Color(0xFFBBD18A)
    GuildHubHotspot.QuestTable -> Color(0xFFFFC26B)
    GuildHubHotspot.LootCache -> Color(0xFFFFD66D)
    GuildHubHotspot.Facilities -> Color(0xFFD7B17A)
}

private fun guildFacilityLevelTotal(session: PlaySessionState): Int =
    GuildFacility.values().sumOf { facility -> session.facilityUpgradeState(facility).level }

@Composable
private fun CoreCrewDialog(
    session: PlaySessionState,
    onToggleCoreCrew: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .heightIn(max = 640.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF171813)),
            border = BorderStroke(1.dp, Color(0x885EE08B)),
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.core_crew_title),
                        color = Color(0xFFFFF0BD),
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                    )
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F695C)),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text(stringResource(R.string.close_action), fontWeight = FontWeight.Black)
                    }
                }
                Spacer(Modifier.height(8.dp))
                CoreCrewPanel(session = session, onToggleCoreCrew = onToggleCoreCrew)
            }
        }
    }
}
@Composable
internal fun CoreCrewPanel(session: PlaySessionState, onToggleCoreCrew: (String) -> Unit) {
    val selectedIds = session.normalizedCoreCrewHeroIds()
    val selectedIdSet = selectedIds.toSet()
    val activeHeroIds = if (session.phase == PlayPhase.Running) {
        session.expedition?.partyHeroIds.orEmpty().toSet()
    } else {
        emptySet()
    }
    val orderedHeroes = session.heroes.sortedWith(
        compareByDescending<Hero> { if (it.id in selectedIdSet) 1 else 0 }
            .thenByDescending { it.rarity }
            .thenBy { it.name },
    )

    PaperPanel(
        title = stringResource(R.string.core_crew_title),
        body = stringResource(
            R.string.core_crew_summary,
            selectedIds.size,
            session.coreCrewSlots(),
            session.passiveGoldPerHour(),
        ),
    ) {
        Spacer(Modifier.height(8.dp))
        orderedHeroes.forEach { hero ->
            val selected = hero.id in selectedIdSet
            CoreCrewHeroRow(
                hero = hero,
                selected = selected,
                canAssign = selected || session.canAssignCoreCrewHero(hero.id),
                contribution = session.coreCrewContribution(hero, activeHeroIds),
                onToggle = { onToggleCoreCrew(hero.id) },
            )
        }
    }
}

@Composable
internal fun CoreCrewHeroRow(
    hero: Hero,
    selected: Boolean,
    canAssign: Boolean,
    contribution: com.ayoshy.badventurers.game.CoreCrewContribution,
    onToggle: () -> Unit,
) {
    val rowColor = if (selected) Color(0x332F695C) else Color(0x18FFFFFF)
    val actionLabel = when {
        selected -> stringResource(R.string.core_crew_assigned_action)
        canAssign -> stringResource(R.string.core_crew_assign_action)
        else -> stringResource(R.string.core_crew_full_action)
    }
    val detail = if (contribution.activeExpeditionPenaltyPercent > 0) {
        stringResource(
            R.string.core_crew_hero_active_detail,
            heroRarityLabel(hero.rarity),
            heroClassLabel(hero.heroClass),
            contribution.goldPerHour,
            contribution.fullGoldPerHour,
        )
    } else {
        stringResource(
            R.string.core_crew_hero_detail,
            heroRarityLabel(hero.rarity),
            heroClassLabel(hero.heroClass),
            contribution.goldPerHour,
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 7.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(rowColor)
            .clickable(enabled = canAssign, onClick = onToggle)
            .padding(horizontal = 9.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = hero.name,
                color = Color(0xFF211F1A),
                fontWeight = FontWeight.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = detail,
                color = Color(0xFF756B54),
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Button(
            onClick = onToggle,
            enabled = canAssign,
            modifier = Modifier
                .padding(start = 8.dp)
                .width(92.dp)
                .height(34.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selected) Color(0xFF2F695C) else Color(0xFFDCC86F),
                contentColor = if (selected) Color(0xFFF8F1D8) else Color(0xFF211F1A),
                disabledContainerColor = Color(0x77756B54),
                disabledContentColor = Color(0xCCFFF1BF),
            ),
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(actionLabel, fontSize = 11.sp, fontWeight = FontWeight.Black, maxLines = 1)
        }
    }
}

@Composable
internal fun TestProgressPanel(onResetProgress: () -> Unit) {
    PaperPanel(
        title = stringResource(R.string.debug_progress_title),
        body = stringResource(R.string.debug_progress_summary),
    ) {
        Button(
            onClick = onResetProgress,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B5531)),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(stringResource(R.string.debug_reset_progress_action), fontWeight = FontWeight.Black)
        }
    }
}

@Composable
internal fun ProgressionAdvicePanel(session: PlaySessionState, selectedQuest: Quest) {
    val advice = ProgressionAdvisor.recommend(state = session, selectedQuest = selectedQuest)
    PaperPanel(
        title = stringResource(R.string.recommended_title),
        body = progressionAdviceBody(session, advice),
    ) {
        ProgressBar(progress = progressionAdviceProgress(session, advice))
    }
}

@Composable
internal fun progressionAdviceBody(session: PlaySessionState, advice: ProgressionAdvice): String =
    when (advice.kind) {
        ProgressionAdviceKind.ViewQuestReport -> stringResource(R.string.progression_advice_view_report)
        ProgressionAdviceKind.HandleLootRewards -> stringResource(R.string.progression_advice_handle_loot)
        ProgressionAdviceKind.UpgradeNoticeBoard -> stringResource(
            R.string.progression_advice_upgrade_notice_board,
            advice.cost,
            advice.missingGold,
        )
        ProgressionAdviceKind.UpgradeTrainingYard -> stringResource(
            R.string.progression_advice_upgrade_training_yard,
            advice.cost,
            advice.missingGold,
        )
        ProgressionAdviceKind.UpgradeBunkRoom -> stringResource(
            R.string.progression_advice_upgrade_bunk_room,
            advice.cost,
            advice.missingGold,
        )
        ProgressionAdviceKind.ImprovePartyFit -> stringResource(
            R.string.progression_advice_improve_party,
            heroNamesForAdvice(session, advice),
            advice.currentSuccessChancePercent ?: 0,
            advice.projectedSuccessChancePercent ?: 0,
        )
        ProgressionAdviceKind.EquipLoot -> stringResource(
            R.string.progression_advice_equip_loot,
            lootNameForAdvice(session, advice),
            heroNamesForAdvice(session, advice),
        )
        ProgressionAdviceKind.RecruitHero -> stringResource(R.string.progression_advice_recruit_hero, advice.cost)
        ProgressionAdviceKind.ClaimAchievement -> stringResource(R.string.progression_advice_claim_achievement)
        ProgressionAdviceKind.EarnReputation -> stringResource(R.string.progression_advice_earn_reputation, advice.missingReputation)
        ProgressionAdviceKind.CompleteQuests -> stringResource(R.string.progression_advice_complete_quests, advice.missingCompletedQuests)
        ProgressionAdviceKind.StartQuest -> stringResource(R.string.progression_advice_start_quest, questNameForAdvice(advice))
    }

internal fun progressionAdviceProgress(session: PlaySessionState, advice: ProgressionAdvice): Float =
    when (advice.kind) {
        ProgressionAdviceKind.UpgradeNoticeBoard,
        ProgressionAdviceKind.UpgradeTrainingYard,
        ProgressionAdviceKind.UpgradeBunkRoom -> if (advice.cost <= 0) 1f else (session.gold.toFloat() / advice.cost).coerceIn(0f, 1f)
        ProgressionAdviceKind.ImprovePartyFit -> ((advice.projectedSuccessChancePercent ?: 0).toFloat() / 100f).coerceIn(0f, 1f)
        ProgressionAdviceKind.EarnReputation -> (session.reputation.toFloat() / (session.reputation + advice.missingReputation).coerceAtLeast(1)).coerceIn(0f, 1f)
        ProgressionAdviceKind.CompleteQuests -> (session.completedQuestCount.toFloat() / (session.completedQuestCount + advice.missingCompletedQuests).coerceAtLeast(1)).coerceIn(0f, 1f)
        ProgressionAdviceKind.ViewQuestReport,
        ProgressionAdviceKind.HandleLootRewards,
        ProgressionAdviceKind.ClaimAchievement -> 1f
        ProgressionAdviceKind.EquipLoot,
        ProgressionAdviceKind.RecruitHero,
        ProgressionAdviceKind.StartQuest -> 0.72f
    }

internal fun heroNamesForAdvice(session: PlaySessionState, advice: ProgressionAdvice): String =
    advice.heroIds
        .mapNotNull { heroId -> session.heroes.firstOrNull { it.id == heroId } ?: HeroCatalog.byId[heroId]?.toHero() }
        .joinToString { it.name }
        .ifBlank { "-" }

internal fun lootNameForAdvice(session: PlaySessionState, advice: ProgressionAdvice): String =
    (session.pendingLootItems + session.lootItems + session.equippedLoot.map { it.item })
        .firstOrNull { it.id == advice.itemId }
        ?.name
        ?: "loot"

internal fun questNameForAdvice(advice: ProgressionAdvice): String =
    advice.questId?.let { questId -> SeedGame.questById[questId]?.title } ?: SeedGame.firstQuest.title

@Composable
internal fun TierGoalPanel(session: PlaySessionState) {
    val threshold = LootGenerator.RARE_LOOT_COMPLETED_QUEST_THRESHOLD
    val completed = session.completedQuestCount.coerceAtMost(threshold)
    val unlocked = LootGenerator.isRareLootUnlocked(session.completedQuestCount)
    val body = if (unlocked) {
        stringResource(R.string.tier_goal_rare_loot_unlocked, LootGenerator.palier2RareLootProfile.rareWeight)
    } else {
        stringResource(R.string.tier_goal_rare_loot_locked, completed, threshold)
    }

    PaperPanel(
        title = stringResource(R.string.tier_goal_rare_loot_title),
        body = body,
    ) {
        ProgressBar(progress = completed.toFloat() / threshold.toFloat())
    }
}

@Composable
internal fun AchievementLedgerPanel(session: PlaySessionState, onOpen: () -> Unit) {
    val claimableCount = session.claimableAchievementCount()
    val nextMilestone = session.nextAchievementMilestone()
    val milestoneLine = nextMilestone?.let { milestone ->
        stringResource(R.string.achievements_next_milestone_detail, milestone.sealsRequired, milestone.title)
    } ?: stringResource(R.string.achievements_all_milestones_done)

    PaperPanel(
        title = stringResource(R.string.achievements_home_title),
        body = stringResource(
            R.string.achievements_home_summary,
            session.completedAchievementCount(),
            claimableCount,
            session.achievementSeals(),
        ),
    ) {
        Spacer(Modifier.height(6.dp))
        Text(
            text = milestoneLine,
            color = Color(0xFF493F2B),
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Button(
            onClick = onOpen,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F695C)),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(stringResource(R.string.achievements_open_action), fontWeight = FontWeight.Black)
        }
    }
}
@Composable
internal fun GuildFacilitiesPanel(session: PlaySessionState, selectedQuest: Quest) {
    val scoutState = session.facilityUpgradeState(GuildFacility.ScoutTable)
    val scoutBehavior = ScoutTableIntel.behavior(session.scoutTableLevel)
    val armoryState = session.facilityUpgradeState(GuildFacility.ArmoryForge)
    val tavernState = session.facilityUpgradeState(GuildFacility.TavernKitchen)
    val infirmaryState = session.facilityUpgradeState(GuildFacility.Infirmary)
    val accountantState = session.facilityUpgradeState(GuildFacility.AccountantOffice)
    PaperPanel(
        title = stringResource(R.string.guild_facilities_title),
        body = stringResource(R.string.guild_facilities_summary),
    ) {
        Spacer(Modifier.height(8.dp))
        FacilityLine(
            label = stringResource(R.string.guild_facility_notice_board),
            value = facilityLevelEffect(
                state = session.facilityUpgradeState(GuildFacility.NoticeBoard),
                effect = stringResource(R.string.guild_facility_notice_effect, session.noticeBoardGoldBonusPercent()),
            ),
        )
        FacilityLine(
            label = stringResource(R.string.guild_facility_training_yard),
            value = facilityLevelEffect(
                state = session.facilityUpgradeState(GuildFacility.TrainingYard),
                effect = stringResource(
                    R.string.guild_facility_training_effect,
                    session.trainingYardPowerBonus(),
                    session.trainingYardQuestXpBonusPercent(),
                ),
            ),
        )
        FacilityLine(
            label = stringResource(R.string.guild_facility_bunk_room),
            value = facilityLevelEffect(
                state = session.facilityUpgradeState(GuildFacility.BunkRoom),
                effect = stringResource(R.string.guild_facility_bunk_effect, session.effectivePartySlots(selectedQuest)),
            ),
        )
        FacilityLine(
            label = stringResource(R.string.guild_facility_scout_table),
            value = facilityLevelEffect(
                state = scoutState,
                effect = if (scoutState.unlocked || scoutState.level > 0) {
                    stringResource(R.string.guild_facility_scout_effect, scoutBehavior.revealedPlanWarnings)
                } else {
                    stringResource(R.string.guild_facility_scout_locked_effect)
                },
            ),
        )
        FacilityLine(
            label = stringResource(R.string.guild_facility_armory_forge),
            value = facilityLevelEffect(
                state = armoryState,
                effect = if (armoryState.unlocked || armoryState.level > 0) {
                    stringResource(R.string.guild_facility_armory_effect, session.passiveLootFindChancePercent())
                } else {
                    stringResource(R.string.guild_facility_armory_locked_effect)
                },
            ),
        )
        FacilityLine(
            label = stringResource(R.string.guild_facility_tavern_kitchen),
            value = facilityLevelEffect(
                state = tavernState,
                effect = if (tavernState.unlocked || tavernState.level > 0) {
                    stringResource(R.string.guild_facility_tavern_effect, (session.passiveIncomeCapBonusSeconds() / 60L).toInt())
                } else {
                    stringResource(R.string.guild_facility_tavern_locked_effect)
                },
            ),
        )
        FacilityLine(
            label = stringResource(R.string.guild_facility_infirmary),
            value = facilityLevelEffect(
                state = infirmaryState,
                effect = if (infirmaryState.unlocked || infirmaryState.level > 0) {
                    stringResource(
                        R.string.guild_facility_infirmary_effect,
                        session.infirmaryFailureRecoveryBonusPercent(),
                        session.infirmarySafePlanPowerBonus(ExpeditionPlanCatalog.safetyFirstId),
                    )
                } else {
                    stringResource(R.string.guild_facility_infirmary_locked_effect)
                },
            ),
        )
        FacilityLine(
            label = stringResource(R.string.guild_facility_accountant_office),
            value = facilityLevelEffect(
                state = accountantState,
                effect = if (accountantState.unlocked || accountantState.level > 0) {
                    stringResource(
                        R.string.guild_facility_accountant_effect,
                        session.passiveGoldPerHour(),
                        session.accountantOfficeQuestGoldBonusPercent(selectedQuest),
                        session.accountantOfficeDuplicateReputationBonus(),
                    )
                } else {
                    stringResource(R.string.guild_facility_accountant_locked_effect)
                },
            ),
        )
    }
}

@Composable
internal fun facilityLevelEffect(state: com.ayoshy.badventurers.game.GuildFacilityUpgradeState, effect: String): String =
    stringResource(R.string.guild_facility_level_effect, state.level, state.definition.maxLevel, effect)

@Composable
internal fun FacilityLine(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = Color(0xFF493F2B),
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = value,
            color = Color(0xFF211F1A),
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.End,
            maxLines = 1,
        )
    }
}
