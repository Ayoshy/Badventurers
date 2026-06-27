package com.ayoshy.badventurers.ui

import android.graphics.BitmapFactory
import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.booleanResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
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
import com.ayoshy.badventurers.game.GuildFacilityUpgradeState
import com.ayoshy.badventurers.game.guildReputationProgress
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

@Composable
internal fun UpgradesScreen(
    session: PlaySessionState,
    onAchievements: () -> Unit,
    onBuyNoticeBoard: () -> Unit,
    onBuyTrainingYard: () -> Unit,
    onBuyBunkRoom: () -> Unit,
    onBuyScoutTable: () -> Unit,
    onBuyArmoryForge: () -> Unit,
    onBuyTavernKitchen: () -> Unit,
    onBuyInfirmary: () -> Unit,
    onBuyAccountantOffice: () -> Unit,
) {
    val noticeBoardCost = session.noticeBoardUpgradeCost()
    val trainingYardCost = session.trainingYardUpgradeCost()
    val bunkRoomCost = session.bunkRoomUpgradeCost()
    val scoutTableState = session.facilityUpgradeState(GuildFacility.ScoutTable)
    val scoutTableCost = session.scoutTableUpgradeCost()
    val armoryForgeState = session.facilityUpgradeState(GuildFacility.ArmoryForge)
    val armoryForgeCost = session.armoryForgeUpgradeCost()
    val tavernKitchenState = session.facilityUpgradeState(GuildFacility.TavernKitchen)
    val tavernKitchenCost = session.tavernKitchenUpgradeCost()
    val infirmaryState = session.facilityUpgradeState(GuildFacility.Infirmary)
    val infirmaryCost = session.infirmaryUpgradeCost()
    val accountantOfficeState = session.facilityUpgradeState(GuildFacility.AccountantOffice)
    val accountantOfficeCost = session.accountantOfficeUpgradeCost()

    ScreenScaffold(title = stringResource(R.string.upgrades_title), status = stringResource(R.string.guild_upgrade_status)) {
        InfoRow(
            title = stringResource(R.string.upgrade_treasury_title),
            detail = stringResource(R.string.upgrade_treasury_detail),
            value = stringResource(R.string.gold_value, session.gold),
        )
        GuildReputationPanel(session = session)
        UpgradeRow(
            title = stringResource(R.string.notice_board_upgrade_title, session.noticeBoardLevel),
            detail = stringResource(R.string.notice_board_upgrade_detail),
            preview = upgradePreviewText(
                currentValue = session.noticeBoardGoldBonusPercent(),
                nextValue = session.noticeBoardLevel * 10,
                maxed = session.facilityUpgradeState(GuildFacility.NoticeBoard).maxed,
                currentFormat = R.string.notice_board_upgrade_preview,
                maxedFormat = R.string.notice_board_upgrade_maxed,
            ),
            cost = noticeBoardCost,
            currentGold = session.gold,
            enabled = session.canUpgradeFacility(GuildFacility.NoticeBoard),
            onBuy = onBuyNoticeBoard,
        )
        UpgradeRow(
            title = stringResource(R.string.training_yard_upgrade_title, session.trainingYardLevel),
            detail = stringResource(R.string.training_yard_upgrade_detail),
            preview = trainingYardUpgradePreviewText(session),
            cost = trainingYardCost,
            currentGold = session.gold,
            enabled = session.canUpgradeFacility(GuildFacility.TrainingYard),
            onBuy = onBuyTrainingYard,
        )
        UpgradeRow(
            title = stringResource(R.string.bunk_room_upgrade_title, session.bunkRoomLevel),
            detail = stringResource(R.string.bunk_room_upgrade_detail),
            preview = upgradePreviewText(
                currentValue = (session.bunkRoomLevel - 1).coerceAtLeast(0),
                nextValue = session.bunkRoomLevel,
                maxed = session.facilityUpgradeState(GuildFacility.BunkRoom).maxed,
                currentFormat = R.string.bunk_room_upgrade_preview,
                maxedFormat = R.string.bunk_room_upgrade_maxed,
            ),
            cost = bunkRoomCost,
            currentGold = session.gold,
            enabled = session.canUpgradeFacility(GuildFacility.BunkRoom),
            onBuy = onBuyBunkRoom,
        )
        UpgradeRow(
            title = stringResource(R.string.scout_table_upgrade_title, session.scoutTableLevel),
            detail = stringResource(R.string.scout_table_upgrade_detail),
            preview = scoutTableUpgradePreviewText(session),
            cost = scoutTableCost,
            currentGold = session.gold,
            enabled = session.canUpgradeFacility(GuildFacility.ScoutTable),
            disabledLabel = facilityDisabledLabel(scoutTableState),
            onBuy = onBuyScoutTable,
        )
        UpgradeRow(
            title = stringResource(R.string.armory_forge_upgrade_title, session.armoryForgeLevel),
            detail = stringResource(R.string.armory_forge_upgrade_detail),
            preview = armoryForgeUpgradePreviewText(session),
            cost = armoryForgeCost,
            currentGold = session.gold,
            enabled = session.canUpgradeFacility(GuildFacility.ArmoryForge),
            disabledLabel = facilityDisabledLabel(armoryForgeState),
            onBuy = onBuyArmoryForge,
        )
        UpgradeRow(
            title = stringResource(R.string.tavern_kitchen_upgrade_title, session.tavernKitchenLevel),
            detail = stringResource(R.string.tavern_kitchen_upgrade_detail),
            preview = tavernKitchenUpgradePreviewText(session),
            cost = tavernKitchenCost,
            currentGold = session.gold,
            enabled = session.canUpgradeFacility(GuildFacility.TavernKitchen),
            disabledLabel = facilityDisabledLabel(tavernKitchenState),
            onBuy = onBuyTavernKitchen,
        )
        UpgradeRow(
            title = stringResource(R.string.infirmary_upgrade_title, session.infirmaryLevel),
            detail = stringResource(R.string.infirmary_upgrade_detail),
            preview = infirmaryUpgradePreviewText(session),
            cost = infirmaryCost,
            currentGold = session.gold,
            enabled = session.canUpgradeFacility(GuildFacility.Infirmary),
            disabledLabel = facilityDisabledLabel(infirmaryState),
            onBuy = onBuyInfirmary,
        )
        UpgradeRow(
            title = stringResource(R.string.accountant_office_upgrade_title, session.accountantOfficeLevel),
            detail = stringResource(R.string.accountant_office_upgrade_detail),
            preview = accountantOfficeUpgradePreviewText(session),
            cost = accountantOfficeCost,
            currentGold = session.gold,
            enabled = session.canUpgradeFacility(GuildFacility.AccountantOffice),
            disabledLabel = facilityDisabledLabel(accountantOfficeState),
            onBuy = onBuyAccountantOffice,
        )
        DarkPanel(title = stringResource(R.string.next_unlock_title), body = stringResource(R.string.next_unlock_summary))
        AchievementLedgerPanel(session = session, onOpen = onAchievements)
    }
}

