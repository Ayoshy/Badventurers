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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import com.ayoshy.badventurers.game.ScoutPlanWarning
import com.ayoshy.badventurers.game.ScoutPlanWarningType
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
internal fun QuestsScreen(
    session: PlaySessionState,
    selectedQuest: Quest,
    onSelectQuest: (Quest) -> Unit,
    onPrepare: (Quest, ExpeditionPlan?) -> Unit,
) {
    val canPrepare = session.phase == PlayPhase.Idle
    val prepareLabel = if (canPrepare) stringResource(R.string.prep_action) else stringResource(R.string.quest_blocked_action)
    var selectedRegion by rememberSaveable { mutableStateOf(QuestActivityRegion.LocalJobs.name) }
    val activityRegion = QuestActivityRegion.valueOf(selectedRegion)
    val visibleQuests = SeedGame.quests.filter { quest -> quest.matchesActivityRegion(activityRegion) }

    ScreenScaffold(title = stringResource(R.string.quests_title), status = phaseStatus(session.phase)) {
        QuestActivityRegionSelector(
            selectedRegion = activityRegion,
            onSelectRegion = { region -> selectedRegion = region.name },
        )
        QuestActivitySummaryPanel(region = activityRegion, session = session, questCount = visibleQuests.size)
        visibleQuests.forEach { quest ->
            QuestActivityCard(
                session = session,
                quest = quest,
                selected = quest.id == selectedQuest.id,
                region = activityRegion,
                canPrepare = canPrepare,
                prepareLabel = prepareLabel,
                onSelectQuest = onSelectQuest,
                onPrepare = onPrepare,
            )
        }
    }
}


@Composable
private fun QuestActivityRegionSelector(
    selectedRegion: QuestActivityRegion,
    onSelectRegion: (QuestActivityRegion) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        QuestActivityRegion.values().forEach { region ->
            val selected = region == selectedRegion
            Button(
                onClick = { onSelectRegion(region) },
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selected) Color(0xFFD0A24A) else Color(0xFF2F2B22),
                    contentColor = if (selected) Color(0xFF211F1A) else Color(0xFFFFF1C0),
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
            ) {
                Text(
                    text = questActivityRegionTitle(region),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    lineHeight = 12.sp,
                )
            }
        }
    }
}

@Composable
private fun QuestActivitySummaryPanel(region: QuestActivityRegion, session: PlaySessionState, questCount: Int) {
    val body = when (region) {
        QuestActivityRegion.LocalJobs -> stringResource(R.string.quest_activity_local_summary)
        QuestActivityRegion.DangerousWork -> stringResource(R.string.quest_activity_hard_summary)
        QuestActivityRegion.SpecialContracts -> stringResource(
            R.string.quest_activity_contracts_summary,
            session.specialContracts,
        )
    }
    val stockValue = when (region) {
        QuestActivityRegion.LocalJobs -> stringResource(R.string.quest_activity_free_value)
        QuestActivityRegion.DangerousWork -> stringResource(R.string.quest_activity_hard_value)
        QuestActivityRegion.SpecialContracts -> stringResource(
            R.string.quest_activity_contract_stock_value,
            session.specialContracts,
        )
    }
    InfoRow(
        title = questActivityRegionTitle(region),
        detail = body,
        value = if (region == QuestActivityRegion.SpecialContracts) {
            stockValue
        } else {
            stringResource(R.string.quest_activity_count_value, questCount)
        },
    )
}

