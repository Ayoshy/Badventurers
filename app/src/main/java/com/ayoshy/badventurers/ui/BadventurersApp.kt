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
    Achievements,
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
        var selectedQuestId by rememberSaveable { mutableStateOf(SeedGame.firstQuest.id) }
        val selectedQuest = SeedGame.questById[selectedQuestId] ?: SeedGame.firstQuest
        var selectedQuestPartyIds by rememberSaveable {
            mutableStateOf(initialSession.selectedPartyForQuest(SeedGame.firstQuest, initialSession.heroes).map { it.id })
        }
        val expeditionEngine = remember { ExpeditionEngine() }
        var selectedPlanId by rememberSaveable { mutableStateOf(ExpeditionPlanCatalog.defaultPlayerPlanId) }
        val selectedPlan = ExpeditionPlanCatalog.selectedPlanForUi(selectedPlanId, selectedQuest)
        val selectedQuestParty = selectedQuestPartyIds
            .mapNotNull { heroId -> session.heroes.firstOrNull { it.id == heroId } }
            .take(session.effectivePartySlots(selectedQuest))
        fun updateSession(nextSession: PlaySessionState) {
            session = nextSession
            if (nextSession.heroes.none { it.id == selectedHeroId }) {
                selectedHeroId = nextSession.heroes.firstOrNull()?.id.orEmpty()
            }
            val nextHeroIds = nextSession.heroes.map { it.id }
            val nextPartySlots = nextSession.effectivePartySlots(selectedQuest)
            val keptPartyIds = selectedQuestPartyIds.filter { it in nextHeroIds }.take(nextPartySlots)
            selectedQuestPartyIds = keptPartyIds + nextHeroIds
                .filterNot { it in keptPartyIds }
                .take(nextPartySlots - keptPartyIds.size)
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
                painter = painterResource(
                    if (selectedTab == GameTab.Achievements) {
                        R.drawable.achievement_hall_concept
                    } else {
                        R.drawable.guild_home_background
                    },
                ),
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
                if (selectedTab == GameTab.Guild) {
                    TopBar(
                        session = session,
                        onGoldDelta = { updateSession(session.adjustGold(it)) },
                        onReputationDelta = { updateSession(session.adjustReputation(it)) },
                        onGuildLevelDelta = { updateSession(session.adjustGuildLevel(it)) },
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                ) {
                    when (selectedTab) {
                        GameTab.Guild -> GuildScreen(
                            session = session,
                            selectedQuest = selectedQuest,
                            nowMillis = nowMillis,
                            onViewResult = { selectedTab = GameTab.QuestResult },
                            onNextQuest = { selectedTab = GameTab.Quests },
                            onAchievements = { selectedTab = GameTab.Achievements },
                            onToggleCoreCrew = { heroId -> updateSession(session.toggleCoreCrewHero(heroId)) },
                            onFinishQuestNow = {
                                updateSession(session.finishQuestNow(expeditionEngine, session.heroes))
                                nowMillis = System.currentTimeMillis()
                            },
                            onResetProgress = { updateSession(session.resetProgressForTesting()) },
                        )
                        GameTab.Quests -> QuestsScreen(
                            session = session,
                            selectedQuest = selectedQuest,
                            onSelectQuest = { quest ->
                                selectedQuestId = quest.id
                                selectedPlanId = ExpeditionPlanCatalog.selectedPlanForUi(selectedPlanId, quest).id
                                val partySlots = session.effectivePartySlots(quest)
                                selectedQuestPartyIds = selectedQuestPartyIds.take(partySlots)
                            },
                            onPrepare = { quest ->
                                selectedQuestId = quest.id
                                selectedPlanId = ExpeditionPlanCatalog.selectedPlanForUi(selectedPlanId, quest).id
                                selectedTab = GameTab.ExpeditionPrep
                            },
                            onParty = { selectedTab = GameTab.Heroes },
                        )
                        GameTab.ExpeditionPrep -> ExpeditionPrepScreen(
                            session = session,
                            quest = selectedQuest,
                            selectedPartyIds = selectedQuestPartyIds,
                            selectedPlanId = selectedPlan.id,
                            onToggleHero = { heroId ->
                                selectedQuestPartyIds = togglePartyHero(
                                    currentIds = selectedQuestPartyIds,
                                    heroId = heroId,
                                    maxSlots = session.effectivePartySlots(selectedQuest),
                                )
                            },
                            onSelectPlan = { plan -> selectedPlanId = plan.id },
                            onLaunch = {
                                val currentTime = System.currentTimeMillis()
                                nowMillis = currentTime
                                updateSession(session.startQuest(currentTime, selectedQuest, selectedQuestParty, selectedPlan.id))
                                selectedTab = GameTab.Guild
                            },
                            onParty = { selectedTab = GameTab.Heroes },
                        )
                        GameTab.OfflineSummary -> OfflineSummaryScreen(
                            session = session,
                            nowMillis = nowMillis,
                            onViewReport = { selectedTab = GameTab.QuestResult },
                            onAchievements = { selectedTab = GameTab.Achievements },
                            onGuild = { selectedTab = GameTab.Guild },
                        )
                        GameTab.QuestResult -> QuestResultScreen(
                            session = session,
                            onCollect = {
                                val nextSession = session.collectResult()
                                updateSession(nextSession)
                                selectedTab = if (nextSession.pendingLootItems.isEmpty()) GameTab.Guild else GameTab.RewardLoot
                            },
                            onCollectWithFakeAd = {
                                val fakeReward = FakeRewardedAdService.rewardFor(session)
                                if (fakeReward != null) {
                                    val nextSession = session.collectResult(fakeRewardedAdReward = fakeReward)
                                    updateSession(nextSession)
                                    selectedTab = if (nextSession.pendingLootItems.isEmpty()) GameTab.Guild else GameTab.RewardLoot
                                }
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
                            onDiscardRest = {
                                val nextSession = session.discardPendingLoot()
                                updateSession(nextSession)
                                selectedTab = GameTab.Guild
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
                            onRecruitWithTicket = { ticketId ->
                                val recruitment = session.recruitHeroWithTicket(ticketId, seed = System.currentTimeMillis().toInt())
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
                            onAchievements = { selectedTab = GameTab.Achievements },
                            onBuyNoticeBoard = { updateSession(session.upgradeNoticeBoard()) },
                            onBuyTrainingYard = { updateSession(session.upgradeTrainingYard()) },
                            onBuyBunkRoom = { updateSession(session.upgradeBunkRoom()) },
                        )
                        GameTab.Achievements -> AchievementsScreen(
                            session = session,
                            onClaim = { achievementId -> updateSession(session.claimAchievement(achievementId, System.currentTimeMillis())) },
                            onClaimAll = { updateSession(session.claimAllAchievements(System.currentTimeMillis())) },
                            onBack = { selectedTab = GameTab.Upgrades },
                        )
                    }
                }
                BottomBar(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
            }
        }
    }
}

@Composable
private fun TopBar(
    session: PlaySessionState,
    onGoldDelta: (Int) -> Unit,
    onReputationDelta: (Int) -> Unit,
    onGuildLevelDelta: (Int) -> Unit,
) {
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
            ResourceChip(
                label = stringResource(R.string.gold_label),
                value = formatCount(session.gold),
                onDecrease = { onGoldDelta(-500) },
                onIncrease = { onGoldDelta(500) },
            )
            ResourceChip(
                label = stringResource(R.string.reputation_label),
                value = session.reputation.toString(),
                onDecrease = { onReputationDelta(-1) },
                onIncrease = { onReputationDelta(1) },
            )
            ResourceChip(
                label = stringResource(R.string.guild_level_label),
                value = "Lv. ${session.guildLevel}",
                onDecrease = { onGuildLevelDelta(-1) },
                onIncrease = { onGuildLevelDelta(1) },
            )
        }
    }
}

@Composable
private fun RowScope.ResourceChip(
    label: String,
    value: String,
    onDecrease: (() -> Unit)? = null,
    onIncrease: (() -> Unit)? = null,
) {
    Card(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xEEF8E7B5)),
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
            Text(
                text = label,
                color = Color(0xFF4F4630),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(3.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = value,
                    color = Color(0xFF211F1A),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                val decrease = onDecrease
                val increase = onIncrease
                if (decrease != null && increase != null) {
                    ResourceStepButton(text = "-", onClick = decrease)
                    ResourceStepButton(text = "+", onClick = increase)
                }
            }
        }
    }
}

@Composable
private fun ResourceStepButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(width = 24.dp, height = 24.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF7B5531),
            contentColor = Color(0xFFFFF1C0),
        ),
        shape = RoundedCornerShape(6.dp),
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
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