@Composable
private fun GuildReputationPanel(session: PlaySessionState) {
    val guildRank = session.guildReputationProgress()
    DarkPanel(
        title = stringResource(R.string.guild_rank_panel_title),
        body = guildReputationPanelBody(guildRank),
    ) {
        ProgressBar(progress = guildRank.progressFraction)
    }
}

@Composable
internal fun AchievementsScreen(
    session: PlaySessionState,
    onClaim: (String) -> Unit,
    onClaimAll: () -> Unit,
    onBack: () -> Unit,
) {
    val definitions = AchievementCatalog.definitions.sortedWith(
        compareByDescending<AchievementDefinition> { definition ->
            val progress = session.achievementProgressFor(definition)
            progress.isCompleted(definition) && !progress.isClaimed
        }.thenBy { it.category.ordinal }.thenBy { it.title },
    )
    var selectedHotspotName by rememberSaveable {
        mutableStateOf(defaultAchievementLedgerHotspot(session, definitions).name)
    }
    val selectedHotspot = AchievementLedgerHotspot.values()
        .firstOrNull { it.name == selectedHotspotName }
        ?: defaultAchievementLedgerHotspot(session, definitions)
    val selectedCategories = achievementLedgerCategoriesForHotspot(selectedHotspot)
    val selectedDefinitions = definitions.filter { definition -> definition.category in selectedCategories }

    ScreenScaffold(
        title = stringResource(R.string.achievements_title),
        status = stringResource(R.string.achievements_status, session.achievementSeals()),
    ) {
        AchievementLedgerArtwork(
            selectedHotspot = selectedHotspot,
            onHotspotSelected = { hotspot -> selectedHotspotName = hotspot.name },
        )
        AchievementLedgerSelectionDrawer(
            session = session,
            selectedHotspot = selectedHotspot,
            definitions = selectedDefinitions,
            onClaimAll = onClaimAll,
            onBack = onBack,
        )
        AchievementCategorySection(
            category = selectedHotspot.category,
            definitions = selectedDefinitions,
            session = session,
            onClaim = onClaim,
        )
    }
}

