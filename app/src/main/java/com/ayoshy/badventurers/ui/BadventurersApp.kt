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
import com.ayoshy.badventurers.game.ExpeditionEngine
import com.ayoshy.badventurers.game.ExpeditionEstimator
import com.ayoshy.badventurers.game.ExpeditionOutcome
import com.ayoshy.badventurers.game.Hero
import com.ayoshy.badventurers.game.HeroCatalog
import com.ayoshy.badventurers.game.HeroClass
import com.ayoshy.badventurers.game.HeroGacha
import com.ayoshy.badventurers.game.HeroRarity
import com.ayoshy.badventurers.game.HeroRecruitmentResult
import com.ayoshy.badventurers.game.JournalEntry
import com.ayoshy.badventurers.game.LootEconomy
import com.ayoshy.badventurers.game.LootIcon
import com.ayoshy.badventurers.game.LootItem
import com.ayoshy.badventurers.game.LootRarity
import com.ayoshy.badventurers.game.LootSlot
import com.ayoshy.badventurers.game.PartyPowerCalculator
import com.ayoshy.badventurers.game.PlayPhase
import com.ayoshy.badventurers.game.PlaySessionState
import com.ayoshy.badventurers.game.Quest
import com.ayoshy.badventurers.game.QuestRisk
import com.ayoshy.badventurers.game.SeedGame
import com.ayoshy.badventurers.game.StatType
import com.ayoshy.badventurers.game.Trait
import kotlinx.coroutines.delay

private val BadventurersColors = darkColorScheme(
    primary = Color(0xFF2F695C),
    secondary = Color(0xFFD0A24A),
    background = Color(0xFF141512),
    surface = Color(0xFF24251F),
    onPrimary = Color(0xFFF8F1D8),
    onSecondary = Color(0xFF211F1A),
    onBackground = Color(0xFFF8F1D8),
    onSurface = Color(0xFFF8F1D8),
)

private data class RarityBorderStyle(
    val borderColor: Color,
    val innerColor: Color,
    val surfaceColor: Color,
    val selectedSurfaceColor: Color,
    val strokeWidth: Dp = 2.dp,
)

private enum class GameTab {
    Guild,
    Quests,
    ExpeditionPrep,
    OfflineSummary,
    QuestResult,
    RewardLoot,
    Heroes,
    Loot,
    Upgrades,
}
private enum class HeroesPanelTab {
    Roster,
    Gacha,
}

private enum class HeroRosterSort {
    Rarity,
    Name,
}

@Composable
fun BadventurersApp(
    initialSession: PlaySessionState = PlaySessionState.initial(),
    showOfflineSummaryOnStart: Boolean = false,
    onSessionChanged: (PlaySessionState) -> Unit = {},
) {
    MaterialTheme(colorScheme = BadventurersColors) {
        var selectedTab by rememberSaveable { mutableStateOf(if (showOfflineSummaryOnStart) GameTab.OfflineSummary else GameTab.Guild) }
        var session by remember { mutableStateOf(initialSession) }
        var nowMillis by remember { mutableStateOf(System.currentTimeMillis()) }
        var lastRecruitment by remember { mutableStateOf<HeroRecruitmentResult?>(null) }
        var selectedHeroId by rememberSaveable { mutableStateOf(initialSession.heroes.firstOrNull()?.id.orEmpty()) }
        var selectedQuestPartyIds by rememberSaveable {
            mutableStateOf(initialSession.selectedPartyForQuest(SeedGame.firstQuest, initialSession.heroes).map { it.id })
        }
        val expeditionEngine = remember { ExpeditionEngine() }
        val selectedQuestParty = selectedQuestPartyIds.mapNotNull { heroId -> session.heroes.firstOrNull { it.id == heroId } }
        fun updateSession(nextSession: PlaySessionState) {
            session = nextSession
            if (nextSession.heroes.none { it.id == selectedHeroId }) {
                selectedHeroId = nextSession.heroes.firstOrNull()?.id.orEmpty()
            }
            val nextHeroIds = nextSession.heroes.map { it.id }
            val keptPartyIds = selectedQuestPartyIds.filter { it in nextHeroIds }.take(SeedGame.firstQuest.partySlots)
            selectedQuestPartyIds = keptPartyIds + nextHeroIds
                .filterNot { it in keptPartyIds }
                .take(SeedGame.firstQuest.partySlots - keptPartyIds.size)
            onSessionChanged(nextSession)
        }
        LaunchedEffect(session.phase, session.expedition?.endsAtMillis) {
            while (session.phase == PlayPhase.Running) {
                delay(250)
                val currentTime = System.currentTimeMillis()
                nowMillis = currentTime
                val nextSession = session.tick(currentTime, expeditionEngine, session.heroes)
                if (nextSession != session) updateSession(nextSession)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF141512)),
        ) {
            Image(
                painter = painterResource(R.drawable.guild_home_background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0x6610110D),
                                Color(0x3310110D),
                                Color(0xCC10110D),
                            ),
                        ),
                    ),
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding(),
            ) {
                if (selectedTab == GameTab.Guild) TopBar(session = session)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                ) {
                    when (selectedTab) {
                        GameTab.Guild -> GuildScreen(
                            session = session,
                            nowMillis = nowMillis,
                            onViewResult = { selectedTab = GameTab.QuestResult },
                            onNextQuest = { selectedTab = GameTab.Quests },
                            onFinishQuestNow = {
                                updateSession(session.finishQuestNow(expeditionEngine, session.heroes))
                                nowMillis = System.currentTimeMillis()
                            },
                        )
                        GameTab.Quests -> QuestsScreen(
                            session = session,
                            onPrepare = { selectedTab = GameTab.ExpeditionPrep },
                            onParty = { selectedTab = GameTab.Heroes },
                        )
                        GameTab.ExpeditionPrep -> ExpeditionPrepScreen(
                            session = session,
                            selectedPartyIds = selectedQuestPartyIds,
                            onToggleHero = { heroId ->
                                selectedQuestPartyIds = togglePartyHero(
                                    currentIds = selectedQuestPartyIds,
                                    heroId = heroId,
                                    maxSlots = SeedGame.firstQuest.partySlots,
                                )
                            },
                            onLaunch = {
                                val currentTime = System.currentTimeMillis()
                                nowMillis = currentTime
                                updateSession(session.startQuest(currentTime, SeedGame.firstQuest, selectedQuestParty))
                                selectedTab = GameTab.Guild
                            },
                            onParty = { selectedTab = GameTab.Heroes },
                        )
                        GameTab.OfflineSummary -> OfflineSummaryScreen(
                            session = session,
                            nowMillis = nowMillis,
                            onViewReport = { selectedTab = GameTab.QuestResult },
                            onGuild = { selectedTab = GameTab.Guild },
                        )
                        GameTab.QuestResult -> QuestResultScreen(
                            session = session,
                            onCollect = {
                                val nextSession = session.collectResult()
                                updateSession(nextSession)
                                selectedTab = if (nextSession.pendingLootItems.isEmpty()) GameTab.Guild else GameTab.RewardLoot
                            },
                            onBack = { selectedTab = GameTab.Guild },
                        )
                        GameTab.RewardLoot -> RewardLootScreen(
                            session = session,
                            onKeep = { item ->
                                val nextSession = session.keepPendingLoot(item)
                                updateSession(nextSession)
                                if (nextSession.pendingLootItems.isEmpty()) selectedTab = GameTab.Guild
                            },
                            onSell = { item ->
                                val nextSession = session.sellPendingLoot(item)
                                updateSession(nextSession)
                                if (nextSession.pendingLootItems.isEmpty()) selectedTab = GameTab.Guild
                            },
                            onDone = { selectedTab = GameTab.Guild },
                        )
                        GameTab.Heroes -> HeroesScreen(
                            session = session,
                            lastRecruitment = lastRecruitment,
                            selectedHeroId = selectedHeroId,
                            onOpenHero = { selectedHeroId = it },
                            onEquip = { heroId, item -> updateSession(session.equipLoot(heroId, item)) },
                            onUnequip = { heroId, slot -> updateSession(session.unequipLoot(heroId, slot)) },
                            onReleaseHero = { heroId -> updateSession(session.releaseHero(heroId)) },
                            onRecruit = {
                                val recruitment = session.recruitHero(seed = System.currentTimeMillis().toInt())
                                if (recruitment != null) {
                                    lastRecruitment = recruitment
                                    updateSession(recruitment.session)
                                }
                            },
                        )
                        GameTab.Loot -> LootScreen(
                            session = session,
                            onSellInventory = { item -> updateSession(session.sellLoot(item)) },
                            onEquip = { heroId, item -> updateSession(session.equipLoot(heroId, item)) },
                        )
                        GameTab.Upgrades -> UpgradesScreen(
                            session = session,
                            onBuy = { updateSession(session.upgradeNoticeBoard()) },
                        )
                    }
                }
                BottomBar(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
            }
        }
    }
}

@Composable
private fun TopBar(session: PlaySessionState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xF2141512), Color(0xAA141512)),
                ),
            )
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.app_name),
                color = Color(0xFFFFF1C0),
                fontWeight = FontWeight.Black,
                fontSize = 22.sp,
                maxLines = 1,
            )
        }
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            ResourceChip(label = stringResource(R.string.gold_label), value = formatCount(session.gold))
            ResourceChip(label = stringResource(R.string.reputation_label), value = session.reputation.toString())
            ResourceChip(label = stringResource(R.string.guild_level_label), value = "Lv. ${session.guildLevel}")
        }
    }
}

