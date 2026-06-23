package com.ayoshy.badventurers.game

import org.junit.Assert.assertEquals
import org.junit.Test

class PlaySessionSnapshotTest {
    @Test
    fun snapshotRoundTripsModifiedIdleState() {
        val extraHero = HeroProgression.withProgress(
            hero = HeroCatalog.byId.getValue("ledger").toHero(),
            level = 6,
            xp = 17,
        )
        val generatedLoot = LootGenerator.generate(2, seed = 5)
        val state = PlaySessionState.initial().copy(
            gold = 2_000,
            reputation = 29,
            guildLevel = 4,
            completedQuestCount = 5,
            noticeBoardLevel = 3,
            trainingYardLevel = 2,
            bunkRoomLevel = 3,
            heroes = HeroCatalog.starterHeroes + extraHero,
            lootRolls = 7,
            lootItems = generatedLoot.drop(1),
            pendingLootItems = generatedLoot.take(1),
            equippedLoot = listOf(EquippedLoot(extraHero.id, generatedLoot.first())),
            journalEntries = listOf(JournalEntry(id = "note-1", text = "The ledger survived.")),
        )

        val snapshot = PlaySessionSnapshot.fromState(state)
        val restored = snapshot.toState()

        assertEquals(PlaySessionSnapshot.CURRENT_VERSION, snapshot.version)
        assertEquals(state, restored)
    }

    @Test
    fun snapshotRestoresRunningExpedition() {
        val startedAt = 1_000L
        val running = PlaySessionState.initial().startQuest(startedAt, SeedGame.firstQuest)

        val restored = PlaySessionSnapshot.fromState(running).toState()

        assertEquals(PlayPhase.Running, restored.phase)
        assertEquals(running.expedition, restored.expedition)
        assertEquals(running.heroes, restored.heroes)
    }


    @Test
    fun snapshotPreservesSelectedExpeditionParty() {
        val selectedParty = HeroCatalog.starterHeroes.take(2)
        val running = PlaySessionState.initial().startQuest(1_000L, SeedGame.firstQuest, selectedParty)

        val restored = PlaySessionSnapshot.fromState(running).toState()

        assertEquals(selectedParty.map { it.id }, restored.expedition?.partyHeroIds)
    }
    @Test
    fun snapshotRestoresReadyResult() {
        val startedAt = 1_000L
        val running = PlaySessionState.initial().startQuest(startedAt, SeedGame.firstQuest)
        val ready = running.finishQuestNow(ExpeditionEngine(), running.heroes, roll = 100)

        val restored = PlaySessionSnapshot.fromState(ready).toState()

        assertEquals(PlayPhase.ResultReady, restored.phase)
        assertEquals(ready.expedition, restored.expedition)
    }

    @Test
    fun snapshotFallsBackToStarterHeroesWhenSavedRosterIsUnknown() {
        val snapshot = PlaySessionSnapshot.fromState(PlaySessionState.initial()).copy(heroIds = listOf("missing"))

        val restored = snapshot.toState()

        assertEquals(HeroCatalog.starterHeroes, restored.heroes)
    }

    @Test
    fun snapshotRestoresKnownLootWithCurrentCatalogDefinition() {
        val staleBoots = LootItemSnapshot(
            id = "armor_winged_boots",
            name = "Winged Boots",
            rarity = LootRarity.Rare,
            slot = LootSlot.Armor,
            bonus = 3,
            icon = LootIcon.Boots,
        )

        val restored = staleBoots.toItem()

        assertEquals(LootSlot.Footwear, restored.slot)
        assertEquals(LootIcon.Boots, restored.icon)
        assertEquals(LootCatalog.byId.getValue("armor_winged_boots").name, restored.name)
    }

    @Test
    fun snapshotMigratesFacilityLevelsIntoCatalogBounds() {
        val snapshot = PlaySessionSnapshot.initial().copy(
            noticeBoardLevel = 0,
            trainingYardLevel = 99,
            bunkRoomLevel = 99,
        )

        val restored = snapshot.toState()

        assertEquals(1, restored.noticeBoardLevel)
        assertEquals(
            GuildFacilityCatalog.definition(GuildFacility.TrainingYard).maxLevel,
            restored.trainingYardLevel,
        )
        assertEquals(
            GuildFacilityCatalog.definition(GuildFacility.BunkRoom).maxLevel,
            restored.bunkRoomLevel,
        )
    }

    @Test
    fun snapshotVersionIsExplicitlyCurrent() {
        val snapshot = PlaySessionSnapshot.initial()

        assertEquals(PlaySessionSnapshot.CURRENT_VERSION, snapshot.version)
    }
}