private fun defaultAchievementLedgerHotspot(
    session: PlaySessionState,
    definitions: List<AchievementDefinition>,
): AchievementLedgerHotspot {
    val claimableCategory = definitions.firstOrNull { definition ->
        val progress = session.achievementProgressFor(definition)
        progress.isCompleted(definition) && !progress.isClaimed
    }?.category
    return claimableCategory?.let(::achievementLedgerHotspotForCategory)
        ?: AchievementLedgerHotspot.QuestRecords
}

private fun achievementLedgerHotspotForCategory(category: AchievementCategory): AchievementLedgerHotspot =
    AchievementLedgerHotspot.values().firstOrNull { hotspot -> hotspot.category == category }
        ?: AchievementLedgerHotspot.Miscellaneous

private fun achievementLedgerCategoriesForHotspot(hotspot: AchievementLedgerHotspot): Set<AchievementCategory> =
    when (hotspot) {
        AchievementLedgerHotspot.Miscellaneous -> setOf(AchievementCategory.Idle, AchievementCategory.Secret)
        else -> setOf(hotspot.category)
    }

@Composable
private fun AchievementLedgerArtwork(
    selectedHotspot: AchievementLedgerHotspot,
    onHotspotSelected: (AchievementLedgerHotspot) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hotspotMaskBitmap = rememberAchievementLedgerHotspotMaskBitmap()
    var pressedHotspotName by remember { mutableStateOf<String?>(null) }
    val pressedHotspot = AchievementLedgerHotspot.values().firstOrNull { it.name == pressedHotspotName }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(AchievementLedgerAspectRatio)
            .padding(bottom = 10.dp)
            .clip(RoundedCornerShape(8.dp))
            .pointerInput(hotspotMaskBitmap) {
                detectTapGestures(
                    onPress = { tap ->
                        val mask = hotspotMaskBitmap ?: return@detectTapGestures
                        val hotspot = achievementLedgerHotspotAtRenderedPoint(
                            pointX = tap.x,
                            pointY = tap.y,
                            containerWidth = size.width.toFloat(),
                            containerHeight = size.height.toFloat(),
                            sourceWidth = mask.width,
                            sourceHeight = mask.height,
                            contentScale = AchievementLedgerImageScale.Fit,
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
                        val hotspot = achievementLedgerHotspotAtRenderedPoint(
                            pointX = tap.x,
                            pointY = tap.y,
                            containerWidth = size.width.toFloat(),
                            containerHeight = size.height.toFloat(),
                            sourceWidth = mask.width,
                            sourceHeight = mask.height,
                            contentScale = AchievementLedgerImageScale.Fit,
                            maskColorAt = { x, y -> mask.getPixel(x, y) },
                        )
                        if (hotspot != null) onHotspotSelected(hotspot)
                    },
                )
            },
    ) {
        Image(
            painter = painterResource(R.drawable.achievement_ledger_interactive),
            contentDescription = stringResource(R.string.achievement_ledger_art_content_description),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds,
        )
        AchievementLedgerSelectionHighlight(
            selectedHotspot = selectedHotspot,
            pressedHotspot = pressedHotspot,
        )
    }
}

@Composable
private fun AchievementLedgerSelectionDrawer(
    session: PlaySessionState,
    selectedHotspot: AchievementLedgerHotspot,
    definitions: List<AchievementDefinition>,
    onClaimAll: () -> Unit,
    onBack: () -> Unit,
) {
    val completed = definitions.count { definition -> session.achievementProgressFor(definition).isCompleted(definition) }
    val claimable = definitions.count { definition ->
        val progress = session.achievementProgressFor(definition)
        progress.isCompleted(definition) && !progress.isClaimed
    }
    val sealsInCase = definitions.sumOf { definition -> achievementRewardTotals(definition).seals }
    val body = if (definitions.isEmpty()) {
        stringResource(R.string.achievement_ledger_drawer_empty_body)
    } else {
        stringResource(
            R.string.achievement_ledger_drawer_body,
            completed,
            definitions.size,
            claimable,
            sealsInCase,
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xEA171813)),
        border = BorderStroke(1.dp, achievementLedgerHotspotColor(selectedHotspot).copy(alpha = 0.72f)),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = achievementLedgerHotspotLabel(selectedHotspot),
                    color = Color(0xFFFFF0BD),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = if (claimable > 0) {
                        stringResource(R.string.achievements_section_claimable, claimable)
                    } else {
                        stringResource(R.string.achievements_completed_value, completed, definitions.size)
                    },
                    color = achievementLedgerHotspotColor(selectedHotspot),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
            Spacer(Modifier.height(5.dp))
            Text(
                text = body,
                color = Color(0xFFE6D8A6),
                fontSize = 12.sp,
                lineHeight = 15.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            ActionRow(
                primaryLabel = stringResource(R.string.achievements_claim_all_action),
                secondaryLabel = stringResource(R.string.achievements_back_to_upgrades_action),
                onPrimary = onClaimAll,
                onSecondary = onBack,
                primaryEnabled = session.claimableAchievementCount() > 0,
            )
        }
    }
}