@Composable
private fun RowScope.ResourceChip(label: String, value: String) {
    Card(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xEEF8E7B5)),
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
            Text(text = label, color = Color(0xFF4F4630), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(text = value, color = Color(0xFF211F1A), fontSize = 15.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun GuildScreen(
    session: PlaySessionState,
    nowMillis: Long,
    onViewResult: () -> Unit,
    onNextQuest: () -> Unit,
    onFinishQuestNow: () -> Unit,
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
                            result.reward.lootRolls,
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
        PaperPanel(title = stringResource(R.string.recommended_title), body = stringResource(R.string.recommended_upgrade)) {
            ProgressBar(progress = 0.72f)
        }
        val journalLines = session.journalEntries.takeLast(3).map { journalEntryText(it) }
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
private fun QuestResultScreen(session: PlaySessionState, onCollect: () -> Unit, onBack: () -> Unit) {
    val run = session.expedition
    val result = run?.result

    ScreenScaffold(title = stringResource(R.string.result_quest_title), status = stringResource(R.string.result_report_status)) {
        if (run == null || result == null) {
            DarkPanel(title = stringResource(R.string.result_missing_title), body = stringResource(R.string.result_missing_summary)) {
                Button(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F695C)),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(stringResource(R.string.guild_home_title), fontWeight = FontWeight.Black)
                }
            }
            return@ScreenScaffold
        }

        val partyNames = run.partyHeroIds
            .mapNotNull { heroId -> session.heroes.firstOrNull { it.id == heroId }?.name }
            .ifEmpty { session.heroes.take(run.quest.partySlots).map { it.name } }
            .joinToString()

        QuestCardArt(
            bannerResourceId = questBannerResource(run.quest),
            frameResourceId = questFrameResource(run.quest.difficulty),
            borderStyle = questDifficultyBorderStyle(run.quest.difficulty),
        )
        DarkPanel(
            title = outcomeLabel(result.outcome),
            body = stringResource(R.string.result_report_summary),
        ) {
            ActionRow(
                primaryLabel = stringResource(R.string.collect_action),
                secondaryLabel = stringResource(R.string.guild_home_title),
                onPrimary = onCollect,
                onSecondary = onBack,
            )
        }
        InfoRow(
            title = stringResource(R.string.result_outcome_title),
            detail = stringResource(R.string.result_outcome_detail, result.scoreMargin),
            value = outcomeLabel(result.outcome),
        )
        InfoRow(
            title = stringResource(R.string.result_reward_title),
            detail = stringResource(R.string.result_reward_detail, result.reward.xp),
            value = stringResource(R.string.gold_value, rewardGoldWithNoticeBoard(session)),
        )
        InfoRow(
            title = stringResource(R.string.result_loot_reveal_title),
            detail = stringResource(R.string.result_loot_reveal_detail, result.reward.lootRolls),
            value = result.reward.lootRolls.toString(),
        )
        InfoRow(
            title = stringResource(R.string.result_party_title),
            detail = partyNames,
            value = stringResource(R.string.result_party_value, run.partyHeroIds.size),
        )
        InfoRow(
            title = stringResource(R.string.result_incident_title),
            detail = resultIncidentText(result.outcome),
            value = stringResource(R.string.result_incident_value),
        )
    }
}

@Composable
private fun OfflineSummaryScreen(
    session: PlaySessionState,
    nowMillis: Long,
    onViewReport: () -> Unit,
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F695C)),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(stringResource(R.string.guild_home_title), fontWeight = FontWeight.Black)
                }
            }
            return@ScreenScaffold
        }

        val secondsAway = ((nowMillis - run.startedAtMillis) / 1000L).coerceAtLeast(0L)
        val secondsLate = ((nowMillis - run.endsAtMillis) / 1000L).coerceAtLeast(0L)
        val partyNames = run.partyHeroIds
            .mapNotNull { heroId -> session.heroes.firstOrNull { it.id == heroId }?.name }
            .ifEmpty { session.heroes.take(run.quest.partySlots).map { it.name } }
            .joinToString()

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
            title = stringResource(R.string.offline_away_title),
            detail = stringResource(R.string.offline_away_detail, secondsLate),
            value = stringResource(R.string.offline_seconds_value, secondsAway),
        )
        InfoRow(
            title = stringResource(R.string.result_outcome_title),
            detail = resultIncidentText(result.outcome),
            value = outcomeLabel(result.outcome),
        )
        InfoRow(
            title = stringResource(R.string.result_reward_title),
            detail = stringResource(R.string.result_reward_detail, result.reward.xp),
            value = stringResource(R.string.gold_value, rewardGoldWithNoticeBoard(session)),
        )
        InfoRow(
            title = stringResource(R.string.result_loot_reveal_title),
            detail = stringResource(R.string.result_loot_reveal_detail, result.reward.lootRolls),
            value = result.reward.lootRolls.toString(),
        )
        InfoRow(
            title = stringResource(R.string.result_party_title),
            detail = partyNames,
            value = stringResource(R.string.result_party_value, run.partyHeroIds.size),
        )
    }
}

@Composable
private fun QuestsScreen(session: PlaySessionState, onPrepare: () -> Unit, onParty: () -> Unit) {
    val canPrepare = session.phase == PlayPhase.Idle
    val prepareLabel = if (canPrepare) stringResource(R.string.prep_action) else stringResource(R.string.quest_blocked_action)
    val quest = SeedGame.firstQuest

    ScreenScaffold(title = stringResource(R.string.quests_title), status = phaseStatus(session.phase)) {
        QuestCardArt(
            bannerResourceId = questBannerResource(quest),
            frameResourceId = questFrameResource(quest.difficulty),
            borderStyle = questDifficultyBorderStyle(quest.difficulty),
        )
        DarkPanel(title = stringResource(R.string.quest_card_title), body = stringResource(R.string.quest_card_summary)) {
            ActionRow(
                primaryLabel = prepareLabel,
                secondaryLabel = stringResource(R.string.party_action),
                onPrimary = onPrepare,
                onSecondary = onParty,
                primaryEnabled = canPrepare,
            )
        }
    }
}

@Composable
private fun ExpeditionPrepScreen(
    session: PlaySessionState,
    selectedPartyIds: List<String>,
    onToggleHero: (String) -> Unit,
    onLaunch: () -> Unit,
    onParty: () -> Unit,
) {
    val quest = SeedGame.firstQuest
    val partyHeroes = selectedPartyIds.mapNotNull { heroId -> session.heroes.firstOrNull { it.id == heroId } }
    val estimate = ExpeditionEstimator.estimate(partyHeroes, quest, session.equippedLoot)
    val canLaunch = session.phase == PlayPhase.Idle && partyHeroes.isNotEmpty()
    val launchLabel = if (canLaunch) stringResource(R.string.launch_quest_action) else stringResource(R.string.quest_blocked_action)
    val previewGold = questBaseGoldWithNoticeBoard(session, quest)

    ScreenScaffold(title = stringResource(R.string.prep_title), status = stringResource(R.string.prep_status)) {
        QuestCardArt(
            bannerResourceId = questBannerResource(quest),
            frameResourceId = questFrameResource(quest.difficulty),
            borderStyle = questDifficultyBorderStyle(quest.difficulty),
        )
        DarkPanel(title = stringResource(R.string.quest_card_title), body = stringResource(R.string.prep_summary)) {
            ActionRow(
                primaryLabel = launchLabel,
                secondaryLabel = stringResource(R.string.party_action),
                onPrimary = onLaunch,
                onSecondary = onParty,
                primaryEnabled = canLaunch,
            )
        }
        InfoRow(
            title = stringResource(R.string.prep_success_title),
            detail = stringResource(R.string.prep_success_detail, estimate.partyPower, estimate.targetPower),
            value = stringResource(R.string.prep_success_value, estimate.successChancePercent),
        )
        InfoRow(
            title = stringResource(R.string.prep_risk_title),
            detail = stringResource(R.string.prep_risk_detail, riskLabel(quest.risk), quest.durationSeconds, estimate.riskPenalty),
            value = stringResource(R.string.prep_target_value, estimate.targetPower),
        )
        InfoRow(
            title = stringResource(R.string.prep_reward_title),
            detail = stringResource(R.string.prep_reward_detail, quest.pityGold, 3),
            value = stringResource(R.string.gold_value, previewGold),
        )
        Text(
            text = stringResource(R.string.prep_party_title, partyHeroes.size, quest.partySlots),
            color = Color(0xFFFFF0BD),
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(top = 2.dp, bottom = 8.dp),
        )
        repeat(quest.partySlots) { index ->
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
            modifier = Modifier.padding(top = 2.dp, bottom = 8.dp),
        )
        session.heroes.forEach { hero ->
            val selected = hero.id in selectedPartyIds
            HeroPartyChoiceRow(
                hero = hero,
                selected = selected,
                enabled = selected || partyHeroes.size < quest.partySlots,
                session = session,
                onClick = { onToggleHero(hero.id) },
            )
        }
    }
}

