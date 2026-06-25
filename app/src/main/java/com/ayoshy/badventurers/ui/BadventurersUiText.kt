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
internal fun phaseStatus(phase: PlayPhase): String =
    when (phase) {
        PlayPhase.Idle -> stringResource(R.string.quest_ready_status)
        PlayPhase.Running -> stringResource(R.string.quest_running_status)
        PlayPhase.ResultReady -> stringResource(R.string.quest_result_status)
    }

@Composable
internal fun outcomeLabel(outcome: ExpeditionOutcome): String =
    when (outcome) {
        ExpeditionOutcome.GreatSuccess -> stringResource(R.string.outcome_great_success)
        ExpeditionOutcome.Success -> stringResource(R.string.outcome_success)
        ExpeditionOutcome.PartialSuccess -> stringResource(R.string.outcome_partial_success)
        ExpeditionOutcome.Failure -> stringResource(R.string.outcome_failure)
        ExpeditionOutcome.RidiculousFailure -> stringResource(R.string.outcome_ridiculous_failure)
    }

@Composable
internal fun riskLabel(risk: QuestRisk): String =
    when (risk) {
        QuestRisk.Low -> stringResource(R.string.risk_low)
        QuestRisk.Medium -> stringResource(R.string.risk_medium)
        QuestRisk.High -> stringResource(R.string.risk_high)
    }

@Composable
internal fun resultIncidentText(planId: String, outcome: ExpeditionOutcome): String =
    when (ExpeditionPlanCatalog.coercePlanId(planId)) {
        ExpeditionPlanCatalog.rushTheJobId -> resultPlanToneText(
            outcome,
            R.string.result_plan_rush_win,
            R.string.result_plan_rush_mixed,
            R.string.result_plan_rush_fail,
        )
        ExpeditionPlanCatalog.safetyFirstId -> resultPlanToneText(
            outcome,
            R.string.result_plan_safety_win,
            R.string.result_plan_safety_mixed,
            R.string.result_plan_safety_fail,
        )
        ExpeditionPlanCatalog.lootPriorityId -> resultPlanToneText(
            outcome,
            R.string.result_plan_loot_win,
            R.string.result_plan_loot_mixed,
            R.string.result_plan_loot_fail,
        )
        ExpeditionPlanCatalog.auditEverythingId -> resultPlanToneText(
            outcome,
            R.string.result_plan_audit_win,
            R.string.result_plan_audit_mixed,
            R.string.result_plan_audit_fail,
        )
        ExpeditionPlanCatalog.sealSideTunnelId -> resultPlanToneText(
            outcome,
            R.string.result_plan_seal_side_tunnel_win,
            R.string.result_plan_seal_side_tunnel_mixed,
            R.string.result_plan_seal_side_tunnel_fail,
        )
        ExpeditionPlanCatalog.followWorstMapId -> resultPlanToneText(
            outcome,
            R.string.result_plan_follow_worst_map_win,
            R.string.result_plan_follow_worst_map_mixed,
            R.string.result_plan_follow_worst_map_fail,
        )
        ExpeditionPlanCatalog.demandReceiptsId -> resultPlanToneText(
            outcome,
            R.string.result_plan_demand_receipts_win,
            R.string.result_plan_demand_receipts_mixed,
            R.string.result_plan_demand_receipts_fail,
        )
        ExpeditionPlanCatalog.blessTheBrineId -> resultPlanToneText(
            outcome,
            R.string.result_plan_bless_the_brine_win,
            R.string.result_plan_bless_the_brine_mixed,
            R.string.result_plan_bless_the_brine_fail,
        )
        ExpeditionPlanCatalog.moonlessShortcutId -> resultPlanToneText(
            outcome,
            R.string.result_plan_moonless_shortcut_win,
            R.string.result_plan_moonless_shortcut_mixed,
            R.string.result_plan_moonless_shortcut_fail,
        )
        ExpeditionPlanCatalog.rationTheBiscuitsId -> resultPlanToneText(
            outcome,
            R.string.result_plan_ration_the_biscuits_win,
            R.string.result_plan_ration_the_biscuits_mixed,
            R.string.result_plan_ration_the_biscuits_fail,
        )
        ExpeditionPlanCatalog.bringDoorFormsId -> resultPlanToneText(
            outcome,
            R.string.result_plan_bring_door_forms_win,
            R.string.result_plan_bring_door_forms_mixed,
            R.string.result_plan_bring_door_forms_fail,
        )
        ExpeditionPlanCatalog.itemizedLastRitesId -> resultPlanToneText(
            outcome,
            R.string.result_plan_itemized_last_rites_win,
            R.string.result_plan_itemized_last_rites_mixed,
            R.string.result_plan_itemized_last_rites_fail,
        )
        else -> resultIncidentText(outcome)
    }

@Composable
internal fun resultPlanToneText(
    outcome: ExpeditionOutcome,
    winStringId: Int,
    mixedStringId: Int,
    failStringId: Int,
): String =
    when (outcome.resultTone()) {
        ResultTone.Win -> stringResource(winStringId)
        ResultTone.Mixed -> stringResource(mixedStringId)
        ResultTone.Fail -> stringResource(failStringId)
    }

