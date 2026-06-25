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
internal fun QuestsScreen(
    session: PlaySessionState,
    selectedQuest: Quest,
    onSelectQuest: (Quest) -> Unit,
    onPrepare: (Quest) -> Unit,
    onParty: () -> Unit,
) {
    val canPrepare = session.phase == PlayPhase.Idle
    val prepareLabel = if (canPrepare) stringResource(R.string.prep_action) else stringResource(R.string.quest_blocked_action)

    ScreenScaffold(title = stringResource(R.string.quests_title), status = phaseStatus(session.phase)) {
        SeedGame.quests.forEach { quest ->
            val selected = quest.id == selectedQuest.id
            val unlocked = session.isQuestUnlocked(quest)
            val primaryLabel = when {
                !unlocked -> stringResource(R.string.quest_locked_action)
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
                    secondaryLabel = stringResource(R.string.party_action),
                    onPrimary = {
                        onSelectQuest(quest)
                        if (unlocked) onPrepare(quest)
                    },
                    onSecondary = onParty,
                    primaryEnabled = canPrepare && unlocked,
                )
            }
        }
    }
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
    onParty: () -> Unit,
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
    val canLaunch = session.phase == PlayPhase.Idle && partyHeroes.isNotEmpty() && unlocked
    val launchLabel = when {
        !unlocked -> stringResource(R.string.quest_locked_action)
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
                secondaryLabel = stringResource(R.string.party_action),
                onPrimary = onLaunch,
                onSecondary = onParty,
                primaryEnabled = canLaunch,
            )
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
    onClick: () -> Unit,
) {
    val borderColor = if (selected) Color(0xFFD0A24A) else Color(0x66806C3A)
    val background = if (selected) Color(0xFFFFE8A6) else Color(0xFFFFF6CD)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 7.dp)
            .clickable(onClick = onClick),
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
            }
            Text(
                text = if (selected) stringResource(R.string.prep_plan_selected_value) else stringResource(R.string.prep_roster_pick_value),
                color = Color(0xFF211F1A),
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                textAlign = TextAlign.End,
            )
        }
    }
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