@Composable
private fun PartySlotRow(slotNumber: Int, hero: Hero?, session: PlaySessionState) {
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
private fun HeroPartyChoiceRow(
    hero: Hero,
    selected: Boolean,
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
        enabled -> stringResource(R.string.prep_roster_pick_value)
        else -> stringResource(R.string.prep_roster_full_value)
    }

    Card(
        modifier = cardModifier,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) borderStyle.selectedSurfaceColor else Color(0xF4FFF1BF),
        ),
        border = if (selected) BorderStroke(borderStyle.strokeWidth, borderStyle.borderColor) else null,
    ) {
        Row(
            modifier = Modifier.padding(9.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = hero.name, color = Color(0xFF211F1A), fontWeight = FontWeight.Black, maxLines = 1)
                Text(
                    text = heroRosterDetail(hero),
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
private fun HeroesScreen(
    session: PlaySessionState,
    lastRecruitment: HeroRecruitmentResult?,
    selectedHeroId: String,
    onOpenHero: (String) -> Unit,
    onEquip: (String, LootItem) -> Unit,
    onUnequip: (String, LootSlot) -> Unit,
    onReleaseHero: (String) -> Unit,
    onRecruit: () -> Unit,
) {
    var panelTab by rememberSaveable { mutableStateOf(HeroesPanelTab.Roster) }
    var sortOrder by rememberSaveable { mutableStateOf(HeroRosterSort.Rarity) }
    var detailHeroId by rememberSaveable { mutableStateOf<String?>(null) }
    val detailHero = detailHeroId?.let { heroId -> session.heroes.firstOrNull { it.id == heroId } }
    val status = when (panelTab) {
        HeroesPanelTab.Roster -> stringResource(R.string.roster_status)
        HeroesPanelTab.Gacha -> stringResource(R.string.gacha_status)
    }

    ScreenScaffold(title = stringResource(R.string.heroes_title), status = status) {
        HeroesPanelSwitcher(selectedTab = panelTab, onSelectedTab = { panelTab = it })
        when (panelTab) {
            HeroesPanelTab.Roster -> HeroRosterPanel(
                session = session,
                sortOrder = sortOrder,
                selectedHeroId = selectedHeroId,
                onSortOrder = { sortOrder = it },
                onOpenHero = { heroId ->
                    detailHeroId = heroId
                    onOpenHero(heroId)
                },
            )
            HeroesPanelTab.Gacha -> HeroGachaPanel(
                session = session,
                lastRecruitment = lastRecruitment,
                onRecruit = onRecruit,
            )
        }
    }

    if (detailHero != null) {
        HeroDetailDialog(
            session = session,
            hero = detailHero,
            onDismiss = { detailHeroId = null },
            onEquip = { item -> onEquip(detailHero.id, item) },
            onUnequip = { slot -> onUnequip(detailHero.id, slot) },
            onReleaseHero = { heroId ->
                onReleaseHero(heroId)
                detailHeroId = null
            },
        )
    }
}

@Composable
private fun HeroesPanelSwitcher(selectedTab: HeroesPanelTab, onSelectedTab: (HeroesPanelTab) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        HeroesPanelButton(
            label = stringResource(R.string.heroes_tab_roster),
            selected = selectedTab == HeroesPanelTab.Roster,
            onClick = { onSelectedTab(HeroesPanelTab.Roster) },
            modifier = Modifier.weight(1f),
        )
        HeroesPanelButton(
            label = stringResource(R.string.heroes_tab_gacha),
            selected = selectedTab == HeroesPanelTab.Gacha,
            onClick = { onSelectedTab(HeroesPanelTab.Gacha) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun HeroesPanelButton(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.height(42.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color(0xFF2F695C) else Color(0xFFE0C66F),
            contentColor = if (selected) Color(0xFFF8F1D8) else Color(0xFF211F1A),
        ),
        shape = RoundedCornerShape(8.dp),
    ) {
        Text(text = label, fontWeight = FontWeight.Black, maxLines = 1)
    }
}

@Composable
private fun HeroRosterPanel(
    session: PlaySessionState,
    sortOrder: HeroRosterSort,
    selectedHeroId: String,
    onSortOrder: (HeroRosterSort) -> Unit,
    onOpenHero: (String) -> Unit,
) {
    val orderedHeroes = session.heroes.sortedWith(heroRosterComparator(sortOrder))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.roster_title),
            color = Color(0xFFFFF0BD),
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.weight(1f),
        )
        HeroSortButton(
            label = stringResource(R.string.heroes_sort_rarity),
            selected = sortOrder == HeroRosterSort.Rarity,
            onClick = { onSortOrder(HeroRosterSort.Rarity) },
        )
        HeroSortButton(
            label = stringResource(R.string.heroes_sort_name),
            selected = sortOrder == HeroRosterSort.Name,
            onClick = { onSortOrder(HeroRosterSort.Name) },
        )
    }

    if (orderedHeroes.isEmpty()) {
        DarkPanel(
            title = stringResource(R.string.hero_roster_empty_title),
            body = stringResource(R.string.hero_roster_empty_summary),
        )
    } else {
        HeroPortraitGrid(
            heroes = orderedHeroes,
            selectedHeroId = selectedHeroId,
            onSelectHero = onOpenHero,
        )
    }
}

@Composable
private fun HeroSortButton(label: String, selected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(34.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color(0xFF2F695C) else Color(0xCCFFF1BF),
            contentColor = if (selected) Color(0xFFF8F1D8) else Color(0xFF211F1A),
        ),
        shape = RoundedCornerShape(8.dp),
    ) {
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Black, maxLines = 1)
    }
}


@Composable
private fun HeroGachaPanel(
    session: PlaySessionState,
    lastRecruitment: HeroRecruitmentResult?,
    onRecruit: () -> Unit,
) {
    val canRecruit = session.gold >= HeroGacha.RECRUIT_COST
    val recruitButtonLabel = if (canRecruit) {
        stringResource(R.string.recruit_action)
    } else {
        stringResource(R.string.recruit_locked_action)
    }
    DarkPanel(
        title = stringResource(R.string.recruitment_title),
        body = stringResource(R.string.recruitment_summary, HeroGacha.RECRUIT_COST),
    ) {
        Button(
            onClick = onRecruit,
            enabled = canRecruit,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F695C)),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(recruitButtonLabel, fontWeight = FontWeight.Black)
        }
    }
    if (lastRecruitment != null) {
        RecruitReveal(recruitment = lastRecruitment)
    }
    Text(
        text = stringResource(R.string.recruit_odds_title),
        color = Color(0xFFFFF0BD),
        fontSize = 16.sp,
        fontWeight = FontWeight.Black,
        modifier = Modifier.padding(top = 2.dp, bottom = 8.dp),
    )
    HeroGacha.rarityWeights.forEach { (rarity, weight) ->
        InfoRow(
            title = heroRarityLabel(rarity),
            detail = stringResource(R.string.recruit_odds_detail),
            value = "$weight%",
        )
    }
}

@Composable
private fun HeroDetailDialog(
    session: PlaySessionState,
    hero: Hero,
    onDismiss: () -> Unit,
    onEquip: (LootItem) -> Unit,
    onUnequip: (LootSlot) -> Unit,
    onReleaseHero: (String) -> Unit,
) {
    val equipment = session.equippedItems(hero.id).associateBy { it.slot }
    var selectedSlot by remember(hero.id) { mutableStateOf<LootSlot?>(null) }
    val selectedPickerSlot = selectedSlot
    val canRelease = session.phase == PlayPhase.Idle && session.heroes.size > 1

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xF4141512))
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 10.dp, vertical = 8.dp),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                HeroDetailHeader(
                    hero = hero,
                    session = session,
                    canRelease = canRelease,
                    onDismiss = onDismiss,
                    onRelease = { onReleaseHero(hero.id) },
                )
                Spacer(Modifier.height(8.dp))
                HeroPortraitWithEquipment(
                    hero = hero,
                    equipment = equipment,
                    selectedSlot = selectedSlot,
                    onChoose = { selectedSlot = it },
                    onUnequip = onUnequip,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                )
                Spacer(Modifier.height(8.dp))
                HeroDetailStatsGrid(
                    hero = hero,
                    equipment = equipment,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(238.dp),
                )
            }
            if (selectedPickerSlot != null) {
                HeroEquipmentPickerOverlay(
                    slot = selectedPickerSlot,
                    items = session.lootItems
                        .filter { it.slot == selectedPickerSlot }
                        .sortedWith(compareByDescending<LootItem> { it.bonus }.thenBy { it.name }),
                    onEquip = { item ->
                        onEquip(item)
                        selectedSlot = null
                    },
                    onClose = { selectedSlot = null },
                )
            }
        }
    }
}

@Composable
private fun HeroDetailHeader(
    hero: Hero,
    session: PlaySessionState,
    canRelease: Boolean,
    onDismiss: () -> Unit,
    onRelease: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = hero.name,
                    color = Color(0xFFFFF1C0),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${heroRarityLabel(hero.rarity)} - ${heroClassLabel(hero.heroClass)} - ${heroTraitLabel(hero.trait)}",
                    color = Color(0xFFDED0A2),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = stringResource(R.string.hero_power_value, PartyPowerCalculator.basePower(hero) + session.equipmentBonus(hero.id)),
                color = Color(0xFFFFF1C0),
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp),
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
        ) {
            Button(
                onClick = onRelease,
                enabled = canRelease,
                modifier = Modifier
                    .width(122.dp)
                    .height(36.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6E3828),
                    contentColor = Color(0xFFFFF1C0),
                    disabledContainerColor = Color(0x8847332A),
                    disabledContentColor = Color(0x99FFF1C0),
                ),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(stringResource(R.string.hero_release_action), fontSize = 11.sp, fontWeight = FontWeight.Black, maxLines = 1)
            }
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .width(96.dp)
                    .height(36.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A5A2C)),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(stringResource(R.string.close_action), fontSize = 11.sp, fontWeight = FontWeight.Black, maxLines = 1)
            }
        }
    }
}