@Composable
internal fun resultIncidentText(outcome: ExpeditionOutcome): String =
    when (outcome) {
        ExpeditionOutcome.GreatSuccess -> stringResource(R.string.result_incident_great_success)
        ExpeditionOutcome.Success -> stringResource(R.string.result_incident_success)
        ExpeditionOutcome.PartialSuccess -> stringResource(R.string.result_incident_partial_success)
        ExpeditionOutcome.Failure -> stringResource(R.string.result_incident_failure)
        ExpeditionOutcome.RidiculousFailure -> stringResource(R.string.result_incident_ridiculous_failure)
    }

private enum class ResultTone { Win, Mixed, Fail }

private fun ExpeditionOutcome.resultTone(): ResultTone = when (this) {
    ExpeditionOutcome.GreatSuccess,
    ExpeditionOutcome.Success -> ResultTone.Win
    ExpeditionOutcome.PartialSuccess -> ResultTone.Mixed
    ExpeditionOutcome.Failure,
    ExpeditionOutcome.RidiculousFailure -> ResultTone.Fail
}
@Composable
internal fun heroRosterDetail(hero: Hero): String =
    stringResource(
        R.string.hero_roster_detail,
        heroRarityLabel(hero.rarity),
        heroClassLabel(hero.heroClass),
        heroTraitLabel(hero.trait),
    )

@Composable
internal fun heroRarityLabel(rarity: HeroRarity): String =
    when (rarity) {
        HeroRarity.Common -> stringResource(R.string.rarity_common)
        HeroRarity.Uncommon -> stringResource(R.string.rarity_uncommon)
        HeroRarity.Rare -> stringResource(R.string.rarity_rare)
        HeroRarity.Epic -> stringResource(R.string.rarity_epic)
        HeroRarity.Legendary -> stringResource(R.string.rarity_legendary)
    }

@Composable
internal fun heroClassLabel(heroClass: HeroClass): String =
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
internal fun heroTraitLabel(trait: Trait): String =
    when (trait) {
        Trait.Overconfident -> stringResource(R.string.trait_overconfident)
        Trait.ReadsManual -> stringResource(R.string.trait_reads_manual)
        Trait.SuspiciouslyLucky -> stringResource(R.string.trait_suspiciously_lucky)
        Trait.PainfullyOrganized -> stringResource(R.string.trait_painfully_organized)
    }

@Composable
internal fun statBonusSummary(bonuses: List<StatBonus>, maxItems: Int = 3): String {
    val visibleBonuses = bonuses.filter { it.value > 0 }.take(maxItems)
    if (visibleBonuses.isEmpty()) return stringResource(R.string.hero_stat_gain_none)

    val labels = mutableListOf<String>()
    for (bonus in visibleBonuses) {
        labels += stringResource(R.string.hero_stat_gain_value, statTypeLabel(bonus.type), bonus.value)
    }

    val extraCount = bonuses.size - visibleBonuses.size
    val joined = labels.joinToString()
    return if (extraCount > 0) {
        stringResource(R.string.hero_stat_gain_more, joined, extraCount)
    } else {
        joined
    }
}

@Composable
internal fun heroSpecialSummary(special: HeroSpecial): String =
    when (special) {
        HeroSpecial.RamshackleCharge -> stringResource(R.string.hero_special_ramshackle_charge)
        HeroSpecial.GlyphReader -> stringResource(R.string.hero_special_glyph_reader)
        HeroSpecial.LightFingers -> stringResource(R.string.hero_special_light_fingers)
        HeroSpecial.HumanWall -> stringResource(R.string.hero_special_human_wall)
        HeroSpecial.AggressiveMinutes -> stringResource(R.string.hero_special_aggressive_minutes)
        HeroSpecial.TerrainManual -> stringResource(R.string.hero_special_terrain_manual)
        HeroSpecial.UnstableLuck -> stringResource(R.string.hero_special_unstable_luck)
        HeroSpecial.DirtyJackpot -> stringResource(R.string.hero_special_dirty_jackpot)
        HeroSpecial.FreshTrail -> stringResource(R.string.hero_special_fresh_trail)
        HeroSpecial.CleanBlessing -> stringResource(R.string.hero_special_clean_blessing)
        HeroSpecial.NoTrace -> stringResource(R.string.hero_special_no_trace)
        HeroSpecial.NecroLever -> stringResource(R.string.hero_special_necro_lever)
        HeroSpecial.HostileAudit -> stringResource(R.string.hero_special_hostile_audit)
        HeroSpecial.UnbreakableOath -> stringResource(R.string.hero_special_unbreakable_oath)
        HeroSpecial.BalancedBooks -> stringResource(R.string.hero_special_balanced_books)
        HeroSpecial.GreenThumb -> stringResource(R.string.hero_special_green_thumb)
        HeroSpecial.DeathDiscount -> stringResource(R.string.hero_special_death_discount)
        HeroSpecial.MoraleRations -> stringResource(R.string.hero_special_morale_rations)
        HeroSpecial.PlanBExplosives -> stringResource(R.string.hero_special_plan_b_explosives)
        HeroSpecial.PreservationSalt -> stringResource(R.string.hero_special_preservation_salt)
        HeroSpecial.CreativeMisunderstanding -> stringResource(R.string.hero_special_creative_misunderstanding)
    }
