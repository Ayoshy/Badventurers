package com.ayoshy.badventurers.storage

import com.ayoshy.badventurers.game.GuildFacility
import com.ayoshy.badventurers.game.GuildFacilityCatalog
import com.ayoshy.badventurers.game.HeroCatalog
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
    fun invalidJsonReturnsNull() {
        assertNull(PlaySessionSnapshotJson.decode("not-json"))
    }
}