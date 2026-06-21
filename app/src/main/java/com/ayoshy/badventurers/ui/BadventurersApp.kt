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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.ayoshy.badventurers.game.PartyPowerCalculator
import com.ayoshy.badventurers.game.SeedGame

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
                TopBar()
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                ) {
                    when (selectedTab) {
                        GameTab.Guild -> GuildScreen(
                            onCollect = { selectedTab = GameTab.Loot },
                            onNextQuest = { selectedTab = GameTab.Quests },
                        )
                        GameTab.Quests -> QuestsScreen(
                            onStart = { selectedTab = GameTab.Guild },
                            onParty = { selectedTab = GameTab.Heroes },
                        )
                        GameTab.Heroes -> HeroesScreen()
                        GameTab.Loot -> LootScreen(onEquip = { selectedTab = GameTab.Heroes })
                        GameTab.Upgrades -> UpgradesScreen(onBuy = { selectedTab = GameTab.Guild })
                    }
                }
                BottomBar(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
            }
        }
    }
}

@Composable
private fun TopBar() {
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
            ResourceChip(label = stringResource(R.string.gold_label), value = "1,284")
            ResourceChip(label = stringResource(R.string.reputation_label), value = "17")
            ResourceChip(label = stringResource(R.string.guild_level_label), value = "Lv. 3")
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
private fun GuildScreen(onCollect: () -> Unit, onNextQuest: () -> Unit) {
    ScreenScaffold(title = stringResource(R.string.guild_home_title), status = stringResource(R.string.quest_ready_status)) {
        DarkPanel(title = stringResource(R.string.completed_quest_title), body = stringResource(R.string.completed_quest_summary)) {
            ActionRow(
                primaryLabel = stringResource(R.string.collect_action),
                secondaryLabel = stringResource(R.string.next_quest_action),
                onPrimary = onCollect,
                onSecondary = onNextQuest,
            )
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
private fun QuestsScreen(onStart: () -> Unit, onParty: () -> Unit) {
    ScreenScaffold(title = stringResource(R.string.quests_title), status = stringResource(R.string.quest_pick_trouble)) {
        ArtSheet(resourceId = R.drawable.quest_cards_sheet, aspectRatio = 1.25f)
        DarkPanel(title = stringResource(R.string.quest_card_title), body = stringResource(R.string.quest_card_summary)) {
            ActionRow(
                primaryLabel = stringResource(R.string.start_action),
                secondaryLabel = stringResource(R.string.party_action),
                onPrimary = onStart,
                onSecondary = onParty,
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
private fun LootScreen(onEquip: () -> Unit) {
    ScreenScaffold(title = stringResource(R.string.loot_title), status = stringResource(R.string.new_loot_status)) {
        ArtSheet(resourceId = R.drawable.loot_icons_sheet, aspectRatio = 1f)
        DarkPanel(title = stringResource(R.string.loot_item_title), body = stringResource(R.string.loot_item_summary)) {
            ActionRow(
                primaryLabel = stringResource(R.string.equip_action),
                secondaryLabel = stringResource(R.string.keep_action),
                onPrimary = onEquip,
                onSecondary = {},
            )
        }
    }
}

@Composable
private fun UpgradesScreen(onBuy: () -> Unit) {
    ScreenScaffold(title = stringResource(R.string.upgrades_title), status = stringResource(R.string.guild_upgrade_status)) {
        InfoRow(title = "Notice Board Lv. 2", detail = "+8% quest rewards", value = "600g")
        InfoRow(title = "Training Rug Lv. 1", detail = "+5% hero XP", value = "420g")
        InfoRow(title = "Accountant Stool Lv. 1", detail = "-3% mysterious losses", value = "500g")
        DarkPanel(title = stringResource(R.string.next_unlock_title), body = stringResource(R.string.next_unlock_summary)) {
            Button(
                onClick = onBuy,
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
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Button(
            onClick = onPrimary,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F695C)),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(primaryLabel, fontWeight = FontWeight.Black)
        }
        Button(
            onClick = onSecondary,
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

