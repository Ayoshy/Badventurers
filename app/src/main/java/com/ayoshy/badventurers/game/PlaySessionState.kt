package com.ayoshy.badventurers.game

import kotlin.random.Random

data class PlaySessionState(
    val gold: Int = 0,
    val reputation: Int = 0,
    val supplies: Int = 0,
    val guildLevel: Int = 1,
    val completedQuestCount: Int = 0,
    val clearedQuestIds: Set<String> = emptySet(),
    val noticeBoardLevel: Int = 1,
    val trainingYardLevel: Int = 1,
    val bunkRoomLevel: Int = 1,
    val scoutTableLevel: Int = 0,
    val armoryForgeLevel: Int = 0,
    val tavernKitchenLevel: Int = 0,
    val infirmaryLevel: Int = 0,
    val accountantOfficeLevel: Int = 0,
    val heroes: List<Hero> = HeroCatalog.starterHeroes,
    val coreCrewHeroIds: List<String> = defaultCoreCrewHeroIds(),
    val lootRolls: Int = 0,
    val lootItems: List<LootItem> = emptyList(),
    val pendingLootItems: List<LootItem> = emptyList(),
    val pendingLootKeepLimit: Int = 0,
    val pendingLootKeptCount: Int = 0,
    val pendingLootCarryBreakdown: LootCarryBreakdown = LootCarryBreakdown(),
    val equippedLoot: List<EquippedLoot> = emptyList(),
    val journalEntries: List<JournalEntry> = emptyList(),
    val expedition: ExpeditionRun? = null,
    val achievementProgress: List<AchievementProgress> = AchievementCatalog.initialProgress(),
    val lastOfflinePassiveIncome: PassiveIncomeReport? = null,
    val lastOfflinePassiveIncidents: List<PassiveIncident> = emptyList(),
    val specialContracts: Int = 0,
    val recruitmentTickets: Map<String, Int> = RecruitmentTicketCatalog.normalizedInventory()
) {
    val phase: PlayPhase
        get() = when {
            expedition == null -> PlayPhase.Idle
            expedition.result == null -> PlayPhase.Running
            else -> PlayPhase.ResultReady
        }

    fun progress(nowMillis: Long): Double {
        val run = expedition ?: return 0.0
        val durationMillis = (run.endsAtMillis - run.startedAtMillis).coerceAtLeast(1L)
        return when {
            nowMillis <= run.startedAtMillis -> 0.0
            nowMillis >= run.endsAtMillis -> 1.0
            else -> ((nowMillis - run.startedAtMillis).toDouble() / durationMillis).coerceIn(0.0, 1.0)
        }
    }

    fun startQuest(
        nowMillis: Long,
        quest: Quest,
        party: List<Hero> = heroes,
        planId: String = ExpeditionPlanCatalog.defaultPlanId,
    ): PlaySessionState {
        if (phase != PlayPhase.Idle || !isQuestUnlocked(quest)) return this
        val selectedPlan = ExpeditionPlanCatalog.planForQuest(planId, quest)
        val contractCost = selectedPlan.specialContractCost
        if (specialContracts < contractCost) return this
        val selectedParty = selectedPartyForQuest(quest, party)
        return copy(
            specialContracts = (specialContracts - contractCost).coerceAtLeast(0),
            expedition = ExpeditionRun(
                quest = quest,
                partyHeroIds = selectedParty.map { it.id },
                startedAtMillis = nowMillis,
                endsAtMillis = nowMillis + ExpeditionPlanCatalog.durationSeconds(quest, selectedPlan.id) * 1000L,
                planId = selectedPlan.id,
            ),
            lastOfflinePassiveIncome = null,
            lastOfflinePassiveIncidents = emptyList(),
        )
    }

    fun tick(
        nowMillis: Long,
        engine: ExpeditionEngine,
        party: List<Hero>,
    ): PlaySessionState {
        val run = expedition ?: return this
        if (run.result != null || nowMillis < run.endsAtMillis) return this
        return copy(
            expedition = run.copy(
                result = engine.resolve(
                    party = partyForRun(run, party),
                    quest = run.quest,
                    equipment = equippedLoot,
                    facilityPowerBonus = expeditionFacilityPowerBonus(run.planId),
                    planId = run.planId,
                ),
            ),
        )
    }

    fun finishQuestNow(
        engine: ExpeditionEngine,
        party: List<Hero>,
        roll: Int? = null,
    ): PlaySessionState {
        val run = expedition ?: return this
        if (run.result != null) return this
        val result = if (roll == null) {
            engine.resolve(
                party = partyForRun(run, party),
                quest = run.quest,
                equipment = equippedLoot,
                facilityPowerBonus = expeditionFacilityPowerBonus(run.planId),
                planId = run.planId,
            )
        } else {
            engine.resolve(
                party = partyForRun(run, party),
                quest = run.quest,
                roll = roll,
                equipment = equippedLoot,
                facilityPowerBonus = expeditionFacilityPowerBonus(run.planId),
                planId = run.planId,
            )
        }
        return copy(expedition = run.copy(result = result))
    }

    fun collectResult(
        party: List<Hero> = heroes,
        fakeRewardedAdReward: FakeRewardedAdReward? = null,
    ): PlaySessionState {
        val run = expedition ?: return this
        val result = run.result ?: return this
        val baseGold = collectableRewardGold(result, run.quest)
        val earnedSpecialContracts = collectableSpecialContracts(result, run.quest)
        val firstClearTicketRewards = if (result.isFirstClearSuccess() && run.quest.id !in clearedQuestIds) {
            run.quest.firstClearTicketRewards
        } else {
            emptyMap()
        }
        val nextClearedQuestIds = if (result.isFirstClearSuccess()) clearedQuestIds + run.quest.id else clearedQuestIds
        val extraGold = fakeRewardedAdReward?.extraGold ?: 0
        val extraLootRolls = fakeRewardedAdReward?.extraLootRolls ?: 0
        val totalLootRolls = collectableLootRolls(result) + extraLootRolls
        val participatingParty = partyForRun(run, party)
        val generatedLoot = LootGenerator.generate(
            totalLootRolls,
            seed = lootSeed(result) + extraLootRolls * 17,
            lootProfile = rewardLootProfile(),
        )
        val generatedJournal = JournalGenerator.generate(result, participatingParty, run.quest)
        val advancedHeroes = heroesWithQuestXp(participatingParty, collectableHeroXp(result, run.quest))
        val advancedParty = participatingParty.map { hero ->
            advancedHeroes.firstOrNull { it.id == hero.id } ?: hero
        }
        val collected = copy(
            gold = gold + baseGold + extraGold,
            lootRolls = lootRolls + totalLootRolls,
            completedQuestCount = completedQuestCount + 1,
            clearedQuestIds = nextClearedQuestIds,
            heroes = advancedHeroes,
            specialContracts = specialContracts + earnedSpecialContracts,
            recruitmentTickets = RecruitmentTicketCatalog.addToInventory(recruitmentTickets, firstClearTicketRewards),
            journalEntries = (journalEntries + generatedJournal).takeLast(12),
            expedition = null,
            lastOfflinePassiveIncome = null,
            lastOfflinePassiveIncidents = emptyList(),
        ).appendPendingLoot(generatedLoot, lootCarryBreakdown(advancedParty))
        return AchievementTracker.applyEvent(
            state = collected,
            event = AchievementEvent.QuestCollected(
                quest = run.quest,
                result = result,
                partyHeroIds = run.partyHeroIds,
            ),
        )
    }

    fun markOfflineReportCollected(nowMillis: Long = 0L): PlaySessionState {
        val run = expedition ?: return this
        if (run.result == null) return this

        val tracked = AchievementTracker.applyEvent(this, AchievementEvent.OfflineCollected, nowMillis)
        if (tracked.lastOfflinePassiveIncome != null) return tracked

        val reportUntilMillis = nowMillis.takeIf { it > 0L } ?: run.endsAtMillis
        val passiveReport = tracked.passiveIncomeReport(
            sinceMillis = run.startedAtMillis,
            untilMillis = reportUntilMillis,
            activeExpeditionHeroIds = run.partyHeroIds,
            activeUntilMillis = run.endsAtMillis,
        )
        val incidents = PassiveIncidentGenerator.generate(tracked, reportUntilMillis)
        val incidentGold = incidents.sumOf { it.reward.gold }
        val incidentReputation = incidents.sumOf { it.reward.reputation }
        val incidentSpecialContracts = incidents.sumOf { it.reward.specialContracts }

        val credited = tracked.copy(
            gold = tracked.gold + passiveReport.gold + incidentGold,
            reputation = (tracked.reputation + incidentReputation).coerceAtLeast(0),
            supplies = tracked.supplies + passiveReport.supplies,
            specialContracts = tracked.specialContracts + incidentSpecialContracts,
            lastOfflinePassiveIncome = passiveReport,
            lastOfflinePassiveIncidents = incidents,
        )
        return credited.appendPendingLoot(passiveReport.lootFinds, passiveLootCarryBreakdown(passiveReport.lootFinds.size))
    }

    fun isQuestUnlocked(quest: Quest): Boolean {
        val conditions = quest.unlockRequirement.conditions
        return conditions.isEmpty() || conditions.any(::isUnlockConditionMet)
    }

    private fun isUnlockConditionMet(condition: QuestUnlockCondition): Boolean =
        reputation >= condition.minReputation &&
            completedQuestCount >= condition.minCompletedQuestCount &&
            noticeBoardLevel >= condition.minNoticeBoardLevel &&
            trainingYardLevel >= condition.minTrainingYardLevel &&
            bunkRoomLevel >= condition.minBunkRoomLevel
    fun selectedPartyForQuest(quest: Quest, party: List<Hero>): List<Hero> {
        val rosterIds = heroes.map { it.id }.toSet()
        return party
            .distinctBy { it.id }
            .filter { it.id in rosterIds }
            .take(effectivePartySlots(quest))
    }

    private fun partyForRun(run: ExpeditionRun, fallbackParty: List<Hero>): List<Hero> {
        val savedParty = run.partyHeroIds.mapNotNull { heroId -> heroes.firstOrNull { it.id == heroId } }
        return selectedPartyForQuest(run.quest, savedParty.ifEmpty { fallbackParty })
    }

    private fun heroesWithQuestXp(party: List<Hero>, xp: Int): List<Hero> {
        if (xp <= 0 || party.isEmpty()) return heroes
        val participatingHeroIds = party.map { it.id }.toSet()
        return heroes.map { hero ->
            if (hero.id in participatingHeroIds) HeroProgression.grantXp(hero, xp) else hero
        }
    }

    private fun lootSeed(result: ExpeditionResult): Int =
        gold + lootRolls * 31 + noticeBoardLevel * 101 + result.scoreMargin

    fun noticeBoardGoldBonusPercent(): Int = (noticeBoardLevel - 1).coerceAtLeast(0) * 10

    fun questGoldWithNoticeBoard(baseGold: Int): Int =
        baseGold + baseGold * (noticeBoardGoldBonusPercent() + achievementQuestGoldBonusPercent()) / 100

    fun trainingYardPowerBonus(): Int =
        (trainingYardLevel - 1).coerceAtLeast(0) * TRAINING_YARD_POWER_PER_LEVEL + achievementTrainingPowerBonus()

    fun expeditionFacilityPowerBonus(planId: String? = null): Int =
        trainingYardPowerBonus() + infirmarySafePlanPowerBonus(planId)

    fun infirmarySafePlanPowerBonus(planId: String?): Int =
        if (ExpeditionPlanCatalog.coercePlanId(planId) == ExpeditionPlanCatalog.safetyFirstId) {
            infirmaryLevel * INFIRMARY_SAFE_PLAN_POWER_PER_LEVEL
        } else {
            0
        }

    fun infirmaryFailureRecoveryBonusPercent(): Int =
        infirmaryLevel * INFIRMARY_FAILURE_RECOVERY_PERCENT_PER_LEVEL

    fun trainingYardQuestXpBonusPercent(): Int =
        (trainingYardLevel - 1).coerceAtLeast(0) * TRAINING_YARD_XP_BONUS_PERCENT_PER_LEVEL

    fun tavernKitchenQuestXpBonusPercent(quest: Quest? = null): Int {
        val baseBonus = tavernKitchenLevel * TAVERN_KITCHEN_XP_BONUS_PERCENT_PER_LEVEL
        val longQuestBonus = if (quest?.let { it.durationSeconds >= LONG_EXPEDITION_SECONDS || it.hasAny(QuestTag.LongQuest) } == true) {
            tavernKitchenLevel * TAVERN_KITCHEN_LONG_QUEST_XP_BONUS_PERCENT_PER_LEVEL
        } else {
            0
        }
        return baseBonus + longQuestBonus
    }

    fun accountantOfficeQuestGoldBonusPercent(quest: Quest? = null): Int =
        if (quest?.hasAny(QuestTag.Paperwork, QuestTag.Contract) == true) {
            accountantOfficeLevel * ACCOUNTANT_PAPERWORK_GOLD_BONUS_PERCENT_PER_LEVEL
        } else {
            0
        }

    fun accountantOfficeDuplicateReputationBonus(): Int =
        accountantOfficeLevel * ACCOUNTANT_DUPLICATE_REPUTATION_PER_LEVEL

    fun duplicateReputationReward(): Int =
        HeroGacha.DUPLICATE_REPUTATION_REWARD + accountantOfficeDuplicateReputationBonus()

    fun rewardLootProfile(): LootGenerator.LootRarityProfile = LootGenerator.lootProfileWithArmoryForgeBonus(
        profile = LootGenerator.lootProfileForProgress(completedQuestCount),
        armoryForgeLevel = armoryForgeLevel,
    )

    fun collectableRewardGold(result: ExpeditionResult, quest: Quest? = null): Int {
        val baseGold = questGoldWithNoticeBoard(achievementAdjustedRewardGold(result))
        val failureRecoveryGold = if (result.outcome.isFailure()) {
            baseGold * infirmaryFailureRecoveryBonusPercent() / 100
        } else {
            0
        }
        val paperworkGold = if (result.outcome.isAtLeastSuccess()) {
            baseGold * accountantOfficeQuestGoldBonusPercent(quest) / 100
        } else {
            0
        }
        return baseGold + failureRecoveryGold + paperworkGold
    }

    fun collectableHeroXp(result: ExpeditionResult, quest: Quest? = null): Int {
        val baseXp = result.reward.xp.coerceAtLeast(0)
        val bonusPercent = trainingYardQuestXpBonusPercent() + tavernKitchenQuestXpBonusPercent(quest)
        return baseXp + baseXp * bonusPercent / 100
    }

    fun collectableLootRolls(result: ExpeditionResult): Int =
        result.reward.lootRolls + achievementRewardChoiceLootRolls(result)

    fun collectableSpecialContracts(result: ExpeditionResult, quest: Quest): Int =
        specialContractsLootedFrom(result, quest)

    fun canAffordPlan(planId: String?, quest: Quest): Boolean =
        specialContracts >= ExpeditionPlanCatalog.specialContractCost(planId, quest)

    fun lootCarryLimit(party: List<Hero> = heroes): Int =
        lootCarryBreakdown(party).total.coerceAtLeast(BASE_LOOT_KEEP_LIMIT)

    fun lootCarryBreakdown(party: List<Hero> = heroes): LootCarryBreakdown = LootCarryBreakdown(
        base = BASE_LOOT_KEEP_LIMIT,
        bunkRoom = bunkRoomLootCarryBonus(),
        veteran = veteranLootCarryBonus(party),
        specialist = specialistLootCarryBonus(party),
    )

    fun bunkRoomLootCarryBonus(): Int = (bunkRoomLevel - 1).coerceAtLeast(0)

    fun pendingLootEffectiveKeepLimit(): Int =
        if (pendingLootItems.isEmpty()) 0 else pendingLootKeepLimit.coerceAtLeast(BASE_LOOT_KEEP_LIMIT)

    fun pendingLootRecoveryBreakdown(): LootCarryBreakdown =
        if (pendingLootItems.isEmpty()) {
            LootCarryBreakdown()
        } else {
            pendingLootCarryBreakdown.takeIf { it.total > 0 }
                ?: LootCarryBreakdown(base = pendingLootEffectiveKeepLimit())
        }

    fun pendingLootRemainingChoices(): Int =
        (pendingLootEffectiveKeepLimit() - pendingLootKeptCount.coerceAtLeast(0)).coerceAtLeast(0)

    fun pendingLootSelectedCount(): Int =
        if (pendingLootItems.isEmpty()) 0 else pendingLootKeptCount.coerceAtLeast(0)

    fun achievementSeals(): Int = AchievementCatalog.claimedSeals(achievementProgress)

    fun completedAchievementCount(): Int {
        val normalized = refreshedAchievementProgress()
        return AchievementCatalog.definitions.count { definition ->
            normalized.firstOrNull { it.achievementId == definition.id }?.isCompleted(definition) == true
        }
    }

    fun claimableAchievements(): List<AchievementDefinition> {
        val progressById = refreshedAchievementProgress().associateBy { it.achievementId }
        return AchievementCatalog.definitions.filter { definition ->
            val progress = progressById[definition.id]
            progress?.isCompleted(definition) == true && !progress.isClaimed
        }
    }

    fun claimableAchievementCount(): Int = claimableAchievements().size

    fun claimableAchievementSeals(): Int =
        claimableAchievements().sumOf { it.sealReward }

    fun claimableRecruitmentTicketRewards(): Map<String, Int> =
        claimableAchievements().fold(RecruitmentTicketCatalog.normalizedInventory()) { inventory, definition ->
            RecruitmentTicketCatalog.addToInventory(inventory, recruitmentTicketRewards(definition.reward))
        }

    fun offlineReportHighlights(postCollectSession: PlaySessionState = collectResult()): OfflineReportHighlights =
        OfflineReportHighlights(
            ticketInventory = postCollectSession.normalizedRecruitmentTickets(),
            completedAchievementDelta = (postCollectSession.completedAchievementCount() - completedAchievementCount()).coerceAtLeast(0),
            claimableAchievementDelta = (postCollectSession.claimableAchievementCount() - claimableAchievementCount()).coerceAtLeast(0),
            claimableSealDelta = (postCollectSession.claimableAchievementSeals() - claimableAchievementSeals()).coerceAtLeast(0),
            claimableTicketRewardDelta = recruitmentTicketRewardDeltaTo(postCollectSession),
        )

    fun achievementProgressFor(definition: AchievementDefinition): AchievementProgress =
        refreshedAchievementProgress()
            .firstOrNull { it.achievementId == definition.id }
            ?: AchievementProgress(achievementId = definition.id)

    fun normalizedRecruitmentTickets(): Map<String, Int> =
        RecruitmentTicketCatalog.normalizedInventory(recruitmentTickets)

    fun recruitmentTicketCount(ticketId: String): Int =
        normalizedRecruitmentTickets()[ticketId] ?: 0

    fun totalRecruitmentTicketCount(): Int =
        normalizedRecruitmentTickets().values.sum()

    fun nextAchievementMilestone(): CharterMilestone? =
        AchievementCatalog.nextMilestone(achievementProgress)

    fun isAchievementFeatureUnlocked(feature: AchievementFeature): Boolean =
        AchievementCatalog.isFeatureUnlocked(achievementProgress, feature)

    fun claimAchievement(achievementId: String, nowMillis: Long = 0L): PlaySessionState {
        val refreshed = AchievementTracker.refresh(this, nowMillis = nowMillis)
        val definition = AchievementCatalog.byId[achievementId] ?: return this
        val normalized = AchievementCatalog.normalizeProgress(refreshed.achievementProgress)
        val progress = normalized.firstOrNull { it.achievementId == achievementId } ?: return this
        if (!progress.isCompleted(definition) || progress.isClaimed) return this

        val rewarded = refreshed.applyAchievementReward(definition.reward, achievementId)
        val claimedAt = nowMillis.takeIf { it > 0L } ?: progress.completedAtMillis ?: 0L
        val nextProgress = normalized.map { item ->
            if (item.achievementId == achievementId) {
                item.copy(
                    current = definition.target,
                    completedAtMillis = item.completedAtMillis ?: claimedAt,
                    claimedAtMillis = claimedAt,
                    seen = true,
                )
            } else {
                item
            }
        }
        return AchievementTracker.refresh(rewarded.copy(achievementProgress = nextProgress), nowMillis = nowMillis)
    }

    fun claimAllAchievements(nowMillis: Long = 0L): PlaySessionState =
        claimableAchievements().fold(this) { nextState, definition ->
            nextState.claimAchievement(definition.id, nowMillis)
        }

    private fun recruitmentTicketRewardDeltaTo(after: PlaySessionState): Map<String, Int> {
        val beforeRewards = claimableRecruitmentTicketRewards()
        val afterRewards = after.claimableRecruitmentTicketRewards()
        return RecruitmentTicketCatalog.normalizedInventory(
            afterRewards.mapValues { (ticketId, count) ->
                count - (beforeRewards[ticketId] ?: 0)
            },
        )
    }

    fun normalizedCoreCrewHeroIds(): List<String> {
        val rosterIds = heroes.map { it.id }.toSet()
        return coreCrewHeroIds
            .distinct()
            .filter { it in rosterIds }
            .take(coreCrewSlots())
    }

    fun coreCrew(): List<Hero> {
        val crewIds = normalizedCoreCrewHeroIds()
        return crewIds.mapNotNull { heroId -> heroes.firstOrNull { it.id == heroId } }
    }

    fun coreCrewSlots(): Int =
        BASE_CORE_CREW_SLOTS + bunkRoomCoreCrewSlotBonus()

    fun coreCrewVacancyCount(): Int =
        (coreCrewSlots() - normalizedCoreCrewHeroIds().size).coerceAtLeast(0)

    fun canAssignCoreCrewHero(heroId: String): Boolean {
        val normalizedCrew = normalizedCoreCrewHeroIds()
        return heroes.any { it.id == heroId } &&
            (heroId in normalizedCrew || normalizedCrew.size < coreCrewSlots())
    }

    fun toggleCoreCrewHero(heroId: String): PlaySessionState {
        if (heroes.none { it.id == heroId }) return this
        val normalizedCrew = normalizedCoreCrewHeroIds()
        val nextCrew = when {
            heroId in normalizedCrew -> normalizedCrew - heroId
            normalizedCrew.size < coreCrewSlots() -> normalizedCrew + heroId
            else -> normalizedCrew
        }
        return copy(coreCrewHeroIds = nextCrew)
    }

    fun passiveGoldPerHour(activeExpeditionHeroIds: Collection<String> = emptyList()): Int {
        val crew = coreCrew()
        if (crew.isEmpty()) return 0

        val activeIds = activeExpeditionHeroIds.toSet()
        val crewGold = crew.sumOf { hero -> coreCrewContribution(hero, activeIds).goldPerHour }
        return PASSIVE_BASE_GOLD_PER_HOUR +
            guildLevel * PASSIVE_GUILD_LEVEL_GOLD_PER_HOUR +
            noticeBoardPassiveGoldPerHour() +
            accountantOfficePassiveGoldPerHour() +
            crewGold
    }

    fun passiveSuppliesPerHour(activeExpeditionHeroIds: Collection<String> = emptyList()): Int {
        val crew = coreCrew()
        if (crew.isEmpty()) return 0

        val activeIds = activeExpeditionHeroIds.toSet()
        val crewSupplies = crew.sumOf { hero -> coreCrewContribution(hero, activeIds).suppliesPerHour }
        return PASSIVE_BASE_SUPPLIES_PER_HOUR +
            scoutTablePassiveSuppliesPerHour() +
            tavernKitchenPassiveSuppliesPerHour() +
            crewSupplies
    }

    fun passiveLootFindChancePercent(): Int {
        val armoryLevel = facilityLevel(GuildFacility.ArmoryForge)
        val scoutLevel = facilityLevel(GuildFacility.ScoutTable)
        if (armoryLevel <= 0 || scoutLevel <= 0) return 0
        return (5 + armoryLevel * PASSIVE_ARMORY_LOOT_CHANCE_PERCENT_PER_LEVEL +
            scoutLevel * PASSIVE_SCOUT_LOOT_CHANCE_PERCENT_PER_LEVEL).coerceAtMost(PASSIVE_LOOT_CHANCE_PERCENT_CAP)
    }

    fun coreCrewContribution(
        hero: Hero,
        activeExpeditionHeroIds: Collection<String> = emptyList(),
    ): CoreCrewContribution {
        val basePower = PartyPowerCalculator.basePower(hero) + equipmentBonus(hero.id)
        val rarityBonus = basePower * passiveRarityBonusPercent(hero.rarity) / 100
        val fullGoldPerHour = PASSIVE_HERO_BASE_GOLD_PER_HOUR +
            basePower / 2 +
            hero.level * PASSIVE_HERO_LEVEL_GOLD_PER_HOUR +
            rarityBonus +
            passiveSpecialGoldPerHour(hero.special)
        val fullSuppliesPerHour = PASSIVE_HERO_BASE_SUPPLIES_PER_HOUR +
            hero.level / 2 +
            passiveRaritySuppliesBonus(hero.rarity) +
            passiveSpecialSuppliesPerHour(hero.special)
        val activePenaltyPercent = if (hero.id in activeExpeditionHeroIds) ACTIVE_EXPEDITION_CORE_CREW_PENALTY_PERCENT else 0
        val adjustedGoldPerHour = fullGoldPerHour * (100 - activePenaltyPercent) / 100
        val adjustedSuppliesPerHour = fullSuppliesPerHour * (100 - activePenaltyPercent) / 100
        return CoreCrewContribution(
            heroId = hero.id,
            heroName = hero.name,
            fullGoldPerHour = fullGoldPerHour,
            goldPerHour = adjustedGoldPerHour,
            fullSuppliesPerHour = fullSuppliesPerHour,
            suppliesPerHour = adjustedSuppliesPerHour,
            activeExpeditionPenaltyPercent = activePenaltyPercent,
        )
    }

    fun passiveIncomeReport(
        sinceMillis: Long,
        untilMillis: Long,
        activeExpeditionHeroIds: Collection<String> = emptyList(),
        activeUntilMillis: Long = sinceMillis,
    ): PassiveIncomeReport {
        val elapsedSeconds = ((untilMillis - sinceMillis) / 1000L).coerceAtLeast(0L)
        val cappedSeconds = elapsedSeconds.coerceAtMost(passiveIncomeCapSeconds())
        val activeSeconds = ((activeUntilMillis.coerceAtMost(untilMillis) - sinceMillis) / 1000L)
            .coerceAtLeast(0L)
            .coerceAtMost(cappedSeconds)
        val idleSeconds = (cappedSeconds - activeSeconds).coerceAtLeast(0L)
        val activeGoldPerHour = passiveGoldPerHour(activeExpeditionHeroIds)
        val idleGoldPerHour = passiveGoldPerHour()
        val rawGold = proratedGold(activeGoldPerHour, activeSeconds) + proratedGold(idleGoldPerHour, idleSeconds)
        val gold = if (cappedSeconds > 0L && maxOf(activeGoldPerHour, idleGoldPerHour) > 0) {
            rawGold.coerceAtLeast(1)
        } else {
            0
        }
        val activeSuppliesPerHour = passiveSuppliesPerHour(activeExpeditionHeroIds)
        val idleSuppliesPerHour = passiveSuppliesPerHour()
        val rawSupplies = proratedSupplies(activeSuppliesPerHour, activeSeconds) + proratedSupplies(idleSuppliesPerHour, idleSeconds)
        val supplies = if (cappedSeconds > 0L && maxOf(activeSuppliesPerHour, idleSuppliesPerHour) > 0) {
            rawSupplies.coerceAtLeast(1)
        } else {
            0
        }
        val random = Random(passiveIncomeSeed(sinceMillis, untilMillis))

        return PassiveIncomeReport(
            sinceMillis = sinceMillis,
            untilMillis = untilMillis,
            elapsedSeconds = elapsedSeconds,
            cappedSeconds = cappedSeconds,
            activeSeconds = activeSeconds,
            idleSeconds = idleSeconds,
            gold = gold,
            goldPerHour = idleGoldPerHour,
            activeGoldPerHour = activeGoldPerHour,
            coreCrewHeroIds = normalizedCoreCrewHeroIds(),
            supplies = supplies,
            suppliesPerHour = idleSuppliesPerHour,
            activeSuppliesPerHour = activeSuppliesPerHour,
            lootFinds = passiveLootFinds(cappedSeconds, random),
        )
    }

    fun passiveIncomeCapSeconds(): Long = PASSIVE_INCOME_CAP_SECONDS + passiveIncomeCapBonusSeconds()

    fun passiveIncomeCapBonusSeconds(): Long = bunkRoomOfflineCapBonusSeconds() + tavernKitchenOfflineCapBonusSeconds()

    fun adjustGold(delta: Int): PlaySessionState =
        copy(gold = (gold + delta).coerceAtLeast(0))

    fun adjustReputation(delta: Int): PlaySessionState =
        copy(reputation = (reputation + delta).coerceAtLeast(0))

    fun adjustGuildLevel(delta: Int): PlaySessionState =
        copy(guildLevel = (guildLevel + delta).coerceAtLeast(1))

    fun resetProgressForTesting(): PlaySessionState =
        copy(
            supplies = 0,
            completedQuestCount = 0,
            clearedQuestIds = emptySet(),
            specialContracts = 0,
            noticeBoardLevel = 1,
            trainingYardLevel = 1,
            bunkRoomLevel = 1,
            scoutTableLevel = 0,
            armoryForgeLevel = 0,
            tavernKitchenLevel = 0,
            infirmaryLevel = 0,
            accountantOfficeLevel = 0,
            heroes = HeroCatalog.starterHeroes,
            coreCrewHeroIds = defaultCoreCrewHeroIds(),
            lootRolls = 0,
            lootItems = emptyList(),
            pendingLootItems = emptyList(),
            pendingLootKeepLimit = 0,
            pendingLootKeptCount = 0,
            pendingLootCarryBreakdown = LootCarryBreakdown(),
            equippedLoot = emptyList(),
            journalEntries = emptyList(),
            expedition = null,
            achievementProgress = AchievementCatalog.initialProgress(),
            lastOfflinePassiveIncome = null,
            lastOfflinePassiveIncidents = emptyList(),
            recruitmentTickets = RecruitmentTicketCatalog.normalizedInventory(),
        )
    fun effectivePartySlots(quest: Quest): Int =
        quest.partySlots.coerceAtLeast(1) + (bunkRoomLevel - 1).coerceAtLeast(0)

    fun facilityLevel(facility: GuildFacility): Int =
        when (facility) {
            GuildFacility.NoticeBoard -> noticeBoardLevel
            GuildFacility.TrainingYard -> trainingYardLevel
            GuildFacility.BunkRoom -> bunkRoomLevel
            GuildFacility.ScoutTable -> scoutTableLevel
            GuildFacility.ArmoryForge -> armoryForgeLevel
            GuildFacility.TavernKitchen -> tavernKitchenLevel
            GuildFacility.Infirmary -> infirmaryLevel
            GuildFacility.AccountantOffice -> accountantOfficeLevel
        }

    fun facilityUpgradeState(facility: GuildFacility): GuildFacilityUpgradeState =
        GuildFacilityCatalog.upgradeState(facility, this)

    fun facilityUpgradeCost(facility: GuildFacility): Int =
        facilityUpgradeState(facility).nextCost ?: 0

    fun canUpgradeFacility(facility: GuildFacility): Boolean {
        val state = facilityUpgradeState(facility)
        return state.definition.implemented && state.unlocked && !state.maxed && state.missingGold == 0
    }

    fun noticeBoardUpgradeCost(): Int = facilityUpgradeCost(GuildFacility.NoticeBoard)

    fun trainingYardUpgradeCost(): Int = facilityUpgradeCost(GuildFacility.TrainingYard)

    fun bunkRoomUpgradeCost(): Int = facilityUpgradeCost(GuildFacility.BunkRoom)

    fun scoutTableUpgradeCost(): Int = facilityUpgradeCost(GuildFacility.ScoutTable)

    fun armoryForgeUpgradeCost(): Int = facilityUpgradeCost(GuildFacility.ArmoryForge)

    fun tavernKitchenUpgradeCost(): Int = facilityUpgradeCost(GuildFacility.TavernKitchen)

    fun infirmaryUpgradeCost(): Int = facilityUpgradeCost(GuildFacility.Infirmary)

    fun accountantOfficeUpgradeCost(): Int = facilityUpgradeCost(GuildFacility.AccountantOffice)

    fun upgradeNoticeBoard(): PlaySessionState = upgradeFacility(GuildFacility.NoticeBoard)

    fun upgradeTrainingYard(): PlaySessionState = upgradeFacility(GuildFacility.TrainingYard)

    fun upgradeBunkRoom(): PlaySessionState = upgradeFacility(GuildFacility.BunkRoom)

    fun upgradeScoutTable(): PlaySessionState = upgradeFacility(GuildFacility.ScoutTable)

    fun upgradeArmoryForge(): PlaySessionState = upgradeFacility(GuildFacility.ArmoryForge)

    fun upgradeTavernKitchen(): PlaySessionState = upgradeFacility(GuildFacility.TavernKitchen)

    fun upgradeInfirmary(): PlaySessionState = upgradeFacility(GuildFacility.Infirmary)

    fun upgradeAccountantOffice(): PlaySessionState = upgradeFacility(GuildFacility.AccountantOffice)

    fun upgradeFacility(facility: GuildFacility): PlaySessionState {
        if (!canUpgradeFacility(facility)) return this
        val upgradeState = facilityUpgradeState(facility)
        val cost = upgradeState.nextCost ?: return this
        val nextLevel = upgradeState.level + 1
        val upgraded = when (facility) {
            GuildFacility.NoticeBoard -> copy(gold = gold - cost, noticeBoardLevel = nextLevel)
            GuildFacility.TrainingYard -> copy(gold = gold - cost, trainingYardLevel = nextLevel)
            GuildFacility.BunkRoom -> copy(gold = gold - cost, bunkRoomLevel = nextLevel)
            GuildFacility.ScoutTable -> copy(gold = gold - cost, scoutTableLevel = nextLevel)
            GuildFacility.ArmoryForge -> copy(gold = gold - cost, armoryForgeLevel = nextLevel)
            GuildFacility.TavernKitchen -> copy(gold = gold - cost, tavernKitchenLevel = nextLevel)
            GuildFacility.Infirmary -> copy(gold = gold - cost, infirmaryLevel = nextLevel)
            GuildFacility.AccountantOffice -> copy(gold = gold - cost, accountantOfficeLevel = nextLevel)
        }
        return AchievementTracker.applyEvent(
            state = upgraded,
            event = AchievementEvent.FacilityUpgraded(upgradeState.definition.achievementFacilityId, nextLevel),
        )
    }

    fun equippedItems(heroId: String): List<LootItem> =
        equippedLoot.filter { it.heroId == heroId }.map { it.item }

    fun equipmentBonus(heroId: String): Int =
        equippedItems(heroId).sumOf { it.bonus }

    fun totalPartyPower(): Int =
        PartyPowerCalculator.totalPower(heroes, equippedLoot) + if (heroes.isEmpty()) 0 else trainingYardPowerBonus()

    fun equipLoot(heroId: String, item: LootItem): PlaySessionState {
        if (heroes.none { it.id == heroId }) return this
        val itemIndex = lootItems.indexOf(item)
        if (itemIndex < 0) return this

        val previousEquipped = equippedLoot.firstOrNull { equipped ->
            equipped.heroId == heroId && equipped.item.slot == item.slot
        }?.item
        val nextInventory = lootItems.toMutableList().also { inventory ->
            inventory.removeAt(itemIndex)
            if (previousEquipped != null) inventory.add(previousEquipped)
        }
        val nextEquipment = equippedLoot
            .filterNot { equipped -> equipped.heroId == heroId && equipped.item.slot == item.slot }
            .plus(EquippedLoot(heroId = heroId, item = item))

        return AchievementTracker.applyEvent(
            state = copy(lootItems = nextInventory, equippedLoot = nextEquipment),
            event = AchievementEvent.LootEquipped(heroId = heroId, item = item),
        )
    }

    fun keepPendingLoot(item: LootItem): PlaySessionState {
        val itemIndex = pendingLootItems.indexOf(item)
        if (itemIndex < 0) return this
        if (pendingLootRemainingChoices() <= 0) return discardPendingLoot()

        val nextPending = pendingLootItems.toMutableList().also { it.removeAt(itemIndex) }
        val nextKeptCount = pendingLootSelectedCount() + 1
        val keepLimit = pendingLootEffectiveKeepLimit()
        val shouldDestroyRest = nextPending.isEmpty() || nextKeptCount >= keepLimit
        val selected = copy(
            pendingLootItems = if (shouldDestroyRest) emptyList() else nextPending,
            pendingLootKeepLimit = if (shouldDestroyRest) 0 else keepLimit,
            pendingLootKeptCount = if (shouldDestroyRest) 0 else nextKeptCount,
            pendingLootCarryBreakdown = if (shouldDestroyRest) LootCarryBreakdown() else pendingLootRecoveryBreakdown(),
            lootItems = lootItems + item,
        )
        return AchievementTracker.applyEvent(
            state = selected,
            event = AchievementEvent.LootKept(item),
        )
    }

    fun keepPendingLootSelection(items: List<LootItem>): PlaySessionState {
        if (pendingLootItems.isEmpty()) return this
        val keepLimit = pendingLootEffectiveKeepLimit()
        if (keepLimit <= 0) return discardPendingLoot()

        val remainingPending = pendingLootItems.toMutableList()
        val keptItems = mutableListOf<LootItem>()
        for (item in items) {
            if (keptItems.size >= keepLimit) break
            val itemIndex = remainingPending.indexOf(item)
            if (itemIndex >= 0) {
                keptItems += remainingPending.removeAt(itemIndex)
            }
        }
        if (keptItems.isEmpty()) return discardPendingLoot()

        val selected = copy(
            pendingLootItems = emptyList(),
            pendingLootKeepLimit = 0,
            pendingLootKeptCount = 0,
            pendingLootCarryBreakdown = LootCarryBreakdown(),
            lootItems = lootItems + keptItems,
        )
        return keptItems.fold(selected) { state, item ->
            AchievementTracker.applyEvent(
                state = state,
                event = AchievementEvent.LootKept(item),
            )
        }
    }
    fun discardPendingLoot(item: LootItem? = null): PlaySessionState {
        if (item != null && item !in pendingLootItems) return this
        if (pendingLootItems.isEmpty()) return this
        return copy(
            pendingLootItems = emptyList(),
            pendingLootKeepLimit = 0,
            pendingLootKeptCount = 0,
            pendingLootCarryBreakdown = LootCarryBreakdown(),
        )
    }

    fun sellLoot(item: LootItem): PlaySessionState {
        val itemIndex = lootItems.indexOf(item)
        if (itemIndex < 0) return this
        val nextInventory = lootItems.toMutableList().also { it.removeAt(itemIndex) }
        return copy(
            gold = gold + LootEconomy.sellValue(item),
            lootItems = nextInventory,
        )
    }

    fun lootRerollCost(item: LootItem): Int =
        LootEconomy.rerollCost(item, armoryForgeLevel)

    fun canRerollLoot(item: LootItem): Boolean =
        armoryForgeLevel > 0 && item in lootItems && gold >= lootRerollCost(item)

    fun rerollLoot(item: LootItem, seed: Int = 0): PlaySessionState {
        if (!canRerollLoot(item)) return this
        val itemIndex = lootItems.indexOf(item)
        if (itemIndex < 0) return this
        val cost = lootRerollCost(item)
        val rerolled = LootGenerator.rerollItem(
            item = item,
            seed = seed + itemIndex * 101 + gold * 31 + armoryForgeLevel * 997,
        )
        val nextInventory = lootItems.toMutableList().also { it[itemIndex] = rerolled }
        return copy(
            gold = gold - cost,
            lootItems = nextInventory,
        )
    }

    fun unequipLoot(heroId: String, slot: LootSlot): PlaySessionState {
        val equipped = equippedLoot.firstOrNull { it.heroId == heroId && it.item.slot == slot } ?: return this
        return copy(
            lootItems = lootItems + equipped.item,
            equippedLoot = equippedLoot - equipped,
        )
    }


    fun canPromoteHeroWithBlankContract(heroId: String): Boolean =
        phase == PlayPhase.Idle &&
            recruitmentTicketCount(RecruitmentTicketCatalog.BLANK_CONTRACT_ID) > 0 &&
            heroes.any { hero -> hero.id == heroId && HeroPromotion.canPromote(hero) }

    fun promoteHeroWithBlankContract(heroId: String): PlaySessionState {
        if (!canPromoteHeroWithBlankContract(heroId)) return this
        val nextTickets = RecruitmentTicketCatalog.consumeFromInventory(
            recruitmentTickets,
            RecruitmentTicketCatalog.BLANK_CONTRACT_ID,
        ) ?: return this
        val promotedHeroes = heroes.map { hero ->
            if (hero.id == heroId) HeroPromotion.promote(hero) ?: hero else hero
        }
        return copy(
            heroes = promotedHeroes,
            recruitmentTickets = nextTickets,
        )
    }

    fun releaseHero(heroId: String): PlaySessionState {
        if (phase != PlayPhase.Idle || heroes.size <= 1 || heroes.none { it.id == heroId }) return this

        val releasedEquipment = equippedLoot.filter { it.heroId == heroId }
        return copy(
            heroes = heroes.filterNot { it.id == heroId },
            coreCrewHeroIds = normalizedCoreCrewHeroIds().filterNot { it == heroId },
            lootItems = lootItems + releasedEquipment.map { it.item },
            equippedLoot = equippedLoot.filterNot { it.heroId == heroId },
        )
    }

    fun recruitHero(seed: Int): HeroRecruitmentResult? {
        if (gold < HeroGacha.RECRUIT_COST) return null

        val recruitmentProfile = if (isLicensedTroubleRecruitmentReady()) {
            HeroGacha.palier2RecruitmentProfile
        } else {
            HeroGacha.baseRecruitmentProfile
        }
        val hero = HeroGacha.summon(
            pulls = 1,
            seed = seed,
            recruitmentProfile = recruitmentProfile,
        ).single()
        val duplicate = heroes.any { it.id == hero.id }
        val reputationReward = if (duplicate) duplicateReputationReward() else 0
        val duplicateBlankContractReward = if (duplicate) 1 else 0
        val recruited = copy(
            gold = gold - HeroGacha.RECRUIT_COST,
            reputation = reputation + reputationReward,
            recruitmentTickets = RecruitmentTicketCatalog.addToInventory(
                recruitmentTickets,
                if (duplicateBlankContractReward > 0) {
                    mapOf(RecruitmentTicketCatalog.BLANK_CONTRACT_ID to duplicateBlankContractReward)
                } else {
                    emptyMap()
                },
            ),
            heroes = if (duplicate) heroes else heroes + hero,
        )
        val tracked = AchievementTracker.applyEvent(
            state = recruited,
            event = AchievementEvent.HeroRecruited(hero = hero, duplicate = duplicate),
        )
        return HeroRecruitmentResult(
            session = tracked,
            hero = hero,
            cost = HeroGacha.RECRUIT_COST,
            duplicate = duplicate,
            reputationReward = reputationReward,
            duplicateBlankContractReward = duplicateBlankContractReward,
        )
    }

    private fun isLicensedTroubleRecruitmentReady(): Boolean =
        completedQuestCount >= HeroGacha.LICENSED_TROUBLE_RECRUITMENT_COMPLETED_QUEST_THRESHOLD
    fun recruitHeroWithTicket(ticketId: String, seed: Int): HeroRecruitmentResult? {
        val ticket = RecruitmentTicketCatalog.byId[ticketId]?.takeIf { it.isRecruitmentTicket } ?: return null
        val nextTickets = RecruitmentTicketCatalog.consumeFromInventory(recruitmentTickets, ticketId) ?: return null
        val result = RecruitmentTicketResolver.resolve(ticket = ticket, seed = seed, roster = heroes)
        val hero = result.hero ?: return null
        val duplicateBlankContractReward = if (result.duplicate) 1 else 0
        val rewardedTickets = RecruitmentTicketCatalog.addToInventory(
            nextTickets,
            if (duplicateBlankContractReward > 0) {
                mapOf(RecruitmentTicketCatalog.BLANK_CONTRACT_ID to duplicateBlankContractReward)
            } else {
                emptyMap()
            },
        )
        val recruited = copy(
            recruitmentTickets = rewardedTickets,
            reputation = reputation + if (result.duplicate) duplicateReputationReward() else result.reputationReward,
            heroes = if (result.duplicate) heroes else heroes + hero,
        )
        val tracked = AchievementTracker.applyEvent(
            state = recruited,
            event = AchievementEvent.HeroRecruited(hero = hero, duplicate = result.duplicate),
        )
        return HeroRecruitmentResult(
            session = tracked,
            hero = hero,
            cost = 0,
            duplicate = result.duplicate,
            reputationReward = if (result.duplicate) duplicateReputationReward() else result.reputationReward,
            duplicateBlankContractReward = duplicateBlankContractReward,
        )
    }
    companion object {
        private const val TRAINING_YARD_POWER_PER_LEVEL = 8
        private const val TRAINING_YARD_XP_BONUS_PERCENT_PER_LEVEL = 10
        private const val INFIRMARY_SAFE_PLAN_POWER_PER_LEVEL = 5
        private const val INFIRMARY_FAILURE_RECOVERY_PERCENT_PER_LEVEL = 15
        private const val TAVERN_KITCHEN_XP_BONUS_PERCENT_PER_LEVEL = 5
        private const val TAVERN_KITCHEN_LONG_QUEST_XP_BONUS_PERCENT_PER_LEVEL = 5
        private const val ACCOUNTANT_PAPERWORK_GOLD_BONUS_PERCENT_PER_LEVEL = 6
        private const val ACCOUNTANT_DUPLICATE_REPUTATION_PER_LEVEL = 2
        private const val LONG_EXPEDITION_SECONDS = 240
        private const val ACHIEVEMENT_TRAINING_POWER_BONUS = 12
        private const val ACHIEVEMENT_INSURANCE_PITY_GOLD_BONUS_PERCENT = 30
        private const val ACHIEVEMENT_CHARTER_QUEST_GOLD_BONUS_PERCENT = 10
        private const val BASE_LOOT_KEEP_LIMIT = 1
        private const val VETERAN_LOOT_CARRY_LEVEL = 5
        private const val SPECIALIST_LOOT_CARRY_LEVEL = 3
        private const val BASE_CORE_CREW_SLOTS = 3
        private const val CORE_CREW_EXTRA_SLOT_BUNK_ROOM_LEVEL = 2
        private const val PASSIVE_INCOME_CAP_SECONDS = 4 * 60 * 60L
        private const val PASSIVE_BASE_GOLD_PER_HOUR = 18
        private const val PASSIVE_GUILD_LEVEL_GOLD_PER_HOUR = 6
        private const val PASSIVE_NOTICE_BOARD_GOLD_PER_HOUR_PER_LEVEL = 5
        private const val PASSIVE_ACCOUNTANT_GOLD_PER_HOUR_PER_LEVEL = 14
        private const val PASSIVE_HERO_BASE_GOLD_PER_HOUR = 6
        private const val PASSIVE_HERO_LEVEL_GOLD_PER_HOUR = 3
        private const val PASSIVE_BASE_SUPPLIES_PER_HOUR = 1
        private const val PASSIVE_HERO_BASE_SUPPLIES_PER_HOUR = 1
        private const val PASSIVE_SCOUT_TABLE_SUPPLIES_PER_HOUR_PER_LEVEL = 1
        private const val PASSIVE_TAVERN_SUPPLIES_PER_HOUR_PER_LEVEL = 2
        private const val PASSIVE_BUNK_ROOM_CAP_BONUS_SECONDS_PER_LEVEL = 30 * 60L
        private const val PASSIVE_TAVERN_CAP_BONUS_SECONDS_PER_LEVEL = 45 * 60L
        private const val PASSIVE_ARMORY_LOOT_CHANCE_PERCENT_PER_LEVEL = 7
        private const val PASSIVE_SCOUT_LOOT_CHANCE_PERCENT_PER_LEVEL = 5
        private const val PASSIVE_LOOT_CHANCE_PERCENT_CAP = 45
        private const val PASSIVE_LOOT_MIN_SECONDS = 2 * 60 * 60L
        private const val PASSIVE_LOOT_GUARANTEED_SECONDS = 3 * 60 * 60L
        private const val ACTIVE_EXPEDITION_CORE_CREW_PENALTY_PERCENT = 50

        fun initial(): PlaySessionState = PlaySessionState()


    }

    private fun refreshedAchievementProgress(nowMillis: Long = 0L): List<AchievementProgress> =
        AchievementTracker.refresh(this, nowMillis = nowMillis).achievementProgress

    private fun appendPendingLoot(items: List<LootItem>, carryBreakdown: LootCarryBreakdown): PlaySessionState {
        if (items.isEmpty()) return this
        val normalizedBreakdown = carryBreakdown.withMinimumBase(BASE_LOOT_KEEP_LIMIT)
        val nextBreakdown = if (pendingLootItems.isEmpty()) {
            normalizedBreakdown
        } else {
            pendingLootRecoveryBreakdown() + normalizedBreakdown
        }
        return copy(
            pendingLootItems = pendingLootItems + items,
            pendingLootKeepLimit = nextBreakdown.total.coerceAtLeast(BASE_LOOT_KEEP_LIMIT),
            pendingLootKeptCount = if (pendingLootItems.isEmpty()) 0 else pendingLootKeptCount.coerceAtLeast(0),
            pendingLootCarryBreakdown = nextBreakdown,
        )
    }

    private fun veteranLootCarryBonus(party: List<Hero>): Int =
        if (party.any { it.level >= VETERAN_LOOT_CARRY_LEVEL }) 1 else 0

    private fun specialistLootCarryBonus(party: List<Hero>): Int =
        if (party.any { it.level >= SPECIALIST_LOOT_CARRY_LEVEL && HeroSpecialCatalog.isLootRecoverySpecial(it.special) }) 1 else 0

    private fun bunkRoomCoreCrewSlotBonus(): Int =
        if (bunkRoomLevel >= CORE_CREW_EXTRA_SLOT_BUNK_ROOM_LEVEL) 1 else 0

    private fun noticeBoardPassiveGoldPerHour(): Int =
        (noticeBoardLevel - 1).coerceAtLeast(0) * PASSIVE_NOTICE_BOARD_GOLD_PER_HOUR_PER_LEVEL

    private fun accountantOfficePassiveGoldPerHour(): Int =
        facilityLevel(GuildFacility.AccountantOffice) * PASSIVE_ACCOUNTANT_GOLD_PER_HOUR_PER_LEVEL

    private fun scoutTablePassiveSuppliesPerHour(): Int =
        facilityLevel(GuildFacility.ScoutTable) * PASSIVE_SCOUT_TABLE_SUPPLIES_PER_HOUR_PER_LEVEL

    private fun tavernKitchenPassiveSuppliesPerHour(): Int =
        facilityLevel(GuildFacility.TavernKitchen) * PASSIVE_TAVERN_SUPPLIES_PER_HOUR_PER_LEVEL

    private fun bunkRoomOfflineCapBonusSeconds(): Long =
        (bunkRoomLevel - 1).coerceAtLeast(0) * PASSIVE_BUNK_ROOM_CAP_BONUS_SECONDS_PER_LEVEL

    private fun tavernKitchenOfflineCapBonusSeconds(): Long =
        facilityLevel(GuildFacility.TavernKitchen) * PASSIVE_TAVERN_CAP_BONUS_SECONDS_PER_LEVEL

    private fun passiveRarityBonusPercent(rarity: HeroRarity): Int = when (rarity) {
        HeroRarity.Common -> 0
        HeroRarity.Uncommon -> 8
        HeroRarity.Rare -> 16
        HeroRarity.Epic -> 25
        HeroRarity.Legendary -> 40
    }

    private fun passiveSpecialGoldPerHour(special: HeroSpecial): Int = when (special) {
        HeroSpecial.BalancedBooks,
        HeroSpecial.HostileAudit -> 12
        HeroSpecial.MoraleRations,
        HeroSpecial.GreenThumb,
        HeroSpecial.PreservationSalt -> 8
        HeroSpecial.LightFingers,
        HeroSpecial.DirtyJackpot,
        HeroSpecial.FreshTrail -> 6
        else -> 3
    }

    private fun passiveRaritySuppliesBonus(rarity: HeroRarity): Int = when (rarity) {
        HeroRarity.Common -> 0
        HeroRarity.Uncommon -> 1
        HeroRarity.Rare -> 2
        HeroRarity.Epic -> 3
        HeroRarity.Legendary -> 4
    }

    private fun passiveSpecialSuppliesPerHour(special: HeroSpecial): Int = when (special) {
        HeroSpecial.MoraleRations,
        HeroSpecial.GreenThumb,
        HeroSpecial.PreservationSalt -> 2
        HeroSpecial.FreshTrail,
        HeroSpecial.LightFingers,
        HeroSpecial.DirtyJackpot -> 1
        else -> 0
    }

    private fun passiveLootFinds(cappedSeconds: Long, random: Random): List<LootItem> {
        val chance = passiveLootFindChancePercent()
        if (cappedSeconds < PASSIVE_LOOT_MIN_SECONDS || chance <= 0) return emptyList()

        val heavilyBuiltForPassiveLoot = armoryForgeLevel >= 3 && scoutTableLevel >= 2 && cappedSeconds >= PASSIVE_LOOT_GUARANTEED_SECONDS
        val finds = if (heavilyBuiltForPassiveLoot || random.nextInt(100) < chance) 1 else 0
        return LootGenerator.generate(
            rolls = finds,
            random = random,
            lootProfile = rewardLootProfile(),
        )
    }

    private fun passiveLootCarryBreakdown(findCount: Int): LootCarryBreakdown =
        if (findCount <= 0) LootCarryBreakdown() else LootCarryBreakdown(base = 1)

    private fun passiveIncomeSeed(sinceMillis: Long, untilMillis: Long): Int {
        var seed = mixedLongToInt(sinceMillis)
        seed = seed * 31 + mixedLongToInt(untilMillis)
        seed = seed * 31 + completedQuestCount
        seed = seed * 31 + gold
        seed = seed * 31 + supplies
        seed = seed * 31 + scoutTableLevel
        seed = seed * 31 + armoryForgeLevel
        seed = seed * 31 + tavernKitchenLevel
        seed = seed * 31 + infirmaryLevel
        seed = seed * 31 + accountantOfficeLevel
        seed = seed * 31 + normalizedCoreCrewHeroIds().hashCode()
        return seed
    }

    private fun proratedGold(goldPerHour: Int, seconds: Long): Int =
        (goldPerHour.toLong() * seconds / 3600L).toInt()

    private fun proratedSupplies(suppliesPerHour: Int, seconds: Long): Int =
        (suppliesPerHour.toLong() * seconds / 3600L).toInt()

    private fun mixedLongToInt(value: Long): Int = (value xor (value ushr 32)).toInt()

    private fun achievementAdjustedRewardGold(result: ExpeditionResult): Int {
        val pityBonusPercent = if (
            isAchievementFeatureUnlocked(AchievementFeature.InsuranceDesk) &&
            (result.outcome == ExpeditionOutcome.Failure || result.outcome == ExpeditionOutcome.RidiculousFailure)
        ) {
            ACHIEVEMENT_INSURANCE_PITY_GOLD_BONUS_PERCENT
        } else {
            0
        }
        return result.reward.gold + result.reward.gold * pityBonusPercent / 100
    }

    private fun achievementRewardChoiceLootRolls(result: ExpeditionResult): Int =
        if (
            isAchievementFeatureUnlocked(AchievementFeature.RewardChoice) &&
            result.outcome == ExpeditionOutcome.GreatSuccess
        ) {
            1
        } else {
            0
        }

    private fun achievementQuestGoldBonusPercent(): Int =
        if (isAchievementFeatureUnlocked(AchievementFeature.GuildCharterBonuses)) {
            ACHIEVEMENT_CHARTER_QUEST_GOLD_BONUS_PERCENT
        } else {
            0
        }

    private fun achievementTrainingPowerBonus(): Int =
        if (isAchievementFeatureUnlocked(AchievementFeature.HeroMentorship)) {
            ACHIEVEMENT_TRAINING_POWER_BONUS
        } else {
            0
        }

    private fun applyAchievementReward(
        reward: AchievementReward,
        achievementId: String,
    ): PlaySessionState = when (reward) {
        is AchievementReward.Currency -> {
            val generatedLoot = LootGenerator.generate(
                reward.lootRolls,
                seed = achievementRewardSeed(achievementId),
                lootProfile = LootGenerator.lootProfileForProgress(completedQuestCount),
            )
            copy(
                gold = gold + reward.gold,
                reputation = reputation + reward.reputation,
                lootRolls = lootRolls + reward.lootRolls,
                specialContracts = specialContracts + reward.specialContracts.coerceAtLeast(0),
            ).appendPendingLoot(generatedLoot, lootCarryBreakdown())
        }
        is AchievementReward.Tickets -> copy(
            recruitmentTickets = RecruitmentTicketCatalog.addToInventory(recruitmentTickets, reward.tickets),
        )
        is AchievementReward.Composite -> reward.rewards.fold(this) { state, item ->
            state.applyAchievementReward(item, achievementId)
        }
        AchievementReward.None -> this
    }


    private fun achievementRewardSeed(achievementId: String): Int =
        achievementId.fold(lootRolls * 31 + gold) { acc, char -> acc * 31 + char.code }
}

