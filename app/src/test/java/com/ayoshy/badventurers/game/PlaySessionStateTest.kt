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
    fun specialContractPlanRequiresStoredContractAndConsumesOne() {
        val quest = SeedGame.questById.getValue("paperwork_toll_of_chaos")
        val unlocked = PlaySessionState.initial().copy(
            completedQuestCount = 8,
            reputation = 30,
            noticeBoardLevel = 2,
        )

        val withoutContract = unlocked.startQuest(
            nowMillis = 1_000L,
            quest = quest,
            party = party,
            planId = ExpeditionPlanCatalog.paperworkTollId,
        )
        val withContract = unlocked.copy(specialContracts = 2).startQuest(
            nowMillis = 1_000L,
            quest = quest,
            party = party,
            planId = ExpeditionPlanCatalog.paperworkTollId,
        )

        assertEquals(PlayPhase.Idle, withoutContract.phase)
        assertEquals(0, withoutContract.specialContracts)
        assertEquals(PlayPhase.Running, withContract.phase)
        assertEquals(1, withContract.specialContracts)
        assertEquals(ExpeditionPlanCatalog.paperworkTollId, withContract.expedition?.planId)
    }

    @Test
    fun standardPlansStayFreeWhenNoSpecialContractsAreStored() {
        val quest = SeedGame.questById.getValue("paperwork_toll_of_chaos")
        val unlocked = PlaySessionState.initial().copy(
            completedQuestCount = 8,
            reputation = 30,
            noticeBoardLevel = 2,
        )

        val started = unlocked.startQuest(
            nowMillis = 1_000L,
            quest = quest,
            party = party,
            planId = ExpeditionPlanCatalog.auditEverythingId,
        )

        assertEquals(PlayPhase.Running, started.phase)
        assertEquals(0, started.specialContracts)
        assertEquals(ExpeditionPlanCatalog.auditEverythingId, started.expedition?.planId)
    }

    @Test
    fun collectResultCanLootSpecialContractsFromSuccessfulContractJobs() {
        val quest = SeedGame.questById.getValue("paperwork_toll_of_chaos")
        val ready = PlaySessionState.initial().copy(
            completedQuestCount = 8,
            reputation = 30,
            noticeBoardLevel = 2,
            expedition = ExpeditionRun(
                quest = quest,
                startedAtMillis = 1_000L,
                endsAtMillis = 2_000L,
                result = ExpeditionResult(
                    outcome = ExpeditionOutcome.GreatSuccess,
                    reward = Reward(gold = 0, xp = 0, lootRolls = 0),
                    scoreMargin = 90,
                ),
            ),
        )

        val collected = ready.collectResult()

        assertEquals(2, collected.specialContracts)
        assertEquals(PlayPhase.Idle, collected.phase)
    }

    @Test
    fun claimAchievementCanAwardSpecialContracts() {
        val claimable = PlaySessionState.initial().copy(completedQuestCount = 10)

        val claimed = claimable.claimAchievement("professional_regret_ii", nowMillis = 5L)
        val claimedAgain = claimed.claimAchievement("professional_regret_ii", nowMillis = 6L)

        assertEquals(1, claimed.specialContracts)
        assertEquals(1, claimedAgain.specialContracts)
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
    fun collectResultUsesRareLootGateForGeneratedRewards() {
        val result = ExpeditionResult(
            outcome = ExpeditionOutcome.GreatSuccess,
            reward = Reward(gold = 0, xp = 0, lootRolls = 120),
            scoreMargin = 80,
        )
        val expedition = ExpeditionRun(
            quest = SeedGame.questById.getValue("paperwork_toll_of_chaos"),
            startedAtMillis = 1_000L,
            endsAtMillis = 2_000L,
            result = result,
        )
        val locked = PlaySessionState.initial().copy(
            completedQuestCount = LootGenerator.RARE_LOOT_COMPLETED_QUEST_THRESHOLD - 1,
            expedition = expedition,
        )
        val unlocked = locked.copy(
            completedQuestCount = LootGenerator.RARE_LOOT_COMPLETED_QUEST_THRESHOLD,
            expedition = expedition,
        )

        val lockedCollected = locked.collectResult()
        val unlockedCollected = unlocked.collectResult()

        assertTrue(lockedCollected.pendingLootItems.none { it.rarity >= LootRarity.Rare })
        assertTrue(unlockedCollected.pendingLootItems.any { it.rarity == LootRarity.Rare })
        assertEquals(0, unlockedCollected.pendingLootItems.count { it.rarity == LootRarity.Epic || it.rarity == LootRarity.Relic })
    }

    @Test
    fun collectResultGrantsFirstClearTicketsOnlyAfterSuccessfulClear() {
        val quest = SeedGame.questById.getValue("the_tower_built_sideways")

        fun ready(
            outcome: ExpeditionOutcome,
            clearedQuestIds: Set<String> = emptySet(),
            tickets: Map<String, Int> = RecruitmentTicketCatalog.normalizedInventory(),
        ): PlaySessionState = PlaySessionState.initial().copy(
            completedQuestCount = 15,
            clearedQuestIds = clearedQuestIds,
            recruitmentTickets = tickets,
            expedition = ExpeditionRun(
                quest = quest,
                startedAtMillis = 1_000L,
                endsAtMillis = 2_000L,
                result = ExpeditionResult(
                    outcome = outcome,
                    reward = Reward(gold = 0, xp = 0, lootRolls = 0),
                    scoreMargin = if (outcome == ExpeditionOutcome.Success) 10 else -10,
                ),
            ),
        )

        val failed = ready(ExpeditionOutcome.Failure).collectResult()
        val firstClear = ready(ExpeditionOutcome.Success).collectResult()
        val duplicateClear = ready(
            outcome = ExpeditionOutcome.Success,
            clearedQuestIds = setOf(quest.id),
            tickets = firstClear.recruitmentTickets,
        ).collectResult()

        assertEquals(false, quest.id in failed.clearedQuestIds)
        assertEquals(0, failed.recruitmentTicketCount(RecruitmentTicketCatalog.EPIC_LIABILITY_WRIT_ID))
        assertTrue(quest.id in firstClear.clearedQuestIds)
        assertEquals(1, firstClear.recruitmentTicketCount(RecruitmentTicketCatalog.EPIC_LIABILITY_WRIT_ID))
        assertEquals(1, duplicateClear.recruitmentTicketCount(RecruitmentTicketCatalog.EPIC_LIABILITY_WRIT_ID))
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
    fun recruitHeroUsesBaseProfileBeforePalier2Unlocked() {
        val state = PlaySessionState.initial().copy(
            gold = HeroGacha.RECRUIT_COST,
            completedQuestCount = 7,
            heroes = emptyList(),
        )
        val seed = 42
        val expected = HeroGacha.summon(
            pulls = 1,
            seed = seed,
            recruitmentProfile = HeroGacha.baseRecruitmentProfile,
        ).single()
        val recruitment = requireNotNull(state.recruitHero(seed))

        assertEquals(expected, recruitment.hero)
        assertEquals(HeroGacha.RECRUIT_COST, recruitment.cost)
        assertEquals(false, recruitment.duplicate)
        assertEquals(0, recruitment.reputationReward)
        assertEquals(state.gold - HeroGacha.RECRUIT_COST, recruitment.session.gold)
        assertEquals(state.heroes.size + 1, recruitment.session.heroes.size)
    }

    @Test
    fun recruitHeroUsesLicensedTroubleProfileWithoutChangingDuplicateFlow() {
        val roster = HeroCatalog.heroes.map { it.toHero() }
        val state = PlaySessionState.initial().copy(
            gold = HeroGacha.RECRUIT_COST,
            completedQuestCount = 8,
            heroes = roster,
        )
        val seed = 42
        val expected = HeroGacha.summon(
            pulls = 1,
            seed = seed,
            recruitmentProfile = HeroGacha.palier2RecruitmentProfile,
        ).single()
        val recruitment = requireNotNull(state.recruitHero(seed))

        assertEquals(expected, recruitment.hero)
        assertEquals(HeroGacha.RECRUIT_COST, recruitment.cost)
        assertEquals(true, recruitment.duplicate)
        assertEquals(HeroGacha.DUPLICATE_REPUTATION_REWARD, recruitment.reputationReward)
        assertEquals(state.gold - HeroGacha.RECRUIT_COST, recruitment.session.gold)
        assertEquals(state.reputation + HeroGacha.DUPLICATE_REPUTATION_REWARD, recruitment.session.reputation)
        assertEquals(1, recruitment.duplicateBlankContractReward)
        assertEquals(1, recruitment.session.recruitmentTicketCount(RecruitmentTicketCatalog.BLANK_CONTRACT_ID))
        assertEquals(roster.size, recruitment.session.heroes.size)
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
        assertEquals(1, recruitment.duplicateBlankContractReward)
        assertEquals(1, recruitment.session.recruitmentTicketCount(RecruitmentTicketCatalog.BLANK_CONTRACT_ID))
        assertEquals(state.heroes.size, recruitment.session.heroes.size)
    }

    @Test
    fun recruitHeroWithTicketConsumesOneTicketAndCostsNoGold() {
        val state = PlaySessionState.initial().copy(
            gold = 500,
            heroes = emptyList(),
            recruitmentTickets = RecruitmentTicketCatalog.normalizedInventory(
                mapOf(RecruitmentTicketCatalog.BASIC_HIRING_VOUCHER_ID to 2),
            ),
        )
        val recruitment = requireNotNull(state.recruitHeroWithTicket(RecruitmentTicketCatalog.BASIC_HIRING_VOUCHER_ID, seed = 42))

        assertEquals(0, recruitment.cost)
        assertEquals(500, recruitment.session.gold)
        assertEquals(false, recruitment.duplicate)
        assertEquals(0, recruitment.reputationReward)
        assertEquals(state.heroes.size + 1, recruitment.session.heroes.size)
        assertEquals(1, recruitment.session.recruitmentTickets[RecruitmentTicketCatalog.BASIC_HIRING_VOUCHER_ID])
    }

    @Test
    fun recruitHeroWithTicketUsesDuplicateFlowAndTriggersRecruitmentAchievements() {
        val state = PlaySessionState.initial().copy(
            heroes = HeroCatalog.heroes.map { it.toHero() },
            recruitmentTickets = RecruitmentTicketCatalog.normalizedInventory(
                mapOf(RecruitmentTicketCatalog.BASIC_HIRING_VOUCHER_ID to 1),
            ),
        )
        val recruitment = requireNotNull(state.recruitHeroWithTicket(RecruitmentTicketCatalog.BASIC_HIRING_VOUCHER_ID, seed = 42))

        assertEquals(true, recruitment.duplicate)
        assertEquals(HeroGacha.DUPLICATE_REPUTATION_REWARD, recruitment.reputationReward)
        assertEquals(state.reputation + HeroGacha.DUPLICATE_REPUTATION_REWARD, recruitment.session.reputation)
        assertEquals(1, recruitment.duplicateBlankContractReward)
        assertEquals(1, recruitment.session.recruitmentTicketCount(RecruitmentTicketCatalog.BLANK_CONTRACT_ID))
        assertEquals(state.heroes.size, recruitment.session.heroes.size)
        assertEquals(true, recruitment.session.isCompleted("first_hire"))
        assertEquals(true, recruitment.session.isCompleted("duplicate_form"))
    }

    @Test
    fun recruitHeroWithTicketRequiresValidRecruitmentTicketAndInventory() {
        val state = PlaySessionState.initial().copy(
            recruitmentTickets = RecruitmentTicketCatalog.normalizedInventory(
                mapOf(
                    RecruitmentTicketCatalog.BASIC_HIRING_VOUCHER_ID to 1,
                    RecruitmentTicketCatalog.BLANK_CONTRACT_ID to 1,
                ),
            ),
            heroes = emptyList(),
        )

        assertEquals(null, state.recruitHeroWithTicket("ghost_ticket", seed = 5))
        assertEquals(null, state.recruitHeroWithTicket(RecruitmentTicketCatalog.BLANK_CONTRACT_ID, seed = 5))
        assertEquals(1, state.recruitmentTickets[RecruitmentTicketCatalog.BASIC_HIRING_VOUCHER_ID])
        assertEquals(1, state.recruitmentTickets[RecruitmentTicketCatalog.BLANK_CONTRACT_ID])
    }

    @Test
    fun blankContractPromotesHeroAndConsumesMaterial() {
        val hero = HeroCatalog.byId.getValue("brugg").toHero()
        val state = PlaySessionState.initial().copy(
            heroes = listOf(hero),
            recruitmentTickets = RecruitmentTicketCatalog.normalizedInventory(
                mapOf(RecruitmentTicketCatalog.BLANK_CONTRACT_ID to 2),
            ),
        )

        val promoted = state.promoteHeroWithBlankContract(hero.id)
        val promotedHero = promoted.heroes.single()

        assertEquals(1, promotedHero.promotionRank)
        assertEquals(hero.stats.total + 6, promotedHero.stats.total)
        assertEquals(1, promoted.recruitmentTicketCount(RecruitmentTicketCatalog.BLANK_CONTRACT_ID))
    }

    @Test
    fun blankContractPromotionRequiresIdleMaterialAndRankRoom() {
        val hero = HeroPromotion.previewPromoted(
            HeroCatalog.byId.getValue("brugg").toHero(),
            targetRank = HeroPromotion.MAX_RANK,
        )
        val withContract = PlaySessionState.initial().copy(
            heroes = listOf(hero),
            recruitmentTickets = RecruitmentTicketCatalog.normalizedInventory(
                mapOf(RecruitmentTicketCatalog.BLANK_CONTRACT_ID to 1),
            ),
        )
        val running = withContract.copy(expedition = ExpeditionRun(SeedGame.firstQuest, startedAtMillis = 0L, endsAtMillis = 1_000L))

        assertEquals(withContract, withContract.promoteHeroWithBlankContract(hero.id))
        assertEquals(running, running.promoteHeroWithBlankContract(hero.id))
        assertEquals(
            PlaySessionState.initial(),
            PlaySessionState.initial().promoteHeroWithBlankContract(HeroCatalog.starterHeroes.first().id),
        )
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
    fun keepPendingLootSelectionCommitsSelectedItemsAndDestroysRest() {
        val existing = testLoot(id = "weapon_existing_spoon", bonus = 1)
        val first = testLoot(id = "weapon_reward_spoon", bonus = 5)
        val second = testLoot(id = "armor_reward_hat", bonus = 2)
        val third = testLoot(id = "trinket_reward_ring", bonus = 7)
        val state = PlaySessionState.initial().copy(
            lootItems = listOf(existing),
            pendingLootItems = listOf(first, second, third),
            pendingLootKeepLimit = 2,
            pendingLootCarryBreakdown = LootCarryBreakdown(base = 1, bunkRoom = 1),
        )

        val kept = state.keepPendingLootSelection(listOf(third, first))

        assertEquals(emptyList<LootItem>(), kept.pendingLootItems)
        assertEquals(listOf(existing, third, first), kept.lootItems)
        assertEquals(0, kept.pendingLootKeepLimit)
        assertEquals(0, kept.pendingLootKeptCount)
        assertEquals(LootCarryBreakdown(), kept.pendingLootCarryBreakdown)
    }

    @Test
    fun keepPendingLootSelectionCapsSelectedItemsAtCarryLimit() {
        val first = testLoot(id = "weapon_reward_spoon", bonus = 5)
        val second = testLoot(id = "armor_reward_hat", bonus = 2)
        val third = testLoot(id = "trinket_reward_ring", bonus = 7)
        val state = PlaySessionState.initial().copy(
            pendingLootItems = listOf(first, second, third),
            pendingLootKeepLimit = 1,
        )

        val kept = state.keepPendingLootSelection(listOf(first, second, third))

        assertEquals(emptyList<LootItem>(), kept.pendingLootItems)
        assertEquals(listOf(first), kept.lootItems)
        assertEquals(0, kept.pendingLootRemainingChoices())
    }

    @Test
    fun keepPendingLootSelectionDiscardsPendingLootWhenSelectionIsInvalid() {
        val first = testLoot(id = "weapon_reward_spoon", bonus = 5)
        val missing = testLoot(id = "missing_reward_spoon", bonus = 1)
        val state = PlaySessionState.initial().copy(
            pendingLootItems = listOf(first),
            pendingLootKeepLimit = 1,
        )

        val kept = state.keepPendingLootSelection(listOf(missing))

        assertEquals(emptyList<LootItem>(), kept.pendingLootItems)
        assertEquals(emptyList<LootItem>(), kept.lootItems)
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
    fun initialCoreCrewUsesStarterSlotsAndProducesPassiveIncome() {
        val state = PlaySessionState.initial()

        assertEquals(3, state.coreCrewSlots())
        assertEquals(HeroCatalog.starterHeroes.map { it.id }, state.normalizedCoreCrewHeroIds())
        assertTrue(state.passiveGoldPerHour() > 0)
    }

    @Test
    fun bunkRoomUnlocksFourthCoreCrewSlot() {
        val roster = HeroCatalog.heroes.map { it.toHero() }
        val state = PlaySessionState.initial().copy(heroes = roster, bunkRoomLevel = 2)
        val extraHero = roster.first { it.id !in state.normalizedCoreCrewHeroIds() }

        val assigned = state.toggleCoreCrewHero(extraHero.id)
        val removed = assigned.toggleCoreCrewHero(extraHero.id)

        assertEquals(4, state.coreCrewSlots())
        assertEquals(4, assigned.normalizedCoreCrewHeroIds().size)
        assertTrue(extraHero.id in assigned.normalizedCoreCrewHeroIds())
        assertEquals(false, extraHero.id in removed.normalizedCoreCrewHeroIds())
    }

    @Test
    fun passiveIncomeUsesGearAndReducesActiveExpeditionCrew() {
        val hero = PlaySessionState.initial().coreCrew().first()
        val item = testLoot(id = "weapon_core_spoon", bonus = 20)
        val base = PlaySessionState.initial()
        val geared = base.copy(lootItems = listOf(item)).equipLoot(hero.id, item)

        val fullReport = geared.passiveIncomeReport(
            sinceMillis = 0L,
            untilMillis = 3_600_000L,
        )
        val activeReport = geared.passiveIncomeReport(
            sinceMillis = 0L,
            untilMillis = 3_600_000L,
            activeExpeditionHeroIds = listOf(hero.id),
            activeUntilMillis = 3_600_000L,
        )

        val cappedReport = geared.passiveIncomeReport(
            sinceMillis = 0L,
            untilMillis = 8 * 3_600_000L,
        )

        assertTrue(geared.passiveGoldPerHour() > base.passiveGoldPerHour())
        assertTrue(activeReport.gold > 0)
        assertTrue(activeReport.gold < fullReport.gold)
        assertEquals(geared.passiveIncomeCapSeconds(), cappedReport.cappedSeconds)
        assertTrue(cappedReport.elapsedSeconds > cappedReport.cappedSeconds)
    }

    @Test
    fun passiveIncomeAddsSuppliesAndFacilityCapBonuses() {
        val base = PlaySessionState.initial()
        val upgraded = base.copy(bunkRoomLevel = 3, tavernKitchenLevel = 2, scoutTableLevel = 1)

        val baseReport = base.passiveIncomeReport(
            sinceMillis = 0L,
            untilMillis = 8 * 3_600_000L,
        )
        val upgradedReport = upgraded.passiveIncomeReport(
            sinceMillis = 0L,
            untilMillis = 8 * 3_600_000L,
        )

        assertTrue(base.passiveSuppliesPerHour() > 0)
        assertTrue(upgraded.passiveSuppliesPerHour() > base.passiveSuppliesPerHour())
        assertTrue(upgraded.passiveIncomeCapSeconds() > base.passiveIncomeCapSeconds())
        assertEquals(base.passiveIncomeCapSeconds(), baseReport.cappedSeconds)
        assertEquals(upgraded.passiveIncomeCapSeconds(), upgradedReport.cappedSeconds)
        assertTrue(upgradedReport.supplies > baseReport.supplies)
    }

    @Test
    fun passiveLootFindsRequireScoutTableAndArmoryForgeAndCreditOnce() {
        val ready = PlaySessionState.initial()
            .startQuest(0L, SeedGame.firstQuest)
            .finishQuestNow(engine, party, roll = 100)

        val onlyScout = ready.copy(scoutTableLevel = 2).passiveIncomeReport(0L, 4 * 3_600_000L)
        val onlyArmory = ready.copy(armoryForgeLevel = 3).passiveIncomeReport(0L, 4 * 3_600_000L)
        val built = ready.copy(
            completedQuestCount = LootGenerator.RARE_LOOT_COMPLETED_QUEST_THRESHOLD,
            scoutTableLevel = 2,
            armoryForgeLevel = 3,
        )
        val report = built.passiveIncomeReport(
            sinceMillis = 0L,
            untilMillis = 4 * 3_600_000L,
            activeExpeditionHeroIds = built.expedition?.partyHeroIds.orEmpty(),
            activeUntilMillis = built.expedition?.endsAtMillis ?: 0L,
        )
        val marked = built.markOfflineReportCollected(4 * 3_600_000L)

        assertEquals(0, ready.passiveLootFindChancePercent())
        assertEquals(emptyList<LootItem>(), onlyScout.lootFinds)
        assertEquals(emptyList<LootItem>(), onlyArmory.lootFinds)
        assertTrue(built.passiveLootFindChancePercent() > 0)
        assertEquals(1, report.lootFinds.size)
        assertEquals(report.supplies, marked.supplies - built.supplies)
        assertEquals(report.lootFinds, marked.lastOfflinePassiveIncome?.lootFinds)
        assertEquals(report.lootFinds, marked.pendingLootItems)
        assertEquals(1, marked.pendingLootEffectiveKeepLimit())

        val markedAgain = marked.markOfflineReportCollected(8 * 3_600_000L)
        assertEquals(marked.supplies, markedAgain.supplies)
        assertEquals(marked.pendingLootItems, markedAgain.pendingLootItems)
    }

    @Test
    fun offlineReportCreditsPassiveIncomeAndIncidentsOnce() {
        val ready = PlaySessionState.initial()
            .startQuest(0L, SeedGame.firstQuest)
            .finishQuestNow(engine, party, roll = 100)

        val marked = ready.markOfflineReportCollected(3_600_000L)
        val report = requireNotNull(marked.lastOfflinePassiveIncome)
        val incidentGold = marked.lastOfflinePassiveIncidents.sumOf { it.reward.gold }
        val incidentReputation = marked.lastOfflinePassiveIncidents.sumOf { it.reward.reputation }
        val markedAgain = marked.markOfflineReportCollected(7_200_000L)

        assertTrue(report.gold > 0)
        assertTrue(marked.lastOfflinePassiveIncidents.isNotEmpty())
        assertEquals(ready.gold + report.gold + incidentGold, marked.gold)
        assertEquals(ready.reputation + incidentReputation, marked.reputation)
        assertEquals(marked.gold, markedAgain.gold)
        assertEquals(marked.reputation, markedAgain.reputation)
        assertEquals(report, markedAgain.lastOfflinePassiveIncome)
        assertEquals(marked.lastOfflinePassiveIncidents, markedAgain.lastOfflinePassiveIncidents)
    }

    @Test
    fun offlineReportSkipsPassiveIncidentsForShortReturns() {
        val ready = PlaySessionState.initial()
            .startQuest(0L, SeedGame.firstQuest)
            .finishQuestNow(engine, party, roll = 100)

        val marked = ready.markOfflineReportCollected(5 * 60 * 1000L)

        assertEquals(emptyList<PassiveIncident>(), marked.lastOfflinePassiveIncidents)
    }

    @Test
    fun offlineReportHighlightsAchievementProgressAfterCollect() {
        val ready = PlaySessionState.initial()
            .startQuest(0L, SeedGame.firstQuest)
            .finishQuestNow(engine, party, roll = 100)
        val marked = ready.markOfflineReportCollected(3_600_000L)

        val highlights = marked.offlineReportHighlights(marked.collectResult())

        assertTrue(highlights.hasAchievementProgress)
        assertTrue(highlights.completedAchievementDelta > 0)
        assertTrue(highlights.claimableAchievementDelta > 0)
        assertTrue(highlights.claimableSealDelta > 0)
    }

    @Test
    fun offlineReportHighlightsTicketInventoryAndClaimableTicketRewards() {
        val before = PlaySessionState.initial().copy(
            recruitmentTickets = RecruitmentTicketCatalog.normalizedInventory(
                mapOf(RecruitmentTicketCatalog.BASIC_HIRING_VOUCHER_ID to 2),
            ),
        )
        val after = before.copy(achievementProgress = completedProgress("first_hire"))

        val highlights = before.offlineReportHighlights(after)

        assertTrue(highlights.hasTicketProgress)
        assertEquals(2, highlights.ticketInventory[RecruitmentTicketCatalog.BASIC_HIRING_VOUCHER_ID])
        assertEquals(1, highlights.claimableTicketRewardDelta[RecruitmentTicketCatalog.BASIC_HIRING_VOUCHER_ID])
    }

    @Test
    fun passiveIncidentGeneratorIsDeterministicAndMentionsCoreCrew() {
        val ready = PlaySessionState.initial()
            .startQuest(0L, SeedGame.firstQuest)
            .finishQuestNow(engine, party, roll = 100)

        val first = PassiveIncidentGenerator.generate(ready, 3_600_000L)
        val second = PassiveIncidentGenerator.generate(ready, 3_600_000L)
        val starterNames = HeroCatalog.starterHeroes.map { it.name }

        assertEquals(first, second)
        assertEquals(1, first.size)
        assertTrue(starterNames.any { name -> first.single().text.contains(name) })
        assertTrue(first.single().reward.gold in 0..8)
        assertTrue(first.single().reward.reputation in 0..1)
    }

    @Test
    fun passiveIncidentGeneratorHandlesUpgradedFacilityTemplates() {
        val ready = PlaySessionState.initial()
            .startQuest(0L, SeedGame.firstQuest)
            .finishQuestNow(engine, party, roll = 100)
        val facilityStates = listOf(
            ready.copy(noticeBoardLevel = 2),
            ready.copy(trainingYardLevel = 2),
            ready.copy(bunkRoomLevel = 2),
            ready.copy(scoutTableLevel = 1),
            ready.copy(noticeBoardLevel = 3, trainingYardLevel = 3, bunkRoomLevel = 2, scoutTableLevel = 2),
        )

        facilityStates.forEach { state ->
            val incidents = PassiveIncidentGenerator.generate(state, 3_600_000L)

            assertTrue(incidents.isNotEmpty())
            incidents.forEach { incident -> assertTrue(incident.text.isNotBlank()) }
        }
    }
    @Test
    fun passiveIncidentGeneratorCanUseScoutTableWithoutCoreCrew() {
        val readyWithoutCrew = PlaySessionState.initial()
            .copy(coreCrewHeroIds = emptyList())
            .startQuest(0L, SeedGame.firstQuest)
            .finishQuestNow(engine, party, roll = 100)
        val readyWithScoutTable = readyWithoutCrew.copy(scoutTableLevel = 1)

        assertEquals(emptyList<PassiveIncident>(), PassiveIncidentGenerator.generate(readyWithoutCrew, 3_600_000L))
        assertTrue(PassiveIncidentGenerator.generate(readyWithScoutTable, 3_600_000L).isNotEmpty())
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
                supplies = 14,
                guildLevel = 4,
                completedQuestCount = 12,
                clearedQuestIds = setOf("cave_minor_regrets"),
                specialContracts = 4,
                noticeBoardLevel = 3,
                trainingYardLevel = 2,
                bunkRoomLevel = 3,
                scoutTableLevel = 2,
                armoryForgeLevel = 2,
                tavernKitchenLevel = 1,
                heroes = emptyList(),
                coreCrewHeroIds = listOf("brugg"),
                recruitmentTickets = RecruitmentTicketCatalog.normalizedInventory(
                    mapOf(RecruitmentTicketCatalog.BASIC_HIRING_VOUCHER_ID to 2),
                ),
                lootRolls = 8,
                lootItems = listOf(item),
                pendingLootItems = listOf(item),
                pendingLootKeepLimit = 1,
                pendingLootKeptCount = 0,
                equippedLoot = listOf(EquippedLoot(heroId = party.first().id, item = item)),
                achievementProgress = emptyList(),
                lastOfflinePassiveIncidents = listOf(PassiveIncident("dirty-reset", "Brugg counted the locks.")),
            )

        val reset = dirty.resetProgressForTesting()

        assertEquals(1_234, reset.gold)
        assertEquals(9, reset.reputation)
        assertEquals(4, reset.guildLevel)
        assertEquals(0, reset.supplies)
        assertEquals(0, reset.completedQuestCount)
        assertEquals(emptySet<String>(), reset.clearedQuestIds)
        assertEquals(0, reset.specialContracts)
        assertEquals(1, reset.noticeBoardLevel)
        assertEquals(1, reset.trainingYardLevel)
        assertEquals(1, reset.bunkRoomLevel)
        assertEquals(0, reset.scoutTableLevel)
        assertEquals(0, reset.armoryForgeLevel)
        assertEquals(0, reset.tavernKitchenLevel)
        assertEquals(HeroCatalog.starterHeroes, reset.heroes)
        assertEquals(HeroCatalog.starterHeroes.map { it.id }, reset.normalizedCoreCrewHeroIds())
        assertEquals(RecruitmentTicketCatalog.normalizedInventory(), reset.recruitmentTickets)
        assertEquals(null, reset.lastOfflinePassiveIncome)
        assertEquals(emptyList<PassiveIncident>(), reset.lastOfflinePassiveIncidents)
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

    private fun PlaySessionState.isCompleted(achievementId: String): Boolean {
        val definition = AchievementCatalog.byId.getValue(achievementId)
        return achievementProgressFor(definition).isCompleted(definition)
    }

    private fun completedProgress(vararg achievementIds: String): List<AchievementProgress> {
        val completedIds = achievementIds.toSet()
        return AchievementCatalog.definitions.map { definition ->
            if (definition.id in completedIds) {
                AchievementProgress(
                    achievementId = definition.id,
                    current = definition.target,
                    completedAtMillis = 1L,
                )
            } else {
                AchievementProgress(achievementId = definition.id)
            }
        }
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
