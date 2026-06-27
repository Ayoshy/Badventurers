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
import com.ayoshy.badventurers.game.OfflineReportHighlights
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
internal fun QuestResultScreen(
    session: PlaySessionState,
    onCollect: () -> Unit,
    onCollectWithFakeAd: () -> Unit,
    onBack: () -> Unit,
) {
    val run = session.expedition
    val result = run?.result

    ScreenScaffold(title = stringResource(R.string.result_quest_title), status = stringResource(R.string.result_report_status)) {
        if (run == null || result == null) {
            DarkPanel(title = stringResource(R.string.result_missing_title), body = stringResource(R.string.result_missing_summary)) {
                Button(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2F695C),
                    contentColor = Color(0xFFF8F1D8),
                    disabledContainerColor = Color(0xFF6B5E3C),
                    disabledContentColor = Color(0xFFFFF1C0),
                ),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(stringResource(R.string.guild_home_title), fontWeight = FontWeight.Black)
                }
            }
            return@ScreenScaffold
        }

        val resultParty = run.partyHeroIds
            .mapNotNull { heroId -> session.heroes.firstOrNull { it.id == heroId } }
            .ifEmpty { session.heroes.take(session.effectivePartySlots(run.quest)) }
        val partyNames = resultParty.joinToString { it.name }
        val resultCauses = ResultCauseGenerator.generate(session, run, resultParty)
        val levelUpPreviews = resultParty
            .map { hero -> hero to HeroProgression.previewGrantXp(hero, session.collectableHeroXp(result, run.quest)) }
            .filter { (_, preview) -> preview.levelsGained > 0 }
        val postCollectSession = session.collectResult()
        val postCollectAdvice = ProgressionAdvisor.recommend(postCollectSession, selectedQuest = run.quest)
        val specialContractReward = session.collectableSpecialContracts(result, run.quest)
        val usedSpecialContract = ExpeditionPlanCatalog.requiresSpecialContract(run.planId, run.quest)
        val firstClearTicketRewards = if (
            run.quest.id !in session.clearedQuestIds &&
            (result.outcome == ExpeditionOutcome.GreatSuccess || result.outcome == ExpeditionOutcome.Success)
        ) {
            run.quest.firstClearTicketRewards.filterValues { count -> count > 0 }
        } else {
            emptyMap()
        }

        QuestCardArt(
            bannerResourceId = questBannerResource(run.quest),
            frameResourceId = questFrameResource(run.quest.difficulty),
            borderStyle = questDifficultyBorderStyle(run.quest.difficulty),
        )
        DarkPanel(
            title = outcomeLabel(result.outcome),
            body = resultIncidentText(run.planId, result.outcome),
        ) {
            ActionRow(
                primaryLabel = stringResource(R.string.collect_action),
                secondaryLabel = stringResource(R.string.guild_home_title),
                onPrimary = onCollect,
                onSecondary = onBack,
            )
        }
        val fakeReward = FakeRewardedAdService.rewardFor(session)
        if (booleanResource(R.bool.debug_tools_enabled) && fakeReward != null) {
            DarkPanel(
                title = stringResource(R.string.rewarded_ad_fake_title),
                body = stringResource(R.string.rewarded_ad_fake_summary, fakeReward.extraGold, fakeReward.extraLootRolls),
            ) {
                Button(
                    onClick = onCollectWithFakeAd,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F695C)),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(stringResource(R.string.rewarded_ad_fake_action), fontWeight = FontWeight.Black)
                }
            }
        }
        InfoRow(
            title = stringResource(R.string.result_outcome_title),
            detail = stringResource(R.string.result_outcome_detail, result.scoreMargin),
            value = outcomeLabel(result.outcome),
        )
        ResultCausesPanel(causes = resultCauses)

        if (usedSpecialContract || specialContractReward > 0) {
            InfoRow(
                title = stringResource(R.string.result_special_contract_payoff_title),
                detail = if (specialContractReward > 0) {
                    stringResource(R.string.result_special_contract_payoff_detail, specialContractReward)
                } else {
                    stringResource(R.string.result_special_contract_payoff_detail_empty)
                },
                value = stringResource(R.string.special_contract_xxl_value),
            )
        }
        if (firstClearTicketRewards.isNotEmpty()) {
            InfoRow(
                title = stringResource(R.string.result_first_clear_jackpot_title),
                detail = stringResource(
                    R.string.result_first_clear_jackpot_detail,
                    firstClearTicketRewardText(firstClearTicketRewards),
                ),
                value = stringResource(R.string.result_first_clear_jackpot_value),
            )
        }

        InfoRow(
            title = stringResource(R.string.result_reward_title),
            detail = resultRewardDetail(session, result),
            value = stringResource(R.string.gold_value, rewardGoldWithNoticeBoard(session)),
        )
        HeroLevelUpRevealPanel(levelUps = levelUpPreviews)
        InfoRow(
            title = stringResource(R.string.result_loot_reveal_title),
            detail = stringResource(R.string.result_loot_reveal_detail, session.collectableLootRolls(result)),
            value = session.collectableLootRolls(result).toString(),
        )
        RewardNextActionPanel(session = postCollectSession, advice = postCollectAdvice)
        InfoRow(
            title = stringResource(R.string.result_party_title),
            detail = partyNames,
            value = stringResource(R.string.result_party_value, resultParty.size),
        )
        InfoRow(
            title = stringResource(R.string.result_incident_title),
            detail = resultIncidentText(run.planId, result.outcome),
            value = stringResource(R.string.result_incident_value),
        )
    }
}

