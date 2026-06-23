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
    fun futureFacilitiesUseUnlockRequirementsBeforeTheyCanUpgrade() {
        val locked = PlaySessionState.initial().copy(
            reputation = 0,
            completedQuestCount = 0,
            noticeBoardLevel = 1,
        )
        val unlocked = locked.copy(
            completedQuestCount = 3,
            noticeBoardLevel = 2,
        )

        assertFalse(locked.facilityUpgradeState(GuildFacility.ArmoryForge).unlocked)
        assertTrue(unlocked.facilityUpgradeState(GuildFacility.ArmoryForge).unlocked)
        assertFalse(unlocked.canUpgradeFacility(GuildFacility.ArmoryForge))
    }
}