@Composable
private fun HeroPortraitWithEquipment(
    hero: Hero,
    equipment: Map<LootSlot, LootItem>,
    selectedSlot: LootSlot?,
    onChoose: (LootSlot) -> Unit,
    onUnequip: (LootSlot) -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderStyle = heroBorderStyle(hero.rarity)
    val shape = RoundedCornerShape(12.dp)

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        val portraitSize = maxWidth.coerceAtMost(maxHeight).coerceAtMost(430.dp)
        Box(
            modifier = Modifier
                .size(portraitSize)
                .clip(shape)
                .background(Color(0xFF171813))
                .border(borderStyle.strokeWidth, borderStyle.borderColor, shape),
        ) {
            Image(
                painter = painterResource(heroPortraitResource(hero)),
                contentDescription = hero.name,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop,
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color(0xB8141512))
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.hero_level_value, hero.level),
                    color = Color(0xFFFFF0BD),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                )
            }
            HeroOverlayEquipmentSlot(
                slot = LootSlot.Headgear,
                item = equipment[LootSlot.Headgear],
                selected = selectedSlot == LootSlot.Headgear,
                onChoose = { onChoose(LootSlot.Headgear) },
                onUnequip = { onUnequip(LootSlot.Headgear) },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 10.dp),
            )
            HeroOverlayEquipmentSlot(
                slot = LootSlot.Armor,
                item = equipment[LootSlot.Armor],
                selected = selectedSlot == LootSlot.Armor,
                onChoose = { onChoose(LootSlot.Armor) },
                onUnequip = { onUnequip(LootSlot.Armor) },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 10.dp),
            )
            HeroOverlayEquipmentSlot(
                slot = LootSlot.Consumable,
                item = equipment[LootSlot.Consumable],
                selected = selectedSlot == LootSlot.Consumable,
                onChoose = { onChoose(LootSlot.Consumable) },
                onUnequip = { onUnequip(LootSlot.Consumable) },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 10.dp),
            )
            HeroOverlayEquipmentSlot(
                slot = LootSlot.Weapon,
                item = equipment[LootSlot.Weapon],
                selected = selectedSlot == LootSlot.Weapon,
                onChoose = { onChoose(LootSlot.Weapon) },
                onUnequip = { onUnequip(LootSlot.Weapon) },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 12.dp, bottom = 34.dp),
            )
            HeroOverlayEquipmentSlot(
                slot = LootSlot.Trinket,
                item = equipment[LootSlot.Trinket],
                selected = selectedSlot == LootSlot.Trinket,
                onChoose = { onChoose(LootSlot.Trinket) },
                onUnequip = { onUnequip(LootSlot.Trinket) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 12.dp, bottom = 34.dp),
            )
        }
    }
}

@Composable
private fun HeroOverlayEquipmentSlot(
    slot: LootSlot,
    item: LootItem?,
    selected: Boolean,
    onChoose: () -> Unit,
    onUnequip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderStyle = item?.rarity?.let(::lootBorderStyle)
    val shape = RoundedCornerShape(11.dp)
    val borderColor = when {
        selected -> Color(0xFFFFF0BD)
        borderStyle != null -> borderStyle.borderColor
        else -> Color(0xFFE0C66F)
    }
    val iconResource = item?.let { lootIconResource(it.icon) } ?: emptySlotIconResource(slot)

    Box(
        modifier = modifier
            .size(62.dp)
            .clip(shape)
            .background(if (item == null) Color(0xDDF6E6B7) else Color(0xF2FFF1BF))
            .border(if (selected) 3.dp else 2.dp, borderColor, shape)
            .clickable(onClick = if (item == null) onChoose else onUnequip)
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(iconResource),
            contentDescription = lootSlotLabel(slot),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
            alpha = if (item == null) 0.72f else 1f,
        )
        if (item != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .clip(RoundedCornerShape(99.dp))
                    .background(Color(0xDD171813))
                    .padding(horizontal = 5.dp, vertical = 2.dp),
            ) {
                Text(
                    text = stringResource(R.string.equipment_power_value, item.bonus),
                    color = Color(0xFFFFF0BD),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun HeroArtworkPanel(hero: Hero, modifier: Modifier = Modifier) {
    val borderStyle = heroBorderStyle(hero.rarity)
    val shape = RoundedCornerShape(10.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .background(Color(0xFF171813))
            .border(borderStyle.strokeWidth, borderStyle.borderColor, shape),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(heroPortraitResource(hero)),
            contentDescription = hero.name,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color(0xCC171813))
                .padding(horizontal = 8.dp, vertical = 5.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.hero_level_value, hero.level),
                color = Color(0xFFFFF0BD),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun HeroDetailStatsGrid(hero: Hero, equipment: Map<LootSlot, LootItem>, modifier: Modifier = Modifier) {
    val leftColumn = listOf(StatType.Force, StatType.Magic, StatType.Luck, StatType.Ego, StatType.Endurance)
    val rightColumn = listOf(StatType.Charisma, StatType.Hygiene, StatType.BadFaith, StatType.Orientation, StatType.Paperwork)

    Box(
        modifier = modifier.clip(RoundedCornerShape(10.dp)),
    ) {
        Image(
            painter = painterResource(R.drawable.hero_stats_notice_board),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.FillBounds,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 42.dp, top = 64.dp, end = 42.dp, bottom = 36.dp),
        ) {
            Text(
                text = stringResource(R.string.hero_stats_title),
                color = Color(0xFF2B2418),
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(bottom = 4.dp),
            )
            leftColumn.indices.forEach { index ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .padding(bottom = if (index == leftColumn.lastIndex) 0.dp else 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(9.dp),
                ) {
                    listOf(leftColumn[index], rightColumn[index]).forEach { statType ->
                        val gearBonus = equipment.values.sumOf { item -> item.stats.filter { it.type == statType }.sumOf { it.value } }
                        HeroDetailStatLine(
                            label = statTypeLabel(statType),
                            value = hero.stats.valueOf(statType) + gearBonus,
                            bonus = gearBonus,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroDetailStatLine(label: String, value: Int, bonus: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(5.dp))
            .background(Color(0x80FFF1BF))
            .border(1.dp, Color(0x667B5531), RoundedCornerShape(5.dp))
            .padding(horizontal = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = Color(0xFF6A5737),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = if (bonus > 0) "$value +$bonus" else value.toString(),
            color = Color(0xFF211F1A),
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            maxLines = 1,
        )
    }
}

private fun emptySlotIconResource(slot: LootSlot): Int =
    when (slot) {
        LootSlot.Weapon -> R.drawable.loot_icon_weapon
        LootSlot.Armor -> R.drawable.ic_slot_armor
        LootSlot.Trinket -> R.drawable.loot_icon_ring
        LootSlot.Headgear -> R.drawable.loot_icon_helmet
        LootSlot.Consumable -> R.drawable.loot_icon_potion
    }
@Composable
private fun HeroEquipmentPickerOverlay(
    slot: LootSlot,
    items: List<LootItem>,
    onEquip: (LootItem) -> Unit,
    onClose: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x99141512))
            .padding(10.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(248.dp),
            shape = RoundedCornerShape(9.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF24251F)),
            border = BorderStroke(1.dp, Color(0xFFE0C66F)),
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.equipment_picker_title, lootSlotLabel(slot)),
                        color = Color(0xFFFFF1C0),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Button(
                        onClick = onClose,
                        modifier = Modifier
                            .width(78.dp)
                            .height(32.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B5531)),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text(stringResource(R.string.close_action), fontSize = 10.sp, fontWeight = FontWeight.Black, maxLines = 1)
                    }
                }
                Spacer(Modifier.height(8.dp))
                if (items.isEmpty()) {
                    Text(
                        text = stringResource(R.string.equipment_picker_empty),
                        color = Color(0xFFDED0A2),
                        fontSize = 13.sp,
                        lineHeight = 17.sp,
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                    ) {
                        items.forEach { item ->
                            LootInventoryRow(item = item, selected = false, onClick = { onEquip(item) })
                        }
                    }
                }
            }
        }
    }
}

private fun heroRosterComparator(sortOrder: HeroRosterSort): Comparator<Hero> =
    when (sortOrder) {
        HeroRosterSort.Rarity -> compareByDescending<Hero> { it.rarity.ordinal }.thenBy { it.name }
        HeroRosterSort.Name -> compareBy { it.name }
    }
@Composable
private fun HeroEquipmentPanel(
    session: PlaySessionState,
    hero: Hero,
    onEquip: (LootItem) -> Unit,
    onUnequip: (LootSlot) -> Unit,
) {
    val equipment = session.equippedItems(hero.id).associateBy { it.slot }
    val basePower = PartyPowerCalculator.basePower(hero)
    val bonus = session.equipmentBonus(hero.id)
    var selectedSlot by remember(hero.id) { mutableStateOf<LootSlot?>(null) }

    DarkPanel(
        title = stringResource(R.string.equipment_title, hero.name),
        body = stringResource(
            R.string.hero_detail_summary,
            heroRarityLabel(hero.rarity),
            heroClassLabel(hero.heroClass),
            basePower,
            bonus,
        ),
    ) {
        HeroStatsGrid(hero = hero, equipment = equipment)
        enumValues<LootSlot>().forEach { slot ->
            EquipmentSlotRow(
                slot = slot,
                item = equipment[slot],
                selected = selectedSlot == slot,
                onSelect = { selectedSlot = if (selectedSlot == slot) null else slot },
                onUnequip = { onUnequip(slot) },
            )
            if (selectedSlot == slot) {
                EquipmentSlotPicker(
                    slot = slot,
                    items = session.lootItems
                        .filter { it.slot == slot }
                        .sortedWith(compareByDescending<LootItem> { it.bonus }.thenBy { it.name }),
                    onEquip = { item ->
                        onEquip(item)
                        selectedSlot = null
                    },
                    onClose = { selectedSlot = null },
                )
            }
        }
    }
}