@Composable
private fun rememberAchievementLedgerHotspotMaskBitmap(): Bitmap? {
    val context = LocalContext.current
    return remember(context) {
        BitmapFactory.decodeResource(context.resources, R.drawable.achievement_ledger_hotspot_mask)
    }
}

@Composable
private fun AchievementLedgerSelectionHighlight(
    selectedHotspot: AchievementLedgerHotspot,
    pressedHotspot: AchievementLedgerHotspot?,
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val imageRect = achievementLedgerRenderedImageRect(
            containerWidth = size.width,
            containerHeight = size.height,
            sourceWidth = AchievementLedgerBaseArtWidth,
            sourceHeight = AchievementLedgerBaseArtHeight,
            contentScale = AchievementLedgerImageScale.Fit,
        ) ?: return@Canvas
        val scaleX = imageRect.width / AchievementLedgerBaseArtWidth.toFloat()
        val scaleY = imageRect.height / AchievementLedgerBaseArtHeight.toFloat()

        AchievementLedgerHotspot.values().forEach { hotspot ->
            val bounds = achievementLedgerHotspotBounds(hotspot)
            val left = imageRect.left + bounds.left * scaleX
            val top = imageRect.top + bounds.top * scaleY
            val highlightSize = Size(bounds.size * scaleX, bounds.size * scaleY)
            val selected = hotspot == selectedHotspot
            val pressed = hotspot == pressedHotspot
            val color = achievementLedgerHotspotColor(hotspot)

            if (selected || pressed) {
                val borderWidth = if (pressed) 4.dp.toPx() else 3.dp.toPx()
                val glowWidth = borderWidth + 5.dp.toPx()
                drawRect(
                    color = color.copy(alpha = if (pressed) 0.18f else 0.12f),
                    topLeft = Offset(left, top),
                    size = highlightSize,
                )
                drawRect(
                    color = Color(0xFFFFF0BD).copy(alpha = if (pressed) 0.72f else 0.46f),
                    topLeft = Offset(left - glowWidth / 2f, top - glowWidth / 2f),
                    size = Size(highlightSize.width + glowWidth, highlightSize.height + glowWidth),
                    style = Stroke(width = glowWidth),
                )
                drawRect(
                    color = Color(0xFFFFD66D),
                    topLeft = Offset(left, top),
                    size = highlightSize,
                    style = Stroke(width = borderWidth),
                )
                drawRect(
                    color = color.copy(alpha = 0.9f),
                    topLeft = Offset(left + borderWidth, top + borderWidth),
                    size = Size(highlightSize.width - borderWidth * 2f, highlightSize.height - borderWidth * 2f),
                    style = Stroke(width = 1.dp.toPx()),
                )
            }
        }
    }
}
@Composable
private fun achievementLedgerHotspotLabel(hotspot: AchievementLedgerHotspot): String =
    achievementCategoryLabel(hotspot.category)