data class OfflineReportHighlights(
    val ticketInventory: Map<String, Int> = emptyMap(),
    val completedAchievementDelta: Int = 0,
    val claimableAchievementDelta: Int = 0,
    val claimableSealDelta: Int = 0,
    val claimableTicketRewardDelta: Map<String, Int> = emptyMap(),
) {
    val hasTicketProgress: Boolean
        get() = ticketInventory.isNotEmpty() || claimableTicketRewardDelta.isNotEmpty()

    val hasAchievementProgress: Boolean
        get() = completedAchievementDelta > 0 || claimableAchievementDelta > 0 || claimableSealDelta > 0
}

data class LootCarryBreakdown(
    val base: Int = 0,
    val bunkRoom: Int = 0,
    val veteran: Int = 0,
    val specialist: Int = 0,
) {
    val total: Int
        get() = base + bunkRoom + veteran + specialist

    operator fun plus(other: LootCarryBreakdown): LootCarryBreakdown = LootCarryBreakdown(
        base = base + other.base,
        bunkRoom = bunkRoom + other.bunkRoom,
        veteran = veteran + other.veteran,
        specialist = specialist + other.specialist,
    )

    fun withMinimumBase(minimumBase: Int): LootCarryBreakdown =
        if (total >= minimumBase) this else copy(base = base + minimumBase - total)
}