@Composable
private fun HeroStatsGrid(hero: Hero, equipment: Map<LootSlot, LootItem>) {
    Text(
        text = stringResource(R.string.hero_stats_title),
        color = Color(0xFFFFF0BD),
        fontSize = 16.sp,
        fontWeight = FontWeight.Black,
        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
    )
    val leftColumn = listOf(
        StatType.Force,
        StatType.Magic,
        StatType.Luck,
        StatType.Ego,
        StatType.Endurance,
    )
    val rightColumn = listOf(
        StatType.Charisma,
        StatType.Hygiene,
        StatType.BadFaith,
        StatType.Orientation,
        StatType.Paperwork,
    )
    leftColumn.indices.forEach { index ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOf(leftColumn[index], rightColumn[index]).forEach { statType ->
                val gearBonus = equipment.values.sumOf { item -> item.stats.filter { it.type == statType }.sumOf { it.value } }
                HeroStatChip(
                    label = statTypeLabel(statType),
                    value = hero.stats.valueOf(statType) + gearBonus,
                    bonus = gearBonus,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun HeroStatChip(label: String, value: Int, bonus: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .height(44.dp)
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xF4FFF1BF)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = label, color = Color(0xFF756B54), fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(
                text = if (bonus > 0) "$value (+$bonus)" else value.toString(),
                color = Color(0xFF211F1A),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1,
            )
        }
    }
}
@Composable
private fun EquipmentSlotRow(
    slot: LootSlot,
    item: LootItem?,
    selected: Boolean,
    onSelect: () -> Unit,
    onUnequip: () -> Unit,
) {
    val borderStyle = item?.rarity?.let(::lootBorderStyle)
    val shape = RoundedCornerShape(8.dp)
    val containerColor = when {
        selected && borderStyle != null -> borderStyle.selectedSurfaceColor
        selected -> Color(0xFFFFEBA8)
        borderStyle != null -> borderStyle.surfaceColor
        else -> Color(0xF4FFF1BF)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .clip(shape)
            .clickable(onClick = onSelect),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = borderStyle?.let { BorderStroke(it.strokeWidth, it.borderColor) },
    ) {
        Row(
            modifier = Modifier.padding(9.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = lootSlotLabel(slot), color = Color(0xFF211F1A), fontWeight = FontWeight.Black, maxLines = 1)
                Text(
                    text = item?.let { lootItemName(it) } ?: stringResource(R.string.equipment_empty_slot),
                    color = Color(0xFF756B54),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = item?.let { stringResource(R.string.equipment_power_value, it.bonus) } ?: "-",
                color = Color(0xFF211F1A),
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = 8.dp),
            )
            Button(
                onClick = if (item == null) onSelect else onUnequip,
                modifier = Modifier.width(96.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (item == null) Color(0xFFDCC86F) else Color(0xFF2F695C),
                    contentColor = if (item == null) Color(0xFF211F1A) else Color(0xFFF8F1D8),
                ),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    text = if (item == null) stringResource(R.string.choose_action) else stringResource(R.string.unequip_action),
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun EquipmentSlotPicker(
    slot: LootSlot,
    items: List<LootItem>,
    onEquip: (LootItem) -> Unit,
    onClose: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.equipment_picker_title, lootSlotLabel(slot)),
                color = Color(0xFFFFF1C0),
                fontWeight = FontWeight.Black,
                modifier = Modifier.weight(1f),
            )
            Button(
                onClick = onClose,
                modifier = Modifier.width(86.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B5531)),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(stringResource(R.string.close_action), fontWeight = FontWeight.Black, fontSize = 12.sp, maxLines = 1)
            }
        }
        if (items.isEmpty()) {
            Text(
                text = stringResource(R.string.equipment_picker_empty),
                color = Color(0xFFDED0A2),
                fontSize = 13.sp,
                lineHeight = 17.sp,
                modifier = Modifier.padding(top = 8.dp),
            )
        } else {
            items.forEach { item ->
                LootInventoryRow(
                    item = item,
                    selected = false,
                    onClick = { onEquip(item) },
                )
            }
        }
    }
}
@Composable
private fun RecruitReveal(recruitment: HeroRecruitmentResult) {
    val hero = recruitment.hero
    val body = if (recruitment.duplicate) {
        stringResource(R.string.recruit_duplicate_summary, hero.name, recruitment.reputationReward)
    } else {
        stringResource(
            R.string.recruit_result_summary,
            hero.name,
            heroRarityLabel(hero.rarity),
            heroClassLabel(hero.heroClass),
            heroTraitLabel(hero.trait),
        )
    }
    val visibleState = remember(recruitment) {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }

    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn(animationSpec = tween(220)) + scaleIn(
            initialScale = 0.95f,
            animationSpec = tween(220),
        ),
        exit = fadeOut(animationSpec = tween(120)) + scaleOut(
            targetScale = 0.98f,
            animationSpec = tween(120),
        ),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            HeroArtworkPanel(
                hero = hero,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .padding(bottom = 10.dp),
            )
            PaperPanel(
                title = stringResource(R.string.recruit_result_title),
                body = body,
            ) {
                if (recruitment.duplicate) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.recruit_duplicate_bonus_detail, recruitment.reputationReward),
                        color = Color(0xFF756B54),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun QuestCardArt(bannerResourceId: Int, frameResourceId: Int, borderStyle: RarityBorderStyle) {
    val shape = RoundedCornerShape(8.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .aspectRatio(4f)
            .clip(shape)
            .background(Color(0xFF171813))
            .border(borderStyle.strokeWidth, borderStyle.borderColor, shape),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(bannerResourceId),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.FillBounds,
        )
        Image(
            painter = painterResource(frameResourceId),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.FillBounds,
        )
    }
}

