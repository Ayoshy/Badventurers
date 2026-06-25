package com.ayoshy.badventurers.storage

import com.ayoshy.badventurers.game.GuildFacility
import com.ayoshy.badventurers.game.ExpeditionPlanCatalog
import com.ayoshy.badventurers.game.GuildFacilityCatalog
import com.ayoshy.badventurers.game.HeroCatalog
import com.ayoshy.badventurers.game.LootCarryBreakdown
import com.ayoshy.badventurers.game.LootGenerator
import com.ayoshy.badventurers.game.PassiveIncomeReport
import com.ayoshy.badventurers.game.PassiveIncident
import com.ayoshy.badventurers.game.PassiveIncidentReward
import com.ayoshy.badventurers.game.PlaySessionSnapshot
import com.ayoshy.badventurers.game.PlaySessionState
import com.ayoshy.badventurers.game.RecruitmentTicketCatalog
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class PlaySessionSnapshotJsonTest {
    @Test
    fun codecRoundTripsFacilityLevels() {
        val state = PlaySessionState.initial().copy(
            gold = 1_250,
            reputation = 11,
            guildLevel = 4,
            completedQuestCount = 9,
            noticeBoardLevel = 3,
            trainingYardLevel = 4,
            bunkRoomLevel = 2,
            lootRolls = 6,
        )
        val snapshot = PlaySessionSnapshot.fromState(state)

        val decoded = PlaySessionSnapshotJson.decode(PlaySessionSnapshotJson.encode(snapshot))
        val restored = decoded?.toState()

        assertNotNull(decoded)
        assertNull(PlaySessionSnapshotJson.lastDecodeFailure)
        assertEquals(PlaySessionSnapshot.CURRENT_VERSION, decoded?.version)
        assertEquals(3, restored?.noticeBoardLevel)
        assertEquals(4, restored?.trainingYardLevel)
        assertEquals(2, restored?.bunkRoomLevel)
        assertEquals(state.gold, restored?.gold)
        assertEquals(state.reputation, restored?.reputation)
        assertEquals(state.guildLevel, restored?.guildLevel)
        assertEquals(state.completedQuestCount, restored?.completedQuestCount)
    }

    @Test
    fun codecRoundTripsCoreCrewAndOfflineIncomeReport() {
        val crewIds = HeroCatalog.starterHeroes.take(2).map { it.id }
        val report = PassiveIncomeReport(
            sinceMillis = 1_000L,
            untilMillis = 61_000L,
            elapsedSeconds = 60L,
            cappedSeconds = 60L,
            activeSeconds = 45L,
            idleSeconds = 15L,
            gold = 2,
            goldPerHour = 130,
            activeGoldPerHour = 90,
            coreCrewHeroIds = crewIds,
        )
        val incidents = listOf(
            PassiveIncident(
                id = "watch-ledger",
                text = "Mira turned a loose form into rent.",
                reward = PassiveIncidentReward(gold = 3, reputation = 1),
            ),
        )
        val state = PlaySessionState.initial().copy(
            coreCrewHeroIds = crewIds,
            lastOfflinePassiveIncome = report,
            lastOfflinePassiveIncidents = incidents,
        )

        val restored = PlaySessionSnapshotJson.decode(
            PlaySessionSnapshotJson.encode(PlaySessionSnapshot.fromState(state)),
        )?.toState()

        assertEquals(crewIds, restored?.normalizedCoreCrewHeroIds())
        assertEquals(report, restored?.lastOfflinePassiveIncome)
        assertEquals(incidents, restored?.lastOfflinePassiveIncidents)
    }

    @Test
    fun legacyJsonWithoutFacilityLevelsDefaultsToStarterFacilities() {
        val legacy = JSONObject()
            .put("version", 7)
            .put("gold", 420)
            .put("reputation", 3)
            .put("guildLevel", 2)
            .put("completedQuestCount", 5)
            .put("lootRolls", 1)
            .put("heroIds", JSONArray().put(HeroCatalog.starterHeroes.first().id))
            .toString()

        val restored = PlaySessionSnapshotJson.decode(legacy)?.toState()

        assertEquals(420, restored?.gold)
        assertEquals(3, restored?.reputation)
        assertEquals(2, restored?.guildLevel)
        assertEquals(5, restored?.completedQuestCount)
        assertEquals(1, restored?.noticeBoardLevel)
        assertEquals(1, restored?.trainingYardLevel)
        assertEquals(1, restored?.bunkRoomLevel)
        assertEquals(listOf(HeroCatalog.starterHeroes.first().id), restored?.normalizedCoreCrewHeroIds())
    }

    @Test
    fun migratedJsonClampsFacilityLevelsToImplementedCaps() {
        val encoded = JSONObject()
            .put("version", PlaySessionSnapshot.CURRENT_VERSION)
            .put("gold", 100)
            .put("reputation", 0)
            .put("guildLevel", 1)
            .put("completedQuestCount", 0)
            .put("noticeBoardLevel", 0)
            .put("trainingYardLevel", 99)
            .put("bunkRoomLevel", 99)
            .put("lootRolls", 0)
            .put("heroIds", JSONArray())
            .toString()

        val restored = PlaySessionSnapshotJson.decode(encoded)?.toState()

        assertEquals(1, restored?.noticeBoardLevel)
        assertEquals(
            GuildFacilityCatalog.definition(GuildFacility.TrainingYard).maxLevel,
            restored?.trainingYardLevel,
        )
        assertEquals(
            GuildFacilityCatalog.definition(GuildFacility.BunkRoom).maxLevel,
            restored?.bunkRoomLevel,
        )
    }


    @Test
    fun legacyRunningExpeditionWithoutPlanDefaultsToStandardContract() {
        val encoded = JSONObject()
            .put("version", 10)
            .put("gold", 100)
            .put("reputation", 0)
            .put("guildLevel", 1)
            .put("completedQuestCount", 0)
            .put("noticeBoardLevel", 1)
            .put("trainingYardLevel", 1)
            .put("bunkRoomLevel", 1)
            .put("lootRolls", 0)
            .put("heroIds", JSONArray().put(HeroCatalog.starterHeroes.first().id))
            .put(
                "expedition",
                JSONObject()
                    .put("questId", "cave_minor_regrets")
                    .put("partyHeroIds", JSONArray().put(HeroCatalog.starterHeroes.first().id))
                    .put("startedAtMillis", 1_000L)
                    .put("endsAtMillis", 46_000L),
            )
            .toString()

        val restored = PlaySessionSnapshotJson.decode(encoded)?.toState()

        assertEquals(ExpeditionPlanCatalog.defaultPlanId, restored?.expedition?.planId)
    }

    @Test
    fun codecRoundTripsExpeditionPlan() {
        val running = PlaySessionState.initial().startQuest(
            nowMillis = 1_000L,
            quest = com.ayoshy.badventurers.game.SeedGame.firstQuest,
            planId = ExpeditionPlanCatalog.auditEverythingId,
        )
        val decoded = PlaySessionSnapshotJson.decode(
            PlaySessionSnapshotJson.encode(PlaySessionSnapshot.fromState(running)),
        )
        val restored = decoded?.toState()

        assertEquals(ExpeditionPlanCatalog.auditEverythingId, restored?.expedition?.planId)
        assertEquals(running.expedition?.endsAtMillis, restored?.expedition?.endsAtMillis)
    }
    @Test
    fun codecRoundTripsPendingLootChoiceLimit() {
        val pendingLoot = LootGenerator.generate(2, seed = 21)
        val state = PlaySessionState.initial().copy(
            pendingLootItems = pendingLoot,
            pendingLootKeepLimit = 1,
            pendingLootKeptCount = 0,
        )

        val restored = PlaySessionSnapshotJson.decode(
            PlaySessionSnapshotJson.encode(PlaySessionSnapshot.fromState(state)),
        )?.toState()

        assertEquals(pendingLoot, restored?.pendingLootItems)
        assertEquals(1, restored?.pendingLootEffectiveKeepLimit())
        assertEquals(1, restored?.pendingLootRemainingChoices())
    }

    @Test
    fun codecRoundTripsPendingLootCarryBreakdown() {
        val pendingLoot = LootGenerator.generate(2, seed = 23)
        val breakdown = LootCarryBreakdown(base = 1, bunkRoom = 1, specialist = 1)
        val state = PlaySessionState.initial().copy(
            pendingLootItems = pendingLoot,
            pendingLootKeepLimit = breakdown.total,
            pendingLootKeptCount = 0,
            pendingLootCarryBreakdown = breakdown,
        )

        val restored = PlaySessionSnapshotJson.decode(
            PlaySessionSnapshotJson.encode(PlaySessionSnapshot.fromState(state)),
        )?.toState()

        assertEquals(pendingLoot, restored?.pendingLootItems)
        assertEquals(breakdown, restored?.pendingLootCarryBreakdown)
        assertEquals(breakdown, restored?.pendingLootRecoveryBreakdown())
        assertEquals(3, restored?.pendingLootEffectiveKeepLimit())
    }

    @Test
    fun legacyJsonWithPendingLootDefaultsToOneChoice() {
        val pendingLoot = LootGenerator.generate(2, seed = 22).map { item ->
            JSONObject()
                .put("id", item.id)
                .put("name", item.name)
                .put("rarity", item.rarity.name)
                .put("slot", item.slot.name)
                .put("stats", JSONArray().also { stats ->
                    item.stats.forEach { stat ->
                        stats.put(
                            JSONObject()
                                .put("type", stat.type.name)
                                .put("value", stat.value),
                        )
                    }
                })
                .put("bonus", item.bonus)
                .put("icon", item.icon.name)
        }
        val encoded = JSONObject()
            .put("version", 11)
            .put("gold", 100)
            .put("reputation", 0)
            .put("guildLevel", 1)
            .put("completedQuestCount", 0)
            .put("noticeBoardLevel", 1)
            .put("trainingYardLevel", 1)
            .put("bunkRoomLevel", 1)
            .put("lootRolls", 2)
            .put("heroIds", JSONArray().put(HeroCatalog.starterHeroes.first().id))
            .put("pendingLootItems", JSONArray().also { array -> pendingLoot.forEach { array.put(it) } })
            .toString()

        val restored = PlaySessionSnapshotJson.decode(encoded)?.toState()

        assertEquals(2, restored?.pendingLootItems?.size)
        assertEquals(1, restored?.pendingLootEffectiveKeepLimit())
        assertEquals(1, restored?.pendingLootRemainingChoices())
        assertEquals(LootCarryBreakdown(base = 1), restored?.pendingLootRecoveryBreakdown())
    }
    @Test
    fun codecRoundTripsRecruitmentTickets() {
        val state = PlaySessionState.initial().copy(
            recruitmentTickets = RecruitmentTicketCatalog.normalizedInventory(
                mapOf(
                    RecruitmentTicketCatalog.BASIC_HIRING_VOUCHER_ID to 3,
                    RecruitmentTicketCatalog.RARE_CONTRACT_TICKET_ID to 1,
                ),
            ),
        )

        val restored = PlaySessionSnapshotJson.decode(
            PlaySessionSnapshotJson.encode(PlaySessionSnapshot.fromState(state)),
        )?.toState()

        assertEquals(state.recruitmentTickets, restored?.recruitmentTickets)
    }

    @Test
    fun legacyJsonWithoutRecruitmentTicketsDefaultsToEmptyInventory() {
        val encoded = JSONObject()
            .put("version", 12)
            .put("gold", 100)
            .put("reputation", 0)
            .put("guildLevel", 1)
            .put("completedQuestCount", 0)
            .put("noticeBoardLevel", 1)
            .put("trainingYardLevel", 1)
            .put("bunkRoomLevel", 1)
            .put("lootRolls", 0)
            .put("heroIds", JSONArray().put(HeroCatalog.starterHeroes.first().id))
            .toString()

        val restored = PlaySessionSnapshotJson.decode(encoded)?.toState()

        assertEquals(RecruitmentTicketCatalog.normalizedInventory(), restored?.recruitmentTickets)
    }

    @Test
    fun invalidJsonReturnsNullAndRecordsFailure() {
        assertNull(PlaySessionSnapshotJson.decode("not-json"))
        assertNotNull(PlaySessionSnapshotJson.lastDecodeFailure)
    }
}