@Composable
internal fun journalEntryText(entry: JournalEntry): String {
    return when {
        entry.id.startsWith("outcome-") -> journalOutcomeText(entry.id)
        entry.id.startsWith("quest-") -> journalQuestText(entry.id) ?: entry.text
        entry.id.startsWith("special-") -> journalSpecialText(entry.id) ?: entry.text
        entry.id.startsWith("hero-") -> journalHeroText(entry.id) ?: entry.text
        entry.id.startsWith("reward-") -> journalRewardText(entry.id) ?: entry.text
        entry.id == "fallback-empty-ledger" -> stringResource(R.string.journal_fallback_empty_ledger)
        else -> entry.text
    }
}

@Composable
internal fun journalOutcomeText(id: String): String =
    when (id) {
        "outcome-greatsuccess" -> stringResource(R.string.journal_outcome_great_success)
        "outcome-success" -> stringResource(R.string.journal_outcome_success)
        "outcome-partialsuccess" -> stringResource(R.string.journal_outcome_partial_success)
        "outcome-failure" -> stringResource(R.string.journal_outcome_failure)
        "outcome-ridiculousfailure" -> stringResource(R.string.journal_outcome_ridiculous_failure)
        else -> id
    }

@Composable
internal fun journalQuestText(id: String): String? =
    when (id.removePrefix("quest-")) {
        "cave_minor_regrets" -> stringResource(R.string.journal_quest_cave_minor_regrets)
        "forest_of_wrong_turns" -> stringResource(R.string.journal_quest_forest_of_wrong_turns)
        "bandit_tax_office" -> stringResource(R.string.journal_quest_bandit_tax_office)
        "salted_swamp_chapel" -> stringResource(R.string.journal_quest_salted_swamp_chapel)
        "moonlit_smuggler_run" -> stringResource(R.string.journal_quest_moonlit_smuggler_run)
        "the_hungry_siege" -> stringResource(R.string.journal_quest_the_hungry_siege)
        "the_last_locked_door" -> stringResource(R.string.journal_quest_the_last_locked_door)
        "crypt_of_unpaid_debts" -> stringResource(R.string.journal_quest_crypt_of_unpaid_debts)
        "paperwork_toll_of_chaos" -> stringResource(R.string.journal_quest_paperwork_toll_of_chaos)
        "licensed_guild_caravan_haunt" -> stringResource(R.string.journal_quest_licensed_guild_caravan_haunt)
        "notary_night_patrol" -> stringResource(R.string.journal_quest_notary_night_patrol)
        "inspectorate_cove_banquet" -> stringResource(R.string.journal_quest_inspectorate_cove_banquet)
        "wedding_with_too_many_oaths" -> stringResource(R.string.journal_quest_wedding_with_too_many_oaths)
        "the_sunken_toll_booth" -> stringResource(R.string.journal_quest_the_sunken_toll_booth)
        "the_crowns_missing_receipt" -> stringResource(R.string.journal_quest_the_crowns_missing_receipt)
        "the_tower_built_sideways" -> stringResource(R.string.journal_quest_the_tower_built_sideways)
        else -> null
    }