@Composable
private fun HeroPortraitGrid(
    heroes: List<Hero>,
    selectedHeroId: String,
    onSelectHero: (String) -> Unit,
) {
    val visibleHeroes = heroes.ifEmpty { HeroCatalog.starterHeroes }
    Column(modifier = Modifier.padding(bottom = 10.dp)) {
        visibleHeroes.chunked(3).forEach { rowHeroes ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rowHeroes.forEach { hero ->
                    HeroPortraitTile(
                        hero = hero,
                        selected = hero.id == selectedHeroId,
                        onClick = { onSelectHero(hero.id) },
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(3 - rowHeroes.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun HeroPortraitTile(
    hero: Hero,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderStyle = heroBorderStyle(hero.rarity)
    val shape = RoundedCornerShape(10.dp)
    val portraitShape = RoundedCornerShape(8.dp)
    val backingColor = if (selected) borderStyle.selectedSurfaceColor else Color(0xDD171813)
    val strokeWidth = if (selected) borderStyle.strokeWidth + 1.dp else borderStyle.strokeWidth

    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(shape)
                .background(backingColor)
                .border(strokeWidth, borderStyle.borderColor, shape)
                .padding(4.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(heroPortraitResource(hero)),
                contentDescription = hero.name,
                modifier = Modifier
                    .matchParentSize()
                    .clip(portraitShape)
                    .border(1.dp, borderStyle.innerColor, portraitShape),
                contentScale = ContentScale.Crop,
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(5.dp)
                    .width(34.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(borderStyle.borderColor),
            )
        }
        Text(
            text = hero.name,
            color = Color(0xFFFFF0BD),
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
        )
    }
}

private fun heroPortraitResource(hero: Hero): Int =
    when (hero.id) {
        "brugg", "darrik", "orla" -> R.drawable.hero_portrait_brugg
        "mira", "pippa" -> R.drawable.hero_portrait_mira
        "nell", "vex" -> R.drawable.hero_portrait_nell
        "quill", "ledger" -> R.drawable.hero_portrait_quill
        "sable" -> R.drawable.hero_portrait_ninja
        "bramble" -> R.drawable.hero_portrait_hunter
        "pax" -> R.drawable.hero_portrait_priest
        "morrow" -> R.drawable.hero_portrait_necromancer
        "paladin" -> R.drawable.hero_portrait_paladin
        "comptable" -> R.drawable.hero_portrait_comptable
        "jardinier" -> R.drawable.hero_portrait_jardinier
        "chevalier_de_la_mort" -> R.drawable.hero_portrait_chevalier_de_la_mort
        "chef_cuistot" -> R.drawable.hero_portrait_chef_cuistot
        "expert_en_demolition" -> R.drawable.hero_portrait_expert_en_demolition
        "elementaire_de_sel" -> R.drawable.hero_portrait_elementaire_de_sel
        "troll_stupide" -> R.drawable.hero_portrait_troll_stupide
        else -> when (hero.heroClass) {
            HeroClass.Bruiser -> R.drawable.hero_portrait_brugg
            HeroClass.ApprenticeMage -> R.drawable.hero_portrait_mira
            HeroClass.Rogueish -> R.drawable.hero_portrait_nell
            HeroClass.BardAccountant -> R.drawable.hero_portrait_quill
            HeroClass.Ninja -> R.drawable.hero_portrait_ninja
            HeroClass.Hunter -> R.drawable.hero_portrait_hunter
            HeroClass.Priest -> R.drawable.hero_portrait_priest
            HeroClass.Necromancer -> R.drawable.hero_portrait_necromancer
            HeroClass.Paladin -> R.drawable.hero_portrait_paladin
            HeroClass.Accountant -> R.drawable.hero_portrait_comptable
            HeroClass.Gardener -> R.drawable.hero_portrait_jardinier
            HeroClass.DeathKnight -> R.drawable.hero_portrait_chevalier_de_la_mort
            HeroClass.Chef -> R.drawable.hero_portrait_chef_cuistot
            HeroClass.DemolitionExpert -> R.drawable.hero_portrait_expert_en_demolition
            HeroClass.SaltElemental -> R.drawable.hero_portrait_elementaire_de_sel
            HeroClass.StupidTroll -> R.drawable.hero_portrait_troll_stupide
        }
    }

@Composable
private fun RewardLootScreen(
    session: PlaySessionState,
    onKeep: (LootItem) -> Unit,
    onSell: (LootItem) -> Unit,
    onDone: () -> Unit,
) {
    val pendingRewards = session.pendingLootItems.asReversed()
    var selectedIndex by remember(pendingRewards.size) { mutableStateOf(0) }
    val selectedItem = pendingRewards.getOrNull(selectedIndex)

    ScreenScaffold(
        title = stringResource(R.string.loot_reward_title),
        status = stringResource(R.string.loot_reward_status, pendingRewards.size),
    ) {
        if (selectedItem == null) {
            DarkPanel(title = stringResource(R.string.loot_rewards_done_title), body = stringResource(R.string.loot_rewards_done_summary)) {
                Button(
                    onClick = onDone,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F695C)),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(stringResource(R.string.guild_home_title), fontWeight = FontWeight.Black)
                }
            }
            return@ScreenScaffold
        }

        val selectedItemName = lootItemName(selectedItem)
        val sellValue = LootEconomy.sellValue(selectedItem)
        LootIconPanel(item = selectedItem, contentDescription = selectedItemName)
        InfoRow(
            title = stringResource(R.string.sell_value_title),
            detail = stringResource(R.string.sell_value_detail),
            value = stringResource(R.string.gold_value, sellValue),
        )
        DarkPanel(
            title = selectedItemName,
            body = lootRewardChoiceSummary(selectedItem, pendingRewards.size, sellValue),
        ) {
            ActionRow(
                primaryLabel = stringResource(R.string.keep_action),
                secondaryLabel = stringResource(R.string.sell_action),
                onPrimary = { onKeep(selectedItem) },
                onSecondary = { onSell(selectedItem) },
            )
        }

        Text(
            text = stringResource(R.string.loot_reward_title),
            color = Color(0xFFFFF0BD),
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(top = 2.dp, bottom = 8.dp),
        )
        pendingRewards.forEachIndexed { index, item ->
            LootInventoryRow(
                item = item,
                selected = index == selectedIndex,
                onClick = { selectedIndex = index },
            )
        }
    }
}

@Composable
private fun LootScreen(
    session: PlaySessionState,
    onSellInventory: (LootItem) -> Unit,
    onEquip: (String, LootItem) -> Unit,
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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F695C)),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(stringResource(R.string.equip_action), fontWeight = FontWeight.Black)
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
private fun LootInventoryRow(item: LootItem, selected: Boolean, onClick: () -> Unit) {
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
                    painter = painterResource(lootIconResource(item.icon)),
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
private fun PerfectLootBadge() {
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

@Composable
private fun UpgradesScreen(session: PlaySessionState, onBuy: () -> Unit) {
    val canBuyNoticeBoard = session.gold >= 600

    ScreenScaffold(title = stringResource(R.string.upgrades_title), status = stringResource(R.string.guild_upgrade_status)) {
        InfoRow(
            title = stringResource(R.string.notice_board_upgrade_title, session.noticeBoardLevel),
            detail = stringResource(R.string.notice_board_upgrade_detail),
            value = "600g",
        )
        InfoRow(title = "Training Rug Lv. 1", detail = "+5% hero XP", value = "420g")
        InfoRow(title = "Accountant Stool Lv. 1", detail = "-3% mysterious losses", value = "500g")
        DarkPanel(title = stringResource(R.string.next_unlock_title), body = stringResource(R.string.next_unlock_summary)) {
            Button(
                onClick = onBuy,
                enabled = canBuyNoticeBoard,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F695C)),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(stringResource(R.string.buy_recommended_action), fontWeight = FontWeight.Black)
            }
        }
    }
}

private fun heroBorderStyle(rarity: HeroRarity): RarityBorderStyle =
    when (rarity) {
        HeroRarity.Common -> rarityBorderStyle(
            border = Color(0xFFAA8B53),
            inner = Color(0xFFE1C37A),
            surface = Color(0xF4F4E2AE),
        )
        HeroRarity.Uncommon -> rarityBorderStyle(
            border = Color(0xFF5E9B67),
            inner = Color(0xFF98D17B),
            surface = Color(0xF4DDEDBC),
        )
        HeroRarity.Rare -> rarityBorderStyle(
            border = Color(0xFF4B8BC6),
            inner = Color(0xFF8BC6EA),
            surface = Color(0xF4D7E9D8),
        )
        HeroRarity.Epic -> rarityBorderStyle(
            border = Color(0xFFAF6BCB),
            inner = Color(0xFFDFA7F0),
            surface = Color(0xF4EAD8D7),
            strokeWidth = 3.dp,
        )
        HeroRarity.Legendary -> rarityBorderStyle(
            border = Color(0xFFE1B94F),
            inner = Color(0xFFFFE08A),
            surface = Color(0xF4F7E5A9),
            strokeWidth = 3.dp,
        )
    }

private fun lootBorderStyle(rarity: LootRarity): RarityBorderStyle =
    when (rarity) {
        LootRarity.Common -> rarityBorderStyle(
            border = Color(0xFF9A8356),
            inner = Color(0xFFD2B978),
            surface = Color(0xF4F2DFB0),
        )
        LootRarity.Uncommon -> rarityBorderStyle(
            border = Color(0xFF4F8D63),
            inner = Color(0xFF96CF83),
            surface = Color(0xF4DCE9B8),
        )
        LootRarity.Rare -> rarityBorderStyle(
            border = Color(0xFF447EB5),
            inner = Color(0xFF8CCAF0),
            surface = Color(0xF4D7E8D6),
        )
        LootRarity.Epic -> rarityBorderStyle(
            border = Color(0xFF9E62B8),
            inner = Color(0xFFDCA1EF),
            surface = Color(0xF4E8D6D6),
            strokeWidth = 3.dp,
        )
        LootRarity.Relic -> rarityBorderStyle(
            border = Color(0xFF42BDA7),
            inner = Color(0xFF9AF1DE),
            surface = Color(0xF4D4ECE0),
            strokeWidth = 3.dp,
        )
    }

private fun questDifficultyBorderStyle(difficulty: Int): RarityBorderStyle =
    when {
        difficulty < 100 -> heroBorderStyle(HeroRarity.Common)
        difficulty < 150 -> heroBorderStyle(HeroRarity.Uncommon)
        difficulty < 210 -> heroBorderStyle(HeroRarity.Rare)
        difficulty < 280 -> heroBorderStyle(HeroRarity.Epic)
        else -> heroBorderStyle(HeroRarity.Legendary)
    }

private fun questFrameResource(difficulty: Int): Int =
    when {
        difficulty < 100 -> R.drawable.quest_frame_oak
        difficulty < 210 -> R.drawable.quest_frame_brass
        else -> R.drawable.quest_frame_moss
    }

private fun questBannerResource(quest: Quest): Int =
    when (quest.id) {
        "cave_minor_regrets" -> R.drawable.quest_banner_cave_minor_regrets
        else -> R.drawable.quest_banner_bandit_tax_office
    }

private fun rarityBorderStyle(
    border: Color,
    inner: Color,
    surface: Color,
    strokeWidth: Dp = 2.dp,
): RarityBorderStyle = RarityBorderStyle(
    borderColor = border,
    innerColor = inner,
    surfaceColor = surface,
    selectedSurfaceColor = surface.copy(alpha = 1f),
    strokeWidth = strokeWidth,
)
@Composable
private fun phaseStatus(phase: PlayPhase): String =
    when (phase) {
        PlayPhase.Idle -> stringResource(R.string.quest_ready_status)
        PlayPhase.Running -> stringResource(R.string.quest_running_status)
        PlayPhase.ResultReady -> stringResource(R.string.quest_result_status)
    }

@Composable
private fun outcomeLabel(outcome: ExpeditionOutcome): String =
    when (outcome) {
        ExpeditionOutcome.GreatSuccess -> stringResource(R.string.outcome_great_success)
        ExpeditionOutcome.Success -> stringResource(R.string.outcome_success)
        ExpeditionOutcome.PartialSuccess -> stringResource(R.string.outcome_partial_success)
        ExpeditionOutcome.Failure -> stringResource(R.string.outcome_failure)
        ExpeditionOutcome.RidiculousFailure -> stringResource(R.string.outcome_ridiculous_failure)
    }

@Composable
private fun riskLabel(risk: QuestRisk): String =
    when (risk) {
        QuestRisk.Low -> stringResource(R.string.risk_low)
        QuestRisk.Medium -> stringResource(R.string.risk_medium)
        QuestRisk.High -> stringResource(R.string.risk_high)
    }

@Composable
private fun resultIncidentText(outcome: ExpeditionOutcome): String =
    when (outcome) {
        ExpeditionOutcome.GreatSuccess -> stringResource(R.string.result_incident_great_success)
        ExpeditionOutcome.Success -> stringResource(R.string.result_incident_success)
        ExpeditionOutcome.PartialSuccess -> stringResource(R.string.result_incident_partial_success)
        ExpeditionOutcome.Failure -> stringResource(R.string.result_incident_failure)
        ExpeditionOutcome.RidiculousFailure -> stringResource(R.string.result_incident_ridiculous_failure)
    }
@Composable
private fun heroRosterDetail(hero: Hero): String =
    stringResource(
        R.string.hero_roster_detail,
        heroRarityLabel(hero.rarity),
        heroClassLabel(hero.heroClass),
        heroTraitLabel(hero.trait),
    )

@Composable
private fun heroRarityLabel(rarity: HeroRarity): String =
    when (rarity) {
        HeroRarity.Common -> stringResource(R.string.rarity_common)
        HeroRarity.Uncommon -> stringResource(R.string.rarity_uncommon)
        HeroRarity.Rare -> stringResource(R.string.rarity_rare)
        HeroRarity.Epic -> stringResource(R.string.rarity_epic)
        HeroRarity.Legendary -> stringResource(R.string.rarity_legendary)
    }

@Composable
private fun heroClassLabel(heroClass: HeroClass): String =
    when (heroClass) {
        HeroClass.Bruiser -> stringResource(R.string.class_bruiser)
        HeroClass.ApprenticeMage -> stringResource(R.string.class_apprentice_mage)
        HeroClass.Rogueish -> stringResource(R.string.class_rogueish)
        HeroClass.BardAccountant -> stringResource(R.string.class_bard_accountant)
        HeroClass.Ninja -> stringResource(R.string.class_ninja)
        HeroClass.Hunter -> stringResource(R.string.class_hunter)
        HeroClass.Priest -> stringResource(R.string.class_priest)
        HeroClass.Necromancer -> stringResource(R.string.class_necromancer)
        HeroClass.Paladin -> stringResource(R.string.class_paladin)
        HeroClass.Accountant -> stringResource(R.string.class_accountant)
        HeroClass.Gardener -> stringResource(R.string.class_gardener)
        HeroClass.DeathKnight -> stringResource(R.string.class_death_knight)
        HeroClass.Chef -> stringResource(R.string.class_chef)
        HeroClass.DemolitionExpert -> stringResource(R.string.class_demolition_expert)
        HeroClass.SaltElemental -> stringResource(R.string.class_salt_elemental)
        HeroClass.StupidTroll -> stringResource(R.string.class_stupid_troll)
    }

@Composable
private fun heroTraitLabel(trait: Trait): String =
    when (trait) {
        Trait.Overconfident -> stringResource(R.string.trait_overconfident)
        Trait.ReadsManual -> stringResource(R.string.trait_reads_manual)
        Trait.SuspiciouslyLucky -> stringResource(R.string.trait_suspiciously_lucky)
        Trait.PainfullyOrganized -> stringResource(R.string.trait_painfully_organized)
    }

@Composable
private fun LootIconPanel(item: LootItem, contentDescription: String) {
    val borderStyle = lootBorderStyle(item.rarity)
    val shape = RoundedCornerShape(8.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(bottom = 10.dp)
            .clip(shape)
            .background(Color(0xDD171813))
            .border(borderStyle.strokeWidth + 1.dp, borderStyle.borderColor, shape),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(borderStyle.innerColor.copy(alpha = 0.18f))
                .border(1.dp, borderStyle.innerColor, RoundedCornerShape(8.dp))
                .padding(10.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(lootIconResource(item.icon)),
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )
        }
    }
}

private fun lootIconResource(icon: LootIcon): Int =
    when (icon) {
        LootIcon.Boots -> R.drawable.loot_icon_boots
        LootIcon.Ring -> R.drawable.loot_icon_ring
        LootIcon.Helmet -> R.drawable.loot_icon_helmet
        LootIcon.Weapon -> R.drawable.loot_icon_weapon
        LootIcon.Spoon -> R.drawable.loot_icon_spoon
        LootIcon.Hood -> R.drawable.loot_icon_hood
        LootIcon.Tankard -> R.drawable.loot_icon_tankard
        LootIcon.Potion -> R.drawable.loot_icon_potion
    }

@Composable
private fun journalEntryText(entry: JournalEntry): String {
    return when {
        entry.id.startsWith("outcome-") -> journalOutcomeText(entry.id)
        entry.id.startsWith("hero-") -> journalHeroText(entry.id) ?: entry.text
        entry.id.startsWith("reward-") -> journalRewardText(entry.id) ?: entry.text
        entry.id == "fallback-empty-ledger" -> stringResource(R.string.journal_fallback_empty_ledger)
        else -> entry.text
    }
}

@Composable
private fun journalOutcomeText(id: String): String =
    when (id) {
        "outcome-greatsuccess" -> stringResource(R.string.journal_outcome_great_success)
        "outcome-success" -> stringResource(R.string.journal_outcome_success)
        "outcome-partialsuccess" -> stringResource(R.string.journal_outcome_partial_success)
        "outcome-failure" -> stringResource(R.string.journal_outcome_failure)
        "outcome-ridiculousfailure" -> stringResource(R.string.journal_outcome_ridiculous_failure)
        else -> id
    }

@Composable
private fun journalHeroText(id: String): String? {
    val heroId = id.removePrefix("hero-")
    val hero = com.ayoshy.badventurers.game.HeroCatalog.byId[heroId]?.toHero() ?: return null
    return when (hero.heroClass) {
        HeroClass.Bruiser -> stringResource(R.string.journal_hero_bruiser, hero.name)
        HeroClass.ApprenticeMage -> stringResource(R.string.journal_hero_apprentice_mage, hero.name)
        HeroClass.Rogueish -> stringResource(R.string.journal_hero_rogueish, hero.name)
        HeroClass.BardAccountant -> stringResource(R.string.journal_hero_bard_accountant, hero.name)
        HeroClass.Ninja -> stringResource(R.string.journal_hero_ninja, hero.name)
        HeroClass.Hunter -> stringResource(R.string.journal_hero_hunter, hero.name)
        HeroClass.Priest -> stringResource(R.string.journal_hero_priest, hero.name)
        HeroClass.Necromancer -> stringResource(R.string.journal_hero_necromancer, hero.name)
        HeroClass.Paladin -> stringResource(R.string.journal_hero_paladin, hero.name)
        HeroClass.Accountant -> stringResource(R.string.journal_hero_accountant, hero.name)
        HeroClass.Gardener -> stringResource(R.string.journal_hero_gardener, hero.name)
        HeroClass.DeathKnight -> stringResource(R.string.journal_hero_death_knight, hero.name)
        HeroClass.Chef -> stringResource(R.string.journal_hero_chef, hero.name)
        HeroClass.DemolitionExpert -> stringResource(R.string.journal_hero_demolition_expert, hero.name)
        HeroClass.SaltElemental -> stringResource(R.string.journal_hero_salt_elemental, hero.name)
        HeroClass.StupidTroll -> stringResource(R.string.journal_hero_stupid_troll, hero.name)
    }
}

@Composable
private fun journalRewardText(id: String): String? {
    val parts = id.removePrefix("reward-").split("-")
    val gold = parts.getOrNull(0)?.toIntOrNull() ?: return null
    val lootRolls = parts.getOrNull(1)?.toIntOrNull() ?: return null
    return if (lootRolls > 0) {
        stringResource(R.string.journal_reward_gold_and_loot, gold, lootRolls)
    } else {
        stringResource(R.string.journal_reward_gold, gold)
    }
}

@Composable
private fun lootItemName(item: LootItem): String =
    when (item.id) {
        "weapon_bent_spoon" -> stringResource(R.string.loot_name_weapon_bent_spoon)
        "weapon_fork_spear" -> stringResource(R.string.loot_name_weapon_fork_spear)
        "weapon_moon_axe" -> stringResource(R.string.loot_name_weapon_moon_axe)
        "weapon_nibblade" -> stringResource(R.string.loot_name_weapon_nibblade)
        "weapon_toast_mace" -> stringResource(R.string.loot_name_weapon_toast_mace)
        "armor_winged_boots" -> stringResource(R.string.loot_name_armor_winged_boots)
        "armor_travel_boots" -> stringResource(R.string.loot_name_armor_travel_boots)
        "armor_patch_hood" -> stringResource(R.string.loot_name_armor_patch_hood)
        "armor_moss_coat" -> stringResource(R.string.loot_name_armor_moss_coat)
        "armor_panic_helm" -> stringResource(R.string.loot_name_armor_panic_helm)
        "trinket_lucky_ring" -> stringResource(R.string.loot_name_trinket_lucky_ring)
        "trinket_pocket_ring" -> stringResource(R.string.loot_name_trinket_pocket_ring)
        "trinket_spare_ring" -> stringResource(R.string.loot_name_trinket_spare_ring)
        "trinket_dusty_ring" -> stringResource(R.string.loot_name_trinket_dusty_ring)
        "trinket_quiet_ring" -> stringResource(R.string.loot_name_trinket_quiet_ring)
        "headgear_soup_helm" -> stringResource(R.string.loot_name_headgear_soup_helm)
        "headgear_wobble_cap" -> stringResource(R.string.loot_name_headgear_wobble_cap)
        "headgear_paper_crown" -> stringResource(R.string.loot_name_headgear_paper_crown)
        "headgear_lantern_hat" -> stringResource(R.string.loot_name_headgear_lantern_hat)
        "headgear_grin_hood" -> stringResource(R.string.loot_name_headgear_grin_hood)
        "consumable_stale_potion" -> stringResource(R.string.loot_name_consumable_stale_potion)
        "consumable_brave_brew" -> stringResource(R.string.loot_name_consumable_brave_brew)
        "consumable_tiny_flask" -> stringResource(R.string.loot_name_consumable_tiny_flask)
        "consumable_odd_elixir" -> stringResource(R.string.loot_name_consumable_odd_elixir)
        "consumable_snap_tonic" -> stringResource(R.string.loot_name_consumable_snap_tonic)
        else -> item.name
    }

@Composable
private fun lootRarityLabel(rarity: LootRarity): String =
    when (rarity) {
        LootRarity.Common -> stringResource(R.string.loot_rarity_common)
        LootRarity.Uncommon -> stringResource(R.string.loot_rarity_uncommon)
        LootRarity.Rare -> stringResource(R.string.loot_rarity_rare)
        LootRarity.Epic -> stringResource(R.string.loot_rarity_epic)
        LootRarity.Relic -> stringResource(R.string.loot_rarity_relic)
    }

@Composable
private fun lootSlotLabel(slot: LootSlot): String =
    when (slot) {
        LootSlot.Weapon -> stringResource(R.string.loot_slot_weapon)
        LootSlot.Armor -> stringResource(R.string.loot_slot_armor)
        LootSlot.Trinket -> stringResource(R.string.loot_slot_trinket)
        LootSlot.Headgear -> stringResource(R.string.loot_slot_headgear)
        LootSlot.Consumable -> stringResource(R.string.loot_slot_consumable)
    }

@Composable
private fun lootItemSummary(item: LootItem, totalItems: Int): String =
    stringResource(
        R.string.loot_generated_summary,
        lootRarityLabel(item.rarity),
        lootSlotLabel(item.slot),
        item.bonus,
        totalItems,
    )


@Composable
private fun statTypeLabel(type: StatType): String =
    when (type) {
        StatType.Force -> stringResource(R.string.stat_force)
        StatType.Magic -> stringResource(R.string.stat_magic)
        StatType.Luck -> stringResource(R.string.stat_luck)
        StatType.Ego -> stringResource(R.string.stat_ego)
        StatType.Hygiene -> stringResource(R.string.stat_hygiene)
        StatType.BadFaith -> stringResource(R.string.stat_bad_faith)
        StatType.Orientation -> stringResource(R.string.stat_orientation)
        StatType.Paperwork -> stringResource(R.string.stat_paperwork)
        StatType.Endurance -> stringResource(R.string.stat_endurance)
        StatType.Charisma -> stringResource(R.string.stat_charisma)
    }

@Composable
private fun statTypeLabelMap(): Map<StatType, String> = mapOf(
    StatType.Force to stringResource(R.string.stat_force),
    StatType.Magic to stringResource(R.string.stat_magic),
    StatType.Luck to stringResource(R.string.stat_luck),
    StatType.Ego to stringResource(R.string.stat_ego),
    StatType.Hygiene to stringResource(R.string.stat_hygiene),
    StatType.BadFaith to stringResource(R.string.stat_bad_faith),
    StatType.Orientation to stringResource(R.string.stat_orientation),
    StatType.Paperwork to stringResource(R.string.stat_paperwork),
    StatType.Endurance to stringResource(R.string.stat_endurance),
    StatType.Charisma to stringResource(R.string.stat_charisma),
)
@Composable
private fun lootStatsSummary(item: LootItem): String {
    val statLine = if (item.stats.isEmpty()) {
        stringResource(R.string.loot_stats_legacy, item.bonus)
    } else {
        val labels = statTypeLabelMap()
        item.stats.joinToString(" / ") { stat -> "+${stat.value} ${labels.getValue(stat.type)}" }
    }
    return if (item.isPerfect) "$statLine - ${stringResource(R.string.loot_perfect_label)}" else statLine
}
@Composable
private fun lootRewardChoiceSummary(item: LootItem, pendingItems: Int, sellValue: Int): String =
    stringResource(
        R.string.loot_reward_choice_summary,
        lootRarityLabel(item.rarity),
        lootSlotLabel(item.slot),
        item.bonus,
        pendingItems,
        sellValue,
    ) + "\n" + lootStatsSummary(item)
@Composable
private fun lootKeepSellSummary(item: LootItem, totalItems: Int, sellValue: Int): String =
    stringResource(
        R.string.loot_keep_sell_summary,
        lootRarityLabel(item.rarity),
        lootSlotLabel(item.slot),
        item.bonus,
        totalItems,
        sellValue,
    ) + "\n" + lootStatsSummary(item)

private fun togglePartyHero(currentIds: List<String>, heroId: String, maxSlots: Int): List<String> {
    val normalized = currentIds.distinct()
    if (heroId in normalized) return normalized - heroId
    if (normalized.size >= maxSlots) return normalized
    return normalized + heroId
}
private data class EquipSuggestion(
    val hero: Hero,
    val gain: Int,
    val matchScore: Int,
)

private fun bestEquipSuggestion(session: PlaySessionState, item: LootItem): EquipSuggestion? {
    if (session.heroes.isEmpty()) return null

    return session.heroes
        .map { hero ->
            EquipSuggestion(
                hero = hero,
                gain = equipGainForHero(session, hero, item),
                matchScore = equipMatchScore(hero, item),
            )
        }
        .sortedWith(
            compareByDescending<EquipSuggestion> { it.gain }
                .thenByDescending { it.matchScore }
                .thenByDescending { PartyPowerCalculator.basePower(it.hero) }
                .thenBy { it.hero.name },
        )
        .firstOrNull()
}

private fun equipGainForHero(session: PlaySessionState, hero: Hero, item: LootItem): Int {
    val currentItem = session.equippedItems(hero.id).firstOrNull { it.slot == item.slot }
    return item.bonus - (currentItem?.bonus ?: 0)
}

private fun equipMatchScore(hero: Hero, item: LootItem): Int =
    item.stats.sumOf { stat -> stat.value * hero.stats.valueOf(stat.type) }

private fun formatSignedCount(value: Int): String = if (value >= 0) "+$value" else value.toString()

private fun remainingSeconds(session: PlaySessionState, nowMillis: Long): Long {
    val endsAt = session.expedition?.endsAtMillis ?: return 0L
    return ((endsAt - nowMillis + 999L) / 1000L).coerceAtLeast(0L)
}


private fun rewardGoldWithNoticeBoard(session: PlaySessionState): Int {
    val rewardGold = session.expedition?.result?.reward?.gold ?: return 0
    return rewardGold + rewardGold * (session.noticeBoardLevel - 1) / 10
}

private fun questBaseGoldWithNoticeBoard(session: PlaySessionState, quest: Quest): Int =
    quest.baseGold + quest.baseGold * (session.noticeBoardLevel - 1) / 10

private fun formatCount(value: Int): String = "%,d".format(value)

@Composable
private fun ScreenScaffold(title: String, status: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = title, color = Color(0xFFFFF0BD), fontSize = 19.sp, fontWeight = FontWeight.Black)
            Text(text = status, color = Color(0xFFE2CF93), fontSize = 12.sp, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.height(10.dp))
        content()
    }
}

