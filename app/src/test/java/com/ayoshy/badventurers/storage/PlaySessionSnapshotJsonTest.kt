package com.ayoshy.badventurers.storage

import com.ayoshy.badventurers.game.GuildFacility
import com.ayoshy.badventurers.game.ExpeditionPlanCatalog
import com.ayoshy.badventurers.game.GuildFacilityCatalog
import com.ayoshy.badventurers.game.HeroCatalog
import com.ayoshy.badventurers.game.LootGenerator
import com.ayoshy.badventurers.game.PlaySessionSnapshot
import com.ayoshy.badventurers.game.PlaySessionState
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
    }
    @Test
    fun invalidJsonReturnsNull() {
        assertNull(PlaySessionSnapshotJson.decode("not-json"))
    }
}
