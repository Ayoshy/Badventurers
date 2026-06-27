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
internal fun LootScreen(
    session: PlaySessionState,
    onSellInventory: (LootItem) -> Unit,
    onEquip: (String, LootItem) -> Unit,
    onReroll: (LootItem) -> Unit,
) {
    val inventory = session.lootItems.asReversed()
    var selectedItem by remember { mutableStateOf<LootItem?>(inventory.firstOrNull()) }
    LaunchedEffect(inventory) {
        if (selectedItem == null || selectedItem !in inventory) {
            selectedItem = inventory.firstOrNull()
        }
    }
    val status = when {
        inventory.isEmpty() -> stringResource(R.string.new_loot_status)
        else -> stringResource(R.string.loot_inventory_status, inventory.size)
    }

    ScreenScaffold(title = stringResource(R.string.loot_title), status = status) {
        if (selectedItem == null) {
            DarkPanel(title = stringResource(R.string.loot_empty_title), body = stringResource(R.string.loot_empty_summary))
            return@ScreenScaffold
        }

        val currentItem = selectedItem!!
        val selectedItemName = lootItemName(currentItem)
        val sellValue = LootEconomy.sellValue(currentItem)
        val rerollCost = session.lootRerollCost(currentItem)
        val canReroll = session.canRerollLoot(currentItem)
        val rerollDetail = if (session.armoryForgeLevel > 0) {
            stringResource(R.string.loot_reroll_detail)
        } else {
            stringResource(R.string.loot_reroll_detail_locked)
        }
        val suggestedTarget = bestEquipSuggestion(session, currentItem)
        val equipTargetDetail = suggestedTarget?.let {
            stringResource(
                R.string.equip_target_detail,
                it.hero.name,
                heroClassLabel(it.hero.heroClass),
                formatSignedCount(it.gain),
            )
        } ?: stringResource(R.string.equip_target_missing)

        LootIconPanel(item = currentItem, contentDescription = selectedItemName)
        InfoRow(
            title = stringResource(R.string.sell_value_title),
            detail = stringResource(R.string.sell_value_detail),
            value = stringResource(R.string.gold_value, sellValue),
        )
        InfoRow(
            title = stringResource(R.string.equip_target_title),
            detail = equipTargetDetail,
            value = suggestedTarget?.let { formatSignedCount(it.gain) } ?: "-",
        )
        InfoRow(
            title = stringResource(R.string.loot_reroll_title),
            detail = rerollDetail,
            value = if (session.armoryForgeLevel > 0) stringResource(R.string.gold_value, rerollCost) else "-",
        )
        DarkPanel(
            title = selectedItemName,
            body = lootKeepSellSummary(currentItem, inventory.size, sellValue),
        ) {
            ActionRow(
                primaryLabel = stringResource(R.string.keep_action),
                secondaryLabel = stringResource(R.string.sell_action),
                onPrimary = {},
                onSecondary = { onSellInventory(currentItem) },
            )
            Button(
                onClick = {
                    val target = suggestedTarget ?: return@Button
                    onEquip(target.hero.id, currentItem)
                },
                enabled = suggestedTarget != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2F695C),
                    contentColor = Color(0xFFF8F1D8),
                    disabledContainerColor = Color(0xFF6B5E3C),
                    disabledContentColor = Color(0xFFFFF1C0),
                ),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(stringResource(R.string.equip_action), fontWeight = FontWeight.Black)
            }
            Button(
                onClick = { onReroll(currentItem) },
                enabled = canReroll,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8B6230),
                    contentColor = Color(0xFFF8F1D8),
                    disabledContainerColor = Color(0xFF6B5E3C),
                    disabledContentColor = Color(0xFFFFF1C0),
                ),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(stringResource(R.string.reroll_action), fontWeight = FontWeight.Black)
            }
        }

        Text(
            text = stringResource(R.string.loot_inventory_title),
            color = Color(0xFFFFF0BD),
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(top = 2.dp, bottom = 8.dp),
        )
        inventory.forEach { item ->
            LootInventoryRow(
                item = item,
                selected = item == selectedItem,
                onClick = { selectedItem = item },
            )
        }
    }
}
@Composable
internal fun LootInventoryRow(item: LootItem, selected: Boolean, onClick: () -> Unit) {
    val borderStyle = lootBorderStyle(item.rarity)
    val shape = RoundedCornerShape(8.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clip(shape)
            .clickable(onClick = onClick),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) borderStyle.selectedSurfaceColor else borderStyle.surfaceColor,
        ),
        border = BorderStroke(if (selected) borderStyle.strokeWidth + 1.dp else borderStyle.strokeWidth, borderStyle.borderColor),
    ) {
        Row(
            modifier = Modifier.padding(9.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(lootArtResource(item)),
                    contentDescription = lootItemName(item),
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(Color(0x66171813))
                        .border(1.dp, borderStyle.innerColor, RoundedCornerShape(7.dp))
                        .padding(4.dp),
                    contentScale = ContentScale.Crop,
                )
                Spacer(Modifier.width(9.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = lootItemName(item), color = Color(0xFF211F1A), fontWeight = FontWeight.Black, maxLines = 1)
                    Text(
                        text = "${lootRarityLabel(item.rarity)} - ${lootSlotLabel(item.slot)}",
                        color = Color(0xFF756B54),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "+${item.bonus}", color = Color(0xFF211F1A), fontWeight = FontWeight.Black)
                if (item.isPerfect) {
                    Spacer(Modifier.height(4.dp))
                    PerfectLootBadge()
                }
            }
        }
    }
}

@Composable
internal fun PerfectLootBadge() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(Color(0xFFE3C15E))
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(
            text = stringResource(R.string.loot_perfect_label),
            color = Color(0xFF211F1A),
            fontSize = 9.sp,
            fontWeight = FontWeight.Black,
            maxLines = 1,
        )
    }
}