@Composable
private fun DarkPanel(title: String, body: String, footer: @Composable ColumnScope.() -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xDD24251F)),
    ) {
        Column(modifier = Modifier.padding(11.dp)) {
            Text(text = title, color = Color(0xFFFFF1C0), fontWeight = FontWeight.Black)
            Spacer(Modifier.height(5.dp))
            Text(text = body, color = Color(0xFFDED0A2), fontSize = 13.sp, lineHeight = 17.sp)
            footer()
        }
    }
}

@Composable
private fun PaperPanel(title: String, body: String, footer: @Composable ColumnScope.() -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xF4FFF1BF)),
    ) {
        Column(modifier = Modifier.padding(11.dp)) {
            Text(text = title, color = Color(0xFF211F1A), fontWeight = FontWeight.Black)
            Spacer(Modifier.height(5.dp))
            Text(text = body, color = Color(0xFF756B54), fontSize = 13.sp, lineHeight = 17.sp)
            footer()
        }
    }
}

@Composable
private fun ActionRow(
    primaryLabel: String,
    secondaryLabel: String,
    onPrimary: () -> Unit,
    onSecondary: () -> Unit,
    primaryEnabled: Boolean = true,
    secondaryEnabled: Boolean = true,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Button(
            onClick = onPrimary,
            enabled = primaryEnabled,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F695C)),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(primaryLabel, fontWeight = FontWeight.Black)
        }
        Button(
            onClick = onSecondary,
            enabled = secondaryEnabled,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDEC777), contentColor = Color(0xFF211F1A)),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(secondaryLabel, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun ProgressBar(progress: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(9.dp)
            .padding(top = 8.dp)
            .clip(RoundedCornerShape(99.dp))
            .background(Color(0x6650472F)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .background(Color(0xFF2F695C)),
        )
    }
}

