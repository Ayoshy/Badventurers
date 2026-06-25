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

internal enum class HeroesPanelTab {
    Roster,
    Gacha,
}

internal enum class HeroRosterSort {
    Rarity,
    Name,
}

@Composable
internal fun HeroesScreen(
    session: PlaySessionState,
    lastRecruitment: HeroRecruitmentResult?,
    selectedHeroId: String,
    onOpenHero: (String) -> Unit,
    onEquip: (String, LootItem) -> Unit,
    onUnequip: (String, LootSlot) -> Unit,
    onReleaseHero: (String) -> Unit,
    onRecruit: () -> Unit,
    onRecruitWithTicket: (String) -> Unit,
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
                onRecruitWithTicket = onRecruitWithTicket,
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
internal fun HeroesPanelSwitcher(selectedTab: HeroesPanelTab, onSelectedTab: (HeroesPanelTab) -> Unit) {
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
internal fun HeroesPanelButton(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
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
internal fun HeroRosterPanel(
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
internal fun HeroSortButton(label: String, selected: Boolean, onClick: () -> Unit) {
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
internal fun HeroGachaPanel(
    session: PlaySessionState,
    lastRecruitment: HeroRecruitmentResult?,
    onRecruit: () -> Unit,
    onRecruitWithTicket: (String) -> Unit,
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
    RecruitmentTicketsPanel(
        session = session,
        onRecruitWithTicket = onRecruitWithTicket,
    )
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
internal fun RecruitmentTicketsPanel(
    session: PlaySessionState,
    onRecruitWithTicket: (String) -> Unit,
) {
    val ticketStacks = RecruitmentTicketCatalog.recruitmentTickets.mapNotNull { ticket ->
        val count = session.recruitmentTicketCount(ticket.id)
        if (count > 0) ticket to count else null
    }
    val body = if (ticketStacks.isEmpty()) {
        stringResource(R.string.recruitment_tickets_empty)
    } else {
        stringResource(R.string.recruitment_tickets_summary)
    }

    DarkPanel(
        title = stringResource(R.string.recruitment_tickets_title),
        body = body,
    ) {
        ticketStacks.forEach { (ticket, count) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val guarantee = recruitmentTicketGuarantee(ticket)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = recruitmentTicketTitle(ticket.id),
                        color = Color(0xFF211F1A),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = recruitmentTicketSummary(ticket.id),
                        color = Color(0xFF756B54),
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (guarantee.isNotBlank()) {
                        Text(
                            text = guarantee,
                            color = Color(0xFF5B5444),
                            fontSize = 10.sp,
                            lineHeight = 14.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                Text(
                    text = stringResource(R.string.recruitment_ticket_count, count),
                    color = Color(0xFF493F2B),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.width(36.dp),
                    textAlign = TextAlign.End,
                )
                Button(
                    onClick = { onRecruitWithTicket(ticket.id) },
                    modifier = Modifier
                        .width(72.dp)
                        .height(36.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F695C)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                ) {
                    Text(
                        text = stringResource(R.string.recruitment_ticket_use_action),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Composable
internal fun recruitmentTicketTitle(ticketId: String): String = when (ticketId) {
    RecruitmentTicketCatalog.BASIC_HIRING_VOUCHER_ID -> stringResource(R.string.ticket_basic_hiring_voucher_title)
    RecruitmentTicketCatalog.SPECIALIST_INVITATION_ID -> stringResource(R.string.ticket_specialist_invitation_title)
    RecruitmentTicketCatalog.RARE_CONTRACT_TICKET_ID -> stringResource(R.string.ticket_rare_contract_title)
    RecruitmentTicketCatalog.EPIC_LIABILITY_WRIT_ID -> stringResource(R.string.ticket_epic_liability_writ_title)
    RecruitmentTicketCatalog.VETERAN_TICKET_ID -> stringResource(R.string.ticket_veteran_ticket_title)
    RecruitmentTicketCatalog.BLANK_CONTRACT_ID -> stringResource(R.string.ticket_blank_contract_title)
    else -> ticketId
}

@Composable
internal fun recruitmentTicketSummary(ticketId: String): String = when (ticketId) {
    RecruitmentTicketCatalog.BASIC_HIRING_VOUCHER_ID -> stringResource(R.string.ticket_basic_hiring_voucher_summary)
    RecruitmentTicketCatalog.SPECIALIST_INVITATION_ID -> stringResource(R.string.ticket_specialist_invitation_summary)
    RecruitmentTicketCatalog.RARE_CONTRACT_TICKET_ID -> stringResource(R.string.ticket_rare_contract_summary)
    RecruitmentTicketCatalog.EPIC_LIABILITY_WRIT_ID -> stringResource(R.string.ticket_epic_liability_writ_summary)
    RecruitmentTicketCatalog.VETERAN_TICKET_ID -> stringResource(R.string.ticket_veteran_ticket_summary)
    RecruitmentTicketCatalog.BLANK_CONTRACT_ID -> stringResource(R.string.ticket_blank_contract_summary)
    else -> stringResource(R.string.ticket_unknown_summary)
}

@Composable
internal fun recruitmentTicketGuarantee(ticket: RecruitmentTicketMetadata): String = when {
    ticket.id == RecruitmentTicketCatalog.BASIC_HIRING_VOUCHER_ID -> stringResource(
        R.string.ticket_basic_hiring_voucher_guarantee,
    )
    ticket.specialistHeroClasses.isNotEmpty() -> stringResource(
        R.string.ticket_specialist_invitation_guarantee,
        recruitmentTicketClassPool(ticket.specialistHeroClasses),
    )
    ticket.guaranteedMinimumRarity != null -> stringResource(
        R.string.ticket_guaranteed_minimum_rarity,
        heroRarityLabel(ticket.guaranteedMinimumRarity),
    )
    ticket.minimumHeroLevel > 1 -> stringResource(
        R.string.ticket_veteran_ticket_guarantee,
        ticket.minimumHeroLevel,
    )
    ticket.id == RecruitmentTicketCatalog.BLANK_CONTRACT_ID -> ""
    else -> ""
}

@Composable
internal fun recruitmentTicketClassPool(classes: Set<HeroClass>): String {
    val sortedClasses = classes.sortedBy { it.name }
    val labels = mutableListOf<String>()
    for (heroClass in sortedClasses) {
        labels.add(heroClassLabel(heroClass))
    }
    return labels.joinToString(", ")
}

@Composable
internal fun HeroDetailDialog(
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
                HeroProgressionPanel(hero = hero, modifier = Modifier.fillMaxWidth())
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
internal fun HeroDetailHeader(
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
                Text(
                    text = stringResource(
                        R.string.hero_progress_value,
                        hero.level,
                        hero.xp,
                        HeroProgression.xpForNextLevel(hero.level),
                    ),
                    color = Color(0xFFFFF0BD),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
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
internal fun HeroProgressionPanel(hero: Hero, modifier: Modifier = Modifier) {
    val nextXp = HeroProgression.xpForNextLevel(hero.level)
    val progress = (hero.xp.toFloat() / nextXp.coerceAtLeast(1)).coerceIn(0f, 1f)
    val xpRemaining = (nextXp - hero.xp).coerceAtLeast(0)
    val nextLevelStats = HeroProgression.statGrowthForLevel(hero, hero.level + 1)
    val nextStatSummary = statBonusSummary(nextLevelStats, maxItems = 4)
    val nextLevelRewards = HeroProgression.rewardUnlocksBetween(hero, hero.level, hero.level + 1)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xE624251F))
            .border(1.dp, Color(0x88D0A24A), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 9.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.hero_progression_title),
                color = Color(0xFFFFF1C0),
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1,
            )
            Text(
                text = stringResource(R.string.hero_next_level_value, hero.level, hero.level + 1),
                color = Color(0xFFE2CF93),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1,
            )
        }
        ProgressBar(progress = progress)
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.hero_xp_remaining_detail, xpRemaining),
            color = Color(0xFFDED0A2),
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = stringResource(R.string.hero_next_stats_detail, nextStatSummary),
            color = Color(0xFFFFF0BD),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (nextLevelRewards.isNotEmpty()) {
            Text(
                text = heroLevelRewardSummary(nextLevelRewards),
                color = Color(0xFFFFF0BD),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = stringResource(R.string.hero_special_detail, heroSpecialSummary(hero.special)),
            color = Color(0xFFDED0A2),
            fontSize = 11.sp,
            lineHeight = 14.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
@Composable
internal fun HeroPortraitWithEquipment(
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
                slot = LootSlot.Footwear,
                item = equipment[LootSlot.Footwear],
                selected = selectedSlot == LootSlot.Footwear,
                onChoose = { onChoose(LootSlot.Footwear) },
                onUnequip = { onUnequip(LootSlot.Footwear) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 34.dp),
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
internal fun HeroOverlayEquipmentSlot(
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
    val iconResource = item?.let(::lootArtResource) ?: emptySlotIconResource(slot)

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
internal fun HeroArtworkPanel(hero: Hero, modifier: Modifier = Modifier) {
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
internal fun HeroDetailStatsGrid(hero: Hero, equipment: Map<LootSlot, LootItem>, modifier: Modifier = Modifier) {
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
internal fun HeroDetailStatLine(label: String, value: Int, bonus: Int, modifier: Modifier = Modifier) {
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

internal fun emptySlotIconResource(slot: LootSlot): Int =
    when (slot) {
        LootSlot.Weapon -> R.drawable.loot_icon_weapon
        LootSlot.Armor -> R.drawable.loot_icon_paper_shield
        LootSlot.Footwear -> R.drawable.loot_icon_boots
        LootSlot.Trinket -> R.drawable.loot_icon_ring
        LootSlot.Headgear -> R.drawable.loot_icon_helmet
        LootSlot.Consumable -> R.drawable.loot_icon_potion
    }
@Composable
internal fun HeroEquipmentPickerOverlay(
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

internal fun heroRosterComparator(sortOrder: HeroRosterSort): Comparator<Hero> =
    when (sortOrder) {
        HeroRosterSort.Rarity -> compareByDescending<Hero> { it.rarity.ordinal }.thenBy { it.name }
        HeroRosterSort.Name -> compareBy { it.name }
    }
@Composable
internal fun HeroEquipmentPanel(
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
internal fun HeroStatsGrid(hero: Hero, equipment: Map<LootSlot, LootItem>) {
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
internal fun HeroStatChip(label: String, value: Int, bonus: Int, modifier: Modifier = Modifier) {
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
internal fun EquipmentSlotRow(
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
internal fun EquipmentSlotPicker(
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
internal fun RecruitReveal(recruitment: HeroRecruitmentResult) {
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