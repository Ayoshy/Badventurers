package com.ayoshy.badventurers.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuestCatalogueBalanceTest {
    @Test
    fun starterRosterCanAttemptEarlyCatalogueButHighRiskStillBites() {
        val chances = SeedGame.quests.associate { quest ->
            quest.id to bestEstimate(SeedGame.heroes, quest).successChancePercent
        }

        assertEquals(100, chances.getValue("cave_minor_regrets"))
        assertEquals(100, chances.getValue("forest_of_wrong_turns"))
        assertInRange(chances.getValue("bandit_tax_office"), 75, 95)
        assertInRange(chances.getValue("salted_swamp_chapel"), 70, 90)
        assertInRange(chances.getValue("moonlit_smuggler_run"), 70, 90)
        assertInRange(chances.getValue("the_hungry_siege"), 45, 70)
        assertInRange(chances.getValue("the_last_locked_door"), 35, 60)
        assertTrue(chances.getValue("crypt_of_unpaid_debts") <= 20)
        assertTrue(chances.getValue("wedding_with_too_many_oaths") <= 20)
        assertTrue(chances.getValue("the_sunken_toll_booth") <= 20)
        assertTrue(chances.getValue("the_crowns_missing_receipt") <= 20)
        assertTrue(chances.getValue("the_tower_built_sideways") <= 20)
    }

    @Test
    fun recruitedRosterHasGoodOddsAcrossExpandedCatalogue() {
        val recruitedRoster = heroesById(
            "brugg",
            "mira",
            "nell",
            "darrik",
            "quill",
            "orla",
            "bramble",
            "pax",
            "sable",
            "comptable",
            "jardinier",
            "chef_cuistot",
            "expert_en_demolition",
            "elementaire_de_sel",
            "troll_stupide",
        )

        SeedGame.quests.forEach { quest ->
            val estimate = bestEstimate(recruitedRoster, quest)

            assertTrue("${quest.id} was only ${estimate.successChancePercent}%", estimate.successChancePercent >= 85)
        }
    }

    @Test
    fun upgradedRosterCanSafelyClearEveryQuest() {
        val upgradedRoster = HeroCatalog.heroes.map { it.toHero() }
        val equipment = upgradedRoster.map { hero -> testGear(hero.id, bonus = 18) }

        SeedGame.quests.forEach { quest ->
            val estimate = bestEstimate(
                roster = upgradedRoster,
                quest = quest,
                equipment = equipment,
                facilityPowerBonus = 24,
            )

            assertTrue("${quest.id} was only ${estimate.successChancePercent}%", estimate.successChancePercent >= 95)
        }
    }

    @Test
    fun leveledStartersGainMeaningfulCatchUpOnMediumAndHighRiskQuests() {
        val hungrySiege = SeedGame.questById.getValue("the_hungry_siege")
        val lockedDoor = SeedGame.questById.getValue("the_last_locked_door")
        val leveledStarters = SeedGame.heroes.map { hero -> HeroProgression.withProgress(hero, level = 8, xp = 0) }

        val baseHungrySiege = bestEstimate(SeedGame.heroes, hungrySiege).successChancePercent
        val leveledHungrySiege = bestEstimate(leveledStarters, hungrySiege).successChancePercent
        val baseLockedDoor = bestEstimate(SeedGame.heroes, lockedDoor).successChancePercent
        val leveledLockedDoor = bestEstimate(leveledStarters, lockedDoor).successChancePercent

        assertTrue(leveledHungrySiege > baseHungrySiege)
        assertTrue("leveled starters should stabilize the last medium-risk starter route", leveledHungrySiege >= 95)
        assertTrue("level gaps should materially improve first high-risk odds", leveledLockedDoor >= baseLockedDoor + 30)
    }

    @Test
    fun maxRankPromotionProjectionStaysBelowFullyUpgradedSafetyCeiling() {
        val promotedRoster = HeroCatalog.heroes.map { definition ->
            HeroPromotion.previewPromoted(definition.toHero(), HeroPromotion.MAX_RANK)
        }
        val upgradedRoster = HeroCatalog.heroes.map { it.toHero() }
        val equipment = upgradedRoster.map { hero -> testGear(hero.id, bonus = 18) }

        SeedGame.quests.forEach { quest ->
            val promotedEstimate = bestEstimate(promotedRoster, quest)
            val upgradedEstimate = bestEstimate(
                roster = upgradedRoster,
                quest = quest,
                equipment = equipment,
                facilityPowerBonus = 24,
            )

            assertTrue(
                "${quest.id} promotion projection should not beat equipment plus facilities",
                promotedEstimate.partyPower < upgradedEstimate.partyPower,
            )
            assertTrue("${quest.id} was only ${promotedEstimate.successChancePercent}%", promotedEstimate.successChancePercent >= 85)
        }
    }

    @Test
    fun questsReferenceExistingRecommendedHeroes() {
        SeedGame.quests.forEach { quest ->
            assertTrue("${quest.id} needs recommended heroes", quest.recommendedHeroIds.isNotEmpty())
            quest.recommendedHeroIds.forEach { heroId ->
                assertTrue("${quest.id} recommends unknown hero $heroId", heroId in HeroCatalog.byId)
            }
        }
    }
    @Test
    fun localDisasterUnlocksAreBalancedAroundContractPlans() {
        val questById = SeedGame.questById

        fun unlocks(questId: String): List<QuestUnlockCondition> =
            questById.getValue(questId).unlockRequirement.conditions

        assertEquals(emptyList<QuestUnlockCondition>(), unlocks("cave_minor_regrets"))
        assertEquals(
            listOf(QuestUnlockCondition(minCompletedQuestCount = 1), QuestUnlockCondition(minReputation = 3)),
            unlocks("forest_of_wrong_turns"),
        )
        assertEquals(
            listOf(
                QuestUnlockCondition(minCompletedQuestCount = 2),
                QuestUnlockCondition(minReputation = 8),
                QuestUnlockCondition(minNoticeBoardLevel = 2),
            ),
            unlocks("bandit_tax_office"),
        )
        assertEquals(
            listOf(
                QuestUnlockCondition(minCompletedQuestCount = 2),
                QuestUnlockCondition(minReputation = 10),
                QuestUnlockCondition(minTrainingYardLevel = 2),
            ),
            unlocks("salted_swamp_chapel"),
        )
        assertEquals(
            listOf(
                QuestUnlockCondition(minCompletedQuestCount = 3),
                QuestUnlockCondition(minReputation = 12),
                QuestUnlockCondition(minNoticeBoardLevel = 2),
            ),
            unlocks("moonlit_smuggler_run"),
        )
        assertEquals(
            listOf(
                QuestUnlockCondition(minCompletedQuestCount = 4),
                QuestUnlockCondition(minReputation = 15),
                QuestUnlockCondition(minBunkRoomLevel = 2),
            ),
            unlocks("the_hungry_siege"),
        )
        assertEquals(
            listOf(
                QuestUnlockCondition(minCompletedQuestCount = 5),
                QuestUnlockCondition(minReputation = 20),
                QuestUnlockCondition(minTrainingYardLevel = 3),
            ),
            unlocks("the_last_locked_door"),
        )
        assertEquals(
            listOf(
                QuestUnlockCondition(minCompletedQuestCount = 5),
                QuestUnlockCondition(minReputation = 22),
                QuestUnlockCondition(minNoticeBoardLevel = 3),
            ),
            unlocks("crypt_of_unpaid_debts"),
        )
    }
    @Test
    fun licensedTroubleQuestsAddedToCatalogueWithUnlockAndPlanCoverage() {
        val licensedTroubleQuestIds = listOf(
            "paperwork_toll_of_chaos",
            "licensed_guild_caravan_haunt",
            "notary_night_patrol",
            "inspectorate_cove_banquet",
        )
        assertEquals(16, SeedGame.quests.size)

        val questById = SeedGame.questById
        val licensedTroubleQuests = licensedTroubleQuestIds.associateWith { questById.getValue(it) }

        assertEquals(
            listOf(30, 34, 38, 42),
            licensedTroubleQuestIds.map { licensedTroubleQuests.getValue(it).unlockRequirement.conditions.single().minReputation },
        )
        assertEquals(
            listOf(8, 9, 10, 11),
            licensedTroubleQuestIds.map { licensedTroubleQuests.getValue(it).unlockRequirement.conditions.single().minCompletedQuestCount },
        )

        val unlockedFacilityProgress = listOf(
            QuestUnlockCondition(minNoticeBoardLevel = 2),
            QuestUnlockCondition(minTrainingYardLevel = 2),
            QuestUnlockCondition(minBunkRoomLevel = 2),
            QuestUnlockCondition(minNoticeBoardLevel = 3, minTrainingYardLevel = 3, minBunkRoomLevel = 2),
        )
        licensedTroubleQuestIds.forEachIndexed { index, questId ->
            val quest = licensedTroubleQuests.getValue(questId)
            val unlock = quest.unlockRequirement.conditions.single()

            assertTrue(quest.recommendedHeroIds.isNotEmpty())
            quest.recommendedHeroIds.forEach { heroId ->
                assertTrue("${quest.id} recommends unknown hero $heroId", heroId in HeroCatalog.byId)
            }

            val facilityCheck = unlockedFacilityProgress[index]
            assertTrue(
                "${quest.id} notice board threshold should progress",
                unlock.minNoticeBoardLevel >= facilityCheck.minNoticeBoardLevel,
            )
            assertTrue(
                "${quest.id} training yard threshold should progress",
                unlock.minTrainingYardLevel >= facilityCheck.minTrainingYardLevel,
            )
            assertTrue(
                "${quest.id} bunk room threshold should progress",
                unlock.minBunkRoomLevel >= facilityCheck.minBunkRoomLevel,
            )

            assertTrue(
                "${quest.id} should have at least one quest-specific plan",
                ExpeditionPlanCatalog.availableFor(quest).any { plan -> quest.id in plan.questIds },
            )
        }
    }

    @Test
    fun regionalLiabilityQuestsAddedToCatalogueWithUnlockRewardsAndPlanCoverage() {
        val regionalLiabilityQuestIds = listOf(
            "wedding_with_too_many_oaths",
            "the_sunken_toll_booth",
            "the_crowns_missing_receipt",
            "the_tower_built_sideways",
        )
        assertEquals(16, SeedGame.quests.size)

        val questById = SeedGame.questById
        val regionalLiabilityQuests = regionalLiabilityQuestIds.associateWith { questById.getValue(it) }

        assertEquals(
            listOf(48, 52, 56, 60),
            regionalLiabilityQuestIds.map { quest ->
                regionalLiabilityQuests.getValue(quest).unlockRequirement.conditions.single().minReputation
            },
        )
        assertEquals(
            listOf(12, 13, 14, 15),
            regionalLiabilityQuestIds.map { quest ->
                regionalLiabilityQuests.getValue(quest).unlockRequirement.conditions.single().minCompletedQuestCount
            },
        )
        assertEquals(
            listOf(330, 360, 390, 420),
            regionalLiabilityQuestIds.map { quest -> regionalLiabilityQuests.getValue(quest).durationSeconds },
        )

        assertEquals(
            mapOf(RecruitmentTicketCatalog.RARE_CONTRACT_TICKET_ID to 1),
            regionalLiabilityQuests.getValue("wedding_with_too_many_oaths").firstClearTicketRewards,
        )
        assertEquals(
            emptyMap<String, Int>(),
            regionalLiabilityQuests.getValue("the_sunken_toll_booth").firstClearTicketRewards,
        )
        assertEquals(
            mapOf(RecruitmentTicketCatalog.VETERAN_TICKET_ID to 1),
            regionalLiabilityQuests.getValue("the_crowns_missing_receipt").firstClearTicketRewards,
        )
        assertEquals(
            mapOf(RecruitmentTicketCatalog.EPIC_LIABILITY_WRIT_ID to 1),
            regionalLiabilityQuests.getValue("the_tower_built_sideways").firstClearTicketRewards,
        )

        regionalLiabilityQuestIds.forEach { questId ->
            val quest = regionalLiabilityQuests.getValue(questId)

            assertEquals(QuestRisk.High, quest.risk)
            assertTrue(quest.recommendedHeroIds.isNotEmpty())
            quest.recommendedHeroIds.forEach { heroId ->
                assertTrue("${quest.id} recommends unknown hero $heroId", heroId in HeroCatalog.byId)
            }
            assertTrue(
                "${quest.id} should have at least one quest-specific plan",
                ExpeditionPlanCatalog.availableFor(quest).any { plan -> quest.id in plan.questIds },
            )
        }
    }

    @Test
    fun everyCurrentQuestOffersAQuestSpecificContractClause() {
        SeedGame.quests.forEach { quest ->
            val available = ExpeditionPlanCatalog.availableFor(quest)
            val questSpecificPlans = available.filter { quest.id in it.questIds }

            assertTrue("${quest.id} should keep generic plans", available.any { it.id == ExpeditionPlanCatalog.rushTheJobId })
            assertTrue("${quest.id} should expose one or two quest clauses", questSpecificPlans.size in 1..2)
        }
    }

    @Test
    fun questSpecificPlansRequireSpecialContractsButGenericPlansStayFree() {
        SeedGame.quests.forEach { quest ->
            val available = ExpeditionPlanCatalog.availableFor(quest)
            val genericPlans = available.filter { it.questIds.isEmpty() }
            val questSpecificPlans = available.filter { quest.id in it.questIds }

            assertTrue("${quest.id} should keep free generic plans", genericPlans.isNotEmpty())
            assertTrue("${quest.id} generic plans should be free", genericPlans.all { !it.requiresSpecialContract })
            assertTrue("${quest.id} clauses should cost a Special Contract", questSpecificPlans.all { it.specialContractCost == 1 })
        }
    }
    @Test
    fun questSpecificPlansDoNotApplyToOtherQuests() {
        val cave = SeedGame.firstQuest
        val lockedDoor = SeedGame.questById.getValue("the_last_locked_door")

        assertEquals(
            ExpeditionPlanModifiers(),
            ExpeditionPlanCatalog.modifiersFor(ExpeditionPlanCatalog.bringDoorFormsId, cave),
        )
        assertTrue(
            ExpeditionPlanCatalog.modifiersFor(ExpeditionPlanCatalog.bringDoorFormsId, lockedDoor).scoreBonus > 0,
        )
        assertEquals(
            ExpeditionPlanCatalog.rushTheJobId,
            ExpeditionPlanCatalog.selectedPlanForUi(ExpeditionPlanCatalog.bringDoorFormsId, cave).id,
        )
    }

    @Test
    fun questSpecificPlansChangeRealEstimateLevers() {
        SeedGame.quests.forEach { quest ->
            val plan = ExpeditionPlanCatalog.availableFor(quest).first { quest.id in it.questIds }
            val standard = ExpeditionEstimator.estimate(party = SeedGame.heroes, quest = quest)
            val planned = ExpeditionEstimator.estimate(
                party = SeedGame.heroes,
                quest = quest,
                planId = plan.id,
            )

            val changed = planned.durationSeconds != standard.durationSeconds ||
                planned.partyPower != standard.partyPower ||
                planned.riskPenalty != standard.riskPenalty ||
                planned.planGoldBonusPercent != standard.planGoldBonusPercent ||
                planned.planBonusLootRolls != standard.planBonusLootRolls ||
                planned.greatSuccessTargetMargin != standard.greatSuccessTargetMargin

            assertTrue("${quest.id} clause ${plan.id} should change estimate levers", changed)
        }
    }
    private fun bestEstimate(
        roster: List<Hero>,
        quest: Quest,
        equipment: List<EquippedLoot> = emptyList(),
        facilityPowerBonus: Int = 0,
    ): ExpeditionEstimate =
        combinations(roster, quest.partySlots.coerceAtMost(roster.size))
            .map { party ->
                ExpeditionEstimator.estimate(
                    party = party,
                    quest = quest,
                    equipment = equipment,
                    facilityPowerBonus = facilityPowerBonus,
                )
            }
            .maxBy { it.successChancePercent }

    private fun combinations(roster: List<Hero>, size: Int): List<List<Hero>> {
        if (size <= 0) return listOf(emptyList())
        if (size >= roster.size) return listOf(roster)

        val result = mutableListOf<List<Hero>>()

        fun collect(startIndex: Int, party: List<Hero>) {
            if (party.size == size) {
                result += party
                return
            }

            for (index in startIndex..roster.lastIndex) {
                collect(index + 1, party + roster[index])
            }
        }

        collect(0, emptyList())
        return result
    }

    private fun heroesById(vararg ids: String): List<Hero> =
        ids.map { HeroCatalog.byId.getValue(it).toHero() }

    private fun testGear(heroId: String, bonus: Int): EquippedLoot =
        EquippedLoot(
            heroId = heroId,
            item = LootItem(
                id = "balance_test_gear_$heroId",
                name = "Balance Test Gear",
                rarity = LootRarity.Rare,
                slot = LootSlot.Weapon,
                bonus = bonus,
                icon = LootIcon.Weapon,
            ),
        )

    private fun assertInRange(actual: Int, min: Int, max: Int) {
        assertTrue("$actual was below $min", actual >= min)
        assertTrue("$actual was above $max", actual <= max)
    }
}
