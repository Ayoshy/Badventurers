package com.ayoshy.badventurers.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ayoshy.badventurers.R
import com.ayoshy.badventurers.game.ExpeditionEngine
import com.ayoshy.badventurers.game.ExpeditionOutcome
import com.ayoshy.badventurers.game.PartyPowerCalculator
import com.ayoshy.badventurers.game.PlayPhase
import com.ayoshy.badventurers.game.PlaySessionState
import com.ayoshy.badventurers.game.SeedGame
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
    Heroes,
    Loot,
    Upgrades,
}

@Composable
fun BadventurersApp() {
    MaterialTheme(colorScheme = BadventurersColors) {
        var selectedTab by rememberSaveable { mutableStateOf(GameTab.Guild) }
        var session by remember { mutableStateOf(PlaySessionState.initial()) }
        var nowMillis by remember { mutableStateOf(System.currentTimeMillis()) }
        val expeditionEngine = remember { ExpeditionEngine() }

        LaunchedEffect(session.phase, session.expedition?.endsAtMillis) {
            while (session.phase == PlayPhase.Running) {
                delay(250)
                val currentTime = System.currentTimeMillis()
                nowMillis = currentTime
                session = session.tick(currentTime, expeditionEngine, SeedGame.heroes)
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
                TopBar(session = session)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                ) {
                    when (selectedTab) {
                        GameTab.Guild -> GuildScreen(
                            session = session,
                            nowMillis = nowMillis,
                            onCollect = {
                                session = session.collectResult()
                                selectedTab = GameTab.Loot
                            },
                            onNextQuest = { selectedTab = GameTab.Quests },
                        )
                        GameTab.Quests -> QuestsScreen(
                            session = session,
                            onStart = {
                                val currentTime = System.currentTimeMillis()
                                nowMillis = currentTime
                                session = session.startQuest(currentTime, SeedGame.firstQuest)
                                selectedTab = GameTab.Guild
                            },
                            onParty = { selectedTab = GameTab.Heroes },
                        )
                        GameTab.Heroes -> HeroesScreen()
                        GameTab.Loot -> LootScreen(session = session, onEquip = { selectedTab = GameTab.Heroes })
                        GameTab.Upgrades -> UpgradesScreen(
                            session = session,
                            onBuy = { session = session.upgradeNoticeBoard() },
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
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                SquareAction(label = "L")
                SquareAction(label = "U")
            }
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
private fun SquareAction(label: String) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(Color(0xDD2D2D25)),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = label, color = Color(0xFFF7E8B5), fontWeight = FontWeight.Black)
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
    onCollect: () -> Unit,
    onNextQuest: () -> Unit,
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
                            primaryLabel = stringResource(R.string.collect_action),
                            secondaryLabel = stringResource(R.string.next_quest_action),
                            onPrimary = onCollect,
                            onSecondary = onNextQuest,
                        )
                    }
                }
            }
        }
        PaperPanel(title = stringResource(R.string.recommended_title), body = stringResource(R.string.recommended_upgrade)) {
            ProgressBar(progress = 0.72f)
        }
        JournalLine(stringResource(R.string.journal_01))
        JournalLine(stringResource(R.string.journal_02))
        JournalLine(stringResource(R.string.journal_03))
    }
}

@Composable
private fun QuestsScreen(session: PlaySessionState, onStart: () -> Unit, onParty: () -> Unit) {
    val canStart = session.phase == PlayPhase.Idle
    val startLabel = if (canStart) stringResource(R.string.start_action) else stringResource(R.string.quest_blocked_action)

    ScreenScaffold(title = stringResource(R.string.quests_title), status = phaseStatus(session.phase)) {
        ArtSheet(resourceId = R.drawable.quest_cards_sheet, aspectRatio = 1.25f)
        DarkPanel(title = stringResource(R.string.quest_card_title), body = stringResource(R.string.quest_card_summary)) {
            ActionRow(
                primaryLabel = startLabel,
                secondaryLabel = stringResource(R.string.party_action),
                onPrimary = onStart,
                onSecondary = onParty,
                primaryEnabled = canStart,
            )
        }
    }
}

@Composable
private fun HeroesScreen() {
    val partyPower = PartyPowerCalculator.totalPower(SeedGame.heroes)
    ScreenScaffold(title = stringResource(R.string.heroes_title), status = stringResource(R.string.roster_status)) {
        ArtSheet(resourceId = R.drawable.starter_heroes_sheet, aspectRatio = 1f)
        SeedGame.heroes.forEach { hero ->
            InfoRow(
                title = hero.name,
                detail = "${hero.heroClass.name} - ${hero.trait.name}",
                value = "Lv. ${hero.level}",
            )
        }
        InfoRow(title = "Party power", detail = "First expedition estimate", value = partyPower.toString())
    }
}

@Composable
private fun LootScreen(session: PlaySessionState, onEquip: () -> Unit) {
    val hasLoot = session.lootRolls > 0
    val title = if (hasLoot) stringResource(R.string.loot_item_title) else stringResource(R.string.loot_empty_title)
    val body = if (hasLoot) {
        stringResource(R.string.loot_inventory_summary, session.lootRolls)
    } else {
        stringResource(R.string.loot_empty_summary)
    }

    ScreenScaffold(title = stringResource(R.string.loot_title), status = stringResource(R.string.new_loot_status)) {
        ArtSheet(resourceId = R.drawable.loot_icons_sheet, aspectRatio = 1f)
        DarkPanel(title = title, body = body) {
            ActionRow(
                primaryLabel = stringResource(R.string.equip_action),
                secondaryLabel = stringResource(R.string.keep_action),
                onPrimary = onEquip,
                onSecondary = {},
                primaryEnabled = hasLoot,
            )
        }
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

private fun remainingSeconds(session: PlaySessionState, nowMillis: Long): Long {
    val endsAt = session.expedition?.endsAtMillis ?: return 0L
    return ((endsAt - nowMillis + 999L) / 1000L).coerceAtLeast(0L)
}

private fun rewardGoldWithNoticeBoard(session: PlaySessionState): Int {
    val rewardGold = session.expedition?.result?.reward?.gold ?: return 0
    return rewardGold + rewardGold * (session.noticeBoardLevel - 1) / 10
}

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
            fontSize = 9.sp,
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

