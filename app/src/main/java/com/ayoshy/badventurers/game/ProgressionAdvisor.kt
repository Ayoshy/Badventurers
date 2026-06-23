package com.ayoshy.badventurers.game

enum class ProgressionAdviceKind {
    ViewQuestReport,
    HandleLootRewards,
    UpgradeNoticeBoard,
    UpgradeTrainingYard,
    UpgradeBunkRoom,
    ImprovePartyFit,
    EquipLoot,
    RecruitHero,
    ClaimAchievement,
    EarnReputation,
    CompleteQuests,
    StartQuest,
}



data class ProgressionAdvice(
    val kind: ProgressionAdviceKind,
    val priority: Int,
    val questId: String? = null,
    val facility: GuildFacility? = null,
    val heroIds: List<String> = emptyList(),
    val itemId: String? = null,
    val cost: Int = 0,
    val missingGold: Int = 0,
    val currentSuccessChancePercent: Int? = null,
    val projectedSuccessChancePercent: Int? = null,
    val missingReputation: Int = 0,
    val missingCompletedQuests: Int = 0,
)

object ProgressionAdvisor {
    private const val LOW_SUCCESS_THRESHOLD = 70
    private const val MEANINGFUL_SUCCESS_GAIN = 10

    fun recommend(
        state: PlaySessionState,
        selectedQuest: Quest = SeedGame.firstQuest,
        selectedParty: List<Hero> = state.selectedPartyForQuest(selectedQuest, state.heroes),
        quests: List<Quest> = SeedGame.quests,
    ): ProgressionAdvice {
        state.expedition?.result?.let { result ->
            return ProgressionAdvice(
                kind = ProgressionAdviceKind.ViewQuestReport,
                priority = 100,
                questId = state.expedition.quest.id,
                currentSuccessChancePercent = result.outcome.successWeight(),
            )
        }

        if (state.pendingLootItems.isNotEmpty()) {
            return ProgressionAdvice(
                kind = ProgressionAdviceKind.HandleLootRewards,
                priority = 95,
                itemId = state.pendingLootItems.first().id,
            )
        }

        lockedQuestAdvice(state, selectedQuest)?.let { return it }
        lowOddsAdvice(state, selectedQuest, selectedParty)?.let { return it }
        affordableFacilityAdvice(state, quests)?.let { return it }

        if (state.claimableAchievementCount() > 0) {
            return ProgressionAdvice(
                kind = ProgressionAdviceKind.ClaimAchievement,
                priority = 45,
            )
        }

        bestEquipAdvice(state)?.let { return it }

        if (state.gold >= HeroGacha.RECRUIT_COST && state.heroes.size < selectedQuest.partySlots + 2) {
            return ProgressionAdvice(
                kind = ProgressionAdviceKind.RecruitHero,
                priority = 25,
                cost = HeroGacha.RECRUIT_COST,
            )
        }

        val bestUnlockedQuest = quests
            .filter(state::isQuestUnlocked)
            .maxByOrNull { it.baseGold }
            ?: selectedQuest

        return ProgressionAdvice(
            kind = ProgressionAdviceKind.StartQuest,
            priority = 10,
            questId = bestUnlockedQuest.id,
        )
    }

    private fun lockedQuestAdvice(state: PlaySessionState, quest: Quest): ProgressionAdvice? {
        if (state.isQuestUnlocked(quest)) return null

        return quest.unlockRequirement.conditions
            .flatMap { condition -> unmetConditionAdvice(state, quest, condition) }
            .sortedWith(
                compareByDescending<ProgressionAdvice> { it.priority }
                    .thenByDescending { it.missingGold == 0 },
            )
            .firstOrNull()
    }

    private fun unmetConditionAdvice(
        state: PlaySessionState,
        quest: Quest,
        condition: QuestUnlockCondition,
    ): List<ProgressionAdvice> = buildList {
        if (state.bunkRoomLevel < condition.minBunkRoomLevel) {
            add(facilityAdvice(state, quest, GuildFacility.BunkRoom, state.bunkRoomUpgradeCost(), priority = 90))
        }
        if (state.trainingYardLevel < condition.minTrainingYardLevel) {
            add(facilityAdvice(state, quest, GuildFacility.TrainingYard, state.trainingYardUpgradeCost(), priority = 88))
        }
        if (state.noticeBoardLevel < condition.minNoticeBoardLevel) {
            add(facilityAdvice(state, quest, GuildFacility.NoticeBoard, state.noticeBoardUpgradeCost(), priority = 86))
        }
        if (state.reputation < condition.minReputation) {
            add(
                ProgressionAdvice(
                    kind = ProgressionAdviceKind.EarnReputation,
                    priority = 72,
                    questId = quest.id,
                    missingReputation = condition.minReputation - state.reputation,
                ),
            )
        }
        if (state.completedQuestCount < condition.minCompletedQuestCount) {
            add(
                ProgressionAdvice(
                    kind = ProgressionAdviceKind.CompleteQuests,
                    priority = 70,
                    questId = quest.id,
                    missingCompletedQuests = condition.minCompletedQuestCount - state.completedQuestCount,
                ),
            )
        }
    }