@Composable
internal fun journalSpecialText(id: String): String? {
    val parts = id.removePrefix("special-").split("-", limit = 2)
    val heroId = parts.getOrNull(0) ?: return null
    val questId = parts.getOrNull(1) ?: return null
    val hero = HeroCatalog.byId[heroId]?.toHero() ?: return null
    val quest = SeedGame.questById[questId] ?: return null
    return when (hero.special) {
        HeroSpecial.RamshackleCharge -> stringResource(R.string.journal_special_ramshackle_charge, hero.name)
        HeroSpecial.GlyphReader -> stringResource(R.string.journal_special_glyph_reader, hero.name)
        HeroSpecial.LightFingers -> stringResource(R.string.journal_special_light_fingers, hero.name)
        HeroSpecial.HumanWall -> stringResource(R.string.journal_special_human_wall, hero.name)
        HeroSpecial.AggressiveMinutes -> stringResource(R.string.journal_special_aggressive_minutes, hero.name)
        HeroSpecial.TerrainManual -> stringResource(R.string.journal_special_terrain_manual, hero.name)
        HeroSpecial.UnstableLuck -> stringResource(R.string.journal_special_unstable_luck, hero.name)
        HeroSpecial.DirtyJackpot -> stringResource(R.string.journal_special_dirty_jackpot, hero.name)
        HeroSpecial.FreshTrail -> stringResource(R.string.journal_special_fresh_trail, hero.name)
        HeroSpecial.CleanBlessing -> stringResource(R.string.journal_special_clean_blessing, hero.name)
        HeroSpecial.NoTrace -> stringResource(R.string.journal_special_no_trace, hero.name)
        HeroSpecial.NecroLever -> stringResource(R.string.journal_special_necro_lever, hero.name)
        HeroSpecial.HostileAudit -> stringResource(R.string.journal_special_hostile_audit, hero.name)
        HeroSpecial.UnbreakableOath -> stringResource(R.string.journal_special_unbreakable_oath, hero.name)
        HeroSpecial.BalancedBooks -> stringResource(R.string.journal_special_balanced_books, hero.name)
        HeroSpecial.GreenThumb -> stringResource(R.string.journal_special_green_thumb, hero.name)
        HeroSpecial.DeathDiscount -> stringResource(R.string.journal_special_death_discount, hero.name)
        HeroSpecial.MoraleRations -> stringResource(R.string.journal_special_morale_rations, hero.name)
        HeroSpecial.PlanBExplosives -> stringResource(R.string.journal_special_plan_b_explosives, hero.name)
        HeroSpecial.PreservationSalt -> stringResource(R.string.journal_special_preservation_salt, hero.name)
        HeroSpecial.CreativeMisunderstanding -> if (quest.hasAny(QuestTag.Paperwork, QuestTag.Contract)) {
            stringResource(R.string.journal_special_creative_misunderstanding_paperwork, hero.name)
        } else {
            stringResource(R.string.journal_special_creative_misunderstanding, hero.name)
        }
    }
}
@Composable
internal fun journalHeroText(id: String): String? {
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
internal fun journalRewardText(id: String): String? {
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
internal fun lootItemName(item: LootItem): String =
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
internal fun lootRarityLabel(rarity: LootRarity): String =
    when (rarity) {
        LootRarity.Common -> stringResource(R.string.loot_rarity_common)
        LootRarity.Uncommon -> stringResource(R.string.loot_rarity_uncommon)
        LootRarity.Rare -> stringResource(R.string.loot_rarity_rare)
        LootRarity.Epic -> stringResource(R.string.loot_rarity_epic)
        LootRarity.Relic -> stringResource(R.string.loot_rarity_relic)
    }

@Composable
internal fun lootSlotLabel(slot: LootSlot): String =
    when (slot) {
        LootSlot.Weapon -> stringResource(R.string.loot_slot_weapon)
        LootSlot.Armor -> stringResource(R.string.loot_slot_armor)
        LootSlot.Footwear -> stringResource(R.string.loot_slot_footwear)
        LootSlot.Trinket -> stringResource(R.string.loot_slot_trinket)
        LootSlot.Headgear -> stringResource(R.string.loot_slot_headgear)
        LootSlot.Consumable -> stringResource(R.string.loot_slot_consumable)
    }

@Composable
internal fun lootItemSummary(item: LootItem, totalItems: Int): String =
    stringResource(
        R.string.loot_generated_summary,
        lootRarityLabel(item.rarity),
        lootSlotLabel(item.slot),
        item.bonus,
        totalItems,
    )


@Composable
internal fun statTypeLabel(type: StatType): String =
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
internal fun statTypeLabelMap(): Map<StatType, String> = mapOf(
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
internal fun lootStatsSummary(item: LootItem): String {
    val statLine = if (item.stats.isEmpty()) {
        stringResource(R.string.loot_stats_legacy, item.bonus)
    } else {
        val labels = statTypeLabelMap()
        item.stats.joinToString(" / ") { stat -> "+${stat.value} ${labels.getValue(stat.type)}" }
    }
    return if (item.isPerfect) "$statLine - ${stringResource(R.string.loot_perfect_label)}" else statLine
}
@Composable
internal fun lootRewardChoiceSummary(item: LootItem, pendingItems: Int, remainingChoices: Int): String =
    stringResource(
        R.string.loot_reward_choice_summary,
        lootRarityLabel(item.rarity),
        lootSlotLabel(item.slot),
        item.bonus,
        pendingItems,
        remainingChoices,
    ) + "\n" + lootStatsSummary(item)
@Composable
internal fun lootKeepSellSummary(item: LootItem, totalItems: Int, sellValue: Int): String =
    stringResource(
        R.string.loot_keep_sell_summary,
        lootRarityLabel(item.rarity),
        lootSlotLabel(item.slot),
        item.bonus,
        totalItems,
        sellValue,
    ) + "\n" + lootStatsSummary(item)

internal fun togglePartyHero(currentIds: List<String>, heroId: String, maxSlots: Int): List<String> {
    val normalized = currentIds.distinct()
    if (heroId in normalized) return normalized - heroId
    if (normalized.size >= maxSlots) return normalized
    return normalized + heroId
}

@Composable
internal fun questUnlockDetail(session: PlaySessionState, quest: Quest): String {
    val requirements = quest.unlockRequirement.conditions
        .map { condition -> questUnlockConditionLabel(session, condition) }
        .filter { label -> label.isNotBlank() }
    return stringResource(
        R.string.quest_unlock_detail,
        requirements.joinToString(stringResource(R.string.quest_unlock_joiner)),
    )
}

@Composable
internal fun questUnlockConditionLabel(session: PlaySessionState, condition: QuestUnlockCondition): String {
    val parts = listOfNotNull(
        condition.minReputation.takeIf { it > 0 }?.let {
            stringResource(R.string.quest_unlock_reputation, session.reputation, it)
        },
        condition.minCompletedQuestCount.takeIf { it > 0 }?.let {
            stringResource(R.string.quest_unlock_completed_quests, session.completedQuestCount, it)
        },
        condition.minNoticeBoardLevel.takeIf { it > 0 }?.let {
            stringResource(R.string.quest_unlock_notice_board_level, session.noticeBoardLevel, it)
        },
        condition.minTrainingYardLevel.takeIf { it > 0 }?.let {
            stringResource(R.string.quest_unlock_training_yard_level, session.trainingYardLevel, it)
        },
        condition.minBunkRoomLevel.takeIf { it > 0 }?.let {
            stringResource(R.string.quest_unlock_bunk_room_level, session.bunkRoomLevel, it)
        },
    )
    return parts.joinToString(stringResource(R.string.quest_unlock_condition_joiner))
}
internal fun specialEstimateDetail(activeSpecials: List<Hero>, estimate: com.ayoshy.badventurers.game.ExpeditionEstimate): String {
    if (activeSpecials.isEmpty()) return "No active specials for these tags."
    val heroNames = activeSpecials.joinToString { it.name }
    val effects = listOfNotNull(
        estimate.specialRiskReduction.takeIf { it > 0 }?.let { "risk -$it" },
        estimate.minimumRoll.takeIf { it > 0 }?.let { "roll floor $it" },
        estimate.goldBonusPercent.takeIf { it > 0 }?.let { "gold +$it%" },
        estimate.bonusLootRolls.takeIf { it > 0 }?.let { "loot +$it" },
    ).joinToString()
    return if (effects.isBlank()) heroNames else "$heroNames: $effects"
}

@Composable
internal fun resultCauseTitle(cause: ResultCause): String =
    when (cause.kind) {
        ResultCauseKind.Plan -> stringResource(
            R.string.result_cause_plan_title,
            expeditionPlanTitle(ExpeditionPlanCatalog.byId(cause.planId)),
        )
        ResultCauseKind.HeroSpecial -> stringResource(
            R.string.result_cause_hero_title,
            cause.heroName ?: stringResource(R.string.result_cause_unknown_hero),
        )
        ResultCauseKind.Facility -> when (cause.facility) {
            ResultCauseFacility.TrainingYard -> stringResource(R.string.guild_facility_training_yard)
            ResultCauseFacility.NoticeBoard -> stringResource(R.string.guild_facility_notice_board)
            null -> stringResource(R.string.result_cause_facility_title)
        }
        ResultCauseKind.Achievement -> when (cause.achievementFeature) {
            AchievementFeature.InsuranceDesk -> stringResource(R.string.result_cause_achievement_insurance)
            AchievementFeature.RewardChoice -> stringResource(R.string.result_cause_achievement_reward_choice)
            AchievementFeature.GuildCharterBonuses -> stringResource(R.string.result_cause_achievement_charter)
            AchievementFeature.HeroMentorship -> stringResource(R.string.result_cause_achievement_mentorship)
            AchievementFeature.AdvancedContracts -> stringResource(R.string.result_cause_achievement_advanced_contracts)
            AchievementFeature.TrophyLedger,
            null -> stringResource(R.string.result_cause_achievement_title)
        }
    }

@Composable
internal fun resultCauseValue(cause: ResultCause): String =
    when (cause.kind) {
        ResultCauseKind.Plan -> stringResource(R.string.result_cause_value_signed)
        ResultCauseKind.HeroSpecial -> stringResource(R.string.result_cause_value_special)
        ResultCauseKind.Facility -> stringResource(R.string.result_cause_value_facility)
        ResultCauseKind.Achievement -> stringResource(R.string.result_cause_value_charter)
    }

@Composable
internal fun resultCauseDetail(cause: ResultCause): String {
    val effects = resultCauseEffects(cause)
    return effects.ifEmpty { listOf(stringResource(R.string.plan_effect_standard)) }.joinToString(" / ")
}

@Composable
internal fun resultCauseEffects(cause: ResultCause): List<String> = listOfNotNull(
    cause.durationDeltaSeconds.takeIf { it < 0 }?.let { stringResource(R.string.result_cause_effect_duration_faster, -it) },
    cause.durationDeltaSeconds.takeIf { it > 0 }?.let { stringResource(R.string.result_cause_effect_duration_longer, it) },
    cause.scoreBonus.takeIf { it > 0 }?.let { stringResource(R.string.plan_effect_power_up, it) },
    cause.scoreBonus.takeIf { it < 0 }?.let { stringResource(R.string.plan_effect_power_down, -it) },
    cause.riskDelta.takeIf { it > 0 }?.let { stringResource(R.string.plan_effect_risk_up, it) },
    cause.riskDelta.takeIf { it < 0 }?.let { stringResource(R.string.plan_effect_risk_down, -it) },
    cause.goldBonusPercent.takeIf { it > 0 }?.let { stringResource(R.string.plan_effect_gold_up, it) },
    cause.lootBonus.takeIf { it > 0 }?.let { stringResource(R.string.plan_effect_loot_up, it) },
    cause.xpBonus.takeIf { it > 0 }?.let { stringResource(R.string.result_cause_effect_xp_up, it) },
    cause.minimumRoll.takeIf { it > 0 }?.let { stringResource(R.string.result_cause_effect_roll_floor, it) },
    cause.greatSuccessMarginDelta.takeIf { it > 0 }?.let { stringResource(R.string.plan_effect_great_success_harder, it) },
    cause.preventsRidiculousFailure.takeIf { it }?.let { stringResource(R.string.result_cause_effect_blocks_ridiculous_failure) },
)
internal val localizedExpeditionPlanTextIds = setOf(
    ExpeditionPlanCatalog.defaultPlanId,
    ExpeditionPlanCatalog.rushTheJobId,
    ExpeditionPlanCatalog.safetyFirstId,
    ExpeditionPlanCatalog.lootPriorityId,
    ExpeditionPlanCatalog.auditEverythingId,
    ExpeditionPlanCatalog.sealSideTunnelId,
    ExpeditionPlanCatalog.followWorstMapId,
    ExpeditionPlanCatalog.demandReceiptsId,
    ExpeditionPlanCatalog.blessTheBrineId,
    ExpeditionPlanCatalog.moonlessShortcutId,
    ExpeditionPlanCatalog.rationTheBiscuitsId,
    ExpeditionPlanCatalog.bringDoorFormsId,
    ExpeditionPlanCatalog.itemizedLastRitesId,
    ExpeditionPlanCatalog.paperworkTollId,
    ExpeditionPlanCatalog.caravanManifestId,
    ExpeditionPlanCatalog.notaryNightPatrolId,
    ExpeditionPlanCatalog.inspectorateBanquetId,
    ExpeditionPlanCatalog.weddingOathLedgerId,
    ExpeditionPlanCatalog.sunkenTollDredgeId,
    ExpeditionPlanCatalog.crownReceiptSubpoenaId,
    ExpeditionPlanCatalog.sidewaysTowerBraceId,
)
@Composable
internal fun expeditionPlanTitle(plan: ExpeditionPlan): String =
    when (plan.id) {
        ExpeditionPlanCatalog.rushTheJobId -> stringResource(R.string.plan_title_rush_the_job)
        ExpeditionPlanCatalog.safetyFirstId -> stringResource(R.string.plan_title_safety_first)
        ExpeditionPlanCatalog.lootPriorityId -> stringResource(R.string.plan_title_loot_priority)
        ExpeditionPlanCatalog.auditEverythingId -> stringResource(R.string.plan_title_audit_everything)
        ExpeditionPlanCatalog.sealSideTunnelId -> stringResource(R.string.plan_title_seal_side_tunnel)
        ExpeditionPlanCatalog.followWorstMapId -> stringResource(R.string.plan_title_follow_worst_map)
        ExpeditionPlanCatalog.demandReceiptsId -> stringResource(R.string.plan_title_demand_receipts)
        ExpeditionPlanCatalog.blessTheBrineId -> stringResource(R.string.plan_title_bless_the_brine)
        ExpeditionPlanCatalog.moonlessShortcutId -> stringResource(R.string.plan_title_moonless_shortcut)
        ExpeditionPlanCatalog.rationTheBiscuitsId -> stringResource(R.string.plan_title_ration_the_biscuits)
        ExpeditionPlanCatalog.bringDoorFormsId -> stringResource(R.string.plan_title_bring_door_forms)
        ExpeditionPlanCatalog.itemizedLastRitesId -> stringResource(R.string.plan_title_itemized_last_rites)
        ExpeditionPlanCatalog.paperworkTollId -> stringResource(R.string.plan_title_stamp_border_toll)
        ExpeditionPlanCatalog.caravanManifestId -> stringResource(R.string.plan_title_demand_manifest)
        ExpeditionPlanCatalog.notaryNightPatrolId -> stringResource(R.string.plan_title_oath_night)
        ExpeditionPlanCatalog.inspectorateBanquetId -> stringResource(R.string.plan_title_banquet_etiquette)
        ExpeditionPlanCatalog.weddingOathLedgerId -> stringResource(R.string.plan_title_audit_the_vows)
        ExpeditionPlanCatalog.sunkenTollDredgeId -> stringResource(R.string.plan_title_dredge_lost_drawer)
        ExpeditionPlanCatalog.crownReceiptSubpoenaId -> stringResource(R.string.plan_title_subpoena_crown)
        ExpeditionPlanCatalog.sidewaysTowerBraceId -> stringResource(R.string.plan_title_brace_sideways_stairs)
        ExpeditionPlanCatalog.defaultPlanId -> stringResource(R.string.plan_title_standard_contract)
        else -> plan.title
    }

@Composable
internal fun expeditionPlanSummary(plan: ExpeditionPlan): String =
    when (plan.id) {
        ExpeditionPlanCatalog.rushTheJobId -> stringResource(R.string.plan_summary_rush_the_job)
        ExpeditionPlanCatalog.safetyFirstId -> stringResource(R.string.plan_summary_safety_first)
        ExpeditionPlanCatalog.lootPriorityId -> stringResource(R.string.plan_summary_loot_priority)
        ExpeditionPlanCatalog.auditEverythingId -> stringResource(R.string.plan_summary_audit_everything)
        ExpeditionPlanCatalog.sealSideTunnelId -> stringResource(R.string.plan_summary_seal_side_tunnel)
        ExpeditionPlanCatalog.followWorstMapId -> stringResource(R.string.plan_summary_follow_worst_map)
        ExpeditionPlanCatalog.demandReceiptsId -> stringResource(R.string.plan_summary_demand_receipts)
        ExpeditionPlanCatalog.blessTheBrineId -> stringResource(R.string.plan_summary_bless_the_brine)
        ExpeditionPlanCatalog.moonlessShortcutId -> stringResource(R.string.plan_summary_moonless_shortcut)
        ExpeditionPlanCatalog.rationTheBiscuitsId -> stringResource(R.string.plan_summary_ration_the_biscuits)
        ExpeditionPlanCatalog.bringDoorFormsId -> stringResource(R.string.plan_summary_bring_door_forms)
        ExpeditionPlanCatalog.itemizedLastRitesId -> stringResource(R.string.plan_summary_itemized_last_rites)
        ExpeditionPlanCatalog.paperworkTollId -> stringResource(R.string.plan_summary_stamp_border_toll)
        ExpeditionPlanCatalog.caravanManifestId -> stringResource(R.string.plan_summary_demand_manifest)
        ExpeditionPlanCatalog.notaryNightPatrolId -> stringResource(R.string.plan_summary_oath_night)
        ExpeditionPlanCatalog.inspectorateBanquetId -> stringResource(R.string.plan_summary_banquet_etiquette)
        ExpeditionPlanCatalog.weddingOathLedgerId -> stringResource(R.string.plan_summary_audit_the_vows)
        ExpeditionPlanCatalog.sunkenTollDredgeId -> stringResource(R.string.plan_summary_dredge_lost_drawer)
        ExpeditionPlanCatalog.crownReceiptSubpoenaId -> stringResource(R.string.plan_summary_subpoena_crown)
        ExpeditionPlanCatalog.sidewaysTowerBraceId -> stringResource(R.string.plan_summary_brace_sideways_stairs)
        ExpeditionPlanCatalog.defaultPlanId -> stringResource(R.string.plan_summary_standard_contract)
        else -> plan.summary
    }

@Composable
internal fun expeditionPlanEffectSummary(plan: ExpeditionPlan, quest: Quest): String {
    val modifiers = ExpeditionPlanCatalog.modifiersFor(plan.id, quest)
    val effects = listOfNotNull(
        modifiers.durationPercentDelta.takeIf { it < 0 }?.let { stringResource(R.string.plan_effect_duration_shorter, -it) },
        modifiers.durationPercentDelta.takeIf { it > 0 }?.let { stringResource(R.string.plan_effect_duration_longer, it) },
        modifiers.scoreBonus.takeIf { it > 0 }?.let { stringResource(R.string.plan_effect_power_up, it) },
        modifiers.scoreBonus.takeIf { it < 0 }?.let { stringResource(R.string.plan_effect_power_down, -it) },
        modifiers.riskPenaltyDelta.takeIf { it > 0 }?.let { stringResource(R.string.plan_effect_risk_up, it) },
        modifiers.riskPenaltyDelta.takeIf { it < 0 }?.let { stringResource(R.string.plan_effect_risk_down, -it) },
        modifiers.goldBonusPercent.takeIf { it > 0 }?.let { stringResource(R.string.plan_effect_gold_up, it) },
        modifiers.goldBonusPercent.takeIf { it < 0 }?.let { stringResource(R.string.plan_effect_gold_down, -it) },
        modifiers.successLootBonus.takeIf { it > 0 }?.let { stringResource(R.string.plan_effect_loot_up, it) },
        modifiers.greatSuccessMarginDelta.takeIf { it > 0 }?.let { stringResource(R.string.plan_effect_great_success_harder, it) },
    )
    return effects.ifEmpty { listOf(stringResource(R.string.plan_effect_standard)) }.joinToString(" / ")
}
@Composable
internal fun questDifficultyLabel(tier: QuestDifficultyTier): String =
    when (tier) {
        QuestDifficultyTier.Errand -> stringResource(R.string.quest_difficulty_errand)
        QuestDifficultyTier.Trouble -> stringResource(R.string.quest_difficulty_trouble)
        QuestDifficultyTier.Hazard -> stringResource(R.string.quest_difficulty_hazard)
        QuestDifficultyTier.Disaster -> stringResource(R.string.quest_difficulty_disaster)
        QuestDifficultyTier.LegendaryMess -> stringResource(R.string.quest_difficulty_legendary_mess)
    }

internal val localizedQuestTextIds = setOf(
    "cave_minor_regrets",
    "forest_of_wrong_turns",
    "bandit_tax_office",
    "salted_swamp_chapel",
    "moonlit_smuggler_run",
    "the_hungry_siege",
    "the_last_locked_door",
    "crypt_of_unpaid_debts",
    "paperwork_toll_of_chaos",
    "licensed_guild_caravan_haunt",
    "notary_night_patrol",
    "inspectorate_cove_banquet",
    "wedding_with_too_many_oaths",
    "the_sunken_toll_booth",
    "the_crowns_missing_receipt",
    "the_tower_built_sideways",
)
@Composable
internal fun questTitle(quest: Quest): String =
    when (quest.id) {
        "cave_minor_regrets" -> stringResource(R.string.quest_title_cave_minor_regrets)
        "forest_of_wrong_turns" -> stringResource(R.string.quest_title_forest_of_wrong_turns)
        "bandit_tax_office" -> stringResource(R.string.quest_title_bandit_tax_office)
        "salted_swamp_chapel" -> stringResource(R.string.quest_title_salted_swamp_chapel)
        "moonlit_smuggler_run" -> stringResource(R.string.quest_title_moonlit_smuggler_run)
        "the_hungry_siege" -> stringResource(R.string.quest_title_the_hungry_siege)
        "the_last_locked_door" -> stringResource(R.string.quest_title_the_last_locked_door)
        "crypt_of_unpaid_debts" -> stringResource(R.string.quest_title_crypt_of_unpaid_debts)
        "paperwork_toll_of_chaos" -> stringResource(R.string.quest_title_paperwork_toll_of_chaos)
        "licensed_guild_caravan_haunt" -> stringResource(R.string.quest_title_licensed_guild_caravan_haunt)
        "notary_night_patrol" -> stringResource(R.string.quest_title_notary_night_patrol)
        "inspectorate_cove_banquet" -> stringResource(R.string.quest_title_inspectorate_cove_banquet)
        "wedding_with_too_many_oaths" -> stringResource(R.string.quest_title_wedding_with_too_many_oaths)
        "the_sunken_toll_booth" -> stringResource(R.string.quest_title_the_sunken_toll_booth)
        "the_crowns_missing_receipt" -> stringResource(R.string.quest_title_the_crowns_missing_receipt)
        "the_tower_built_sideways" -> stringResource(R.string.quest_title_the_tower_built_sideways)
        else -> quest.title
    }

@Composable
internal fun questSummary(quest: Quest): String =
    when (quest.id) {
        "cave_minor_regrets" -> stringResource(R.string.quest_summary_cave_minor_regrets)
        "forest_of_wrong_turns" -> stringResource(R.string.quest_summary_forest_of_wrong_turns)
        "bandit_tax_office" -> stringResource(R.string.quest_summary_bandit_tax_office)
        "salted_swamp_chapel" -> stringResource(R.string.quest_summary_salted_swamp_chapel)
        "moonlit_smuggler_run" -> stringResource(R.string.quest_summary_moonlit_smuggler_run)
        "the_hungry_siege" -> stringResource(R.string.quest_summary_the_hungry_siege)
        "the_last_locked_door" -> stringResource(R.string.quest_summary_the_last_locked_door)
        "crypt_of_unpaid_debts" -> stringResource(R.string.quest_summary_crypt_of_unpaid_debts)
        "paperwork_toll_of_chaos" -> stringResource(R.string.quest_summary_paperwork_toll_of_chaos)
        "licensed_guild_caravan_haunt" -> stringResource(R.string.quest_summary_licensed_guild_caravan_haunt)
        "notary_night_patrol" -> stringResource(R.string.quest_summary_notary_night_patrol)
        "inspectorate_cove_banquet" -> stringResource(R.string.quest_summary_inspectorate_cove_banquet)
        "wedding_with_too_many_oaths" -> stringResource(R.string.quest_summary_wedding_with_too_many_oaths)
        "the_sunken_toll_booth" -> stringResource(R.string.quest_summary_the_sunken_toll_booth)
        "the_crowns_missing_receipt" -> stringResource(R.string.quest_summary_the_crowns_missing_receipt)
        "the_tower_built_sideways" -> stringResource(R.string.quest_summary_the_tower_built_sideways)
        else -> quest.summary
    }

@Composable
internal fun questTagsLabel(tags: List<QuestTag>): String {
    val labels = mutableListOf<String>()
    for (index in 0 until minOf(tags.size, 4)) {
        labels += questTagLabel(tags[index])
    }
    return labels.joinToString(" / ")
}

@Composable
internal fun questTagLabel(tag: QuestTag): String =
    when (tag) {
        QuestTag.Ancient -> stringResource(R.string.quest_tag_ancient)
        QuestTag.Bandit -> stringResource(R.string.quest_tag_bandit)
        QuestTag.Breach -> stringResource(R.string.quest_tag_breach)
        QuestTag.Camp -> stringResource(R.string.quest_tag_camp)
        QuestTag.Cave -> stringResource(R.string.quest_tag_cave)
        QuestTag.Collapse -> stringResource(R.string.quest_tag_collapse)
        QuestTag.Contract -> stringResource(R.string.quest_tag_contract)
        QuestTag.Curse -> stringResource(R.string.quest_tag_curse)
        QuestTag.Debt -> stringResource(R.string.quest_tag_debt)
        QuestTag.Escort -> stringResource(R.string.quest_tag_escort)
        QuestTag.Exploration -> stringResource(R.string.quest_tag_exploration)
        QuestTag.Guard -> stringResource(R.string.quest_tag_guard)
        QuestTag.Heist -> stringResource(R.string.quest_tag_heist)
        QuestTag.Holy -> stringResource(R.string.quest_tag_holy)
        QuestTag.Hunt -> stringResource(R.string.quest_tag_hunt)
        QuestTag.LongQuest -> stringResource(R.string.quest_tag_long_quest)
        QuestTag.Magic -> stringResource(R.string.quest_tag_magic)
        QuestTag.Obstacle -> stringResource(R.string.quest_tag_obstacle)
        QuestTag.Paperwork -> stringResource(R.string.quest_tag_paperwork)
        QuestTag.Poison -> stringResource(R.string.quest_tag_poison)
        QuestTag.Rot -> stringResource(R.string.quest_tag_rot)
        QuestTag.Siege -> stringResource(R.string.quest_tag_siege)
        QuestTag.Simple -> stringResource(R.string.quest_tag_simple)
        QuestTag.Stealth -> stringResource(R.string.quest_tag_stealth)
        QuestTag.Swamp -> stringResource(R.string.quest_tag_swamp)
        QuestTag.Trap -> stringResource(R.string.quest_tag_trap)
        QuestTag.Undead -> stringResource(R.string.quest_tag_undead)
        QuestTag.Urban -> stringResource(R.string.quest_tag_urban)
        QuestTag.Wall -> stringResource(R.string.quest_tag_wall)
        QuestTag.Wilderness -> stringResource(R.string.quest_tag_wilderness)
    }

internal fun formatCount(value: Int): String = "%,d".format(value)
