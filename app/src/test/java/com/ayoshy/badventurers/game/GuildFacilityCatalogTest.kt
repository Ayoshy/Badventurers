package com.ayoshy.badventurers.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GuildFacilityCatalogTest {
    @Test
    fun catalogDefinesEveryMvpFacility() {
        assertEquals(
            GuildFacility.values().toSet(),
            GuildFacilityCatalog.definitions.map { it.facility }.toSet(),
        )
        assertTrue(GuildFacilityCatalog.definition(GuildFacility.NoticeBoard).implemented)
        assertTrue(GuildFacilityCatalog.definition(GuildFacility.TrainingYard).implemented)
        assertTrue(GuildFacilityCatalog.definition(GuildFacility.BunkRoom).implemented)
        assertTrue(GuildFacilityCatalog.definition(GuildFacility.ScoutTable).implemented)
        assertTrue(GuildFacilityCatalog.definition(GuildFacility.ArmoryForge).implemented)
        assertTrue(GuildFacilityCatalog.definition(GuildFacility.Infirmary).implemented)
        assertTrue(GuildFacilityCatalog.definition(GuildFacility.TavernKitchen).implemented)
        assertTrue(GuildFacilityCatalog.definition(GuildFacility.AccountantOffice).implemented)
    }

    @Test
    fun catalogMapsFacilitiesToProgressionEffects() {
        val effects = GuildFacilityCatalog.definitions.flatMap { it.effects }.toSet()

        assertTrue(GuildFacilityEffect.QuestUnlocks in effects)
        assertTrue(GuildFacilityEffect.PartySlots in effects)
        assertTrue(GuildFacilityEffect.HeroPower in effects)
        assertTrue(GuildFacilityEffect.HeroXpGain in effects)
        assertTrue(GuildFacilityEffect.RiskMitigation in effects)
        assertTrue(GuildFacilityEffect.ExpeditionDuration in effects)
        assertTrue(GuildFacilityEffect.PlanWarnings in effects)
        assertTrue(GuildFacilityEffect.UnlockPreviews in effects)
        assertTrue(GuildFacilityEffect.PassiveScouting in effects)
        assertTrue(GuildFacilityEffect.LootQuality in effects)
        assertTrue(GuildFacilityEffect.OfflineCap in effects)
        assertTrue(GuildFacilityEffect.RecruitPoolQuality in effects)
        assertTrue(GuildFacilityEffect.GoldReputationYield in effects)
    }

    @Test
    fun implementedFacilityUpgradeUsesCatalogCostAndCapsAtMaxLevel() {
        val definition = GuildFacilityCatalog.definition(GuildFacility.NoticeBoard)
        val state = PlaySessionState.initial().copy(gold = 10_000, noticeBoardLevel = 1)
        val upgraded = state.upgradeFacility(GuildFacility.NoticeBoard)

        assertEquals(state.gold - definition.costForNextLevel(1)!!, upgraded.gold)
        assertEquals(2, upgraded.noticeBoardLevel)

        val maxed = state.copy(noticeBoardLevel = definition.maxLevel)

        assertFalse(maxed.canUpgradeFacility(GuildFacility.NoticeBoard))
        assertEquals(null, maxed.facilityUpgradeState(GuildFacility.NoticeBoard).nextCost)
        assertEquals(maxed, maxed.upgradeFacility(GuildFacility.NoticeBoard))
    }

    @Test
    fun scoutTableUnlockGateAndUpgradeUseZeroBasedFacilityLevel() {
        val definition = GuildFacilityCatalog.definition(GuildFacility.ScoutTable)
        val locked = PlaySessionState.initial().copy(
            gold = 10_000,
            reputation = 7,
            noticeBoardLevel = 2,
        )
        val unlocked = locked.copy(reputation = 8)
        val upgraded = unlocked.upgradeScoutTable()

        assertEquals(0, locked.scoutTableLevel)
        assertFalse(locked.canUpgradeFacility(GuildFacility.ScoutTable))
        assertTrue(unlocked.canUpgradeFacility(GuildFacility.ScoutTable))
        assertEquals(unlocked.gold - definition.costForNextLevel(0)!!, upgraded.gold)
        assertEquals(1, upgraded.scoutTableLevel)
    }

    @Test
    fun scoutTableWarningsChangeThePlanDecisionSurface() {
        val quest = SeedGame.firstQuest
        val rushPlan = ExpeditionPlanCatalog.byId(ExpeditionPlanCatalog.rushTheJobId)
        val lootPlan = ExpeditionPlanCatalog.byId(ExpeditionPlanCatalog.lootPriorityId)

        assertEquals(emptyList<ScoutPlanWarning>(), ScoutTableIntel.planWarningsFor(0, rushPlan, quest))
        assertFalse(ScoutTableIntel.behavior(1).revealsUnlockPreviews)
        assertTrue(ScoutTableIntel.behavior(2).revealsUnlockPreviews)

        val firstWarning = ScoutTableIntel.planWarningsFor(1, rushPlan, quest).single()
        assertEquals(ScoutPlanWarningType.HigherRisk, firstWarning.type)
        assertEquals(12, firstWarning.amount)

        val deeperWarnings = ScoutTableIntel.planWarningsFor(2, lootPlan, quest)
        assertEquals(
            listOf(ScoutPlanWarningType.HigherRisk, ScoutPlanWarningType.LowerPower),
            deeperWarnings.map { it.type },
        )
    }

    @Test
    fun passiveEconomyFacilitiesUseUnlockRequirementsBeforeTheyCanUpgrade() {
        val locked = PlaySessionState.initial().copy(
            reputation = 0,
            completedQuestCount = 0,
            noticeBoardLevel = 1,
            trainingYardLevel = 1,
            gold = 10_000,
        )
        val armoryReady = locked.copy(
            completedQuestCount = 3,
            noticeBoardLevel = 2,
        )
        val tavernReady = locked.copy(
            completedQuestCount = 3,
            trainingYardLevel = 2,
        )
        val infirmaryReady = locked.copy(completedQuestCount = 2)
        val accountantReady = locked.copy(
            reputation = 12,
            noticeBoardLevel = 2,
        )

        assertFalse(locked.facilityUpgradeState(GuildFacility.ArmoryForge).unlocked)
        assertTrue(armoryReady.facilityUpgradeState(GuildFacility.ArmoryForge).unlocked)
        assertTrue(armoryReady.canUpgradeFacility(GuildFacility.ArmoryForge))
        assertEquals(1, armoryReady.upgradeArmoryForge().armoryForgeLevel)

        assertFalse(locked.facilityUpgradeState(GuildFacility.TavernKitchen).unlocked)
        assertTrue(tavernReady.facilityUpgradeState(GuildFacility.TavernKitchen).unlocked)
        assertTrue(tavernReady.canUpgradeFacility(GuildFacility.TavernKitchen))
        assertEquals(1, tavernReady.upgradeTavernKitchen().tavernKitchenLevel)

        assertFalse(locked.facilityUpgradeState(GuildFacility.Infirmary).unlocked)
        assertTrue(infirmaryReady.facilityUpgradeState(GuildFacility.Infirmary).unlocked)
        assertTrue(infirmaryReady.canUpgradeFacility(GuildFacility.Infirmary))
        assertEquals(1, infirmaryReady.upgradeInfirmary().infirmaryLevel)

        assertFalse(locked.facilityUpgradeState(GuildFacility.AccountantOffice).unlocked)
        assertTrue(accountantReady.facilityUpgradeState(GuildFacility.AccountantOffice).unlocked)
        assertTrue(accountantReady.canUpgradeFacility(GuildFacility.AccountantOffice))
        assertEquals(1, accountantReady.upgradeAccountantOffice().accountantOfficeLevel)
    }
}
