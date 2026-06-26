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

internal data class RarityBorderStyle(
    val borderColor: Color,
    val innerColor: Color,
    val surfaceColor: Color,
    val selectedSurfaceColor: Color,
    val strokeWidth: Dp = 2.dp,
)

@Composable
internal fun QuestCardArt(bannerResourceId: Int, frameResourceId: Int, borderStyle: RarityBorderStyle) {
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
internal fun HeroPortraitGrid(
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
internal fun HeroPortraitTile(
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

internal fun heroPortraitResource(hero: Hero): Int =
    when (hero.id) {
        "brugg" -> R.drawable.hero_portrait_brugg
        "darrik" -> R.drawable.hero_portrait_darrik
        "orla" -> R.drawable.hero_portrait_orla
        "mira" -> R.drawable.hero_portrait_mira
        "pippa" -> R.drawable.hero_portrait_pippa
        "nell" -> R.drawable.hero_portrait_nell
        "vex" -> R.drawable.hero_portrait_vex
        "quill" -> R.drawable.hero_portrait_quill
        "ledger" -> R.drawable.hero_portrait_comptable
        "sable" -> R.drawable.hero_portrait_ninja
        "bramble" -> R.drawable.hero_portrait_hunter
        "pax" -> R.drawable.hero_portrait_priest
        "morrow" -> R.drawable.hero_portrait_necromancer
        "paladin" -> R.drawable.hero_portrait_paladin
        "comptable" -> R.drawable.hero_portrait_tally_noakes
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
            HeroClass.Accountant -> R.drawable.hero_portrait_tally_noakes
            HeroClass.Gardener -> R.drawable.hero_portrait_jardinier
            HeroClass.DeathKnight -> R.drawable.hero_portrait_chevalier_de_la_mort
            HeroClass.Chef -> R.drawable.hero_portrait_chef_cuistot
            HeroClass.DemolitionExpert -> R.drawable.hero_portrait_expert_en_demolition
            HeroClass.SaltElemental -> R.drawable.hero_portrait_elementaire_de_sel
            HeroClass.StupidTroll -> R.drawable.hero_portrait_troll_stupide
        }
    }

internal fun heroBorderStyle(rarity: HeroRarity): RarityBorderStyle =
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

internal fun lootBorderStyle(rarity: LootRarity): RarityBorderStyle =
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

internal fun questDifficultyBorderStyle(difficulty: Int): RarityBorderStyle =
    when {
        difficulty < 100 -> heroBorderStyle(HeroRarity.Common)
        difficulty < 150 -> heroBorderStyle(HeroRarity.Uncommon)
        difficulty < 210 -> heroBorderStyle(HeroRarity.Rare)
        difficulty < 280 -> heroBorderStyle(HeroRarity.Epic)
        else -> heroBorderStyle(HeroRarity.Legendary)
    }

internal fun questFrameResource(difficulty: Int): Int =
    when {
        difficulty < 100 -> R.drawable.quest_frame_oak
        difficulty < 210 -> R.drawable.quest_frame_brass
        else -> R.drawable.quest_frame_moss
    }

internal fun questBannerResource(quest: Quest): Int =
    questBannerArtReference(quest).resourceId

internal data class QuestBannerArtReference(
    val resourceId: Int,
    val temporaryBrief: String? = null,
)

internal fun questBannerArtReference(quest: Quest): QuestBannerArtReference =
    when (quest.id) {
        "cave_minor_regrets" -> QuestBannerArtReference(R.drawable.quest_banner_cave_minor_regrets)
        "bandit_tax_office" -> QuestBannerArtReference(R.drawable.quest_banner_bandit_tax_office_v2)
        "forest_of_wrong_turns" -> QuestBannerArtReference(R.drawable.quest_banner_forest_of_wrong_turns)
        "salted_swamp_chapel" -> QuestBannerArtReference(R.drawable.quest_banner_salted_swamp_chapel)
        "moonlit_smuggler_run" -> QuestBannerArtReference(R.drawable.quest_banner_moonlit_smuggler_run)
        "the_hungry_siege" -> QuestBannerArtReference(R.drawable.quest_banner_the_hungry_siege)
        "the_last_locked_door" -> QuestBannerArtReference(R.drawable.quest_banner_the_last_locked_door)
        "crypt_of_unpaid_debts" -> QuestBannerArtReference(R.drawable.quest_banner_crypt_of_unpaid_debts)
        "paperwork_toll_of_chaos" -> QuestBannerArtReference(
            resourceId = R.drawable.quest_banner_03,
            temporaryBrief = "Border checkpoint booth stacked with stamped receipts, chaos toll signs, and nervous adventurers.",
        )
        "licensed_guild_caravan_haunt" -> QuestBannerArtReference(
            resourceId = R.drawable.quest_banner_03,
            temporaryBrief = "Licensed caravan at dusk with spectral cargo straps, official seals, and triplicate manifests.",
        )
        "notary_night_patrol" -> QuestBannerArtReference(
            resourceId = R.drawable.quest_banner_03,
            temporaryBrief = "Lantern-lit notary patrol in identical robes, oath scrolls, and suspiciously legal shadows.",
        )
        "inspectorate_cove_banquet" -> QuestBannerArtReference(
            resourceId = R.drawable.quest_banner_03,
            temporaryBrief = "Coastal banquet table with inspector seals, emergency audit papers, and anxious serving covers.",
        )
        "wedding_with_too_many_oaths" -> QuestBannerArtReference(
            resourceId = R.drawable.quest_banner_03,
            temporaryBrief = "Crowded guild wedding hall with oath scrolls, witness benches, brass seals, and a cake under legal review.",
        )
        "the_sunken_toll_booth" -> QuestBannerArtReference(
            resourceId = R.drawable.quest_banner_03,
            temporaryBrief = "Half-submerged toll booth in swamp water with floating coins, stamped driftwood, and a glowing fee schedule.",
        )
        "the_crowns_missing_receipt" -> QuestBannerArtReference(
            resourceId = R.drawable.quest_banner_03,
            temporaryBrief = "Royal archive desk buried in ribbons, crowns, missing receipts, and worried guards checking drawers.",
        )
        "the_tower_built_sideways" -> QuestBannerArtReference(
            resourceId = R.drawable.quest_banner_03,
            temporaryBrief = "Impossible tower leaning sideways with braces, cracked stairs, magic survey lines, and tiny adventurers climbing left.",
        )
        else -> QuestBannerArtReference(R.drawable.quest_banner_03)
    }

internal fun rarityBorderStyle(
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
internal fun LootIconPanel(item: LootItem, contentDescription: String) {
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
                painter = painterResource(lootArtResource(item)),
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )
        }
    }
}

