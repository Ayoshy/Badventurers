package com.ayoshy.badventurers.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

    ScreenScaffold(title = stringResource(R.string.upgrades_title), status = stringResource(R.string.guild_upgrade_status)) {
        InfoRow(
            title = stringResource(R.string.upgrade_treasury_title),
            detail = stringResource(R.string.upgrade_treasury_detail),
            value = stringResource(R.string.gold_value, session.gold),
        )
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
        DarkPanel(title = stringResource(R.string.next_unlock_title), body = stringResource(R.string.next_unlock_summary))
        AchievementLedgerPanel(session = session, onOpen = onAchievements)
    }
}

@Composable
internal fun AchievementsScreen(
    session: PlaySessionState,
    onClaim: (String) -> Unit,
    onClaimAll: () -> Unit,
    onBack: () -> Unit,
) {
    val claimable = session.claimableAchievementCount()
    val definitions = AchievementCatalog.definitions.sortedWith(
        compareByDescending<AchievementDefinition> { definition ->
            val progress = session.achievementProgressFor(definition)
            progress.isCompleted(definition) && !progress.isClaimed
        }.thenBy { it.category.ordinal }.thenBy { it.title },
    )
    val nextMilestone = session.nextAchievementMilestone()

    ScreenScaffold(
        title = stringResource(R.string.achievements_title),
        status = stringResource(R.string.achievements_status, session.achievementSeals()),
    ) {
        DarkPanel(
            title = stringResource(R.string.achievements_summary_title),
            body = stringResource(
                R.string.achievements_completed_summary,
                session.completedAchievementCount(),
                AchievementCatalog.definitions.size,
                claimable,
            ),
        ) {
            if (nextMilestone != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(
                        R.string.achievements_next_milestone_detail,
                        nextMilestone.sealsRequired,
                        nextMilestone.title,
                    ),
                    color = Color(0xFFDED0A2),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    text = nextMilestone.summary,
                    color = Color(0xFFBFAF7E),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = onClaimAll,
                    enabled = claimable > 0,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F695C)),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(stringResource(R.string.achievements_claim_all_action), fontWeight = FontWeight.Black)
                }
                Button(
                    onClick = onBack,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDEC777), contentColor = Color(0xFF211F1A)),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(stringResource(R.string.upgrades_title), fontWeight = FontWeight.Black)
                }
            }
        }

        ArtSheet(resourceId = R.drawable.achievement_unlock_banner, aspectRatio = 2f)
        ArtSheet(resourceId = R.drawable.achievement_badges_sheet, aspectRatio = 2.2f)

        definitions.forEach { definition ->
            AchievementRow(
                definition = definition,
                progress = session.achievementProgressFor(definition),
                onClaim = onClaim,
            )
        }
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
        colors = CardDefaults.cardColors(containerColor = if (claimable) Color(0xFFFFF1BF) else Color(0xEEF8E7B5)),
        border = BorderStroke(2.dp, if (claimable) Color(0xFFD0A24A) else Color(0xAA7B6A43)),
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (completed) Color(0xFF2F695C) else Color(0xFF7B6A43)),
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
                Text(
                    text = definition.title,
                    color = Color(0xFF211F1A),
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${achievementCategoryLabel(definition.category)} - ${definition.summary}",
                    color = Color(0xFF756B54),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(6.dp))
                ProgressBar(progress = progressFraction.coerceIn(0f, 1f))
                Text(
                    text = achievementRewardSummary(definition),
                    color = Color(0xFF493F2B),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (claimable) {
                Button(
                    onClick = { onClaim(definition.id) },
                    modifier = Modifier
                        .width(82.dp)
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F695C)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                ) {
                    Text(
                        text = stringResource(R.string.achievements_claim_action),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                    )
                }
            } else {
                Text(
                    text = statusText,
                    color = Color(0xFF211F1A),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(74.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
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

internal fun achievementCategoryLabel(category: AchievementCategory): String =
    when (category) {
        AchievementCategory.Quest -> "Quest"
        AchievementCategory.Guild -> "Guild"
        AchievementCategory.Hero -> "Hero"
        AchievementCategory.Loot -> "Loot"
        AchievementCategory.Result -> "Result"
        AchievementCategory.Idle -> "Idle"
        AchievementCategory.Secret -> "Secret"
    }

internal fun achievementRewardSummary(definition: AchievementDefinition): String {
    val reward = definition.reward
    val parts = mutableListOf("+${definition.sealReward} seals")
    fun addReward(item: com.ayoshy.badventurers.game.AchievementReward) {
        when (item) {
            is com.ayoshy.badventurers.game.AchievementReward.Currency -> {
                if (item.gold > 0) parts += "+${item.gold}g"
                if (item.reputation > 0) parts += "+${item.reputation} Rep"
                if (item.lootRolls > 0) parts += "+${item.lootRolls} loot"
                if (item.specialContracts > 0) parts += "+${item.specialContracts} contract"
            }
            is com.ayoshy.badventurers.game.AchievementReward.Tickets -> {
                item.tickets.forEach { (_, count) ->
                    if (count > 0) parts += "+${count} ticket"
                }
            }
            is com.ayoshy.badventurers.game.AchievementReward.Composite -> item.rewards.forEach(::addReward)
            com.ayoshy.badventurers.game.AchievementReward.None -> Unit
        }
    }
    addReward(reward)
    return parts.joinToString(" / ")
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
