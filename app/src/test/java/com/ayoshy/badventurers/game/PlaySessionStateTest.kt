package com.ayoshy.badventurers.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaySessionStateTest {
    private val engine = ExpeditionEngine()
    private val party = SeedGame.heroes

    @Test
    fun startQuestEntersRunningAndDefinesEnd() {
        val startedAt = 1_000L
        val state = PlaySessionState.initial().startQuest(startedAt, SeedGame.firstQuest)

        assertEquals(PlayPhase.Running, state.phase)
        assertNotNull(state.expedition)
        assertEquals(
            startedAt + SeedGame.firstQuest.durationSeconds * 1000L,
            state.expedition?.endsAtMillis,
        )
    }


    @Test
    fun startQuestStoresSelectedPartyAndLimitsSlots() {
        val selectedParty = HeroCatalog.heroes.map { it.toHero() }
        val state = PlaySessionState.initial().copy(heroes = selectedParty).startQuest(1_000L, SeedGame.firstQuest, selectedParty)

        assertEquals(SeedGame.firstQuest.partySlots, state.expedition?.partyHeroIds?.size)
        assertEquals(selectedParty.take(SeedGame.firstQuest.partySlots).map { it.id }, state.expedition?.partyHeroIds)
    }


    @Test
    fun startQuestStoresPlanAndAdjustedDuration() {
        val startedAt = 1_000L
        val state = PlaySessionState.initial().startQuest(
            nowMillis = startedAt,
            quest = SeedGame.firstQuest,
            party = party,
            planId = ExpeditionPlanCatalog.rushTheJobId,
        )

        assertEquals(ExpeditionPlanCatalog.rushTheJobId, state.expedition?.planId)
        assertEquals(
            startedAt + ExpeditionPlanCatalog.durationSeconds(SeedGame.firstQuest, ExpeditionPlanCatalog.rushTheJobId) * 1000L,
            state.expedition?.endsAtMillis,
        )
        assertTrue(requireNotNull(state.expedition).endsAtMillis < startedAt + SeedGame.firstQuest.durationSeconds * 1000L)
    }
    @Test
    fun finishQuestUsesStoredPartyInsteadOfFullRoster() {
        val quest = SeedGame.firstQuest.copy(tags = emptyList(), recommendedHeroIds = emptyList())
        val selectedParty = listOf(party.first())
        val running = PlaySessionState.initial().startQuest(1_000L, quest, selectedParty)
        val finished = running.finishQuestNow(engine, party, roll = 0)
        val expectedMargin = PartyPowerCalculator.totalPower(selectedParty) - quest.difficulty

        assertEquals(expectedMargin, finished.expedition?.result?.scoreMargin)
    }
    @Test
    fun lockedQuestCannotStartUntilUnlockRequirementIsMet() {
        val quest = SeedGame.questById.getValue("the_last_locked_door")
        val locked = PlaySessionState.initial().copy(
            reputation = 0,
            completedQuestCount = 0,
            trainingYardLevel = 1,
        )

        assertEquals(false, locked.isQuestUnlocked(quest))
        assertEquals(PlayPhase.Idle, locked.startQuest(1_000L, quest).phase)

        val unlocked = locked.copy(reputation = 22)

        assertEquals(true, unlocked.isQuestUnlocked(quest))
        assertEquals(PlayPhase.Running, unlocked.startQuest(1_000L, quest).phase)
    }

    @Test
    fun questCanUnlockThroughCompletedCountOrFacilityUpgrade() {
        val quest = SeedGame.questById.getValue("crypt_of_unpaid_debts")
        val locked = PlaySessionState.initial().copy(
            reputation = 0,
            completedQuestCount = 0,
            noticeBoardLevel = 1,
        )

        assertEquals(false, locked.isQuestUnlocked(quest))
        assertEquals(true, locked.copy(completedQuestCount = 6).isQuestUnlocked(quest))
        assertEquals(true, locked.copy(noticeBoardLevel = 3).isQuestUnlocked(quest))
    }
    @Test
    fun progressIsBoundedBetweenZeroAndOne() {
        val startedAt = 1_000L
        val state = PlaySessionState.initial().startQuest(startedAt, SeedGame.firstQuest)
        val beforeStart = state.progress(startedAt - 500L)
        val midQuest = state.progress(startedAt + 20_000L)
        val afterEnd = state.progress(startedAt + SeedGame.firstQuest.durationSeconds * 1000L + 500L)

        assertTrue(beforeStart in 0.0..1.0)
        assertTrue(midQuest in 0.0..1.0)
        assertTrue(afterEnd in 0.0..1.0)
    }

    @Test
    fun tickBeforeEndDoesNotResolve() {
        val startedAt = 1_000L
        val state = PlaySessionState.initial().startQuest(startedAt, SeedGame.firstQuest)
        val advanced = state.tick(startedAt + 10_000L, engine, party)

        assertEquals(PlayPhase.Running, advanced.phase)
        assertEquals(null, advanced.expedition?.result)
    }

    @Test
    fun tickAfterEndProducesReadyResult() {
        val startedAt = 1_000L
        val state = PlaySessionState.initial().startQuest(startedAt, SeedGame.firstQuest)
        val finished = state.tick(startedAt + SeedGame.firstQuest.durationSeconds * 1000L, engine, party)

        assertEquals(PlayPhase.ResultReady, finished.phase)
        assertNotNull(finished.expedition?.result)
        assertNotNull(finished.expedition?.result?.reward)
    }

    @Test
    fun collectCreditsGoldAndLootOnlyOnce() {
        val startedAt = 1_000L
        val running = PlaySessionState.initial().startQuest(startedAt, SeedGame.firstQuest)
        val finished = running.tick(startedAt + SeedGame.firstQuest.durationSeconds * 1000L, engine, party)
        val reward = finished.expedition!!.result!!.reward
        val collected = finished.collectResult()

        assertEquals(finished.gold + reward.gold, collected.gold)
        assertEquals(reward.lootRolls, collected.lootRolls)
        assertEquals(PlayPhase.Idle, collected.phase)
        assertEquals(1, collected.completedQuestCount)
        assertEquals(collected, collected.collectResult())
    }

    @Test
    fun collectResultGrantsXpToParticipatingHeroesOnly() {
        val participant = HeroCatalog.byId.getValue("brugg").toHero()
        val benchHero = HeroCatalog.byId.getValue("mira").toHero()
        val earnedXp = HeroProgression.xpForNextLevel(participant.level) + 5
        val ready = PlaySessionState.initial().copy(
            heroes = listOf(participant, benchHero),
            expedition = ExpeditionRun(
                quest = SeedGame.firstQuest,
                partyHeroIds = listOf(participant.id),
                startedAtMillis = 1_000L,
                endsAtMillis = 2_000L,
                result = ExpeditionResult(
                    outcome = ExpeditionOutcome.Success,
                    reward = Reward(gold = 0, xp = earnedXp, lootRolls = 0),
                    scoreMargin = 10,
                ),
            ),
        )

        val collected = ready.collectResult()
        val advancedHero = collected.heroes.first { it.id == participant.id }
        val unchangedBenchHero = collected.heroes.first { it.id == benchHero.id }

        assertEquals(participant.level + 1, advancedHero.level)
        assertEquals(5, advancedHero.xp)
        assertEquals(benchHero, unchangedBenchHero)
    }
    @Test
    fun upgradedTrainingYardAddsQuestXpBonusToParticipatingHeroesOnly() {
        val participant = HeroCatalog.byId.getValue("brugg").toHero()
        val benchHero = HeroCatalog.byId.getValue("mira").toHero()
        val ready = PlaySessionState.initial().copy(
            trainingYardLevel = 3,
            heroes = listOf(participant, benchHero),
            expedition = ExpeditionRun(
                quest = SeedGame.firstQuest,
                partyHeroIds = listOf(participant.id),
                startedAtMillis = 1_000L,
                endsAtMillis = 2_000L,
                result = ExpeditionResult(
                    outcome = ExpeditionOutcome.Success,
                    reward = Reward(gold = 0, xp = 20, lootRolls = 0),
                    scoreMargin = 10,
                ),
            ),
        )

        assertEquals(20, ready.expedition?.result?.reward?.xp)
        assertEquals(20, ready.trainingYardQuestXpBonusPercent())
        assertEquals(24, ready.collectableHeroXp(requireNotNull(ready.expedition?.result)))

        val collected = ready.collectResult()
        val advancedHero = collected.heroes.first { it.id == participant.id }
        val unchangedBenchHero = collected.heroes.first { it.id == benchHero.id }

        assertEquals(24, advancedHero.xp)
        assertEquals(benchHero, unchangedBenchHero)
    }

    @Test
    fun upgradeNoticeBoardConsumesGoldAndIncreasesLevel() {
        val state = PlaySessionState.initial().copy(gold = 600)
        val upgraded = state.upgradeNoticeBoard()

        assertEquals(state.gold - 600, upgraded.gold)
        assertEquals(2, upgraded.noticeBoardLevel)
    }

    @Test
    fun upgradeNoticeBoardFailsWithoutEnoughGold() {
        val state = PlaySessionState.initial().copy(gold = 599)

        assertEquals(state, state.upgradeNoticeBoard())
    }

    @Test
    fun collectAppliesNoticeBoardBonus() {
        val ready = PlaySessionState.initial().copy(
            noticeBoardLevel = 2,
            expedition = ExpeditionRun(
                quest = SeedGame.firstQuest,
                startedAtMillis = 1_000L,
                endsAtMillis = 2_000L,
                result = ExpeditionResult(
                    outcome = ExpeditionOutcome.Success,
                    reward = Reward(gold = 100, xp = 10, lootRolls = 1),
                    scoreMargin = 10,
                ),
            ),
        )

        val collected = ready.collectResult()

        assertEquals(ready.gold + 110, collected.gold)
        assertEquals(1, collected.lootRolls)
    }

    @Test
    fun upgradeTrainingYardConsumesGoldAndIncreasesPowerBonus() {
        val state = PlaySessionState.initial().copy(gold = 450)
        val upgraded = state.upgradeTrainingYard()

        assertEquals(state.gold - state.trainingYardUpgradeCost(), upgraded.gold)
        assertEquals(2, upgraded.trainingYardLevel)
        assertEquals(8, upgraded.trainingYardPowerBonus())
    }

    @Test
    fun upgradeBunkRoomAddsEffectiveQuestPartySlot() {
        val upgraded = PlaySessionState.initial()
            .copy(gold = 750, heroes = HeroCatalog.heroes.map { it.toHero() })
            .upgradeBunkRoom()

        val selectedParty = upgraded.selectedPartyForQuest(SeedGame.firstQuest, upgraded.heroes)

        assertEquals(SeedGame.firstQuest.partySlots + 1, upgraded.effectivePartySlots(SeedGame.firstQuest))
        assertEquals(SeedGame.firstQuest.partySlots + 1, selectedParty.size)
    }

    @Test
    fun finishQuestAppliesTrainingYardPowerBonus() {
        val quest = SeedGame.firstQuest.copy(tags = emptyList(), recommendedHeroIds = emptyList())
        val selectedParty = listOf(party.first())
        val running = PlaySessionState.initial()
            .copy(gold = 450)
            .upgradeTrainingYard()
            .startQuest(1_000L, quest, selectedParty)
        val finished = running.finishQuestNow(engine, party, roll = 0)
        val expectedMargin = PartyPowerCalculator.totalPower(selectedParty) + running.trainingYardPowerBonus() - quest.difficulty

        assertEquals(expectedMargin, finished.expedition?.result?.scoreMargin)
    }


    @Test
    fun collectResultAppliesFakeRewardedAdBonus() {
        val ready = PlaySessionState.initial().copy(
            expedition = ExpeditionRun(
                quest = SeedGame.firstQuest,
                startedAtMillis = 1_000L,
                endsAtMillis = 2_000L,
                result = ExpeditionResult(
                    outcome = ExpeditionOutcome.Success,
                    reward = Reward(gold = 100, xp = 10, lootRolls = 2),
                    scoreMargin = 10,
                ),
            ),
        )
        val fakeReward = requireNotNull(FakeRewardedAdService.rewardFor(ready))

        val collected = ready.collectResult(fakeRewardedAdReward = fakeReward)

        assertEquals(ready.gold + 200, collected.gold)
        assertEquals(4, collected.lootRolls)
        assertEquals(4, collected.pendingLootItems.size)
        assertEquals(1, collected.pendingLootEffectiveKeepLimit())
        assertEquals(1, collected.pendingLootRemainingChoices())
        assertEquals(PlayPhase.Idle, collected.phase)
        assertEquals(1, collected.completedQuestCount)
    }
    @Test
    fun collectGeneratesLootItemsAndJournalEntries() {
        val ready = PlaySessionState.initial().copy(
            expedition = ExpeditionRun(
                quest = SeedGame.firstQuest,
                startedAtMillis = 1_000L,
                endsAtMillis = 2_000L,
                result = ExpeditionResult(
                    outcome = ExpeditionOutcome.Success,
                    reward = Reward(gold = 100, xp = 10, lootRolls = 2),
                    scoreMargin = 10,
                ),
            ),
        )

        val collected = ready.collectResult(SeedGame.heroes)

        assertEquals(emptyList<LootItem>(), collected.lootItems)
        assertEquals(2, collected.pendingLootItems.size)
        assertEquals(1, collected.pendingLootEffectiveKeepLimit())
        assertTrue(collected.journalEntries.size >= 2)
        assertEquals(PlayPhase.Idle, collected.phase)
        assertEquals(1, collected.completedQuestCount)
    }

    @Test
    fun finishQuestNowProducesResultBeforeEnd() {
        val startedAt = 1_000L
        val state = PlaySessionState.initial().startQuest(startedAt, SeedGame.firstQuest)
        val finished = state.finishQuestNow(engine, party, roll = 100)

        assertEquals(PlayPhase.ResultReady, finished.phase)
        assertNotNull(finished.expedition?.result)
        assertEquals(PlayPhase.Running, state.phase)
    }

    @Test
    fun recruitHeroConsumesGoldAndAddsSummonedHero() {
        val state = PlaySessionState.initial().copy(gold = HeroGacha.RECRUIT_COST, heroes = emptyList())
        val recruitment = requireNotNull(state.recruitHero(seed = 42))

        assertEquals(HeroGacha.RECRUIT_COST, recruitment.cost)
        assertEquals(false, recruitment.duplicate)
        assertEquals(0, recruitment.reputationReward)
        assertEquals(state.gold - HeroGacha.RECRUIT_COST, recruitment.session.gold)
        assertEquals(state.reputation, recruitment.session.reputation)
        assertEquals(state.heroes.size + 1, recruitment.session.heroes.size)
        assertEquals(recruitment.hero, recruitment.session.heroes.last())
    }

    @Test
    fun recruitHeroFailsWithoutEnoughGold() {
        val state = PlaySessionState.initial().copy(gold = HeroGacha.RECRUIT_COST - 1)

        assertEquals(null, state.recruitHero(seed = 42))
    }

    @Test
    fun recruitHeroConvertsDuplicateIntoReputation() {
        val state = PlaySessionState.initial().copy(
            gold = HeroGacha.RECRUIT_COST,
            heroes = HeroCatalog.heroes.map { it.toHero() },
        )
        val recruitment = requireNotNull(state.recruitHero(seed = 42))

        assertEquals(true, recruitment.duplicate)
        assertEquals(HeroGacha.DUPLICATE_REPUTATION_REWARD, recruitment.reputationReward)
        assertEquals(state.gold - HeroGacha.RECRUIT_COST, recruitment.session.gold)
        assertEquals(state.reputation + HeroGacha.DUPLICATE_REPUTATION_REWARD, recruitment.session.reputation)
        assertEquals(state.heroes.size, recruitment.session.heroes.size)
    }
    @Test
    fun equipLootMovesItemIntoHeroSlotAndBoostsPartyPower() {
        val hero = party.first()
        val item = testLoot(id = "weapon_training_spoon", bonus = 5)
        val state = PlaySessionState.initial().copy(lootItems = listOf(item))

        val equipped = state.equipLoot(hero.id, item)

        assertEquals(emptyList<LootItem>(), equipped.lootItems)
        assertEquals(listOf(item), equipped.equippedItems(hero.id))
        assertEquals(PartyPowerCalculator.totalPower(state.heroes) + item.bonus, equipped.totalPartyPower())
    }

    @Test
    fun equipLootSwapsSameSlotItemBackIntoInventory() {
        val hero = party.first()
        val firstItem = testLoot(id = "weapon_training_spoon", bonus = 3)
        val secondItem = testLoot(id = "weapon_invoice_sword", bonus = 7)
        val state = PlaySessionState.initial().copy(lootItems = listOf(firstItem, secondItem))

        val withFirst = state.equipLoot(hero.id, firstItem)
        val withSecond = withFirst.equipLoot(hero.id, secondItem)
        val unequipped = withSecond.unequipLoot(hero.id, LootSlot.Weapon)

        assertEquals(listOf(firstItem), withSecond.lootItems)
        assertEquals(listOf(secondItem), withSecond.equippedItems(hero.id))
        assertEquals(listOf(firstItem, secondItem), unequipped.lootItems)
        assertEquals(emptyList<LootItem>(), unequipped.equippedItems(hero.id))
    }



    @Test
    fun releaseHeroRemovesHeroAndReturnsEquipmentToInventory() {
        val hero = party.first()
        val item = testLoot(id = "weapon_release_spoon", bonus = 5)
        val state = PlaySessionState.initial()
            .copy(lootItems = listOf(item))
            .equipLoot(hero.id, item)

        val released = state.releaseHero(hero.id)

        assertEquals(false, released.heroes.any { it.id == hero.id })
        assertEquals(listOf(item), released.lootItems)
        assertEquals(emptyList<LootItem>(), released.equippedItems(hero.id))
    }

    @Test
    fun releaseHeroKeepsLastHero() {
        val onlyHero = party.first()
        val state = PlaySessionState.initial().copy(heroes = listOf(onlyHero))

        val released = state.releaseHero(onlyHero.id)

        assertEquals(listOf(onlyHero), released.heroes)
    }


    @Test
    fun keepPendingLootMovesRewardIntoInventory() {
        val item = testLoot(id = "weapon_reward_spoon", bonus = 5)
        val state = PlaySessionState.initial().copy(pendingLootItems = listOf(item))
        val kept = state.keepPendingLoot(item)

        assertEquals(emptyList<LootItem>(), kept.pendingLootItems)
        assertEquals(listOf(item), kept.lootItems)
        assertEquals(state.gold, kept.gold)
    }

    @Test
    fun discardPendingLootRemovesRewardWithoutGold() {
        val item = testLoot(id = "armor_reward_hat", bonus = 2)
        val state = PlaySessionState.initial().copy(pendingLootItems = listOf(item))
        val discarded = state.discardPendingLoot()

        assertEquals(emptyList<LootItem>(), discarded.pendingLootItems)
        assertEquals(emptyList<LootItem>(), discarded.lootItems)
        assertEquals(state.gold, discarded.gold)
    }
    @Test
    fun sellLootRemovesItemAndCreditsGold() {
        val item = testLoot(id = "trinket_invoice_pebble", bonus = 4)
        val state = PlaySessionState.initial().copy(lootItems = listOf(item))
        val sold = state.sellLoot(item)

        assertEquals(emptyList<LootItem>(), sold.lootItems)
        assertEquals(state.gold + LootEconomy.sellValue(item), sold.gold)
    }

    @Test
    fun keepPendingLootDestroysUnchosenLootWhenCarryLimitIsReached() {
        val first = testLoot(id = "weapon_reward_spoon", bonus = 5)
        val second = testLoot(id = "armor_reward_hat", bonus = 2)
        val state = PlaySessionState.initial().copy(
            pendingLootItems = listOf(first, second),
            pendingLootKeepLimit = 1,
        )

        val kept = state.keepPendingLoot(second)

        assertEquals(emptyList<LootItem>(), kept.pendingLootItems)
        assertEquals(listOf(second), kept.lootItems)
        assertEquals(0, kept.pendingLootKeepLimit)
        assertEquals(0, kept.pendingLootKeptCount)
    }

    @Test
    fun keepPendingLootAllowsMultipleChoicesWhenCarryLimitIsHigher() {
        val first = testLoot(id = "weapon_reward_spoon", bonus = 5)
        val second = testLoot(id = "armor_reward_hat", bonus = 2)
        val state = PlaySessionState.initial().copy(
            pendingLootItems = listOf(first, second),
            pendingLootKeepLimit = 2,
        )

        val afterFirst = state.keepPendingLoot(first)
        val afterSecond = afterFirst.keepPendingLoot(second)

        assertEquals(listOf(second), afterFirst.pendingLootItems)
        assertEquals(1, afterFirst.pendingLootSelectedCount())
        assertEquals(1, afterFirst.pendingLootRemainingChoices())
        assertEquals(emptyList<LootItem>(), afterSecond.pendingLootItems)
        assertEquals(listOf(first, second), afterSecond.lootItems)
    }

    @Test
    fun lootCarryLimitStartsAtOneAndScalesWithGuildAndVeteranHeroes() {
        val veteran = HeroProgression.withProgress(party.first(), level = 5, xp = 0)
        val base = PlaySessionState.initial()
        val upgraded = base.copy(bunkRoomLevel = 2)

        assertEquals(1, base.lootCarryLimit(listOf(party.first())))
        assertEquals(2, upgraded.lootCarryLimit(listOf(party.first())))
        assertEquals(3, upgraded.lootCarryLimit(listOf(veteran)))
    }
    @Test
    fun lootCarryBreakdownShowsRecoverySources() {
        val specialistVeteran = HeroProgression.withProgress(
            HeroCatalog.byId.getValue("nell").toHero(),
            level = 5,
            xp = 0,
        )
        val state = PlaySessionState.initial().copy(bunkRoomLevel = 3)
        val breakdown = state.lootCarryBreakdown(listOf(specialistVeteran))

        assertEquals(1, breakdown.base)
        assertEquals(2, breakdown.bunkRoom)
        assertEquals(1, breakdown.veteran)
        assertEquals(1, breakdown.specialist)
        assertEquals(5, breakdown.total)
        assertEquals(5, state.lootCarryLimit(listOf(specialistVeteran)))
    }

    @Test
    fun collectResultStoresPendingLootRecoveryBreakdown() {
        val specialistVeteran = HeroProgression.withProgress(
            HeroCatalog.byId.getValue("nell").toHero(),
            level = 5,
            xp = 0,
        )
        val ready = PlaySessionState.initial().copy(
            bunkRoomLevel = 2,
            heroes = listOf(specialistVeteran),
            expedition = ExpeditionRun(
                quest = SeedGame.firstQuest,
                partyHeroIds = listOf(specialistVeteran.id),
                startedAtMillis = 1_000L,
                endsAtMillis = 2_000L,
                result = ExpeditionResult(
                    outcome = ExpeditionOutcome.Success,
                    reward = Reward(gold = 0, xp = 0, lootRolls = 2),
                    scoreMargin = 10,
                ),
            ),
        )

        val collected = ready.collectResult()
        val breakdown = collected.pendingLootRecoveryBreakdown()

        assertEquals(1, breakdown.base)
        assertEquals(1, breakdown.bunkRoom)
        assertEquals(1, breakdown.veteran)
        assertEquals(1, breakdown.specialist)
        assertEquals(4, breakdown.total)
        assertEquals(4, collected.pendingLootEffectiveKeepLimit())
    }
    @Test
    fun debugAdjustmentsClampResources() {
        val state = PlaySessionState.initial().copy(gold = 10, reputation = 1, guildLevel = 1)
        val clamped = state.adjustGold(-50).adjustReputation(-10).adjustGuildLevel(-3)

        assertEquals(0, clamped.gold)
        assertEquals(0, clamped.reputation)
        assertEquals(1, clamped.guildLevel)

        val raised = clamped.adjustGold(500).adjustReputation(2).adjustGuildLevel(4)

        assertEquals(500, raised.gold)
        assertEquals(2, raised.reputation)
        assertEquals(5, raised.guildLevel)
    }

    @Test
    fun resetProgressForTestingClearsHeroesItemsFacilitiesAndPerks() {
        val item = testLoot(id = "weapon_reset_spoon", bonus = 7)
        val dirty = PlaySessionState.initial()
            .startQuest(1_000L, SeedGame.firstQuest)
            .copy(
                gold = 1_234,
                reputation = 9,
                guildLevel = 4,
                completedQuestCount = 12,
                noticeBoardLevel = 3,
                trainingYardLevel = 2,
                bunkRoomLevel = 3,
                heroes = emptyList(),
                lootRolls = 8,
                lootItems = listOf(item),
                pendingLootItems = listOf(item),
                pendingLootKeepLimit = 1,
                pendingLootKeptCount = 0,
                equippedLoot = listOf(EquippedLoot(heroId = party.first().id, item = item)),
                achievementProgress = emptyList(),
            )

        val reset = dirty.resetProgressForTesting()

        assertEquals(1_234, reset.gold)
        assertEquals(9, reset.reputation)
        assertEquals(4, reset.guildLevel)
        assertEquals(0, reset.completedQuestCount)
        assertEquals(1, reset.noticeBoardLevel)
        assertEquals(1, reset.trainingYardLevel)
        assertEquals(1, reset.bunkRoomLevel)
        assertEquals(HeroCatalog.starterHeroes, reset.heroes)
        assertEquals(0, reset.lootRolls)
        assertEquals(emptyList<LootItem>(), reset.lootItems)
        assertEquals(emptyList<LootItem>(), reset.pendingLootItems)
        assertEquals(0, reset.pendingLootKeepLimit)
        assertEquals(0, reset.pendingLootKeptCount)
        assertEquals(emptyList<EquippedLoot>(), reset.equippedLoot)
        assertEquals(emptyList<JournalEntry>(), reset.journalEntries)
        assertEquals(null, reset.expedition)
        assertEquals(AchievementCatalog.initialProgress(), reset.achievementProgress)
    }

    private fun testLoot(id: String, bonus: Int): LootItem = LootItem(
        id = id,
        name = id,
        rarity = LootRarity.Rare,
        slot = LootSlot.Weapon,
        bonus = bonus,
        icon = LootIcon.Spoon,
    )
}