@Composable
internal fun resultRewardDetail(session: PlaySessionState, result: ExpeditionResult): String {
    val quest = session.expedition?.quest
    val totalXp = session.collectableHeroXp(result, quest)
    val baseXp = result.reward.xp.coerceAtLeast(0)
    val bonusPercent = session.trainingYardQuestXpBonusPercent() + session.tavernKitchenQuestXpBonusPercent(quest)
    if (bonusPercent <= 0) return stringResource(R.string.result_reward_detail, totalXp)

    return stringResource(
        R.string.result_reward_detail_training_bonus,
        totalXp,
        baseXp,
        totalXp - baseXp,
        bonusPercent,
    )
}
@Composable
internal fun RewardNextActionPanel(session: PlaySessionState, advice: ProgressionAdvice) {
    PaperPanel(
        title = stringResource(R.string.reward_next_action_title),
        body = progressionAdviceBody(session, advice),
    ) {
        ProgressBar(progress = progressionAdviceProgress(session, advice))
    }
}

@Composable
internal fun ResultCausesPanel(causes: List<ResultCause>) {
    if (causes.isEmpty()) return

    PaperPanel(
        title = stringResource(R.string.result_causes_title),
        body = stringResource(R.string.result_causes_body),
    ) {
        Spacer(Modifier.height(8.dp))
        causes.forEach { cause -> ResultCauseRow(cause) }
    }
}

@Composable
internal fun ResultCauseRow(cause: ResultCause) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 7.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0x99D0A24A)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF6CD)),
    ) {
        Row(
            modifier = Modifier.padding(9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = resultCauseTitle(cause),
                    color = Color(0xFF211F1A),
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = resultCauseDetail(cause),
                    color = Color(0xFF756B54),
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = resultCauseValue(cause),
                color = Color(0xFF211F1A),
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                textAlign = TextAlign.End,
                maxLines = 1,
            )
        }
    }
}

@Composable
internal fun HeroLevelUpRevealPanel(levelUps: List<Pair<Hero, HeroXpPreview>>) {
    if (levelUps.isEmpty()) return

    DarkPanel(
        title = stringResource(R.string.result_level_up_title),
        body = stringResource(R.string.result_level_up_summary),
    ) {
        Spacer(Modifier.height(4.dp))
        levelUps.forEach { (hero, preview) ->
            HeroLevelUpRevealRow(hero = hero, preview = preview)
        }
    }
}

