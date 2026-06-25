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

@Composable
internal fun GuildScreen(
    session: PlaySessionState,
    selectedQuest: Quest,
    nowMillis: Long,
    onViewResult: () -> Unit,
    onNextQuest: () -> Unit,
    onAchievements: () -> Unit,
    onToggleCoreCrew: (String) -> Unit,
    onFinishQuestNow: () -> Unit,
    onResetProgress: () -> Unit,
) {
    ScreenScaffold(title = stringResource(R.string.guild_home_title), status = phaseStatus(session.phase)) {
        when (session.phase) {
            PlayPhase.Idle -> {
                DarkPanel(title = stringResource(R.string.idle_quest_title), body = stringResource(R.string.idle_quest_summary)) {
                    ActionRow(
                        primaryLabel = stringResource(R.string.next_quest_action),
                        secondaryLabel = stringResource(R.string.party_action),
                        onPrimary = onNextQuest,
                        onSecondary = {},
                    )
                }
            }
            PlayPhase.Running -> {
                val secondsLeft = remainingSeconds(session, nowMillis)
                DarkPanel(
                    title = stringResource(R.string.running_quest_title),
                    body = stringResource(R.string.running_quest_summary, secondsLeft),
                ) {
                    ProgressBar(progress = session.progress(nowMillis).toFloat())
                    if (booleanResource(R.bool.debug_tools_enabled)) {
                        Button(
                            onClick = onFinishQuestNow,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B5531)),
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Text(stringResource(R.string.instant_quest_action), fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
            PlayPhase.ResultReady -> {
                val result = session.expedition?.result
                if (result != null) {
                    DarkPanel(
                        title = stringResource(R.string.result_quest_title),
                        body = stringResource(
                            R.string.result_quest_summary,
                            outcomeLabel(result.outcome),
                            rewardGoldWithNoticeBoard(session),
                            session.collectableLootRolls(result),
                        ),
                    ) {
                        ActionRow(
                            primaryLabel = stringResource(R.string.view_report_action),
                            secondaryLabel = stringResource(R.string.next_quest_action),
                            onPrimary = onViewResult,
                            onSecondary = onNextQuest,
                        )
                    }
                }
            }
        }
        CoreCrewPanel(session = session, onToggleCoreCrew = onToggleCoreCrew)
        GuildFacilitiesPanel(session, selectedQuest)
        TierGoalPanel(session = session)
        AchievementLedgerPanel(session = session, onOpen = onAchievements)
        ProgressionAdvicePanel(session = session, selectedQuest = selectedQuest)
        if (booleanResource(R.bool.debug_tools_enabled)) {
            TestProgressPanel(onResetProgress = onResetProgress)
        }
        val journalLines = session.journalEntries.takeLast(5).map { journalEntryText(it) }
        if (journalLines.isEmpty()) {
            JournalLine(stringResource(R.string.journal_01))
            JournalLine(stringResource(R.string.journal_02))
            JournalLine(stringResource(R.string.journal_03))
        } else {
            journalLines.forEach { line -> JournalLine(line) }
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
