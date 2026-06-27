package com.ayoshy.badventurers.game

import org.junit.Assert.assertEquals
import org.junit.Test

class PlaySessionSnapshotTest {
    @Test
    fun snapshotRoundTripsModifiedIdleState() {
        val extraHero = HeroPromotion.previewPromoted(
            hero = HeroProgression.withProgress(
                hero = HeroCatalog.byId.getValue("ledger").toHero(),
                level = 6,
                xp = 17,
            ),
            targetRank = 2,
        )
        val generatedLoot = LootGenerator.generate(2, seed = 5)
        val state = PlaySessionState.initial().copy(
            gold = 2_000,
            reputation = 29,
            supplies = 13,
            guildLevel = 4,
            completedQuestCount = 5,
            clearedQuestIds = setOf("cave_minor_regrets", "the_tower_built_sideways"),
            noticeBoardLevel = 3,
            trainingYardLevel = 2,
            bunkRoomLevel = 3,
            armoryForgeLevel = 2,
            tavernKitchenLevel = 1,
            infirmaryLevel = 2,
            accountantOfficeLevel = 1,
            heroes = HeroCatalog.starterHeroes + extraHero,
            coreCrewHeroIds = (HeroCatalog.starterHeroes.take(2) + extraHero).map { it.id },
            lootRolls = 7,
            lootItems = generatedLoot.drop(1),
            pendingLootItems = generatedLoot.take(1),
            pendingLootKeepLimit = 1,
            pendingLootKeptCount = 0,
            equippedLoot = listOf(EquippedLoot(extraHero.id, generatedLoot.first())),
            journalEntries = listOf(JournalEntry(id = "note-1", text = "The ledger survived.")),
            lastOfflinePassiveIncome = PassiveIncomeReport(
                sinceMillis = 1_000L,
                untilMillis = 91_000L,
                elapsedSeconds = 90L,
                cappedSeconds = 90L,
                activeSeconds = 45L,
                idleSeconds = 45L,
                gold = 3,
                goldPerHour = 120,
                activeGoldPerHour = 80,
                coreCrewHeroIds = (HeroCatalog.starterHeroes.take(2) + extraHero).map { it.id },
                supplies = 2,
                suppliesPerHour = 8,
                activeSuppliesPerHour = 5,
                lootFinds = generatedLoot.take(1),
            ),
            lastOfflinePassiveIncidents = listOf(
                PassiveIncident(
                    id = "watch-ledger",
                    text = "Brugg watched the ledgers blink.",
                    reward = PassiveIncidentReward(gold = 4, reputation = 1, specialContracts = 1),
                ),
            ),
        )

        val snapshot = PlaySessionSnapshot.fromState(state)
        val restored = snapshot.toState()

        assertEquals(PlaySessionSnapshot.CURRENT_VERSION, snapshot.version)
        assertEquals(state, restored)
        assertEquals(2, restored.heroes.first { it.id == extraHero.id }.promotionRank)
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
    fun snapshotPreservesExpeditionPlan() {
        val running = PlaySessionState.initial().startQuest(
            nowMillis = 1_000L,
            quest = SeedGame.firstQuest,
            planId = ExpeditionPlanCatalog.rushTheJobId,
        )

        val restored = PlaySessionSnapshot.fromState(running).toState()

        assertEquals(ExpeditionPlanCatalog.rushTheJobId, restored.expedition?.planId)
        assertEquals(running.expedition?.endsAtMillis, restored.expedition?.endsAtMillis)
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
            scoutTableLevel = 99,
            armoryForgeLevel = 99,
            tavernKitchenLevel = 99,
            infirmaryLevel = 99,
            accountantOfficeLevel = 99,
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
        assertEquals(
            GuildFacilityCatalog.definition(GuildFacility.ScoutTable).maxLevel,
            restored.scoutTableLevel,
        )
        assertEquals(
            GuildFacilityCatalog.definition(GuildFacility.ArmoryForge).maxLevel,
            restored.armoryForgeLevel,
        )
        assertEquals(
            GuildFacilityCatalog.definition(GuildFacility.TavernKitchen).maxLevel,
            restored.tavernKitchenLevel,
        )
        assertEquals(
            GuildFacilityCatalog.definition(GuildFacility.Infirmary).maxLevel,
            restored.infirmaryLevel,
        )
        assertEquals(
            GuildFacilityCatalog.definition(GuildFacility.AccountantOffice).maxLevel,
            restored.accountantOfficeLevel,
        )
        assertEquals(0, snapshot.copy(scoutTableLevel = -1).toState().scoutTableLevel)
        assertEquals(0, snapshot.copy(armoryForgeLevel = -1).toState().armoryForgeLevel)
        assertEquals(0, snapshot.copy(tavernKitchenLevel = -1).toState().tavernKitchenLevel)
        assertEquals(0, snapshot.copy(infirmaryLevel = -1).toState().infirmaryLevel)
        assertEquals(0, snapshot.copy(accountantOfficeLevel = -1).toState().accountantOfficeLevel)
    }

    @Test
    fun snapshotMigratesCoreCrewToKnownHeroesAndSlotLimit() {
        val roster = HeroCatalog.heroes.map { it.toHero() }
        val snapshot = PlaySessionSnapshot.fromState(
            PlaySessionState.initial().copy(heroes = roster, bunkRoomLevel = 1),
        ).copy(coreCrewHeroIds = roster.map { it.id } + "missing")

        val restored = snapshot.toState()

        assertEquals(3, restored.coreCrewSlots())
        assertEquals(roster.take(3).map { it.id }, restored.normalizedCoreCrewHeroIds())
    }

    @Test
    fun snapshotMigratesPendingLootWithoutCarryLimitToOneChoice() {
        val pendingLoot = LootGenerator.generate(2, seed = 13)
        val snapshot = PlaySessionSnapshot.initial().copy(
            pendingLootItems = pendingLoot.map { LootItemSnapshot.fromItem(it) },
            pendingLootKeepLimit = 0,
            pendingLootKeptCount = 0,
        )

        val restored = snapshot.toState()

        assertEquals(pendingLoot, restored.pendingLootItems)
        assertEquals(1, restored.pendingLootEffectiveKeepLimit())
        assertEquals(1, restored.pendingLootRemainingChoices())
    }
    @Test
    fun snapshotRoundTripsPendingLootCarryBreakdown() {
        val pendingLoot = LootGenerator.generate(2, seed = 31)
        val breakdown = LootCarryBreakdown(base = 1, bunkRoom = 1, veteran = 1)
        val state = PlaySessionState.initial().copy(
            pendingLootItems = pendingLoot,
            pendingLootKeepLimit = breakdown.total,
            pendingLootKeptCount = 0,
            pendingLootCarryBreakdown = breakdown,
        )

        val restored = PlaySessionSnapshot.fromState(state).toState()

        assertEquals(breakdown, restored.pendingLootCarryBreakdown)
        assertEquals(breakdown, restored.pendingLootRecoveryBreakdown())
        assertEquals(3, restored.pendingLootEffectiveKeepLimit())
    }
    @Test
    fun snapshotRoundTripsRecruitmentTickets() {
        val tickets = RecruitmentTicketCatalog.normalizedInventory(
            mapOf(
                RecruitmentTicketCatalog.BASIC_HIRING_VOUCHER_ID to 2,
                RecruitmentTicketCatalog.VETERAN_TICKET_ID to 1,
            ),
        )
        val restored = PlaySessionSnapshot.fromState(
            PlaySessionState.initial().copy(recruitmentTickets = tickets),
        ).toState()

        assertEquals(tickets, restored.recruitmentTickets)
    }

    @Test
    fun snapshotVersionIsExplicitlyCurrent() {
        val snapshot = PlaySessionSnapshot.initial()

        assertEquals(PlaySessionSnapshot.CURRENT_VERSION, snapshot.version)
    }
}