@Composable
internal fun HeroLevelUpRevealRow(hero: Hero, preview: HeroXpPreview) {
    val borderStyle = heroBorderStyle(hero.rarity)
    val statSummary = statBonusSummary(preview.statGains)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xF4FFF1BF))
            .border(1.dp, borderStyle.borderColor, RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        Image(
            painter = painterResource(heroPortraitResource(hero)),
            contentDescription = hero.name,
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(7.dp))
                .border(1.dp, borderStyle.innerColor, RoundedCornerShape(7.dp)),
            contentScale = ContentScale.Crop,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.result_level_up_line, hero.name, preview.afterLevel),
                color = Color(0xFF211F1A),
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = stringResource(R.string.result_level_up_detail, preview.levelsGained, statSummary),
                color = Color(0xFF756B54),
                fontSize = 11.sp,
                lineHeight = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (preview.rewardUnlocks.isNotEmpty()) {
                Text(
                    text = heroLevelRewardSummary(preview.rewardUnlocks),
                    color = Color(0xFF493F2B),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
internal fun heroLevelRewardSummary(rewards: List<HeroLevelRewardUnlock>): String {
    val lines = mutableListOf<String>()
    rewards.forEach { reward ->
        lines += when (reward.type) {
            HeroLevelRewardType.SpecialistLootRecovery -> stringResource(
                R.string.hero_level_reward_specialist_loot_recovery,
                reward.level,
            )
            HeroLevelRewardType.VeteranLootRecovery -> stringResource(
                R.string.hero_level_reward_veteran_loot_recovery,
                reward.level,
            )
        }
    }
    return lines.joinToString(" / ")
}

@Composable
internal fun OfflineSummaryScreen(
    session: PlaySessionState,
    nowMillis: Long,
    onViewReport: () -> Unit,
    onAchievements: () -> Unit,
    onGuild: () -> Unit,
) {
    val run = session.expedition
    val result = run?.result

    ScreenScaffold(title = stringResource(R.string.offline_summary_title), status = stringResource(R.string.offline_summary_status)) {
        if (run == null || result == null) {
            DarkPanel(title = stringResource(R.string.result_missing_title), body = stringResource(R.string.result_missing_summary)) {
                Button(
                    onClick = onGuild,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2F695C),
                    contentColor = Color(0xFFF8F1D8),
                    disabledContainerColor = Color(0xFF6B5E3C),
                    disabledContentColor = Color(0xFFFFF1C0),
                ),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(stringResource(R.string.guild_home_title), fontWeight = FontWeight.Black)
                }
            }
            return@ScreenScaffold
        }

        val secondsAway = ((nowMillis - run.startedAtMillis) / 1000L).coerceAtLeast(0L)
        val secondsLate = ((nowMillis - run.endsAtMillis) / 1000L).coerceAtLeast(0L)
        val resultParty = run.partyHeroIds
            .mapNotNull { heroId -> session.heroes.firstOrNull { it.id == heroId } }
            .ifEmpty { session.heroes.take(session.effectivePartySlots(run.quest)) }
        val partyNames = resultParty.joinToString { it.name }
        val resultCauses = ResultCauseGenerator.generate(session, run, resultParty, maxCauses = 3)
        val postCollectSession = session.collectResult()
        val postCollectAdvice = ProgressionAdvisor.recommend(postCollectSession, selectedQuest = run.quest)
        val specialContractReward = session.collectableSpecialContracts(result, run.quest)
        val usedSpecialContract = ExpeditionPlanCatalog.requiresSpecialContract(run.planId, run.quest)
        val firstClearTicketRewards = if (
            run.quest.id !in session.clearedQuestIds &&
            (result.outcome == ExpeditionOutcome.GreatSuccess || result.outcome == ExpeditionOutcome.Success)
        ) {
            run.quest.firstClearTicketRewards.filterValues { count -> count > 0 }
        } else {
            emptyMap()
        }
        val offlineHighlights = session.offlineReportHighlights(postCollectSession)
        val passiveReport = session.lastOfflinePassiveIncome ?: session.passiveIncomeReport(
            sinceMillis = run.startedAtMillis,
            untilMillis = nowMillis,
            activeExpeditionHeroIds = run.partyHeroIds,
            activeUntilMillis = run.endsAtMillis,
        )

        QuestCardArt(
            bannerResourceId = questBannerResource(run.quest),
            frameResourceId = questFrameResource(run.quest.difficulty),
            borderStyle = questDifficultyBorderStyle(run.quest.difficulty),
        )
        DarkPanel(
            title = stringResource(R.string.offline_summary_panel_title),
            body = stringResource(R.string.offline_summary_panel_body),
        ) {
            ActionRow(
                primaryLabel = stringResource(R.string.view_report_action),
                secondaryLabel = stringResource(R.string.guild_home_title),
                onPrimary = onViewReport,
                onSecondary = onGuild,
            )
        }
        InfoRow(
            title = stringResource(R.string.offline_expedition_title),
            detail = stringResource(R.string.offline_expedition_detail, questTitle(run.quest)),
            value = outcomeLabel(result.outcome),
        )
        InfoRow(
            title = stringResource(R.string.offline_away_title),
            detail = stringResource(R.string.offline_away_detail, secondsLate),
            value = stringResource(R.string.offline_seconds_value, secondsAway),
        )
        if (passiveReport.cappedSeconds > 0L) {
            PassiveIncomeInfoRow(passiveReport)
        }
        if (session.lastOfflinePassiveIncidents.isNotEmpty()) {
            PassiveIncidentsPanel(session.lastOfflinePassiveIncidents)
        }
        InfoRow(
            title = stringResource(R.string.result_outcome_title),
            detail = resultIncidentText(run.planId, result.outcome),
            value = outcomeLabel(result.outcome),
        )
        ResultCausesPanel(causes = resultCauses)

        if (usedSpecialContract || specialContractReward > 0) {
            InfoRow(
                title = stringResource(R.string.result_special_contract_payoff_title),
                detail = if (specialContractReward > 0) {
                    stringResource(R.string.result_special_contract_payoff_detail, specialContractReward)
                } else {
                    stringResource(R.string.result_special_contract_payoff_detail_empty)
                },
                value = stringResource(R.string.special_contract_xxl_value),
            )
        }
        if (firstClearTicketRewards.isNotEmpty()) {
            InfoRow(
                title = stringResource(R.string.result_first_clear_jackpot_title),
                detail = stringResource(
                    R.string.result_first_clear_jackpot_detail,
                    firstClearTicketRewardText(firstClearTicketRewards),
                ),
                value = stringResource(R.string.result_first_clear_jackpot_value),
            )
        }

        InfoRow(
            title = stringResource(R.string.result_reward_title),
            detail = resultRewardDetail(session, result),
            value = stringResource(R.string.gold_value, rewardGoldWithNoticeBoard(session)),
        )
        InfoRow(
            title = stringResource(R.string.result_loot_reveal_title),
            detail = stringResource(R.string.result_loot_reveal_detail, session.collectableLootRolls(result)),
            value = session.collectableLootRolls(result).toString(),
        )
        InfoRow(
            title = stringResource(R.string.result_party_title),
            detail = partyNames,
            value = stringResource(R.string.result_party_value, resultParty.size),
        )
        OfflineTicketProgressPanel(highlights = offlineHighlights)
        OfflineAchievementProgressPanel(session = session, highlights = offlineHighlights, onAchievements = onAchievements)
        RewardNextActionPanel(session = postCollectSession, advice = postCollectAdvice)
    }
}

