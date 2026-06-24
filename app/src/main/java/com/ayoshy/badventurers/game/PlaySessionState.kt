package com.ayoshy.badventurers.game

data class PlaySessionState(
    val gold: Int = 0,
    val reputation: Int = 0,
    val guildLevel: Int = 1,
    val completedQuestCount: Int = 0,
    val noticeBoardLevel: Int = 1,
    val trainingYardLevel: Int = 1,
    val bunkRoomLevel: Int = 1,
    val heroes: List<Hero> = HeroCatalog.starterHeroes,
    val lootRolls: Int = 0,
    val lootItems: List<LootItem> = emptyList(),
    val pendingLootItems: List<LootItem> = emptyList(),
    val pendingLootKeepLimit: Int = 0,
    val pendingLootKeptCount: Int = 0,
    val equippedLoot: List<EquippedLoot> = emptyList(),
    val journalEntries: List<JournalEntry> = emptyList(),
    val expedition: ExpeditionRun? = null,
    val achievementProgress: List<AchievementProgress> = AchievementCatalog.initialProgress(),
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
        val selectedParty = selectedPartyForQuest(quest, party)
        return copy(
            expedition = ExpeditionRun(
                quest = quest,
                partyHeroIds = selectedParty.map { it.id },
                startedAtMillis = nowMillis,
                endsAtMillis = nowMillis + ExpeditionPlanCatalog.durationSeconds(quest, planId) * 1000L,
                planId = ExpeditionPlanCatalog.coercePlanId(planId),
            ),
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
                    facilityPowerBonus = trainingYardPowerBonus(),
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
                facilityPowerBonus = trainingYardPowerBonus(),
                planId = run.planId,
            )
        } else {
            engine.resolve(
                party = partyForRun(run, party),
                quest = run.quest,
                roll = roll,
                equipment = equippedLoot,
                facilityPowerBonus = trainingYardPowerBonus(),
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
        val baseGold = collectableRewardGold(result)
        val extraGold = fakeRewardedAdReward?.extraGold ?: 0
        val extraLootRolls = fakeRewardedAdReward?.extraLootRolls ?: 0
        val totalLootRolls = collectableLootRolls(result) + extraLootRolls
        val participatingParty = partyForRun(run, party)
        val generatedLoot = LootGenerator.generate(totalLootRolls, seed = lootSeed(result) + extraLootRolls * 17)
        val generatedJournal = JournalGenerator.generate(result, participatingParty, run.quest)
        val advancedHeroes = heroesWithQuestXp(participatingParty, collectableHeroXp(result))
        val advancedParty = participatingParty.map { hero ->
            advancedHeroes.firstOrNull { it.id == hero.id } ?: hero
        }
        val collected = copy(
            gold = gold + baseGold + extraGold,
            lootRolls = lootRolls + totalLootRolls,
            completedQuestCount = completedQuestCount + 1,
            heroes = advancedHeroes,
            journalEntries = (journalEntries + generatedJournal).takeLast(12),
            expedition = null,
        ).appendPendingLoot(generatedLoot, lootCarryLimit(advancedParty))
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
        return AchievementTracker.applyEvent(this, AchievementEvent.OfflineCollected, nowMillis)
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

    fun trainingYardQuestXpBonusPercent(): Int =
        (trainingYardLevel - 1).coerceAtLeast(0) * TRAINING_YARD_XP_BONUS_PERCENT_PER_LEVEL

    fun collectableRewardGold(result: ExpeditionResult): Int =
        questGoldWithNoticeBoard(achievementAdjustedRewardGold(result))

    fun collectableHeroXp(result: ExpeditionResult): Int {
        val baseXp = result.reward.xp.coerceAtLeast(0)
        return baseXp + baseXp * trainingYardQuestXpBonusPercent() / 100
    }

    fun collectableLootRolls(result: ExpeditionResult): Int =
        result.reward.lootRolls + achievementRewardChoiceLootRolls(result)

    fun lootCarryLimit(party: List<Hero> = heroes): Int =
        (BASE_LOOT_KEEP_LIMIT + bunkRoomLootCarryBonus() + veteranLootCarryBonus(party) + specialistLootCarryBonus(party))
            .coerceAtLeast(BASE_LOOT_KEEP_LIMIT)

    fun bunkRoomLootCarryBonus(): Int = (bunkRoomLevel - 1).coerceAtLeast(0)

    fun pendingLootEffectiveKeepLimit(): Int =
        if (pendingLootItems.isEmpty()) 0 else pendingLootKeepLimit.coerceAtLeast(BASE_LOOT_KEEP_LIMIT)

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

    fun achievementProgressFor(definition: AchievementDefinition): AchievementProgress =
        refreshedAchievementProgress()
            .firstOrNull { it.achievementId == definition.id }
            ?: AchievementProgress(achievementId = definition.id)

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

    fun adjustGold(delta: Int): PlaySessionState =
        copy(gold = (gold + delta).coerceAtLeast(0))

    fun adjustReputation(delta: Int): PlaySessionState =
        copy(reputation = (reputation + delta).coerceAtLeast(0))

    fun adjustGuildLevel(delta: Int): PlaySessionState =
        copy(guildLevel = (guildLevel + delta).coerceAtLeast(1))

    fun resetProgressForTesting(): PlaySessionState =
        copy(
            completedQuestCount = 0,
            noticeBoardLevel = 1,
            trainingYardLevel = 1,
            bunkRoomLevel = 1,
            heroes = HeroCatalog.starterHeroes,
            lootRolls = 0,
            lootItems = emptyList(),
            pendingLootItems = emptyList(),
            pendingLootKeepLimit = 0,
            pendingLootKeptCount = 0,
            equippedLoot = emptyList(),
            journalEntries = emptyList(),
            expedition = null,
            achievementProgress = AchievementCatalog.initialProgress(),
        )

    fun effectivePartySlots(quest: Quest): Int =
        quest.partySlots.coerceAtLeast(1) + (bunkRoomLevel - 1).coerceAtLeast(0)

    fun facilityLevel(facility: GuildFacility): Int =
        when (facility) {
            GuildFacility.NoticeBoard -> noticeBoardLevel
            GuildFacility.TrainingYard -> trainingYardLevel
            GuildFacility.BunkRoom -> bunkRoomLevel
            GuildFacility.ArmoryForge,
            GuildFacility.Infirmary,
            GuildFacility.ScoutTable,
            GuildFacility.TavernKitchen,
            GuildFacility.AccountantOffice -> 0
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

    fun upgradeNoticeBoard(): PlaySessionState = upgradeFacility(GuildFacility.NoticeBoard)

    fun upgradeTrainingYard(): PlaySessionState = upgradeFacility(GuildFacility.TrainingYard)

    fun upgradeBunkRoom(): PlaySessionState = upgradeFacility(GuildFacility.BunkRoom)

    fun upgradeFacility(facility: GuildFacility): PlaySessionState {
        if (!canUpgradeFacility(facility)) return this
        val upgradeState = facilityUpgradeState(facility)
        val cost = upgradeState.nextCost ?: return this
        val nextLevel = upgradeState.level + 1
        val upgraded = when (facility) {
            GuildFacility.NoticeBoard -> copy(gold = gold - cost, noticeBoardLevel = nextLevel)
            GuildFacility.TrainingYard -> copy(gold = gold - cost, trainingYardLevel = nextLevel)
            GuildFacility.BunkRoom -> copy(gold = gold - cost, bunkRoomLevel = nextLevel)
            GuildFacility.ArmoryForge,
            GuildFacility.Infirmary,
            GuildFacility.ScoutTable,
            GuildFacility.TavernKitchen,
            GuildFacility.AccountantOffice -> return this
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
            lootItems = lootItems + item,
        )
        return AchievementTracker.applyEvent(
            state = selected,
            event = AchievementEvent.LootKept(item),
        )
    }

    fun discardPendingLoot(item: LootItem? = null): PlaySessionState {
        if (item != null && item !in pendingLootItems) return this
        if (pendingLootItems.isEmpty()) return this
        return copy(
            pendingLootItems = emptyList(),
            pendingLootKeepLimit = 0,
            pendingLootKeptCount = 0,
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

    fun unequipLoot(heroId: String, slot: LootSlot): PlaySessionState {
        val equipped = equippedLoot.firstOrNull { it.heroId == heroId && it.item.slot == slot } ?: return this
        return copy(
            lootItems = lootItems + equipped.item,
            equippedLoot = equippedLoot - equipped,
        )
    }


    fun releaseHero(heroId: String): PlaySessionState {
        if (phase != PlayPhase.Idle || heroes.size <= 1 || heroes.none { it.id == heroId }) return this

        val releasedEquipment = equippedLoot.filter { it.heroId == heroId }
        return copy(
            heroes = heroes.filterNot { it.id == heroId },
            lootItems = lootItems + releasedEquipment.map { it.item },
            equippedLoot = equippedLoot.filterNot { it.heroId == heroId },
        )
    }

    fun recruitHero(seed: Int): HeroRecruitmentResult? {
        if (gold < HeroGacha.RECRUIT_COST) return null

        val hero = HeroGacha.summon(pulls = 1, seed = seed).single()
        val duplicate = heroes.any { it.id == hero.id }
        val reputationReward = if (duplicate) HeroGacha.DUPLICATE_REPUTATION_REWARD else 0
        val recruited = copy(
            gold = gold - HeroGacha.RECRUIT_COST,
            reputation = reputation + reputationReward,
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
        )
    }

    companion object {
        private const val TRAINING_YARD_POWER_PER_LEVEL = 8
        private const val TRAINING_YARD_XP_BONUS_PERCENT_PER_LEVEL = 10
        private const val ACHIEVEMENT_TRAINING_POWER_BONUS = 12
        private const val ACHIEVEMENT_INSURANCE_PITY_GOLD_BONUS_PERCENT = 30
        private const val ACHIEVEMENT_CHARTER_QUEST_GOLD_BONUS_PERCENT = 10
        private const val BASE_LOOT_KEEP_LIMIT = 1
        private const val VETERAN_LOOT_CARRY_LEVEL = 5
        private const val SPECIALIST_LOOT_CARRY_LEVEL = 3
        private val LOOT_CARRY_SPECIALS = setOf(
            HeroSpecial.LightFingers,
            HeroSpecial.DirtyJackpot,
            HeroSpecial.PreservationSalt,
        )

        fun initial(): PlaySessionState = PlaySessionState()


    }

    private fun refreshedAchievementProgress(nowMillis: Long = 0L): List<AchievementProgress> =
        AchievementTracker.refresh(this, nowMillis = nowMillis).achievementProgress

    private fun appendPendingLoot(items: List<LootItem>, keepLimit: Int): PlaySessionState {
        if (items.isEmpty()) return this
        val nextKeepLimit = if (pendingLootItems.isEmpty()) {
            keepLimit.coerceAtLeast(BASE_LOOT_KEEP_LIMIT)
        } else {
            pendingLootEffectiveKeepLimit() + keepLimit.coerceAtLeast(BASE_LOOT_KEEP_LIMIT)
        }
        return copy(
            pendingLootItems = pendingLootItems + items,
            pendingLootKeepLimit = nextKeepLimit,
            pendingLootKeptCount = if (pendingLootItems.isEmpty()) 0 else pendingLootKeptCount.coerceAtLeast(0),
        )
    }

    private fun veteranLootCarryBonus(party: List<Hero>): Int =
        if (party.any { it.level >= VETERAN_LOOT_CARRY_LEVEL }) 1 else 0

    private fun specialistLootCarryBonus(party: List<Hero>): Int =
        if (party.any { it.level >= SPECIALIST_LOOT_CARRY_LEVEL && it.special in LOOT_CARRY_SPECIALS }) 1 else 0

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
            )
            copy(
                gold = gold + reward.gold,
                reputation = reputation + reward.reputation,
                lootRolls = lootRolls + reward.lootRolls,
            ).appendPendingLoot(generatedLoot, lootCarryLimit())
        }
        is AchievementReward.Composite -> reward.rewards.fold(this) { state, item ->
            state.applyAchievementReward(item, achievementId)
        }
        AchievementReward.None -> this
    }

    private fun achievementRewardSeed(achievementId: String): Int =
        achievementId.fold(lootRolls * 31 + gold) { acc, char -> acc * 31 + char.code }
}

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
)

sealed interface PlayPhase {
    data object Idle : PlayPhase
    data object Running : PlayPhase
    data object ResultReady : PlayPhase
}