    private fun facilityAdvice(
        state: PlaySessionState,
        quest: Quest?,
        facility: GuildFacility,
        cost: Int,
        priority: Int,
    ): ProgressionAdvice =
        ProgressionAdvice(
            kind = when (facility) {
                GuildFacility.NoticeBoard -> ProgressionAdviceKind.UpgradeNoticeBoard
                GuildFacility.TrainingYard -> ProgressionAdviceKind.UpgradeTrainingYard
                GuildFacility.BunkRoom -> ProgressionAdviceKind.UpgradeBunkRoom
                GuildFacility.ArmoryForge,
                GuildFacility.Infirmary,
                GuildFacility.ScoutTable,
                GuildFacility.TavernKitchen,
                GuildFacility.AccountantOffice -> error("Unsupported facility advice: $facility")
            },
            priority = priority,
            questId = quest?.id,
            facility = facility,
            cost = cost,
            missingGold = (cost - state.gold).coerceAtLeast(0),
        )

    private fun lowOddsAdvice(
        state: PlaySessionState,
        quest: Quest,
        selectedParty: List<Hero>,
    ): ProgressionAdvice? {
        if (!state.isQuestUnlocked(quest)) return null

        val partySlots = state.effectivePartySlots(quest)
        val currentEstimate = ExpeditionEstimator.estimate(
            party = selectedParty,
            quest = quest,
            equipment = state.equippedLoot,
            facilityPowerBonus = state.trainingYardPowerBonus(),
        )
        if (currentEstimate.successChancePercent >= LOW_SUCCESS_THRESHOLD) return null

        val recommendedParty = HeroRecommendationScorer.rankHeroes(
            roster = state.heroes,
            quest = quest,
            selectedParty = selectedParty,
            equipment = state.equippedLoot,
            facilityPowerBonus = state.trainingYardPowerBonus(),
            partySlots = partySlots,
        )
            .map { it.hero }
            .take(partySlots)
        val projectedEstimate = ExpeditionEstimator.estimate(
            party = recommendedParty,
            quest = quest,
            equipment = state.equippedLoot,
            facilityPowerBonus = state.trainingYardPowerBonus(),
        )
        val selectedIds = selectedParty.map { it.id }.toSet()
        val recommendedIds = recommendedParty.map { it.id }
        val successGain = projectedEstimate.successChancePercent - currentEstimate.successChancePercent

        if (recommendedIds.toSet() != selectedIds && successGain >= MEANINGFUL_SUCCESS_GAIN) {
            return ProgressionAdvice(
                kind = ProgressionAdviceKind.ImprovePartyFit,
                priority = 82,
                questId = quest.id,
                heroIds = recommendedIds,
                currentSuccessChancePercent = currentEstimate.successChancePercent,
                projectedSuccessChancePercent = projectedEstimate.successChancePercent,
            )
        }

        return facilityAdvice(
            state = state,
            quest = quest,
            facility = GuildFacility.TrainingYard,
            cost = state.trainingYardUpgradeCost(),
            priority = 78,
        )
    }

    private fun affordableFacilityAdvice(state: PlaySessionState, quests: List<Quest>): ProgressionAdvice? {
        val futureLockedByBunkRoom = quests.any { quest ->
            !state.isQuestUnlocked(quest) &&
                quest.unlockRequirement.conditions.any { it.minBunkRoomLevel > state.bunkRoomLevel }
        }
        val candidates = buildList {
            if (state.noticeBoardLevel <= 2) {
                add(facilityAdvice(state, null, GuildFacility.NoticeBoard, state.noticeBoardUpgradeCost(), priority = 60))
            }
            if (state.trainingYardLevel <= 2) {
                add(facilityAdvice(state, null, GuildFacility.TrainingYard, state.trainingYardUpgradeCost(), priority = 58))
            }
            if (futureLockedByBunkRoom) {
                add(facilityAdvice(state, null, GuildFacility.BunkRoom, state.bunkRoomUpgradeCost(), priority = 56))
            }
        }

        return candidates
            .filter { it.missingGold == 0 }
            .maxByOrNull { it.priority }
    }

    private fun bestEquipAdvice(state: PlaySessionState): ProgressionAdvice? =
        state.lootItems
            .flatMap { item ->
                state.heroes.map { hero ->
                    val currentItem = state.equippedItems(hero.id).firstOrNull { it.slot == item.slot }
                    val gain = item.bonus - (currentItem?.bonus ?: 0)
                    Triple(item, hero, gain)
                }
            }
            .filter { (_, _, gain) -> gain > 0 }
            .maxWithOrNull(
                compareBy<Triple<LootItem, Hero, Int>> { it.third }
                    .thenBy { it.second.name },
            )
            ?.let { (item, hero, gain) ->
                ProgressionAdvice(
                    kind = ProgressionAdviceKind.EquipLoot,
                    priority = 35 + gain,
                    heroIds = listOf(hero.id),
                    itemId = item.id,
                )
            }

    private fun ExpeditionOutcome.successWeight(): Int = when (this) {
        ExpeditionOutcome.GreatSuccess -> 100
        ExpeditionOutcome.Success -> 80
        ExpeditionOutcome.PartialSuccess -> 55
        ExpeditionOutcome.Failure -> 20
        ExpeditionOutcome.RidiculousFailure -> 0
    }
}