@Composable
private fun QuestActivityCard(
    session: PlaySessionState,
    quest: Quest,
    selected: Boolean,
    region: QuestActivityRegion,
    canPrepare: Boolean,
    prepareLabel: String,
    onSelectQuest: (Quest) -> Unit,
    onPrepare: (Quest, ExpeditionPlan?) -> Unit,
) {
    val unlocked = session.isQuestUnlocked(quest)
    val specialPlan = if (region == QuestActivityRegion.SpecialContracts) quest.specialContractPlan() else null
    val specialContractReady = specialPlan == null || session.specialContracts >= specialPlan.specialContractCost
    val primaryEnabled = canPrepare && unlocked && specialContractReady
    val primaryLabel = when {
        !unlocked -> stringResource(R.string.quest_locked_action)
        specialPlan != null && !specialContractReady -> stringResource(R.string.special_contract_need_action)
        specialPlan != null -> stringResource(R.string.quest_special_contract_prepare_action)
        selected -> prepareLabel
        else -> stringResource(R.string.quest_select_action)
    }

    QuestCardArt(
        bannerResourceId = questBannerResource(quest),
        frameResourceId = questFrameResource(quest.difficulty),
        borderStyle = questDifficultyBorderStyle(quest.difficulty),
    )
    DarkPanel(title = questTitle(quest), body = questSummary(quest)) {
        Text(
            text = "${questDifficultyLabel(quest.difficultyTier)} - ${questTagsLabel(quest.tags)}",
            color = Color(0xFFDED0A2),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        QuestIntentBadges(session = session, quest = quest, region = region)
        QuestRewardSummary(session = session, quest = quest)
        if (specialPlan != null) {
            Text(
                text = stringResource(R.string.quest_special_contract_clause_detail, expeditionPlanTitle(specialPlan)),
                color = Color(0xFFFFE08A),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(bottom = 4.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = if (specialContractReady) {
                    stringResource(R.string.quest_special_contract_ready_detail, session.specialContracts)
                } else {
                    stringResource(R.string.quest_special_contract_missing_detail, session.specialContracts)
                },
                color = if (specialContractReady) Color(0xFFFFE08A) else Color(0xFFFF9B79),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }
        RecommendedHeroesInline(session = session, quest = quest)
        if (!unlocked) {
            Text(
                text = questUnlockDetail(session, quest),
                color = Color(0xFFFFD27D),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }
        ActionRow(
            primaryLabel = primaryLabel,
            onPrimary = {
                onSelectQuest(quest)
                if (unlocked) onPrepare(quest, specialPlan)
            },
            primaryEnabled = primaryEnabled,
        )
    }
}

@Composable
private fun questActivityRegionTitle(region: QuestActivityRegion): String =
    when (region) {
        QuestActivityRegion.LocalJobs -> stringResource(R.string.quest_activity_local_title)
        QuestActivityRegion.DangerousWork -> stringResource(R.string.quest_activity_hard_title)
        QuestActivityRegion.SpecialContracts -> stringResource(R.string.quest_activity_contracts_title)
    }

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuestIntentBadges(session: PlaySessionState, quest: Quest, region: QuestActivityRegion) {
    val badges = questActivityIntentBadges(session, quest, region)
    if (badges.isEmpty()) return

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        badges.forEach { intent ->
            Text(
                text = questActivityIntentLabel(intent),
                color = Color(0xFF211F1A),
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(questActivityIntentColor(intent))
                    .padding(horizontal = 7.dp, vertical = 3.dp),
            )
        }
    }
}

@Composable
private fun questActivityIntentLabel(intent: QuestActivityIntent): String = when (intent) {
    QuestActivityIntent.RecommendedNext -> stringResource(R.string.quest_intent_recommended_next)
    QuestActivityIntent.FirstClear -> stringResource(R.string.quest_intent_first_clear)
    QuestActivityIntent.Contract -> stringResource(R.string.quest_intent_contract)
    QuestActivityIntent.Gold -> stringResource(R.string.quest_intent_gold)
    QuestActivityIntent.Loot -> stringResource(R.string.quest_intent_loot)
    QuestActivityIntent.Xp -> stringResource(R.string.quest_intent_xp)
    QuestActivityIntent.Reputation -> stringResource(R.string.quest_intent_reputation)
}

private fun questActivityIntentColor(intent: QuestActivityIntent): Color = when (intent) {
    QuestActivityIntent.RecommendedNext -> Color(0xFFD0A24A)
    QuestActivityIntent.FirstClear -> Color(0xFFFFD27D)
    QuestActivityIntent.Contract -> Color(0xFFFFE08A)
    QuestActivityIntent.Gold -> Color(0xFFE4B857)
    QuestActivityIntent.Loot -> Color(0xFFA6D38E)
    QuestActivityIntent.Xp -> Color(0xFF9EC6E8)
    QuestActivityIntent.Reputation -> Color(0xFFFF9B79)
}

@Composable
private fun QuestRewardSummary(session: PlaySessionState, quest: Quest) {
    val firstClear = firstClearRewardPreview(session, quest)
    if (firstClear.state != FirstClearRewardState.None) {
        Text(
            text = when (firstClear.state) {
                FirstClearRewardState.Available -> stringResource(
                    R.string.quest_first_clear_reward_detail,
                    firstClearTicketRewardText(firstClear.ticketRewards),
                )
                FirstClearRewardState.Claimed -> stringResource(R.string.quest_first_clear_claimed_detail)
                FirstClearRewardState.None -> ""
            },
            color = if (firstClear.state == FirstClearRewardState.Available) Color(0xFFFFE08A) else Color(0xFFB9AA82),
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(bottom = 4.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
    Text(
        text = stringResource(R.string.quest_repeat_reward_detail, quest.baseGold),
        color = Color(0xFFDED0A2),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
internal fun firstClearTicketRewardText(rewards: Map<String, Int>): String {
    val labels = mutableListOf<String>()
    for ((ticketId, count) in rewards) {
        val title = recruitmentTicketTitle(ticketId)
        labels += if (count > 1) stringResource(R.string.quest_first_clear_ticket_stack, title, count) else title
    }
    return labels.joinToString()
}


@Composable
internal fun ExpeditionPrepScreen(
    session: PlaySessionState,
    quest: Quest,
    selectedPartyIds: List<String>,
    selectedPlanId: String,
    onToggleHero: (String) -> Unit,
    onSelectPlan: (ExpeditionPlan) -> Unit,
    onLaunch: () -> Unit,
) {
    val partySlots = session.effectivePartySlots(quest)
    val partyHeroes = selectedPartyIds.mapNotNull { heroId -> session.heroes.firstOrNull { it.id == heroId } }.take(partySlots)
    val availablePlans = ExpeditionPlanCatalog.availableFor(quest)
    val selectedPlan = ExpeditionPlanCatalog.selectedPlanForUi(selectedPlanId, quest)
    val heroRecommendations = HeroRecommendationScorer.rankHeroes(
        roster = session.heroes,
        quest = quest,
        selectedParty = partyHeroes,
        equipment = session.equippedLoot,
        facilityPowerBonus = session.trainingYardPowerBonus(),
        partySlots = partySlots,
    )
    val heroRecommendationById = heroRecommendations.associateBy { it.hero.id }
    val topRecommendedHeroIds = heroRecommendations
        .take(maxOf(3, partySlots))
        .map { it.hero.id }
        .toSet()
    val estimate = ExpeditionEstimator.estimate(
        party = partyHeroes,
        quest = quest,
        equipment = session.equippedLoot,
        facilityPowerBonus = session.trainingYardPowerBonus(),
        planId = selectedPlan.id,
    )
    val activeSpecials = HeroSpecialCatalog.activeHeroes(partyHeroes, quest)
    val unlocked = session.isQuestUnlocked(quest)
    val selectedPlanCost = selectedPlan.specialContractCost
    val selectedPlanAffordable = session.canAffordPlan(selectedPlan.id, quest)
    val selectedPlanRequiresContract = selectedPlanCost > 0
    val canLaunch = session.phase == PlayPhase.Idle && partyHeroes.isNotEmpty() && unlocked && selectedPlanAffordable
    val launchLabel = when {
        !unlocked -> stringResource(R.string.quest_locked_action)
        selectedPlanRequiresContract && !selectedPlanAffordable -> stringResource(R.string.special_contract_need_action)
        canLaunch && selectedPlanRequiresContract -> stringResource(R.string.launch_special_contract_action)
        canLaunch -> stringResource(R.string.launch_quest_action)
        else -> stringResource(R.string.quest_blocked_action)
    }
    val previewGold = questBaseGoldWithNoticeBoard(session, quest, estimate.rewardGoldBonusPercent)

    ScreenScaffold(title = stringResource(R.string.prep_title), status = stringResource(R.string.prep_status)) {
        QuestCardArt(
            bannerResourceId = questBannerResource(quest),
            frameResourceId = questFrameResource(quest.difficulty),
            borderStyle = questDifficultyBorderStyle(quest.difficulty),
        )
        DarkPanel(title = questTitle(quest), body = questSummary(quest)) {
            if (!unlocked) {
                Text(
                    text = questUnlockDetail(session, quest),
                    color = Color(0xFFFFD27D),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }
            ActionRow(
                primaryLabel = launchLabel,
                onPrimary = onLaunch,
                primaryEnabled = canLaunch,
            )
            if (selectedPlanRequiresContract) {
                Text(
                    text = if (selectedPlanAffordable) {
                        stringResource(R.string.prep_special_contract_ready_detail, session.specialContracts)
                    } else {
                        stringResource(R.string.prep_special_contract_missing_detail, session.specialContracts)
                    },
                    color = if (selectedPlanAffordable) Color(0xFFFFE08A) else Color(0xFFFF9B79),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
        RecommendedHeroesPanel(
            session = session,
            quest = quest,
            selectedPartyIds = selectedPartyIds,
            recommendations = heroRecommendations,
        )
        ExpeditionPlanPanel(
            quest = quest,
            selectedPlan = selectedPlan,
            availablePlans = availablePlans,
            scoutTableLevel = session.scoutTableLevel,
            specialContracts = session.specialContracts,
            onSelectPlan = onSelectPlan,
        )
        InfoRow(
            title = stringResource(R.string.prep_success_title),
            detail = stringResource(R.string.prep_success_detail, estimate.partyPower, estimate.targetPower),
            value = stringResource(R.string.prep_success_value, estimate.successChancePercent),
        )
        InfoRow(
            title = stringResource(R.string.prep_risk_title),
            detail = stringResource(R.string.prep_risk_detail, riskLabel(quest.risk), estimate.durationSeconds, estimate.riskPenalty),
            value = stringResource(R.string.prep_target_value, estimate.targetPower),
        )
        InfoRow(
            title = stringResource(R.string.prep_difficulty_title),
            detail = questTagsLabel(quest.tags),
            value = questDifficultyLabel(quest.difficultyTier),
        )
        InfoRow(
            title = stringResource(R.string.prep_special_title),
            detail = specialEstimateDetail(activeSpecials, estimate),
            value = if (estimate.specialPowerBonus == 0) "-" else formatSignedCount(estimate.specialPowerBonus),
        )
        InfoRow(
            title = stringResource(R.string.prep_reward_title),
            detail = stringResource(R.string.prep_reward_detail, quest.pityGold, 3 + estimate.rewardBonusLootRolls),
            value = stringResource(R.string.gold_value, previewGold),
        )
        Text(
            text = stringResource(R.string.prep_party_title, partyHeroes.size, partySlots),
            color = Color(0xFFFFF0BD),
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(top = 2.dp, bottom = 8.dp),
        )
        repeat(partySlots) { index ->
            PartySlotRow(
                slotNumber = index + 1,
                hero = partyHeroes.getOrNull(index),
                session = session,
            )
        }
        Text(
            text = stringResource(R.string.prep_roster_title),
            color = Color(0xFFFFF0BD),
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
        )
        session.heroes
            .sortedWith(
                compareByDescending<Hero> { it.id in selectedPartyIds }
                    .thenByDescending { hero -> heroRecommendationById[hero.id]?.score ?: 0 }
                    .thenByDescending { hero -> PartyPowerCalculator.basePower(hero) + session.equipmentBonus(hero.id) }
                    .thenBy { it.name },
            )
            .forEach { hero ->
                val selected = hero.id in selectedPartyIds
                val recommendation = heroRecommendationById[hero.id]
                val recommended = hero.id in topRecommendedHeroIds
                HeroPartyChoiceRow(
                    hero = hero,
                    selected = selected,
                    recommended = recommended,
                    recommendation = recommendation,
                    enabled = selected || partyHeroes.size < partySlots,
                    session = session,
                    onClick = { onToggleHero(hero.id) },
                )
            }
    }
}
@Composable
internal fun RecommendedHeroesInline(session: PlaySessionState, quest: Quest) {
    val names = recommendedHeroNames(session, quest, selectedParty = emptyList())
    if (names.isBlank()) return

    Text(
        text = stringResource(R.string.quest_recommended_heroes_detail, names),
        color = Color(0xFFFFE08A),
        fontSize = 12.sp,
        fontWeight = FontWeight.Black,
        modifier = Modifier.padding(bottom = 8.dp),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
internal fun RecommendedHeroesPanel(
    session: PlaySessionState,
    quest: Quest,
    selectedPartyIds: List<String>,
    recommendations: List<HeroRecommendation>,
) {
    val topRecommendations = recommendations.take(maxOf(3, session.effectivePartySlots(quest)))
    val topRecommendedIds = topRecommendations.map { it.hero.id }.toSet()
    val selectedRecommended = selectedPartyIds.count { it in topRecommendedIds }
    val names = recommendationNames(topRecommendations)
    if (names.isBlank()) return

    PaperPanel(
        title = stringResource(R.string.prep_recommended_heroes_title),
        body = stringResource(
            R.string.prep_recommended_heroes_detail,
            selectedRecommended,
            topRecommendedIds.size,
            names,
        ),
    )
}

internal fun recommendedHeroNames(
    session: PlaySessionState,
    quest: Quest,
    selectedParty: List<Hero>,
): String =
    recommendationNames(
        HeroRecommendationScorer.rankHeroes(
            roster = session.heroes,
            quest = quest,
            selectedParty = selectedParty,
            equipment = session.equippedLoot,
            facilityPowerBonus = session.trainingYardPowerBonus(),
            partySlots = session.effectivePartySlots(quest),
        ).take(maxOf(3, session.effectivePartySlots(quest))),
    )

internal fun recommendationNames(recommendations: List<HeroRecommendation>): String =
    recommendations.joinToString { it.hero.name }

@Composable
internal fun ExpeditionPlanPanel(
    quest: Quest,
    selectedPlan: ExpeditionPlan,
    availablePlans: List<ExpeditionPlan>,
    scoutTableLevel: Int,
    specialContracts: Int,
    onSelectPlan: (ExpeditionPlan) -> Unit,
) {
    PaperPanel(
        title = stringResource(R.string.prep_plan_panel_title),
        body = stringResource(R.string.prep_plan_panel_body),
    ) {
        Spacer(Modifier.height(8.dp))
        availablePlans.forEach { plan ->
            ExpeditionPlanChoiceRow(
                quest = quest,
                plan = plan,
                selected = plan.id == selectedPlan.id,
                scoutTableLevel = scoutTableLevel,
                specialContracts = specialContracts,
                onClick = { onSelectPlan(plan) },
            )
        }
    }
}

@Composable
internal fun ExpeditionPlanChoiceRow(
    quest: Quest,
    plan: ExpeditionPlan,
    selected: Boolean,
    scoutTableLevel: Int,
    specialContracts: Int,
    onClick: () -> Unit,
) {
    val scoutWarnings = ScoutTableIntel.planWarningsFor(scoutTableLevel, plan, quest)
    val scoutWarningLabels = mutableListOf<String>()
    for (warning in scoutWarnings) {
        scoutWarningLabels += scoutPlanWarningText(warning)
    }
    val scoutWarningText = scoutWarningLabels.joinToString(" / ")
    val requiresContract = plan.requiresSpecialContract
    val canAffordContract = !requiresContract || specialContracts >= plan.specialContractCost
    val borderColor = when {
        selected -> Color(0xFFD0A24A)
        requiresContract && !canAffordContract -> Color(0xFF9C5137)
        else -> Color(0x66806C3A)
    }
    val background = when {
        selected -> Color(0xFFFFE8A6)
        requiresContract && !canAffordContract -> Color(0xFFFFE0D2)
        else -> Color(0xFFFFF6CD)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 7.dp)
            .clickable(enabled = canAffordContract, onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = background),
    ) {
        Row(
            modifier = Modifier.padding(9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expeditionPlanTitle(plan),
                    color = Color(0xFF211F1A),
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = expeditionPlanSummary(plan),
                    color = Color(0xFF756B54),
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = expeditionPlanEffectSummary(plan, quest),
                    color = Color(0xFF5B4E2F),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (requiresContract) {
                    Text(
                        text = if (canAffordContract) {
                            stringResource(R.string.special_contract_row_ready, specialContracts)
                        } else {
                            stringResource(R.string.special_contract_row_locked, specialContracts)
                        },
                        color = if (canAffordContract) Color(0xFF5B4E2F) else Color(0xFF8B3F24),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (scoutWarnings.isNotEmpty()) {
                    Text(
                        text = scoutWarningText,
                        color = Color(0xFF8B3F24),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Text(
                text = when {
                    requiresContract && !canAffordContract -> stringResource(R.string.special_contract_need_short)
                    requiresContract -> stringResource(R.string.special_contract_xxl_value)
                    selected -> stringResource(R.string.prep_plan_selected_value)
                    else -> stringResource(R.string.prep_roster_pick_value)
                },
                color = Color(0xFF211F1A),
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                textAlign = TextAlign.End,
            )
        }
    }
}

@Composable
internal fun scoutPlanWarningText(warning: ScoutPlanWarning): String = when (warning.type) {
    ScoutPlanWarningType.HigherRisk -> stringResource(R.string.prep_plan_scout_warning_risk, warning.amount)
    ScoutPlanWarningType.LowerPower -> stringResource(R.string.prep_plan_scout_warning_power, warning.amount)
    ScoutPlanWarningType.LongerDuration -> stringResource(R.string.prep_plan_scout_warning_duration, warning.amount)
    ScoutPlanWarningType.HarderGreatSuccess -> stringResource(R.string.prep_plan_scout_warning_great_success, warning.amount)
    ScoutPlanWarningType.LowerGold -> stringResource(R.string.prep_plan_scout_warning_gold, warning.amount)
}
@Composable
internal fun PartySlotRow(slotNumber: Int, hero: Hero?, session: PlaySessionState) {
    InfoRow(
        title = hero?.name ?: stringResource(R.string.prep_empty_slot_title),
        detail = hero?.let {
            stringResource(
                R.string.prep_party_slot_detail,
                slotNumber,
                heroRarityLabel(it.rarity),
                heroClassLabel(it.heroClass),
            )
        } ?: stringResource(R.string.prep_empty_slot_detail, slotNumber),
        value = hero?.let {
            stringResource(R.string.hero_power_value, PartyPowerCalculator.basePower(it) + session.equipmentBonus(it.id))
        } ?: "-",
    )
}

@Composable
internal fun HeroPartyChoiceRow(
    hero: Hero,
    selected: Boolean,
    recommended: Boolean,
    recommendation: HeroRecommendation?,
    enabled: Boolean,
    session: PlaySessionState,
    onClick: () -> Unit,
) {
    val borderStyle = heroBorderStyle(hero.rarity)
    val shape = RoundedCornerShape(8.dp)
    val cardModifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp)
        .clip(shape)
        .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier)
    val value = when {
        selected -> stringResource(R.string.prep_roster_selected_value)
        recommended && recommendation != null && recommendation.successGainPercent > 0 ->
            stringResource(R.string.prep_roster_recommendation_gain_value, recommendation.successGainPercent)
        recommended -> stringResource(R.string.prep_roster_recommended_value)
        enabled -> stringResource(R.string.prep_roster_pick_value)
        else -> stringResource(R.string.prep_roster_full_value)
    }

    Card(
        modifier = cardModifier,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = when {
                selected -> borderStyle.selectedSurfaceColor
                recommended -> Color(0xFFFFF4C6)
                else -> Color(0xF4FFF1BF)
            },
        ),
        border = when {
            selected -> BorderStroke(borderStyle.strokeWidth, borderStyle.borderColor)
            recommended -> BorderStroke(2.dp, Color(0xFFD0A24A))
            else -> null
        },
    ) {
        Row(
            modifier = Modifier.padding(9.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = hero.name, color = Color(0xFF211F1A), fontWeight = FontWeight.Black, maxLines = 1)
                Text(
                    text = heroRosterRecommendationDetail(hero, if (recommended) recommendation else null),
                    color = if (enabled) Color(0xFF756B54) else Color(0x99756B54),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = value, color = Color(0xFF211F1A), fontWeight = FontWeight.Black, fontSize = 12.sp, maxLines = 1)
                Text(
                    text = stringResource(R.string.hero_power_value, PartyPowerCalculator.basePower(hero) + session.equipmentBonus(hero.id)),
                    color = Color(0xFF756B54),
                    fontSize = 12.sp,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
internal fun heroRosterRecommendationDetail(hero: Hero, recommendation: HeroRecommendation?): String {
    val base = heroRosterDetail(hero)
    if (recommendation == null) return base

    val reasons = listOfNotNull(
        if (recommendation.successGainPercent > 0) {
            stringResource(R.string.prep_recommendation_odds_reason, recommendation.successGainPercent)
        } else null,
        if (recommendation.activeSpecial) stringResource(R.string.prep_recommendation_special_reason) else null,
        if (recommendation.statAffinity >= 60) {
            stringResource(R.string.prep_recommendation_stats_reason, recommendation.matchingStats.size)
        } else null,
        if (recommendation.equipmentBonus > 0) {
            stringResource(R.string.prep_recommendation_gear_reason, recommendation.equipmentBonus)
        } else null,
    ).take(2)

    val detail = reasons.ifEmpty {
        listOf(stringResource(R.string.prep_recommendation_power_reason, recommendation.power))
    }.joinToString(" | ")

    return stringResource(R.string.prep_roster_recommendation_detail, base, detail)
}