data class CoreCrewContribution(
    val heroId: String,
    val heroName: String,
    val fullGoldPerHour: Int,
    val goldPerHour: Int,
    val fullSuppliesPerHour: Int = 0,
    val suppliesPerHour: Int = 0,
    val activeExpeditionPenaltyPercent: Int = 0,
)

data class PassiveIncomeReport(
    val sinceMillis: Long,
    val untilMillis: Long,
    val elapsedSeconds: Long,
    val cappedSeconds: Long,
    val activeSeconds: Long,
    val idleSeconds: Long,
    val gold: Int,
    val goldPerHour: Int,
    val activeGoldPerHour: Int,
    val coreCrewHeroIds: List<String>,
    val supplies: Int = 0,
    val suppliesPerHour: Int = 0,
    val activeSuppliesPerHour: Int = 0,
    val lootFinds: List<LootItem> = emptyList(),
)

data class ExpeditionRun(
    val quest: Quest,
    val partyHeroIds: List<String> = emptyList(),
    val startedAtMillis: Long,
    val endsAtMillis: Long,
    val result: ExpeditionResult? = null,
    val planId: String = ExpeditionPlanCatalog.defaultPlanId,
)

data class HeroRecruitmentResult(
    val session: PlaySessionState,
    val hero: Hero,
    val cost: Int,
    val duplicate: Boolean,
    val reputationReward: Int,
    val duplicateBlankContractReward: Int = 0,
)