@Composable
private fun ArtSheet(resourceId: Int, aspectRatio: Float) {
    Image(
        painter = painterResource(resourceId),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF171813)),
        contentScale = ContentScale.Crop,
    )
    Spacer(Modifier.height(10.dp))
}

@Composable
private fun JournalLine(text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 7.dp),
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xF4FFF1BF)),
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(34.dp)
                    .background(Color(0xFFD0A24A)),
            )
            Spacer(Modifier.width(8.dp))
            Text(text = text, color = Color(0xFF493F2B), fontSize = 12.sp, lineHeight = 16.sp)
        }
    }
}

@Composable
private fun InfoRow(title: String, detail: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xF4FFF1BF)),
    ) {
        Row(
            modifier = Modifier.padding(9.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = Color(0xFF211F1A), fontWeight = FontWeight.Black, maxLines = 1)
                Text(
                    text = detail,
                    color = Color(0xFF756B54),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(text = value, color = Color(0xFF211F1A), fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun BottomBar(selectedTab: GameTab, onTabSelected: (GameTab) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color(0xEE151612))
            .padding(horizontal = 4.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        BottomTab(GameTab.Guild, selectedTab, "G", stringResource(R.string.guild_home_title), onTabSelected)
        BottomTab(GameTab.Quests, selectedTab, "Q", stringResource(R.string.quests_title), onTabSelected)
        BottomTab(GameTab.Heroes, selectedTab, "H", stringResource(R.string.heroes_title), onTabSelected)
        BottomTab(GameTab.Loot, selectedTab, "L", stringResource(R.string.loot_title), onTabSelected)
        BottomTab(GameTab.Upgrades, selectedTab, "U", stringResource(R.string.upgrades_title), onTabSelected)
    }
}

@Composable
private fun RowScope.BottomTab(
    tab: GameTab,
    selectedTab: GameTab,
    icon: String,
    label: String,
    onTabSelected: (GameTab) -> Unit,
) {
    val selected = tab == selectedTab
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onTabSelected(tab) }
            .background(if (selected) Color(0x552F695C) else Color.Transparent)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(if (selected) Color(0xFF2F695C) else Color(0x2AFFF1BF)),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = icon, color = Color(0xFFFFF0BD), fontSize = 11.sp, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            color = if (selected) Color(0xFFFFF0BD) else Color(0xFFD7C891),
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun BadventurersAppPreview() {
    BadventurersApp()
}