private fun achievementLedgerHotspotColor(hotspot: AchievementLedgerHotspot): Color = when (hotspot) {
    AchievementLedgerHotspot.QuestRecords -> Color(0xFFFFC26B)
    AchievementLedgerHotspot.GuildCharter -> Color(0xFFE7A177)
    AchievementLedgerHotspot.HeroRoster -> Color(0xFFBBD18A)
    AchievementLedgerHotspot.LootDisplay -> Color(0xFFFFD66D)
    AchievementLedgerHotspot.ResultTrophy -> Color(0xFFD7B17A)
    AchievementLedgerHotspot.Miscellaneous -> Color(0xFF9DDBB3)
}
@Composable
internal fun AchievementHeroPanel(
    completedCount: Int,
    totalCount: Int,
    claimableCount: Int,
    sealCount: Int,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(156.dp)
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF171813)),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(R.drawable.achievement_unlock_banner),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xCC171813), Color(0xDD24251F)),
                        ),
                    ),
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.achievements_summary_title),
                        color = Color(0xFFFFF1C0),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = stringResource(R.string.achievements_hero_body),
                        color = Color(0xFFE6D8A6),
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                ) {
                    AchievementStatChip(
                        label = stringResource(R.string.achievements_completed_label),
                        value = stringResource(R.string.achievements_completed_value, completedCount, totalCount),
                        modifier = Modifier.weight(1f),
                    )
                    AchievementStatChip(
                        label = stringResource(R.string.achievements_claimable_label),
                        value = claimableCount.toString(),
                        modifier = Modifier.weight(1f),
                    )
                    AchievementStatChip(
                        label = stringResource(R.string.achievements_seals_label),
                        value = sealCount.toString(),
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
internal fun AchievementStatChip(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xDDFFF1BF))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = label,
            color = Color(0xFF6B5A2D),
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = value,
            color = Color(0xFF211F1A),
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
internal fun AchievementClaimQueuePanel(
    session: PlaySessionState,
    claimableDefinitions: List<AchievementDefinition>,
    onClaimAll: () -> Unit,
    onBack: () -> Unit,
) {
    val claimableCount = claimableDefinitions.size
    val body = if (claimableCount > 0) {
        stringResource(
            R.string.achievements_claim_queue_body,
            claimableCount,
            session.claimableAchievementSeals(),
        )
    } else {
        stringResource(R.string.achievements_claim_queue_empty_body)
    }

    PaperPanel(
        title = if (claimableCount > 0) {
            stringResource(R.string.achievements_claim_queue_title, claimableCount)
        } else {
            stringResource(R.string.achievements_claim_queue_empty_title)
        },
        body = body,
    ) {
        if (claimableDefinitions.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            claimableDefinitions.take(3).forEach { definition ->
                AchievementClaimQueueLine(definition = definition)
            }
            if (claimableDefinitions.size > 3) {
                Text(
                    text = stringResource(R.string.achievements_claim_queue_more, claimableDefinitions.size - 3),
                    color = Color(0xFF493F2B),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(
                onClick = onClaimAll,
                enabled = claimableCount > 0,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2F695C),
                    disabledContainerColor = Color(0xFF6B5E3C),
                    disabledContentColor = Color(0xFFFFF1C0),
                ),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    text = stringResource(R.string.achievements_claim_all_action),
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Button(
                onClick = onBack,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDEC777), contentColor = Color(0xFF211F1A)),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    text = stringResource(R.string.achievements_back_to_upgrades_action),
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
internal fun AchievementClaimQueueLine(definition: AchievementDefinition) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0x223C6A55))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = achievementCategoryShort(definition.category),
            color = Color(0xFF2F695C),
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.width(18.dp),
            textAlign = TextAlign.Center,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = definition.title,
                color = Color(0xFF211F1A),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = achievementRewardSummary(definition),
                color = Color(0xFF493F2B),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
internal fun AchievementMilestonePanel(
    currentSeals: Int,
    nextMilestone: com.ayoshy.badventurers.game.CharterMilestone?,
) {
    val targetSeals = nextMilestone?.sealsRequired
        ?: AchievementCatalog.milestones.maxOf { milestone -> milestone.sealsRequired }
    val body = nextMilestone?.let { milestone ->
        stringResource(
            R.string.achievements_charter_next_body,
            currentSeals.coerceAtMost(milestone.sealsRequired),
            milestone.sealsRequired,
            milestone.title,
        )
    } ?: stringResource(R.string.achievements_charter_complete_body)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xF01A1B15)),
        border = BorderStroke(1.dp, Color(0x665C4A24)),
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.achievements_charter_title),
                    color = Color(0xFFFFF1C0),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "${currentSeals.coerceAtMost(targetSeals)}/$targetSeals",
                    color = Color(0xFFD0A24A),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.End,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
            Text(
                text = body,
                color = Color(0xFFE6D8A6),
                fontSize = 11.sp,
                lineHeight = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 3.dp),
            )
            Spacer(Modifier.height(8.dp))
            ProgressBar(
                progress = (currentSeals.toFloat() / targetSeals.toFloat().coerceAtLeast(1f)).coerceIn(0f, 1f),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                AchievementCatalog.milestones.forEach { milestone ->
                    AchievementMilestonePip(
                        sealsRequired = milestone.sealsRequired,
                        unlocked = currentSeals >= milestone.sealsRequired,
                        isNext = nextMilestone == milestone,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
internal fun AchievementMilestonePip(
    sealsRequired: Int,
    unlocked: Boolean,
    isNext: Boolean,
    modifier: Modifier = Modifier,
) {
    val status = when {
        unlocked -> stringResource(R.string.achievements_milestone_unlocked)
        isNext -> stringResource(R.string.achievements_milestone_next)
        else -> stringResource(R.string.achievements_milestone_locked)
    }
    val accent = when {
        unlocked -> Color(0xFF2F695C)
        isNext -> Color(0xFFD0A24A)
        else -> Color(0xFF5F573C)
    }
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (unlocked || isNext) Color(0x332F695C) else Color(0x1FFFFFFF))
            .padding(horizontal = 5.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = sealsRequired.toString(),
            color = accent,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            maxLines = 1,
        )
        Text(
            text = status,
            color = Color(0xFFE6D8A6),
            fontSize = 8.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
@Composable
internal fun AchievementCategorySection(
    category: AchievementCategory,
    definitions: List<AchievementDefinition>,
    session: PlaySessionState,
    onClaim: (String) -> Unit,
) {
    val completed = definitions.count { definition -> session.achievementProgressFor(definition).isCompleted(definition) }
    val claimable = definitions.count { definition ->
        val progress = session.achievementProgressFor(definition)
        progress.isCompleted(definition) && !progress.isClaimed
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFD0A24A)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = achievementCategoryShort(category),
                    color = Color(0xFF211F1A),
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                )
            }
            Column {
                Text(
                    text = achievementCategoryLabel(category),
                    color = Color(0xFFFFF1C0),
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = stringResource(R.string.achievements_section_summary, completed, definitions.size),
                    color = Color(0xFFBFAF7E),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (claimable > 0) {
            Text(
                text = stringResource(R.string.achievements_section_claimable, claimable),
                color = Color(0xFFFFF1C0),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF2F695C))
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }

    definitions.forEach { definition ->
        AchievementRow(
            definition = definition,
            progress = session.achievementProgressFor(definition),
            onClaim = onClaim,
        )
    }
}

@Composable
internal fun AchievementRow(
    definition: AchievementDefinition,
    progress: com.ayoshy.badventurers.game.AchievementProgress,
    onClaim: (String) -> Unit,
) {
    val completed = progress.isCompleted(definition)
    val claimable = completed && !progress.isClaimed
    val progressValue = progress.current.coerceAtMost(definition.target)
    val progressFraction = if (definition.target <= 0) 1f else progressValue.toFloat() / definition.target.toFloat()
    val rowColor = when {
        claimable -> Color(0xFFFFF1BF)
        progress.isClaimed -> Color(0xEEF1E2A8)
        else -> Color(0xEEF8E7B5)
    }
    val accentColor = when {
        claimable -> Color(0xFF2F695C)
        progress.isClaimed -> Color(0xFF7B6A43)
        else -> Color(0xFFD0A24A)
    }
    val statusText = when {
        claimable -> stringResource(R.string.achievements_ready_value)
        progress.isClaimed -> stringResource(R.string.achievements_claimed_value)
        else -> stringResource(R.string.achievements_progress_value, progressValue, definition.target)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = rowColor),
        border = BorderStroke(2.dp, if (claimable) Color(0xFFD0A24A) else Color(0xAA7B6A43)),
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = achievementCategoryShort(definition.category),
                        color = Color(0xFFFFF1C0),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = definition.title,
                            color = Color(0xFF211F1A),
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = statusText,
                            color = if (claimable) Color(0xFFFFF1C0) else Color(0xFF211F1A),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (claimable) Color(0xFF2F695C) else Color(0x227B6A43))
                                .padding(horizontal = 7.dp, vertical = 4.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Text(
                        text = definition.summary,
                        color = Color(0xFF756B54),
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.achievements_progress_label),
                    color = Color(0xFF493F2B),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    text = stringResource(R.string.achievements_progress_value, progressValue, definition.target),
                    color = Color(0xFF493F2B),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                )
            }
            ProgressBar(progress = progressFraction.coerceIn(0f, 1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 7.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.achievements_reward_label),
                        color = Color(0xFF756B54),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                    )
                    Text(
                        text = achievementRewardSummary(definition),
                        color = Color(0xFF493F2B),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (claimable) {
                    Button(
                        onClick = { onClaim(definition.id) },
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .width(120.dp)
                            .height(38.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F695C)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.achievements_claim_reward_action),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                } else if (progress.isClaimed) {
                    Text(
                        text = stringResource(R.string.achievements_claimed_detail),
                        color = Color(0xFF756B54),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .width(98.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

internal fun achievementCategoryShort(category: AchievementCategory): String =
    when (category) {
        AchievementCategory.Quest -> "Q"
        AchievementCategory.Guild -> "G"
        AchievementCategory.Hero -> "H"
        AchievementCategory.Loot -> "L"
        AchievementCategory.Result -> "R"
        AchievementCategory.Idle -> "I"
        AchievementCategory.Secret -> "?"
    }

@Composable
internal fun achievementCategoryLabel(category: AchievementCategory): String =
    when (category) {
        AchievementCategory.Quest -> stringResource(R.string.achievement_category_quest)
        AchievementCategory.Guild -> stringResource(R.string.achievement_category_guild)
        AchievementCategory.Hero -> stringResource(R.string.achievement_category_hero)
        AchievementCategory.Loot -> stringResource(R.string.achievement_category_loot)
        AchievementCategory.Result -> stringResource(R.string.achievement_category_result)
        AchievementCategory.Idle -> stringResource(R.string.achievement_category_idle)
        AchievementCategory.Secret -> stringResource(R.string.achievement_category_secret)
    }

@Composable
internal fun achievementRewardSummary(definition: AchievementDefinition): String {
    val totals = achievementRewardTotals(definition)
    val parts = mutableListOf<String>()
    if (totals.seals > 0) parts += stringResource(R.string.achievement_reward_seals, totals.seals)
    if (totals.gold > 0) parts += stringResource(R.string.achievement_reward_gold, totals.gold)
    if (totals.reputation > 0) parts += stringResource(R.string.achievement_reward_reputation, totals.reputation)
    if (totals.lootRolls > 0) parts += stringResource(R.string.achievement_reward_loot, totals.lootRolls)
    if (totals.specialContracts > 0) parts += stringResource(R.string.achievement_reward_contracts, totals.specialContracts)
    if (totals.tickets > 0) parts += stringResource(R.string.achievement_reward_tickets, totals.tickets)
    return parts.joinToString(" / ")
}

private data class AchievementRewardTotals(
    val seals: Int = 0,
    val gold: Int = 0,
    val reputation: Int = 0,
    val lootRolls: Int = 0,
    val specialContracts: Int = 0,
    val tickets: Int = 0,
)

private fun achievementRewardTotals(definition: AchievementDefinition): AchievementRewardTotals {
    fun addReward(
        totals: AchievementRewardTotals,
        item: com.ayoshy.badventurers.game.AchievementReward,
    ): AchievementRewardTotals = when (item) {
        is com.ayoshy.badventurers.game.AchievementReward.Currency -> totals.copy(
            gold = totals.gold + item.gold,
            reputation = totals.reputation + item.reputation,
            lootRolls = totals.lootRolls + item.lootRolls,
            specialContracts = totals.specialContracts + item.specialContracts,
        )
        is com.ayoshy.badventurers.game.AchievementReward.Tickets -> totals.copy(
            tickets = totals.tickets + item.tickets.values.sum(),
        )
        is com.ayoshy.badventurers.game.AchievementReward.Composite -> item.rewards.fold(totals, ::addReward)
        com.ayoshy.badventurers.game.AchievementReward.None -> totals
    }

    return addReward(AchievementRewardTotals(seals = definition.sealReward), definition.reward)
}

@Composable
internal fun UpgradeRow(
    title: String,
    detail: String,
    preview: String,
    cost: Int,
    currentGold: Int,
    enabled: Boolean,
    disabledLabel: String? = null,
    onBuy: () -> Unit,
) {
    val missingGold = (cost - currentGold).coerceAtLeast(0)
    val buttonLabel = if (enabled) {
        stringResource(R.string.buy_cost_action, cost)
    } else {
        disabledLabel ?: stringResource(R.string.upgrade_missing_gold_action, missingGold)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xF4FFF1BF)),
    ) {
        Row(
            modifier = Modifier.padding(9.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = Color(0xFF211F1A), fontWeight = FontWeight.Black, maxLines = 1)
                Text(
                    text = detail,
                    color = Color(0xFF756B54),
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = preview,
                    color = Color(0xFF3C6A55),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 3.dp),
                )
            }
            Button(
                onClick = onBuy,
                enabled = enabled,
                modifier = Modifier
                    .width(112.dp)
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2F695C),
                    contentColor = Color(0xFFF8F1D8),
                    disabledContainerColor = Color(0xFF6B5E3C),
                    disabledContentColor = Color(0xFFFFF1C0),
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp),
            ) {
                Text(
                    text = buttonLabel,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
internal fun facilityDisabledLabel(state: GuildFacilityUpgradeState): String? = when {
    !state.unlocked -> stringResource(R.string.upgrade_locked_action)
    state.maxed -> stringResource(R.string.upgrade_maxed_action)
    else -> null
}

@Composable
internal fun scoutTableUpgradePreviewText(session: PlaySessionState): String {
    val state = session.facilityUpgradeState(GuildFacility.ScoutTable)
    val current = ScoutTableIntel.behavior(session.scoutTableLevel)
    val next = ScoutTableIntel.behavior(session.scoutTableLevel + 1)
    return if (state.maxed) {
        stringResource(R.string.scout_table_upgrade_maxed, current.revealedPlanWarnings)
    } else {
        stringResource(R.string.scout_table_upgrade_preview, current.revealedPlanWarnings, next.revealedPlanWarnings)
    }
}

@Composable
internal fun armoryForgeUpgradePreviewText(session: PlaySessionState): String {
    val state = session.facilityUpgradeState(GuildFacility.ArmoryForge)
    val nextLevel = (session.armoryForgeLevel + 1).coerceAtMost(state.definition.maxLevel)
    val currentChance = session.passiveLootFindChancePercent()
    val nextChance = session.copy(armoryForgeLevel = nextLevel).passiveLootFindChancePercent()
    return if (state.maxed) {
        stringResource(R.string.armory_forge_upgrade_maxed, currentChance)
    } else {
        stringResource(R.string.armory_forge_upgrade_preview, currentChance, nextChance)
    }
}

@Composable
internal fun tavernKitchenUpgradePreviewText(session: PlaySessionState): String {
    val state = session.facilityUpgradeState(GuildFacility.TavernKitchen)
    val nextLevel = (session.tavernKitchenLevel + 1).coerceAtMost(state.definition.maxLevel)
    val currentBonusMinutes = (session.passiveIncomeCapBonusSeconds() / 60L).toInt()
    val nextBonusMinutes = (session.copy(tavernKitchenLevel = nextLevel).passiveIncomeCapBonusSeconds() / 60L).toInt()
    return if (state.maxed) {
        stringResource(R.string.tavern_kitchen_upgrade_maxed, currentBonusMinutes)
    } else {
        stringResource(R.string.tavern_kitchen_upgrade_preview, currentBonusMinutes, nextBonusMinutes)
    }
}

@Composable
internal fun infirmaryUpgradePreviewText(session: PlaySessionState): String {
    val state = session.facilityUpgradeState(GuildFacility.Infirmary)
    val nextLevel = (session.infirmaryLevel + 1).coerceAtMost(state.definition.maxLevel)
    val currentRecovery = session.infirmaryFailureRecoveryBonusPercent()
    val nextRecovery = session.copy(infirmaryLevel = nextLevel).infirmaryFailureRecoveryBonusPercent()
    val currentSafePower = session.infirmarySafePlanPowerBonus(ExpeditionPlanCatalog.safetyFirstId)
    val nextSafePower = session.copy(infirmaryLevel = nextLevel).infirmarySafePlanPowerBonus(ExpeditionPlanCatalog.safetyFirstId)
    return if (state.maxed) {
        stringResource(R.string.infirmary_upgrade_maxed, currentRecovery, currentSafePower)
    } else {
        stringResource(R.string.infirmary_upgrade_preview, currentRecovery, currentSafePower, nextRecovery, nextSafePower)
    }
}

@Composable
internal fun accountantOfficeUpgradePreviewText(session: PlaySessionState): String {
    val state = session.facilityUpgradeState(GuildFacility.AccountantOffice)
    val nextLevel = (session.accountantOfficeLevel + 1).coerceAtMost(state.definition.maxLevel)
    val currentGold = session.accountantOfficeQuestGoldBonusPercent(SeedGame.firstQuest)
    val nextGold = session.copy(accountantOfficeLevel = nextLevel).accountantOfficeQuestGoldBonusPercent(SeedGame.firstQuest)
    val currentDuplicate = session.accountantOfficeDuplicateReputationBonus()
    val nextDuplicate = session.copy(accountantOfficeLevel = nextLevel).accountantOfficeDuplicateReputationBonus()
    return if (state.maxed) {
        stringResource(R.string.accountant_office_upgrade_maxed, currentGold, currentDuplicate)
    } else {
        stringResource(R.string.accountant_office_upgrade_preview, currentGold, currentDuplicate, nextGold, nextDuplicate)
    }
}

@Composable
internal fun trainingYardUpgradePreviewText(session: PlaySessionState): String {
    val currentPower = session.trainingYardPowerBonus()
    val currentXpBonus = session.trainingYardQuestXpBonusPercent()
    return if (session.facilityUpgradeState(GuildFacility.TrainingYard).maxed) {
        stringResource(R.string.training_yard_upgrade_maxed, currentPower, currentXpBonus)
    } else {
        stringResource(
            R.string.training_yard_upgrade_preview,
            currentPower,
            currentXpBonus,
            currentPower + 8,
            currentXpBonus + 10,
        )
    }
}

@Composable
internal fun upgradePreviewText(
    currentValue: Int,
    nextValue: Int,
    maxed: Boolean,
    currentFormat: Int,
    maxedFormat: Int,
): String =
    if (maxed) {
        stringResource(maxedFormat, currentValue)
    } else {
        stringResource(currentFormat, currentValue, nextValue)
    }