sealed interface PlayPhase {
    data object Idle : PlayPhase
    data object Running : PlayPhase
    data object ResultReady : PlayPhase
}

private fun ExpeditionResult.isFirstClearSuccess(): Boolean =
    outcome == ExpeditionOutcome.GreatSuccess || outcome == ExpeditionOutcome.Success

private fun ExpeditionOutcome.isAtLeastSuccess(): Boolean =
    this == ExpeditionOutcome.GreatSuccess || this == ExpeditionOutcome.Success

private fun ExpeditionOutcome.isFailure(): Boolean =
    this == ExpeditionOutcome.Failure || this == ExpeditionOutcome.RidiculousFailure

private fun specialContractsLootedFrom(result: ExpeditionResult, quest: Quest): Int = when {
    !result.isFirstClearSuccess() -> 0
    quest.hasAny(QuestTag.Contract) && result.outcome == ExpeditionOutcome.GreatSuccess -> 2
    quest.hasAny(QuestTag.Contract) -> 1
    result.outcome == ExpeditionOutcome.GreatSuccess && quest.risk == QuestRisk.High -> 1
    else -> 0
}

private fun recruitmentTicketRewards(reward: AchievementReward): Map<String, Int> = when (reward) {
    is AchievementReward.Tickets -> reward.tickets
    is AchievementReward.Composite -> reward.rewards.fold(RecruitmentTicketCatalog.normalizedInventory()) { inventory, item ->
        RecruitmentTicketCatalog.addToInventory(inventory, recruitmentTicketRewards(item))
    }
    is AchievementReward.Currency,
    AchievementReward.None -> RecruitmentTicketCatalog.normalizedInventory()
}

private fun defaultCoreCrewHeroIds(): List<String> =
    HeroCatalog.starterHeroes.take(3).map { it.id }