@Composable
internal fun PassiveIncomeInfoRow(report: PassiveIncomeReport) {
    val detail = if (report.activeSeconds > 0L && report.activeGoldPerHour < report.goldPerHour) {
        stringResource(
            R.string.offline_passive_income_detail_active,
            report.cappedSeconds,
            report.activeGoldPerHour,
            report.goldPerHour,
        )
    } else {
        stringResource(
            R.string.offline_passive_income_detail,
            report.cappedSeconds,
            report.goldPerHour,
        )
    }
    InfoRow(
        title = stringResource(R.string.offline_passive_income_title),
        detail = detail,
        value = passiveIncomeValueText(report),
    )
}

@Composable
internal fun passiveIncomeValueText(report: PassiveIncomeReport): String {
    val parts = buildList {
        add(stringResource(R.string.offline_passive_income_value, report.gold))
        if (report.supplies > 0) add(stringResource(R.string.offline_passive_income_value_supplies, report.supplies))
        if (report.lootFinds.isNotEmpty()) add(stringResource(R.string.offline_passive_income_value_loot, report.lootFinds.size))
    }
    return parts.joinToString(" / ")
}

@Composable
internal fun PassiveIncidentsPanel(incidents: List<PassiveIncident>) {
    PaperPanel(
        title = stringResource(R.string.offline_passive_incidents_title),
        body = stringResource(R.string.offline_passive_incidents_summary),
    ) {
        Spacer(Modifier.height(8.dp))
        incidents.forEach { incident ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = incident.text,
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF493F2B),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = passiveIncidentRewardLabel(incident.reward),
                    color = Color(0xFF211F1A),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
internal fun passiveIncidentRewardLabel(reward: PassiveIncidentReward): String {
    val parts = buildList {
        if (reward.gold > 0) add(stringResource(R.string.offline_passive_incident_reward_gold, reward.gold))
        if (reward.reputation > 0) add(stringResource(R.string.offline_passive_incident_reward_rep, reward.reputation))
        if (reward.specialContracts > 0) add(stringResource(R.string.offline_passive_incident_reward_contract, reward.specialContracts))
    }
    return parts.ifEmpty { listOf(stringResource(R.string.offline_passive_incident_reward_none)) }.joinToString(", ")
}

@Composable
internal fun OfflineTicketProgressPanel(highlights: OfflineReportHighlights) {
    if (!highlights.hasTicketProgress) return

    val inventoryLabels = offlineTicketStackLabels(highlights.ticketInventory)
    val claimableLabels = offlineTicketStackLabels(highlights.claimableTicketRewardDelta)
    PaperPanel(
        title = stringResource(R.string.offline_tickets_title),
        body = if (inventoryLabels.isNotEmpty()) {
            stringResource(R.string.offline_tickets_summary)
        } else {
            stringResource(R.string.offline_tickets_claimable_summary)
        },
    ) {
        Spacer(Modifier.height(6.dp))
        if (inventoryLabels.isNotEmpty()) {
            Text(
                text = stringResource(R.string.offline_tickets_inventory_line, inventoryLabels.joinToString(" / ")),
                color = Color(0xFF493F2B),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (claimableLabels.isNotEmpty()) {
            Text(
                text = stringResource(R.string.offline_tickets_claimable_line, claimableLabels.joinToString(" / ")),
                modifier = Modifier.padding(top = if (inventoryLabels.isNotEmpty()) 4.dp else 0.dp),
                color = Color(0xFF493F2B),
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun offlineTicketStackLabels(stacks: Map<String, Int>): List<String> {
    val labels = mutableListOf<String>()
    for ((ticketId, count) in stacks) {
        labels += stringResource(R.string.offline_ticket_stack, recruitmentTicketTitle(ticketId), count)
    }
    return labels
}

@Composable
internal fun OfflineAchievementProgressPanel(
    session: PlaySessionState,
    highlights: OfflineReportHighlights,
    onAchievements: () -> Unit,
) {
    val claimableCount = session.claimableAchievementCount()
    val nextMilestone = session.nextAchievementMilestone()
    val milestoneLine = nextMilestone?.let { milestone ->
        stringResource(R.string.achievements_next_milestone_detail, milestone.sealsRequired, milestone.title)
    } ?: stringResource(R.string.achievements_all_milestones_done)

    PaperPanel(
        title = stringResource(R.string.offline_achievements_title),
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
        if (highlights.hasAchievementProgress) {
            Text(
                text = stringResource(
                    R.string.offline_achievement_delta,
                    highlights.completedAchievementDelta,
                    highlights.claimableAchievementDelta,
                    highlights.claimableSealDelta,
                ),
                modifier = Modifier.padding(top = 4.dp),
                color = Color(0xFF493F2B),
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Button(
            onClick = onAchievements,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F695C)),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(stringResource(R.string.achievements_open_action), fontWeight = FontWeight.Black)
        }
    }
}


@Composable
internal fun RewardLootScreen(
    session: PlaySessionState,
    onKeepSelection: (List<LootItem>) -> Unit,
    onDiscardRest: () -> Unit,
    onDone: () -> Unit,
) {
    val pendingRewards = session.pendingLootItems.asReversed()
    val keepLimit = session.pendingLootEffectiveKeepLimit()
    val keepCapacity = minOf(keepLimit, pendingRewards.size)
    val carryBreakdown = session.pendingLootRecoveryBreakdown()
    var selectedIndex by remember(pendingRewards.size) { mutableStateOf(0) }
    var selectedIndexes by remember(pendingRewards.size, keepCapacity) { mutableStateOf(emptySet<Int>()) }
    var confirmingSelection by remember(pendingRewards.size, keepCapacity) { mutableStateOf(false) }
    val selectedItem = pendingRewards.getOrNull(selectedIndex)
    val selectedItems = selectedIndexes.sorted().mapNotNull { index -> pendingRewards.getOrNull(index) }
    val selectedCount = selectedItems.size
    val remainingChoices = (keepCapacity - selectedCount).coerceAtLeast(0)

    ScreenScaffold(
        title = stringResource(R.string.loot_reward_title),
        status = stringResource(R.string.loot_reward_status, pendingRewards.size, keepCapacity),
    ) {
        if (selectedItem == null) {
            DarkPanel(title = stringResource(R.string.loot_rewards_done_title), body = stringResource(R.string.loot_rewards_done_summary)) {
                Button(
                    onClick = onDone,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2F695C),
                        contentColor = Color(0xFFF8F1D8),
                        disabledContainerColor = Color(0xFF6B5E3C),
                        disabledContentColor = Color(0xFFFFF1C0),
                    ),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(stringResource(R.string.guild_home_title), fontWeight = FontWeight.Black)
                }
            }
            return@ScreenScaffold
        }

        val selectedItemName = lootItemName(selectedItem)
        val suggestedTarget = bestEquipSuggestion(session, selectedItem)?.takeIf { it.gain > 0 }
        val equipTargetDetail = suggestedTarget?.let {
            stringResource(
                R.string.equip_target_detail,
                it.hero.name,
                heroClassLabel(it.hero.heroClass),
                formatSignedCount(it.gain),
            )
        } ?: stringResource(R.string.reward_equip_target_missing)
        val projectedSession = if (selectedItems.isEmpty()) session else session.keepPendingLootSelection(selectedItems)
        val nextAdvice = ProgressionAdvisor.recommend(projectedSession)
        val itemIsSelected = selectedIndex in selectedIndexes

        LootIconPanel(item = selectedItem, contentDescription = selectedItemName)
        InfoRow(
            title = stringResource(R.string.loot_carry_title),
            detail = stringResource(R.string.loot_carry_detail, selectedCount, keepLimit),
            value = stringResource(R.string.loot_carry_value, remainingChoices),
        )
        InfoRow(
            title = stringResource(R.string.loot_selection_title),
            detail = stringResource(R.string.loot_selection_detail, selectedCount, keepCapacity),
            value = stringResource(R.string.loot_selection_value, remainingChoices),
        )
        LootCarryBreakdownPanel(carryBreakdown)
        InfoRow(
            title = stringResource(R.string.equip_target_title),
            detail = equipTargetDetail,
            value = suggestedTarget?.let { formatSignedCount(it.gain) } ?: "-",
        )
        DarkPanel(
            title = selectedItemName,
            body = lootRewardChoiceSummary(selectedItem, pendingRewards.size, remainingChoices),
        ) {
            ActionRow(
                primaryLabel = if (itemIsSelected) stringResource(R.string.loot_unselect_action) else stringResource(R.string.loot_select_action),
                secondaryLabel = if (selectedCount > 0) stringResource(R.string.loot_confirm_selection_action) else stringResource(R.string.loot_discard_rest_action),
                onPrimary = {
                    selectedIndexes = if (itemIsSelected) {
                        selectedIndexes - selectedIndex
                    } else {
                        selectedIndexes + selectedIndex
                    }
                    confirmingSelection = false
                },
                onSecondary = {
                    if (selectedCount > 0) {
                        confirmingSelection = true
                    } else {
                        onDiscardRest()
                    }
                },
                primaryEnabled = itemIsSelected || selectedCount < keepCapacity,
            )
        }
        if (confirmingSelection && selectedItems.isNotEmpty()) {
            DarkPanel(
                title = stringResource(R.string.loot_confirm_title),
                body = stringResource(R.string.loot_confirm_summary, selectedCount, pendingRewards.size - selectedCount),
            ) {
                ActionRow(
                    primaryLabel = stringResource(R.string.loot_keep_selected_action),
                    secondaryLabel = stringResource(R.string.loot_review_action),
                    onPrimary = { onKeepSelection(selectedItems) },
                    onSecondary = { confirmingSelection = false },
                )
            }
        }
        if (selectedItems.isNotEmpty()) {
            RewardNextActionPanel(session = projectedSession, advice = nextAdvice)
        }

        Text(
            text = stringResource(R.string.loot_reward_candidates_title),
            color = Color(0xFFFFF0BD),
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(top = 2.dp, bottom = 8.dp),
        )
        pendingRewards.forEachIndexed { index, item ->
            LootInventoryRow(
                item = item,
                selected = index == selectedIndex || index in selectedIndexes,
                onClick = {
                    selectedIndex = index
                    confirmingSelection = false
                },
            )
        }
    }
}
@Composable
internal fun LootCarryBreakdownPanel(breakdown: LootCarryBreakdown) {
    DarkPanel(
        title = stringResource(R.string.loot_carry_breakdown_title),
        body = stringResource(R.string.loot_carry_breakdown_body, breakdown.total),
    ) {
        Spacer(Modifier.height(8.dp))
        LootCarryBreakdownRow(
            label = stringResource(R.string.loot_carry_source_base),
            value = stringResource(R.string.loot_carry_source_base_value, breakdown.base),
        )
        LootCarryBreakdownRow(
            label = stringResource(R.string.loot_carry_source_bunk_room),
            value = stringResource(R.string.loot_carry_source_bonus_value, breakdown.bunkRoom),
        )
        LootCarryBreakdownRow(
            label = stringResource(R.string.loot_carry_source_veteran),
            value = stringResource(R.string.loot_carry_source_bonus_value, breakdown.veteran),
        )
        LootCarryBreakdownRow(
            label = stringResource(R.string.loot_carry_source_specialist),
            value = stringResource(R.string.loot_carry_source_bonus_value, breakdown.specialist),
        )
    }
}

@Composable
internal fun LootCarryBreakdownRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = Color(0xFFDED0A2),
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            color = Color(0xFFFFF1C0),
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            maxLines = 1,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}
