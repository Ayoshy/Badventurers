package com.ayoshy.badventurers.ui

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
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
            if (selectedTab != GameTab.Guild) {
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
            }
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
                    TopBar(session = session)
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
                            onLoot = { selectedTab = GameTab.Loot },
                            onFacilities = { selectedTab = GameTab.Upgrades },
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
                            onPrepare = { quest, plan ->
                                selectedQuestId = quest.id
                                selectedPlanId = plan?.id ?: ExpeditionPlanCatalog.selectedPlanForUi(selectedPlanId, quest).id
                                selectedTab = GameTab.ExpeditionPrep
                            },
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
                            onKeepSelection = { items ->
                                val nextSession = session.keepPendingLootSelection(items)
                                updateSession(nextSession)
                                selectedTab = GameTab.Guild
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
                            onPromoteHero = { heroId -> updateSession(session.promoteHeroWithBlankContract(heroId)) },
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
                            onReroll = { item -> updateSession(session.rerollLoot(item, seed = System.currentTimeMillis().toInt())) },
                        )
                        GameTab.Upgrades -> UpgradesScreen(
                            session = session,
                            onAchievements = { selectedTab = GameTab.Achievements },
                            onBuyNoticeBoard = { updateSession(session.upgradeNoticeBoard()) },
                            onBuyTrainingYard = { updateSession(session.upgradeTrainingYard()) },
                            onBuyBunkRoom = { updateSession(session.upgradeBunkRoom()) },
                            onBuyScoutTable = { updateSession(session.upgradeScoutTable()) },
                            onBuyArmoryForge = { updateSession(session.upgradeArmoryForge()) },
                            onBuyTavernKitchen = { updateSession(session.upgradeTavernKitchen()) },
                            onBuyInfirmary = { updateSession(session.upgradeInfirmary()) },
                            onBuyAccountantOffice = { updateSession(session.upgradeAccountantOffice()) },
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
private fun TopBar(session: PlaySessionState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFA141512), Color(0xDD141512)),
                ),
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ResourceChip(
            label = stringResource(R.string.gold_label),
            value = formatCount(session.gold),
            modifier = Modifier.weight(1f),
        )
        ResourceChip(
            label = stringResource(R.string.reputation_label),
            value = session.reputation.toString(),
            modifier = Modifier.weight(1f),
        )
        ResourceChip(
            label = stringResource(R.string.guild_level_label),
            value = stringResource(R.string.guild_level_value, session.guildLevel),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ResourceChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .height(30.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(Color(0xEAF8E7B5))
            .padding(horizontal = 7.dp, vertical = 3.dp),
    ) {
        Text(
            text = label,
            color = Color(0xFF4F4630),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 10.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = value,
            color = Color(0xFF211F1A),
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            lineHeight = 13.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun BottomBar(selectedTab: GameTab, onTabSelected: (GameTab) -> Unit) {
    val activeTab = selectedTab.bottomNavigationTab()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(GuildHomeBottomNavigationHeightDp.dp)
            .background(Color(0xF0141512))
            .padding(horizontal = 6.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        BottomTab(GameTab.Guild, activeTab, stringResource(R.string.guild_home_title), onTabSelected)
        BottomTab(GameTab.Quests, activeTab, stringResource(R.string.quests_title), onTabSelected)
        BottomTab(GameTab.Heroes, activeTab, stringResource(R.string.heroes_title), onTabSelected)
        BottomTab(GameTab.Loot, activeTab, stringResource(R.string.loot_title), onTabSelected)
        BottomTab(GameTab.Upgrades, activeTab, stringResource(R.string.upgrades_title), onTabSelected)
    }
}

private fun GameTab.bottomNavigationTab(): GameTab = when (this) {
    GameTab.ExpeditionPrep -> GameTab.Quests
    GameTab.OfflineSummary,
    GameTab.QuestResult -> GameTab.Guild
    GameTab.RewardLoot -> GameTab.Loot
    GameTab.Achievements -> GameTab.Upgrades
    else -> this
}

@Composable
private fun RowScope.BottomTab(
    tab: GameTab,
    selectedTab: GameTab,
    label: String,
    onTabSelected: (GameTab) -> Unit,
) {
    val selected = tab == selectedTab
    val tabShape = RoundedCornerShape(8.dp)
    val iconShape = RoundedCornerShape(7.dp)
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clip(tabShape)
            .background(if (selected) Color(0x3A2F695C) else Color.Transparent)
            .border(
                BorderStroke(1.dp, if (selected) Color(0x66FFD36D) else Color(0x14FFF1BF)),
                tabShape,
            )
            .clickable { onTabSelected(tab) }
            .padding(horizontal = 2.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(iconShape)
                .background(if (selected) Color(0xFF2F695C) else Color(0x252F695C))
                .border(
                    BorderStroke(1.dp, if (selected) Color(0xAAFFD36D) else Color(0x36FFF1BF)),
                    iconShape,
                )
                .padding(3.dp),
            contentAlignment = Alignment.Center,
        ) {
            BottomTabIcon(tab = tab, selected = selected)
        }
        Spacer(Modifier.height(3.dp))
        Text(
            text = label,
            color = if (selected) Color(0xFFFFF0BD) else Color(0xFFD7C891),
            fontSize = 9.sp,
            fontWeight = FontWeight.Black,
            lineHeight = 10.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun BottomTabIcon(tab: GameTab, selected: Boolean) {
    val iconColor = if (selected) Color(0xFFFFE5A3) else Color(0xFFD7C891)
    val shadowColor = if (selected) Color(0x80301916) else Color(0x5510110D)
    Canvas(modifier = Modifier.fillMaxSize()) {
        when (tab) {
            GameTab.Guild -> drawGuildTabIcon(iconColor, shadowColor)
            GameTab.Quests,
            GameTab.ExpeditionPrep,
            GameTab.OfflineSummary,
            GameTab.QuestResult -> drawQuestTabIcon(iconColor, shadowColor)
            GameTab.Heroes -> drawHeroTabIcon(iconColor, shadowColor)
            GameTab.Loot,
            GameTab.RewardLoot -> drawLootTabIcon(iconColor, shadowColor)
            GameTab.Upgrades,
            GameTab.Achievements -> drawUpgradeTabIcon(iconColor, shadowColor)
        }
    }
}

private fun DrawScope.drawGuildTabIcon(color: Color, shadow: Color) {
    val stroke = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
    drawRoundRect(
        color = shadow,
        topLeft = Offset(size.width * 0.22f, size.height * 0.47f),
        size = Size(size.width * 0.56f, size.height * 0.42f),
        cornerRadius = CornerRadius(1.8.dp.toPx(), 1.8.dp.toPx()),
    )
    drawLine(color, Offset(size.width * 0.18f, size.height * 0.48f), Offset(size.width * 0.50f, size.height * 0.18f), strokeWidth = stroke.width, cap = StrokeCap.Round)
    drawLine(color, Offset(size.width * 0.50f, size.height * 0.18f), Offset(size.width * 0.82f, size.height * 0.48f), strokeWidth = stroke.width, cap = StrokeCap.Round)
    drawRoundRect(
        color = color,
        topLeft = Offset(size.width * 0.27f, size.height * 0.50f),
        size = Size(size.width * 0.46f, size.height * 0.36f),
        cornerRadius = CornerRadius(1.4.dp.toPx(), 1.4.dp.toPx()),
        style = stroke,
    )
    drawLine(color, Offset(size.width * 0.50f, size.height * 0.64f), Offset(size.width * 0.50f, size.height * 0.86f), strokeWidth = stroke.width, cap = StrokeCap.Round)
}

private fun DrawScope.drawQuestTabIcon(color: Color, shadow: Color) {
    val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round)
    drawRoundRect(
        color = shadow,
        topLeft = Offset(size.width * 0.18f, size.height * 0.22f),
        size = Size(size.width * 0.64f, size.height * 0.58f),
        cornerRadius = CornerRadius(2.2.dp.toPx(), 2.2.dp.toPx()),
    )
    drawRoundRect(
        color = color,
        topLeft = Offset(size.width * 0.20f, size.height * 0.18f),
        size = Size(size.width * 0.60f, size.height * 0.58f),
        cornerRadius = CornerRadius(2.2.dp.toPx(), 2.2.dp.toPx()),
        style = stroke,
    )
    drawLine(color, Offset(size.width * 0.35f, size.height * 0.22f), Offset(size.width * 0.30f, size.height * 0.74f), strokeWidth = stroke.width, cap = StrokeCap.Round)
    drawLine(color, Offset(size.width * 0.60f, size.height * 0.20f), Offset(size.width * 0.66f, size.height * 0.72f), strokeWidth = stroke.width, cap = StrokeCap.Round)
    drawCircle(color, radius = size.minDimension * 0.055f, center = Offset(size.width * 0.55f, size.height * 0.46f))
}

private fun DrawScope.drawHeroTabIcon(color: Color, shadow: Color) {
    val shield = Path().apply {
        moveTo(size.width * 0.50f, size.height * 0.16f)
        lineTo(size.width * 0.78f, size.height * 0.28f)
        lineTo(size.width * 0.70f, size.height * 0.66f)
        lineTo(size.width * 0.50f, size.height * 0.86f)
        lineTo(size.width * 0.30f, size.height * 0.66f)
        lineTo(size.width * 0.22f, size.height * 0.28f)
        close()
    }
    val stroke = Stroke(width = 1.7.dp.toPx(), cap = StrokeCap.Round)
    drawCircle(shadow, radius = size.minDimension * 0.24f, center = Offset(size.width * 0.53f, size.height * 0.47f))
    drawPath(shield, color, style = stroke)
    drawLine(color, Offset(size.width * 0.50f, size.height * 0.30f), Offset(size.width * 0.50f, size.height * 0.75f), strokeWidth = stroke.width, cap = StrokeCap.Round)
    drawLine(color, Offset(size.width * 0.36f, size.height * 0.48f), Offset(size.width * 0.64f, size.height * 0.48f), strokeWidth = stroke.width, cap = StrokeCap.Round)
}

private fun DrawScope.drawLootTabIcon(color: Color, shadow: Color) {
    val stroke = Stroke(width = 1.7.dp.toPx(), cap = StrokeCap.Round)
    drawRoundRect(
        color = shadow,
        topLeft = Offset(size.width * 0.20f, size.height * 0.38f),
        size = Size(size.width * 0.62f, size.height * 0.42f),
        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
    )
    drawRoundRect(
        color = color,
        topLeft = Offset(size.width * 0.22f, size.height * 0.40f),
        size = Size(size.width * 0.56f, size.height * 0.36f),
        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
        style = stroke,
    )
    drawLine(color, Offset(size.width * 0.24f, size.height * 0.53f), Offset(size.width * 0.76f, size.height * 0.53f), strokeWidth = stroke.width, cap = StrokeCap.Round)
    drawLine(color, Offset(size.width * 0.50f, size.height * 0.40f), Offset(size.width * 0.50f, size.height * 0.76f), strokeWidth = stroke.width, cap = StrokeCap.Round)
    drawRoundRect(
        color = color,
        topLeft = Offset(size.width * 0.37f, size.height * 0.25f),
        size = Size(size.width * 0.26f, size.height * 0.15f),
        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
        style = stroke,
    )
}

private fun DrawScope.drawUpgradeTabIcon(color: Color, shadow: Color) {
    val stroke = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
    drawLine(shadow, Offset(size.width * 0.35f, size.height * 0.80f), Offset(size.width * 0.80f, size.height * 0.35f), strokeWidth = 3.2.dp.toPx(), cap = StrokeCap.Round)
    drawLine(color, Offset(size.width * 0.30f, size.height * 0.76f), Offset(size.width * 0.76f, size.height * 0.30f), strokeWidth = stroke.width, cap = StrokeCap.Round)
    drawRoundRect(
        color = color,
        topLeft = Offset(size.width * 0.56f, size.height * 0.18f),
        size = Size(size.width * 0.25f, size.height * 0.18f),
        cornerRadius = CornerRadius(1.5.dp.toPx(), 1.5.dp.toPx()),
        style = stroke,
    )
    drawLine(color, Offset(size.width * 0.18f, size.height * 0.78f), Offset(size.width * 0.40f, size.height * 0.78f), strokeWidth = stroke.width, cap = StrokeCap.Round)
    drawLine(color, Offset(size.width * 0.22f, size.height * 0.65f), Offset(size.width * 0.48f, size.height * 0.65f), strokeWidth = stroke.width, cap = StrokeCap.Round)
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun BadventurersAppPreview() {
    BadventurersApp()
}