internal fun lootArtResource(item: LootItem): Int =
    acceptedLootArtResourceIds[item.id] ?: lootIconResource(item.icon)

internal val acceptedLootArtResourceIds: Map<String, Int> = mapOf(
    "weapon_bent_spoon" to R.drawable.loot_art_weapon_bent_spoon,
    "weapon_fork_spear" to R.drawable.loot_art_weapon_fork_spear,
    "weapon_moon_axe" to R.drawable.loot_art_weapon_moon_axe,
    "weapon_nibblade" to R.drawable.loot_art_weapon_nibblade,
    "weapon_toast_mace" to R.drawable.loot_art_weapon_toast_mace,
    "armor_patch_hood" to R.drawable.loot_art_armor_patch_hood,
    "armor_moss_coat" to R.drawable.loot_art_armor_moss_coat,
    "armor_winged_boots" to R.drawable.loot_art_armor_winged_boots,
    "armor_travel_boots" to R.drawable.loot_art_armor_travel_boots,
    "trinket_lucky_ring" to R.drawable.loot_art_trinket_lucky_ring,
    "trinket_pocket_ring" to R.drawable.loot_art_trinket_pocket_ring,
    "trinket_spare_ring" to R.drawable.loot_art_trinket_spare_ring,
    "trinket_dusty_ring" to R.drawable.loot_art_trinket_dusty_ring,
    "trinket_quiet_ring" to R.drawable.loot_art_trinket_quiet_ring,
    "armor_panic_helm" to R.drawable.loot_art_armor_panic_helm,
    "headgear_soup_helm" to R.drawable.loot_art_headgear_soup_helm,
    "headgear_wobble_cap" to R.drawable.loot_art_headgear_wobble_cap,
    "headgear_paper_crown" to R.drawable.loot_art_headgear_paper_crown,
    "headgear_lantern_hat" to R.drawable.loot_art_headgear_lantern_hat,
    "headgear_grin_hood" to R.drawable.loot_art_headgear_grin_hood,
    "consumable_stale_potion" to R.drawable.loot_art_consumable_stale_potion,
    "consumable_brave_brew" to R.drawable.loot_art_consumable_brave_brew,
    "consumable_tiny_flask" to R.drawable.loot_art_consumable_tiny_flask,
    "consumable_odd_elixir" to R.drawable.loot_art_consumable_odd_elixir,
    "consumable_snap_tonic" to R.drawable.loot_art_consumable_snap_tonic,
    "weapon_receipt_cutter" to R.drawable.loot_art_weapon_receipt_cutter,
    "weapon_mop_halberd" to R.drawable.loot_art_weapon_mop_halberd,
    "armor_buttoned_barrel" to R.drawable.loot_art_armor_buttoned_barrel,
    "footwear_squeaky_greaves" to R.drawable.loot_art_footwear_squeaky_greaves,
    "trinket_queue_token" to R.drawable.loot_art_trinket_queue_token,
    "headgear_bucket_visor" to R.drawable.loot_art_headgear_bucket_visor,
    "consumable_warmish_tea" to R.drawable.loot_art_consumable_warmish_tea,
    "consumable_pickle_potion" to R.drawable.loot_art_consumable_pickle_potion,
    "weapon_fine_print_rapier" to R.drawable.loot_art_weapon_fine_print_rapier,
    "weapon_taxman_gavel" to R.drawable.loot_art_weapon_taxman_gavel,
    "armor_invoice_mail" to R.drawable.loot_art_armor_invoice_mail,
    "footwear_witness_slippers" to R.drawable.loot_art_footwear_witness_slippers,
    "trinket_overtime_hourglass" to R.drawable.loot_art_trinket_overtime_hourglass,
    "headgear_notary_wig" to R.drawable.loot_art_headgear_notary_wig,
    "consumable_bottled_courage" to R.drawable.loot_art_consumable_bottled_courage,
    "trinket_minor_oracle_receipt" to R.drawable.loot_art_trinket_minor_oracle_receipt,
    "weapon_auditors_halberd" to R.drawable.loot_art_weapon_auditors_halberd,
    "weapon_dragon_stamp" to R.drawable.loot_art_weapon_dragon_stamp,
    "armor_misfiled_aegis" to R.drawable.loot_art_armor_misfiled_aegis,
    "footwear_plausible_denial_boots" to R.drawable.loot_art_footwear_plausible_denial_boots,
    "trinket_bell_of_last_call" to R.drawable.loot_art_trinket_bell_of_last_call,
    "headgear_emergency_minutes_crown" to R.drawable.loot_art_headgear_emergency_minutes_crown,
    "consumable_second_chance_soup" to R.drawable.loot_art_consumable_second_chance_soup,
    "trinket_contract_knot" to R.drawable.loot_art_trinket_contract_knot,
    "weapon_spoon_final_notice" to R.drawable.loot_art_weapon_spoon_final_notice,
    "weapon_moonlit_receipt_cleaver" to R.drawable.loot_art_weapon_moonlit_receipt_cleaver,
    "armor_many_signatures_cloak" to R.drawable.loot_art_armor_many_signatures_cloak,
    "footwear_inevitable_return_sandals" to R.drawable.loot_art_footwear_inevitable_return_sandals,
    "trinket_perpetual_queue_ring" to R.drawable.loot_art_trinket_perpetual_queue_ring,
    "headgear_halo_compliance" to R.drawable.loot_art_headgear_halo_compliance,
    "consumable_absolute_maybe_elixir" to R.drawable.loot_art_consumable_absolute_maybe_elixir,
    "trinket_unpaid_charter_seal" to R.drawable.loot_art_trinket_unpaid_charter_seal,
)

internal fun lootIconResource(icon: LootIcon): Int =
    when (icon) {
        LootIcon.Boots -> R.drawable.loot_icon_boots
        LootIcon.Ring -> R.drawable.loot_icon_ring
        LootIcon.Helmet -> R.drawable.loot_icon_helmet
        LootIcon.Weapon -> R.drawable.loot_icon_weapon
        LootIcon.Spoon -> R.drawable.loot_icon_spoon
        LootIcon.Hood -> R.drawable.loot_icon_hood
        LootIcon.Tankard -> R.drawable.loot_icon_tankard
        LootIcon.Potion -> R.drawable.loot_icon_potion
        LootIcon.Blade -> R.drawable.loot_icon_blade
    }
